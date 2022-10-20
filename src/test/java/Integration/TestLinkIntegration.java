package Integration;
import Functions.CreateDriver;
import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIException;
import Functions.SeleniumFunctions;

import java.io.IOException;

public class TestLinkIntegration {




    public static void updateResults(String testCaseName, String exception, String results) throws TestLinkAPIException, IOException {
        String TESTLINK_KEY = SeleniumFunctions.readProperties("TESTLINK_KEY");
        String TESTLINK_URL = SeleniumFunctions.readProperties("TESTLINK_URL");
        String TESTLINK_PROJECT_NAME = CreateDriver.defaultProjectTestLink();
        String TESTLINK_PLAN_NAME = CreateDriver.defaultPlanTestLink();
        String BUILD_NAME = SeleniumFunctions.readProperties("BUILD_NAME");

        TestLinkAPIClient testlink = new TestLinkAPIClient(TESTLINK_KEY,TESTLINK_URL);
        testlink.reportTestCaseResult(TESTLINK_PROJECT_NAME,TESTLINK_PLAN_NAME, testCaseName, BUILD_NAME, exception, results);

    }
}
