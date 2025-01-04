package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.restassured.RestAssured;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.ConfigurationReader;

import java.io.IOException;

public class BaseTest {

    protected static ExtentReports report;
    protected static ExtentHtmlReporter htmlReporter;
    protected static ExtentTest extentLogger;

    @BeforeTest
    public void setUpTest(){
        report = new ExtentReports();
        String projectPath = System.getProperty("user.dir");
        String path = projectPath + "/test-output/report.html";
        htmlReporter = new ExtentHtmlReporter(path);
        report.attachReporter(htmlReporter);
        htmlReporter.config().setReportName("API Smoke Test");
    }

    @BeforeClass
    public static void setUpClass() {
        RestAssured.baseURI = ConfigurationReader.get("baseUrl");
    }

    @BeforeMethod
    public void setUpMethod(ITestResult result) {
        extentLogger = report.createTest(result.getMethod().getMethodName());
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if(result.getStatus() == ITestResult.FAILURE){
            extentLogger.fail(result.getMethod().getMethodName());
            extentLogger.fail(result.getThrowable());
        } else if(result.getStatus() == ITestResult.SUCCESS){
            extentLogger.pass(result.getMethod().getMethodName());
        }
        // Flush after each test
        report.flush();
    }

    @AfterTest
    public void tearDownTest(){
        // Flush report at the end of the test suite
        report.flush();
    }
}
