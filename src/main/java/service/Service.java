package service;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import webserver.HttpRequest;
import webserver.RequestHandler;

import java.util.Collection;
import java.util.Map;

/**
 * Created by jojoldu@gmail.com on 2016-07-06.
 */
public class Service {
    private final Logger log = LoggerFactory.getLogger(Service.class);

    public User getUser(HttpRequest request) {
        return DataBase.findUserById(request.getParameter("userId"));
    }

    public byte[] getUserList() {
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

    public void create(HttpRequest request) {
        String body = request.getBody();
        Map<String, String> params = HttpRequestUtils.parseQueryString(body);
        User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
        log.debug("user : {}", user);
        DataBase.addUser(user);
    }

}
