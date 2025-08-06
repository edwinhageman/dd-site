package emh.dd_site.event.entity;

import emh.dd_site.event.WineType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Course Entity")
class CourseTests {

	private Course course;

	private Event event;

	private Dish dish;

	private Wine wine;

	private final Integer testCourseNo = 1;

	private final String testCook = "Test Cook";

	@BeforeEach
	void setUp() {
		event = new Event(LocalDate.now(), "Test Host");
		dish = new Dish("Test Dish");
		wine = new Wine("Test Wine", WineType.RED, "Merlot", "France");
		course = TestCourseBuilder.aCourse(event, dish, wine).withCourseNo(testCourseNo).withCook(testCook).build();
	}

	@Nested
	@DisplayName("Constructor Tests")
	class ConstructorTests {

		@Test
		@DisplayName("Should create course with required parameters")
		void shouldCreateCourseWithRequiredParameters() {
			assertNotNull(course);
			assertEquals(event, course.getEvent());
			assertEquals(testCourseNo, course.getCourseNo());
			assertEquals(testCook, course.getCook());
			assertEquals(dish, course.getDish());
			assertEquals(wine, course.getWine());
		}

		@Test
		@DisplayName("Should throw NullPointerException when event is null")
		void shouldThrowExceptionWhenEventIsNull() {
			assertThrows(NullPointerException.class, () -> new Course(null, testCourseNo, testCook));
		}

		@Test
		@DisplayName("Should throw NullPointerException when courseNo is null")
		void shouldThrowExceptionWhenCourseNoIsNull() {
			assertThrows(NullPointerException.class, () -> new Course(event, null, testCook));
		}

		@Test
		@DisplayName("Should throw NullPointerException when cook is null")
		void shouldThrowExceptionWhenCookIsNull() {
			assertThrows(NullPointerException.class, () -> new Course(event, testCourseNo, null));
		}

	}

	@Nested
	@DisplayName("Property Tests")
	class PropertyTests {

		@Test
		@DisplayName("Should set and get event")
		void shouldSetAndGetEvent() {
			Event newEvent = new Event(LocalDate.now(), "New Host");
			course.setEvent(newEvent);
			assertEquals(newEvent, course.getEvent());
		}

		@Test
		@DisplayName("Should set and get courseNo")
		void shouldSetAndGetCourseNo() {
			Integer newCourseNo = 2;
			course.setCourseNo(newCourseNo);
			assertEquals(newCourseNo, course.getCourseNo());
		}

		@Test
		@DisplayName("Should set and get cook")
		void shouldSetAndGetCook() {
			String newCook = "New Cook";
			course.setCook(newCook);
			assertEquals(newCook, course.getCook());
		}

		@Test
		@DisplayName("Should set and get dish")
		void shouldSetAndGetDish() {
			Dish newDish = TestDishBuilder.aDish().withName("New Dish").build();
			course.setDish(newDish);
			assertEquals(newDish, course.getDish());
		}

		@Test
		@DisplayName("Should set and get wine")
		void shouldSetAndGetWine() {
			Wine newWine = TestWineBuilder.aWine().withName("New Wine").build();
			course.setWine(newWine);
			assertEquals(newWine, course.getWine());
		}

	}

	@Nested
	@DisplayName("Relationship Management Tests")
	class RelationshipManagementTests {

		@Test
		@DisplayName("Should set dish and maintain bidirectional relationship")
		void shouldSetDishAndMaintainBidirectionalRelationship() {
			Dish newDish = new Dish("New Dish");
			course.setDish(newDish);
			assertEquals(newDish, course.getDish());
			assertEquals(course, newDish.getCourse());
		}

		@Test
		@DisplayName("Should set wine and maintain bidirectional relationship")
		void shouldSetWineAndMaintainBidirectionalRelationship() {
			Wine newWine = new Wine("New Wine", WineType.WHITE, "Chardonnay", "France");
			course.setWine(newWine);
			assertEquals(newWine, course.getWine());
			assertTrue(newWine.getCourses().contains(course));
		}

		@Test
		@DisplayName("Should throw NullPointerException when setting null dish")
		void shouldThrowExceptionWhenSettingNullDish() {
			assertThrows(NullPointerException.class, () -> course.setDish(null));
		}

		@Test
		@DisplayName("Should throw NullPointerException when setting null wine")
		void shouldThrowExceptionWhenSettingNullWine() {
			assertThrows(NullPointerException.class, () -> course.setWine(null));
		}

		@Test
		@DisplayName("Should properly handle wine replacement")
		void shouldProperlyHandleWineReplacement() {
			Wine newWine = new Wine("New Wine", WineType.WHITE, "Chardonnay", "France");
			Wine oldWine = course.getWine();

			course.setWine(newWine);

			assertFalse(oldWine.getCourses().contains(course));
			assertTrue(newWine.getCourses().contains(course));
			assertEquals(newWine, course.getWine());
		}

	}

	@Nested
	@DisplayName("Equals and HashCode Tests")
	class EqualsAndHashCodeTests {

		@Test
		@DisplayName("Courses with null IDs should not be equal")
		void coursesWithNullIdsShouldNotBeEqual() {
			Course course1 = TestCourseBuilder.aCourse(event, dish, wine).withId(null).build();
			Course course2 = TestCourseBuilder.aCourse(event, dish, wine).withId(null).build();
			assertNotEquals(course1, course2);
		}

		@Test
		@DisplayName("Same course should be equal to itself")
		void sameCourseShouldBeEqualToItself() {
			assertEquals(course, course);
		}

		@Test
		@DisplayName("Course should not be equal to null")
		void courseShouldNotBeEqualToNull() {
			assertNotEquals(null, course);
		}

		@Test
		@DisplayName("Courses with same non-null ID should be equal")
		void coursesWithSameNonNullIdShouldBeEqual() {
			Course course1 = TestCourseBuilder.aCourse(event, dish, wine).withId(1L).build();
			Course course2 = TestCourseBuilder.aCourse(event, dish, wine).withId(1L).build();
			assertEquals(course1, course2);
		}

		@Test
		@DisplayName("Courses with different IDs should not be equal")
		void coursesWithDifferentIdsShouldNotBeEqual() {
			Course course1 = TestCourseBuilder.aCourse(event, dish, wine).withId(1L).build();
			Course course2 = TestCourseBuilder.aCourse(event, dish, wine).withId(2L).build();
			assertNotEquals(course1, course2);
		}

	}

	@Test
	@DisplayName("Should generate meaningful toString output")
	void shouldGenerateMeaningfulToString() {
		String toString = course.toString();
		assertNotNull(toString);
		assertTrue(toString.contains(testCourseNo.toString()));
		assertTrue(toString.contains(testCook));
		assertTrue(toString.contains(dish.getName()));
		assertTrue(toString.contains(wine.getName()));
	}

}
