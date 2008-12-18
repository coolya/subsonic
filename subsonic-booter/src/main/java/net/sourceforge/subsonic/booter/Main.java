package net.sourceforge.subsonic.booter;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Application entry point for Subsonic booter.
 * <p/>
 * Use command line argument "-agent" to start the service monitoring agent.
 *
 * @author Sindre Mehus
 */
public class Main {

    public Main(boolean agent) {
        String context = agent ? "applicationContext-agent.xml" : "applicationContext-deployer.xml";
        new ClassPathXmlApplicationContext(context);
    }

    public static void main(String[] args) {
        boolean agent = args.length > 0 && "-agent".equals(args[0]);
        new Main(agent);
    }
}
