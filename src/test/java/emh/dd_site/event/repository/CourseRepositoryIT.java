package emh.dd_site.event.repository;

import emh.dd_site.TestcontainersConfig;
import emh.dd_site.event.WineType;
import emh.dd_site.event.entity.Course;
import emh.dd_site.event.entity.Dish;
import emh.dd_site.event.entity.Event;
import emh.dd_site.event.entity.Wine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@Import(TestcontainersConfig.class)
class CourseRepositoryIT {

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private WineRepository wineRepository;

	private Event testEvent;

	private Dish testDish;

	private Wine testWine;

	@BeforeEach
	void setUp() {
		// Create test event
		testEvent = new Event(LocalDate.now().plusDays(7), "Test Event");
		testEvent.setLocation("Test Location");
		eventRepository.save(testEvent);

		// Create test dish
		testDish = new Dish("Test Dish");

		// Create test wine
		testWine = new Wine("Test Wine", WineType.WHITE, "Sauvignon Blanc", "France");
		wineRepository.save(testWine);
	}

	@Test
	void shouldSaveAndRetrieveCourse() {
		// Arrange
		Course course = new Course(testEvent, 1, "Test Cook");
		course.setDish(testDish);
		course.setWine(testWine);

		// Act
		Course savedCourse = courseRepository.save(course);

		// Assert
		Optional<Course> retrievedCourse = courseRepository.findById(savedCourse.getId());
		assertTrue(retrievedCourse.isPresent());
		assertEquals(course.getCourseNo(), retrievedCourse.get().getCourseNo());
		assertEquals(course.getCook(), retrievedCourse.get().getCook());
		assertEquals(course.getEvent().getId(), retrievedCourse.get().getEvent().getId());
		assertEquals(course.getDish().getId(), retrievedCourse.get().getDish().getId());
		assertEquals(course.getWine().getId(), retrievedCourse.get().getWine().getId());
	}

	@Test
	void shouldUpdateCourse() {
		// Arrange
		Course course = new Course(testEvent, 1, "Original Cook");
		course.setDish(testDish);
		course.setWine(testWine);
		Course savedCourse = courseRepository.save(course);

		// Act
		savedCourse.setCook("Updated Cook");
		savedCourse.setCourseNo(2);
		Course updatedCourse = courseRepository.save(savedCourse);

		// Assert
		assertEquals("Updated Cook", updatedCourse.getCook());
		assertEquals(2, updatedCourse.getCourseNo());
	}

	@Test
	void shouldDeleteCourse() {
		// Arrange
		Course course = new Course(testEvent, 1, "Test Cook");
		course.setDish(testDish);
		course.setWine(testWine);

		Course savedCourse = courseRepository.save(course);

		// Act
		courseRepository.deleteById(savedCourse.getId());

		// Assert
		Optional<Course> deletedCourse = courseRepository.findById(savedCourse.getId());
		assertFalse(deletedCourse.isPresent());
	}

	@Test
	void shouldPaginateAndSortCourses() {
		// Arrange
		Course course1 = new Course(testEvent, 1, "Cook A");
		course1.setDish(new Dish("test dish 1"));
		course1.setWine(testWine);
		Course course2 = new Course(testEvent, 2, "Cook B");
		course2.setDish(new Dish("test dish 2"));
		course2.setWine(testWine);
		Course course3 = new Course(testEvent, 3, "Cook C");
		course3.setDish(new Dish("test dish 3"));
		course3.setWine(testWine);
		courseRepository.saveAll(List.of(course1, course2, course3));

		// Act
		Page<Course> coursePage = courseRepository
			.findAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "courseNo")));

		// Assert
		assertThat(coursePage.getContent()).hasSize(2);
		assertThat(coursePage.getTotalElements()).isEqualTo(3);
		assertThat(coursePage.getTotalPages()).isEqualTo(2);
		assertThat(coursePage.getContent().get(0).getCourseNo()).isEqualTo(1);
		assertThat(coursePage.getContent().get(1).getCourseNo()).isEqualTo(2);
	}

	@Test
	void shouldMaintainRelationshipsWhenSaving() {
		// Arrange
		Course course = new Course(testEvent, 1, "Test Cook");
		course.setDish(testDish);
		course.setWine(testWine);

		// Act
		Course savedCourse = courseRepository.save(course);

		// Assert
		assertNotNull(savedCourse.getId());
		assertEquals(testEvent.getId(), savedCourse.getEvent().getId());
		assertEquals(testDish.getId(), savedCourse.getDish().getId());
		assertEquals(testWine.getId(), savedCourse.getWine().getId());

		// Verify bidirectional relationships
		assertEquals(savedCourse, savedCourse.getDish().getCourse());
		assertTrue(savedCourse.getWine().getCourses().contains(savedCourse));
	}

	@Test
	void shouldHandleMultipleCoursesForSameEvent() {
		// Arrange
		Course firstCourse = new Course(testEvent, 1, "Cook A");
		firstCourse.setDish(new Dish("test dish 1"));
		firstCourse.setWine(testWine);
		Course secondCourse = new Course(testEvent, 2, "Cook B");
		secondCourse.setDish(new Dish("test dish 2"));
		secondCourse.setWine(testWine);

		// Act
		courseRepository.saveAll(List.of(firstCourse, secondCourse));

		// Assert
		List<Course> allCourses = courseRepository.findAll();

		assertThat(allCourses).hasSize(2);
		assertThat(allCourses).extracting(Course::getEvent).containsOnly(testEvent);
	}

	@Test
	void shouldFindByEventId_withPaginationAndSorting() {
		// Arrange: three courses for the primary event
		Course c1 = new Course(testEvent, 1, "Cook A");
		Course c2 = new Course(testEvent, 2, "Cook B");
		Course c3 = new Course(testEvent, 3, "Cook C");
		courseRepository.saveAll(List.of(c1, c2, c3));

		// And an extra course for another event that must not be returned
		Event otherEvent = new Event(LocalDate.now().plusDays(10), "Other Event");
		otherEvent.setLocation("Elsewhere");
		eventRepository.save(otherEvent);
		courseRepository.save(new Course(otherEvent, 1, "Other Cook"));

		// Act
		Page<Course> page = courseRepository.findByEventId(testEvent.getId(),
				PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "courseNo")));

		// Assert
		assertThat(page.getContent()).hasSize(2);
		assertThat(page.getTotalElements()).isEqualTo(3);
		assertThat(page.getTotalPages()).isEqualTo(2);
		assertThat(page.getContent()).extracting(Course::getCourseNo).containsExactly(1, 2);
		assertThat(page.getContent()).extracting(Course::getEvent).containsOnly(testEvent);
	}

	@Test
	void shouldReturnEmptyPage_whenFindingByUnknownEventId() {
		// Arrange
		long unknownEventId = 999_999L;

		// Act
		Page<Course> page = courseRepository.findByEventId(unknownEventId,
				PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "courseNo")));

		// Assert
		assertThat(page.getContent()).isEmpty();
		assertThat(page.getTotalElements()).isZero();
		assertThat(page.getTotalPages()).isZero();
	}

}