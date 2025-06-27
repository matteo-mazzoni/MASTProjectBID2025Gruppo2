/**
 * Configures and exposes a RestTemplate Bean backed by Apache HttpClient.
 * It centralises HTTP client settings to standardise all outbound calls
 * to external services (e.g. OpenLibrary Search & Covers APIs),
 * including a retry policy for transient errors.
 */
package com.mast.readup.configuration;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {

        // Connection manager with pooling to set maximum number of connections
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(20);

        // Retry strategy: up to 3 retries with 1s interval on I/O exceptions or 5xx
         DefaultHttpRequestRetryStrategy    retryStrategy = new DefaultHttpRequestRetryStrategy(
            3,                                  // max retry count
            TimeValue.ofSeconds(1)              // retry interval
        );

        // Creates HttpClient with pooling and retry
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(connManager)
            .setRetryStrategy(retryStrategy)
            .evictExpiredConnections()
            .useSystemProperties()
            .build();

        // Bridge object between HttpClient and RestTemplate
        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory(httpClient);

        // Timeout settings
        factory.setConnectTimeout(5_000); // 5 seconds to establish TCP connection
        factory.setReadTimeout(5_000);    // 5 seconds to complete reading response

        // Creates RestTemplate with configured factory
        return new RestTemplate(factory);
    }
}
