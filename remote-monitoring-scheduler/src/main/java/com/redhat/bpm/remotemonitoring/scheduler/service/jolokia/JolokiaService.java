package com.redhat.bpm.remotemonitoring.scheduler.service.jolokia;

import com.redhat.bpm.remotemonitoring.scheduler.http.HttpClientManager;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class JolokiaService {

    private static final String CONTENT_TYPE_LABEL = "content-type";
    private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    @Inject
    HttpClientManager httpClientManager;

    private final static Logger logger = LoggerFactory.getLogger(JolokiaService.class);

    public JolokiaResponse post(Server server, JMXBean jmxBean) {

        JolokiaResponse response = new JolokiaResponse();
        String result;

        try {
            HttpPost post = new HttpPost(getJolokiaUrl(server));
            JolokiaInputBody jolokiaInputBody = new JolokiaInputBody();
            jolokiaInputBody.setMbean(jmxBean.getMbean());
            jolokiaInputBody.setAttribute(jmxBean.getAttribute());

            JSONObject jsonObject = new JSONObject(jolokiaInputBody);
            String body = jsonObject.toString();

            StringEntity params = new StringEntity(body);
            post.addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_FORM);
            post.setEntity(params);

            if(server.isBasicAuth()) {
                String toEncode = server.getUser() + ":" + server.getPassword();
                byte [] encoding = Base64.encodeBase64(toEncode.getBytes());
                post.setHeader("Authorization", "Basic " + new String(encoding));
            }

            result = httpClientManager.executeRequest(post);
        } catch (Exception ex) {
            logger.error("Error invoking Jolokia", ex);
            return response;
        }

        response.setStatus(200);
        response.setContent(result);

        return response;
    }

    private String getJolokiaUrl(Server server) {
        StringBuilder sb = new StringBuilder();
        sb.append(server.getProtocol());
        sb.append("://");
        sb.append(server.getHost());
        sb.append(":");
        sb.append(server.getPort());
        sb.append("/");
        sb.append(server.getJolokiaContext());
        sb.append("/");
        return sb.toString();
    }
}
