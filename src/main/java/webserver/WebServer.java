package webserver;

import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import annotation.RequestMapping;
import controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.Service;

public class WebServer {
	private static final Logger log = LoggerFactory.getLogger(WebServer.class);
	private static final int DEFAULT_PORT = 8080;

    public static Map<String, Method> methodFactory = new HashMap<>();
    public static Controller controller = new Controller();
    public static Service service = new Service();

    public static void main(String args[]) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT; 
        } else {
            port = Integer.parseInt(args[0]);
        }
        createMethodFactory();
        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
    	
    	try (ServerSocket listenSocket = new ServerSocket(port)) {
    		log.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
            	RequestHandler requestHandler = new RequestHandler(connection);
                requestHandler.start();
            }
    	}
    }

    public static void createMethodFactory(){
        Method methods[] = WebServer.controller.getClass().getDeclaredMethods();

        for(Method m : methods){
            RequestMapping requestMapping = m.getAnnotation(RequestMapping.class);
            methodFactory.put(requestMapping.value(), m);
        }
    }

}
