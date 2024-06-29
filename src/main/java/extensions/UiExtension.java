package extensions;

import com.google.inject.Guice;
import com.google.inject.Injector;
import modules.GuiceModule;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;


public class UiExtension implements BeforeEachCallback, AfterEachCallback {

  private Injector injector;

  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    injector = Guice.createInjector(new GuiceModule());

    extensionContext.getTestInstance()
        .ifPresent(instance -> {
          injector.injectMembers(instance);
        });
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) {
    extensionContext.getTestInstance()
        .ifPresent(instance -> {
          WebDriver driver = injector.getProvider(WebDriver.class).get();
          if (driver != null) {
            driver.quit();
          }
        });
  }
}
