package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;

public class HttpServer {
    private static int port;
    private Handler defaultHandler = null;

    /* two level mapping of this map handlers.*/
    private Map<String, Map<String, Handler>> handlers = new HashMap<>();

    HttpServer(int port) {
        this.port = port;

    }
    public void addHandler(String method, String path, Handler handler) {
        Map<String,  Handler> methodHandlers = handlers.get(method);
        if (methodHandlers == null) {
            methodHandlers = new HashMap<>();
            handlers.put(method, methodHandlers);
        }
        methodHandlers.put(path, handler);
    }

    public void start() throws IOException {
        ServerSocket socket = new ServerSocket(port);
        System.out.println("listening at port "+ port);
        Socket client;
        while ((client = socket.accept()) != null) {
            System.out.println("Receiving connection from  "+ client.getRemoteSocketAddress().toString());
            SocketHandler socketHandler = new SocketHandler(client, handlers);
            Thread thread = new Thread(socketHandler);
            thread.start();
        }

    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(8080);
        server.addHandler("GET", "/hello", new Handler() {
            @Override
            public void handle(Request request, Response response) {
            String html = "requested,  It worked  "+ request.getParameter("name");
            response.setResponseCode(200, "ok");
            response.addHeader("content-type", "text/html");
            response.addBody(html);
            }

        });
//        server.addHandler("GET", "/*", (Handler) new FileHandler());
        server.start();

    }
}