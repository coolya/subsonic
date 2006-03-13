<%--$Revision: 1.9 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.*,
                 net.sourceforge.subsonic.service.*,
                 net.sourceforge.subsonic.util.*,
                 org.apache.commons.fileupload.*,
                 org.apache.tools.zip.*,
                 javax.servlet.jsp.*,
                 java.io.*,
                 java.util.*"%>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="style.css" rel="stylesheet">
</head><body>

<%!
    private static final Logger LOG = Logger.getLogger("net.sourceforge.subsonic.jsp.upload");
    private InternationalizationService is = ServiceFactory.getInternationalizationService();
%>

<h2><%=is.get("upload.title")%></h2>

<%
    out.flush();

    try {

        // Check that we have a file upload request
        if (!FileUpload.isMultipartContent(request)) {
            throw new Exception("Illegal request.");
        }

        File dir = null;
        boolean unzip = false;

        DiskFileUpload upload = new DiskFileUpload();
        List items = upload.parseRequest(request);

        // First, look for "dir" and "unzip" parameters.
        Iterator iter = items.iterator();
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();

            if (item.isFormField() && "dir".equals(item.getFieldName())) {
                dir = new File(item.getString());
            } else if (item.isFormField() && "unzip".equals(item.getFieldName())) {
                unzip = true;
            }
        }

        if (dir == null) {
            throw new Exception("Missing 'dir' parameter.");
        }

        // Look for file items.
        int fileCount = 0;
        iter = items.iterator();
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();

            if (!item.isFormField()) {
                fileCount++;
                String fileName = item.getName();
                File targetFile = new File(dir, new File(fileName).getName());

                if (!ServiceFactory.getSecurityService().isUploadAllowed(targetFile)) {
                    throw new Exception("Permission denied: " + StringUtil.toHtml(targetFile.getPath()));
                }

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                item.write(targetFile);
                LOG.info("Uploaded " + targetFile);
                out.println("<p>" + is.get("upload.success", StringUtil.toHtml(targetFile.getPath())) + "</p>");

                if (unzip && targetFile.getName().toLowerCase().endsWith(".zip")) {
                    unzip(targetFile, out);
                }
            }
        }

        if (fileCount == 0) {
            out.println("<p>" + is.get("upload.empty") + "</p>");
        }

    } catch (Exception x) {
        LOG.warn("Uploading failed.", x);
        out.println("<p>" + is.get("upload.failed", x.getMessage()) + "</p>");
    }
%>

<p><a href="more.jsp">[<%=is.get("common.back")%>]</a></p>
</body></html>

<%!
    private void unzip(File file, JspWriter out) throws Exception {
        LOG.info("Unzipping " + file);

        ZipFile zipFile = new ZipFile(file);

        try {

            Enumeration entries = zipFile.getEntries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                File entryFile = new File(file.getParentFile(), entry.getName());

                if (!entry.isDirectory()) {

                    if (!ServiceFactory.getSecurityService().isUploadAllowed(entryFile)) {
                        throw new Exception("Permission denied: " + StringUtil.toHtml(entryFile.getPath()));
                    }

                    entryFile.getParentFile().mkdirs();
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    try {
                        inputStream = zipFile.getInputStream(entry);
                        outputStream = new FileOutputStream(entryFile);

                        byte[] buf = new byte[8192];
                        while (true) {
                            int n = inputStream.read(buf);
                            if (n == -1) {
                                break;
                            }
                            outputStream.write(buf, 0, n);
                        }

                        LOG.info("Unzipped " + entryFile);
                        out.println(is.get("upload.unzipped", StringUtil.toHtml(entryFile.getPath())) + "<br/>");
                    } finally {
                        try {inputStream.close();} catch (Exception x) {}
                        try {outputStream.close();} catch (Exception x) {}
                    }
                }
            }

            zipFile.close();
            file.delete();

        } finally {
            zipFile.close();
        }
    }
%>

