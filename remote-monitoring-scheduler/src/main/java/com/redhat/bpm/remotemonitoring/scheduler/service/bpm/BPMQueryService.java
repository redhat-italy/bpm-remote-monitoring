package com.redhat.bpm.remotemonitoring.scheduler.service.bpm;

import com.redhat.bpm.remotemonitoring.scheduler.model.KieServerDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;

@ApplicationScoped
public class BPMQueryService {

    private final static Logger logger = LoggerFactory.getLogger(BPMQueryService.class);

    private final static int MAX_PROCESS_BY_QUERY = 1000;
    private final static String ACTIVE_PROCESSES_LASTMINUTES = "ActiveProcessesLastMinutes";

    public List<ProcessInstance> activeProcesses(KieServerDefinition kieServerDefinition, List<String> processesBlackList) {
        String serverUrl = new StringBuilder(kieServerDefinition.getProtocol())
                .append("://").append(kieServerDefinition.getHost())
                .append(":").append(kieServerDefinition.getPort())
                .append("/").append(kieServerDefinition.getContext())
                .append("/services/rest/server")
                .toString();

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(serverUrl, kieServerDefinition.getUser(), kieServerDefinition.getPwd());
        config.setMarshallingFormat(MarshallingFormat.JSON);
        config.setTimeout(kieServerDefinition.getTimeout());
        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);
        QueryServicesClient queryClient = client.getServicesClient(QueryServicesClient.class);

        List<ProcessInstance> result = new ArrayList<>();
        int page = 0;
        List<ProcessInstance> processInstances =
                queryClient.findProcessInstancesByContainerId(kieServerDefinition.getContainerId(), Arrays.asList(STATE_ACTIVE), page, MAX_PROCESS_BY_QUERY);;
        while(processInstances.size() == MAX_PROCESS_BY_QUERY) {
            page++;
            ignoreBlacklistedProcesses(processesBlackList, result, processInstances);
            processInstances =
                    queryClient.findProcessInstancesByContainerId(kieServerDefinition.getContainerId(), Arrays.asList(STATE_ACTIVE), page, MAX_PROCESS_BY_QUERY);;

        }
        if(page == 0 && CollectionUtils.isNotEmpty(processInstances))
            ignoreBlacklistedProcesses(processesBlackList, result, processInstances);

        client.completeConversation();

        return result;

    }

    public List<ProcessInstance> activeProcessesLastInterval(KieServerDefinition kieServerDefinition, Integer interval, List<String> processesBlackList) {
        String serverUrl = new StringBuilder(kieServerDefinition.getProtocol())
                .append("://").append(kieServerDefinition.getHost())
                .append(":").append(kieServerDefinition.getPort())
                .append("/").append(kieServerDefinition.getContext())
                .append("/services/rest/server")
                .toString();

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(serverUrl, kieServerDefinition.getUser(), kieServerDefinition.getPwd());
        config.setMarshallingFormat(MarshallingFormat.JSON);
        config.setTimeout(kieServerDefinition.getTimeout());
        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);
        QueryServicesClient queryClient = client.getServicesClient(QueryServicesClient.class);
        QueryDefinition queryDefinition = createQueryDefinitionForActiveProcessesLastInterval(kieServerDefinition.getDatasource(), kieServerDefinition.getContainerId(), interval);
        client.getServicesClient(QueryServicesClient.class).replaceQuery(queryDefinition);

        List<ProcessInstance> result = new ArrayList<>();
        int page = 0;
        List<ProcessInstance> processInstances = queryClient.query(queryDefinition.getName(), QueryServicesClient.QUERY_MAP_PI, "processId asc, processInstanceId desc", page, MAX_PROCESS_BY_QUERY, ProcessInstance.class);
        while(processInstances.size() == MAX_PROCESS_BY_QUERY) {
            page++;
            ignoreBlacklistedProcesses(processesBlackList, result, processInstances);
            processInstances = queryClient.query(queryDefinition.getName(), QueryServicesClient.QUERY_MAP_PI, page, MAX_PROCESS_BY_QUERY, ProcessInstance.class);
        }
        if(page == 0 && CollectionUtils.isNotEmpty(processInstances))
            ignoreBlacklistedProcesses(processesBlackList, result, processInstances);

        client.completeConversation();

        return result;

    }

    private void ignoreBlacklistedProcesses(List<String> processesBlackList, List<ProcessInstance> result, List<ProcessInstance> processInstances) {
        for (ProcessInstance processInstance : processInstances) {
            if (processesBlackList.contains(processInstance.getProcessId()))
                continue;
            else
                result.add(processInstance);
        }
    }

    private QueryDefinition createQueryDefinitionForActiveProcessesLastInterval(String datasource, String containerId, Integer interval) {
        String lastMinutes = interval + " minutes";
        QueryDefinition query = new QueryDefinition();
        query.setName(ACTIVE_PROCESSES_LASTMINUTES);
        query.setSource(datasource);
        query.setExpression("select * from processinstancelog plog where  externalid = '"+containerId+"'" +
                "  and status = 1 or end_date > now() -interval '"+lastMinutes+"'");
        query.setTarget("CUSTOM");
        return query;
    }


}
