package StepDefinitions;

import Functions.CreateDriver;
import Functions.SeleniumFunctions;
import Integration.TestLinkIntegration;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import testlink.api.java.client.TestLinkAPIException;
import testlink.api.java.client.TestLinkAPIResults;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Hooks {

    public static WebDriver driver;
    Logger log = Logger.getLogger(Hooks.class);
    Scenario scenario = null;
    SeleniumFunctions functions = new SeleniumFunctions();

    @Before
    public void before(Scenario scenario) throws IOException {
        log.info("***********************************************************************************************************");
        log.info("[ Configuration ] - Initializing driver configuration");
        log.info("***********************************************************************************************************");
        driver = CreateDriver.initConfig();



        this.scenario = scenario;
        log.info("***********************************************************************************************************");
        log.info("[ Scenario ] - " + scenario.getName());
        log.info("***********************************************************************************************************");

        driver.manage().window().setSize(new Dimension(1366, 768));

    }


    @After
    public void after(Scenario scenario) throws Exception {
        SeleniumFunctions.HandleMyWindows.clear();
        String message;
        String cad = "";
        // Selecciono las variables que obtuve de la ejecución
        for (String keys : SeleniumFunctions.ScenaryObtained.keySet()) {
          //  boolean variable = keys.contains(".");
           // if (variable == true) {
                cad += keys + ": " + SeleniumFunctions.ScenaryObtained.get(keys) + ", ";
          //  }
        }

        // Este código permite identificar si el escenario tiene caso en test link y leer el ID.
        String lista = (scenario.getSourceTagNames().toString() + ",").replace("]", "");

        int testExist = lista.indexOf("@tmsLink");
        String testCase;
        if (testExist > 0) {
            testCase = lista.substring(lista.indexOf("@tmsLink") + 9, lista.indexOf(",", lista.indexOf("@tmsLink")));
        } else {

            testCase = "";
            System.out.println();

        }
        //  Fin código leer caso de testlink de los tags.
        boolean resti = Boolean.parseBoolean(CreateDriver.defaultTestlink());
        String browser = CreateDriver.defaultBrowser();
        String ambiente = CreateDriver.defaultAmbiente();



        if (scenario.isFailed()) {

            message = "El estado del ambiente es INESTABLE, prueba automatizada realizada en el Navegador: " + browser + ", Ambiente: " + ambiente + ", Se obtuvieron las siguientes variables: " + cad;

            // Valido si debo agregar resultado a testlink


            if (resti && testExist > 0) {

                try {
                    TestLinkIntegration.updateResults(testCase, message, TestLinkAPIResults.TEST_FAILED);
                    System.out.println("Result test added to Testlink case name " + testCase);
                } catch (TestLinkAPIException e) {
                    System.out.println("There is a problem updating data to testlink." + e);
                }
            } else {
                System.out.println("TestLink Result Disabled or unassigned");
            }
            // Agrego a reporte Allure un pantallazo y las variables obtenidas
            try {
                scenario.attach(functions.getByteScreenshot(), "image/png", message);
            } catch (WebDriverException somePlatformsDontSupportScreenshots) {
                System.err.println(somePlatformsDontSupportScreenshots.getMessage());
            }
        } else {

            message = "El estado del ambiente es estable, prueba automatizada realizada en el Navegador: " + browser + ", Ambiente: " + ambiente + ", Se obtuvieron las siguientes variables: " + cad;
            // si escenario es exitoso, guardo variables obtenidas y guardo resultado en testlink si está habilitado
            scenario.attach(functions.getByteScreenshot(), "image/png", message);
            if (resti && testExist > 0) {
                try {
					TestLinkIntegration.updateResults(testCase, message, TestLinkAPIResults.TEST_PASSED);
                    System.out.println("Result test added to Testlink case name " + testCase);

                } catch (TestLinkAPIException e) {
                    System.out.println("There is a problem updating data to testlink.");
                }

            } else {
                System.out.println("TestLink Result Disabled or unassigned");
            }
        }

		/*/  Aqui identifico si esta habilitado slack, si es asi intento leer de las variables el webhook e intento enviar el mensaje.
		if (slack == true) {
			try {

				SlackUtils.sendMessage(
						SlackMessage.builder()
								.text(message)
								.build(), slackHook);

			} catch (Exception e) {
			}
		}
        allureEnvironmentWriter(
                ImmutableMap.<String, String>builder()
                        .put("Navegador", browser)
                        .put("Ambiente", ambiente)
                        .put("URL", urlBase)
                        .build());

                        */

        File dir = new File(("src/test/resources/Files/"));

        for (File file : dir.listFiles()) {
            if (!file.isDirectory())
                file.delete();
        }
        SeleniumFunctions.ScenaryObtained.clear();

      driver.quit(); // Cierra el navegador

    }

    @AfterStep
    public void addScreenShot(Scenario scenario){

        if(SeleniumFunctions.StepScreenShots.size()!=0) {
        // using for-each loop for iteration over Map.entrySet()
        for (Map.Entry<String, byte[]> entry : SeleniumFunctions.StepScreenShots.entrySet()) {
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
            scenario.attach(entry.getValue(), "image/png",entry.getKey());
        }
        }
        SeleniumFunctions.StepScreenShots.clear();
    }
}
