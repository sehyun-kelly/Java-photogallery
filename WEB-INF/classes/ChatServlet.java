import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint(value = "/chat")
public class ChatServlet extends HttpServlet {
    private static String currentUser;
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<ChatServlet> connections =
            new CopyOnWriteArraySet<>();

    private Session session;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = isLoggedIn(request);
        if (!isLoggedIn) {
            response.setStatus(302);
            response.sendRedirect("login");
        }

        currentUser = session.getAttribute("USER_ID").toString();
        String loginMsg = "Logged in as: " + session.getAttribute("USER_ID");
        String html = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">\n" +
                "<head>\n" +
                "    <title>User Chat</title>\n" +
                "    <style type=\"text/css\">\n" +
                "        input#chat {\n" +
                "        width: 410px\n" +
                "    }\n" +
                "\n" +
                "    #console-container {\n" +
                "        width: 400px;\n" +
                "    }\n" +
                "\n" +
                "    #console {\n" +
                "        border: 1px solid #CCCCCC;\n" +
                "        border-right-color: #999999;\n" +
                "        border-bottom-color: #999999;\n" +
                "        height: 170px;\n" +
                "        overflow-y: scroll;\n" +
                "        padding: 5px;\n" +
                "        width: 100%;\n" +
                "    }\n" +
                "\n" +
                "    #console p {\n" +
                "        padding: 0;\n" +
                "        margin: 0;\n" +
                "    }\n" +
                "    ></style>\n" +
                "    <script type=\"application/javascript\">\n" +
                "    \"use strict\";\n" +
                "\n" +
                "    var Chat = {};\n" +
                "\n" +
                "    Chat.socket = null;\n" +
                "\n" +
                "    Chat.connect = (function(host) {\n" +
                "        if ('WebSocket' in window) {\n" +
                "            Chat.socket = new WebSocket(host);\n" +
                "        } else if ('MozWebSocket' in window) {\n" +
                "            Chat.socket = new MozWebSocket(host);\n" +
                "        } else {\n" +
                "            Console.log('Error: WebSocket is not supported by this browser.');\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        Chat.socket.onopen = function () {\n" +
                "            Console.log('Info: WebSocket connection opened.');\n" +
                "            document.getElementById('chat').onkeydown = function(event) {\n" +
                "                if (event.keyCode == 13) {\n" +
                "                    Chat.sendMessage();\n" +
                "                }\n" +
                "            };\n" +
                "        };\n" +
                "\n" +
                "        Chat.socket.onclose = function () {\n" +
                "            document.getElementById('chat').onkeydown = null;\n" +
                "            Console.log('Info: WebSocket closed.');\n" +
                "        };\n" +
                "\n" +
                "        Chat.socket.onmessage = function (message) {\n" +
                "            Console.log(message.data);\n" +
                "        };\n" +
                "    });\n" +
                "\n" +
                "    Chat.initialize = function() {\n" +
                "        if (window.location.protocol == 'http:') {\n" +
                "            Chat.connect('ws://' + window.location.host + '/photogallery/chat');\n" +
                "        } else {\n" +
                "            Chat.connect('wss://' + window.location.host + '/photogallery/chat');\n" +
                "        }\n" +
                "    };\n" +
                "\n" +
                "    Chat.sendMessage = (function() {\n" +
                "        var message = document.getElementById('chat').value;\n" +
                "        if (message != '') {\n" +
                "            Chat.socket.send(message);\n" +
                "            document.getElementById('chat').value = '';\n" +
                "        }\n" +
                "    });\n" +
                "\n" +
                "    var Console = {};\n" +
                "\n" +
                "    Console.log = (function(message) {\n" +
                "        var console = document.getElementById('console');\n" +
                "        var p = document.createElement('p');\n" +
                "        p.style.wordWrap = 'break-word';\n" +
                "        p.innerHTML = message;\n" +
                "        console.appendChild(p);\n" +
                "        while (console.childNodes.length > 25) {\n" +
                "            console.removeChild(console.firstChild);\n" +
                "        }\n" +
                "        console.scrollTop = console.scrollHeight;\n" +
                "    });\n" +
                "\n" +
                "    Chat.initialize();\n" +
                "\n" +
                "\n" +
                "    document.addEventListener(\"DOMContentLoaded\", function() {\n" +
                "        // Remove elements with \"noscript\" class - <noscript> is not allowed in XHTML\n" +
                "        var noscripts = document.getElementsByClassName(\"noscript\");\n" +
                "        for (var i = 0; i < noscripts.length; i++) {\n" +
                "            noscripts[i].parentNode.removeChild(noscripts[i]);\n" +
                "        }\n" +
                "    }, false);\n" +
                "\n" +
                "    </script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"noscript\"><h2 style=\"color: #ff0000\">Seems your browser doesn't support JavaScript! Websockets rely on JavaScript being enabled. Please enable\n" +
                "    JavaScript and reload this page!</h2></div>\n" +
                "<div>\n" +
                "<div style=\"text-align: right;\">\n" +
                loginMsg +
                "\n</div>\n" +
                "    <p>\n" +
                "        <input type=\"text\" placeholder=\"type and press enter to chat\" id=\"chat\" />\n" +
                "    </p>\n" +
                "    <div id=\"console-container\">\n" +
                "        <div id=\"console\"/>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
        PrintWriter out = response.getWriter();
        out.println(html);
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);

        return session != null && req.isRequestedSessionIdValid();
    }

    @OnOpen
    public void start(Session session) {
        this.session = session;
        connections.add(this);
        String message = String.format("* %s %s", currentUser, "has joined.");
        broadcast(message);
    }


    @OnClose
    public void end() {
        connections.remove(this);
        String message = String.format("* %s %s", currentUser, "has disconnected.");
        broadcast(message);
    }


    @OnMessage
    public void incoming(String message) {
        // Never trust the client
        String filteredMessage = String.format("%s: %s", currentUser, message);
        broadcast(filteredMessage);
    }




    @OnError
    public void onError(Throwable t) throws Throwable {
        System.out.println("Chat Error: " + t.toString());
    }


    private static void broadcast(String msg) {
        for (ChatServlet client : connections) {
            try {
                synchronized (client) {
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                connections.remove(client);
                try {
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = String.format("* %s %s", client.currentUser, "has been disconnected.");
                broadcast(message);
            }
        }
    }

}
