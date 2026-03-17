package com.nttdata.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"html:target/cucumber/cucumber-report.html",
                "json:target/cucumber/cucumber.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"},
        stepNotifications = true,
        features = "src/test/resources/features",
        glue = "com.nttdata",
        tags = "@loginMyStore"
)
public class RunnerTest {
}
