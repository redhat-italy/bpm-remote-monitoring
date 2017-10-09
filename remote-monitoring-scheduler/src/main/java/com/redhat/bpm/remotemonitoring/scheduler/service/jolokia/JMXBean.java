package com.redhat.bpm.remotemonitoring.scheduler.service.jolokia;


import java.io.Serializable;
import java.util.List;

public class JMXBean implements Serializable {

    private JMXBeanDefinition name;
    private String mbean;
    private String attribute;
    private List<String> additionalArgs;

    public JMXBeanDefinition getName() {
        return name;
    }

    public void setName(JMXBeanDefinition name) {
        this.name = name;
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

    public List<String> getAdditionalArgs() {
        return additionalArgs;
    }

    public void setAdditionalArgs(List<String> additionalArgs) {
        this.additionalArgs = additionalArgs;
    }
}
