package com.mast.readup.configuration;

import com.mast.readup.services.LibroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Component that triggers the automatic population of missing ISBNs
 * from OpenLibrary when the application context is fully initialized.
 */
@Component
public class StartISBN {

    private static final Logger log = LoggerFactory.getLogger(StartISBN.class);
    private final LibroService libroService;

    public StartISBN(LibroService libroService) {
        this.libroService = libroService;
    }

    /**
     * Listens for the ApplicationReadyEvent and invokes the service
     * to populate any Libro records missing an ISBN.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application ready: starting ISBN population...");
        try {
            libroService.populateMissingIsbns();
            log.info("ISBN population completed.");
        } catch (Exception ex) {
            log.error("Error during ISBN population on startup", ex);
        }
    }
}