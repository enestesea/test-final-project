package com.example.pages.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WikipediaWebPage {

    private final WebDriver webDriver;
    private final WebDriverWait waitDriver;
    private static final String BASE_URL = "https://ru.wikipedia.org/wiki/Заглавная_страница";

    private final By WIKI_LOGO = By.id("p-logo");
    private final By SEARCH_INPUT = By.id("searchInput");
    private final By ARTICLE_HEADING = By.id("firstHeading");
    private final By RANDOM_PAGE_LINK = By.id("n-randompage");
    private final By BODY_CONTENT = By.id("bodyContent");

    public WikipediaWebPage(final WebDriver webDriver) {
        this.webDriver = webDriver;
        this.waitDriver = new WebDriverWait(webDriver, Duration.ofSeconds(15));
    }

    /**
     * Проверяет доступность основного контента на главной странице
     * Выполняет переход на базовый URL и проверяет отображение ключевых элементов:
     * логотипа Wikipedia и основного контентного блока
     */
    public boolean isMainPageContentAvailable() {
        webDriver.get(BASE_URL);
        try {
            waitDriver.until(ExpectedConditions.visibilityOfElementLocated(WIKI_LOGO));
            return waitDriver.until(ExpectedConditions.visibilityOfElementLocated(BODY_CONTENT)).isDisplayed();
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Выполняет поиск по указанному запросу на сайте.
     * Ожидает появление поля ввода, очищает его, вводит текст запроса и отправляет форму
     */
    public void searchFor(final String query) {
        final WebElement searchInput = waitDriver.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
        searchInput.clear();
        searchInput.sendKeys(query);
        searchInput.submit();
    }

    /**
     * Извлекает и возвращает текст заголовка текущей статьи
     * Ожидает появление элемента заголовка статьи и возвращает его текст
     */
    public String getArticleHeadingText() {
        return waitDriver.until(ExpectedConditions.visibilityOfElementLocated(ARTICLE_HEADING)).getText().trim();
    }

    /**
     * Переходит на случайную страницу Wikipedia
     * Находит и кликает по ссылке "Случайная статья", затем ожидает загрузку основного контента
     */
    public void goToRandomPage() {
        final WebElement randomLink = waitDriver.until(ExpectedConditions.elementToBeClickable(RANDOM_PAGE_LINK));
        randomLink.click();
        waitDriver.until(ExpectedConditions.visibilityOfElementLocated(BODY_CONTENT));
    }

    /**
     * Проверяет состояние поля поиска на доступность для взаимодействия
     * Ожидает появление поля ввода и проверяет, что оно отображается и активно
     */
    public boolean isSearchInputDisplayedAndEnabled() {
        final WebElement searchInput = waitDriver.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
        return searchInput.isDisplayed() && searchInput.isEnabled();
    }
}