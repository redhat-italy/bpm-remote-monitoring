# bpm-remote-monitoring
Monitor Remote Kie Server can collect metrics from running kie servers.

Metrics are collected using a scheduler and ejb timers; each timer has the
definition of the metrics to collects.

# Compile

Run command:

```bash
mvn clean package
```
an EAR file will be created in bpm-remote-monitoring/remote-monitoring-ear/target/bpm-remote-monitoring.ear

# Deploy

Deploy the EAR file in $JBOSS_HOME/standalone/deployments

# Json definition

```bash
mvn clean package
```
an EAR file will be created in bpm-remote-monitoring/remote-monitoring-ear/target/bpm-remote-monitoring.ear

Timer definitions are defined in a json file named monitor-definition.json;
the json file must be placed in $JBOSS_HOME/standalone/configuration directory

```json
[{
  "enabled": "true",
  "type": "ACTIVE_INSTANCES",
  "name": "monitor_active_instances",
  "description": "Monitor Active BPM instances",
  "schedule": {
    "hour": "*",
    "minute": "*/5",
    "second": 0
  },
  "kieservers": [{
    "protocol": "",
    "host": "",
    "port": ,
    "context": "",
    "user": "",
    "timeout": ,
    "pwd": "",
    "containerId": ""
  }],
  "processesBlackList": [""]
},
  {
    "enabled": "true",
    "type": "ACTIVE_INSTANCES_LAST_MINUTES",
    "name": "monitor_active_instances_last_minutes",
    "description": "Monitor Active BPM instances in XX minutes",
    "interval": 3,
    "schedule": {
      "hour": "*",
      "minute": "*/1",
      "second": 0
    },
    "kieservers": [{
      "protocol": "",
      "host": "",
      "port": ,
      "context": "",
      "user": "",
      "timeout": ,
      "pwd": "",
      "containerId": "",
      "datasource": ""
    }],
    "processesBlackList": [""]
  }
]
```

Timer defintions available so far:

  - ACTIVE_INSTANCES, Monitor Active BPM instances
  - ACTIVE_INSTANCES_LAST_MINUTES, Monitor Active BPM instances in last XX minutes

Json fields detail:

  - enabled --> true|false - if the job is enabled
  - type --> string - the type of the jobs: so far can have only these values:
  ACTIVE_INSTANCES or ACTIVE_INSTANCES_LAST_MINUTES
  - name --> string - name of the job
  - description --> string - description of the job
  - interval --> integer (optional) - if present, the metric will compute a sum calculation for data extracted between: job_run_time minus interval (in minutes) and job_run_time
  - schedule --> object defined according to http://docs.oracle.com/javaee/6/api/javax/ejb/ScheduleExpression.html
      - hour: --> string - cron expression for hour
      - minute: --> string - cron expression for minute
      - second: --> integer --> cron expression for second
  - kieservers --> list of objects identifying the kie servers to connect:
      - protocol: --> string - http or https
      - host: --> string - hostname of kie server
      - port: --> integer - port of kie server
      - context: --> string - context path where kie server REST API are bounded
      - user: --> string - user granted to use REST API for the kie server
      - pwd: --> string - password of the user
      - timeout: --> integer - timeout of REST connection
      - containerId --> string - kie container id to Monitor
      - datasource --> jbpm jndi datasource name for kie server
  - processesBlackList --> list of process defintions name to be excluded from monitoring
