package actions;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import waiters.StandartWaiter;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class CommonActions<T> {

  protected WebDriver driver;
  protected StandartWaiter standartWaiter;

  public CommonActions(WebDriver driver) {
    this.driver = driver;
    PageFactory.initElements(driver, this);

    standartWaiter = new StandartWaiter(driver);
  }

  protected BiConsumer<By, Predicate<? super WebElement>> clickElementByPredicate = (By locator, Predicate<? super WebElement> predicate) -> {
    List<WebElement> elements = driver.findElements(locator).stream().filter(predicate).collect(Collectors.toList());

    if(!elements.isEmpty()) {
      elements.get(0).click();
    }
  };

  public WebElement $(By locator) {
    return driver.findElement(locator);
  }

  public List<WebElement> $$(By locator) {
    return driver.findElements(locator);
  }
}
