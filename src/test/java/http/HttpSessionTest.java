package http;

import db.HttpSessions;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertThat;

/**
 * Created by jojoldu@gmail.com on 2016-07-09.
 */
public class HttpSessionTest {
    private String testDirectory = "./src/test/resources/";
    private HttpSession httpSession;

    @Before
    public void setUp() throws Exception {
        httpSession = new HttpSession();
    }

    @Test
    public void test_uuid() throws Exception {
        System.out.println(httpSession.getId());
    }

    @Test
    public void test_cookie() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
        HttpRequest request = new HttpRequest(in);
        HttpResponse response = new HttpResponse(System.out);
        httpSession.addCookieHeaderValue(request, response);
        System.out.println(response.getHeader("Set-Cookie"));
    }

    @Test
    public void test_cookie2() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);
        HttpResponse response = new HttpResponse(System.out);
        httpSession.addCookieHeaderValue(request, response);
        System.out.println(response.getHeader("Set-Cookie"));
    }

    @Test
    public void test_sessions() throws Exception {
        HttpSessions.put(httpSession);
        Stream.of(HttpSessions.getAll()).forEach(System.out::println);
    }

}