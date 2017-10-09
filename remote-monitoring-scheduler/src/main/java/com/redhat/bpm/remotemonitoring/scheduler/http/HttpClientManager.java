package com.redhat.bpm.remotemonitoring.scheduler.http;


import com.redhat.bpm.remotemonitoring.scheduler.service.jolokia.properties.HttpClientConfig;
import org.apache.commons.configuration.Configuration;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;


@ApplicationScoped
public class HttpClientManager {

    @Inject
    @HttpClientConfig
    Configuration httpClientProps;

    private CloseableHttpClient httpClient;

    private PoolingHttpClientConnectionManager cm;

    private ConnectionKeepAliveStrategy keepAliveStrategy;

    private final static Logger logger = LoggerFactory.getLogger(HttpClientManager.class);

    @PostConstruct
    public void initialize() {

        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(httpClientProps.getInt("http_client_pool_maxSize"));
        cm.setDefaultMaxPerRoute(httpClientProps.getInt("http_client_pool_maxSizePerRoute"));

        IdleConnectionMonitorThread staleMonitor = new IdleConnectionMonitorThread(cm);
        staleMonitor.start();
        try {
            staleMonitor.join(1000);
        } catch (InterruptedException e) {
            logger.error("http client manager idle connection thread error {}", e.getMessage());
        }

        logger.info("http client manager pool created with {} connections", httpClientProps.getInt("http_client_pool_maxSize"));

        if(httpClientProps.getBoolean("http_client_keepAlive")) {
            /**
             * This strategy will first try to apply the host’s Keep-Alive policy stated in the header.
             * If that information is not present in the response header it will keep alive connections for n seconds.
             */
            keepAliveStrategy = (response, context) -> {
                HeaderElementIterator it = new BasicHeaderElementIterator
                        (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        return Long.parseLong(value) * 1000;
                    }
                }

                return httpClientProps.getInt("http_client_keepAlive_duration") * 1000;
            };


            logger.info("http client manager pool created with keep alive strategy");

        }

    }

    @PreDestroy
    public void preDestroy() {
        cm.close();
        cm.shutdown();
    }

    public String executeRequest(HttpEntityEnclosingRequestBase request) throws Exception {

        HttpResponse response = send(request);
        StringBuffer result = new StringBuffer();
        try(BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        }

        return result.toString();
    }



    private CloseableHttpResponse send(HttpEntityEnclosingRequestBase request) throws HttpSendException {

        HttpClientContext context = HttpClientContext.create();

        //force a default request behaviour
        if(request.getConfig() == null) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(httpClientProps.getInt("http_client_socketTimeout"))
                    .setConnectTimeout(httpClientProps.getInt("http_client_connectTimeout"))
                    .setMaxRedirects(httpClientProps.getInt("http_client_maxRedirects"))
                    .build();

            request.setConfig(requestConfig);
        }

        if(httpClientProps.getBoolean("http_client_keepAlive")) {
            httpClient = HttpClients.custom()
                    .setKeepAliveStrategy(keepAliveStrategy)
                    .setConnectionManager(cm)
                    .build();
        } else {
            httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .build();
        }
        try (CloseableHttpResponse response = httpClient.execute(request, context)) {
            return response;
        } catch (ClientProtocolException e) {
            throw new HttpSendException(e);
        } catch (IOException e) {
            throw new HttpSendException(e);
        }
    }

    public class IdleConnectionMonitorThread extends Thread {
        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread
                (PoolingHttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
        }
        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(1000);
                        connMgr.closeExpiredConnections();
                        connMgr.closeIdleConnections(httpClientProps.getInt("http_client_pool_idleCheck"), TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                shutdown();
            }
        }
        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }


}
