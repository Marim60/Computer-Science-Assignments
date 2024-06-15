6#include<bits/stdc++.h>
#include <cstdio>
#include <cstdlib>
#include <cstring>
using namespace std;

/*
{ Sample program
  in TINY language
  compute factorial
}

read x; {input an integer}
if 0<x then {compute only if x>=1}
  fact:=1;
  repeat
    fact := fact * x;
    x:=x-1
  until x=0;
  write fact {output factorial}
end
*/

// sequence of statements separated by ;
// no procedures - no declarations
// all variables are integers
// variables are declared simply by assigning values to them :=
// if-statement: if (boolean) then [else] end
// repeat-statement: repeat until (boolean)
// boolean only in if and repeat conditions < = and two mathematical expressions
// math expressions integers only, + - * / ^
// I/O read write
// Comments {}

////////////////////////////////////////////////////////////////////////////////////
// Strings /////////////////////////////////////////////////////////////////////////

bool Equals(const char* a, const char* b)
{
    return strcmp(a, b)==0;
}

bool StartsWith(const char* a, const char* b)
{
    int nb=strlen(b);
    return strncmp(a, b, nb)==0;
}

void Copy(char* a, const char* b, int n=0)
{
    if(n>0) {strncpy(a, b, n); a[n]=0;}
    else strcpy(a, b);
}

void AllocateAndCopy(char** a, const char* b)
{
    if(b==0) {*a=0; return;}
    int n=strlen(b);
    *a=new char[n+1];
    strcpy(*a, b);
}

////////////////////////////////////////////////////////////////////////////////////
// Input and Output ////////////////////////////////////////////////////////////////

#define MAX_LINE_LENGTH 10000

struct InFile
{
    FILE* file;
    int cur_line_num;

    char line_buf[MAX_LINE_LENGTH];
    int cur_ind, cur_line_size;

    InFile(const char* str) {file=0; if(str) file=fopen(str, "r"); cur_line_size=0; cur_ind=0; cur_line_num=0;}
    ~InFile(){if(file) fclose(file);}

    void SkipSpaces()
    {
        while(cur_ind<cur_line_size)
        {
            char ch=line_buf[cur_ind];
            if(ch!=' ' && ch!='\t' && ch!='\r' && ch!='\n') break;
            cur_ind++;
        }
    }

    bool SkipUpto(const char* str)
    {
        while(true)
        {
            SkipSpaces();
            while(cur_ind>=cur_line_size) {if(!GetNewLine()) return false; SkipSpaces();}

            if(StartsWith(&line_buf[cur_ind], str))
            {
                cur_ind+=strlen(str);
                return true;
            }
            cur_ind++;
        }
        return false;
    }

    bool GetNewLine()
    {
        cur_ind=0; line_buf[0]=0;
        if(!fgets(line_buf, MAX_LINE_LENGTH, file)) return false;
        cur_line_size=strlen(line_buf);
        if(cur_line_size==0) return false; // End of file
        cur_line_num++;
        return true;
    }

    char* GetNextTokenStr()
    {
        SkipSpaces();
        while(cur_ind>=cur_line_size) {if(!GetNewLine()) return 0; SkipSpaces();}
        return &line_buf[cur_ind];
    }

    void Advance(int num)
    {
        cur_ind+=num;
    }
};

struct OutFile
{
    FILE* file;
    OutFile(const char* str) {file=0; if(str) file=fopen(str, "w");}
    ~OutFile(){if(file) fclose(file);}

    void Out(const char* s)
    {
        fprintf(file, "%s\n", s); fflush(file);
    }
};

////////////////////////////////////////////////////////////////////////////////////
// Compiler Parameters /////////////////////////////////////////////////////////////

struct CompilerInfo
{
    InFile in_file;
    OutFile out_file;
    OutFile debug_file;

    CompilerInfo(const char* in_str, const char* out_str, const char* debug_str)
            : in_file(in_str), out_file(out_str), debug_file(debug_str)
    {
    }
};

////////////////////////////////////////////////////////////////////////////////////
// Scanner /////////////////////////////////////////////////////////////////////////

#define MAX_TOKEN_LEN 40

enum TokenType{
    IF, THEN, ELSE, END, REPEAT, UNTIL, READ, WRITE,
    ASSIGN, EQUAL, LESS_THAN,
    PLUS, MINUS, TIMES, DIVIDE, POWER,
    SEMI_COLON,
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE,
    ID, NUM,
    ENDFILE, ERROR
};

// Used for debugging only /////////////////////////////////////////////////////////
const char* TokenTypeStr[]=
        {
                "If", "Then", "Else", "End", "Repeat", "Until", "Read", "Write",
                "Assign", "Equal", "LessThan",
                "Plus", "Minus", "Times", "Divide", "Power",
                "SemiColon",
                "LeftParen", "RightParen",
                "LeftBrace", "RightBrace",
                "ID", "Num",
                "EndFile", "Error"
        };

struct Token
{
    TokenType type;
    char str[MAX_TOKEN_LEN+1];

    Token(){str[0]=0; type=ERROR;}
    Token(TokenType _type, const char* _str) {type=_type; Copy(str, _str);}
};

const Token reserved_words[]=
        {
                Token(IF, "if"),
                Token(THEN, "then"),
                Token(ELSE, "else"),
                Token(END, "end"),
                Token(REPEAT, "repeat"),
                Token(UNTIL, "until"),
                Token(READ, "read"),
                Token(WRITE, "write")
        };
const int num_reserved_words=sizeof(reserved_words)/sizeof(reserved_words[0]);

// if there is tokens like < <=, sort them such that sub-tokens come last: <= <
// the closing comment should come immediately after opening comment
const Token symbolic_tokens[]=
        {
                Token(ASSIGN, ":="),
                Token(EQUAL, "="),
                Token(LESS_THAN, "<"),
                Token(PLUS, "+"),
                Token(MINUS, "-"),
                Token(TIMES, "*"),
                Token(DIVIDE, "/"),
                Token(POWER, "^"),
                Token(SEMI_COLON, ";"),
                Token(LEFT_PAREN, "("),
                Token(RIGHT_PAREN, ")"),
                Token(LEFT_BRACE, "{"),
                Token(RIGHT_BRACE, "}")
        };
const int num_symbolic_tokens=sizeof(symbolic_tokens)/sizeof(symbolic_tokens[0]);

inline bool IsDigit(char ch){return (ch>='0' && ch<='9');}
inline bool IsLetter(char ch){return ((ch>='a' && ch<='z') || (ch>='A' && ch<='Z'));}
inline bool IsLetterOrUnderscore(char ch){return (IsLetter(ch) || ch=='_');}

void GetNextToken(CompilerInfo* pci, Token* ptoken)
{
    ptoken->type=ERROR;
    ptoken->str[0]=0;

    int i;
    char* s=pci->in_file.GetNextTokenStr();
    if(!s)
    {
        ptoken->type=ENDFILE;
        ptoken->str[0]=0;
        return;
    }

    for(i=0;i<num_symbolic_tokens;i++)
    {
        if(StartsWith(s, symbolic_tokens[i].str))
            break;
    }

    if(i<num_symbolic_tokens)
    {
        if(symbolic_tokens[i].type==LEFT_BRACE)
        {
            pci->in_file.Advance(strlen(symbolic_tokens[i].str));
            if(!pci->in_file.SkipUpto(symbolic_tokens[i+1].str)) return;
            return GetNextToken(pci, ptoken);
        }
        ptoken->type=symbolic_tokens[i].type;
        Copy(ptoken->str, symbolic_tokens[i].str);
    }
    else if(IsDigit(s[0]))
    {
        int j=1;
        while(IsDigit(s[j])) j++;

        ptoken->type=NUM;
        Copy(ptoken->str, s, j);
    }
    else if(IsLetterOrUnderscore(s[0]))
    {
        int j=1;
        while(IsLetterOrUnderscore(s[j])) j++;

        ptoken->type=ID;
        Copy(ptoken->str, s, j);

        for(i=0;i<num_reserved_words;i++)
        {
            if(Equals(ptoken->str, reserved_words[i].str))
            {
                ptoken->type=reserved_words[i].type;
                break;
            }
        }
    }

    int len=strlen(ptoken->str);
    if(len>0) pci->in_file.Advance(len);
}

////////////////////////////////////////////////////////////////////////////////////
// Parser //////////////////////////////////////////////////////////////////////////

// program -> stmtseq
// stmtseq -> stmt { ; stmt }
// stmt -> ifstmt | repeatstmt | assignstmt | readstmt | writestmt
// ifstmt -> if exp then stmtseq [ else stmtseq ] end
// repeatstmt -> repeat stmtseq until expr
// assignstmt -> identifier := expr
// readstmt -> read identifier
// writestmt -> write expr
// expr -> mathexpr [ (<|=) mathexpr ]
// mathexpr -> term { (+|-) term }    left associative
// term -> factor { (*|/) factor }    left associative
// factor -> newexpr { ^ newexpr }    right associative
// newexpr -> ( mathexpr ) | number | identifier

enum NodeKind{
    IF_NODE, REPEAT_NODE, ASSIGN_NODE, READ_NODE, WRITE_NODE,
    OPER_NODE, NUM_NODE, ID_NODE
};

// Used for debugging only /////////////////////////////////////////////////////////
const char* NodeKindStr[]=
        {
                "If", "Repeat", "Assign", "Read", "Write",
                "Oper", "Num", "ID"
        };

enum ExprDataType {VOID, INTEGER, BOOLEAN};

// Used for debugging only /////////////////////////////////////////////////////////
const char* ExprDataTypeStr[]=
        {
                "Void", "Integer", "Boolean"
        };

#define MAX_CHILDREN 3

struct TreeNode
{
    TreeNode* child[MAX_CHILDREN];
    TreeNode* sibling; // used for sibling statements only

    NodeKind node_kind;

    union{TokenType oper; int num; char* id;}; // defined for expression/int/identifier only
    ExprDataType expr_data_type; // defined for expression/int/identifier only

    int line_num;

    TreeNode() {int i; for(i=0;i<MAX_CHILDREN;i++) child[i]=0; sibling=0; expr_data_type=VOID;}
};
void DestroyTree(TreeNode* node)
{
    int i;

    if(node->node_kind==ID_NODE || node->node_kind==READ_NODE || node->node_kind==ASSIGN_NODE)
        if(node->id) delete[] node->id;

    for(i=0;i<MAX_CHILDREN;i++) if(node->child[i]) DestroyTree(node->child[i]);
    if(node->sibling) DestroyTree(node->sibling);

    delete node;
}
struct ParseInfo
{
    Token next_token;
};
void match(CompilerInfo* , ParseInfo* ,TokenType);
TreeNode* assignStmt(CompilerInfo* ,ParseInfo* );
TreeNode* readStmt(CompilerInfo* ,ParseInfo* );
TreeNode* writeStmt(CompilerInfo* ,ParseInfo* );
TreeNode* mathExpr(CompilerInfo* ,ParseInfo* );
TreeNode* term(CompilerInfo* ,ParseInfo* );
TreeNode* factor(CompilerInfo* ,ParseInfo* );
TreeNode* newExpr(CompilerInfo* ,ParseInfo* );
TreeNode* expr(CompilerInfo*, ParseInfo*);
TreeNode* Parsing(CompilerInfo* );
TreeNode* stmtSeq(CompilerInfo* ,ParseInfo* );
TreeNode* stmt(CompilerInfo* ,ParseInfo* );
TreeNode* ifStmt(CompilerInfo* ,ParseInfo* );
TreeNode* repeatStmt(CompilerInfo* ,ParseInfo* );
void PrintTree(TreeNode*, int );
//match the current token in parseInfo with the expected_tokenType
// If the match is successful, the next token is retrieved; otherwise, an error is displayed.
void match(CompilerInfo* compilerInfo, ParseInfo* parseInfo, TokenType expected_tokenType)
{
    if(parseInfo->next_token.type == expected_tokenType)
    {
        GetNextToken(compilerInfo, &parseInfo->next_token);
    }
    else
    {
        printf("Match Error: Expected %s, found %s\n", TokenTypeStr[expected_tokenType], TokenTypeStr[parseInfo->next_token.type]);
        throw 0;
    }
}
///ifstmt -> if expr then stmtseq [ else stmtseq ] end
// Generates a part of the parse tree representing the IF statement
TreeNode* ifStmt(CompilerInfo* compilerInfo, ParseInfo* parseInfo)
{
    TreeNode* node = new TreeNode();
    node->node_kind = IF_NODE;
    node->line_num = compilerInfo->in_file.cur_line_num;
    // Match the "IF" keyword
    match(compilerInfo, parseInfo, IF);
    // Parse the expression inside the IF statement and assign it as the first child of the IF node
    node->child[0] = expr(compilerInfo, parseInfo);
    // Match the "THEN" keyword
    match(compilerInfo, parseInfo, THEN);
    // Parse the statement sequence inside the IF statement and assign it as the second child of the IF node
    node->child[1] = stmtSeq(compilerInfo, parseInfo);
    // If the next token is "ELSE"
    if(parseInfo->next_token.type == ELSE)
    {
        // Match the "ELSE" keyword
        match(compilerInfo, parseInfo, ELSE);
        //parse the statement sequence inside the ELSE statement and assign it as the third child of the IF node
        node->child[2] = stmtSeq(compilerInfo, parseInfo);
    }
    // Match the "END" keyword
    match(compilerInfo, parseInfo, END);
    return node;
}
///repeatstmt -> repeat stmtseq until expr
// Generates a part of the parse tree representing the REPEAT statement
TreeNode* repeatStmt(CompilerInfo* compilerInfo, ParseInfo* parseInfo)
{
    TreeNode* node = new TreeNode;
    node->node_kind = REPEAT_NODE;
    node->line_num = compilerInfo->in_file.cur_line_num;
    // Match the "REPEAT" keyword
    match(compilerInfo, parseInfo, REPEAT);
    // Parse the statement sequence inside the REPEAT statement and assign it as the first child of the REPEAT node
    node->child[0] = stmtSeq(compilerInfo, parseInfo);
    // Match the "UNTIL" keyword
    match(compilerInfo, parseInfo, UNTIL);
    // Parse the expression inside the UNTIL statement and assign it as the second child of the REPEAT node
    node->child[1] = expr(compilerInfo, parseInfo);
    return node;
}
///assignstmt -> identifier := expr
// Generates a part of the parse tree representing the ASSIGN statement
TreeNode* assignStmt(CompilerInfo* compilerInfo, ParseInfo* parseInfo)
{
    TreeNode* node = new TreeNode;
    node->node_kind = ASSIGN_NODE;
    if(parseInfo->next_token.type == ID)
    {
        // Copy the identifier name to the node
        AllocateAndCopy(&node->id, parseInfo->next_token.str);
        node->expr_data_type = INTEGER;
        node->line_num = compilerInfo->in_file.cur_line_num;
    }
    // Match the identifier
    match(compilerInfo, parseInfo, parseInfo->next_token.type);
    match(compilerInfo, parseInfo, ASSIGN);
    node->child[0] = expr(compilerInfo, parseInfo);
    return node;
}
///readstmt -> read identifier
// Generates a part of the parse tree representing the READ statement
TreeNode* readStmt(CompilerInfo* compilerInfo, ParseInfo* parseInfo)
{
    TreeNode* node = new TreeNode;
    node->node_kind = READ_NODE;
    match(compilerInfo, parseInfo, READ);
    if(parseInfo->next_token.type == ID)
    {
        // Copy the identifier name to the node
        AllocateAndCopy(&node->id, parseInfo->next_token.str);
        node->expr_data_type = INTEGER;
        node->line_num = compilerInfo->in_file.cur_line_num;
    }
    // Match the identifier
    match(compilerInfo, parseInfo, ID);
    return node;
}
///writestmt -> write expr
// Generates a part of the parse tree representing the WRITE statement
TreeNode* writeStmt(CompilerInfo* compilerInfo, ParseInfo* parseInfo)
{
    TreeNode* node = new TreeNode;
    node->node_kind = WRITE_NODE;
    node->line_num = compilerInfo->in_file.cur_line_num;
    match(compilerInfo, parseInfo, WRITE);
    // Parse the expression inside the WRITE statement and assign it as the first child of the WRITE node
    node->child[0] = expr(compilerInfo, parseInfo);
    return node;
}
///expr -> mathexpr [ (<|=) mathexpr ]
// Generates a part of the parse tree representing the expression
TreeNode* expr(CompilerInfo* compilerInfo , ParseInfo* parserInfo)
{
    TreeNode* node;
    node= mathExpr(compilerInfo, parserInfo);
    if(parserInfo->next_token.type == LESS_THAN || parserInfo->next_token.type == EQUAL)
    {
        TreeNode* temp = new TreeNode;
        temp->node_kind = OPER_NODE;
        temp->expr_data_type = BOOLEAN;
        temp->line_num = compilerInfo->in_file.cur_line_num;
        temp->oper = parserInfo->next_token.type;
        match(compilerInfo, parserInfo, parserInfo->next_token.type);
        temp->child[0] = node;
        temp->child[1] = mathExpr(compilerInfo, parserInfo);
        node = temp;

    }
    return node;
}
///factor -> newexpr { ^ newexpr }    right associative
// Generates a part of the parse tree representing the factor
TreeNode* factor(CompilerInfo* compilerInfo , ParseInfo* parserInfo)
{
    TreeNode* node;
    node = newExpr( compilerInfo, parserInfo);
    if(parserInfo->next_token.type == POWER)
    {
        // Create a temp TreeNode to represent the power operation
        TreeNode* temp = new TreeNode;
        temp->node_kind = OPER_NODE;
        temp->line_num = compilerInfo->in_file.cur_line_num;
        temp->oper = parserInfo->next_token.type;
        match(compilerInfo, parserInfo, parserInfo->next_token.type);
        // Set the child of the temp node to the left operand (node) and the right operand (factor)
        temp->child[0] = node;
        // recursively call factor to parse the right operand
        temp->child[1] = factor(compilerInfo, parserInfo);
        // Return the temp node representing the power operation
        return temp;
    }
    // If there is no power operator return the parsed node
    return node;
}
///term -> factor { (*|/) factor }    left associative
// Generates a part of the parse tree representing the term
TreeNode* term(CompilerInfo* compilerInfo , ParseInfo* parserInfo)
{
    TreeNode* node;
    node = factor(compilerInfo, parserInfo);
    while (parserInfo->next_token.type == TIMES || parserInfo->next_token.type == DIVIDE)
    {
        TreeNode* temp = new TreeNode;
        temp->node_kind = OPER_NODE;
        temp->oper = parserInfo->next_token.type;
        temp->line_num = compilerInfo->in_file.cur_line_num;
        match(compilerInfo, parserInfo, parserInfo->next_token.type);
        // Set the child of the temp node to the left operand (node) and the right operand (factor)
        temp->child[0] = node;
        temp->child[1] = factor(compilerInfo, parserInfo);
        // Update the current node to the temp node, representing the result of the operation
        node = temp;
    }
    return node;
}

///mathexpr -> term { (+|-) term }    left associative
// Generates a part of the parse tree representing the math expression
TreeNode* mathExpr(CompilerInfo* compilerInfo , ParseInfo* parseInfo)
{
    TreeNode* node = new TreeNode;
    node = term(compilerInfo, parseInfo);
    while (parseInfo->next_token.type == PLUS || parseInfo->next_token.type == MINUS)
    {
        TreeNode* temp = new TreeNode;
        temp->node_kind = OPER_NODE;
        temp->oper = parseInfo->next_token.type;
        temp->line_num = compilerInfo->in_file.cur_line_num;
        match(compilerInfo, parseInfo, parseInfo->next_token.type);
        temp->child[0] = node;
        temp->child[1] = term(compilerInfo, parseInfo);
        node = temp;
    }
    return node;
}
/// newexpr -> ( mathexpr ) | number | identifier
// Generates a part of the parse tree representing the new expression
TreeNode* newExpr(CompilerInfo* compilerInfo, ParseInfo* parseInfo)
{
    TreeNode* node = new TreeNode();
    if(parseInfo->next_token.type == NUM)
    {
        node->node_kind = NUM_NODE;
        node->num = atoi(parseInfo->next_token.str);
        node->expr_data_type = INTEGER;
        node->line_num = compilerInfo->in_file.cur_line_num;

        match(compilerInfo, parseInfo, parseInfo->next_token.type);
        return node;
    }
    if(parseInfo->next_token.type == ID)
    {
        node->node_kind = ID_NODE;
        node->expr_data_type = INTEGER;
        node->line_num = compilerInfo->in_file.cur_line_num;
        AllocateAndCopy(&node->id, parseInfo->next_token.str);
        match(compilerInfo, parseInfo, parseInfo->next_token.type);
        return node;
    }
    if(parseInfo->next_token.type == LEFT_PAREN)
    {
        match(compilerInfo, parseInfo, parseInfo->next_token.type);
        node = mathExpr(compilerInfo, parseInfo);
        match(compilerInfo, parseInfo, parseInfo->next_token.type);
        node->line_num = compilerInfo->in_file.cur_line_num;
        return node;
    }
    throw 0;
}
///stmt -> ifstmt | repeatstmt | assignstmt | readstmt | writestmt
// Generates a part of the parse tree representing the statement
TreeNode* stmt(CompilerInfo* compilerInfo, ParseInfo* parseInfo)
{
    TreeNode* node = new TreeNode;
    if(parseInfo->next_token.type == IF)
    {
        node = ifStmt(compilerInfo, parseInfo);
    }
    else if(parseInfo->next_token.type == REPEAT)
    {
        node = repeatStmt(compilerInfo, parseInfo);
    }
    else if(parseInfo->next_token.type == ID)
    {
        node = assignStmt(compilerInfo, parseInfo);
    }
    else if(parseInfo->next_token.type == READ)
    {
        node = readStmt(compilerInfo, parseInfo);
    }
    else if(parseInfo->next_token.type == WRITE)
    {
        node = writeStmt(compilerInfo, parseInfo);
    }
    return node;
}

///stmtseq -> stmt { ; stmt }
// left most child right sibling
TreeNode* stmtSeq(CompilerInfo* compilerInfo, ParseInfo* parseInfo)
{
    TreeNode* node;
    // Parse the first statement
    node = stmt(compilerInfo,parseInfo);
    TreeNode* nodeCopy;
    // Set the copy of the node to the root of the statement sequence like a linked list
    nodeCopy=node;
    // follow set of stmtseq is first set of stmt
    while (parseInfo->next_token.type != ENDFILE&&parseInfo->next_token.type != END &&parseInfo->next_token.type != ELSE&&parseInfo->next_token.type != UNTIL)
    {
        // Match a semicolon, ensuring it is consumed
        match(compilerInfo, parseInfo, SEMI_COLON);
        // Parse the next statement and link it as a sibling to the previous one as a linked list
        nodeCopy->sibling = stmt(compilerInfo, parseInfo);
        nodeCopy = nodeCopy->sibling;
    }
    // Return the head of the statement sequence
    return node;
}
void PrintTree(TreeNode* node, int sh=0)
{
    int i, NSH=3;
    for(i=0;i<sh;i++) printf(" ");

    printf("[%s]", NodeKindStr[node->node_kind]);

    if(node->node_kind==OPER_NODE) printf("[%s]", TokenTypeStr[node->oper]);
    else if(node->node_kind==NUM_NODE) printf("[%d]", node->num);
    else if(node->node_kind==ID_NODE || node->node_kind==READ_NODE || node->node_kind==ASSIGN_NODE) printf("[%s]", node->id);

    if(node->expr_data_type!=VOID) printf("[%s]", ExprDataTypeStr[node->expr_data_type]);

    printf("\n");

    for(i=0;i<MAX_CHILDREN;i++) if(node->child[i]) PrintTree(node->child[i], sh+NSH);
    if(node->sibling) PrintTree(node->sibling, sh);
}
TreeNode* Parsing(CompilerInfo* compilerInfo){
    ParseInfo parserToken;
    GetNextToken(compilerInfo, &parserToken.next_token);
    TreeNode* parse_tree ;
    parse_tree = stmtSeq(compilerInfo,&parserToken);
    return parse_tree;
}
TreeNode* startParsing(CompilerInfo* compilerInfo){
    TreeNode* parse_tree;
    parse_tree = Parsing(compilerInfo);
    cout<<("------>      Parsing Tree     <-----")<<endl;
    PrintTree(parse_tree, 0);
    //DestroyTree(parse_tree);
    return parse_tree;
}


////////////////////////////////////////////////////////////////////////////////////
// Analyzer ////////////////////////////////////////////////////////////////////////

const int SYMBOL_HASH_SIZE=10007;

struct LineLocation
{
    int line_num;
    LineLocation* next;
};

struct VariableInfo
{
    char* name;
    int memloc;
    LineLocation* head_line; // the head of linked list of source line locations
    LineLocation* tail_line; // the tail of linked list of source line locations
    VariableInfo* next_var; // the next variable in the linked list in the same hash bucket of the symbol table
};

struct SymbolTable
{
    int num_vars;
    VariableInfo* var_info[SYMBOL_HASH_SIZE];

    SymbolTable() {num_vars=0; int i; for(i=0;i<SYMBOL_HASH_SIZE;i++) var_info[i]=0;}

    int Hash(const char* name)
    {
        int i, len=strlen(name);
        int hash_val=11;
        for(i=0;i<len;i++) hash_val=(hash_val*17+(int)name[i])%SYMBOL_HASH_SIZE;
        return hash_val;
    }

    VariableInfo* Find(const char* name)
    {
        int h=Hash(name);
        VariableInfo* cur=var_info[h];
        while(cur)
        {
            if(Equals(name, cur->name)) return cur;
            cur=cur->next_var;
        }
        return 0;
    }

    void Insert(const char* name, int line_num)
    {
        LineLocation* lineloc=new LineLocation;
        lineloc->line_num=line_num;
        lineloc->next=0;

        int h=Hash(name);
        VariableInfo* prev=0;
        VariableInfo* cur=var_info[h];

        while(cur)
        {
            if(Equals(name, cur->name))
            {
                // just add this line location to the list of line locations of the existing var
                cur->tail_line->next=lineloc;
                cur->tail_line=lineloc;
                return;
            }
            prev=cur;
            cur=cur->next_var;
        }

        VariableInfo* vi=new VariableInfo;
        vi->head_line=vi->tail_line=lineloc;
        vi->next_var=0;
        vi->memloc=num_vars++;
        AllocateAndCopy(&vi->name, name);

        if(!prev) var_info[h]=vi;
        else prev->next_var=vi;
    }

    void Print()
    {
        int i;
        for(i=0;i<SYMBOL_HASH_SIZE;i++)
        {
            VariableInfo* curv=var_info[i];
            while(curv)
            {
                printf("[Var=%s][Mem=%d]", curv->name, curv->memloc);
                LineLocation* curl=curv->head_line;
                while(curl)
                {
                    printf("[Line=%d]", curl->line_num);
                    curl=curl->next;
                }
                printf("\n");
                curv=curv->next_var;
            }
        }
    }

    void Destroy()
    {
        int i;
        for(i=0;i<SYMBOL_HASH_SIZE;i++)
        {
            VariableInfo* curv=var_info[i];
            while(curv)
            {
                LineLocation* curl=curv->head_line;
                while(curl)
                {
                    LineLocation* pl=curl;
                    curl=curl->next;
                    delete pl;
                }
                VariableInfo* p=curv;
                curv=curv->next_var;
                delete p;
            }
            var_info[i]=0;
        }
    }
};
////// Simulating
void generateStatement(TreeNode*, SymbolTable*);
int fpow(int b, int p) { if (!p) return 1; int ret = fpow(b, p/ 2); ret *= ret; if (p & 1) ret *= b; return ret; }
// evalutes the value of the two id in if condition like 0 < x , num = 0, and ge the value of x from symbole table
vector<int> evaluateCondition(TreeNode* node, SymbolTable* symbolTable)
{
    VariableInfo* variableInfo;
    int x, y;
    vector<int> num;
    if(node->child[0]->node_kind == ID_NODE)
    {
        variableInfo = symbolTable->Find(node->child[0]->id);
        x = variableInfo->memloc;
    }
    else if(node->child[0]->node_kind == NUM_NODE)
        x = node->child[0]->num;

    if(node->child[1]->node_kind == ID_NODE)
    {
        variableInfo = symbolTable->Find(node->child[1]->id);
        y = variableInfo->memloc;
    }
    else if(node->child[1]->node_kind == NUM_NODE)
        y = node->child[1]->num;
    num.push_back(x);
    num.push_back(y);
    return num;
}
// It checks that the condition is true or false for if and until
bool oper(TreeNode* node, SymbolTable* symbolTable){
    vector<int>num = evaluateCondition(node,symbolTable);
    bool isTrue = false;
    if(node->oper == LESS_THAN)
    {
        if(num[0] < num[1])
            isTrue = true;
    }
    else if(node->oper == EQUAL)
    {
        if(num[0] == num[1])
            isTrue = true;
    }
    return isTrue;
}
// this function evalute the real value of expression afer making operation like / + ..
int evaluateExp(TreeNode* node, SymbolTable* symbolTable)
{
    VariableInfo* variableInfo;
    int x, y;
    // base case
    if(node->node_kind == ID_NODE)
    {
        variableInfo = symbolTable->Find(node->id);
        return variableInfo->memloc;
    }
    else if(node->node_kind == NUM_NODE)
        return node->num;

    // Check Childs
    x = evaluateExp(node->child[0], symbolTable);
    y = evaluateExp(node->child[1], symbolTable );

    int result = 0;
    if(node->oper == TIMES)
        result = x * y;
    if(node->oper == MINUS)
        result = x - y;
    if(node->oper == PLUS)
        result = x + y;
    if(node->oper == DIVIDE)
        result = x / y;
    if(node->oper == POWER)
        result = fpow(x, y);
    return result;

}
// simulate reading in c++ 
void read(TreeNode* node, SymbolTable* symbolTable)
{
    VariableInfo* variableInfo;
    // print variableInfo
    int x;
    cout<<"Enter the number: "<<'\n';
    cin>>x;
    variableInfo = symbolTable->Find(node->id);
    variableInfo->memloc = x;
}
// simulate write in c++
void write(TreeNode* node, SymbolTable* symbolTable)
{
    VariableInfo* variableInfo;
    variableInfo = symbolTable->Find(node->child[0]->id);
    cout<<node->child[0]->id<<" "<<variableInfo->memloc<<'\n';
}
// simulate if else in c++
void ifElse(TreeNode* node, SymbolTable* symbolTable)
{
    bool condition = oper(node->child[0],symbolTable);
    if(condition)
        generateStatement(node->child[1],symbolTable); // dfs of second child
    else if(node->child[2] != nullptr){
        generateStatement(node->child[2],symbolTable); // if condition is false and there is else part 
    }

}
// simulate assign in c++
void assign(TreeNode* node, SymbolTable* symbolTable)
{
    int result;
    VariableInfo* variableInfo;
    if(node->child[0]->node_kind == NUM_NODE)
        result = node->child[0]->num;
    else if(node->child[0]->node_kind == ID_NODE)
    {
        variableInfo = symbolTable->Find(node->child[0]->id);
        result = variableInfo->memloc;
    }
    else {
        result = evaluateExp(node->child[0], symbolTable);
    }
    variableInfo = symbolTable->Find(node->id);
    variableInfo->memloc = result;
}

void generateStatement(TreeNode* node, SymbolTable* symbolTable)
{
    if(node == nullptr)
        return;
    if(node->node_kind == READ_NODE) {
        if(node->id != nullptr)
            read(node, symbolTable);
        else
        {
            printf("read should be followed by a id.");
            throw 0;
        }
    }
    if(node->node_kind == WRITE_NODE) {
        if(node->child[0]->id != nullptr)
            write(node, symbolTable);
        else
        {
            printf("write should be followed by a id.");
            throw 0;
        }
    }
    if(node->node_kind == ASSIGN_NODE)
        assign(node, symbolTable);
    if(node->node_kind == IF_NODE) {
        if(node->child[0]->expr_data_type == BOOLEAN)
            ifElse(node, symbolTable);
        else
        {
            printf("if should be followed by a Boolean.");
            throw 0;
        }

    }    if(node->node_kind == REPEAT_NODE)
    {
        if(node->child[1]->expr_data_type == BOOLEAN) {
            do {
                generateStatement(node->child[0], symbolTable);
            } while (!oper(node->child[1], symbolTable));
        }
        else
        {
            printf("Repeat should be followed by a Boolean.");
            throw 0;
        }

    }
    generateStatement(node->sibling, symbolTable);
}
void generateSympolTable(TreeNode* node, SymbolTable* symbolTable)
{
    if(node == nullptr)
        return;
    if(node->node_kind == ID_NODE || node->node_kind == READ_NODE || node->node_kind == ASSIGN_NODE)
        symbolTable->Insert(node->id, node->line_num);
    // dfs to fill the table with childern of the node
    for (int i = 0; i < MAX_CHILDREN; ++i) {
        generateSympolTable(node->child[i], symbolTable);
    }
        generateSympolTable(node->sibling, symbolTable);
}

void SympolTablePrint(TreeNode* node, SymbolTable* symbolTable)
{
    cout<<"----------->  Sympol Table  <-------------"<<endl;
    symbolTable->Print();
}
int main(int argc, char* argv[])
{
    try {
        CompilerInfo compilerInfo("../input.txt", "../output.txt", "../debug.txt");
        TreeNode * tree = startParsing(&compilerInfo);
        SymbolTable symbolTable = SymbolTable();
        generateSympolTable(tree, &symbolTable);
        SympolTablePrint(tree,&symbolTable);
        generateStatement(tree, &symbolTable);
        SympolTablePrint(tree,&symbolTable);
        symbolTable.Destroy();
        DestroyTree(tree);
    }
    catch (const char* e) {
        cout << e << endl;
    }


    return 0;
}






