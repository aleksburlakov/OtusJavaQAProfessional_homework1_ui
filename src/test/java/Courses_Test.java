import com.google.inject.Inject;
import extensions.UiExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.CoursesCatalogPage;

@ExtendWith(UiExtension.class)
public class Courses_Test {
  @Inject
  private CoursesCatalogPage coursesCatalog;

  @Test
  public void findCourseTest() {
    final String COURSE_NAME = "Java QA Engineer. Professional";

    coursesCatalog
        .open()
        .clickCourseByName(COURSE_NAME)
        .pageShouldBeOpened(COURSE_NAME);
  }

  @Test
  public void checkTheEarliestAndTheLatestCourseStartDateTest() {

    coursesCatalog
        .open()
        .checkTheEarliestAndTheLatestCoursesStartDates();
  }
}
