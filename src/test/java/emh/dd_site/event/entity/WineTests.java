package emh.dd_site.event.entity;

import emh.dd_site.event.WineType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Wine Entity")
class WineTests {

	private Wine wine;

	private final String testName = "Test Wine";

	private final WineType testType = WineType.RED;

	private final String testGrape = "Merlot";

	private final String testCountry = "France";

	@BeforeEach
	void setUp() {
		wine = new Wine(testName, testType, testGrape, testCountry);
	}

	@Nested
	@DisplayName("Constructor Tests")
	class ConstructorTests {

		@Test
		@DisplayName("Should create wine with required parameters")
		void shouldCreateWineWithRequiredParameters() {
			assertNotNull(wine);
			assertEquals(testName, wine.getName());
			assertEquals(testType, wine.getType());
			assertEquals(testGrape, wine.getGrape());
			assertEquals(testCountry, wine.getCountry());
			assertTrue(wine.getCourses().isEmpty());
		}

		@Test
		@DisplayName("Should throw NullPointerException when name is null")
		void shouldThrowExceptionWhenNameIsNull() {
			assertThrows(NullPointerException.class, () -> new Wine(null, testType, testGrape, testCountry));
		}

		@Test
		@DisplayName("Should throw NullPointerException when type is null")
		void shouldThrowExceptionWhenTypeIsNull() {
			assertThrows(NullPointerException.class, () -> new Wine(testName, null, testGrape, testCountry));
		}

		@Test
		@DisplayName("Should throw NullPointerException when grape is null")
		void shouldThrowExceptionWhenGrapeIsNull() {
			assertThrows(NullPointerException.class, () -> new Wine(testName, testType, null, testCountry));
		}

		@Test
		@DisplayName("Should throw NullPointerException when country is null")
		void shouldThrowExceptionWhenCountryIsNull() {
			assertThrows(NullPointerException.class, () -> new Wine(testName, testType, testGrape, null));
		}

	}

	@Nested
	@DisplayName("Property Tests")
	class PropertyTests {

		@Test
		@DisplayName("Should set and get optional region")
		void shouldSetAndGetRegion() {
			String region = "Bordeaux";
			wine.setRegion(region);
			assertEquals(region, wine.getRegion());
		}

		@Test
		@DisplayName("Should set and get optional year")
		void shouldSetAndGetYear() {
			Year year = Year.of(2020);
			wine.setYear(year);
			assertEquals(year, wine.getYear());
		}

		@Test
		@DisplayName("Should set and get name")
		void shouldSetAndGetName() {
			String newName = "New Wine";
			wine.setName(newName);
			assertEquals(newName, wine.getName());
		}

		@Test
		@DisplayName("Should set and get type")
		void shouldSetAndGetType() {
			WineType newType = WineType.WHITE;
			wine.setType(newType);
			assertEquals(newType, wine.getType());
		}

	}

	@Nested
	@DisplayName("Course Management Tests")
	class CourseManagementTests {

		private Course course;

		private Event event;

		private Dish dish;

		@BeforeEach
		void setUp() {
			event = new Event(LocalDate.now(), "Test Host");
			dish = new Dish("Test Dish");
			course = new Course(event, 1, "Test Cook", dish, wine);
		}

		@Test
		@DisplayName("Should add course successfully")
		void shouldAddCourse() {
			wine.addCourse(course);
			assertTrue(wine.getCourses().contains(course));
			assertEquals(wine, course.getWine());
		}

		@Test
		@DisplayName("Should not add duplicate course")
		void shouldNotAddDuplicateCourse() {
			wine.addCourse(course);
			wine.addCourse(course);
			assertEquals(1, wine.getCourses().size());
		}

		@Test
		@DisplayName("Should remove course successfully")
		void shouldRemoveCourse() {
			wine.addCourse(course);
			wine.removeCourse(course);
			assertFalse(wine.getCourses().contains(course));
		}

		@Test
		@DisplayName("Should throw NullPointerException when adding null course")
		void shouldThrowExceptionWhenAddingNullCourse() {
			assertThrows(NullPointerException.class, () -> wine.addCourse(null));
		}

		@Test
		@DisplayName("Should throw NullPointerException when removing null course")
		void shouldThrowExceptionWhenRemovingNullCourse() {
			assertThrows(NullPointerException.class, () -> wine.removeCourse(null));
		}

		@Test
		@DisplayName("Should return unmodifiable course list")
		void shouldReturnUnmodifiableList() {
			List<Course> courses = wine.getCourses();
			assertThrows(UnsupportedOperationException.class, () -> courses.add(course));
		}

	}

	@Nested
	@DisplayName("Equals and HashCode Tests")
	class EqualsAndHashCodeTests {

		@Test
		@DisplayName("Wines with null IDs should not be equal")
		void winesWithNullIdsShouldNotBeEqual() {
			Wine wine1 = TestWineBuilder.aWine().withId(null).build();
			Wine wine2 = TestWineBuilder.aWine().withId(null).build();
			assertNotEquals(wine1, wine2);
		}

		@Test
		@DisplayName("Same wine should be equal to itself")
		void sameWineShouldBeEqualToItself() {
			assertEquals(wine, wine);
		}

		@Test
		@DisplayName("Wine should not be equal to null")
		void wineShouldNotBeEqualToNull() {
			assertNotEquals(null, wine);
		}

		@Test
		@DisplayName("Wines with same non-null ID should be equal")
		void winesWithSameNonNullIdShouldBeEqual() {
			Wine wine1 = TestWineBuilder.aWine().withId(1L).build();
			Wine wine2 = TestWineBuilder.aWine().withId(1L).build();
			assertEquals(wine1, wine2);
		}

		@Test
		@DisplayName("Wines with different IDs should not be equal")
		void winesWithDifferentIdsShouldNotBeEqual() {
			Wine wine1 = TestWineBuilder.aWine().withId(1L).build();
			Wine wine2 = TestWineBuilder.aWine().withId(2L).build();
			assertNotEquals(wine1, wine2);
		}

	}

	@Test
	@DisplayName("Should generate meaningful toString output")
	void shouldGenerateMeaningfulToString() {
		Wine testWine = TestWineBuilder.aWine()
			.withName(testName)
			.withType(testType)
			.withGrape(testGrape)
			.withCountry(testCountry)
			.withRegion("Bordeaux")
			.withYear(Year.of(2020))
			.build();

		String toString = testWine.toString();
		assertNotNull(toString);
		assertTrue(toString.contains(testName));
		assertTrue(toString.contains(testType.toString()));
		assertTrue(toString.contains(testGrape));
		assertTrue(toString.contains(testCountry));
		assertTrue(toString.contains("Bordeaux"));
		assertTrue(toString.contains("2020"));
	}

}