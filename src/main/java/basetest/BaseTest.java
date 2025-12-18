package basetest;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.lang.reflect.Method;

public class BaseTest {
	
	protected WebDriver driver;
    protected static ExtentReports extent;
    protected ExtentTest test;

    // Centralized application URL
    protected String appUrl = "https://www.saucedemo.com/";

    @BeforeSuite
    public void setupReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter("target/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);

        // Add system info to report
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
    }

    @Parameters("browser")
    @BeforeMethod
    public void setupDriver(@Optional("chrome") String browser, Method method) {
        switch (browser.toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            default:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
        }
        driver.manage().window().maximize();
        test = extent.createTest(method.getName() + " [" + browser + "]");

        // Navigate to app URL before each test
        driver.get(appUrl);
        test.info("Navigated to: " + appUrl);
    }

    @AfterMethod
    public void tearDownDriver(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String screenshotPath = takeScreenshot(result.getName());
            test.fail("Test Failed: " + result.getThrowable(),
                      MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test Passed");
        }
        if (driver != null) driver.quit();
    }

    @AfterSuite
    public void flushReport() {
        extent.flush();
    }

    public String takeScreenshot(String name) {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String path = "target/screenshots/" + name + ".png";
        try {
            Files.createDirectories(new File("target/screenshots").toPath());
            Files.copy(src.toPath(), new File(path).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

	}

	
	

