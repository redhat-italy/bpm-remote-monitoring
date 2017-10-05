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
    "protocol": "http",
    "host": "localhost",
    "port": 8080,
    "context": "kie-server",
    "user": "",
    "timeout": 30000,
    "pwd": "",
    "containerId": ""
  }],
  "processesBlackList": [""]
}
]
```

Timer defintions available so far:

  - ACTIVE_INSTANCES, Monitor Active BPM instances for Kie Server
