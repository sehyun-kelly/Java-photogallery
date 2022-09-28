import java.net.*;
import java.io.*;
import java.util.*;

    
public class GetListing {
    private static String PARAM = "caption=doggie&date=2022-09-27";

    public static void main(String[] args) throws Exception{
        sendPOST();
        System.out.println("POST DONE");
    }

    private static void getFileInput(){
        Scanner scanner = new Scanner(System.in);

    }
    private static void sendPOST() throws IOException {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("https://comp3940-photogallery.herokuapp.com/upload");
            conn = (HttpURLConnection) url.openConnection();

            System.out.println("conn");
            conn.setRequestMethod("POST");
//            conn.connect();
//            System.out.println("connect");

            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(PARAM.getBytes());
            os.flush();
            os.close();
            System.out.println("close");


//            InputStream inputStream = conn.getInputStream();
//            System.out.println("inputstream");
//            StringBuffer buffer = new StringBuffer();
//            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//            System.out.println("buffer reader");
//            String response = "", line = "";
//            while ((line = br.readLine()) != null) {
//                response += line;
//            }
//            br.close();
//            conn.disconnect();
//            if (response != null && response.length() > 0) {
//                System.out.println(response);
//            }
            int responseCode = conn.getResponseCode();
            System.out.println("code: " + responseCode);
        } catch (Exception e) {
            System.err.println("Web Access:" + e.getMessage());
        }
    }
}