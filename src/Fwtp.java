import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Fwtp {
    public final int FWTP_VERSION = 1;

    private final String[] SUPPORTED_VERSIONS = {"1"};
    private final String DEFAULT_FIELD_DELIMITER = "|";
    private final String DEFAULT_FIELD_DELIMITER_REGEX = "\\|";
    private final String DEFAULT_LIST_DELIMITER = ",";
    private final String DEFAULT_LIST_DELIMITER_REGEX = ",";
    private final int DEFAULT_PORT = 4444;

    private ServerSocket serv;
    private Socket sock;
    private DataInputStream in;
    private BufferedReader inReader;
    private DataOutputStream out;
    private BufferedWriter outWriter;
    private Game game;

    private int connFwtpVersion = -1;  

    private boolean running = false;

    public Fwtp(Game game) {
        this.game = game;
    }

    public void connect(String host) throws UnknownHostException, IOException {
        this.connect(host, DEFAULT_PORT);
    }

    public void connect(String host, int port) throws UnknownHostException, IOException {
        System.out.println("Verbindung wird aufgebaut: " + host + ":" + port);
        this.sock = new Socket(host, port);
        initIO();
        run();
    }

    public void listen() throws IOException {
        this.listen(DEFAULT_PORT);
    }

    public void listen(int port) throws IOException {
        System.out.println("Auf Verbindung warten auf Port " + port);
        this.serv = new ServerSocket(port);
        
        try {
            this.sock = this.serv.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Verbindung aufgebaut: " + sock.getRemoteSocketAddress());

        initIO();
        handshake();
        ownTurn();
        run();
    }

    private void initIO() {
        try {
            this.in = new DataInputStream(this.sock.getInputStream());
            this.inReader = new BufferedReader(new InputStreamReader(this.in));
            this.out = new DataOutputStream(this.sock.getOutputStream());
            this.outWriter = new BufferedWriter(new OutputStreamWriter(this.out));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        this.running = true;

        while (this.running) {
            try {
                String input = inReader.readLine();
                if(input != null) {
                    handlePacket(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
                this.running = false;
            }
        }
    }

    public void sendPacket(String[] fields) {
        try {
            this.outWriter.write(String.join(DEFAULT_FIELD_DELIMITER, fields));
            this.outWriter.write("\n");
            this.outWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePacket(String packet) {
        String[] fields = packet.split(DEFAULT_FIELD_DELIMITER_REGEX);

        int action = Integer.parseInt(fields[0]);

        try {
            if (action == 0) { // -> INCOMING HANDSHAKE INIT
                handleHandshakeInit(fields);
            } else if (action == 1) { // -> INCOMING HANDSHAKE ANSWER
                handleHandshakeAnswer(fields);
            } else if (action == 2) { // -> INCOMING ERROR
                handlePacketError(fields);
            } else if (action == 3) { // -> INCOMING CHIP
                handlePacketChip(fields);
            }    
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private void handleHandshakeInit(String[] fields) {
        int resultVersion = -1;

        String[] versionListRaw = fields[1].split(DEFAULT_LIST_DELIMITER_REGEX);
        
        for (int i = 0; i < versionListRaw.length; i++) {

            int ver = Integer.parseInt(versionListRaw[i]);
            
            boolean isSupported = false;
            
            for(int a = 0; a < SUPPORTED_VERSIONS.length; a++) {
                if(SUPPORTED_VERSIONS[a].matches(versionListRaw[i])) {
                    isSupported = true;
                    break;
                }
            }

            if(ver > resultVersion && isSupported) {
                resultVersion = ver;
            }
        }

        if(resultVersion == -1) {
            this.sendPacket(new String[] {"2", "1", "No fitting version"});
        } else {
            connFwtpVersion = resultVersion;
            this.sendPacket(new String[] {"1", Integer.toString(connFwtpVersion)});
        }

        try {
            outWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleHandshakeAnswer(String[] fields) {
        connFwtpVersion = Integer.parseInt(fields[1]);
    }

    private void handlePacketError(String[] fields) {
        System.err.print("Error ");
        
        if(fields.length >= 2) {
            System.err.print(fields[1]);
            System.err.print(": ");
        }

        if(fields.length >= 3) {
            System.err.println(fields[2]);
        }

        int errorCode = Integer.parseInt(fields[1]);

        if(errorCode == 1) { //! NO MATCHING VERSION
            this.stop();
        } else if(errorCode == 2) { //! INVALID MOVE
            ownTurn();
        }
    }

    public void stop() {
        try {
            this.running = false;

            this.in.close();
            this.out.close();
            this.inReader.close();
            this.outWriter.close();
            this.sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePacketChip(String[] fields) {
        int col = Integer.parseInt(fields[1]);
        try {
            game.insertChip(col, false);
        } catch (InvalidMoveException e) {
            sendPacket(new String[] {"2", "2", e.getMessage()});
            return;
        }

        game.getUi().drawField();
        game.evalWin();
        ownTurn();
    }

    private void ownTurn() {
        boolean retry = true;
        int input = -1;

        while(retry) {
            input = game.getUi().getOwnTurn();
            try {
                game.insertChip(input, true);
                retry = false;
            } catch (InvalidMoveException e) {
                game.getUi().displayExceptionMsg(e);
            }
        }
        
        sendPacket(new String[] {"3", Integer.toString(input)});
        game.getUi().drawField();
        game.evalWin();
    }

    private void handshake() {
        String versionList = String.join(DEFAULT_LIST_DELIMITER, SUPPORTED_VERSIONS);
        this.sendPacket(new String[] {"0", versionList});
    }
}
