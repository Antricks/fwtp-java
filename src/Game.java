public class Game {
    private Gui ui;

    private int fieldWidth = 7;
    private int fieldHeight = 6;

    private Integer[][] field = new Integer[fieldWidth][fieldHeight];

    public Integer[][] getField() {
        return this.field;
    }

    public Game() {
        for(int x = 0; x < fieldWidth; x++) {
            for(int y = 0; y < fieldHeight; y++) {
                field[x][y] = 0;
            }
        }
    }

    public Integer[][][] getCheckField() {
        Integer[][][] checkField = new Integer[fieldWidth][fieldHeight][9];
        
        for (int y = 0; y < fieldHeight; y++) {
            for(int x = 0; x < fieldWidth; x++) {
                
                int chip = getChipAt(x, y);

                for(int a = -1; a <= 1; a++) {
                    for(int b = -1; b <= 1; b++) {
                        if(a == 0 && b == 0) continue;

                        int checkIndex = 3*(a+1) + (b+1);

                        int c = 0;

                        while(getChipAt(x+c*a, y+c*b) == chip) {
                            c++;
                        }

                        checkField[x][y][checkIndex] = c-1;
                    }
                }
            }
        }

        return checkField;
    }

    // 0 no winner, 1 we win, 2 opponent won
    public int checkForWin() {
        Integer[][][] checkField = getCheckField();

        for(int y = 0; y < fieldHeight; y++) {
            for(int x = 0; x < fieldWidth; x++) {
                int chip = getChipAt(x, y);

                if(chip == 0) continue;

                for(int i = 0; i < 9; i++) {
                    if(i == 4) continue;

                    if(checkField[x][y][i] >= 3) {
                        return chip;
                    }
                }
            }
        }

        return 0;
    }

    public int getChipAt(int x, int y) {
        if(x < 0 || x >= fieldWidth || y < 0 || y >= fieldHeight) {
            return -1;
        }
        
        return field[x][y];
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    protected void insertChip(int column, boolean isOwn) throws InvalidMoveException {
        if(column < 0 || column >= this.fieldWidth) {
            throw new InvalidMoveException("Column " + column + " out of bounds.");
        }
        
        int i = 0;
        try {
            while(this.field[column][i] != 0) {
                i++;
            }    
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidMoveException("Column " + column + " is already filled up.");
        }
        
        if(isOwn) {
            this.field[column][i] = 1;
        } else {
            this.field[column][i] = 2;
        }
    }

    public void evalWin() {
        if(this.checkForWin() == 1) {
            this.ui.showVictory();
            System.exit(0);
        } else if(this.checkForWin() == 2) {
            this.ui.showDefeat();
            System.exit(0);
        }
    }

    public void registerUI(Gui ui) {
        this.ui = ui;
    }

    public Gui getUi() {
        return this.ui;
    }
}
