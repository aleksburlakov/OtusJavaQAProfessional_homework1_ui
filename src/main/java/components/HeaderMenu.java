package components;

import java.util.List;
import java.util.Random;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import pages.CourseCategoryPage;

public class HeaderMenu extends AnyComponentAbs {

  @FindBy(xpath = "//p[text()='Все курсы']/following-sibling::div/a[text()!='Подписка на курсы']")
  private List<WebElement> courseCategories;

  public HeaderMenu(WebDriver driver) {
    super(driver);
  }

  public HeaderMenu openHeaderMenuItem(String itemName) {
    WebElement item = driver.findElement(By.xpath(String.format("//nav//span[@title='%s']/parent::div", itemName)));

    Actions actions = new Actions(driver);
    actions.moveToElement(item).perform();

    Assertions.assertThat(standartWaiter.waitForElementVisible(item.findElement(By.xpath("./following-sibling::div[1]"))))
        .as("На странице не отобразилось выпадающее меню " + itemName)
        .isTrue();
    return this;
  }

  public String getRandomItemCategoryName() {
    courseCategories.forEach(categoryItem -> System.out.println(categoryItem.getText()));
    int randomIndex = new Random().ints(0, courseCategories.size() - 1)
        .findFirst()
        .getAsInt();
    return courseCategories.get(randomIndex).getAttribute("textContent").split(" \\(")[0];
  }

  public CourseCategoryPage selectCourseCategory(String categoryName) {
    courseCategories.stream().filter(categoryItem -> categoryItem.getText().contains(categoryName)).findFirst().get().click();
    return new CourseCategoryPage(driver);
  }
}
