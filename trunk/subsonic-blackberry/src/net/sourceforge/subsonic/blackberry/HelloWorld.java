package net.sourceforge.subsonic.blackberry;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class HelloWorld extends UiApplication {

    public void start() {
        MainScreen main = new MainScreen();
        LabelField label = new LabelField("Halloen world II");
        System.out.println("This is system out");
        System.err.println("This is system err");
        main.add(label);

        UiApplication app = UiApplication.getUiApplication();
        app.pushScreen(main);
        app.enterEventDispatcher();
    }

    public static void main(String[] args) {
        new HelloWorld().start();
    }

}
