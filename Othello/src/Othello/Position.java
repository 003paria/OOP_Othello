
package assignment2;

public abstract class Position {

    protected char piece;
    public static final char EMPTY = ' ';
    public static final char BLACK = 'B';
    public static final char WHITE = 'W';

    public Position() {
        this.piece = EMPTY;
    }

    public abstract boolean canPlay();

    public char getPiece() {
        return piece;
    }

    public void setPiece(char piece) {
        this.piece = piece;
    }
}
