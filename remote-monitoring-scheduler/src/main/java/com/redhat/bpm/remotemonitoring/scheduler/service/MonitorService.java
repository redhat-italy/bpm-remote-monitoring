package com.redhat.bpm.remotemonitoring.scheduler.service;

import com.redhat.bpm.remotemonitoring.scheduler.model.KieServerDefinition;
import com.redhat.bpm.remotemonitoring.scheduler.model.MonitorDefinition;
import com.redhat.bpm.remotemonitoring.scheduler.service.bpm.BPMQueryService;
import org.apache.commons.collections.CollectionUtils;
import org.kie.server.api.model.instance.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class MonitorService {

    private final Logger logger = LoggerFactory.getLogger(MonitorService.class);

    public void evaluate(MonitorDefinition monitorDefinition) {

        switch (monitorDefinition.getType()) {
            case ACTIVE_INSTANCES:
                int processInstancesSize = 0;
                for (KieServerDefinition kieServerDefinition : monitorDefinition.getKieservers()) {
                    List<ProcessInstance> processInstances = BPMQueryService.activeProcesses(kieServerDefinition, monitorDefinition.getProcessesBlackList());
                    if (CollectionUtils.isNotEmpty(processInstances))
                        processInstancesSize += processInstances.size();
                }
                logger.info("Processes instance ACTIVE: {}", processInstancesSize);
                break;
            default:
                break;
        }


    }


}
