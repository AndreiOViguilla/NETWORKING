import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class FileClient {
    private static Socket clientEndpoint;
    private static DataOutputStream dos;
    private static DataInputStream dis;
    static String sUsername = "client";
    private static boolean isRegistered = false;

    public static void main(String[] args) {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        Socket socket = null;
        boolean isConnected = false;
        String fileName = "";

        try {
            System.out.println("============================================= Welcome to ====================================================");
            System.out.println("                                      ___________.__.__                                  ");
            System.out.println("                                      \\_   _____/|__|  |   ____                         ");
            System.out.println("                                       |    __)  |  |  | _/ __ \\                        ");
            System.out.println("                                       |     \\   |  |  |\\  ___/                        ");
            System.out.println("                                       \\___  /   |__|____/\\___  >                       ");
            System.out.println("                                           \\/                 \\/                        ");
            System.out.println("___________             .__                                     _________              __ ");
            System.out.println("\\_   _____/__  ___ ____ |  |__ _____    ____    ____   ____    /   _____/__.__. ______/  |_  ____   _____ ");
            System.out.println(" |    __)_\\  \\/  // ___\\|  |  \\__  \\  /    \\  / ___\\_/ __ \\   \\_____  <   |  |/  ___|   __\\/ __ \\ /     \\");
            System.out.println(" |        \\>    <\\  \\___|   Y  \\/ __ \\|   |  \\/ /_/  >  ___/   /        \\___  |\\___ \\ |  | \\  ___/|  Y Y  \\");
            System.out.println("/_______  /__/\\_ \\\\___  >___|  (____  /___|  /\\___  / \\___  > /_______  / ____/____  >|__|  \\___  >__|_|  /");
            System.out.println("        \\/      \\/    \\/     \\/     \\/     \\//_____/      \\/          \\/\\/         \\/           \\/      \\/");
            System.out.println("============================================= Client Side ====================================================");
            while (true) {
                String userCommand = userInput.readLine();

                switch (userCommand.split(" ")[0]) {
                    case "/join":
                        String[] joinCommandParts = userCommand.split(" ");
                        if (joinCommandParts.length != 3) {
                            System.out.println("Error: Command parameters do not match or is not allowed.");
                            continue;
                        }
                        if (isConnected) {
                            System.out.println("Already connected. Please use other commands.");
                            continue;
                        }

                        String serverIP = joinCommandParts[1];
                        int serverPort = Integer.parseInt(joinCommandParts[2]);

                        if (isRegistered) {
                            System.out.println("Error: You are already registered. You cannot register again.");
                            continue;
                        }

                        if (joinServer(serverIP, serverPort)) {
                            isConnected = true;
                        } else {
                            System.out.println("Can't Connect");
                            continue;
                        }
                        continue;

                    case "/leave":
                        if (isConnected) {
                            try {

                                dos.writeUTF("/leave");
                                System.out.println("You left the Server. Thank you!");
                            } catch (IOException e) {
                                System.out.println("Error sending leave command to the server.");
                            }
                        } else {
                            System.out.println("Not connected to any server.");
                        }
                        isConnected = false;
                        isRegistered = false;
                        continue;

                    case "/register":
                        String[] registerCommandPartsq = userCommand.split(" ");
                        if (registerCommandPartsq.length != 2) {
                            System.out.println("Error: Command parameters do not match or is not allowed.");
                            continue;
                        }
                        if (isConnected && !isRegistered) {
                            try {
                                String[] registerCommandParts = userCommand.split(" ");
                                if (registerCommandParts.length != 2) {
                                    System.out.println("Error: Command parameters do not match or is not allowed.");
                                    continue;
                                }

                                String handle = registerCommandParts[1];
                                registerHandle(handle);
                            } catch (IOException e) {
                                System.out.println("Error sending registration command to the server.");
                            }
                        } else if (!isConnected) {
                            System.out.println("Not connected to any server. Use /join command first.");
                        } else if (isRegistered) {
                            System.out.println("Error: You are already registered. You cannot register again.");
                        }
                        continue;


                    case "/store":
                        if (isConnected) {
                            String[] parts = userCommand.split(" ");
                            if (parts.length != 2) {
                                System.out.println("Error: Command parameters do not match or is not allowed.");
                                continue;
                            }

                            String filePath = parts[1];
                            dos.writeUTF("/store " + filePath);
                            sendFileToServer(filePath, dos, sUsername);
                        } else {
                            System.out.println("Not connected to any server.");
                        }
                        continue;

                    case "/dir":
                        if (isConnected && isRegistered) {
                            try {
                                handleDirCommand();
                            } catch (IOException e) {
                                System.out.println("Error handling /dir command: " + e);
                            }
                        } else if (!isConnected) {
                            System.out.println("Not connected to any server. Use /join command first.");
                        } else if (!isRegistered) {
                            System.out.println("Error: You are not yet registered. Use /register command first.");
                        }
                        continue;

                    case "/get":
                        if (isConnected && isRegistered) {
                            String[] parts = userCommand.split(" ");
                            if (parts.length != 2) {
                                System.out.println("Error: Command parameters do not match or is not allowed.");
                                continue;
                            }

                            String requestedFileName = parts[1];
                            dos.writeUTF("/get " + requestedFileName);
                            handleFilerecieve(requestedFileName, dis);
                        } else if (!isConnected) {
                            System.out.println("Not connected to any server. Use /join command first.");
                        } else if (!isRegistered) {
                            System.out.println("Error: You are not yet registered. Use /register command first.");
                        }
                        continue;

                    case "/?":
                        System.out.println("Choose a test case:");
                        System.out.println("1. /join <server_ip_address> <port>");
                        System.out.println("2. /leave");
                        System.out.println("3. /register <handle>");
                        System.out.println("4. /store <filename>");
                        System.out.println("5. /dir");
                        System.out.println("6. /get <filename>");
                        System.out.println("7. Request command help to output all Input Syntax commands for references /?");
                        continue;

                    default:
                        System.out.println("Error: Command not found.");
                        continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean joinServer(String IP, int Port) {
        try {
            establishConnection(IP, Port);

            dos = new DataOutputStream(clientEndpoint.getOutputStream());
            sendConnectionMessage();

            dis = new DataInputStream(clientEndpoint.getInputStream());
            printConnectionMessage();

            return true;
        } catch (ConnectException e) {
            handleConnectException(e);
        } catch (Exception e) {
            handleException(e);
        }
        return false;
    }

    private static void establishConnection(String IP, int Port) throws IOException {
        clientEndpoint = new Socket(IP, Port);
        System.out.println("Connection to the File Exchange Server is successful!");
    }

    private static void sendConnectionMessage() throws IOException {
        dos.writeUTF("New client connected: " + clientEndpoint.getLocalSocketAddress());
    }

    private static void printConnectionMessage() throws IOException {
        System.out.println(dis.readUTF());
    }

    private static void handleConnectException(ConnectException e) {
        System.out.println("Error: Connection to the Server has failed! Please check IP Address and Port Number.");
    }

    public static void registerHandle(String alias) throws IOException{
        try {
            dos.writeUTF("/register");
            dos.writeUTF(alias);
            String sServerResponse = dis.readUTF();

            handleRegistrationResponse(alias, sServerResponse);

        } catch (IOException e) {
            handleIOException(e);
        }
    }

    private static void handleRegistrationResponse(String alias, String serverResponse) {
        try {
            if (serverResponse.equals("notregistered")) {
                System.out.println("Error: Registration failed. Handle or alias already exists.");
            } else if (serverResponse.equals("registered")) {
                System.out.println("Welcome " + alias + "!");
                sUsername = alias;
                isRegistered = true;
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    private static void handleIOException(IOException e) {
        System.out.println("Error: " + e.getMessage());

        if (e instanceof SocketException) {
            System.out.println("Server has unexpectedly closed the connection");
            sUsername = "client";
            clientEndpoint = null;

            try {
                if (clientEndpoint != null && !clientEndpoint.isClosed()) {
                    clientEndpoint.close();
                }
            } catch (IOException err) {
                handleException(err);
            }
        }
    }

    private static void handleException(Exception e) {
        System.out.println(e);
    }

    public static void handleDirCommand() throws IOException {
        try {
            dos.writeUTF("/dir");

            String serverResponse = dis.readUTF();
            if (serverResponse.equals("/dirErr!")) {
                System.out.println("Error: Unable to retrieve server directory.");
            } else {
                System.out.println("Server Directory:\n" + serverResponse);
            }
        } catch (IOException e) {
            System.out.println("Error handling /dir command: " + e);
        }
    }
    private static void sendFileToServer(String filePath, DataOutputStream dos, String userName) throws IOException {
        File file = new File(filePath);
        dos.writeBoolean(file.exists());

        if (!file.exists()) {
            System.out.println("Error: File not found in the server.");
            return;
        }

        FileInputStream fis = new FileInputStream(file);
        dos.writeLong(file.length());

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            dos.write(buffer, 0, bytesRead);
        }

        fis.close();


        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);


        System.out.println(userName + "<" + formattedDateTime + ">: Uploaded " + file.getName());
    }

    private static void handleFilerecieve(String fileName, DataInputStream dis) {
        try {
            String serverResponse = dis.readUTF();
            if (serverResponse.startsWith("/filesize ")) {
                long fileSize = Long.parseLong(serverResponse.substring(10));
                FileOutputStream fos = new FileOutputStream("clientFiles/" + fileName); // Adjust the directory as needed
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytesRead = 0;

                while (totalBytesRead < fileSize && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }

                fos.close();
                System.out.println("File " + fileName + " received successfully.");
            } else {
                System.out.println("File not found on server.");
            }
        } catch (IOException e) {
            System.out.println("Error receiving file: " + e.getMessage());
        }
    }
}
