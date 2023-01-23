import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;

public class ConsoleUI extends Gui {
    private final String GRID_COLOR = "\u001b[36m";
    private final String OWN_CHIP_COLOR = "\u001b[34m";
    private final String OPPONENT_CHIP_COLOR = "\u001b[31m";
    private final String NUMBER_COLOR = "\u001b[33m";
    private final String ERROR_COLOR = "\u001b[31m";
    private final String VICTORY_COLOR = "\u001b[34m";
    private final String DEFEAT_COLOR = "\u001b[31m";
    private final String COLOR_RESET = "\u001b[0m";
    
    BufferedReader inputReader;

    private Hints hints;
    
    public ConsoleUI(Game game) {
        super(game);
        this.hints = new Hints(game);
        this.inputReader = new BufferedReader(new InputStreamReader(System.in));
    }

    private void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    @Override
    public void drawField() {
        Integer[][] field = game.getField();
        LinkedList<Integer[]> hintList = hints.getHints();

        this.clear();

        System.out.println("\n" + NUMBER_COLOR + " ".repeat(game.getFieldWidth()*2-5) + "𝓒𝓸𝓷𝓷𝓮𝓬𝓽 𝓕𝓸𝓾𝓻\n");

        System.out.println(GRID_COLOR + "┏" + "━━━┳".repeat(game.getFieldWidth()-1) + "━━━┓");
        
        for(int y = game.getFieldHeight()-1; y >= 0; y--) {
            for(int x = 0; x < game.getFieldWidth(); x++) {
                String chipString = " ";
                
                if(field[x][y] == 1) {
                    chipString = OWN_CHIP_COLOR + "⬤";
                } else if(field[x][y] == 2) {
                    chipString = OPPONENT_CHIP_COLOR + "⬤";
                } else {
                    Iterator<Integer[]> iter = hintList.iterator();

                    while(iter.hasNext()) {
                        Integer[] hint = iter.next();
                        
                        if(x == hint[0] && y == hint[1]) {
                            if(hint[2] == 1) {
                                chipString = OWN_CHIP_COLOR + "⚠";
                                break;
                            } else if(hint[2] == 2) {
                                chipString = OPPONENT_CHIP_COLOR + "⚠";
                                break;
                            }
                        }
                    }
                }
                
                System.out.print(GRID_COLOR + "┃ " + chipString + " ");
            }
            System.out.println(GRID_COLOR + "┃");

            if(y != 0) {
                System.out.println(GRID_COLOR + "┣" + "━━━╋".repeat(game.getFieldWidth()-1) + "━━━┫");
            }
        }
        System.out.println("┗" + "━━━┻".repeat(game.getFieldWidth()-1) + "━━━┛");
        
        System.out.print(NUMBER_COLOR);
        for(int x = 0; x < game.getFieldWidth(); x++) {
            System.out.print("  " + (x+1) + " ".repeat(2-Integer.toString(x+1).length()));
        }
        System.out.println("\n" + COLOR_RESET);
    }

    @Override
    public int getOwnTurn() {

        this.clear();
        this.drawField();

        int input = -1;
        boolean retry = true;

        while(retry) {
            System.out.print(OWN_CHIP_COLOR + "[⬤]" + COLOR_RESET + " Please enter a number from 1 to " + game.getFieldWidth() + ": " + OWN_CHIP_COLOR);

            try {
                input = Integer.parseInt(inputReader.readLine())-1;
                
                if(0 <= input && input <= game.getFieldWidth()) {
                    retry = false;
                }
            } catch (NumberFormatException e) {
                System.err.println(ERROR_COLOR + "That's not a valid number.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return input;
    }

    @Override
    public void showDefeat() {
        this.clear();
        this.drawField();
    
        System.out.print(DEFEAT_COLOR);
        System.out.println("𝐘𝐎𝐔 𝐋𝐎𝐒𝐓. 𝐁𝐄𝐓𝐓𝐄𝐑 𝐋𝐔𝐂𝐊 𝐍𝐄𝐗𝐓 𝐓𝐈𝐌𝐄!                       ");    
        System.out.print(COLOR_RESET);
    }

    @Override
    public void showVictory() {
        this.clear();
        this.drawField();

        System.out.print(VICTORY_COLOR);
        System.out.println("𝐂𝐎𝐍𝐆𝐑𝐀𝐓𝐒 - 𝐘𝐎𝐔 𝐖𝐎𝐍!                   ");
        System.out.print(COLOR_RESET);
    }

    @Override
    public void displayExceptionMsg(Exception exception) {
        System.out.println(ERROR_COLOR + "[ERR] " + exception.getMessage());
        System.out.println(NUMBER_COLOR + "[Press Enter to continue...]");
        try {
            inputReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
