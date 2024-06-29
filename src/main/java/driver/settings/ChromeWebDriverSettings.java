package driver.settings;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

public class ChromeWebDriverSettings implements IWebDriverSettings<ChromeOptions> {

  @Override
  public ChromeOptions getWebDriverSettings() {
    ChromeOptions chromeOptions = new ChromeOptions();

    chromeOptions.addArguments("--no-first-run");
    chromeOptions.addArguments("--enable-extensions");
    chromeOptions.addArguments("--homepage=about:blank");
    chromeOptions.addArguments("--ignore-certificate-errors");
    chromeOptions.addArguments("--start-maximized");
    chromeOptions.setCapability(CapabilityType.BROWSER_NAME, System.getProperty("browser", "chrome"));

    return chromeOptions;
  }
}
