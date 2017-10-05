package com.redhat.bpm.remotemonitoring.scheduler.alert;

import com.redhat.bpm.remotemonitoring.scheduler.core.timer.AbstractTimer;
import com.redhat.bpm.remotemonitoring.scheduler.model.MonitorDefinition;
import com.redhat.bpm.remotemonitoring.scheduler.service.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.inject.Inject;
import javax.inject.Named;


@Stateless
@Named("monitor-timer")
public class MonitorTimer extends AbstractTimer {

    private final Logger logger = LoggerFactory.getLogger(MonitorTimer.class);

    @Inject
    MonitorService monitorService;

    @Override
    @Timeout
    public void execute(Timer timer) {
         MonitorDefinition payload = (MonitorDefinition) timer.getInfo();

        try {
            monitorService.evaluate(payload);
        } catch (Exception e) {
            logger.info("Job {} failed {}", payload.getUuid(), e);
        }

    }
}
