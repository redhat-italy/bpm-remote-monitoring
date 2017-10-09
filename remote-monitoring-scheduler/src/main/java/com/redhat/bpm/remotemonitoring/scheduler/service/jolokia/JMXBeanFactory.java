package com.redhat.bpm.remotemonitoring.scheduler.service.jolokia;


import com.redhat.bpm.remotemonitoring.scheduler.model.MonitorDefinition;

import java.text.MessageFormat;
import java.util.List;

public class JMXBeanFactory {

    public static JMXBean fillJMXBean(MonitorDefinition.MonitorType monitorType, List<String> additionalArgs) {
        JMXBean jmxBean = new JMXBean();
        switch (monitorType) {
            case EAP_INUSE_DATASOURCE:
                String mbean = "jboss.as:data-source={0},statistics=pool,subsystem=datasources";
                jmxBean.setMbean(MessageFormat.format(mbean, additionalArgs));
                jmxBean.setAttribute("InUseCount");
                break;

        }
        return jmxBean;
    }


}
