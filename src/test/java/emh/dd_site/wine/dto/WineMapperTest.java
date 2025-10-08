package emh.dd_site.wine.dto;

import emh.dd_site.wine.entity.*;
import emh.dd_site.wine.repository.GrapeRepository;
import emh.dd_site.wine.repository.WineStyleRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

	@Mock
	private WineStyleRepository wineStyleRepository;

	@Mock
	private GrapeRepository grapeRepository;

	@Mock
	private WineStyleMapper wineStyleMapper;

	@InjectMocks
	private WineMapper mapper;

	private WineStyle testStyle1, testStyle2;

	private Grape testGrape1, testGrape2;

	private WineGrapeComposition testGrapeComposition1, testGrapeComposition2;

	private Wine testWine;

	private WineUpsertRequest testRequest;

	@BeforeEach
	void setUp() {
		testWine = TestWineBuilder.builder()
			.withId(1L)
			.withName("Wine Name")
			.withWinery("Winery Name")
			.withCountry("Country Name")
			.withRegion("Region Name")
			.withAppellation("Appellation Name")
			.withVintage(Year.of(2000))
			.build();

		testStyle1 = TestWineStyleBuilder.builder().withId(1).withName("Style 1").build();
		testStyle2 = TestWineStyleBuilder.builder().withId(2).withName("Style 2").build();
		testWine.addStyle(testStyle1);
		testWine.addStyle(testStyle2);

		testGrape1 = TestGrapeBuilder.builder().withId(1).withName("Grape 1").build();
		testGrape2 = TestGrapeBuilder.builder().withId(2).withName("Grape 2").build();
		testGrapeComposition1 = new WineGrapeComposition(testWine, testGrape1);
		testGrapeComposition1.setPercentage(BigDecimal.valueOf(.6));
		testGrapeComposition2 = new WineGrapeComposition(testWine, testGrape2);
		testGrapeComposition2.setPercentage(BigDecimal.valueOf(.6));
		testWine.addGrape(testGrape1, null);
		testWine.addGrape(testGrape2, null);

		testRequest = new WineUpsertRequest("Upsert Dish Name", "Upsert Winery", "Upsert Country", "Upsert Region",
				"Upsert Appellation", Year.of(1900), null, null);
	}

	@Nested
	@DisplayName("Wine to WineResponse mapping tests")
	class ToWineResponseTests {

		@Test
		@DisplayName("should return null when entity is null")
		void shouldReturnNullWhenEntityIsNull() {
			assertThat(mapper.toWineResponse(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			given(emf.getPersistenceUnitUtil()).willReturn(persistenceUtil);
			given(persistenceUtil.isLoaded(testWine, "styles")).willReturn(true);
			given(persistenceUtil.isLoaded(testWine, "grapeComposition")).willReturn(true);

			given(wineStyleMapper.toWineStyleResponse(testStyle1)).willReturn(new WineStyleResponse(1, "Style 1"));
			given(wineStyleMapper.toWineStyleResponse(testStyle2)).willReturn(new WineStyleResponse(2, "Style 2"));

			var result = mapper.toWineResponse(testWine);

			assertThat(result).isNotNull().satisfies(wineDto -> {
				assertThat(wineDto.id()).isEqualTo(testWine.getId());
				assertThat(wineDto.name()).isEqualTo(testWine.getName());
				assertThat(wineDto.winery()).isEqualTo(testWine.getWinery());
				assertThat(wineDto.country()).isEqualTo(testWine.getCountry());
				assertThat(wineDto.region()).isEqualTo(testWine.getRegion());
				assertThat(wineDto.appellation()).isEqualTo(testWine.getAppellation());
				assertThat(wineDto.vintage()).isEqualTo(testWine.getVintage());
				assertThat(wineDto.styles()).isNotNull().hasSize(2);
				assertThat(wineDto.styles().get(0).id()).isEqualTo(testStyle1.getId());
				assertThat(wineDto.styles().get(1).id()).isEqualTo(testStyle2.getId());
				assertThat(wineDto.grapeComposition()).isNotNull().hasSize(2);
				assertThat(wineDto.grapeComposition().get(0).grape().id()).isEqualTo(testGrape1.getId());
				assertThat(wineDto.grapeComposition().get(1).grape().id()).isEqualTo(testGrape2.getId());
			});
		}

		@Test
		@DisplayName("should handle empty styles")
		void shouldHandleEmptyCourses() {
			given(emf.getPersistenceUnitUtil()).willReturn(persistenceUtil);
			given(persistenceUtil.isLoaded(testWine, "styles")).willReturn(true);
			given(persistenceUtil.isLoaded(testWine, "grapeComposition")).willReturn(true);

			testWine.clearStyles();
			var result = mapper.toWineResponse(testWine);

			assertThat(result).isNotNull();
			assertThat(result.styles()).isEmpty();
		}

		@Test
		@DisplayName("should handle uninitialized styles collection")
		void shouldHandleUninitializedStylesCollection() {
			given(emf.getPersistenceUnitUtil()).willReturn(persistenceUtil);
			given(persistenceUtil.isLoaded(testWine, "styles")).willReturn(false);
			given(persistenceUtil.isLoaded(testWine, "grapeComposition")).willReturn(true);

			var result = mapper.toWineResponse(testWine);

			assertThat(result).isNotNull();
			assertThat(result.styles()).isEmpty();
		}

		@Test
		@DisplayName("should handle empty grapes")
		void shouldHandleEmptyGrapes() {
			given(emf.getPersistenceUnitUtil()).willReturn(persistenceUtil);
			given(persistenceUtil.isLoaded(testWine, "styles")).willReturn(true);
			given(persistenceUtil.isLoaded(testWine, "grapeComposition")).willReturn(true);

			testWine.clearGrapeComposition();
			var result = mapper.toWineResponse(testWine);

			assertThat(result).isNotNull();
			assertThat(result.grapeComposition()).isEmpty();
		}

		@Test
		@DisplayName("should handle uninitialized grapes collection")
		void shouldHandleUninitializedGrapesCollection() {
			given(emf.getPersistenceUnitUtil()).willReturn(persistenceUtil);
			given(persistenceUtil.isLoaded(testWine, "styles")).willReturn(true);
			given(persistenceUtil.isLoaded(testWine, "grapeComposition")).willReturn(false);

			var result = mapper.toWineResponse(testWine);

			assertThat(result).isNotNull();
			assertThat(result.grapeComposition()).isEmpty();
		}

	}

	@Nested
	@DisplayName("to GrapeResponse mapping tests")
	class ToGrapeResponseTests {

		@Test
		@DisplayName("should return null when entity is null")
		void shouldReturnNullWhenEntityIsNull() {
			assertThat(mapper.toGrapeResponse(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.toGrapeResponse(testGrape1);

			assertThat(result).isNotNull().satisfies(grapeDto -> {
				assertThat(grapeDto.id()).isEqualTo(testGrape1.getId());
				assertThat(grapeDto.name()).isEqualTo(testGrape1.getName());
			});
		}

	}

	@Nested
	@DisplayName("to WineGrapeCompositionResponse mapping tests")
	class ToWineGrapeCompositionResponseTests {

		@Test
		@DisplayName("should return null when entity is null")
		void shouldReturnNullWhenEntityIsNull() {
			assertThat(mapper.toWineGrapeCompositionResponse(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.toWineGrapeCompositionResponse(testGrapeComposition1);

			assertThat(result).isNotNull().satisfies(compositionDto -> {
				assertThat(compositionDto.grape()).isNotNull().satisfies(grapeDto -> {
					assertThat(grapeDto.id()).isEqualTo(testGrape1.getId());
					assertThat(grapeDto.name()).isEqualTo(testGrape1.getName());
				});
				assertThat(compositionDto.percentage()).isEqualByComparingTo(testGrapeComposition1.getPercentage());
			});
		}

	}

	@Nested
	@DisplayName("from WineUpsertRequest mapping tests")
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

			assertThat(result).isNotNull().satisfies(wine -> {
				assertThat(wine.getId()).isNull();
				assertThat(wine.getName()).isEqualTo(testRequest.name());
				assertThat(wine.getWinery()).isEqualTo(testRequest.winery());
				assertThat(wine.getCountry()).isEqualTo(testRequest.country());
				assertThat(wine.getRegion()).isEqualTo(testRequest.region());
				assertThat(wine.getAppellation()).isEqualTo(testRequest.appellation());
				assertThat(wine.getVintage()).isEqualTo(testRequest.vintage());
			});
		}

		@Test
		@DisplayName("should fetch and map styles from repository")
		void shouldFetchAndMapStylesFromRepository() {
			var styleIds = List.of(testStyle1.getId(), testStyle2.getId());
			var request = new WineUpsertRequest("Upsert Name", null, null, null, null, null, styleIds, null);

			given(wineStyleRepository.findAllById(styleIds)).willReturn(List.of(testStyle1, testStyle2));

			var result = mapper.fromWineUpsertRequest(request);

			assertThat(result).isNotNull().satisfies(wine -> {
				assertThat(wine.getStyles()).hasSize(2);
				assertThat(wine.getStyles()).extracting(WineStyle::getId).containsExactlyInAnyOrder(1, 2);
			});

			verify(wineStyleRepository).findAllById(styleIds);
		}

		@Test
		@DisplayName("should fetch grapes from repository and map grape composition")
		void shouldFetchGrapesFromRepositorAndMapGrapeComposition() {
			var grapeIds = Set.of(testGrapeComposition1.getGrape().getId(), testGrapeComposition2.getGrape().getId());
			var grapeCompositions = List.of(
					new WineUpsertRequest.GrapeComposition(testGrapeComposition1.getGrape().getId(),
							BigDecimal.valueOf(.6)),
					new WineUpsertRequest.GrapeComposition(testGrapeComposition2.getGrape().getId(),
							BigDecimal.valueOf(.4)));
			var request = new WineUpsertRequest("Upsert Name", null, null, null, null, null, null, grapeCompositions);

			given(grapeRepository.findAllById(grapeIds)).willReturn(List.of(testGrape1, testGrape2));

			var result = mapper.fromWineUpsertRequest(request);

			assertThat(result).isNotNull().satisfies(grape -> {
				assertThat(grape.getGrapeComposition()).hasSize(2);
				assertThat(grape.getGrapeComposition()).extracting(WineGrapeComposition::getGrape)
					.containsExactlyInAnyOrder(testGrape1, testGrape2);
			});

			verify(grapeRepository).findAllById(grapeIds);
		}

		@Test
		@DisplayName("should handle null values")
		void shouldHandleNullValues() {
			testRequest = new WineUpsertRequest("Upsert Name", null, null, null, null, null, null, null);

			var result = mapper.fromWineUpsertRequest(testRequest);

			assertThat(result).isNotNull().satisfies(wine -> {
				assertThat(wine.getId()).isNull();
				assertThat(wine.getName()).isEqualTo(testRequest.name());
				assertThat(wine.getWinery()).isNull();
				assertThat(wine.getCountry()).isNull();
				assertThat(wine.getRegion()).isNull();
				assertThat(wine.getAppellation()).isNull();
				assertThat(wine.getVintage()).isNull();
				assertThat(wine.getStyles()).isEmpty();
				assertThat(wine.getGrapeComposition()).isEmpty();
			});
		}

	}

	@Nested
	@DisplayName("merge with WineUpsertRequest mapping tests")
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

			assertThat(result).isNotNull().satisfies(wine -> {
				assertThat(wine.getId()).isEqualTo(testWine.getId());
				assertThat(wine.getName()).isEqualTo(testRequest.name());
				assertThat(wine.getWinery()).isEqualTo(testRequest.winery());
				assertThat(wine.getCountry()).isEqualTo(testRequest.country());
				assertThat(wine.getRegion()).isEqualTo(testRequest.region());
				assertThat(wine.getAppellation()).isEqualTo(testRequest.appellation());
				assertThat(wine.getVintage()).isEqualTo(testRequest.vintage());
			});
		}

		@Test
		@DisplayName("should clear styles and grape composition when request contains empty collections")
		void shouldMapStylesAsEmptyWhenRequestStyleIsNullOrEmpty() {
			var request = new WineUpsertRequest("Upsert Name", null, null, null, null, null, Collections.emptyList(),
					Collections.emptyList());

			assertThat(mapper.mergeWithWineUpsertRequest(testWine, request).getStyles()).isEmpty();
			assertThat(mapper.mergeWithWineUpsertRequest(testWine, request).getStyles()).isEmpty();
		}

		@Test
		@DisplayName("should fetch and map styles from repository")
		void shouldFetchAndMapStylesFromRepository() {
			var style11 = TestWineStyleBuilder.builder().withId(11).withName("Style 11").build();
			var style12 = TestWineStyleBuilder.builder().withId(12).withName("Style 12").build();

			var styleIds = List.of(testStyle1.getId(), testStyle2.getId(), 999);

			var request = new WineUpsertRequest("Upsert Name", null, null, null, null, null, styleIds, null);

			// won't return grape with id 999
			given(wineStyleRepository.findAllById(styleIds)).willReturn(List.of(style11, style12));

			var result = mapper.mergeWithWineUpsertRequest(testWine, request);

			assertThat(result).isNotNull().satisfies(wine -> {
				// should have cleared the existing grape composition
				// grape with id 999 should be ignored because it doesn't exist
				assertThat(wine.getStyles()).hasSize(2);
				assertThat(wine.getStyles()).extracting(WineStyle::getId).containsExactlyInAnyOrder(11, 12);
			});

			verify(wineStyleRepository).findAllById(styleIds);
		}

		@Test
		@DisplayName("should fetch grapes from repository and map grape composition")
		void shouldFetchGrapesFromRepositoryAndMapGrapeComposition() {
			var grape11 = TestGrapeBuilder.builder().withId(11).withName("Grape 11").build();
			var grape12 = TestGrapeBuilder.builder().withId(12).withName("Grape 12").build();

			var grapeCompositions = List.of(
					new WineUpsertRequest.GrapeComposition(grape11.getId(), BigDecimal.valueOf(.6)),
					new WineUpsertRequest.GrapeComposition(grape12.getId(), BigDecimal.valueOf(.4)),
					new WineUpsertRequest.GrapeComposition(999, BigDecimal.valueOf(1)));

			var request = new WineUpsertRequest("Upsert Name", null, null, null, null, null, null, grapeCompositions);

			// won't return grape with id 999
			given(grapeRepository.findAllById(Set.of(11, 12, 999))).willReturn(List.of(grape11, grape12));

			var result = mapper.mergeWithWineUpsertRequest(testWine, request);

			assertThat(result).isNotNull().satisfies(grape -> {
				// should have cleared the existing grape composition
				// grape with id 999 should be ignored because it doesn't exist
				assertThat(grape.getGrapeComposition()).hasSize(2);
				assertThat(grape.getGrapeComposition()).extracting(WineGrapeComposition::getGrape)
					.containsExactlyInAnyOrder(grape11, grape12);
			});
		}

		@Test
		@DisplayName("should handle null values")
		void shouldHandleNullValues() {
			testRequest = new WineUpsertRequest("Upsert Name", null, null, null, null, null, null, null);

			var result = mapper.mergeWithWineUpsertRequest(testWine, testRequest);

			assertThat(result).isNotNull().satisfies(wine -> {
				assertThat(wine.getId()).isEqualTo(testWine.getId());
				assertThat(wine.getName()).isEqualTo("Upsert Name");
				assertThat(wine.getWinery()).isNull();
				assertThat(wine.getCountry()).isNull();
				assertThat(wine.getRegion()).isNull();
				assertThat(wine.getAppellation()).isNull();
				assertThat(wine.getVintage()).isNull();
				assertThat(wine.getStyles()).isEqualTo(wine.getStyles());
				assertThat(wine.getGrapeComposition()).isEqualTo(wine.getGrapeComposition());
			});
		}

	}

}