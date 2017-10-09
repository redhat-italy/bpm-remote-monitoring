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
                    final String datasourceDetails = server.getHost() + ":" + server.getPort() + " - " + monitorDefinition.getAdditionalArgs().get(0);
                    JMXBean jmxBean = JMXBeanFactory.fillJMXBean(monitorDefinition.getType(), monitorDefinition.getAdditionalArgs());
                    JolokiaResponse jolokiaResponse = jolokiaService.post(server, jmxBean);
                    if(jolokiaResponse.getStatus() == 200)
                        logger.info("Datasource: {} in use count {}", datasourceDetails, jolokiaResponse.getContent());
                }
                break;

            /**
             *  select * from processinstancelog plog where  externalid = $1
             *  and status = 1 and processId not in ($2)
             */

            case ACTIVE_INSTANCES:
                int processInstancesSize = 0;
                for (KieServerDefinition kieServerDefinition : monitorDefinition.getKieservers()) {
                    final String kieserverName = kieServerDefinition.getHost() + ":" + kieServerDefinition.getPort() + " - " + kieServerDefinition.getContainerId();
                    List<ProcessInstance> processInstances = bpmQueryService.activeProcesses(kieServerDefinition, monitorDefinition.getProcessesBlackList());
                    if (CollectionUtils.isNotEmpty(processInstances))
                        logger.info("BPM processes instance ACTIVE for kie server {}: {}", kieserverName, processInstances.size());
                        processInstancesSize += processInstances.size();
                }
                logger.info("Total BPM processes instance ACTIVE: {}", processInstancesSize);
                break;

            /**
             *  select * from processinstancelog plog where  externalid = 'com.enel.workbeat:acq-process:1.4.0-SNAPSHOT'
             *  and processId not in ('com.enel.workbeat.common.process.rest-client')
                and end_date is null or end_date > now() -interval 'XX minutes'
             */

            case ACTIVE_INSTANCES_LAST_MINUTES:
                int processInstancesLastIntervalSize = 0;
                for (KieServerDefinition kieServerDefinition : monitorDefinition.getKieservers()) {
                    final String kieserverName = kieServerDefinition.getHost() + ":" + kieServerDefinition.getPort() + " - " + kieServerDefinition.getContainerId();
                    List<ProcessInstance> processInstances = bpmQueryService.activeProcessesLastInterval(kieServerDefinition, monitorDefinition.getInterval(), monitorDefinition.getProcessesBlackList());
                    if (CollectionUtils.isNotEmpty(processInstances))
                        logger.info("BPM processes instance ACTIVE for kie server " + kieserverName + ", during last interval: {} - {} minutes", processInstances.size(), monitorDefinition.getInterval());
                    processInstancesLastIntervalSize += processInstances.size();
                }
                logger.info("Total BPM processes instance ACTIVE during last interval: {} - {} minutes", processInstancesLastIntervalSize, monitorDefinition.getInterval());
                break;
            default:
                break;
        }


    }




}
