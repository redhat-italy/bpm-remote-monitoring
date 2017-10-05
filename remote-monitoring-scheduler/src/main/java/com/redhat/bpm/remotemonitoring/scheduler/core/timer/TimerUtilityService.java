package com.redhat.bpm.remotemonitoring.scheduler.core.timer;


import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TimerService;
import java.util.Collection;

@Stateless
public class TimerUtilityService {

    @Resource
    protected TimerService timerService;

    public Collection<javax.ejb.Timer> getAllTimers() {
        return timerService.getAllTimers();
    }
}
