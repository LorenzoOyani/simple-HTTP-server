package org.example;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private final OutputStream out;
    private String statusMessage;
    private int statusCode;
    private final Map<String, String> headers = new HashMap<>();
    private String body;

    Response(OutputStream out) {
        this.out = out;
    }

    public void setResponseCode(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public void addHeader(String headerName, String headerValue) {
        this.headers.put(headerName, headerValue);
    }

    public void addBody(String body) {
        headers.put("Content-length", String.valueOf(body.length()));
        this.body = body;
    }

    public void send() throws IOException {
        headers.put("connection", "close");
        out.write(("HTTP/1.1" + " " +  statusCode+ " "+ statusMessage + "\r\n").getBytes());
        for (String headerName : headers.keySet()) {
            out.write((headerName +":" + headers.get(headerName) + "\r\n").getBytes());

        }
        out.write("\r\n".getBytes());
        assert body !=null;
        out.write(body.getBytes());


    }
}
