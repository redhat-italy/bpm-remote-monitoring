package com.redhat.bpm.remotemonitoring.scheduler.core.timer;

import com.redhat.bpm.remotemonitoring.scheduler.model.MonitorDefinition;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import java.util.UUID;


public abstract class AbstractTimer {

    @Resource
    protected TimerService timerService;


    public void createTimer(MonitorDefinition payload, ScheduleExpression scheduleExpression, Logger logger) {
        if(Boolean.valueOf(payload.getEnabled())) {
            final String uuid = UUID.randomUUID().toString();
            payload.setUuid(uuid);
            TimerConfig config = new TimerConfig();
            config.setPersistent(false);
            config.setInfo(payload);
            try {
                timerService.createCalendarTimer(scheduleExpression, config);
            } catch (Exception ex) {
                logger.error("Can't create timer!", ex);
            }
        }
    }

    public abstract void execute(Timer timer);


    public MonitorDefinition cancelTimer(String uuid) throws TimerException {

        if (timerService.getTimers() != null) {
            for(Timer timer: timerService.getTimers()) {
                MonitorDefinition payload = (MonitorDefinition) timer.getInfo();
                if(payload.getUuid().equals(uuid)) {
                    //clone payload
                    MonitorDefinition clonedPayload;
                    try {
                        clonedPayload = (MonitorDefinition)payload.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new TimerException(e);
                    }

                    timer.cancel();
                    return clonedPayload;
                }
            }
        }

        return null;
    }

    public void cancelTimers(String prefix) throws TimerException {
        if (timerService.getTimers() != null) {
            for(Timer timer: timerService.getTimers()) {
                MonitorDefinition payload = (MonitorDefinition) timer.getInfo();
                if(payload.getUuid().startsWith(prefix) && payload.getStatus() == MonitorDefinition.Status.FINISHED)
                    timer.cancel();
            }
        }
    }

}
