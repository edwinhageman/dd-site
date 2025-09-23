package emh.dd_site.event.dto;

import emh.dd_site.event.WineType;
import emh.dd_site.event.entity.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("WineMapper tests")
class WineMapperTest {

	@Mock
	private EntityManagerFactory emf;

	@Mock
	private PersistenceUnitUtil persistenceUtil;

	private WineMapper mapper;

	private Course testCourse1, testCourse2;

	private Wine testWine;

	private WineUpsertRequest testRequest;

	@BeforeEach
	void setUp() {
		mapper = new WineMapper(emf, new CourseMapper(new EventMapper(), new DishMapper()));
		var testEvent = TestEventBuilder.anEvent()
			.withId(1L)
			.withHost("Test Host")
			.withLocation("Test Location")
			.build();
		testCourse1 = TestCourseBuilder.aCourse()
			.withId(1L)
			.withEvent(testEvent)
			.withCourseNo(1)
			.withCook("Cook name 1")
			.build();
		testCourse2 = TestCourseBuilder.aCourse()
			.withId(2L)
			.withEvent(testEvent)
			.withCourseNo(2)
			.withCook("Cook name 2")
			.build();

		testWine = TestWineBuilder.aWine()
			.withId(1L)
			.withName("Wine Name")
			.withType(WineType.UNKNOWN)
			.withGrape("Grape Name")
			.withCountry("Country Name")
			.withRegion("Region Name")
			.withYear(Year.of(2000))
			.addCourse(testCourse1)
			.addCourse(testCourse2)
			.build();

		testRequest = new WineUpsertRequest("Upsert Dish Name", WineType.RED, "Upsert Grape", "Upsert Country",
				"Upsert Region", Year.of(1900));
	}

	@Nested
	@DisplayName("Wine to WineResponse mapping tests")
	class ToWineResponseTests {

		@BeforeEach
		void setUp() {
			given(emf.getPersistenceUnitUtil()).willReturn(persistenceUtil);
			given(persistenceUtil.isLoaded(testWine.getCourses())).willReturn(true);
		}

		@Test
		@DisplayName("should return null when entity is null")
		void shouldReturnNullWhenEntityIsNull() {
			Mockito.reset(emf, persistenceUtil);
			assertThat(mapper.toWineResponse(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.toWineResponse(testWine);

			assertThat(result).isNotNull().satisfies(wineDto -> {
				assertThat(wineDto.id()).isEqualTo(testWine.getId());
				assertThat(wineDto.name()).isEqualTo(testWine.getName());
				assertThat(wineDto.type()).isEqualTo(testWine.getType());
				assertThat(wineDto.grape()).isEqualTo(testWine.getGrape());
				assertThat(wineDto.country()).isEqualTo(testWine.getCountry());
				assertThat(wineDto.region()).isEqualTo(testWine.getRegion());
				assertThat(wineDto.year()).isEqualTo(testWine.getYear());
				assertThat(wineDto.courses()).isNotNull().hasSize(2);
				assertThat(wineDto.courses().get(0).id()).isEqualTo(testCourse1.getId());
				assertThat(wineDto.courses().get(1).id()).isEqualTo(testCourse2.getId());
			});
		}

		@Test
		@DisplayName("should handle empty courses")
		void shouldHandleEmptyCourses() {
			testWine.clearCourses();
			var result = mapper.toWineResponse(testWine);

			assertThat(result).isNotNull();
			assertThat(result.courses()).isEmpty();
		}

		@Test
		@DisplayName("should handle uninitialized course collection")
		void shouldHandleUninitializedCourseCollection() {
			given(persistenceUtil.isLoaded(testWine.getCourses())).willReturn(false);

			var result = mapper.toWineResponse(testWine);

			assertThat(result).isNotNull();
			assertThat(result.courses()).isEmpty();
			verify(persistenceUtil).isLoaded(testWine.getCourses());
		}

	}

	@Nested
	@DisplayName("Wine from WineUpsertRequest mapping tests")
	class FromWineUpsertRequestTests {

		@Test
		@DisplayName("should return null when request is null")
		void shouldReturnNullWhenRequestIsNull() {
			assertThat(mapper.fromWineUpsertRequest(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.fromWineUpsertRequest(testRequest);

			assertThat(result).isNotNull().satisfies(course -> {
				assertThat(course.getId()).isNull();
				assertThat(course.getName()).isEqualTo(testRequest.name());
				assertThat(course.getType()).isEqualTo(testRequest.type());
				assertThat(course.getGrape()).isEqualTo(testRequest.grape());
				assertThat(course.getCountry()).isEqualTo(testRequest.country());
				assertThat(course.getRegion()).isEqualTo(testRequest.region());
				assertThat(course.getYear()).isEqualTo(testRequest.year());
			});
		}

		@Test
		@DisplayName("should handle null values")
		void shouldHandleNullValues() {
			testRequest = new WineUpsertRequest("Upsert Dish Name", WineType.RED, "Upsert Grape", "Upsert Country",
					null, null);

			var result = mapper.fromWineUpsertRequest(testRequest);

			assertThat(result).isNotNull().satisfies(course -> {
				assertThat(course.getId()).isNull();
				assertThat(course.getName()).isEqualTo(testRequest.name());
				assertThat(course.getType()).isEqualTo(testRequest.type());
				assertThat(course.getGrape()).isEqualTo(testRequest.grape());
				assertThat(course.getCountry()).isEqualTo(testRequest.country());
				assertThat(course.getRegion()).isNull();
				assertThat(course.getYear()).isNull();
			});
		}

	}

	@Nested
	@DisplayName("Wine merge with WineUpsertRequest mapping tests")
	class MergeWithWineUpsertRequestTests {

		@Test
		@DisplayName("should return null when wine is null")
		void shouldReturnWineWhenEventIsNull() {
			assertThat(mapper.mergeWithWineUpsertRequest(null, testRequest)).isNull();
		}

		@Test
		@DisplayName("should return wine when request is null")
		void shouldReturnWineWhenRequestIsNull() {
			assertThat(mapper.mergeWithWineUpsertRequest(testWine, null)).isEqualTo(testWine);
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.mergeWithWineUpsertRequest(testWine, testRequest);

			assertThat(result).isNotNull().satisfies(course -> {
				assertThat(course.getId()).isEqualTo(testWine.getId());
				assertThat(course.getName()).isEqualTo(testRequest.name());
				assertThat(course.getType()).isEqualTo(testRequest.type());
				assertThat(course.getGrape()).isEqualTo(testRequest.grape());
				assertThat(course.getCountry()).isEqualTo(testRequest.country());
				assertThat(course.getRegion()).isEqualTo(testRequest.region());
				assertThat(course.getYear()).isEqualTo(testRequest.year());
			});
		}

		@Test
		@DisplayName("should handle null values")
		void shouldHandleNullValues() {
			testRequest = new WineUpsertRequest("Upsert Dish Name", WineType.RED, "Upsert Grape", "Upsert Country",
					null, null);

			var result = mapper.fromWineUpsertRequest(testRequest);

			assertThat(result).isNotNull().satisfies(course -> {
				assertThat(course.getId()).isNull();
				assertThat(course.getName()).isEqualTo(testRequest.name());
				assertThat(course.getType()).isEqualTo(testRequest.type());
				assertThat(course.getGrape()).isEqualTo(testRequest.grape());
				assertThat(course.getCountry()).isEqualTo(testRequest.country());
				assertThat(course.getRegion()).isNull();
				assertThat(course.getYear()).isNull();
			});
		}

	}

}