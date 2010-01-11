package net.sourceforge.subsonic.backend.servlet;

import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Runs a task at regular intervals, checking for incoming donations and sending
 * out license keys by email.
 *
 * @author Sindre Mehus
 * @version $Id$
 */
public class LicenseServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(LicenseServlet.class);

    private static final long DELAY = 5 * 60; // Five minutes.
    private static final String POP_MAIL_SERVER = "pop.gmail.com";
    private static final String SMTP_MAIL_SERVER = "smtp.gmail.com";
    private static final String USER = "subsonic@activeobjects.no";
    private static final String[] DONATION_SUBJECTS = {
            "Notification of donation received",
            "Payment received",
            "Notification of payment received",
            "Notification of a Cleared eCheck Payment",
            "You've got money"};

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private Session session;
    private String password;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Runnable task = new Runnable() {
            public void run() {
                try {
                    LOG.info("Starting license generator.");
                    processMessages();
                    LOG.info("Completed license generator.");
                } catch (Throwable x) {
                    LOG.error("Failed to process license emails.", x);
                }
            }
        };
        executor.scheduleWithFixedDelay(task, DELAY, DELAY, TimeUnit.SECONDS);
        LOG.info("Scheduled license generator to run every " + DELAY + " seconds.");
    }

    @Override
    public void destroy() {
        super.destroy();
        executor.shutdown();
    }

    private void processMessages() throws Exception {
        initSession();
        Store store = session.getStore();
        store.connect(POP_MAIL_SERVER, USER, password);
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);

        try {
            int messageCount = folder.getMessageCount();
            LOG.info("Got " + messageCount + " message(s).");

            Message[] messages = folder.getMessages();
            for (Message message : messages) {
                if (isDonationMessage(message)) {
                    sendLicense(message);
                    message.setFlag(Flags.Flag.DELETED, true);
                }
            }
        } finally {
            folder.close(true);
            store.close();
        }
    }

    private String getPassword() throws IOException {
        Reader reader = new FileReader("/var/subsonic-backend/gmailpwd.txt");
        try {
            return StringUtils.trimToNull(IOUtils.toString(reader));
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private void initSession() throws IOException {
        Properties props = new Properties();
//        props.setProperty("mail.debug", "true");
        props.setProperty("mail.store.protocol", "pop3s");
        props.setProperty("mail.smtps.host", SMTP_MAIL_SERVER);
        props.setProperty("mail.smtps.auth", "true");

        session = Session.getDefaultInstance(props, null);
        password = getPassword();
    }

    private boolean isDonationMessage(Message message) throws MessagingException, IOException {
        String subject = message.getSubject();
        LOG.info("Evaluating message with subject: '" + subject + "' sent " + message.getSentDate());
        for (String donationSubject : DONATION_SUBJECTS) {
            if (subject != null && subject.toLowerCase().contains(donationSubject.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void sendLicense(Message donationMessage) throws MessagingException {
        Message licenseMessage = donationMessage.reply(false);
        Address to = licenseMessage.getRecipients(Message.RecipientType.TO)[0];
        sendMessage(licenseMessage, to);
    }

    public void sendLicenseTo(String recipient) throws MessagingException {
        Address to = new InternetAddress(recipient);
        Message licenseMessage = new MimeMessage(session);
        licenseMessage.setRecipient(Message.RecipientType.TO, to);
        sendMessage(licenseMessage, to);
    }

    private void sendMessage(Message licenseMessage, Address to) throws MessagingException {
        Address from = new InternetAddress("subsonic_donation@activeobjects.no");
        Address bcc = new InternetAddress("sindre@activeobjects.no");

        licenseMessage.setSubject("Subsonic License");
        licenseMessage.setFrom(from);
        licenseMessage.setReplyTo(new Address[]{from});
        licenseMessage.setRecipients(Message.RecipientType.BCC, new Address[]{from, bcc});
        licenseMessage.setText(createLicenseContent(to));

        // Send the message
        Transport transport = null;
        try {
            transport = session.getTransport("smtps");
            transport.connect(USER, password);
            transport.sendMessage(licenseMessage, licenseMessage.getAllRecipients());
        } finally {
            if (transport != null) {
                transport.close();
            }
        }

        LOG.info("Sent license to " + to);
    }

    private String createLicenseContent(Address emailAddress) {
        String address = ((InternetAddress) emailAddress).getAddress();
        String license = md5Hex(address.toLowerCase());

        return "Dear Subsonic donor,\n" +
                "\n" +
                "Many thanks for your kind donation to Subsonic!\n" +
                "Please find your license key below.\n" +
                "\n" +
                "Email: " + address + "\n" +
                "License: " + license + " \n" +
                "\n" +
                "Thanks again for supporting the project!\n" +
                "\n" +
                "Best regards,\n" +
                "Sindre";
    }

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex string.
     *
     * @param s Data to digest.
     * @return MD5 digest as a hex string.
     */
    public static String md5Hex(String s) {
        if (s == null) {
            return null;
        }

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return new String(Hex.encodeHex(md5.digest(s.getBytes("UTF-8"))));
        } catch (Exception x) {
            throw new RuntimeException(x.getMessage(), x);
        }
    }

}
