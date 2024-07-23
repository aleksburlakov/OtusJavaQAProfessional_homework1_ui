import com.google.inject.Inject;
import components.HeaderMenu;
import extensions.UiExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.MainPage;

@ExtendWith(UiExtension.class)
public class Category_Test {

  @Inject
  private MainPage mainPage;

  @Inject
  private HeaderMenu headerMenu;

  @Test
  public void checkCategoryTest() {
    mainPage.open();

    String categoryName = headerMenu
        .getRandomItemCategoryName();

    headerMenu
        .openHeaderMenuItem("Обучение")
        .selectCourseCategory(categoryName)
        .pageShouldBeOpened()
        .checkSelectedCategory(categoryName);
  }
}
