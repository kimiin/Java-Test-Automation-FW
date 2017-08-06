package com.mh.ta.base.selenium;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mh.ta.base.selenium.webelement.FindBy;
import com.mh.ta.core.exception.TestContextException;
import com.mh.ta.factory.DriverFactory;
import com.mh.ta.interfaces.element.TAElement;

class WebElementFinder {

	public TAElement findElementUntilVisible(FindBy by, int timeOut, int pollingTime) {
		SeleniumDriver drivers = (SeleniumDriver) DriverFactory.getDriver();
		Wait<WebDriver> wait = new FluentWait<WebDriver>(drivers.getCoreDriver()).withTimeout(timeOut, TimeUnit.SECONDS)
				.pollingEvery(pollingTime, TimeUnit.MILLISECONDS)
				.ignoring(NoSuchElementException.class, WebDriverException.class);
		return wait.until((driver) -> {
			WebDriverWait explicit = new WebDriverWait(driver, timeOut);
			WebElement element = explicit
					.until(ExpectedConditions.visibilityOfElementLocated(convertToSeleniumBy.apply(by)));
			return new SeleniumElement(element);
		});
	}

	public TAElement findElement(FindBy by) {
		return new SeleniumElement(findSeleniumElement.apply(by));
	}

	public List<TAElement> findListElement(FindBy by) {
		return findListSeleniumElement.apply(by).stream().map(SeleniumElement::new).collect(Collectors.toList());
	}

	private Function<FindBy, By> convertToSeleniumBy = (by) -> {
		switch (by.getLocatorType()) {
		case ID:
			return By.id(by.getLocatorValue());
		case NAME:
			return By.name(by.getLocatorValue());
		case CSS:
			return By.cssSelector(by.getLocatorValue());
		case LINK:
			return By.cssSelector(by.getLocatorValue());
		case TAG:
			return By.tagName(by.getLocatorValue());
		case XPATH:
			return By.xpath(by.getLocatorValue());
		case CLASS:
			return By.className(by.getLocatorValue());
		default:
			throw new TestContextException("Invalid locator type");
		}
	};

	private Function<FindBy, WebElement> findSeleniumElement = (by) -> {
		try {
			SeleniumDriver driver = (SeleniumDriver) DriverFactory.getDriver();
			WebElement element = driver.getCoreDriver().findElement(convertToSeleniumBy.apply(by));
			return element;
		} catch (StaleElementReferenceException e) {
			SeleniumDriver driver = (SeleniumDriver) DriverFactory.getDriver();
			WebElement element = driver.getCoreDriver().findElement(convertToSeleniumBy.apply(by));
			return element;
		}
	};

	private Function<FindBy, List<WebElement>> findListSeleniumElement = (by) -> {
		try {
			SeleniumDriver driver = (SeleniumDriver) DriverFactory.getDriver();
			List<WebElement> listElement = driver.getCoreDriver().findElements(convertToSeleniumBy.apply(by));
			return listElement;
		} catch (StaleElementReferenceException e) {
			SeleniumDriver driver = (SeleniumDriver) DriverFactory.getDriver();
			List<WebElement> listElement = driver.getCoreDriver().findElements(convertToSeleniumBy.apply(by));
			return listElement;
		}
	};
}
