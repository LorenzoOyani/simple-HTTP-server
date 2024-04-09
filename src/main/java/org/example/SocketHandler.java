package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class SocketHandler implements Runnable {
    private final Socket socket;
    private Handler defaultHandlers;
    private final Map<String, Map<String, Handler>> handlers;

    SocketHandler(Socket socket, Map<String, Map<String, Handler>> handlers) {
        this.socket = socket;
        this.handlers = handlers;

    }

    public void log(String message) {
        System.out.println(message);
    }

    public void respond(int statusCode, String statusMessage, OutputStream out) throws IOException {
        String responseLine = "HTTP/1.1" + " " + statusCode + " " + statusMessage + "\r\n\r\n";
        log(responseLine);
        out.write(responseLine.getBytes());
    }


    @Override
    public void run() {
        BufferedReader in = null;
        OutputStream out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = socket.getOutputStream();

            Request request = new Request(in);
            if (!request.parse()) {
                respond(500, "unable to parse request", out);
                return;
            }
            boolean foundHandler = false;
            Response response = new Response(out);
            Map<String, Handler> methodHandlers = handlers.get(request.getMethod());
            if (methodHandlers == null) {
                respond(405, "method not supported", out);
                return;
            }
            for (String handlerPath : methodHandlers.keySet()) {
                if (handlerPath.equals(request.getPath())) {
                    methodHandlers.get(request.getPath()).handle(request, response);
                    response.send();
                    foundHandler = true;
                    break;
                }
            }
            if (!foundHandler) {
                if (methodHandlers.get("/*") != null) { /*Non-null map values to keys*/
                    methodHandlers.get("/*").handle(request, response);
                    response.send();

                } else {
                    respond(404, "not found", out);
                }

            }


        } catch (IOException e) {
            try {
                e.printStackTrace();
                if (socket != null) {
                    assert out != null;
                    respond(500, e.toString(), out);
                }

            } catch (IOException e1) {
                e1.printStackTrace();

            } finally {
                try {
                    if (socket != null) socket.close();
                    if (in != null) in.close();
                    if (out != null) out.close();

                } catch (IOException e2) {
                    e.printStackTrace();
                }
            }
        }

    }


}
