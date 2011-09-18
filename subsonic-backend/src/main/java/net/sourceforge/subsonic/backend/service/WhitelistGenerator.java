package net.sourceforge.subsonic.backend.service;

import net.sourceforge.subsonic.backend.dao.PaymentDao;
import org.apache.log4j.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import java.util.Date;

/**
 * Creates a license whitelist.
 *
 * @author Sindre Mehus
 * @version $Id$
 */
public class WhitelistGenerator {

    private static final Logger LOG = Logger.getLogger(WhitelistGenerator.class);

    private PaymentDao paymentDao;

    public void generate(Date newerThan) throws Exception {
        LOG.info("Starting whitelist update for emails newer than " + newerThan);

        EmailSession session = new EmailSession();
        Folder folder = session.getFolder("[Gmail]/Sent Mail");
        int n = folder.getMessageCount();

        for (int i = n; i >= 0; i--) {
            Message message = folder.getMessage(i);
            Date date = message.getSentDate();
            String recipient = message.getRecipients(Message.RecipientType.TO)[0].toString();
            if (date.before(newerThan)) {
                break;
            }
            LOG.info(date + " " + recipient);

            if (paymentDao.getPaymentByEmail(recipient) == null && !paymentDao.isWhitelisted(recipient)) {
                paymentDao.whitelist(recipient);
                LOG.info("WHITELISTED " + recipient);
            }
        }
        folder.close(false);
        LOG.info("Completed whitelist update.");
    }

    public void setPaymentDao(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }
}
