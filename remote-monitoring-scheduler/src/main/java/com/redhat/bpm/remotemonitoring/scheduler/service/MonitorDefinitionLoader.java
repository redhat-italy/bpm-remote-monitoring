package com.redhat.bpm.remotemonitoring.scheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.bpm.remotemonitoring.scheduler.alert.MonitorTimerService;
import com.redhat.bpm.remotemonitoring.scheduler.model.MonitorDefinition;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ApplicationScoped
public class MonitorDefinitionLoader {

    private final Logger logger = LoggerFactory.getLogger(MonitorTimerService.class);

    private List<MonitorDefinition> monitorDefinitions;

    @PostConstruct
    public void loadMonitors() {
        try {
            String configFilesDir = System.getProperty("jboss.server.config.dir");
            String alertFile = configFilesDir + File.separator + "monitor-definition.json";
            Path path = Paths.get(alertFile);
            if (Files.exists(path)) {
                InputStream is = new FileInputStream(alertFile);
                String jsonTxt = IOUtils.toString(is);
                ObjectMapper objectMapper = new ObjectMapper();
                monitorDefinitions = objectMapper.readValue(
                        jsonTxt,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, MonitorDefinition.class));
            }

        } catch (Throwable ex) {
            logger.error("Can't load json monitor file", ex);
        }

    }

    public List<MonitorDefinition> getMonitorDefinitions() {
        return monitorDefinitions;
    }

    public void setMonitorDefinitions(List<MonitorDefinition> monitorDefinitions) {
        this.monitorDefinitions = monitorDefinitions;
    }

}
