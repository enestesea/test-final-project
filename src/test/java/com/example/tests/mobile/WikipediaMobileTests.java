package com.example.tests.mobile;

import com.example.pages.mobile.WikipediaAppPage;
import com.example.utils.AppiumDriverManager;
import com.example.utils.RunWithWaitUtil;
import io.appium.java_client.android.AndroidDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WikipediaMobileTests {
    private AndroidDriver driver;
    private WikipediaAppPage page;

    @BeforeMethod
    public void setUp() {
        try {
            RunWithWaitUtil.runWithPostWait(() -> {
                driver = AppiumDriverManager.getDriver();
            }, 3000);
            page = new WikipediaAppPage(driver);
            resetToMainScreen();
        } catch (RuntimeException e) {
            System.out.println("Ошибка при инициализации теста: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @AfterMethod
    public void tearDown() {
        try {
            RunWithWaitUtil.runWithPostWait(this::resetToMainScreen, 3000);
        } catch (RuntimeException e) {
            System.out.println("Ошибка при очистке после теста: " + e.getMessage());
        } finally {
            AppiumDriverManager.quitDriver();
            driver = null;
        }
    }

    private void resetToMainScreen() {
        try {
            for (int i = 0; i < 5; i++) {
                try {
                    if (page.isSearchContainerDisplayed()) {
                        break;
                    }
                    page.goBack();
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                }
            }

            page.closeSearchIfOpen();

            final AtomicInteger attempts = new AtomicInteger(0);
            while (attempts.get() < 10 && !page.isSearchContainerDisplayed()) {
                RunWithWaitUtil.runWithPreWait(attempts::incrementAndGet, 1000);
            }

            if (page.isSearchContainerDisplayed()) {
                System.out.println("Главный экран успешно загружен ✓");
            } else {
                System.out.println("Предупреждение: главный экран может быть не загружен");
            }

        } catch (RuntimeException e) {
            System.out.println("Ошибка при сбросе: " + e.getMessage());
        }
    }

    @Test
    public void testMainScreenDisplay() {
        Assert.assertTrue(page.isSearchContainerDisplayed(),
                "Контейнер поиска должен отображаться на главном экране");
    }

    @Test
    public void testSearchArticle() {
        page.clickSearchContainer();
        Assert.assertTrue(page.isSearchOpen(), "Поисковый интерфейс должен быть открыт");

        page.enterSearchQuery("Java");

        final boolean resultsAppeared = page.waitForSearchResults(10);
        Assert.assertTrue(resultsAppeared, "Результаты поиска должны появиться");

        final int resultsCount = page.getSearchResultsCount();
        System.out.println("Найдено результатов: " + resultsCount);
        Assert.assertTrue(resultsCount > 0, "Должны быть найдены результаты поиска");

    }

    @Test
    public void testOpenArticleAndCheckTitle() throws InterruptedException {
        page.clickSearchContainer();
        Assert.assertTrue(page.isSearchOpen(), "Поиск должен быть открыт");

        page.enterSearchQuery("Java");

        final boolean resultsAppeared = page.waitForSearchResults(15);
        Assert.assertTrue(resultsAppeared, "Результаты поиска должны появиться");

        Thread.sleep(2000); // Пауза для стабилизации

        final int resultsCount = page.getSearchResultsCount();
        System.out.println("Найдено результатов: " + resultsCount);
        Assert.assertTrue(resultsCount > 0, "Должны быть найдены результаты поиска");

        final String firstResultTitle = page.getFirstResultTitle();
        System.out.println("Первый результат: " + firstResultTitle);
        Assert.assertFalse(firstResultTitle.isEmpty(), "Заголовок не должен быть пустым");

        final String expectedTitlePart = firstResultTitle.substring(0, Math.min(10, firstResultTitle.length()));

        page.clickFirstSearchResult();

        final boolean articleLoaded = page.waitForArticleToLoad(15);
        Assert.assertTrue(articleLoaded, "Статья должна загрузиться");

        final boolean isArticleOpen = page.isArticleOpen();
        System.out.println("Статья открыта: " + isArticleOpen);

        if (isArticleOpen) {
            final String articleTitle = page.getArticleTitle();
            System.out.println("Заголовок статьи: " + articleTitle);

            Assert.assertFalse(articleTitle.isEmpty(), "Заголовок статьи не должен быть пустым");

            Assert.assertTrue(
                    articleTitle.contains(expectedTitlePart) ||
                            expectedTitlePart.contains(articleTitle.substring(0, Math.min(5, articleTitle.length()))),
                    "Заголовок статьи должен соответствовать выбранному результату"
            );

            final boolean resultsStillVisible = page.isSearchResultsDisplayed();
            System.out.println("Результаты поиска отображаются: " + resultsStillVisible);
            Assert.assertFalse(resultsStillVisible, "Результаты поиска не должны отображаться");

        } else {
            System.out.println("Статья не открылась полностью, возможно показывается предпросмотр");

            String articleTitle = page.getArticleTitle();
            if (!articleTitle.isEmpty()) {
                System.out.println("Найден заголовок в предпросмотре: " + articleTitle);
            }
        }

    }
}