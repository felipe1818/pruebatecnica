package Functions;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class CreateDriver {
    private static String browser;
    private static String os;
    private static String logLevel;

    private static Properties prop = new Properties();
    private static InputStream in = CreateDriver.class.getResourceAsStream("../test.properties");
    private static CreateDriver instance = null;

    /******** Log Attribute ********/
    private static Logger log = Logger.getLogger(CreateDriver.class);
    
    /******** Initialize Driver Configuration when the class is instanced ********/
    private CreateDriver() throws IOException {
    	CreateDriver.initConfig();
    }

    /**
     * Singleton pattern
     * @return a single instance

    public static CreateDriver getInstance() throws IOException {
        if (instance == null) {
            instance = new CreateDriver();
        }
        return instance;
    }

    /**
     * Get the Browser from the POM
     */
     public static WebDriver initConfig() throws IOException {
    	WebDriver driver; 
    	
        try {
        	log.info("***********************************************************************************************************");
        	log.info("[ POM Configuration ] - Read the basic properties configuration from: ../test.properties");
            prop.load(in);
           //
            browser = CreateDriver.defaultBrowser();
            os = prop.getProperty("os");
            logLevel = prop.getProperty("logLevel");
            
        } catch (IOException e) {
        	log.error("initConfig Error", e);
        }
        
        /******** POM Information ********/
        log.info("[ POM Configuration ] - OS: " + os + " | Browser: " + browser + " |");
        log.info("[ POM Configuration ] - Logger Level: " + logLevel);
        log.info("***********************************************************************************************************");
        
        /****** Load the driver *******/

            driver = WebDriverFactory.createNewWebDriver(browser, os);

        return driver;
    }

    public static String defaultAmbiente() throws IOException {

        String ambiente = System.getProperty("environment");
        if (ambiente == null) { ambiente = SeleniumFunctions.readProperties("environment"); }
        return ambiente;
    }

    public static String defaultBrowser() throws IOException {


        String browser = System.getProperty("browser");

        if (browser == null) { browser = SeleniumFunctions.readProperties("browser"); }
        return browser;
    }


    public static String defaultPlanTestLink() throws IOException {

        String testplan = System.getProperty("testplan");
        if (testplan == null) { testplan = SeleniumFunctions.readProperties("TESTLINK_PLAN_NAME"); }
        return testplan;
    }

    public static String defaultProjectTestLink() throws IOException {

        String testplan = System.getProperty("testproject");
        if (testplan == null) { testplan = SeleniumFunctions.readProperties("TESTLINK_PROJECT_NAME"); }
        return testplan;
    }

    public static String defaultTestlink() throws IOException {

        String testlink = System.getProperty("testlink");
        if (testlink == null) { testlink = SeleniumFunctions.readProperties("testlink"); }
        return testlink;
    }

    public static String defaultHeadLess() throws IOException {

        String headless = System.getProperty("headless");
        if (headless == null) { headless = SeleniumFunctions.readProperties("headless"); }
        return headless;
    }

    public static String defaultSlack() throws IOException {

        String slack = System.getProperty("slack");
        if (slack == null) { slack = SeleniumFunctions.readProperties("slack"); }
        return slack;
    }


}
