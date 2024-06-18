package Mariam.com;

public class Tag {
    public int position;
    public int length;
    public char nextSymbol;
    public Tag(int position, int length, char nextSymbol)
    {
        this.position = position;
        this.length = length;
        this.nextSymbol = nextSymbol;
    }
}
