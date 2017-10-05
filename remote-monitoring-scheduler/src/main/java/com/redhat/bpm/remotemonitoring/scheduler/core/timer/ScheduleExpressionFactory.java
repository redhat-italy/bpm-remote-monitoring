package com.redhat.bpm.remotemonitoring.scheduler.core.timer;


import com.redhat.bpm.remotemonitoring.scheduler.model.ScheduleDefinition;

import javax.ejb.ScheduleExpression;

public class ScheduleExpressionFactory {

    public static ScheduleExpression create(ScheduleDefinition scheduleDefinition) {
        ScheduleExpression scheduleExpression = new ScheduleExpression();
        scheduleExpression.second(scheduleDefinition.getSecond());
        scheduleExpression.minute(scheduleDefinition.getMinute());
        scheduleExpression.hour(scheduleDefinition.getHour());
        return scheduleExpression;
    }


}
