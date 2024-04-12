package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

/* get the series of bytes from both the content-type and together with it's chunked sizes */
public class HttpInputStream extends InputStream {
    private Reader source;
    private int byteRemaining;
    private boolean chunked = true;


    HttpInputStream(Reader source, Map<String, String> headers) {
        this.source = source;
        String declaredContentType = headers.get("Content-type");
        if (declaredContentType != null) {
            try {
            byteRemaining = Integer.parseInt(declaredContentType);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if ("Ckunked".equals(headers.get("Transfer Enconding"))) {
            chunked =true;
            byteRemaining = getChunkedPackets();

        }

    }

    public int getChunkedPackets()  {
        int b;
        int chunkSize = 0;
        /*
        * Character values are compared using the nor-or null;
        * */
        try {
            while ((b = source.read()) != '\r') {
                chunkSize = (chunkSize << 4);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return chunkSize;

    }


    @Override
    public int read() throws IOException {
        if (byteRemaining == 0) {
            return -1;
        }else{
            byteRemaining -=1;
            return source.read();

        }
    }
}
