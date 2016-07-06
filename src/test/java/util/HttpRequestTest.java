package util;

import annotation.RequestMapping;
import org.junit.Test;
import webserver.HttpRequest;
import webserver.RequestHandler;
import webserver.WebServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

/**
 * Created by jojoldu@gmail.com on 2016-07-04.
 */
public class HttpRequestTest {
    private String testDir = "./src/test/resources/";

    @Test
    public void request_GET() throws Exception {
        InputStream in = new FileInputStream(new File(testDir + "Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);

        assertEquals("GET", request.getMethod());
        assertEquals("/user/create", request.getPath());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("javajigi", request.getParameter("userId"));
        assertEquals("password", request.getParameter("password"));
    }

    @Test
    public void test_RequestFactory() throws Exception {
        WebServer.createMethodFactory();
        assertEquals("create", WebServer.methodFactory.get("/user/create").getName());
    }

    @Test
    public void response_GET() throws Exception {
        InputStream in = new FileInputStream(new File(testDir + "Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);
    }
}
