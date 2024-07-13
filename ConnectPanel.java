import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

class ConnectPanel extends JPanel {
    private JButton jcomp1;
    private JButton jcomp2;
    private JLabel jcomp3;
    private JTextField jcomp4;
    private JButton jcomp5;
    private JList jcomp6;
    private JButton jcomp7;
    private JButton jcomp8;
    boolean isConnected = false;
    boolean isRegistered = false;
    public ConnectPanel() {
        //construct preComponents
        String[] jcomp6Items = {"CONNECT TO THE SERVER FIRST"};

        //construct components
        jcomp1 = new JButton ("JOIN");
        jcomp2 = new JButton ("REGISTER");
        jcomp3 = new JLabel ("WELCOME TO THE FILE EXCHANGER");
        jcomp4 = new JTextField (5);
        jcomp5 = new JButton ("SEND");
        jcomp6 = new JList (jcomp6Items);
        jcomp7 = new JButton ("LEAVE");
        jcomp8 = new JButton ("GET");
        JScrollPane scrollPane = new JScrollPane(jcomp6);
        scrollPane.setBounds(175, 145, 275, 145);
        add(scrollPane);
        //adjust size and set layout
        setPreferredSize (new Dimension (626, 337));
        setLayout (null);
        //add components
        add (jcomp1);
        add (jcomp2);
        add (jcomp3);
        add (jcomp4);
        add (jcomp5);
        add (jcomp6);
        add (jcomp7);
        add (jcomp8);
        //set component bounds (only needed by Absolute Positioning)
        jcomp1.setBounds (50, 110, 100, 20);
        jcomp2.setBounds (50, 135, 100, 20);
        jcomp3.setBounds (185, 70, 225, 30);
        jcomp4.setBounds (175, 110, 275, 25);
        jcomp5.setBounds (455, 110, 100, 25);
        jcomp6.setBounds (175, 145, 275, 145);
        jcomp7.setBounds (50, 160, 100, 25);
        jcomp8.setBounds (265, 295, 100, 25);

        jcomp1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("JOIN button clicked");

                if (isConnected) {
                    // Display a pop-up window indicating that the user is already in a server
                    JOptionPane.showMessageDialog(null, "You are already in a server", "Already Connected", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Add logic for the JOIN button
                String serverAddress = JOptionPane.showInputDialog("Enter Server IP Address:");
                String serverPortString = JOptionPane.showInputDialog("Enter Server Port:");

                // Check if the user clicked Cancel or closed the dialog
                if (serverAddress == null || serverPortString == null) {
                    System.out.println("Join canceled by the user.");
                    return;
                }
                try {
                    int serverPort = Integer.parseInt(serverPortString);

                    // Call the method from FileClient to join the server
                    boolean joinSuccess = FileClient.joinServer(serverAddress, serverPort);

                    if (joinSuccess && !isConnected) {
                        // Update the GUI or perform any other actions on successful join
                        System.out.println("Successfully joined the server!");
                        isConnected = true;
                        enableButtons(true);
                    } else {
                        System.out.println("Failed to join the server!");
                        JOptionPane.showMessageDialog(null, "Failed to join the server. Please check IP Address and Port Number.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid port number. Please enter a valid integer.");
                }
            }
        });

        jcomp2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("REGISTER button clicked");
                if (!isConnected) {
                    // Display a pop-up window indicating that the user needs to join a server first
                    JOptionPane.showMessageDialog(null, "You need to join a server first", "Not Connected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Add logic for the REGISTER button
                String aliasToRegister = JOptionPane.showInputDialog("Enter the name for registration:");

                // Check if the user clicked Cancel or closed the dialog
                if (aliasToRegister == null) {
                    System.out.println("Registration canceled by the user.");
                    return;
                }

                try {
                    // Call the method from FileClient to register the user
                    boolean registrationSuccess = FileClient.registerHandles(aliasToRegister);
                    // Check if registration was successful
                    if (registrationSuccess) {
                        // Update the GUI or perform any other actions on successful registration
                        System.out.println("Successfully registered as: " + aliasToRegister);
                        List<String> fileList = FileClient.getClientList();
                        jcomp6.setListData(fileList.toArray(new String[0]));
                        isRegistered = true;
                        enableButtons(true);
                    } else {
                        // Display an error message if registration failed
                        System.out.println("Error: Registration failed. Handle or alias already exists.");
                        JOptionPane.showMessageDialog(null, "Error: Registration failed. Handle or alias already exists.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    System.out.println("Error registering the user: " + ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Error registering the user. Please try again.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        jcomp5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("SEND button clicked");
                // Add logic for the SEND button
            }
        });

        jcomp7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("LEAVE button clicked");

                if (isConnected) {
                    // Add logic for the LEAVE button
                    try {
                        // Call the method from FileClient to leave the server
                        FileClient.leaveServer();

                        // Update the GUI or perform any other actions on successful leave
                        System.out.println("Successfully left the server!");
                        isConnected = false;
                        isRegistered = false;
                        enableButtons(false);
                    } catch (IOException ex) {
                        System.out.println("Error leaving the server: " + ex.getMessage());
                        JOptionPane.showMessageDialog(null, "Error leaving the server. Please try again.", "Leave Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Display a pop-up window indicating that the user is not connected to any server
                    JOptionPane.showMessageDialog(null, "You are not connected to any server", "Not Connected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });


        // Inside the ConnectPanel class
        jcomp8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("GET button clicked");

                if (isConnected && isRegistered) {


                    // Optionally, you can display a message to indicate the successful retrieval of the file list
                    System.out.println("File list retrieved successfully.");
                } else {
                    // Display a pop-up window indicating that the user is not connected or registered
                    JOptionPane.showMessageDialog(null, "You need to be connected and registered to get the file list", "Not Connected or Registered", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        // Initial state of the buttons
        enableButtons(false);
    }
    private void enableButtons(boolean enable) {
        jcomp2.setEnabled(!isRegistered && enable); // Disable REGISTER if already registered
        jcomp5.setEnabled(isRegistered && enable);  // Enable SEND only if registered
        jcomp7.setEnabled(enable);
        jcomp8.setEnabled(isRegistered && enable);  // Enable GET only if registered
    }

}
