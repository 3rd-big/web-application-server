package webserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            String url = request.getUrl();
            Method invokeMethod = WebServer.methodFactory.get(request.getPath());
            if(invokeMethod == null){
                response.getResource(url);
            }else{
                invokeMethod.invoke(WebServer.controller, request, response);
                response.getResource(url);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
