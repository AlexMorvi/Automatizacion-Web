package com.nttdata.core;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverManager {
    private static WebDriver driver;
    private static Scenario scenario;

    public static WebDriver getDriver() {
        return driver;
    }

    @Before(order = 0)
    public void setUp() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
        } else if (os.contains("mac")) {
            System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");
        } else {
            System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");
        }
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Before(order = 1)
    public void setScenario(Scenario scenario) {
        DriverManager.scenario = scenario;
    }

    @After
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    public static void screenShot(String label) {
        byte[] evidencia = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        scenario.attach(evidencia, "image/png", label);
    }
}
