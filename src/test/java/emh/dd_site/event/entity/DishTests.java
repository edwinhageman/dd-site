
package emh.dd_site.event.entity;

import emh.dd_site.event.WineType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Dish Entity")
class DishTests {

	private Dish dish;

	private final String testName = "Test Dish";

	@BeforeEach
	void setUp() {
		dish = TestDishBuilder.aDish().withName(testName).build();
	}

	@Nested
	@DisplayName("Constructor Tests")
	class ConstructorTests {

		@Test
		@DisplayName("Should create dish with required parameters")
		void shouldCreateDishWithRequiredParameters() {
			assertNotNull(dish);
			assertEquals(testName, dish.getName());
			assertNull(dish.getCourse());
			assertNull(dish.getMainIngredient());
		}

		@Test
		@DisplayName("Should throw NullPointerException when name is null")
		void shouldThrowExceptionWhenNameIsNull() {
			assertThrows(NullPointerException.class, () -> new Dish(null));
		}

	}

	@Nested
	@DisplayName("Property Tests")
	class PropertyTests {

		@Test
		@DisplayName("Should set and get name")
		void shouldSetAndGetName() {
			String newName = "New Dish";
			dish.setName(newName);
			assertEquals(newName, dish.getName());
		}

		@Test
		@DisplayName("Should set and get main ingredient")
		void shouldSetAndGetMainIngredient() {
			String ingredient = "Chicken";
			dish.setMainIngredient(ingredient);
			assertEquals(ingredient, dish.getMainIngredient());
		}

	}

	@Nested
	@DisplayName("Course Relationship Tests")
	class CourseRelationshipTests {

		private Course course;

		private Dish dish;

		private Event event;

		private Wine wine;

		@BeforeEach
		void setUp() {
			event = new Event(LocalDate.now(), "Test Host");
			wine = new Wine("Test Wine", WineType.RED, "Merlot", "France");
			dish = new Dish("Test Dish");
			course = new Course(event, 1, "Test Cook");
			course.setDish(new Dish("Initial dish"));
			course.setWine(wine);
		}

		@Test
		@DisplayName("Should set course successfully")
		void shouldSetCourse() {
			dish.setCourse(course);
			assertEquals(course, dish.getCourse());
			assertEquals(dish, course.getDish());
		}

		@Test
		@DisplayName("Should handle bidirectional relationship")
		void shouldHandleBidirectionalRelationship() {
			dish.setCourse(course);
			course.setDish(dish);
			assertEquals(course, dish.getCourse());
			assertEquals(dish, course.getDish());
		}

	}

	@Nested
	@DisplayName("Equals and HashCode Tests")
	class EqualsAndHashCodeTests {

		@Test
		@DisplayName("Dishes with null IDs should not be equal")
		void dishesWithNullIdsShouldNotBeEqual() {
			Dish dish1 = TestDishBuilder.aDish().withId(null).build();
			Dish dish2 = TestDishBuilder.aDish().withId(null).build();
			assertNotEquals(dish1, dish2);
		}

		@Test
		@DisplayName("Same dish should be equal to itself")
		void sameDishShouldBeEqualToItself() {
			assertEquals(dish, dish);
		}

		@Test
		@DisplayName("Dish should not be equal to null")
		void dishShouldNotBeEqualToNull() {
			assertNotEquals(null, dish);
		}

		@Test
		@DisplayName("Dishes with same non-null ID should be equal")
		void dishesWithSameNonNullIdShouldBeEqual() {
			Dish dish1 = TestDishBuilder.aDish().withId(1L).build();
			Dish dish2 = TestDishBuilder.aDish().withId(1L).build();
			assertEquals(dish1, dish2);
		}

		@Test
		@DisplayName("Dishes with different IDs should not be equal")
		void dishesWithDifferentIdsShouldNotBeEqual() {
			Dish dish1 = TestDishBuilder.aDish().withId(1L).build();
			Dish dish2 = TestDishBuilder.aDish().withId(2L).build();
			assertNotEquals(dish1, dish2);
		}

	}

	@Test
	@DisplayName("Should generate meaningful toString output")
	void shouldGenerateMeaningfulToString() {
		Dish testDish = TestDishBuilder.aDish().withName(testName).withMainIngredient("Chicken").build();

		String toString = testDish.toString();
		assertNotNull(toString);
		assertTrue(toString.contains(testName));
		assertTrue(toString.contains("Chicken"));
	}

}
