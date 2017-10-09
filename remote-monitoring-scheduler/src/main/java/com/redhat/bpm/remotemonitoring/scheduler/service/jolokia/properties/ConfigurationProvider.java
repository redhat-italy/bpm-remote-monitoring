package com.redhat.bpm.remotemonitoring.scheduler.service.jolokia.properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.String.format;

@ApplicationScoped
public class ConfigurationProvider {

    public Configuration provideConfiguration(String propertyName) {
        try {
            String configFilesDir = System.getProperty("jboss.server.config.dir");
            if(StringUtils.isNotEmpty(configFilesDir)) {
                if(Files.exists(Paths.get(configFilesDir + File.separator + propertyName)))
                    propertyName = configFilesDir + File.separator + propertyName;
            }

            return new PropertiesConfiguration(propertyName);
        } catch (ConfigurationException e) {
            throw new IllegalStateException(format("Can't load config %s ", propertyName));
        }
    }
}
