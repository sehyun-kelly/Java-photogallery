import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Scanner;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.lang.Thread;

public class JavaWebSocketClient {

    public static void main(String[] args) throws Exception {
//        HttpPostMultipart multipart = new HttpPostMultipart("https://comp3940-photogallery.herokuapp.com/chat", "utf-8");
//        // Add form field
//        multipart.addFormField("username", "Guest");
//        String response = multipart.finish();
//        System.out.println(response);
        Scanner scanner = new Scanner(System.in);
        CountDownLatch latch = new CountDownLatch(1);
        WebSocket ws = HttpClient
                .newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create("ws://comp3940-photogallery.herokuapp.com/chat"), new WebSocketClient(latch))
                .join();
        System.out.println("Enter your chat. Quit or Q to quit.");
        String userchat = "Guest has joined the chat";
        while (!userchat.equalsIgnoreCase("quit") && !userchat.equalsIgnoreCase("q")) {
            ws.sendText(userchat, true);
            userchat = scanner.nextLine().trim();
        }
        latch.await();
        Thread.sleep(10000);
    }

    private static class WebSocketClient implements WebSocket.Listener {
        private final CountDownLatch latch;

        public WebSocketClient(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("Opening..." + webSocket.getSubprotocol());
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println(data);
            latch.countDown();
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.out.println("An error has occurred! " + webSocket.toString());
            WebSocket.Listener.super.onError(webSocket, error);
        }
    }
}