package com.redhat.bpm.remotemonitoring.scheduler.alert;


import com.redhat.bpm.remotemonitoring.scheduler.core.timer.AbstractTimer;
import com.redhat.bpm.remotemonitoring.scheduler.core.timer.ScheduleExpressionFactory;
import com.redhat.bpm.remotemonitoring.scheduler.core.timer.TimerException;
import com.redhat.bpm.remotemonitoring.scheduler.model.MonitorDefinition;
import com.redhat.bpm.remotemonitoring.scheduler.service.MonitorDefinitionLoader;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;


@ApplicationScoped
public class MonitorTimerService {

    private final Logger logger = LoggerFactory.getLogger(MonitorTimerService.class);

    private final static String JOBS_PREFIX =  "MONITOR_JOBS";

    @Inject
    @Named("monitor-timer")
    AbstractTimer timer;

    @Inject
    MonitorDefinitionLoader monitorDefinitionLoader;


    public void initJobs() {
        try {
            timer.cancelTimers(JOBS_PREFIX);

            List<MonitorDefinition> jobs = monitorDefinitionLoader.getMonitorDefinitions();

            if(CollectionUtils.isNotEmpty(jobs))
                jobs.forEach(j -> timer.createTimer(j, ScheduleExpressionFactory.create(j.getScheduleExprName()), logger));

        } catch (TimerException e) {
            throw new IllegalStateException("Can't delete previous timers");
        }
    }



}
