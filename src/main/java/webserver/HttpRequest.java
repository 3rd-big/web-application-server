package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by jojoldu@gmail.com on 2016-07-04.
 */
public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private int contentLength;
    private boolean logined;
    private String method;
    private String path;
    private String url;
    private Map<String, String> headers = new LinkedHashMap<>();
    private Map<String, String> parameters = new LinkedHashMap<>();
    private String body;

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public boolean isLogined() {
        return logined;
    }

    public void setLogined(boolean logined) {
        this.logined = logined;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name){
        return this.headers.get(name);
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(String name){
        return this.parameters.get(name);
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            if (line == null) {
                return;
            }

            log.debug("request line : {}", line);
            String[] tokens = line.split(" ");
            this.method = tokens[0];
            contentLength = 0;
            logined = false;

            while (!line.equals("")) {
                line = br.readLine();
                if(line == null){
                    break;
                }
                log.debug("header : {}", line);
                HttpRequestUtils.Pair p = HttpRequestUtils.parseHeader(line);
                headers.put(p.getKey(), p.getValue());

                if (line.contains("Content-Length")) {
                    contentLength = getContentLength(line);
                }

                if (line.contains("Cookie")) {
                    logined = isLogin(line);
                }
            }
            this.url = getDefaultUrl(tokens);
            String[] urlParse = url.split("\\?");
            this.path = urlParse[0];
            this.parameters = HttpRequestUtils.parseQueryString(urlParse[1]);
            this.body = IOUtils.readData(br, contentLength);

        }catch (Exception e){
            log.error("HttpRequest error");
        }
    }

    private boolean isLogin(String line) {
        String[] headerTokens = line.split(":");
        Map<String, String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }
    private int getContentLength(String line) {
        String[] headerTokens = line.split(":");
        return Integer.parseInt(headerTokens[1].trim());
    }

    private String getDefaultUrl(String[] tokens) {
        String url = tokens[1];
        if (url.equals("/")) {
            url = "/index.html";
        }
        return url;
    }

}
