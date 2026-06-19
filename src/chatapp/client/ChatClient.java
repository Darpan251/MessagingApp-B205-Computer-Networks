package chatapp.client;

import chatapp.common.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * ChatClient.java
 *
 * Console-based client application.
 *
 * Responsibilities:
 * - Connect to the server using Java sockets.
 * - Ask the user for a unique username.
 * - Send commands/messages to the server.
 * - Receive real-time messages from the server using a background thread.
 */
public class ChatClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    private Socket socket;
    private BufferedReader serverInput;
    private PrintWriter serverOutput;
    private BufferedReader keyboardInput;

    private volatile boolean running = true;

    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;

        if (args.length >= 1) {
            host = args[0];
        }

        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port. Using default port 5000.");
                port = DEFAULT_PORT;
            }
        }

        ChatClient client = new ChatClient();
        client.start(host, port);
    }

    /**
     * Starts the client.
     */
    public void start(String host, int port) {
        try {
            socket = new Socket(host, port);
            serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOutput = new PrintWriter(socket.getOutputStream(), true);
            keyboardInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connected to server " + host + ":" + port);

            registerUsername();

            Thread receiverThread = new Thread(this::receiveMessagesFromServer);
            receiverThread.setDaemon(true);
            receiverThread.start();

            printLocalHelp();
            readUserInputLoop();

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());

        } finally {
            closeClient();
        }
    }

    /**
     * Reads username from console and sends HELLO|username to the server.
     */
    private void registerUsername() throws IOException {
        System.out.print("Enter username: ");
        String username = keyboardInput.readLine();

        if (username == null || username.trim().isEmpty()) {
            username = "guest" + System.currentTimeMillis();
        }

        serverOutput.println(Protocol.build(Protocol.HELLO, username.trim()));
    }

    /**
     * Keeps reading messages from the user.
     *
     * Any normal text becomes a chat message.
     * Commands start with /.
     */
    private void readUserInputLoop() throws IOException {
        String userInput;

        while (running && (userInput = keyboardInput.readLine()) != null) {
            userInput = userInput.trim();

            if (userInput.isEmpty()) {
                continue;
            }

            if (userInput.startsWith("/")) {
                handleLocalCommand(userInput);
            } else {
                serverOutput.println(Protocol.build(Protocol.MSG, userInput));
            }
        }
    }

    /**
     * Converts user-friendly slash commands into protocol messages.
     */
    private void handleLocalCommand(String commandLine) {
        String[] parts = commandLine.split("\\s+", 3);
        String command = parts[0].toLowerCase();

        switch (command) {
            case "/rooms":
                serverOutput.println(Protocol.ROOMS);
                break;

            case "/users":
                serverOutput.println(Protocol.USERS);
                break;

            case "/who":
                serverOutput.println(Protocol.WHO);
                break;

            case "/create":
                if (parts.length < 2) {
                    System.out.println("Usage: /create roomName");
                } else {
                    serverOutput.println(Protocol.build(Protocol.CREATE, parts[1]));
                }
                break;

            case "/join":
                if (parts.length < 2) {
                    System.out.println("Usage: /join roomName");
                } else {
                    serverOutput.println(Protocol.build(Protocol.JOIN, parts[1]));
                }
                break;

            case "/switch":
                if (parts.length < 2) {
                    System.out.println("Usage: /switch roomName");
                } else {
                    serverOutput.println(Protocol.build(Protocol.SWITCH, parts[1]));
                }
                break;

            case "/invite":
                if (parts.length < 3) {
                    System.out.println("Usage: /invite username roomName");
                } else {
                    String targetUsername = parts[1];
                    String roomName = parts[2];
                    serverOutput.println(Protocol.build(Protocol.INVITE, targetUsername, roomName));
                }
                break;

            case "/leave":
                serverOutput.println(Protocol.LEAVE);
                break;

            case "/help":
                printLocalHelp();
                serverOutput.println(Protocol.HELP);
                break;

            case "/quit":
                serverOutput.println(Protocol.QUIT);
                running = false;
                closeClient();
                break;

            default:
                System.out.println("Unknown command. Type /help.");
                break;
        }
    }

    /**
     * Receives messages from the server in real time.
     */
    private void receiveMessagesFromServer() {
        try {
            String serverMessage;

            while (running && (serverMessage = serverInput.readLine()) != null) {
                printServerMessage(serverMessage);
            }

        } catch (IOException e) {
            if (running) {
                System.out.println("Disconnected from server: " + e.getMessage());
            }

        } finally {
            running = false;
        }
    }

    /**
     * Makes server protocol responses easier to read on the console.
     */
    private void printServerMessage(String message) {
        String[] parts = message.split("\\|", 4);
        String type = parts[0];

        switch (type) {
            case Protocol.CHAT:
                if (parts.length >= 4) {
                    String room = parts[1];
                    String username = parts[2];
                    String text = parts[3];
                    System.out.println("[" + room + "] " + username + ": " + text);
                } else {
                    System.out.println(message);
                }
                break;

            case Protocol.OK:
                if (parts.length >= 2) {
                    System.out.println("[OK] " + parts[1]);
                } else {
                    System.out.println("[OK]");
                }
                break;

            case Protocol.ERROR:
                if (parts.length >= 2) {
                    System.out.println("[ERROR] " + parts[1]);
                } else {
                    System.out.println("[ERROR]");
                }
                break;

            case Protocol.INFO:
                if (parts.length >= 2) {
                    System.out.println("[INFO] " + parts[1]);
                } else {
                    System.out.println("[INFO]");
                }
                break;

            case Protocol.WELCOME:
                if (parts.length >= 2) {
                    System.out.println("[WELCOME] " + parts[1]);
                } else {
                    System.out.println("[WELCOME]");
                }
                break;

            default:
                System.out.println(message);
                break;
        }
    }

    /**
     * Prints local command help.
     */
    private void printLocalHelp() {
        System.out.println();
        System.out.println("===== Java Chat Client Commands =====");
        System.out.println("Type a normal message and press Enter to send it.");
        System.out.println("/rooms                  Show all chat rooms");
        System.out.println("/users                  Show online users");
        System.out.println("/who                    Show users in your current room");
        System.out.println("/create roomName        Create a new room and join it");
        System.out.println("/join roomName          Join an existing room");
        System.out.println("/switch roomName        Switch to an existing room");
        System.out.println("/invite username room   Invite a user to a room");
        System.out.println("/leave                  Leave your current room");
        System.out.println("/help                   Show help");
        System.out.println("/quit                   Exit");
        System.out.println("=====================================");
        System.out.println();
    }

    /**
     * Closes the client socket.
     */
    private void closeClient() {
        try {
            running = false;

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

        } catch (IOException e) {
            System.out.println("Error closing client: " + e.getMessage());
        }
    }
}