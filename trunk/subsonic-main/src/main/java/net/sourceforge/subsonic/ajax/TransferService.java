package net.sourceforge.subsonic.ajax;

import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.controller.*;
import uk.ltd.getahead.dwr.*;

import javax.servlet.http.*;

/**
 * Provides AJAX-enabled services for retrieving the status of ongoing transfers.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class TransferService {

    /**
     * Returns info about any ongoing upload within the current session.
     * @return Info about ongoing upload.
     */
    public UploadInfo getUploadInfo() {

        HttpSession session = WebContextFactory.get().getSession();
        TransferStatus status = (TransferStatus) session.getAttribute(UploadController.UPLOAD_STATUS);

        if (status != null) {
            return new UploadInfo(status.getBytesTransfered(), status.getBytesTotal());
        }
        return new UploadInfo(0L, 0L);
    }
}
