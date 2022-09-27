import java.net.*;
import java.io.*;
import java.util.*;

    
public class GetListing {
    private static String PARAM = "fileName=dog.PNG&caption=doggie&date=2022-09-27";

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
            System.out.println("connect");

            conn.setDoOutput(true);
            System.out.println("setdooutput");
            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
            os.write(PARAM);
            System.out.println("os write");
            os.flush();
            System.out.println("os flush");
//            os.close();
            System.out.println("os close");

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
        } catch (Exception e) {
            System.err.println("Web Access:" + e.getMessage());
        }
    }
}