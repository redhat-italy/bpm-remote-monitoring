# bpm-remote-monitoring
Monitor Remote Kie Server can collect metrics from running kie servers.

Metrics are collected using a scheduler and ejb timers; each timer has the
definition of the metrics to collects.

Timer definitions are defined in a json file:

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
