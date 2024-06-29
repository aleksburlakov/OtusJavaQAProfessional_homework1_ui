package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import components.HeaderMenu;
import driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import pages.CoursePage;
import pages.CoursesCatalogPage;
import pages.MainPage;

public class GuiceModule extends AbstractModule {
  private final WebDriver driver = new DriverFactory().getDriver();

  @Provides
  public WebDriver getDriver() {
    return driver;
  }

  @Provides
  @Singleton
  public MainPage getMainPage() {
    return new MainPage(driver);
  }

  @Provides
  @Singleton
  public CoursesCatalogPage getCoursesCatalogPage() {
    return new CoursesCatalogPage(driver);
  }

  @Provides
  @Singleton
  public CoursePage getCoursePage() {
    return new CoursePage(driver);
  }

  @Provides
  @Singleton
  public HeaderMenu getHeaderMenu() {
    return new HeaderMenu(driver);
  }
}
