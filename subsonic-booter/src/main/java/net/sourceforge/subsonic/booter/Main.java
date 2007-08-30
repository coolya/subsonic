package net.sourceforge.subsonic.booter;


import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * @author Sindre Mehus
 */
public class Main {


    public Main() throws Exception {

        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(8080);
        server.addConnector(connector);

        WebAppContext context = new WebAppContext();
//        context.setTempDirectory(new File("/tmp/jetty"));
        context.setContextPath("/subsonic");
        context.setWar("file:///projects/subsonic/trunk/subsonic-main/target/subsonic.war");

        server.addHandler(context);
        server.start();

    }

    public static void main(String[] args) throws Exception {
        new Main();
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
