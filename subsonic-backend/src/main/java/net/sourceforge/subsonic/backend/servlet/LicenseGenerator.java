package net.sourceforge.subsonic.backend.servlet;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import net.sourceforge.subsonic.backend.Util;
import net.sourceforge.subsonic.backend.dao.PaymentDao;
import net.sourceforge.subsonic.backend.domain.Payment;

/**
 * Runs a task at regular intervals, checking for incoming donations and sending
 * out license keys by email.
 *
 * @author Sindre Mehus
 * @version $Id$
 */
public class LicenseGenerator {

    private static final Logger LOG = Logger.getLogger(LicenseGenerator.class);

    private static final long DELAY = 60; // One minute.
    private static final String SMTP_MAIL_SERVER = "smtp.gmail.com";
    private static final String USER = "subsonic@activeobjects.no";

    private PaymentDao paymentDao;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private Session session;
    private String password;

    public void init() {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    LOG.info("Starting license generator.");
                    processPayments();
                    LOG.info("Completed license generator.");
                } catch (Throwable x) {
                    LOG.error("Failed to process license emails.", x);
                }
            }
        };
        executor.scheduleWithFixedDelay(task, DELAY, DELAY, TimeUnit.SECONDS);
        LOG.info("Scheduled license generator to run every " + DELAY + " seconds.");
    }

    private void processPayments() throws Exception {

        List<Payment> payments = paymentDao.getPaymentsByProcessingStatus(Payment.ProcessingStatus.NEW);
        LOG.info(payments.size() + " new payment(s).");
        if (payments.isEmpty()) {
            return;
        }

        initSession();
        for (Payment payment : payments) {
            processPayment(payment);
        }
    }

    private void initSession() throws IOException {
        Properties props = new Properties();
//        props.setProperty("mail.debug", "true");
        props.setProperty("mail.store.protocol", "pop3s");
        props.setProperty("mail.smtps.host", SMTP_MAIL_SERVER);
        props.setProperty("mail.smtps.auth", "true");
        props.put("mail.smtps.timeout", "10000");
        props.put("mail.smtps.connectiontimeout", "10000");
        props.put("mail.pop3s.timeout", "10000");
        props.put("mail.pop3s.connectiontimeout", "10000");

        session = Session.getDefaultInstance(props, null);
        password = Util.getPassword("gmailpwd.txt");
    }

    private void processPayment(Payment payment) {
        try {
            LOG.info("Processing " + payment);
            String email = payment.getPayerEmail();
            if (email == null) {
                throw new Exception("Missing email address.");
            }

            if (isEligible(payment)) {
                sendLicenseTo(email);
                payment.setProcessingStatus(Payment.ProcessingStatus.COMPLETED);
                payment.setLastUpdated(new Date());
                paymentDao.updatePayment(payment);
                LOG.info("Sent license key for " + payment);
            } else {
                LOG.info("Payment not eligible for " + payment);
            }

        } catch (Throwable x) {
            LOG.error("Failed to process " + payment, x);
        }
    }

    private boolean isEligible(Payment payment) {
        String status = payment.getPaymentStatus();
        if ("echeck".equalsIgnoreCase(payment.getPaymentType())) {
            return "Pending".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status);
        }
        return "Completed".equalsIgnoreCase(status);
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
                "To install the license, click the \"Donate\" link in the top right corner of the Subsonic " +
                "web interface.\n" +
                "\n" +
                "More info here: http://subsonic.org/pages/getting-started.jsp#3" +
                "\n" +
                "Thanks again for supporting the project!\n" +
                "\n" +
                "Best regards,\n" +
                "Sindre\n" +
                "\n" +
                "--\n" +
                "Sindre Mehus\n" +
                "Subsonic developer";
    }

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex string.
     *
     * @param s Data to digest.
     * @return MD5 digest as a hex string.
     */
    private static String md5Hex(String s) {
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

    public void setPaymentDao(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    public static void main(String[] args) {
        String address = args[0];
        String license = md5Hex(address.toLowerCase());
        System.out.println("Email: " + address);
        System.out.println("License: " + license);
    }
}
