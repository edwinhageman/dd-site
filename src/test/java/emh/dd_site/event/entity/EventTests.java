package emh.dd_site.event.entity;

import emh.dd_site.event.WineType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Event Entity")
class EventTests {

	private Event event;

	private final LocalDate testDate = LocalDate.of(2024, 1, 1);

	private final String testHost = "Test Host";

	@BeforeEach
	void setUp() {
		event = TestEventBuilder.anEvent().withDate(testDate).withHost(testHost).build();
	}

	@Nested
	@DisplayName("Constructor Tests")
	class ConstructorTests {

		@Test
		@DisplayName("Should create event with required parameters")
		void shouldCreateEventWithRequiredParameters() {
			assertNotNull(event);
			assertEquals(testDate, event.getDate());
			assertEquals(testHost, event.getHost());
			assertTrue(event.getCourses().isEmpty());
		}

		@Test
		@DisplayName("Should throw NullPointerException when date is null")
		void shouldThrowExceptionWhenDateIsNull() {
			assertThrows(NullPointerException.class, () -> new Event(null, testHost));
		}

		@Test
		@DisplayName("Should throw NullPointerException when host is null")
		void shouldThrowExceptionWhenHostIsNull() {
			assertThrows(NullPointerException.class, () -> new Event(testDate, null));
		}

	}

	@Nested
	@DisplayName("Basic Property Tests")
	class PropertyTests {

		@Test
		@DisplayName("Should set and get location")
		void shouldSetAndGetLocation() {
			String location = "Test Location";
			event.setLocation(location);
			assertEquals(location, event.getLocation());
		}

		@Test
		@DisplayName("Should set and get host")
		void shouldSetAndGetHost() {
			String newHost = "New Host";
			event.setHost(newHost);
			assertEquals(newHost, event.getHost());
		}

		@Test
		@DisplayName("Should set and get date")
		void shouldSetAndGetDate() {
			LocalDate newDate = LocalDate.of(2024, 2, 1);
			event.setDate(newDate);
			assertEquals(newDate, event.getDate());
		}

	}

	@Nested
	@DisplayName("Course Management Tests")
	class CourseManagementTests {

		private Course course;

		@BeforeEach
		void setUp() {
			course = new Course(event, 1, "Test Cook", new Dish("Test Dish"),
					new Wine("Test Wine", WineType.RED, "Merlot", "France"));
		}

		@Test
		@DisplayName("Should add course successfully")
		void shouldAddCourse() {
			event.addCourse(course);
			assertTrue(event.getCourses().contains(course));
			assertEquals(event, course.getEvent());
		}

		@Test
		@DisplayName("Should not add duplicate course")
		void shouldNotAddDuplicateCourse() {
			event.addCourse(course);
			event.addCourse(course);
			assertEquals(1, event.getCourses().size());
		}

		@Test
		@DisplayName("Should remove course successfully")
		void shouldRemoveCourse() {
			event.addCourse(course);
			event.removeCourse(course);
			assertFalse(event.getCourses().contains(course));
		}

		@Test
		@DisplayName("Should throw NullPointerException when adding null course")
		void shouldThrowExceptionWhenAddingNullCourse() {
			assertThrows(NullPointerException.class, () -> event.addCourse(null));
		}

		@Test
		@DisplayName("Should throw NullPointerException when removing null course")
		void shouldThrowExceptionWhenRemovingNullCourse() {
			assertThrows(NullPointerException.class, () -> event.removeCourse(null));
		}

		@Test
		@DisplayName("Should return unmodifiable course list")
		void shouldReturnUnmodifiableList() {
			List<Course> courses = event.getCourses();
			assertThrows(UnsupportedOperationException.class, () -> courses.add(course));
		}

	}

	@Nested
	@DisplayName("Equals and HashCode Tests")
	class EqualsAndHashCodeTests {

		@Test
		@DisplayName("Events with null IDs should not be equal")
		void eventsWithNullIdsShouldNotBeEqual() {
			Event event1 = TestEventBuilder.anEvent().withId(null).build();
			Event event2 = TestEventBuilder.anEvent().withId(null).build();
			assertNotEquals(event1, event2);
		}

		@Test
		@DisplayName("Same event should be equal to itself")
		void sameEventShouldBeEqualToItself() {
			assertEquals(event, event);
		}

		@Test
		@DisplayName("Event should not be equal to null")
		void eventShouldNotBeEqualToNull() {
			assertNotEquals(null, event);
		}

		@Test
		@DisplayName("Events with same non-null ID should be equal")
		void eventsWithSameNonNullIdShouldBeEqual() {
			Event event1 = TestEventBuilder.anEvent().withId(1L).build();
			Event event2 = TestEventBuilder.anEvent().withId(1L).build();
			assertEquals(event1, event2);
		}

		@Test
		@DisplayName("Events with same non-null ID should be equal")
		void eventsWithDifferentIdShouldNotBeEqual() {
			Event event1 = TestEventBuilder.anEvent().withId(1L).build();
			Event event2 = TestEventBuilder.anEvent().withId(2L).build();
			assertNotEquals(event1, event2);
		}

	}

	@Test
	@DisplayName("Should generate meaningful toString output")
	void shouldGenerateMeaningfulToString() {
		String toString = event.toString();
		assertNotNull(toString);
		assertTrue(toString.contains(testHost));
		assertTrue(toString.contains(testDate.toString()));
	}

}