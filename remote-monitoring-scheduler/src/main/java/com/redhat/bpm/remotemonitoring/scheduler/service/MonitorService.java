package com.redhat.bpm.remotemonitoring.scheduler.service;

import com.redhat.bpm.remotemonitoring.scheduler.model.KieServerDefinition;
import com.redhat.bpm.remotemonitoring.scheduler.model.MonitorDefinition;
import com.redhat.bpm.remotemonitoring.scheduler.service.bpm.BPMQueryService;
import com.redhat.bpm.remotemonitoring.scheduler.service.jolokia.*;
import org.apache.commons.collections.CollectionUtils;
import org.kie.server.api.model.instance.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class MonitorService {

    private final Logger logger = LoggerFactory.getLogger(MonitorService.class);

    @Inject
    JolokiaService jolokiaService;

    @Inject
    BPMQueryService bpmQueryService;

    public void evaluate(MonitorDefinition monitorDefinition) {

        switch (monitorDefinition.getType()) {

            case EAP_INUSE_DATASOURCE:
                for(Server server: monitorDefinition.getJolokiaservers()) {
                    JMXBean jmxBean = JMXBeanFactory.fillJMXBean(monitorDefinition.getType(), monitorDefinition.getAdditionalArgs());
                    JolokiaResponse jolokiaResponse = jolokiaService.post(server, jmxBean);
                    if(jolokiaResponse.getStatus() == 200)
                        logger.info("Datasource: {} in use count {}", monitorDefinition.getAdditionalArgs().get(0), jolokiaResponse.getContent());
                }
                break;

            /**
             *  select * from processinstancelog plog where  externalid = $1
             *  and status = 1 and processId not in ($2)
             */

            case ACTIVE_INSTANCES:
                int processInstancesSize = 0;
                for (KieServerDefinition kieServerDefinition : monitorDefinition.getKieservers()) {
                    List<ProcessInstance> processInstances = bpmQueryService.activeProcesses(kieServerDefinition, monitorDefinition.getProcessesBlackList());
                    if (CollectionUtils.isNotEmpty(processInstances))
                        processInstancesSize += processInstances.size();
                }
                logger.info("Processes instance ACTIVE: {}", processInstancesSize);
                break;

            /**
             *  select * from processinstancelog plog where  externalid = 'com.enel.workbeat:acq-process:1.4.0-SNAPSHOT'
             *  and processId not in ('com.enel.workbeat.common.process.rest-client')
                and end_date is null or end_date > now() -interval '1 minutes'
             */

            case ACTIVE_INSTANCES_LAST_MINUTES:
                int processInstancesLastMinuteSize = 0;
                for (KieServerDefinition kieServerDefinition : monitorDefinition.getKieservers()) {
                    List<ProcessInstance> processInstances = bpmQueryService.activeProcessesLastMinutes(kieServerDefinition, monitorDefinition.getInterval(), monitorDefinition.getProcessesBlackList());
                    if (CollectionUtils.isNotEmpty(processInstances))
                        processInstancesLastMinuteSize += processInstances.size();
                }
                logger.info("Processes instance ACTIVE during last minute: {}", processInstancesLastMinuteSize);
                break;
            default:
                break;
        }


    }




}
