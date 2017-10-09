# bpm-remote-monitoring
BPM Remote Monitor can collect metrics from running kie servers (EAP).

Metrics are collected using a scheduler and ejb timers; each timer has the
definition of the metrics to collects.

The monitor can also be connected to a jolokia agent to expose metrics based on jmx.
The monitor connects to jolokia using Apache HTTP Client; settings of Apache HTTP Client
are defined in file *httpclient.properties*

# Compile

Run command:

```bash
mvn clean package
```
an EAR file will be created in *bpm-remote-monitoring/remote-monitoring-ear/target/bpm-remote-monitoring.ear*

# Deploy

```bash
mvn clean package
```
an EAR file will be created in *bpm-remote-monitoring/remote-monitoring-ear/target/bpm-remote-monitoring.ear*

Deploy the EAR file in *$JBOSS_HOME/standalone/deployments*

Place the *monitor-definition.json* file in *$JBOSS_HOME/standalone/configuration* directory

Place the *httpclient.properties* file in *$JBOSS_HOME/standalone/configuration* directory

# Json definition

Timer definitions are defined in a json file named *monitor-definition.json*
the json file must be placed in *$JBOSS_HOME/standalone/configuration* directory

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
  },
  {
    "enabled": "true",
    "type": "EAP_INUSE_DATASOURCE",
    "name": "monitor_inuse_datasource",
    "description": "Monitor In Use datasource connections",
    "schedule": {
      "hour": "*",
      "minute": "*/5",
      "second": 0
    },
    "jolokiaservers": [{
      "protocol": "",
      "host": "",
      "port": ,
      "jolokiaContext": "",
      "user": "",
      "password": ,
      "basicAuth": ""
    }],
    "additionalArgs": [""]
  }
]
```

Timer defintions available so far:

  - ACTIVE_INSTANCES, Monitor Active BPM instances
  - ACTIVE_INSTANCES_LAST_MINUTES, Monitor Active BPM instances in last XX minutes
  - EAP_INUSE_DATASOURCE, Monitor JDBC Connection is use for Datasource

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
  - jolokiaservers --> list of objects identifying the jolokia servers to connect:
      - protocol: --> string - http or https
      - host: --> string - hostname of jolokia server
      - port: --> integer - port of jolokia server
      - jolokiaContext: --> string - context path where jolokia server REST API are bounded
      - user: --> string - user granted to use REST API for the jolokia server
      - password: --> string - password of the user
      - basicAuth: --> true|false - if basic auth is enabled on jolokia (need user and password)
  - additionalArgs --> list of addiitonal strings to pass to metrics; so far it is in use only for
  jolokia metrics (ex. name of datasource)

  # HTTP client properties

  The monitor connects to jolokia using Apache HTTP Client; settings of Apache HTTP Client
  are defined in file *httpclient.properties*
  the json file must be placed in *$JBOSS_HOME/standalone/configuration* directory

  The properties are defined according to:
  https://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html

  Properties fields:
  - http_client_pool_maxSize --> integer - number of http client connections total in pool
  - http_client_pool_maxSizePerRoute --> integer - number of http client connections per route
  - http_client_keepAlive --> true|false - keep alive on/off
  - http_client_keepAlive_duration --> integer - keep alive connections for n seconds
  - http_client_socketTimeout --> integer - defines the socket timeout (SO_TIMEOUT) in milliseconds, which is the timeout for waiting for data
  - http_client_connectTimeout --> integer - determines the timeout in milliseconds until a connection is established
  - http_client_maxRedirects --> integer - maximum number of redirects per route
  - http_client_pool_idleCheck --> integer - defines the interval in seconds to check for idle http connections
