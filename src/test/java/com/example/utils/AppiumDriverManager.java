package com.example.utils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

public class AppiumDriverManager {
    private static AndroidDriver driver;
    private static final String APPIUM_SERVER_URL = "http://localhost:4723";

    public static AndroidDriver getDriver() {
        if (driver == null) {
            UiAutomator2Options options = new UiAutomator2Options();
            options.setPlatformName("Android");
            options.setDeviceName("emulator-5554");
            options.setAutomationName("UiAutomator2");
            options.setNoReset(true);
            options.setFullReset(false);
            options.setUdid(System.getProperty("deviceName", "emulator-5554"));

            final String apkPath = System.getProperty("appPath");
            if (apkPath != null && !apkPath.isEmpty()) {
                options.setApp(apkPath);
            } else {
                String appPackage = System.getProperty("appPackage");
                String appActivity = System.getProperty("appActivity");

                if (appPackage == null || appPackage.isEmpty()) {
                    appPackage = "org.wikipedia.alpha";
                    appActivity = ".main.MainActivity";
                }

                options.setAppPackage(appPackage);
                if (appActivity != null && !appActivity.isEmpty()) {
                    options.setAppActivity(appActivity);
                }
            }

            driver = SneakyThrowUtil.sneakyGet(() -> new AndroidDriver(URI.create(APPIUM_SERVER_URL).toURL(), options));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
