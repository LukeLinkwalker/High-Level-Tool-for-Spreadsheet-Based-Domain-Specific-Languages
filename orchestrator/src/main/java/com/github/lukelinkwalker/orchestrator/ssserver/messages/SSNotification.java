package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSNotification {
    private String method;
    private Object[] params;

    public SSNotification(String method, Object[] params) {
        this.method = method;
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
