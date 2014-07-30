package com.hileco.drpc.http.core;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * @author Philipp Gayret
 */
public class HttpClientFactory {

    public static HttpClient create() {

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(50);
        connManager.setDefaultMaxPerRoute(50);

        return HttpClients.custom()
                .setConnectionManager(connManager)
                .build();

    }

}
