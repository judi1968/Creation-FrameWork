package jframework.tools;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    String view;
    HashMap<String, Object> data = new HashMap<>();
    String messageApi;
     
    public void setView(String view) {
        this.view = view;
    }
    public String getView() {
        return view;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void addData(String key, Object data) {
        this.data.put(key, data);
    }
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
    public String getMessageApi() {
        return messageApi;
    }
    public void setMessageApi(String messageApi) {
        this.messageApi = messageApi;
    }
}
