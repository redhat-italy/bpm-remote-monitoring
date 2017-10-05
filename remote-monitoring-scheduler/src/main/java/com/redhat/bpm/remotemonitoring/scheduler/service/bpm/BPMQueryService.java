package com.redhat.bpm.remotemonitoring.scheduler.service.bpm;

import com.redhat.bpm.remotemonitoring.scheduler.model.KieServerDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;


public class BPMQueryService {

    private final static Logger logger = LoggerFactory.getLogger(BPMQueryService.class);

    private final static int MAX_PROCESS_BY_QUERY = 1000;


    public static List<ProcessInstance> activeProcesses(KieServerDefinition kieServerDefinition, List<String> processesBlackList) {
        String serverUrl = new StringBuilder(kieServerDefinition.getProtocol())
                .append("://").append(kieServerDefinition.getHost())
                .append(":").append(kieServerDefinition.getPort())
                .append("/").append(kieServerDefinition.getContext())
                .append("/services/rest/server")
                .toString();

        logger.info("KIE SERVER Url {}", serverUrl);

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

    private static void ignoreBlacklistedProcesses(List<String> processesBlackList, List<ProcessInstance> result, List<ProcessInstance> processInstances) {
        for (ProcessInstance processInstance : processInstances) {
            if (processesBlackList.contains(processInstance.getProcessId()))
                continue;
            else
                result.add(processInstance);
        }
    }


}
