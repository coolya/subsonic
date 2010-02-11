package net.sourceforge.subsonic.booter;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Application entry point for Subsonic booter.
 * <p/>
 * Use command line argument "-agent" to start the Windows service monitoring agent,
 * or "-mac" to start the Mac version of the deployer.
 *
 * @author Sindre Mehus
 */
public class Main {

    public Main(String context) {
        new ClassPathXmlApplicationContext("applicationContext" + context + ".xml");
    }

    public static void main(String[] args) {
        String context = "-deployer";
        if (args.length > 0) {
            context = args[0];
        }
        new Main(context);
    }
}
