package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private static int port;

    private final Map<String, Map<String, Handler>> handlers = new HashMap<>();

    HttpServer(int port) {
        HttpServer.port = port;

    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(8080);
        server.addHandler("GET", "/hello", new Handler() {
            @Override
            public void handle(Request request, Response response) {
                String html = "requested,  It worked  " + request.getParameter("name");
                response.setResponseCode(200, "ok");
                response.addHeader("content-type", "text/html");
                response.addBody(html);
            }

        });

        // Handling Http Post requests.
        server.addHandler("/POST", "login", new Handler() {
            @Override
            public void handle(Request request, Response response) {
                InputStream in = request.getBody();
                StringBuilder buf = new StringBuilder();
                int c = 0;
                try {
                    while ((c = in.read()) != -1) {
                        buf.append((char) c);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String html = getString(buf);
                response.setResponseCode(200, "ok");
                response.addHeader("Content-type", "text/html");
                response.addBody(html);


            }

            private static String getString(StringBuilder buffers) {
              String[] components = buffers.toString().split("&");
              Map<String, String> hashmap = new HashMap<>();
                for (String primes : components) {
                    String[] prime = primes.split("=");
                    hashmap.put(prime[0], prime[1]);
                }
                return STR."<Body>Welcome, \{hashmap.get("username")}</Body>";
            }
        });
        server.start();

    }

    public void addHandler(String method, String path, Handler handler) {
        Map<String, Handler> methodHandlers = handlers.get(method);
        if (methodHandlers == null) {
            methodHandlers = new HashMap<>();
            handlers.put(method, methodHandlers);
        }
        methodHandlers.put(path, handler);
    }

    public void start() throws IOException {
        try (ServerSocket socket = new ServerSocket(port)) {

            System.out.println(STR."listening at port \{port}");
            Socket client;
            while ((client = socket.accept()) != null) {
                System.out.println(STR."Receiving connection from  \{client.getRemoteSocketAddress().toString()}");
                SocketHandler socketHandler = new SocketHandler(client, handlers);
                Thread thread = new Thread(socketHandler);
                thread.start();
            }
        }


    }
}