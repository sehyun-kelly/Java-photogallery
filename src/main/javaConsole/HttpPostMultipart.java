import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.time.format.*;

public class HttpPostMultipart {
    private final String boundary;
    private static final String LINE = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @param charset
     * @param headers
     * @throws IOException
     */
    public HttpPostMultipart(String requestURL, String charset) throws IOException {
        this.charset = charset;
        boundary = UUID.randomUUID().toString();
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);    // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) throws IOException {
        writer.append("--" + boundary).append(LINE);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE);
        writer.append("Content-Type: text/plain; charset=" + charset).append(LINE);
        writer.append(LINE);
        writer.append(value).append(LINE);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName
     * @param uploadFile
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE);
        writer.append("Content-Transfer-Encoding: binary").append(LINE);
        writer.append(LINE);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[16777216];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        writer.append(LINE);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return String as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish() throws IOException {
        String response = "Files you have uploaded: \n";
        writer.flush();
        writer.append("--" + boundary + "--").append(LINE);
        writer.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = httpConn.getInputStream().read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            response += result.toString(this.charset);

            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
        return response;
    }

    private String getFileName(Scanner scanner){
        System.out.println("Please enter the file name of the image(png only) include the path: ");
        System.out.println("Ex.) /Users/Desktop/hello.png");
        String fileName = scanner.nextLine();

        while(fileName.charAt(0) != '/' || !fileName.contains(".png")){
            if(!fileName.contains(".png")) System.out.println("No png file entered! Please enter the path again:");
            else System.out.println("Wrong path! Please enter the path again: ");
            fileName = scanner.nextLine();
        }

        return fileName;
    }

    private String getCaption(Scanner scanner){
        System.out.println("\nPlease enter the caption for your image: ");
        String caption = scanner.nextLine();

        return caption;
    }

    private String getDate(Scanner scanner){
        final int DATE_LENGTH = 10;
        System.out.println("\nPlease enter the date: ");
        System.out.println("Ex) 2022-09-24 ");
        String date = scanner.nextLine();

        while(!isDateValid(date)){
            System.out.println("Wrong date format! Please enter the date in ####-##-## format: ");
            date = scanner.nextLine();
        }

        return date;
    }
    private static boolean isDateValid(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.US)
                .withResolverStyle(ResolverStyle.STRICT);

        try{
            formatter.parse(date);
        }catch (DateTimeParseException e){
            return false;
        }
        return true;
    }

    private HashMap<String, String> getParams(){
        HashMap<String, String> postParams = new HashMap<>();

        Scanner scanner = new Scanner(System.in);
        String fileName=getFileName(scanner);
        String caption=getCaption(scanner);
        String date=getDate(scanner);

        postParams.put("fileName", fileName);
        postParams.put("caption", caption);
        postParams.put("date", date);

        return postParams;
    }

    public static void main(String[] args) {
        try {
            HttpPostMultipart multipart = new HttpPostMultipart("https://comp3940-photogallery.herokuapp.com/consoleUpload", "utf-8");
            HashMap<String, String> params = multipart.getParams();

            // Add form field
            multipart.addFormField("caption", params.get("caption"));
            multipart.addFormField("date", params.get("date"));
            // Add file
            File file = new File(params.get("fileName"));
            multipart.addFilePart("fileName", file);
            // Print result
            String response = multipart.finish();
            System.out.println("\n" + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}