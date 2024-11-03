package assignment2;

public class UnplayablePosition extends Position {


    public static final char UNPLAYABLE = '*';

    public UnplayablePosition(){
        this.piece = UNPLAYABLE;
    }

    @Override
    public boolean canPlay(){
        return false;
    }
}
