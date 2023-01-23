import java.io.IOException;

public class App {
    public static void main(String[] args) throws Exception {
        
        Game game = new Game();
        ConsoleUI ui = new ConsoleUI(game);
        Fwtp fwtp = new Fwtp(game);

        if(args.length == 0) {
            try {
                fwtp.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(args.length >= 1) {
            String host = args[0];

            try {
                fwtp.connect(host);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
