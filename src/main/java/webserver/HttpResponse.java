package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * Created by jojoldu@gmail.com on 2016-07-04.
 */
public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        dos = new DataOutputStream(out);

    }
    public void getResource(String url) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        String contentType="html";

        if(url.endsWith(".css")){
            contentType = "css";
        }
        if(url.endsWith(".js")){
            contentType = "javascript";
        }

        get200Header(body.length, contentType);
        getBody(body);
    }

    public void get200Header(int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/"+contentType+";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void set302Header() throws IOException {
        dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
        dos.writeBytes("Location: /index.html \r\n");
        dos.writeBytes("\r\n");
    }

    public void get302Header() {
        try {
            set302Header();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void get302LoginSuccessHeader() {
        try {
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            set302Header();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    public void getBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
