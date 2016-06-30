package pojo;

import java.util.Map;

/**
 * Created by jojoldu@gmail.com on 2016-06-30.
 */
public class UrlData {
    private String model;
    private String method;
    private Map<String, String> params;

    public UrlData() {
    }

    public UrlData(String model, String method, Map<String, String> params) {
        this.model = model;
        this.method = method;
        this.params = params;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
