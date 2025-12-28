package com.example.tests.web;

import com.example.pages.web.WikipediaWebPage;
import com.example.utils.WebDriverManagerUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WikipediaWebTests {

    private WebDriver driver;
    private WikipediaWebPage wikipediaPage;
    private static final String BASE_URL = "https://ru.wikipedia.org/";

    @BeforeMethod
    public void setUp() {
        WebDriver driver = WebDriverManagerUtil.getDriver();
        driver.get(BASE_URL);
        wikipediaPage = new WikipediaWebPage(driver);
    }

    @AfterMethod
    public void tearDown() {
        WebDriverManagerUtil.quitDriver();
    }

    @Test
    public void testMainPageLoaded() {
        Assert.assertTrue(wikipediaPage.isMainPageContentAvailable());
    }

    @Test
    public void testSearch() {
        final String searchQuery = "Java";
        final String expectedArticleTitle = "Java";

        wikipediaPage.searchFor(searchQuery);

        final String heading = wikipediaPage.getArticleHeadingText();

        Assert.assertEquals(heading, expectedArticleTitle, "Expected heading: " + expectedArticleTitle + ", but got: " + heading);
    }

    @Test
    public void testRandomPageNavigation() {
        wikipediaPage.isMainPageContentAvailable();
        WebDriver driver = WebDriverManagerUtil.getDriver();
        final String originalUrl = driver.getCurrentUrl();

        wikipediaPage.goToRandomPage();

        Assert.assertNotEquals(driver.getCurrentUrl(), originalUrl);
    }

    @Test
    public void testSearchAvailable() {
        wikipediaPage.isMainPageContentAvailable();

        Assert.assertTrue(wikipediaPage.isSearchInputDisplayedAndEnabled());
    }
}
