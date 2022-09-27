import java.net.*;
import java.io.*;
import java.util.*;

    
public class GetListing {
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
            conn.setRequestMethod("GET");
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            StringBuffer buffer = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String response = "", line = "";
            while ((line = br.readLine()) != null) {
                response += line;
            }
            br.close();
            conn.disconnect();
            if (response != null && response.length() > 0) {
                System.out.println(response);
            }
        } catch (Exception e) {
            System.err.println("Web Access:" + e.getMessage());
        }
    }
}