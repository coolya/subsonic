package net.sourceforge.subsonic.ajax;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import uk.ltd.getahead.dwr.*;

/**
 * Provides AJAX-enabled services for retrieving the status of ongoing transfers.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 * @version $Revision: 1.1 $ $Date: 2006/02/26 21:46:28 $
 */
public class TransferService {

    /**
     * Returns info about ongoing uploads from the player associated with the HTTP request.
     * @return Info about ongoing uploads.
     */
    public UploadInfo getUploadInfo() {
        WebContext webContext = WebContextFactory.get();
        Player player = ServiceFactory.getPlayerService().getPlayer(webContext.getHttpServletRequest(), webContext.getHttpServletResponse());
        TransferStatus[] statuses = ServiceFactory.getStatusService().getUploadStatusesForPlayer(player);

        long bytesUploaded = 0L;
        long bytesTotal = 0L;
        for (TransferStatus status : statuses) {
            bytesUploaded += status.getBytesTransfered();
            bytesTotal += status.getBytesTotal();
        }

        return new UploadInfo(bytesUploaded, bytesTotal);
    }

}
