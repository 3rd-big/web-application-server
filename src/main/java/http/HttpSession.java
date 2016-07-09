package http;

import util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by jojoldu@gmail.com on 2016-07-09.
 */
public class HttpSession {
    private String id;
    private Map<String, Object> attributes = new HashMap<>();
    public static final String NAME="JSESSIONID";

    public void addCookieHeaderValue(HttpRequest request, HttpResponse response){
        String cookie = StringUtils.isNotEmpty(request.getHeader("Set-Cookie"))? request.getHeader("Set-Cookie"): "";
        response.addHeader("Set-Cookie", cookie+NAME+"="+this.generateId()+";");
    }


    public String getId(){
        if(StringUtils.isNotEmpty(this.id)){
            return this.id;
        }
        return this.id;
    }

    public String generateId(){
        return UUID.randomUUID().toString();
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getAttribute(String name){
        return this.getAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }
    
    public void removeAttribute(String name){
        this.attributes.remove(name);
    }

    public void invalidate(){
        this.attributes.clear();
    }
}
