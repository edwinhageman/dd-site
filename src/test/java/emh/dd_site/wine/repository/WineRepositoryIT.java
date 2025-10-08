package emh.dd_site.wine.repository;

import emh.dd_site.TestcontainersConfig;
import emh.dd_site.event.entity.Course;
import emh.dd_site.event.entity.Dish;
import emh.dd_site.event.entity.Event;
import emh.dd_site.event.repository.CourseRepository;
import emh.dd_site.event.repository.EventRepository;
import emh.dd_site.wine.entity.Grape;
import emh.dd_site.wine.entity.Wine;
import emh.dd_site.wine.entity.WineGrapeComposition;
import emh.dd_site.wine.entity.WineStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
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
	private WineStyleRepository wineStyleRepository;

	@Autowired
	private GrapeRepository grapeRepository;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private CourseRepository courseRepository;

	private WineStyle style1, style2;

	private Grape grape1, grape2;

	private Wine wine1, wine2, wine3;

	@BeforeEach
	void setUp() {
		style1 = new WineStyle("Style 1");
		style2 = new WineStyle("Style 2");

		wineStyleRepository.saveAll(List.of(style1, style2));

		grape1 = new Grape("Grape 1");
		grape2 = new Grape("Grape 2");

		grapeRepository.saveAll(List.of(grape1, grape2));

		wine1 = new Wine("Wine name 1");
		wine1.setWinery("Winery name 1");
		wine1.setCountry("Country 1");
		wine1.setRegion("Region 1");
		wine1.setAppellation("Appellation 1");
		wine1.setVintage(Year.of(2015));
		wine1.addStyle(style1);
		wine1.addGrape(grape1, BigDecimal.valueOf(1));

		wine2 = new Wine("Wine name 2");
		wine2.setWinery("Winery name 2");
		wine2.setCountry("Country 2");
		wine2.setRegion("Region 2");
		wine2.setAppellation("Appellation 2");
		wine2.setVintage(Year.of(2016));
		wine2.addStyle(style2);
		wine2.addGrape(grape2, null);

		wine3 = new Wine("Wine name 3");
		wine3.setWinery("Winery name 3");
		wine3.setCountry("Country 3");
		wine3.setRegion("Region 3");
		wine3.setAppellation("Appellation 3");
		wine3.setVintage(Year.of(2017));
		wine3.addStyle(style1);
		wine3.addStyle(style2);
		wine3.addGrape(grape1, BigDecimal.valueOf(.4));
		wine3.addGrape(grape2, BigDecimal.valueOf(.6));

		wineRepository.saveAll(List.of(wine1, wine2, wine3));
	}

	@Test
	@DisplayName("Should save and retrieve wine styles")
	void shouldSaveAndRetrieveWine() {
		var result = wineRepository.findById(wine1.getId());

		assertTrue(result.isPresent());
		assertEquals(wine1.getName(), result.get().getName());
		assertEquals(wine1.getWinery(), result.get().getWinery());
		assertEquals(wine1.getCountry(), result.get().getCountry());
		assertEquals(wine1.getRegion(), result.get().getRegion());
		assertEquals(wine1.getAppellation(), result.get().getAppellation());
		assertEquals(wine1.getVintage(), result.get().getVintage());
	}

	@Test
	@DisplayName("Should update wine")
	void shouldUpdateWine() {
		wine1.setName("Updated Wine name");
		wine1.setWinery("Updated Winery name");
		wine1.setCountry("Updated Country");
		wine1.setRegion("Updated Region");
		wine1.setAppellation("Updated Appellation");
		wine1.setVintage(Year.of(2016));

		Wine updatedWine = wineRepository.save(wine1);

		assertEquals("Updated Wine name", updatedWine.getName());
		assertEquals("Updated Winery name", updatedWine.getWinery());
		assertEquals("Updated Country", updatedWine.getCountry());
		assertEquals("Updated Region", updatedWine.getRegion());
		assertEquals("Updated Appellation", updatedWine.getAppellation());
		assertEquals(Year.of(2016), updatedWine.getVintage());
	}

	@Test
	@DisplayName("Should delete wine")
	void shouldDeleteWine() {
		wineRepository.deleteById(wine1.getId());

		Optional<Wine> deletedWine = wineRepository.findById(wine1.getId());
		assertFalse(deletedWine.isPresent());
	}

	@Test
	@DisplayName("Should paginate and sort wines")
	void shouldPaginateAndSortWines() {
		var result = wineRepository.findAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name")));

		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(3);
		assertThat(result.getTotalPages()).isEqualTo(2);
		assertThat(result.getContent().get(0).getName()).isEqualTo("Wine name 1");
		assertThat(result.getContent().get(1).getName()).isEqualTo("Wine name 2");
	}

	@Test
	@DisplayName("findWithStylesAndGrapesById should return wine with related styles and grapes")
	void findWithStylesAndGrapesById_shouldReturnWineWithStylesAndGrapes() {
		var result = wineRepository.findWithStylesAndGrapesById(wine3.getId()).orElseThrow();

		assertThat(result.getStyles()).hasSize(2);
		assertThat(result.getStyles()).extracting(WineStyle::getName).containsExactlyInAnyOrder("Style 1", "Style 2");
		assertThat(result.getGrapeComposition()).hasSize(2);
		assertThat(result.getGrapeComposition()).extracting(w -> w.getGrape().getName())
			.containsExactlyInAnyOrder("Grape 1", "Grape 2");
	}

	@Test
	@DisplayName("findAllIds should return page with ids")
	void findAllIds_shouldReturnPageWithIds() {
		var pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));
		var result = wineRepository.findAllIds(pageRequest);

		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(3);
		assertThat(result.getTotalPages()).isEqualTo(2);
		assertThat(result.getContent()).containsOnly(wine3.getId(), wine2.getId());
	}

	@Test
	@DisplayName("findIdsByCourseEventId should return page with ids")
	void findIdsByCourseEventId_shouldReturnPageWithIds() {
		var event = new Event(LocalDate.now(), "Test Event");
		event = eventRepository.save(event);

		var course1 = new Course(event, 1, "Cook A");
		course1.setDish(new Dish("Test Dish 1"));
		course1.setWine(wine1);
		var course2 = new Course(event, 2, "Cook B");
		course2.setDish(new Dish("Test Dish 2"));
		course2.setWine(wine2);

		courseRepository.saveAll(List.of(course1, course2));
		entityManager.flush();

		var pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "id"));
		var result = wineRepository.findAllIds(pageRequest);

		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(3);
		assertThat(result.getTotalPages()).isEqualTo(2);
		assertThat(result.getContent()).containsOnly(wine1.getId(), wine2.getId());
	}

	@Test
	@DisplayName("findAllWithStylesAndGrapesByIdIn should return wines with related styles and grapes")
	void findAllWithStylesAndGrapesByIdId_shouldReturnWinesWithStylesAndGrapes() {
		var result = wineRepository
			.findAllWithStylesAndGrapesByIdIn(List.of(wine1.getId(), wine2.getId(), wine3.getId()));

		assertThat(result).hasSize(3);
		assertThat(result.get(0)).satisfies(wine -> {
			assertThat(wine.getName()).isEqualTo("Wine name 1");
			assertThat(wine.getStyles()).extracting(WineStyle::getName).containsExactlyInAnyOrder("Style 1");
			assertThat(wine.getGrapeComposition()).extracting(w -> w.getGrape().getName())
				.containsExactlyInAnyOrder("Grape 1");
		});
		assertThat(result.get(1)).satisfies(wine -> {
			assertThat(wine.getName()).isEqualTo("Wine name 2");
			assertThat(wine.getStyles()).extracting(WineStyle::getName).containsExactlyInAnyOrder("Style 2");
			assertThat(wine.getGrapeComposition()).extracting(w -> w.getGrape().getName())
				.containsExactlyInAnyOrder("Grape 2");
		});
		assertThat(result.get(2)).satisfies(wine -> {
			assertThat(wine.getName()).isEqualTo("Wine name 3");
			assertThat(wine.getStyles()).hasSize(2);
			assertThat(wine.getGrapeComposition()).hasSize(2);
		});
	}

	@Test
	void shouldHandleWineStyleRelationshipManagement() {
		var result = wineRepository.findWithStylesAndGrapesById(wine1.getId()).orElseThrow();

		assertThat(result.getStyles()).hasSize(1);
		assertThat(result.getStyles()).contains(style1);

		wine1.removeStyle(style1);
		wineRepository.save(wine1);
		entityManager.flush();
		entityManager.clear();

		result = wineRepository.findById(wine1.getId()).orElseThrow();
		assertThat(result.getStyles()).isEmpty();
	}

	@Test
	void shouldHandleGrapeCompositionRelationshipManagement() {
		var result = wineRepository.findWithStylesAndGrapesById(wine1.getId()).orElseThrow();

		assertThat(result.getGrapeComposition()).hasSize(1);
		assertThat(result.getGrapeComposition()).contains(new WineGrapeComposition(wine1, grape1, null));

		wine1.removeGrape(grape1);
		wineRepository.save(wine1);
		entityManager.flush();
		entityManager.clear();

		result = wineRepository.findById(wine1.getId()).orElseThrow();
		assertThat(result.getGrapeComposition()).isEmpty();
	}

	@Test
	void shouldHandleOptionalFields() {
		var wine = new Wine("Test Wine");
		// Don't set optional fields

		wineRepository.save(wine);
		var result = wineRepository.findById(wine.getId()).orElseThrow();

		assertNull(result.getWinery());
		assertNull(result.getCountry());
		assertNull(result.getRegion());
		assertNull(result.getAppellation());
		assertNull(result.getVintage());
		assertNotNull(result.getId());
		assertEquals("Test Wine", result.getName());
	}

}