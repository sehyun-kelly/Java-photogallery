import com.mysql.cj.xdevapi.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LocalReader {
    private String url;
    private String user;
    private String password;

    public void readFile(String path) {
        JsonArray jsonArray = new JsonArray();
        try {
            jsonArray = JsonParser.parseArray(new StringReader(Files.readString(Paths.get(path))));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        url = String.valueOf(jsonArray.get(0));
        user = String.valueOf(jsonArray.get(1));
        password = String.valueOf(jsonArray.get(2));
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
