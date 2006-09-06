package net.sourceforge.subsonic.controller;

/**
 * Used in subsonic-servlet.xml to specify frame reloading.
 *
 * @author Sindre Mehus
 */
public class ReloadFrame {
    private String frame;
    private String view;

    public ReloadFrame() {}

    public ReloadFrame(String frame, String view) {
        this.frame = frame;
        this.view = view;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
