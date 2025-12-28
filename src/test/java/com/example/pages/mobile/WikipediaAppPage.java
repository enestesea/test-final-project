package com.example.pages.mobile;

import com.example.utils.RunWithWaitUtil;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class WikipediaAppPage {
    private AndroidDriver driver;
    private WebDriverWait wait;

    private static long COMMON_WAIT_TIME = 3000L;

    public WikipediaAppPage(final AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Получает заголовок текущей статьи
     * Ожидает появление элемента заголовка и возвращает его текст
     *
     * @return текст заголовка статьи, или пустую строку в случае ошибки
     */
    public String getArticleTitle() {
        try {
            WebElement title = findArticleTitle();
            return title != null ? title.getText() : "";
        } catch (RuntimeException e) {
            System.out.println("Ошибка при получении заголовка статьи: " + e.getMessage());
            return "";
        }
    }

    /**
     * Ищет заголовок статьи разными способами
     */
    private WebElement findArticleTitle() {
        final String[] possibleTitleIds = {
                "org.wikipedia.alpha:id/view_page_title_text",
                "org.wikipedia.alpha:id/page_toolbar_button_text",
                "org.wikipedia.alpha:id/articleTitle",
                "org.wikipedia.alpha:id/title"
        };

        for (String id : possibleTitleIds) {
            try {
                final List<WebElement> elements = driver.findElements(By.id(id));
                if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                    System.out.println("Найден заголовок статьи с id: " + id);
                    return elements.get(0);
                }
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }

        try {
            final WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.className("android.widget.TextView")
            ));
            if (title.isDisplayed() && !title.getText().isEmpty()) {
                return title;
            }
        } catch (RuntimeException e) {
            System.out.println("Не удалось найти заголовок статьи");
        }

        return null;
    }

    /**
     * Кликает на контейнер поиска для активации поискового интерфейса
     */
    public void clickSearchContainer() {
        try {
            RunWithWaitUtil.runWithPostWait(() -> {
                final WebElement searchContainer = wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("org.wikipedia.alpha:id/search_container")
                ));
                searchContainer.click();
                System.out.println("Кликнули на контейнер поиска");
            }, COMMON_WAIT_TIME);
        } catch (RuntimeException e) {
            System.out.println("Ошибка при клике на контейнер поиска: " + e.getMessage());
        }
    }

    /**
     * Вводит поисковый запрос в поле ввода
     */
    public void enterSearchQuery(String query) {
        try {
            RunWithWaitUtil.runWithPostWait(() -> {
                final WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(15));
                final WebElement searchInput = longWait.until(ExpectedConditions.presenceOfElementLocated(
                        By.id("org.wikipedia.alpha:id/search_src_text")
                ));
                searchInput.clear();
                searchInput.sendKeys(query);
                System.out.println("Введен запрос: " + query);
            }, COMMON_WAIT_TIME);
        } catch (RuntimeException e) {
            System.out.println("Ошибка при вводе поискового запроса: " + e.getMessage());
        }
    }

    /**
     * Проверяет отображение контейнера поиска
     */
    public boolean isSearchContainerDisplayed() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            final WebElement container = shortWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("org.wikipedia.alpha:id/search_container")
            ));
            return container.isDisplayed();
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Закрывает поисковый интерфейс, если он открыт
     */
    public void closeSearchIfOpen() {
        try {
            List<WebElement> closeButtons = driver.findElements(By.id("org.wikipedia.alpha:id/search_close_btn"));
            if (!closeButtons.isEmpty() && closeButtons.get(0).isDisplayed()) {
                RunWithWaitUtil.runWithPostWait(() -> {
                    System.out.println("Закрываем поиск...");
                    closeButtons.get(0).click();
                }, COMMON_WAIT_TIME);
            }
        } catch (RuntimeException e) {
        }
    }

    /**
     * Проверяет, открыт ли поисковый интерфейс
     */
    public boolean isSearchOpen() {
        try {
            List<WebElement> searchInputs = driver.findElements(By.id("org.wikipedia.alpha:id/search_src_text"));
            return !searchInputs.isEmpty() && searchInputs.get(0).isDisplayed();
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Проверяет отображение контейнера с результатами поиска
     */
    public boolean isSearchResultsDisplayed() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            final WebElement container = shortWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("org.wikipedia.alpha:id/search_results_container")
            ));
            return container.isDisplayed();
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Ожидает появления результатов поиска
     */
    public boolean waitForSearchResults(int timeoutSeconds) {
        try {
            WebDriverWait resultsWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            resultsWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.id("org.wikipedia.alpha:id/page_list_item_title")
            ));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Получает количество найденных результатов поиска
     */
    public int getSearchResultsCount() {
        try {
            final List<WebElement> titles = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.id("org.wikipedia.alpha:id/page_list_item_title")
            ));
            return titles.size();
        } catch (RuntimeException e) {
            return 0;
        }
    }

    /**
     * Кликает на первый результат в списке поиска
     * Пробует разные способы клика для надежности
     */
    public void clickFirstSearchResult() {
        try {
            RunWithWaitUtil.runWithPostWait(() -> {
                System.out.println("Пытаемся кликнуть на первый результат...");

                // Способ 1: Обычный клик
                try {
                    final WebElement firstResult = wait.until(ExpectedConditions.elementToBeClickable(
                            By.id("org.wikipedia.alpha:id/page_list_item_title")
                    ));
                    System.out.println("Найден первый результат: " + firstResult.getText());
                    firstResult.click();
                    System.out.println("Клик выполнен (способ 1)");
                    return;
                } catch (Exception e) {
                    System.out.println("Способ 1 не сработал: " + e.getMessage());
                }

                // Способ 2: JavaScript клик
                try {
                    List<WebElement> results = driver.findElements(By.id("org.wikipedia.alpha:id/page_list_item_title"));
                    if (!results.isEmpty()) {
                        driver.executeScript("arguments[0].click();", results.get(0));
                        System.out.println("Клик выполнен (способ 2 - JavaScript)");
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("Способ 2 не сработал: " + e.getMessage());
                }

                // Способ 3: Tap по координатам
                try {
                    List<WebElement> results = driver.findElements(By.id("org.wikipedia.alpha:id/page_list_item_title"));
                    if (!results.isEmpty()) {
                        WebElement firstResult = results.get(0);
                        int x = firstResult.getLocation().getX() + firstResult.getSize().getWidth() / 2;
                        int y = firstResult.getLocation().getY() + firstResult.getSize().getHeight() / 2;

                        driver.executeScript("mobile: clickGesture",
                                Map.of("x", x, "y", y, "duration", 100));
                        System.out.println("Клик выполнен (способ 3 - координаты)");
                    }
                } catch (Exception e) {
                    System.out.println("Способ 3 не сработал: " + e.getMessage());
                }

                System.out.println("Все способы клика не сработали");

            }, COMMON_WAIT_TIME);
        } catch (RuntimeException e) {
            System.out.println("Ошибка при клике на первый результат: " + e.getMessage());
        }
    }

    /**
     * Получает заголовок первого результата в списке поиска
     */
    public String getFirstResultTitle() {
        try {
            final WebElement firstResult = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("org.wikipedia.alpha:id/page_list_item_title")
            ));
            return firstResult.getText();
        } catch (RuntimeException e) {
            return "";
        }
    }

    /**
     * Ожидает завершения загрузки статьи
     * Пробует разные способы определить, что статья загрузилась
     */
    public boolean waitForArticleToLoad(int timeoutSeconds) {
        try {
            System.out.println("Ожидание загрузки статьи...");
            WebDriverWait articleWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

            boolean articleLoaded = articleWait.until(driver -> {
                try {
                    List<WebElement> titleElements = driver.findElements(
                            By.id("org.wikipedia.alpha:id/view_page_title_text")
                    );
                    if (!titleElements.isEmpty() && titleElements.get(0).isDisplayed()) {
                        System.out.println("Заголовок статьи найден: " + titleElements.get(0).getText());
                        return true;
                    }

                    List<WebElement> searchResults = driver.findElements(
                            By.id("org.wikipedia.alpha:id/search_results_container")
                    );
                    if (searchResults.isEmpty() || !searchResults.get(0).isDisplayed()) {
                        System.out.println("Результаты поиска скрыты - статья возможно загрузилась");
                        return true;
                    }

                    return false;
                } catch (Exception e) {
                    return false;
                }
            });

            if (articleLoaded) {
                Thread.sleep(2000);
            }

            return articleLoaded;

        } catch (Exception e) {
            System.out.println("Ошибка при ожидании загрузки статьи: " + e.getMessage());
            return false;
        }
    }

    /**
     * Проверяет, открыта ли статья (не экран поиска)
     */
    public boolean isArticleOpen() {
        try {
            if (isSearchOpen()) {
                return false;
            }

            String[] articleElements = {
                    "org.wikipedia.alpha:id/view_page_title_text",
                    "org.wikipedia.alpha:id/page_actions_tab_layout",
                    "org.wikipedia.alpha:id/page_scroll_view"
            };

            for (String elementId : articleElements) {
                List<WebElement> elements = driver.findElements(By.id(elementId));
                if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Возвращается назад из статьи или поиска
     */
    public void goBack() {
        try {
            driver.navigate().back();
            Thread.sleep(1000);
            System.out.println("Нажата кнопка Назад");
        } catch (Exception e) {
            System.out.println("Ошибка при нажатии Назад: " + e.getMessage());
        }
    }
}