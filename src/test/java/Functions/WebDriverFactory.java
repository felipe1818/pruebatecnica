package Functions;

import org.apache.log4j.Logger;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v100.network.Network;
import org.openqa.selenium.devtools.v100.network.model.Response;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class WebDriverFactory {


	private static Properties prop = new Properties();
	private static InputStream in = CreateDriver.class.getResourceAsStream("../test.properties");
	private static String resourceFolder;

    /******** Log Attribute ********/
    private static Logger log = Logger.getLogger(WebDriverFactory.class);
    
	private static WebDriverFactory instance = null;
	    
    private WebDriverFactory() {

    }
    
    /**
     * Singleton pattern
     * @return a single instance
     */
    public static WebDriverFactory getInstance() {
        if (instance == null) {
            instance = new WebDriverFactory();
        }
        return instance;
    }    
	
		
	 public static WebDriver createNewWebDriver(String browser, String os) throws IOException {
		 WebDriver driver = null;
		 ChromeDriver driver2 = null;
		 prop.load(in);
		 resourceFolder = prop.getProperty("resourceFolder");

		 if ("FIREFOX".equalsIgnoreCase(browser)) {
			 if("WINDOWS".equalsIgnoreCase(os)){
				 System.setProperty("webdriver.gecko.driver", resourceFolder + os + "/geckodriver.exe");
			 }
			 else{
				 System.setProperty("webdriver.gecko.driver", resourceFolder + os + "/geckodriver");
			 }
		     driver = new FirefoxDriver();
		 }

		 /******** The driver selected is Chrome  ********/

	     else if ("CHROME".equalsIgnoreCase(browser)) {
	    	 if("WINDOWS".equalsIgnoreCase(os)){
	    		 System.setProperty("webdriver.chrome.driver", resourceFolder+os+"/chromedriverw.exe");
	    	 }
	    	 else{
	    		 System.setProperty("webdriver.chrome.driver", resourceFolder+os+"/chromedriver");
	    	 }
			 ChromeOptions options = new ChromeOptions();
			 //options.addArguments("enable-automation");
			 // Esto de abajo oculta la barra que indica que es una prueba automatizada
			 options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});


			 boolean headless = Boolean.parseBoolean(CreateDriver.defaultHeadLess());
			 if (headless == true) {
			  options.addArguments("--headless");
			 }
			 //options.addArguments("--window-size=1920,1080");
			 options.addArguments("--start-maximized");
			 options.addArguments("--no-sandbox");
			 options.addArguments("--disable-extensions");
			 options.addArguments("--dns-prefetch-disable");
			 options.addArguments("--disable-gpu");



			 //options.addArguments("--incognito");
			 options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
			 driver2 = new ChromeDriver(options);
	     }

		 /******** The driver selected is Internet Explorer ********/
	     else if ("INTERNET EXPLORER".equalsIgnoreCase(browser)) {
	    	 System.setProperty("webdriver.ie.driver", resourceFolder+os+"/IEDriverServer.exe");
			 driver = new InternetExplorerDriver();

	     }

		 else if ("EDGE".equalsIgnoreCase(browser)) {
			 System.setProperty("webdriver.ie.driver", resourceFolder+os+"/msedgedriver.exe");
			 driver = new InternetExplorerDriver();

		 }

		 else if ("OPERA".equalsIgnoreCase(browser)) {
			 System.setProperty("webdriver.ie.driver", resourceFolder+os+"/operadriver.exe");
			 driver = new InternetExplorerDriver();

		 }

		 /******** The driver is not selected  ********/
	     else {
	    	 log.error("The Driver is not selected properly, invalid name: " + browser + ", " + os);
			 return null;
		 }

		 if("CHROME".equalsIgnoreCase(browser)){



			 return driver2;
		 } else {
			 driver.manage().window().maximize();
			 return driver;
		 }


        }

}
