public abstract class Gui {
    Game game;
    
    public Gui(Game game) {
        this.game = game;
        game.registerUI(this);
    }
    
    public void drawField() {
        throw new UnsupportedOperationException("Abstract GUI class does not support drawing.");
    }

    public void showDefeat() {
        throw new UnsupportedOperationException("Abstract GUI class does not support drawing.");
    }

    public void showVictory() {
        throw new UnsupportedOperationException("Abstract GUI class does not support drawing.");
    }

    public void displayExceptionMsg(Exception e) {
        throw new UnsupportedOperationException("Abstract GUI class does not support drawing.");
    };

    public int getOwnTurn() {
        throw new UnsupportedOperationException("Abstract GUI class does not support getting input.");
    }
}
