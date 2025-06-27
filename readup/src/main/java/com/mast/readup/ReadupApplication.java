package com.mast.readup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.client.HttpClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.stereotype.Component;



/**
 * Classe principale di Spring Boot e logger per variabili d'ambiente.
 */
@SpringBootApplication(
    exclude = {
      HttpClientAutoConfiguration.class,
      RestClientAutoConfiguration.class
    }
)
public class ReadupApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReadupApplication.class, args);
    }

    /**
     * Componente che stampa il valore di DB_HOST all'avvio dell'applicazione.
     */
    @Component
    public static class EnvLogger implements ApplicationRunner {

        @Value("${DB_HOST:undefined}")
        private String dbHost;

        @Override
        public void run(ApplicationArguments args) {
            System.out.println(">> DB_HOST = " + dbHost);
        }
    }
}
