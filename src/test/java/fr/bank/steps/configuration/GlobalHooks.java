package fr.bank.steps.configuration;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;

public class GlobalHooks {

    @BeforeAll
    public static void startKoin() {
        System.out.println("GlobalHooks: Initializing Koin...");
        KoinManager.startKoinIfNeeded();
        TestApplicationManager.startApplication();
    }

    @AfterAll
    public static void stopKoin() {
        System.out.println("GlobalHooks: Stopping Koin...");
        KoinManager.stopKoinIfNeeded();
        TestApplicationManager.stopApplication();
    }
}
