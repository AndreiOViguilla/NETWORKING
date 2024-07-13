
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class FileClientGUI extends JPanel {
    private JButton jcomp1;
    private JButton jcomp2;
    private JLabel jcomp3;
    public FileClientGUI() {
        //construct components
        jcomp1 = new JButton ("CONNECT");
        jcomp2 = new JButton ("EXIT");
        jcomp3 = new JLabel ("WELCOME TO THE FILE EXCHANGER");

        //adjust size and set layout
        setPreferredSize (new Dimension (626, 337));
        setLayout (null);

        //add components
        add (jcomp1);
        add (jcomp2);
        add (jcomp3);

        //set component bounds (only needed by Absolute Positioning)
        jcomp1.setBounds (250, 110, 100, 20);
        jcomp2.setBounds (250, 135, 100, 20);
        jcomp3.setBounds (185, 70, 225, 30);

        jcomp1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("CONNECT button clicked");
                changePanel(new ConnectPanel(), new Dimension(650, 400));
            }
        });

        jcomp2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitAction();
                System.out.println("EXIT button clicked");
            }
        });

    }
    private void changePanel(JPanel newPanel, Dimension newSize) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(newPanel);
        frame.setSize(newSize);
        frame.setLocationRelativeTo(null);
        frame.revalidate();
        frame.repaint();
    }

    private void exitAction() {
        int confirm = JOptionPane.showOptionDialog(
                this,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, null);

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("File Client GUI");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                FileClientGUI panel = new FileClientGUI();
                frame.getContentPane().add(panel);

                frame.setSize(626, 337);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
