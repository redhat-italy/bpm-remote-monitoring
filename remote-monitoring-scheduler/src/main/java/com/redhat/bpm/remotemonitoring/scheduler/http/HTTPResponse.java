package com.redhat.bpm.remotemonitoring.scheduler.http;


public class HTTPResponse {

    protected int status = 500;
    protected String error;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
