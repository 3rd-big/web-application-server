package controller;

import annotation.RequestMapping;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.WebServer;

import java.io.IOException;

/**
 * Created by jojoldu@gmail.com on 2016-07-06.
 */
public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    @RequestMapping("/")
    public void index(HttpRequest request, HttpResponse response){
        try {
            response.getResource(request.getUrl());
        } catch (IOException e) {
            log.error("/ : ", e);
        }
    }

    @RequestMapping("/user/create")
    public void create(HttpRequest request, HttpResponse response){
        WebServer.service.create(request);
        response.get302Header();
    }

    @RequestMapping("/user/login")
    public void login(HttpRequest request, HttpResponse response){
        User user = WebServer.service.getUser(request);
        if (user != null && user.login(request.getBodyValue("password"))) {
            response.get302LoginSuccessHeader();
        } else {
            try {
                response.getResource("/user/login_failed.html");
            } catch (IOException e) {
                log.error("/user/login : ", e);
            }
        }
    }

    @RequestMapping("/user/list")
    public void list(HttpRequest request, HttpResponse response){
        if (!request.isLogined()) {
            try {
                response.getResource("/user/login.html");
            } catch (IOException e) {
                log.error("/user/list : ", e);
            }
            return;
        }

        byte[] body = WebServer.service.getUserList();
        response.get200Header(body.length, "html");
        response.getBody(body);
    }
}
