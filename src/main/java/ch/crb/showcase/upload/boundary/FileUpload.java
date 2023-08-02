package ch.crb.showcase.upload.boundary;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet(name = "FileUploadServlet", urlPatterns = {"/upload"})
@MultipartConfig
public class FileUpload extends HttpServlet {

    public static final String PATH = "/home/jboss/share";
    public static final String FILE_PART = "submitted";
    public static final String LOCATION_HEADER = "Location";
    public static final String CONTENT_HEADER = "content-disposition";
    public static final String CONTENT_SEPARATOR = ";";
    public static final String CONTENT_FILENAME = "filename";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "*");
        response.addHeader("Access-Control-Expose-Headers", "*");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.addHeader("Access-Control-Max-Age", "151200");
        response.addHeader("x-responded-by", "cors-response-filter");

        try {
            fetchDataFromDB(request, response);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final Part filePart = request.getPart(FILE_PART);
        final String oldFileName = getFileName(filePart);
        int startOfExtension = oldFileName.indexOf('.');
        String filename = UUID.randomUUID().toString() + oldFileName.substring(startOfExtension);
        response.addHeader(LOCATION_HEADER, "/share/" + filename);

        writeFile(response.getWriter(), filename, filePart.getInputStream());
    }

    private static String getFilename(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim()
                        .replace("\"", "");
            }
        }
        return null;
    }

    private static String getValue(Part part) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                part.getInputStream(), "UTF-8"));
        StringBuilder value = new StringBuilder();
        char[] buffer = new char[1024];
        for (int length = 0; (length = reader.read(buffer)) > 0;) {
            value.append(buffer, 0, length);
        }
        return value.toString();
    }

    private void fetchDataFromDB(HttpServletRequest request,
                                 HttpServletResponse response) throws InterruptedException,
            IllegalStateException, ServletException {
        try {
            System.out.println("Test 1");
            for (Part part : request.getParts()) {
                String filename = getFilename(part);
                System.out.println("Test 2");
                if (filename == null) {
                    System.out.println("Test 3");
                    // Process regular form field (input
                    // type="text|radio|checkbox|etc", select, etc).
                    String fieldname = part.getName();
                    System.out.println(fieldname);
                    String fieldvalue = getValue(part);
                    System.out.println(fieldvalue);
                    // ... (do your job here)
                } else if (!filename.isEmpty()) {
                    System.out.println("Test 4");
                    // Process form file field (input type="file").
                    String fieldname = part.getName();
                    System.out.println(fieldname);
                    filename = filename
                            .substring(filename.lastIndexOf('/') + 1)
                            .substring(filename.lastIndexOf('\\') + 1); // MSIE
                    // fix.
                    InputStream filecontent = part.getInputStream();
                    System.out.println(filecontent);
                    String[] command = new String[] {
                            "C:/Program Files (x86)/MySQL/MySQL Server "
                                    + "5.5/bin/mysql",
                            "--user=root", "--password=root", "dummy", "-e",
                            "source "+"C:/Users/John/Desktop/LMS/backup.sql" };
                    Process restore = Runtime.getRuntime().exec(command);
                    int check = restore.waitFor();
                    System.out.println(check);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setAccessControlHeaders(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "*");
        response.addHeader("Access-Control-Expose-Headers", "*");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.addHeader("Access-Control-Max-Age", "151200");
        response.addHeader("x-responded-by", "cors-response-filter");
    }

    private void writeFile(final PrintWriter writer, String fileName, final InputStream filecontent) throws IOException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(new File(PATH + File.separator + fileName));
            int read;
            final byte[] bytes = new byte[1024];
            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
        } catch (IOException e){
            throw new RuntimeException(e); //NOSONAR
        } finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }

    private String getFileName(final Part part) {
        System.out.println("part = " + part);
        for (String content : part.getHeader(CONTENT_HEADER).split(CONTENT_SEPARATOR)) {
            if (content.trim().startsWith(CONTENT_FILENAME)) {
                int startOfFilename = content.indexOf('=') + 1;
                return content
                        .substring(startOfFilename)
                        .trim()
                        .replace("\"", "");
            }
        }
        throw new IllegalArgumentException("no filename found");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public String getServletInfo() {
        return "Servlet that uploads files";
    }
}

