package assignment2;

public class PlayablePosition extends Position {

    public PlayablePosition(){
        this.piece = EMPTY;
    }

    @Override
    public boolean canPlay(){
        return true;
    }


}