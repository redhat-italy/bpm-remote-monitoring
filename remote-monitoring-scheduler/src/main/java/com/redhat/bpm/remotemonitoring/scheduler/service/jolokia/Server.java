package com.redhat.bpm.remotemonitoring.scheduler.service.jolokia;

import java.io.Serializable;


public class Server implements Serializable {

    private String name;
    private String protocol;
    private String host;
    private int port;
    private boolean basicAuth = false;
    private String user;
    private String password;
    private String jolokiaContext;


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getJolokiaContext() {
        return jolokiaContext;
    }

    public void setJolokiaContext(String jolokiaContext) {
        this.jolokiaContext = jolokiaContext;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isBasicAuth() {
        return basicAuth;
    }

    public void setBasicAuth(boolean basicAuth) {
        this.basicAuth = basicAuth;
    }
}
