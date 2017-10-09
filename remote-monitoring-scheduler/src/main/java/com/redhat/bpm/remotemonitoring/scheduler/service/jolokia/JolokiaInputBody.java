package com.redhat.bpm.remotemonitoring.scheduler.service.jolokia;


import com.fasterxml.jackson.annotation.JsonInclude;

public class JolokiaInputBody {

    private String type = "read";
    private String mbean;
    private String attribute;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String path;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMbean() {
        return mbean;
    }

    public void setMbean(String mbean) {
        this.mbean = mbean;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
