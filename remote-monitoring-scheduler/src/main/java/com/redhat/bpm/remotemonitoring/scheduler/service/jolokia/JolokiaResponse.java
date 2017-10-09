package com.redhat.bpm.remotemonitoring.scheduler.service.jolokia;


import com.redhat.bpm.remotemonitoring.scheduler.http.HTTPResponse;

public class JolokiaResponse extends HTTPResponse {

    private String content;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
