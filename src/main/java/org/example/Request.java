package org.example;

//TODO
/* Create headers strings key/value pair, using an Hashmap.
Query the entered header string
create an input reader to take in the string to be queried
Add the full Url gotten from the inputStreamReader and add it to path;
Create a response method that send back the header, the body, using the getResponseBody outPutStream.
create an handler that implement runnable that handles the request -response from the client/server
create an HttpServer class that has a running thread for each request and response by starting the handler interface.

*
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class Request {
    private final Map<String, Object> headers = new HashMap<>();
    private final Map<String, Object> queryParameters = new HashMap<>();
    private BufferedReader in = null;
    private String method;
    private String fullUrl;
    private String path;

    Request(BufferedReader in) {
        this.in = in;
    }
    public Map<String, Object> getHeaders() {
        return headers;
    }

    public Map<String, Object> getQueryParameters(String getParam) {
        return (Map<String, Object>) queryParameters.get(getParam);
    }

    public String getIn() throws IOException {
        return in.readLine();
    }

    public String getMethod() {
        return method;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public String getPath() {
        return path;
    }

    public void parseQueryParameters(String queryString) { /*get the raw query string of the URL path*/
        if (queryString != null) {
            String[] parameters = queryString.split("&");
            for (String parameter : parameters) {
                int separator = parameter.indexOf(":");
                if (separator > -1) {
                    queryParameters.put(parameter.substring(0, separator), parameter.substring(separator + 1));
                } else {
                    queryParameters.put(parameter, null);
                }
            }
        } else {
            System.out.println("Can't query string, check The Object type!");
        }

    }

    public boolean parse() throws IOException {
        String inputLine = in.readLine();
        System.out.println(inputLine);
        StringTokenizer tokenizer = new StringTokenizer(inputLine);
        String[] component = new String[3];
        for (int i = 0; i < component.length; i++) {
            if (tokenizer.hasMoreTokens()) {
                component[i] = tokenizer.nextToken();
            } else {
                return false;
            }
        }
        method = component[0];
        fullUrl = component[1]; // headers.

        // consume Header-lines.
        while (true) {
            String headerLines = in.readLine();
            System.out.println(headerLines);
            if (headerLines.isEmpty()) {
                break;
            }
            int separator = headerLines.indexOf(":");
            if (separator == -1) {
                return false;
            } else {
                headers.put(headerLines.substring(0, separator), headerLines.substring(separator + 1));
            }
        }

        if (!component[1].contains("?")) { // starting index of the full path
            path = component[1];
        } else {
            path = component[1].substring(0, component[1].indexOf("?"));
            parseQueryParameters(component[1].substring(component[1].indexOf("?") + 1));
        }

        if (Objects.equals("/", path)) {
            path = "/index.html";
        }
        return true;
    }

    @Override
    public String toString() {
        return "Request{" +
                "headers=" + headers +
                ", method='" + method + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
