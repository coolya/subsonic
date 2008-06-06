package net.sourceforge.subsonic.jmeplayer.screens;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Sindre Mehus
 */
public class WaitScreen extends Canvas {
    private int count, maximum;

    private int width, height, x, y, radius;
    private String message;
    private Timer timer;

    public WaitScreen() {
        count = 0;
        maximum = 36;
        int interval = 100;

        width = getWidth();
        height = getHeight();

        // Calculate the radius.
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        radius = Math.min(halfWidth, halfHeight);

        // Calculate the location.
        x = halfWidth - radius / 2;
        y = halfHeight - radius / 2;

        // Create a Timer to update the display.
        TimerTask task = new TimerTask() {
            public void run() {
                count = (count + 1) % maximum;
                repaint();
            }
        };
        timer = new Timer();
        timer.schedule(task, 0, interval);

        // TODO: stop timer when screen disappears.
    }

    public void setMessage(String s) {
        message = s;
        repaint();
    }

    public void stop() {
        timer.cancel();
    }

    public void paint(Graphics g) {
        int theta = -(count * 360 / maximum);

        // Clear the whole screen.
        g.setColor(255, 255, 255);
        g.fillRect(0, 0, width, height);

        // Now draw the pinwheel.
        g.setColor(0, 0, 0);
        g.drawArc(x, y, radius, radius, 0, 360);
        g.fillArc(x, y, radius, radius, theta + 90, 90);
        g.fillArc(x, y, radius, radius, theta + 270, 90);

        // Draw the message, if there is a message.
        if (message != null) {
            g.drawString(message, width / 2, height, Graphics.BOTTOM | Graphics.HCENTER);
        }
    }
}