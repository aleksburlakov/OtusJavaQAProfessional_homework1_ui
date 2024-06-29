package pages;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import annotations.UrlPrefix;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@UrlPrefix("/catalog/courses")
public class CoursesCatalogPage extends AnyPageAbs<CoursesCatalogPage> {

  @FindBy(xpath = "//div[@class='sticky-banner js-sticky-banner']")
  private WebElement banner;

  @FindBy(xpath = "//div[text() = 'Каталог']/ancestor::section[1]//a[contains(@href, '/lessons/')]/h6/div")
  private List<WebElement> coursesNames;

  @FindBy(xpath = "//div[text() = 'Каталог']/ancestor::section[1]//a[contains(@href, '/lessons/')]"
      + "/div[2]/div/div[starts-with(text(), '3') or starts-with(text(), '2') or starts-with(text(), '1') or starts-with(text(), '0')]")
  private List<WebElement> coursesDates;

  @FindBy(xpath = "//div[text() = 'Каталог']/ancestor::section[1]//a[contains(@href, '/lessons/')]")
  private List<WebElement> courses;

  @FindBy(xpath = "//button[contains(text(), 'Показать еще')]")
  private WebElement showMoreCourses;

  public CoursesCatalogPage(WebDriver driver) {
    super(driver);
  }

  public CoursePage clickCourseByName(String courseName) {
    findCourseByName(courseName).click();
    return new CoursePage(driver);
  }

  private WebElement findCourseByName(String name) {
    closeBanner();
    WebElement course = null;
    while (standartWaiter.waitForElementVisible(showMoreCourses)) {
      course = coursesNames.stream().filter(
              courseName -> name.equals(courseName.getText()))
          .findFirst().orElse(
              null
          );
      if (course != null) {
        break;
      } else {
        showMoreCourses.click();
      }
    }
    assertThat(course)
        .as("Курс с названием " + name + "  в каталоге курсов не найден")
        .isNotNull();
    return course;
  }

  public void checkTheEarliestAndTheLatestCoursesStartDates() {
    closeBanner();
    while (standartWaiter.waitForElementVisible(showMoreCourses)) {
      showMoreCourses.click();
    }

    List<Course> courseList = new ArrayList<>();
    courses.forEach(courseWebElement -> {
      String courseName = courseWebElement.findElement(By.xpath("./h6//div")).getText();
      String courseStartDate = courseWebElement.findElement(
          By.xpath("./div[2]/div/div")).getText();
      String coursePageUrl = courseWebElement.getAttribute("href");

      courseList.add(new Course(courseName, getStartDateFromString(courseStartDate), coursePageUrl));
    });

    CoursesCatalog coursesCatalog = new CoursesCatalog(courseList);
    String theEarliestCourseStartDate = coursesCatalog
        .getTheEarliestOrTheLatestCourseStartDate(CoursesCatalog.StartDataType.THE_EARLIEST);
    List<Course> theEarliestCourseList = coursesCatalog
        .getCoursesWithExpectedStartDate(theEarliestCourseStartDate);

    String theLatestCourseStartDate = coursesCatalog
        .getTheEarliestOrTheLatestCourseStartDate(CoursesCatalog.StartDataType.THE_LATEST);
    List<Course> theLatestCourseList = coursesCatalog
        .getCoursesWithExpectedStartDate(theLatestCourseStartDate);

    List<Course> resultList = new ArrayList<>(theEarliestCourseList);
    resultList.addAll(theLatestCourseList);
    checkCoursesStartDates(resultList);
  }

  private void checkCoursesStartDates(List<Course> courseList) {
    courseList.forEach(course -> {
      Document doc;
      try {
        doc = Jsoup.connect(course.getCourseLink()).get();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      String actualTitle = doc.select("h1").get(0).text();
      String actualDate = doc.select("main > div > section > div").get(2).select("div").get(2).select("p").get(0).text();
      assertThat(actualTitle).as("Название курса не совпадает с ожидаемым").isEqualTo(course.getCourseName());
      assertThat(actualDate).as("Дата начала курса не совпадает с ожидаемой").isEqualTo(course.getCourseStartDate().split(",")[0]);
    });
  }


  private String getEarliestCourseDate(List<WebElement> coursesStartDates) {
    return coursesStartDates.stream().reduce((d1, d2) -> {
      LocalDate firstDate = getCourseStartDate(d1);
      LocalDate secondDate = getCourseStartDate(d2);
      if (firstDate.isBefore(secondDate)) {
        return d1;
      } else {
        return d2;
      }
    }).get().getText();
  }

  private LocalDate getCourseStartDate(WebElement startDate) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy");
    return LocalDate.parse(getStartDateFromString(startDate.getText()), dateTimeFormatter);
  }

  /**
   * Получает из строки вида '27 мая, 2024 · 5 месяцев' дату 27 мая, 2024
   *
   * @param startDateString
   * @return
   */
  private String getStartDateFromString(String startDateString) {
    return startDateString.split(" · ")[0];
  }

  private void closeBanner() {
    if (standartWaiter.waitForElementVisible(banner)) {
      banner.findElement(
              By.xpath(".//div[@class='sticky-banner__close js-sticky-banner-close']")
          )
          .click();
    }
  }


  @Data
  @AllArgsConstructor
  private class CoursesCatalog {

    private List<Course> courseList;

    public enum StartDataType {
      THE_LATEST,
      THE_EARLIEST
    }

    public String getTheEarliestOrTheLatestCourseStartDate(StartDataType startDataType) {
      return courseList.stream().reduce((d1, d2) -> {
        LocalDate firstDate = getCourseStartDate(d1.courseStartDate);
        LocalDate secondDate = getCourseStartDate(d2.courseStartDate);
        if (firstDate == null) {
          return d2;
        }
        if (secondDate == null) {
          return d1;
        }

        BiFunction<LocalDate, LocalDate, Boolean> function;
        if (StartDataType.THE_EARLIEST.equals(startDataType)) {
          function = LocalDate::isBefore;
        } else {
          function = LocalDate::isAfter;
        }

        if (function.apply(firstDate, secondDate)) {
          return d1;
        } else {
          return d2;
        }
      }).get().getCourseStartDate();
    }

    public List<Course> getCoursesWithExpectedStartDate(String expectedStartDate) {
      return courseList.stream().filter(course -> expectedStartDate.equals(course.courseStartDate)).collect(Collectors.toList());
    }

    private LocalDate getCourseStartDate(String startDate) {
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy");
      LocalDate courseStartDate;
      try {
        courseStartDate = LocalDate.parse(startDate, dateTimeFormatter);
      } catch (DateTimeParseException e) {
        courseStartDate = null;
      }
      return courseStartDate;
    }

  }

  @Data
  @AllArgsConstructor
  private class Course {

    private String courseName;
    private String courseStartDate;
    private String courseLink;
  }

}
