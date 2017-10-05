package com.redhat.bpm.remotemonitoring.scheduler.alert;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;


@Startup
@Singleton
public class MonitorScheduler {

    @Inject
    MonitorTimerService monitorTimerService;

    @PostConstruct
    public void init() {
        monitorTimerService.initJobs();
    }

}
