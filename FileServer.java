import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class FileServer {
    private static final int PORT = 12345;
    private static final ArrayList<String> registeredUsers = new ArrayList<>();

    public static void main(String[] args) {
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
        System.out.println("============================================== Sever Side =====================================================");
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : PORT;
        System.out.println("Server: Listening on port " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ConnectionThread(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Connection lost or disconnected");
        } finally {
            System.out.println("Server: Connection is terminated.");
        }
    }

    private static class ConnectionThread extends Thread {
        private Socket clientSocket;
        private String username = "client";

        ConnectionThread(Socket socket) {
            clientSocket = socket;
        }

        public void run() {
            String clientAddress = clientSocket.getInetAddress().toString();

            try (
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())
            ) {
                System.out.println(dis.readUTF());
                dos.writeUTF("Server: Welcome to this File Server!");

                while (true) {
                    String clientRequest = dis.readUTF();
                    System.out.println("Client " + username + " requested: " + clientRequest);

                    if (clientRequest.startsWith("/get")) {
                        String fileName = clientRequest.substring(5);
                        handleget(fileName, dos);
                    }  if (clientRequest.startsWith("/store ")) {
                        String fileName = clientRequest.substring(7);
                        receiveFileFromClient(fileName, dis, username);
                    }
                    else if (clientRequest.startsWith("/leave")) {
                        registeredUsers.remove(username);
                        System.out.println("Client " + username + " has disconnected");
                        return;
                    } else if (clientRequest.startsWith("/dir")) {
                        handleDirCommand(dos);
                    } else if (clientRequest.startsWith("/store")) {
                        String receivingFile = dis.readUTF();
                    } else if (clientRequest.startsWith("/register")) {
                        String aliasToRegister = dis.readUTF();
                        registerUser(aliasToRegister, dos);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error on connection with: " + clientAddress + ": " + e);
            }
        }

        private void registerUser(String alias, DataOutputStream dos) {
            try {
                if (isUsernameAvailable(alias)) {
                    setUsernameAndNotifyClient(alias, dos);
                    System.out.println("Server: Added user " + username + " to server");
                } else {
                    sendStatusMessage(dos, "notregistered");
                }
            } catch (IOException err) {
                System.out.println(err);
            }
        }

        private boolean isUsernameAvailable(String alias) {
            return !registeredUsers.contains(alias);
        }

        private void sendStatusMessage(DataOutputStream dos, String message) throws IOException {
            dos.writeUTF(message);
        }

        private void setUsernameAndNotifyClient(String alias, DataOutputStream dos) throws IOException {
            username = alias;
            registeredUsers.add(alias);
            sendStatusMessage(dos, "registered");
        }

        private static void handleget(String fileName, DataOutputStream dos) {
            File file = new File("serverDirectory/" + fileName);

            if (!file.exists()) {
                System.out.println("File not found: " + fileName);
                try {
                    dos.writeUTF("File not found:");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            try (FileInputStream fis = new FileInputStream(file)) {
                long fileSize = file.length();
                dos.writeUTF("/filesize " + fileSize);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                System.out.println("Error sending file: " + e.getMessage());
            }
        }
        private static void receiveFileFromClient(String fileName, DataInputStream dis, String userName) throws IOException {
            boolean fileExists = dis.readBoolean();
            if (!fileExists) {
                System.out.println("File " + fileName + " not found");
                return;
            }

            File serverDirectory = new File("serverDirectory");
            File file = new File(serverDirectory, fileName);
            FileOutputStream fos = new FileOutputStream(file);

            long fileSize = dis.readLong();
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesRead = 0;

            while (totalBytesRead < fileSize && (bytesRead = dis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }

            fos.close();


            LocalDateTime now = LocalDateTime.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);


            System.out.println(userName + "<" + formattedDateTime + ">: Uploaded " + file.getName());
        }



        private void handleDirCommand(DataOutputStream dos) {
            try {
                File serverDirectory = new File("serverDirectory/");
                File[] files = serverDirectory.listFiles();

                StringBuilder fileList = new StringBuilder();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            fileList.append("[Folder] ");
                        } else {
                            fileList.append("[File] ");
                        }
                        fileList.append(file.getName());
                        fileList.append("\n");
                    }
                }

                dos.writeUTF(fileList.toString());
            } catch (IOException e) {
                System.out.println("Error handling /dir command: " + e);
            }
        }
    }
}
