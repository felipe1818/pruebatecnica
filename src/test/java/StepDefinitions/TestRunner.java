package StepDefinitions;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;


import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:Features",
        glue = "classpath:StepDefinitions",
        plugin = {"pretty", "html:target/cucumber-reports.html",
                "json:target/cucumber-reports",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"},

        tags = "@InicioSesion"

)

public class TestRunner {
}
