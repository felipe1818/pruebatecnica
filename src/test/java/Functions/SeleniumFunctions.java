package Functions;

import StepDefinitions.Hooks;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

public class SeleniumFunctions {

    static WebDriver driver;
    public static Properties prop = new Properties();
    public static InputStream in = SeleniumFunctions.class.getResourceAsStream("../test.properties");
    public static Map<String, String> ScenaryData = new HashMap<>();
    public static Map<String, String> ScenaryObtained = new HashMap<>();
    public static Map<String, String> HandleMyWindows = new HashMap<>();
    public static Map<String, byte[]> StepScreenShots = new HashMap<String, byte[]>();
    public static Map<String, Integer> apisFailed = new HashMap<>();


    public SeleniumFunctions() {
        driver = Hooks.driver;
    }

    public static String Environment = "";

    public String ElementText = "";

    public static final Duration EXPLICIT_TIMEOUT = Duration.ofSeconds(60);
    public static final Duration EXPLICIT_TIMETWO = Duration.ofSeconds(2);
    public static final Duration EXPLICIT_TIMEOUTMAX = Duration.ofSeconds(180);

    public static boolean isDisplayed = Boolean.parseBoolean(null);
    public static boolean isEnable = Boolean.parseBoolean(null);

    /******** Scenario Attributes ********/
    Scenario scenario = null;

    public void scenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public static String readProperties(String property) throws IOException {
        prop.load(in);
        return prop.getProperty(property);
    }



    /******** Log Attribute ********/
    private static Logger log = Logger.getLogger(SeleniumFunctions.class);

    /******** Page Path ********/
    public static String FileName = "";
    public static String PagesFilePath = "src/test/resources/Pages/";

    public static String GetFieldBy = "";
    public static String ValueToFind = "";


    String getCurrentDirectory() {
        return this.getClass().getClassLoader().getResource("").getPath();
    }

    public static Object readJson() throws Exception {
        FileReader reader = new FileReader(PagesFilePath + FileName);
        try {

            if (reader != null) {
                JSONParser jsonParser = new JSONParser();
                return jsonParser.parse(reader);
            } else {
                return null;
            }
        } catch (FileNotFoundException | NullPointerException e) {
            log.error("ReadEntity: No existe el archivo " + FileName);
            throw new IllegalStateException("ReadEntity: No existe el archivo " + FileName, e);
        }

    }

    public static JSONObject ReadEntity(String element) throws Exception {
        JSONObject Entity = null;

        JSONObject jsonObject = (JSONObject) readJson();
        try {
            Entity = (JSONObject) jsonObject.get(element);
            log.info(Entity.toJSONString());
        } catch (NullPointerException e) {
            log.info("El elemento " + element + " no existe en el JSON");
        }


        return Entity;

    }

    public static By getCompleteElement(String element) throws Exception {
        By result = null;

        try {
            JSONObject Entity = ReadEntity(element);
            GetFieldBy = (String) Entity.get("GetFieldBy");
            ValueToFind = (String) Entity.get("ValueToFind");
        } catch (NullPointerException e) {
            // aqui coloco que si no encuentra el elemento en el JSON, entonces lo busque como un localizador xpath.
            log.info("Se utilizo  " + element + " como un localizador Xpath");
            GetFieldBy = "Xpath";
            ValueToFind = element;
        }


        // Aqui implemente que el JSON pueda incluir variables y las lee del properties.
        String ValuetoFinder = ValueToFind;
        int check = ValuetoFinder.indexOf("${");
        String variable = "${}";
        String replaceVariable = "";
        if (check > -1) {
            Integer ini = ValuetoFinder.indexOf("${");
            Integer fin = ValuetoFinder.indexOf("}");
            variable = ValuetoFinder.substring(ini, fin + 1);

            // creo una variable para guardar la variable sin ${} y leer del properties
            String variable2 = ValuetoFinder.substring(ini, fin);
            String variableProp = variable2.substring(2);
            //  fin variable para leer del properties


            Environment = CreateDriver.defaultAmbiente();

            replaceVariable = ScenaryData.get(variableProp);
        }
        String ValueToFind = ValuetoFinder.replace(variable, replaceVariable);
        // Fin modificación implementación de variables en JSON.

        // log.info(ValueToFind);
        if ("className".equalsIgnoreCase(GetFieldBy)) {
            result = By.className(ValueToFind);
        } else if ("cssSelector".equalsIgnoreCase(GetFieldBy)) {
            result = By.cssSelector(ValueToFind);
        } else if ("id".equalsIgnoreCase(GetFieldBy)) {
            result = By.id(ValueToFind);
        } else if ("linkText".equalsIgnoreCase(GetFieldBy)) {
            result = By.linkText(ValueToFind);
        } else if ("name".equalsIgnoreCase(GetFieldBy)) {
            result = By.name(ValueToFind);
        } else if ("link".equalsIgnoreCase(GetFieldBy)) {
            result = By.partialLinkText(ValueToFind);
        } else if ("tagName".equalsIgnoreCase(GetFieldBy)) {
            result = By.tagName(ValueToFind);
        } else if ("xpath".equalsIgnoreCase(GetFieldBy)) {
            result = By.xpath(ValueToFind);
        }

        log.info(result);
        return result;
    }

    public static String readPropertieOfEnvironment(String propertie) {

        boolean exist = ScenaryData.containsKey(propertie);
        if (exist) {
            String url = ScenaryData.get(propertie);
            return url;
        } else {
            Assert.assertTrue(String.format("The given key %s do not exist in Context"), ScenaryData.containsKey(propertie));
            return "No se encuentra la propiedad";
        }
    }

    public void RetriveTestData(String parameter) throws Exception {
        // DEPRECATED, se crea uno nuevo que hace un foreach por el properties.
        Environment = CreateDriver.defaultAmbiente();
        try {
            SaveInScenarioVar(parameter, readProperties(parameter + "." + Environment));
            System.out.println("Este es el valor de la prop " + parameter + ": " + this.ScenaryData.get(parameter));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void RetriveTestDatas() throws Exception {
        Environment = CreateDriver.defaultAmbiente();
        prop.load(in);
        System.out.println("Se obtienen del properties las variables del ambiente "+Environment);
        prop.keySet().forEach(x -> {
            String propiedad = x.toString();
            if(propiedad.contains(Environment)){
                try {
                    int mid = propiedad.indexOf(".");
                    String key = propiedad.substring(0,mid);
                    SaveInScenarioVar(key, readProperties(propiedad));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public void ScreenShot(String TestCaptura) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
        String screenShotName = readProperties("ScreenShotPath") + "\\" + CreateDriver.defaultBrowser() + "\\" + TestCaptura + "_(" + dateFormat.format(GregorianCalendar.getInstance().getTime()) + ")";
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        log.info("Screenshot saved as:" + screenShotName);
        FileUtils.copyFile(scrFile, new File(String.format("%s.png", screenShotName)));
    }


    public byte[] attachScreenShot() throws IOException {
        log.info("Attaching Screenshot");
        // Nombre que se le dara a la foto.
        long numer = System.currentTimeMillis();
        DateFormat simple = new SimpleDateFormat("dd-MMM-yyyy-HH-mm-ss");
        Date result = new Date(numer);
        long namedate = System.currentTimeMillis();
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        this.StepScreenShots.put(simple.format(result), screenshot);
        return screenshot;
    }
    public byte[] attachScreenShotWithData() throws IOException {
        // ESTO AGREGA EL URL DEL AMBIENTE ENCIMA DE LA FOTO.
        String url = driver.getCurrentUrl();

        log.info("Attaching Screenshot");
        // Nombre que se le dara a la foto.
        long numer = System.currentTimeMillis();
        DateFormat simple = new SimpleDateFormat("dd-MMM-yyyy-HH-mm-ss");
        Date result = new Date(numer);
        long namedate = System.currentTimeMillis();
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        this.StepScreenShots.put(url+" "+simple.format(result), screenshot);
        return screenshot;
    }

    public static byte[] getByteScreenshot() throws IOException
    {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        byte[] fileContent = FileUtils.readFileToByteArray(src);
        return fileContent;
    }


    public void attachText(String text) {

        log.info("Attaching Text " + text);
        Allure.addAttachment("Texto Adjunto", text);
    }

    public void attachKey(String key) {

        boolean exist = this.ScenaryData.containsKey(key);
        if (exist) {
            String text = this.ScenaryData.get(key);
            log.info(String.format("Attaching Key Value " + key + " with value: " + text));
            Allure.addAttachment("Variable Adjunta " + key, text);

        } else {
            System.out.println("No se encontro la variable para adjuntarla");
        }


    }


    public void removeKey(String key) {

        boolean exist = this.ScenaryData.containsKey(key);
        if (exist) {
            String text = this.ScenaryData.get(key);
            log.info(String.format("Removing Key Value " + key + " with value: " + text));
            this.ScenaryData.remove(key);
        } else {
            System.out.println("No se encontro la variable para removerla");
        }


    }

    public void ifElementNotDisplayedClickto(String element, String element2) throws Exception {

        log.info("Verifing if element is present");
        Boolean result = isElementDisplayed(element, EXPLICIT_TIMETWO);
        if (result == true) {
            log.info("The element is present.");
        } else {
            log.info("Element is not present, clicking to second element");
            iClicInElement(element2);
            log.info("Second element clicked");
        }

    }

    public void ClickToElseClickFirstTo(String element, String element2) throws Exception {

        log.info("Verifing if element is present");
        Boolean result = isElementDisplayed(element, EXPLICIT_TIMETWO);
        if (result == true) {
            log.info("The element One is present. Clicking");
            iClicInElement(element);
            log.info("One element clicked");
        } else {
            log.info("Element is not present, clicking to second element");
            iClicInElement(element2);
            iClicInElement(element);
            log.info("Second element clicked");
        }

    }

    public boolean isElementDisplayed(String element, Duration time) throws Exception {

        try {
            By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
            log.info(String.format("Waiting Element: %s", element + " of " + ValueToFind));
            WebDriverWait wait = new WebDriverWait(driver, time);
            isDisplayed = wait.until(ExpectedConditions.presenceOfElementLocated(SeleniumElement)).isDisplayed();
        } catch (NoSuchElementException | TimeoutException e) {
            isDisplayed = false;
            log.info(e);
        }
        log.info(String.format("%s visibility is: %s", element + " of " + ValueToFind, isDisplayed));
        return isDisplayed;
    }

    public boolean isElementEnable(String element) throws Exception {

        try {
            By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
            log.info(String.format("Waiting Element: %s", element + " of " + ValueToFind));
            WebDriverWait wait = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
            isEnable = wait.until(ExpectedConditions.presenceOfElementLocated(SeleniumElement)).isEnabled();
        } catch (NoSuchElementException | TimeoutException e) {
            isEnable = false;
            log.info(e);
        }
        log.info(String.format("%s enable is: %s", element + " of " + ValueToFind, isEnable));
        return isEnable;
    }

    public void AcceptAlert() {

        try {
            WebDriverWait wait = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            log.info("The alert was accepted successfully.");
        } catch (Throwable e) {
            log.error("Error came while waiting for the alert popup. " + e.getMessage());
        }
    }

    public void dismissAlert() {

        try {
            WebDriverWait wait = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.dismiss();
            log.info("The alert was dismissed successfully.");
        } catch (Throwable e) {
            log.error("Error came while waiting for the alert popup. " + e.getMessage());
        }
    }

    public void selectOptionDropdownByIndex(String element, int option) throws Exception {

        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        log.info(String.format("Waiting Element: %s", element));

        Select opt = new Select(driver.findElement(SeleniumElement));
        log.info("Select option: " + option + "by text");
        opt.selectByIndex(option);
    }

    public void selectOptionDropdownByText(String element, String option) throws Exception {

        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        log.info(String.format("Waiting Element: %s", element + " of " + ValueToFind));

        Select opt = new Select(driver.findElement(SeleniumElement));
        log.info("Select option: " + option + "by text");
        opt.selectByVisibleText(option);
    }

    public void selectOptionDropdownByValue(String element, String option) throws Exception {

        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        log.info(String.format("Waiting Element: %s", element));

        Select opt = new Select(driver.findElement(SeleniumElement));
        log.info("Select option: " + option + "by text");
        opt.selectByValue(option);
    }

    public void checkCheckbox(String element) throws Exception {

        try {
            By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
            boolean isChecked = driver.findElement(SeleniumElement).isSelected();
            if (!isChecked) {
                log.info("Clicking on the checkbox to select: " + element + " of " + ValueToFind);
                driver.findElement(SeleniumElement).click();
            }
        } catch (ElementClickInterceptedException e) {
            log.info("Element " + element + " is not clickeable in this moment, trying again, second once...");
            int seconds = 5000;
            Thread.sleep(seconds);
            By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
            boolean isChecked = driver.findElement(SeleniumElement).isSelected();
            if (!isChecked) {
                log.info("Clicking on the checkbox to select: " + element + " of " + ValueToFind);
                driver.findElement(SeleniumElement).click();
            }
        }
    }

    public void clickCheckboxListContains(String text) throws Exception {
        String element = "//li[contains(.,'" + text + "')]//span[contains(@class, 'ui-chkbox-icon ui-clickable pi pi-check')]/..";
        try {
            int count = countElement(element);
            if (count < 1) {
                element = "//li[contains(.,'" + text + "')]//span[contains(@class, 'ui-chkbox-icon ui-clickable')]/..";
                By SeleniumElement = By.xpath(element);
                log.info("Clicking on the checkbox to select: " + text + " of " + ValueToFind);
                driver.findElement(SeleniumElement).click();
            } else {
                log.info("Checkbox already been checked.");
            }
        } catch (ElementClickInterceptedException e) {
            log.info("Element " + text + " is not clickeable in this moment, trying again, second once...");
            Thread.sleep(5000);
            int count = countElement(element);
            if (count < 1) {
                element = "//li[contains(.,'" + text + "')]//span[contains(@class, 'ui-chkbox-icon ui-clickable')]/..";
                By SeleniumElement = By.xpath(element);
                log.info("Clicking on the checkbox to select: " + text + " of " + ValueToFind);
                driver.findElement(SeleniumElement).click();
            } else {
                log.info("Checkbox already been checked.");
            }
        }
    }

    public void iClickCheckboxContains(String text) throws Exception {
        String element = "//p-checkbox[contains(.,'" + text + "')]//span[@class='ui-chkbox-icon ui-clickable pi pi-check']/..";
        try {
            int count = countElement(element);
            if (count < 1) {
                element = "//p-checkbox[contains(.,'" + text + "')]//span[@class='ui-chkbox-icon ui-clickable']/..|//p-multiselectitem[contains(.,'" + text + "')]//span[@class='ui-chkbox-icon ui-clickable']/..";
                By SeleniumElement = By.xpath(element);
                log.info("Clicking on the checkbox to select: " + text + " of " + ValueToFind);
                driver.findElement(SeleniumElement).click();
            } else {
                log.info("Checkbox already been checked.");
            }
        } catch (ElementClickInterceptedException e) {
            log.info("Element " + text + " is not clickeable in this moment, trying again, second once...");
            Thread.sleep(5000);
            int count = countElement(element);
            if (count < 1) {
                element = "//p-checkbox[contains(.,'" + text + "')]//span[@class='ui-chkbox-icon ui-clickable']/..|//p-multiselectitem[contains(.,'" + text + "')]//span[@class='ui-chkbox-icon ui-clickable']/..";
                By SeleniumElement = By.xpath(element);
                log.info("Clicking on the checkbox to select: " + text + " of " + ValueToFind);
                driver.findElement(SeleniumElement).click();
            } else {
                log.info("Checkbox already been checked.");
            }
        }
    }

    public void iClickRadiobuttonContains(String text) throws Exception {
        String element = "//label[contains(.,'" + text + "')]/../p-radiobutton//span[@class='ui-radiobutton-icon ui-clickable pi pi-circle-on']/..|//label[contains(.,'" + text + "')]/../../p-radiobutton//span[@class='ui-radiobutton-icon ui-clickable pi pi-circle-on']/..";
        try {
            int count = countElement(element);
            if (count < 1) {
                element = "//label[contains(.,'" + text + "')]/../p-radiobutton//span[@class='ui-radiobutton-icon ui-clickable']/..|//label[contains(.,'" + text + "')]/../../p-radiobutton//span[@class='ui-radiobutton-icon ui-clickable']/..";
                By SeleniumElement = By.xpath(element);
                log.info("Clicking on the radio to select: " + text + " of " + ValueToFind);
                driver.findElement(SeleniumElement).click();
            } else {
                log.info("Checkbox already been checked.");
            }
        } catch (ElementClickInterceptedException e) {
            log.info("Element " + text + " is not clickeable in this moment, trying again, second once...");
            Thread.sleep(5000);
            int count = countElement(element);
            if (count < 1) {
                element = "//label[contains(.,'" + text + "')]/../p-radiobutton//span[@class='ui-radiobutton-icon ui-clickable']/..|//label[contains(.,'" + text + "')]/../../p-radiobutton//span[@class='ui-radiobutton-icon ui-clickable']/..";
                By SeleniumElement = By.xpath(element);
                log.info("Clicking on the radio to select: " + text + " of " + ValueToFind);
                driver.findElement(SeleniumElement).click();
            } else {
                log.info("Checkbox already been checked.");
            }
        }
    }

    public void UncheckCheckbox(String element) throws Exception {

        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        boolean isChecked = driver.findElement(SeleniumElement).isSelected();
        if (isChecked) {
            log.info("Clicking on the checkbox to select: " + element + " of " + ValueToFind);
            driver.findElement(SeleniumElement).click();
        }
    }

    public void scrollToElement(String element) throws Exception {

        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        log.info("Scrolling to element: " + element + " of " + ValueToFind);
        jse.executeScript("arguments[0].scrollIntoView(true);" + "window.scrollBy(0,-100);", driver.findElement(SeleniumElement));
        Thread.sleep(1000);
    }

    public void scrollToHorizontal(String element) throws Exception {

        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        log.info("Scrolling Horizontal to element: " + element + " of " + ValueToFind);
        jse.executeScript("arguments[0].scrollIntoView()", driver.findElement(SeleniumElement));
        Thread.sleep(1000);
    }

    public void ClickJSElement(String element) throws Exception {


        try {
            By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            log.info("Click on JS element by: " + element + " of " + ValueToFind);
            jse.executeScript("arguments[0].click()", driver.findElement(SeleniumElement));
        } catch (ElementClickInterceptedException | StaleElementReferenceException | NoSuchElementException e) {
            log.info("Element " + element + " of " + ValueToFind + " is not clickeable in this moment, trying again...");
            int secs = 5000;
            Thread.sleep(secs);

            By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            log.info("Click on JS element by: " + element + " of " + ValueToFind);
            jse.executeScript("arguments[0].click()", driver.findElement(SeleniumElement));
        } catch (ElementNotInteractableException e) {
            log.info("Element " + element + " of " + ValueToFind + " is not clickeable in this moment, trying again...");
            int secs = 5000;
            Thread.sleep(secs);

            try {
                By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
                JavascriptExecutor jse = (JavascriptExecutor) driver;
                log.info("Click on JS element by: " + element + " of " + ValueToFind);
                jse.executeScript("arguments[0].click()", driver.findElement(SeleniumElement));
            } catch (ElementNotInteractableException r) {
                log.info("Element " + element + " of " + ValueToFind + " is not clickeable in this moment, trying again, second once...");
                int seconds = 8000;
                Thread.sleep(seconds);

                By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
                JavascriptExecutor jse = (JavascriptExecutor) driver;
                log.info("Click on JS element by: " + element + " of " + ValueToFind);
                jse.executeScript("arguments[0].click()", driver.findElement(SeleniumElement));
            }

        }

    }

    public void OpenNewTabWithURL(String URL) {

        log.info("Open New tab with URL: " + URL);
        System.out.println("Open New tab with URL: " + URL);
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript(String.format("window.open('%s','_blank');", URL));

    }


    public void checkPartialTextElementNotPresent(String element, String text) throws Exception {
        ElementText = GetTextElement(element);

        boolean isFoundFalse = ElementText.indexOf(text) != -1 ? true : false;
        attachScreenShot();
        Assert.assertFalse("Text is present in element: " + element + " current text is: " + ElementText, isFoundFalse);

    }

    public void checkPartialTextElementPresent(String element, String text) throws Exception {

        ElementText = GetTextElement(element);

        boolean isFound = ElementText.indexOf(text) != -1 ? true : false;
        attachScreenShot();
        Assert.assertTrue("Text is not present in element: " + element + " current text is: " + ElementText, isFound);

    }

    public void checkTextElementEqualTo(String element, String text) throws Exception {

        ElementText = GetTextElement(element);

        Assert.assertEquals("Text is not present in element: " + element + " current text is: " + ElementText, text, ElementText);

    }

    public void iWaitTime(int time) throws InterruptedException {
        log.info("Waiting for " + time + " seconds..");
        Thread.sleep(time * 1000);

    }

    public void checkTextElementEqualToKey(String element, String key) throws Exception {

        ElementText = GetTextElement(element);

        boolean exist = this.ScenaryData.containsKey(key);
        if (exist) {
            String text = this.ScenaryData.get(key);
            log.info(String.format("Comparing %s with text key value %s", element + " of " + ValueToFind, text));
            Assert.assertEquals("Text is not present in element: " + element + " current text is: " + ElementText, text, ElementText);

        } else {
            Assert.assertTrue(String.format("The given key %s do not exist in Context", key), this.ScenaryData.containsKey(key));
        }


    }


    public String GetTextElement(String element) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebDriverWait wait = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
        wait.until(ExpectedConditions.presenceOfElementLocated(SeleniumElement));
        log.info(String.format("Esperando el elemento: %s", element + " of " + ValueToFind));

        ElementText = driver.findElement(SeleniumElement).getText();

        if (ElementText.isEmpty()) {
            log.info(String.format("Texto vacio se intenta buscar el valor del elemento"));
            ElementText = driver.findElement(SeleniumElement).getAttribute("value");
        }

        log.info(String.format("El texto del elemento es: " + ElementText));

        return ElementText;

    }

    public void iSetElementWithText(String element, String text) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        driver.findElement(SeleniumElement).sendKeys(text);
        log.info(String.format("Set on element %s with text %s", element + " of " + ValueToFind, text));
    }

    public void iSendKeyEnterToElement(String element) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        driver.findElement(SeleniumElement).sendKeys(Keys.ENTER);
        log.info(String.format("Send Key Enter on element %s", element + " of " + ValueToFind));
    }

    public void iSendClearToElement(String element) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        driver.findElement(SeleniumElement).clear();
        log.info(String.format("Send Clear on element %s", element + " of " + ValueToFind));
    }

    public void iSendKeyEscapeToPerform() throws Exception {

        Actions action = new Actions(driver);
        action.sendKeys(Keys.ESCAPE).build().perform();

        log.info(String.format("Send Key Escape on element "));
    }

    public void iSetElementWithKeyValue(String element, String key) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        boolean exist = this.ScenaryData.containsKey(key);
        if (exist) {
            String text = this.ScenaryData.get(key);
            driver.findElement(SeleniumElement).sendKeys(text);
            log.info(String.format("Set on element %s with text %s", element + " of " + ValueToFind, text));
        } else {
            Assert.assertTrue(String.format("The given key %s do not exist in Context", key), this.ScenaryData.containsKey(key));
        }

    }


    public void doubleClick(String element) throws Exception {
        Actions action = new Actions(driver);
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        action.moveToElement(driver.findElement(SeleniumElement)).doubleClick().perform();
        log.info("Double click on element: " + element + " of " + ValueToFind);
    }

    public void iClicInElement(String element) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);

        for (int retry = 0; retry < 3; retry++) {
            try {
                driver.findElement(SeleniumElement).click();
                log.info("Click on element by " + element + " of " + ValueToFind);
                retry = 3;
            } catch (Exception e) {
                log.info("Element " + element + " is not clickeable in this moment, trying "+(retry+2)+" once...");
                Thread.sleep(5000*(retry+2));

                if(retry==2){
                    driver.findElement(SeleniumElement).click();
                }
            }
        }

    }


    public void iSelectContainsText(String element, String text) throws Exception {

        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        System.out.println(SeleniumElement.toString());
        String ValuetoFinder = SeleniumElement.toString();
        int check = ValuetoFinder.indexOf("[contains(.,'')]");
        String elementEdited = "";
        elementEdited = ValuetoFinder.replace("By.xpath: ","");
        if (check > -1) {
            elementEdited = elementEdited.replace("contains(.,'')","contains(.,'"+text+"')");

            System.out.println("El elemento tenia contains se reemplaza: "+elementEdited);
        } else{
            elementEdited = elementEdited+"[contains(.,'"+text+"')]";
            System.out.println("El elemento no tenia el contains sea agrego: "+elementEdited);
        }

        iClicInElement(elementEdited);
    }


    public void iSelectContainsKey(String element, String key) throws Exception {

        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);

        String text = ScenaryData.get(key);

        System.out.println(SeleniumElement.toString());
        String ValuetoFinder = SeleniumElement.toString();
        int check = ValuetoFinder.indexOf("[contains(.,'')]");
        String elementEdited = "";
        elementEdited = ValuetoFinder.replace("By.xpath: ","");
        if (check > -1) {
            elementEdited = elementEdited.replace("contains(.,'')","contains(.,'"+text+"')");

            System.out.println("El elemento tenia contains se reemplaza: "+elementEdited);
        } else{
            elementEdited = elementEdited+"[contains(.,'"+text+"')]";
            System.out.println("El elemento no tenia el contains sea agrego al final: "+elementEdited);
        }

        iClicInElement(elementEdited);
    }


    public void scrollPage(String to) throws Exception {

        JavascriptExecutor jse = (JavascriptExecutor) driver;
        if (to.equals("top")) {
            log.info("Scrolling to the top of the page");
            jse.executeScript("scroll(0, -250);");
            Thread.sleep(500);

        } else if (to.equals("end")) {
            log.info("Scrolling to the end of the page");
            jse.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(500);
        } else {
            int px = Integer.parseInt(to);
            log.info("Scrolling "+ to +" px down in the page");
            jse.executeScript("scroll(0, "+px+");");
            Thread.sleep(500);
        }
    }

    public void scrollAllPageAndTakeScreenShots() throws Exception {

        JavascriptExecutor jse = (JavascriptExecutor) driver;

        log.info("Scrolling to the end of the page");
        jse.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Object size = jse.executeScript("return document.body.scrollHeight");
        jse.executeScript("scroll(0, -250);");

        int veces = (Integer.parseInt(String.valueOf(size))/500);
        int pixeles = 0;
        for(int i = 0; i < veces; i++){
            jse.executeScript("scroll(0, "+pixeles+");");
            Thread.sleep(500);
            attachScreenShot();
            System.out.println("Se toma pantallazo: "+i);
            pixeles += 500;
        }





    }

    public void zoomTillElementDisplay(String element) throws Exception {

        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebElement html = driver.findElement(SeleniumElement);
        html.sendKeys(Keys.chord(Keys.CONTROL, "0"));
    }

    public void switchToFrame(String Frame) throws Exception {
        waitForElementPresent(Frame);
        By SeleniumElement = SeleniumFunctions.getCompleteElement(Frame);
        WebDriverWait w = new WebDriverWait(driver, EXPLICIT_TIMEOUTMAX);
        log.info("Waiting for the element: " + Frame + " to be present");
        w.until(ExpectedConditions.presenceOfElementLocated(SeleniumElement));
        log.info("Switching to frame: " + Frame);
        driver.switchTo().frame(driver.findElement(SeleniumElement));

    }

    public void switchToParentFrame() {

        log.info("Switching to parent frame");
        driver.switchTo().parentFrame();

    }

    public void waitForElementPresent(String element) throws Exception {

        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebDriverWait w = new WebDriverWait(driver, EXPLICIT_TIMEOUTMAX);
        log.info("Waiting for the element: " + element + " of " + ValueToFind + " to be present");
        w.until(ExpectedConditions.presenceOfElementLocated(SeleniumElement));
    }

    public void waitForElementPresent(String element, int seconds) throws Exception {

        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        log.info("Waiting for the element: " + element + " of " + ValueToFind + " to be present");
        w.until(ExpectedConditions.presenceOfElementLocated(SeleniumElement));
    }

    public void waitForElementNotPresent(String element) throws Exception {

        try {
            By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
            WebDriverWait w = new WebDriverWait(driver, EXPLICIT_TIMEOUTMAX);
            log.info("Waiting for the element: " + element + " of " + ValueToFind + " to be not present");
            WebElement elemento = driver.findElement((SeleniumElement));
            w.until(ExpectedConditions.stalenessOf(elemento));
        } catch (NoSuchElementException e) {
            log.info("Never was element: " + element + " of " + ValueToFind + " present");
        }

    }

    public void waitForElementNotPresent(String element, int seconds) throws Exception {

        try {
            By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(seconds));
            log.info("Waiting for the element: " + element + " of " + ValueToFind + " to be not present in "+seconds+" seconds.");
            WebElement elemento = driver.findElement((SeleniumElement));
            w.until(ExpectedConditions.stalenessOf(elemento));
        } catch (NoSuchElementException e) {
            log.info("Never was element: " + element + " of " + ValueToFind + " present");
        }

    }

    public void waitForElementPresentElseClick(String element, String element2) throws Exception {

        try {
            By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
            WebDriverWait w = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
            log.info("Waiting for the element: " + element + " of " + ValueToFind + " to be present");
            w.until(ExpectedConditions.presenceOfElementLocated(SeleniumElement));
        } catch (TimeoutException | NoSuchElementException e) {
            log.info("Element never present. It will do click to element " + element2 + " of " + ValueToFind);
            By SeleniumElement = SeleniumFunctions.getCompleteElement(element2);


            driver.findElement(SeleniumElement).click();
            log.info("Click on element by " + element2 + " of " + ValueToFind);
        }


    }

    public void waitForElementNotPresentElseClick(String element, String element2) throws Exception {
        try {
            By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
            WebDriverWait w = new WebDriverWait(driver, EXPLICIT_TIMEOUTMAX);
            log.info("Waiting for the element: " + element + " of " + ValueToFind + " to be not present");
            WebElement elemento = driver.findElement((SeleniumElement));
            w.until(ExpectedConditions.stalenessOf(elemento));
        } catch (TimeoutException | NoSuchElementException e) {
            log.info("Element never not present. It will do click to element " + element2 + " of " + ValueToFind);
            By SeleniumElement = SeleniumFunctions.getCompleteElement(element2);
            log.info("Click on element by " + element2 + " of " + ValueToFind);
            driver.findElement(SeleniumElement).click();

        }


    }

    public void waitForElementVisible(String element) throws Exception {

        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebDriverWait w = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
        log.info("Waiting for the element: " + element + " of " + ValueToFind + " to be visible");
        w.until(ExpectedConditions.visibilityOfElementLocated(SeleniumElement));
    }

    public void page_has_loaded() {
        String GetActual = driver.getCurrentUrl();
        System.out.println(String.format("Checking if %s page is loaded.", GetActual));
        log.info(String.format("Checking if %s page is loaded.", GetActual));
        new WebDriverWait(driver, EXPLICIT_TIMEOUT).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    public void WindowsHandle(String WindowsName) throws Exception {
        // I do clic in the element that open the new windows.
        attachScreenShot();
        //  I check if the var exits to remove it.
        if (this.HandleMyWindows.containsKey(WindowsName)) {
            HandleMyWindows.remove("Hotmail");
            log.info(String.format("Already exist the windows %s, cleaning var", WindowsName));
        }

        String principal = this.HandleMyWindows.get("Principal");

        for (String winHandle : driver.getWindowHandles()) {
            this.HandleMyWindows.put(WindowsName, winHandle);
            log.info("The New window " + WindowsName + " is saved in scenario with value " + this.HandleMyWindows.get(WindowsName));
        }
        String winHandle = this.HandleMyWindows.get(WindowsName);
        //  I try three times
        for (int retry = 0; retry < 4; retry++) {
            // If session id is equal to id of principal tab, wait three times.
            if(principal.equals(winHandle)){
                iWaitTime(5);
                log.info("No found the new windows, waiting for try again.");
            } else {
                // if session id is different to principal tab, save the session id and switch.
                retry = 4;
                this.HandleMyWindows.put(WindowsName, winHandle);
                log.info("The New window " + WindowsName + " is saved in scenario with value " + this.HandleMyWindows.get(WindowsName));
                driver.switchTo().window(winHandle);
            }
        }
    }


    public void SaveInScenarioVar(String key, String element) throws Exception {

        String text = element;

        if (!this.ScenaryData.containsKey(key)) {
            this.ScenaryData.put(key, text);
            log.info(String.format("Save as Scenario Context key: %s with value: %s ", key, text));
        } else {
            this.ScenaryData.replace(key, text);
            log.info(String.format("Update Scenario Context key: %s with value: %s ", key, text));
        }

    }


    public void iSaveTextOfElementInScenario(String key, String element) throws Exception {

        String elementText = GetTextElement(element);

        if (!this.ScenaryData.containsKey(key)) {
            this.ScenaryData.put(key, elementText);
            log.info(String.format("Save as Scenario Context key: %s with value: %s ", key, elementText));
        } else {
            this.ScenaryData.replace(key, elementText);
            log.info(String.format("Update Scenario Context key: %s with value: %s ", key, elementText));
        }

        // Creo una nueva lista para solo guardar los datos obtenidos de las pantallas

        if (!this.ScenaryObtained.containsKey(key)) {
            this.ScenaryObtained.put(key, elementText);
        } else {
            this.ScenaryObtained.replace(key, elementText);
        }

        attachKey(key);

    }


    public void iSaveTextInScenario(String key, String text) throws Exception {


        if (!this.ScenaryData.containsKey(key)) {
            this.ScenaryData.put(key, text);
            log.info(String.format("Save as Scenario Context key: %s with value: %s ", key, text));
        } else {
            this.ScenaryData.replace(key, text);
            log.info(String.format("Update Scenario Context key: %s with value: %s ", key, text));
        }
        attachKey(key);
    }


    public void iModifyKey(String key, int size) throws Exception {

        boolean exist = this.ScenaryData.containsKey(key);
        if (exist) {
            String text1 = this.ScenaryData.get(key);
            String text = text1.substring(size);
            log.info("Key " + key + " modified, new value is: " + text);
            this.ScenaryData.replace(key, text);

        } else {
            Assert.assertTrue(String.format("The given key %s do not exist in Context", key), this.ScenaryData.containsKey(key));
        }

    }

    public void iExtractRightOfKey(String key, int size) throws Exception {
        boolean exist = this.ScenaryData.containsKey(key);
        if (exist) {
            String text1 = this.ScenaryData.get(key);
            int fullSize = text1.length();
            int newsize = fullSize - size;
            String text = text1.substring(newsize, fullSize);
            log.info("Key " + key + " modified, new value is: " + text);
            this.ScenaryData.replace(key, text);
            this.ScenaryObtained.replace(key, text);
            attachKey(key);
        } else {
            Assert.assertTrue(String.format("The given key %s do not exist in Context", key), this.ScenaryData.containsKey(key));
        }

    }

    public void iExtractOfKey(String key, String inicio, String fin) throws Exception {


        boolean exist = this.ScenaryData.containsKey(key);
        if (exist) {
            String text1 = this.ScenaryData.get(key);

            int ini = text1.indexOf(inicio) + inicio.length();
            int fn = text1.indexOf(fin);

            String text = text1.substring(ini, fn);
            log.info("Key " + key + " modified, new value is: " + text);
            this.ScenaryData.replace(key, text);
            this.ScenaryObtained.replace(key, text);
            attachKey(key);
        } else {
            Assert.assertTrue(String.format("The given key %s do not exist in Context", key), this.ScenaryData.containsKey(key));
        }

    }


    public void iExtractOfKey(String key, int inicio, int fin) throws Exception {

        boolean exist = this.ScenaryData.containsKey(key);
        if (exist) {
            String text1 = this.ScenaryData.get(key);

            String text = text1.substring(inicio, fin);
            log.info("Key " + key + " modified, new value is: " + text);
            this.ScenaryData.replace(key, text);
            this.ScenaryObtained.replace(key, text);
            attachKey(key);
        } else {
            Assert.assertTrue(String.format("The given key %s do not exist in Context", key), this.ScenaryData.containsKey(key));
        }

    }


    public void saveInScenarioRandomText(String key) throws IOException {

        long numer = System.currentTimeMillis();

        String fileName = ("Prueba-" + numer);

        if (!this.ScenaryData.containsKey(key)) {

            this.ScenaryData.put(key, fileName);

            log.info(String.format("Se creo una variable %s con el texto: %s ", key, fileName));
        } else {

            this.ScenaryData.replace(key, fileName);
            log.info(String.format("Se actualizo una variable una variable %s con el texto: %s ", key, fileName));

        }

    }

    public void copyKeytoKey (String key, String newKey)  {

        boolean exist = this.ScenaryData.containsKey(key);

        if (exist) {
            System.out.println("entro al if");
            String text = this.ScenaryData.get(key);

            if (!this.ScenaryData.containsKey(newKey)) {
                this.ScenaryData.put(newKey, text);
            } else {
                this.ScenaryData.replace(newKey, text);
            }
            log.info(String.format("Se copio el valor %s de la variable %s a la variable %s ",text, key,newKey));
        }
    }

    public void SaveInScenarioFile(String key, String format) throws IOException {

        String directoryName = new SeleniumFunctions().getCurrentDirectory();

        long numer = System.currentTimeMillis();

        String fileName = ("Prueba-" + numer);
        Path originalPath = Paths.get(("src/test/resources/Files/Base/Prueba." + format + ""));
        Path text = Paths.get("src/test/resources/Files/" + fileName + "." + format + "");

        String ruta0 = directoryName.replace("target/test-classes/", "") + text.toString();
        String ruta1 = ruta0.substring(1);


        Files.copy(originalPath, text, StandardCopyOption.REPLACE_EXISTING);
        String ruta = ruta1.replace('/', '\\');
        if (!this.ScenaryData.containsKey(key)) {

            this.ScenaryData.put(key, ruta);
            this.ScenaryData.put(key + ".name", fileName);

            log.info(String.format("Se creo un archivo en la variable %s en la ruta: %s ", key, text));
        } else {


            this.ScenaryData.replace(key, ruta);

            this.ScenaryData.replace(key + ".name", fileName);
            log.info(String.format("Se actualizo una variable de archivo key: %s with value: %s ", key, text));


        }
        attachKey(key);
    }


    public void readMain(String ambiente) {

        boolean exist = this.ScenaryData.containsKey(ambiente);
        if (exist) {
            String url = this.ScenaryData.get(ambiente);
            log.info("Navigate to: " + url);
            open(url);
            HandleMyWindows.put("Principal", driver.getWindowHandle());
            page_has_loaded();
        } else {
            Assert.assertTrue(String.format("The given key %s do not exist in Context"), this.ScenaryData.containsKey(ambiente));
        }

    }


    public void open(String url) {
        driver.get(url);
    }

    public void checkIfElementIsPresent(String element) throws Exception {

        boolean isDisplayed = isElementDisplayed(element, Duration.ofSeconds(10));
        attachScreenShot();
        Assert.assertTrue("El elemento esperado no esta presente: " + element, isDisplayed);

    }

    public void checkIfElementIsNotPresent(String element) throws Exception {

        boolean isDisplayed = isElementDisplayed(element, Duration.ofSeconds(10));
        attachScreenShot();
        Assert.assertFalse("El elemento no deberia estar presente: " + element, isDisplayed);

    }

    public void checkIfElementIsEnable(String element) throws Exception {


        boolean isEnable = isElementEnable(element);
        attachScreenShot();
        Assert.assertTrue("Element is not enable: " + element, isEnable);
    }
    public void checkIfElementIsDisable(String element) throws Exception {


        boolean isEnable = isElementEnable(element);
        attachScreenShot();
        Assert.assertFalse("El elemento no esta deshabilitado: " + element, isEnable);
    }


    public void navigateTo(String url) {

        log.info("Navigate to: " + url);
        open(url);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        HandleMyWindows.put("Principal", driver.getWindowHandle());
        page_has_loaded();

    }

    public void switchToNewTab(String url, String nameTab) {
        log.info("Navigate to: " + url);
        driver.switchTo().newWindow(WindowType.TAB);
        open(url);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        HandleMyWindows.put(nameTab, driver.getWindowHandle());
        page_has_loaded();
    }

    public void switchToTab(String nameTab) {
        log.info("Switch to tab " + nameTab);
        driver.switchTo().window(HandleMyWindows.get(nameTab));
    }

    public void closeCurrenteTab() {
        log.info("Windows or Tab Closed by command");
        driver.close();
    }

    public void iLoadTheDOMInformation(String file) throws Exception {

        SeleniumFunctions.FileName = file;
        SeleniumFunctions.readJson();
        log.info("initialize file: " + file);

    }


    public void whileElementNotPresentClicTo(String element, String elementWhile) throws Exception {
        // Se dara clic a un elemento A mientras no exista B, cuando exista B se dará clic a B.
        Boolean ordinaria = isElementDisplayed(element, EXPLICIT_TIMETWO);
        while (ordinaria == false) {
            iClicInElement(elementWhile);
            log.info("No se encontro " + element + ", Se dio click al elemento " + elementWhile);
            ordinaria = isElementDisplayed(element, EXPLICIT_TIMETWO);
        }
        iClicInElement(element);
        log.info("Se encontro " + element + ", y se le dio click.");

    }

    public int countElement(String element) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        List<WebElement> links = driver.findElements(SeleniumElement);    //Identify the number of Link on webpage and assign into Webelement List

        int linkCount = links.size();     // Count the total Link list on Web Page

        System.out.println("Total Number of element count on webpage = " + linkCount);    //Print the total count of links on webpage

        return linkCount;
    }

    public void checkCountElement(String element, int cant) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        List<WebElement> links = driver.findElements(SeleniumElement);    //Identify the number of Link on webpage and assign into Webelement List

        int linkCount = links.size();     // Count the total Link list on Web Page

        System.out.println("Total Number of element count on webpage = " + linkCount);    //Print the total count of links on webpage
        scrollToElement(element);
        attachScreenShot();
        Assert.assertTrue("Element count is not equals", linkCount == cant);
    }

    public void iSignObject(String element) throws Exception {
        Actions action = new Actions(driver);
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebElement signatureCanvas = driver.findElement(SeleniumElement);
        action.dragAndDropBy(signatureCanvas, 80, 0).build().perform();
        action.dragAndDropBy(signatureCanvas, 90, 15).build().perform();
        action.dragAndDropBy(signatureCanvas, 100, -40).build().perform();
        action.dragAndDropBy(signatureCanvas, 20, 25).build().perform();
        action.dragAndDropBy(signatureCanvas, -20, 25).build().perform();
        attachScreenShot();
    }

    public void refreshPage() throws Exception {
        driver.navigate().refresh();
    }


}