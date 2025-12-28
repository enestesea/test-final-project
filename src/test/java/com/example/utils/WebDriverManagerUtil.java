package com.example.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class WebDriverManagerUtil {
    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null) {
            String browser = System.getProperty("browser", "chrome");
            driver = createDriver(browser);
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
        return driver;
    }

    private static WebDriver createDriver(String browser) {
        try {
            switch (browser.toLowerCase()) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                    String chromeBinary = System.getProperty("chrome.binary");
                    if (chromeBinary != null && !chromeBinary.isEmpty()) {
                        chromeOptions.setBinary(chromeBinary);
                    }
                    return new ChromeDriver(chromeOptions);
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    String firefoxBinary = System.getProperty("firefox.binary");
                    if (firefoxBinary != null && !firefoxBinary.isEmpty()) {
                        firefoxOptions.setBinary(firefoxBinary);
                    }
                    return new FirefoxDriver(firefoxOptions);
                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browser);
            }
        } catch (RuntimeException e) {
            System.out.println("Unable to initialize driver: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
