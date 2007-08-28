package net.sourceforge.subsonic.booter;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Sindre Mehus
 */
public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Subsonic");

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        String dir = System.getProperty("user.dir");
        frame.getContentPane().add(new JLabel(dir));
        frame.setBounds(300, 300, 300, 200);
        frame.setVisible(true);


    }
}
