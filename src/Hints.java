import java.util.LinkedList;

public class Hints {
    private Game game;

    public Hints(Game game) {
        this.game = game;
    }

    public LinkedList<Integer[]> getHints() {
        LinkedList<Integer[]> hints = new LinkedList<>();
        
        Integer[][][] checkField = game.getCheckField();

        for(int y = 0; y < game.getFieldHeight(); y++) {
            for(int x = 0; x < game.getFieldWidth(); x++) {
                int chip = game.getChipAt(x, y);

                if(chip != 0) continue;

                for(int a = -1; a <= 1; a++) {
                    if(x+a < 0 || x+a > game.getFieldWidth()-1) continue;

                    for(int b = -1; b <= 1; b++) {
                        if(y+b < 0 || y+b > game.getFieldHeight()-1) continue;
                        if(game.getChipAt(x+a, y+b) == 0) continue; 

                        int checkIndex = 3*(a+1) + (b+1);
                        int inverseCheckIndex = 8-checkIndex;

                        if(checkField[x+a][y+b][checkIndex] == 2) {
                            hints.add(new Integer[] {x,y,game.getChipAt(x+a, y+b)});
                            continue;
                        }

                        if(y-b < 0 || y-b > game.getFieldHeight()-1) continue;
                        if(x-a < 0 || x-a > game.getFieldWidth()-1) continue;
                        if(game.getChipAt(x-a, y-b) != game.getChipAt(x+a, y+b)) continue;

                        if(checkField[x-a][y-b][inverseCheckIndex] + checkField[x+a][y+b][checkIndex] >= 1) {
                            hints.add(new Integer[] {x,y,game.getChipAt(x+a, y+b)});
                        }
                    }
                }
                
            }
        }
        
        return hints;
    }
}
