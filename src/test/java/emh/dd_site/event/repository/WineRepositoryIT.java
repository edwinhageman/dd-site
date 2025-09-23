package emh.dd_site.event.repository;

import emh.dd_site.TestcontainersConfig;
import emh.dd_site.event.WineType;
import emh.dd_site.event.entity.Course;
import emh.dd_site.event.entity.Dish;
import emh.dd_site.event.entity.Event;
import emh.dd_site.event.entity.Wine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@Import(TestcontainersConfig.class)
class WineRepositoryIT {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private WineRepository wineRepository;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Test
	void shouldSaveAndRetrieveWine() {
		// Arrange
		Wine wine = new Wine("Château Margaux", WineType.RED, "Cabernet Sauvignon", "France");
		wine.setRegion("Bordeaux");
		wine.setYear(Year.of(2015));

		// Act
		Wine savedWine = wineRepository.save(wine);

		// Assert
		Optional<Wine> retrievedWine = wineRepository.findById(savedWine.getId());
		assertTrue(retrievedWine.isPresent());
		assertEquals(wine.getName(), retrievedWine.get().getName());
		assertEquals(wine.getType(), retrievedWine.get().getType());
		assertEquals(wine.getGrape(), retrievedWine.get().getGrape());
		assertEquals(wine.getCountry(), retrievedWine.get().getCountry());
		assertEquals(wine.getRegion(), retrievedWine.get().getRegion());
		assertEquals(wine.getYear(), retrievedWine.get().getYear());
	}

	@Test
	void shouldUpdateWine() {
		// Arrange
		Wine wine = new Wine("Original Wine", WineType.RED, "Merlot", "France");
		Wine savedWine = wineRepository.save(wine);

		// Act
		savedWine.setName("Updated Wine");
		savedWine.setType(WineType.WHITE);
		savedWine.setGrape("Chardonnay");
		Wine updatedWine = wineRepository.save(savedWine);

		// Assert
		assertEquals("Updated Wine", updatedWine.getName());
		assertEquals(WineType.WHITE, updatedWine.getType());
		assertEquals("Chardonnay", updatedWine.getGrape());
	}

	@Test
	void shouldDeleteWine() {
		// Arrange
		Wine wine = new Wine("Test Wine", WineType.RED, "Merlot", "France");
		Wine savedWine = wineRepository.save(wine);

		// Act
		wineRepository.deleteById(savedWine.getId());

		// Assert
		Optional<Wine> deletedWine = wineRepository.findById(savedWine.getId());
		assertFalse(deletedWine.isPresent());
	}

	@Test
	void shouldPaginateAndSortWines() {
		// Arrange
		Wine wine1 = new Wine("Wine A", WineType.RED, "Merlot", "France");
		Wine wine2 = new Wine("Wine B", WineType.WHITE, "Chardonnay", "Italy");
		Wine wine3 = new Wine("Wine C", WineType.ROSE, "Grenache", "Spain");
		wineRepository.saveAll(List.of(wine1, wine2, wine3));

		// Act
		Page<Wine> winePage = wineRepository.findAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name")));

		// Assert
		assertThat(winePage.getContent()).hasSize(2);
		assertThat(winePage.getTotalElements()).isEqualTo(3);
		assertThat(winePage.getTotalPages()).isEqualTo(2);
		assertThat(winePage.getContent().get(0).getName()).isEqualTo("Wine A");
		assertThat(winePage.getContent().get(1).getName()).isEqualTo("Wine B");
	}

	@Test
	void shouldFineWineByIdAndFetchRelatedCourses() {
		// Arrange
		Wine wine = new Wine("Test Wine", WineType.RED, "Merlot", "France");
		wine = wineRepository.save(wine);

		Event event = new Event(LocalDate.now(), "Test Event");
		event = eventRepository.save(event);

		Course course1 = new Course(event, 1, "Cook A");
		course1.setDish(new Dish("Test Dish 1"));
		course1.setWine(wine);
		Course course2 = new Course(event, 2, "Cook B");
		course2.setDish(new Dish("Test Dish 2"));
		course2.setWine(wine);

		// Act
		courseRepository.saveAll(List.of(course1, course2));
		entityManager.flush();

		Wine retrievedWine = wineRepository.findByIdWithCourses(wine.getId()).orElseThrow();

		// Assert
		assertThat(retrievedWine.getCourses()).hasSize(2);
		assertThat(retrievedWine.getCourses()).extracting(Course::getCourseNo).containsExactlyInAnyOrder(1, 2);
	}

	@Test
	void shouldFindWineByEventId() {
		// Arrange
		Wine wine1 = new Wine("Test Wine 1", WineType.RED, "Merlot", "France");
		Wine wine2 = new Wine("Test Wine 2", WineType.WHITE, "Chardonnay", "France");
		wine1 = wineRepository.save(wine1);
		wine2 = wineRepository.save(wine2);

		Event event = new Event(LocalDate.now(), "Test Event");
		event = eventRepository.save(event);

		Course course1 = new Course(event, 1, "Cook A");
		course1.setDish(new Dish("Test Dish 1"));
		course1.setWine(wine1);
		Course course2 = new Course(event, 2, "Cook B");
		course2.setDish(new Dish("Test Dish 2"));
		course2.setWine(wine2);

		// Act
		courseRepository.saveAll(List.of(course1, course2));
		entityManager.flush();

		Page<Wine> winePage = wineRepository.findByEventId(event.getId(),
				PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name")));

		// Assert
		assertThat(winePage.getContent()).hasSize(2);
		assertThat(winePage.getTotalElements()).isEqualTo(2);
		assertThat(winePage.getTotalPages()).isEqualTo(1);
		assertThat(winePage.getContent().get(0).getName()).isEqualTo("Test Wine 1");
		assertThat(winePage.getContent().get(1).getName()).isEqualTo("Test Wine 2");
	}

	@Test
	void shouldHandleWineCourseRelationshipManagement() {
		// Arrange
		Wine wine = new Wine("Test Wine", WineType.RED, "Merlot", "France");
		wineRepository.save(wine);

		Event event = new Event(LocalDate.now(), "Test Event");
		eventRepository.save(event);

		Course course = new Course(event, 1, "Test Cook");
		course.setDish(new Dish("Test Dish"));
		course.setWine(wine);
		courseRepository.save(course);

		// Act & Assert
		// Test adding course
		wine.addCourse(course);
		wineRepository.save(wine);
		entityManager.flush();
		entityManager.clear();

		Wine retrievedWine = wineRepository.findByIdWithCourses(wine.getId()).orElseThrow();
		assertThat(retrievedWine.getCourses()).hasSize(1);
		assertTrue(retrievedWine.getCourses().contains(course));

		// Test removing course
		wine.removeCourse(course);
		wineRepository.save(wine);
		entityManager.flush();
		entityManager.clear();

		retrievedWine = wineRepository.findById(wine.getId()).orElseThrow();
		assertThat(retrievedWine.getCourses()).isEmpty();
	}

	@Test
	void shouldHandleOptionalFields() {
		// Arrange
		Wine wine = new Wine("Test Wine", WineType.RED, "Merlot", "France");
		// Don't set optional fields (region and year)

		// Act
		Wine savedWine = wineRepository.save(wine);
		Wine retrievedWine = wineRepository.findById(savedWine.getId()).orElseThrow();

		// Assert
		assertNull(retrievedWine.getRegion());
		assertNull(retrievedWine.getYear());
		assertNotNull(retrievedWine.getId());
		assertEquals("Test Wine", retrievedWine.getName());
	}

	@Test
	void shouldHandleAllWineTypes() {
		// Arrange & Act
		Wine redWine = wineRepository.save(new Wine("Red Wine", WineType.RED, "Merlot", "France"));
		Wine whiteWine = wineRepository.save(new Wine("White Wine", WineType.WHITE, "Chardonnay", "Italy"));
		Wine roseWine = wineRepository.save(new Wine("Rose Wine", WineType.ROSE, "Grenache", "Spain"));

		// Assert
		List<Wine> wines = wineRepository.findAll();
		assertThat(wines).extracting(Wine::getType)
			.containsExactlyInAnyOrder(WineType.RED, WineType.WHITE, WineType.ROSE);
	}

}