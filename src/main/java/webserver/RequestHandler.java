package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import db.DataBase;

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

            String url = request.getPath();

            if ("/user/create".equals(url)) {
                create(request);
                response.get302Header();
            } else if ("/user/login".equals(url)) {
                User user = getUser(request);
                if (user != null && user.login(request.getParameter("password"))) {
                    response.get302LoginSuccessHeader();
                } else {
                    response.getResource("/user/login_failed.html");
                }
            } else if ("/user/list".equals(url)) {
                if (!request.isLogined()) {
                    response.getResource("/user/login.html");
                    return;
                }

                byte[] body = getUserList();
                response.get200Header(body.length, "html");
                response.getBody(body);
            } else {
                response.getResource(url);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private User getUser(HttpRequest request) {
        return DataBase.findUserById(request.getParameter("userId"));
    }

    private byte[] getUserList() {
        Collection<User> users = DataBase.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (User user : users) {
            sb
                    .append("<tr>")
                    .append("<td>" + user.getUserId() + "</td>")
                    .append("<td>" + user.getName() + "</td>")
                    .append("<td>" + user.getEmail() + "</td>")
                    .append("</tr>");
        }
        sb.append("</table>");
        return sb.toString().getBytes();
    }

    private void create(HttpRequest request) {
        String body = request.getBody();
        Map<String, String> params = HttpRequestUtils.parseQueryString(body);
        User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
        log.debug("user : {}", user);
        DataBase.addUser(user);
    }


}
