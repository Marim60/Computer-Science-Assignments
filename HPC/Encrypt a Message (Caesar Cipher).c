#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <ctype.h>
#include <string.h>
char* createfile(char fileName[100])
{
    char    *text;
    long    numbytes;
    FILE *messageFile;
    messageFile = fopen(fileName, "r");
    if(messageFile == NULL)
        return NULL;
    fseek(messageFile, 0L, SEEK_END);
    numbytes = ftell(messageFile);
    fseek(messageFile, 0L, SEEK_SET);  
    text = (char*)calloc(numbytes, sizeof(char));   
    if(text == NULL)
        return NULL;
    fread(text, sizeof(char), numbytes, messageFile);
    fclose(messageFile);
    return text;
}
void writeIntofile(char* reciveText)
{
    FILE *fp = fopen("Ciphertext.txt", "w");
    if (fp == NULL)
    {
        return;
    }
    fprintf(fp, "%s\n", reciveText);
    fclose(fp);
}
char encode(char t, int shiftValue)
{
    if(isupper(t))
    {
        t = (((t-'A') + shiftValue ) % 26) + 'A';
    }
    else if(islower(t))
    {
        t = (((t-'a') + shiftValue ) % 26) + 'a';
    }
    return t;
}
int main(int argc, char* argv[])
{
    int shiftValue;
    char fileName[100];
    char  *text;
    char *reciveText;
    int textLength;
    char *subText;
    MPI_Status status;
    int numOfProcess;
    int numOfCharPerProcess;
    int my_rank;
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    MPI_Comm_size(MPI_COMM_WORLD, &numOfProcess);
    if(my_rank == 0)
    {
        printf("Enter the key (shift) value\n");
        scanf("%d", &shiftValue);
        printf("Enter the file name\n");
        scanf("%s", fileName);
        text = createfile(fileName);
        textLength = strlen(text);
        numOfCharPerProcess = textLength / numOfProcess;
        reciveText = (char*)calloc(textLength, sizeof(char));
        //printf("%s\n",text);
    }
    MPI_Bcast(&shiftValue, 1, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Bcast(&numOfCharPerProcess, 1, MPI_INT, 0, MPI_COMM_WORLD);
    //printf("shift num %d, rank %d\n", shiftValue, my_rank);
    subText= (char*)calloc(numOfCharPerProcess, sizeof(char));
    MPI_Scatter(text, numOfCharPerProcess, MPI_CHAR, subText, numOfCharPerProcess, MPI_CHAR, 0, MPI_COMM_WORLD);
    for (int i = 0; i < numOfCharPerProcess; i++)
    {
        subText[i] = encode(subText[i], shiftValue);
    }
    MPI_Gather(subText,  numOfCharPerProcess,MPI_CHAR,  reciveText, numOfCharPerProcess,MPI_CHAR, 0, MPI_COMM_WORLD);
    if(my_rank == 0)
    {
        int partialLength = numOfCharPerProcess * numOfProcess;
        for (int i = partialLength; i < textLength; i++)
        {
            reciveText[i] = encode(text[i], shiftValue);
        }
        
        printf("%s\n", reciveText);
        writeIntofile(reciveText);
    }
    MPI_Finalize();
    free(text);
    free(subText);
    free(reciveText);
    
}