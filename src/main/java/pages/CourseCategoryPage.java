package pages;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CourseCategoryPage extends AnyPageAbs<CourseCategoryPage> {

  @FindBy(xpath = "//p[text()='Направление']/ancestor::div[2]//input/ancestor::div[2]")
  private List<WebElement> categories;

  public CourseCategoryPage(WebDriver driver) {
    super(driver);
  }

  public CourseCategoryPage pageShouldBeOpened() {

    assertThat(standartWaiter.waitForElementVisible($(By.xpath("//h1/div[text()='Каталог']"))))
        .as("Страница 'Каталог' не была открыта")
        .isTrue();

    return this;
  }

  public void checkSelectedCategory(String expectedSelectedCategory) {
    List<WebElement> selectedCategories = categories.stream().filter(category ->
        category.getAttribute("value").equals("true")).collect(Collectors.toList());
    Assertions.assertThat(selectedCategories.size() == 1).isTrue()
        .as("Выделено больше чем одна категория или не выделено ни одной");
    Assertions.assertThat(selectedCategories.get(0).getText())
        .isEqualTo(expectedSelectedCategory)
        .as("Выделена неверная категория");
  }
}
