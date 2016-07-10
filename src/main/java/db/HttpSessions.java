package db;

import http.HttpSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jojoldu@gmail.com on 2016-07-10.
 */
public class HttpSessions {
    private static Map<String, HttpSession> sessions = new HashMap<>();

    public static HttpSession get(String key){
        return sessions.get(key);
    }

    public static Map<String, HttpSession> getAll(){
        return sessions;
    }
    public static void put(HttpSession httpSession){
        sessions.put(httpSession.getId(), httpSession);
    }
}
