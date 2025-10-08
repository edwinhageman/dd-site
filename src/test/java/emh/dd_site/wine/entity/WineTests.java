package emh.dd_site.wine.entity;

import emh.dd_site.event.entity.Course;
import emh.dd_site.event.entity.Dish;
import emh.dd_site.event.entity.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Wine Entity")
class WineTests {

	private Wine wine;

	private final String testName = "Test Wine";

	private final String testWinery = "Test Winery";

	private final String testCountry = "Test Country";

	private final String testRegion = "Test Region";

	private final String testAppellation = "Test Appellation";

	private final Year testVintage = Year.of(2020);

	@BeforeEach
	void setUp() {
		wine = new Wine(testName);
		wine.setWinery(testWinery);
		wine.setCountry(testCountry);
		wine.setRegion(testRegion);
		wine.setAppellation(testAppellation);
		wine.setVintage(testVintage);
	}

	@Nested
	@DisplayName("Constructor Tests")
	class ConstructorTests {

		@Test
		@DisplayName("Should create wine with required parameters")
		void shouldCreateWineWithRequiredParameters() {
			assertNotNull(wine);
			assertEquals(testName, wine.getName());
			assertTrue(wine.getStyles().isEmpty());
			assertTrue(wine.getGrapeComposition().isEmpty());
			assertTrue(wine.getCourses().isEmpty());
		}

		@Test
		@DisplayName("Should throw NullPointerException when name is null")
		void shouldThrowExceptionWhenNameIsNull() {
			assertThrows(NullPointerException.class, () -> new Wine(null));
		}

	}

	@Nested
	@DisplayName("Property Tests")
	class PropertyTests {

		@Test
		@DisplayName("Should set and get optional winery")
		void shouldSetAndGetWinery() {
			String winery = "Winery";
			wine.setWinery(winery);
			assertEquals(winery, wine.getWinery());
		}

		@Test
		@DisplayName("Should set and get optional country")
		void shouldSetAndGetCountry() {
			String country = "Country";
			wine.setCountry(country);
			assertEquals(country, wine.getCountry());
		}

		@Test
		@DisplayName("Should set and get optional region")
		void shouldSetAndGetRegion() {
			String region = "Region";
			wine.setRegion(region);
			assertEquals(region, wine.getRegion());
		}

		@Test
		@DisplayName("Should set and get optional appellation")
		void shouldSetAndGetAppellation() {
			String appellation = "Appellation";
			wine.setAppellation(appellation);
			assertEquals(appellation, wine.getAppellation());
		}

		@Test
		@DisplayName("Should set and get optional vintage")
		void shouldSetAndGetVintage() {
			Year vintage = Year.of(2020);
			wine.setVintage(vintage);
			assertEquals(vintage, wine.getVintage());
		}

		@Test
		@DisplayName("Should set and get name")
		void shouldSetAndGetName() {
			String newName = "Name";
			wine.setName(newName);
			assertEquals(newName, wine.getName());
		}

	}

	@Nested
	@DisplayName("Styles Management Tests")
	class StyleManagementTests {

		private WineStyle style;

		@BeforeEach
		void setUp() {
			style = TestWineStyleBuilder.builder().withName("style 1").build();
		}

		@Test
		@DisplayName("Should add style successfully")
		void shouldAddStyle() {
			wine.addStyle(style);
			assertThat(wine.getStyles()).containsOnly(style);
		}

		@Test
		@DisplayName("Should not add duplicate style")
		void shouldNotAddDuplicateStyle() {
			wine.addStyle(style);
			wine.addStyle(style);
			assertEquals(1, wine.getStyles().size());
		}

		@Test
		@DisplayName("Should remove style successfully")
		void shouldRemoveStyle() {
			wine.addStyle(style);
			wine.removeStyle(style);
			assertThat(wine.getStyles()).doesNotContainSequence(style);
		}

		@Test
		@DisplayName("Should clear styles")
		void shouldClearStyles() {
			wine.addStyle(style);
			wine.clearStyles();
			assertTrue(wine.getStyles().isEmpty());
		}

		@Test
		@DisplayName("Should throw NullPointerException when adding null style")
		void shouldThrowExceptionWhenAddingNullStyle() {
			assertThrows(NullPointerException.class, () -> wine.addStyle(null));
		}

		@Test
		@DisplayName("Should throw NullPointerException when removing null style")
		void shouldThrowExceptionWhenRemovingNullStyle() {
			assertThrows(NullPointerException.class, () -> wine.removeStyle(null));
		}

		@Test
		@DisplayName("Should return unmodifiable style list")
		void shouldReturnUnmodifiableList() {
			var styles = wine.getStyles();
			assertThrows(UnsupportedOperationException.class, () -> styles.add(style));
		}

	}

	@Nested
	@DisplayName("Grape Management Tests")
	class GrapeManagementTests {

		private Grape grape1, grape2;

		@BeforeEach
		void setUp() {
			grape1 = TestGrapeBuilder.builder().withId(1).withName("Grape 1").build();
			grape2 = TestGrapeBuilder.builder().withId(2).withName("Grape 2").build();
		}

		@Test
		@DisplayName("Should add grape successfully")
		void shouldAddGrape() {
			wine.addGrape(grape1, BigDecimal.ONE);
			wine.addGrape(grape2, null);
			assertThat(wine.getGrapeComposition()).containsExactlyInAnyOrder(
					new WineGrapeComposition(wine, grape1, BigDecimal.ONE),
					new WineGrapeComposition(wine, grape2, null));
		}

		@Test
		@DisplayName("Should not add duplicate grapes")
		void shouldNotAddDuplicateGrapes() {
			wine.addGrape(grape1, null);
			wine.addGrape(grape1, BigDecimal.ONE);
			assertEquals(1, wine.getGrapeComposition().size());
		}

		@Test
		@DisplayName("Should remove grape successfully")
		void shouldRemoveGrape() {
			wine.addGrape(grape1, null);
			wine.removeGrape(grape1);
			assertThat(wine.getGrapeComposition()).doesNotContain(new WineGrapeComposition(wine, grape1, null));
		}

		@Test
		@DisplayName("Should clear grapes")
		void shouldClearGrapes() {
			wine.addGrape(grape1, null);
			wine.clearGrapeComposition();
			assertTrue(wine.getGrapeComposition().isEmpty());
		}

		@Test
		@DisplayName("Should throw NullPointerException when adding null grape")
		void shouldThrowExceptionWhenAddingNullGrape() {
			assertThrows(NullPointerException.class, () -> wine.addGrape(null, null));
		}

		@Test
		@DisplayName("Should throw IllegalArgumentException when adding grape with percentage < 0")
		void shouldThrowIllegalArgumentExceptionWhenAddingGrapeWithPercentageLessThanZero() {
			assertThrows(IllegalArgumentException.class, () -> wine.addGrape(grape1, BigDecimal.valueOf(-1)));
		}

		@Test
		@DisplayName("Should throw IllegalArgumentException when adding grape with percentage > 1")
		void shouldThrowIllegalArgumentExceptionWhenAddingGrapeWithPercentageGreaterThanZero() {
			assertThrows(IllegalArgumentException.class, () -> wine.addGrape(grape1, BigDecimal.valueOf(1.1)));
		}

		@Test
		@DisplayName("Should throw NullPointerException when removing null grape")
		void shouldThrowExceptionWhenRemovingNullGrape() {
			assertThrows(NullPointerException.class, () -> wine.removeGrape(null));
		}

		@Test
		@DisplayName("Should return unmodifiable grape composition set")
		void shouldReturnUnmodifiableSet() {
			Set<WineGrapeComposition> composition = wine.getGrapeComposition();
			assertThrows(UnsupportedOperationException.class,
					() -> composition.add(new WineGrapeComposition(wine, grape1, null)));
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
			course = new Course(event, 1, "Test Cook");
			course.setDish(dish);
			course.setWine(wine);
		}

		@Test
		@DisplayName("Should add course successfully")
		void shouldAddCourseAndSetCourseWineReference() {
			course.setWine(null);
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
		@DisplayName("Should clear courses")
		void shouldClearCourses() {
			wine.addCourse(course);
			wine.clearCourses();
			assertTrue(wine.getCourses().isEmpty());
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
		@DisplayName("Should return unmodifiable course set")
		void shouldReturnUnmodifiableSet() {
			Set<Course> courses = wine.getCourses();
			assertThrows(UnsupportedOperationException.class, () -> courses.add(course));
		}

	}

	@Nested
	@DisplayName("Equals and HashCode Tests")
	class EqualsAndHashCodeTests {

		@Test
		@DisplayName("Wines with null IDs should not be equal")
		void winesWithNullIdsShouldNotBeEqual() {
			Wine wine1 = TestWineBuilder.builder().withId(null).build();
			Wine wine2 = TestWineBuilder.builder().withId(null).build();
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
			Wine wine1 = TestWineBuilder.builder().withId(1L).build();
			Wine wine2 = TestWineBuilder.builder().withId(1L).build();
			assertEquals(wine1, wine2);
		}

		@Test
		@DisplayName("Wines with different IDs should not be equal")
		void winesWithDifferentIdsShouldNotBeEqual() {
			Wine wine1 = TestWineBuilder.builder().withId(1L).build();
			Wine wine2 = TestWineBuilder.builder().withId(2L).build();
			assertNotEquals(wine1, wine2);
		}

	}

}