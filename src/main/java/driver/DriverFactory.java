package driver;

import driver.settings.ChromeWebDriverSettings;
import driver.settings.IWebDriverSettings;
import exceptions.DriverTypeNotSupported;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.Locale;
import listeners.ActionsListener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.events.EventFiringDecorator;

public class DriverFactory implements IDriverFactory {
  private final String browserName = System.getProperty("browser", "chrome").toLowerCase(Locale.ROOT);
  private final String browserVesion = System.getProperty("browser.version", "126.0");

  @Override
  public WebDriver getDriver() {
    switch (this.browserName) {
      case "chrome":
        WebDriverManager.chromiumdriver().browserVersion(browserVesion).setup();
        IWebDriverSettings<ChromeOptions> browserSettings = new ChromeWebDriverSettings();
        return new EventFiringDecorator<>(new ActionsListener())
            .decorate(new ChromeDriver(browserSettings.getWebDriverSettings()));

      default:
        try {
          throw new DriverTypeNotSupported(this.browserName);
        } catch (DriverTypeNotSupported ex) {
          ex.printStackTrace();
          return null;
        }
    }
  }
}
