package emh.dd_site.wine.service;

import emh.dd_site.wine.dto.WineMapper;
import emh.dd_site.wine.dto.WineResponse;
import emh.dd_site.wine.dto.WineUpsertRequest;
import emh.dd_site.wine.entity.TestWineBuilder;
import emh.dd_site.wine.entity.Wine;
import emh.dd_site.wine.exception.WineNotFoundException;
import emh.dd_site.wine.repository.WineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WineService Unit Tests")
class WineServiceTest {

	@Mock
	private WineRepository wineRepository;

	@Mock
	private WineMapper wineMapper;

	@InjectMocks
	private WineService wineService;

	private Pageable pageable;

	private WineResponse response1, response2;

	@BeforeEach
	void setUp() {
		pageable = PageRequest.of(0, 10, Sort.by("wineNo").ascending());
		response1 = new WineResponse(1L, "Wine 1", "Winery 1", "Country 1", "Region 1", "Appellation 1", Year.of(2023),
				"https://vivino.com", Collections.emptyList(), Collections.emptyList());
		response2 = new WineResponse(2L, "Wine 2", "Winery 2", "Country 2", "Region 2", "Appellation 2", Year.of(2023),
				"https://vivino.com", Collections.emptyList(), Collections.emptyList());
	}

	@Nested
	@DisplayName("List wines tests")
	class ListAllWinesTests {

		@Test
		@DisplayName("should return paged list of mapped DTOs")
		void shouldReturnMappedDtoList_whenWinesAvailable() {
			var wine1 = TestWineBuilder.builder().withId(1L).withName("Wine 1").build();
			var wine2 = TestWineBuilder.builder().withId(2L).withName("Wine 2").build();
			var idPage = new PageImpl<>(List.of(1L, 2L), pageable, 2);

			given(wineRepository.findAllIds(pageable)).willReturn(idPage);
			given(wineRepository.findAllWithStylesAndGrapesByIdIn(idPage.getContent()))
				.willReturn(List.of(wine1, wine2));
			given(wineMapper.toWineResponse(wine1)).willReturn(response1);
			given(wineMapper.toWineResponse(wine2)).willReturn(response2);

			Page<WineResponse> result = wineService.listAll(pageable);

			assertThat(result.getTotalElements()).isEqualTo(2);
			assertThat(result.getContent()).containsExactly(response1, response2);
		}

		@Test
		@DisplayName("should return empty page when no wines available")
		void shouldReturnEmptyPage_whenNoWinesAvailable() {
			Page<Long> idPage = new PageImpl<>(List.of(), pageable, 0);

			given(wineRepository.findAllIds(pageable)).willReturn(idPage);

			Page<WineResponse> result = wineService.listAll(pageable);

			assertThat(result.getTotalElements()).isEqualTo(0);
			assertThat(result.getContent()).isEmpty();
			assertThat(result.getNumber()).isEqualTo(0);
			assertThat(result.getSize()).isEqualTo(10);

			verifyNoMoreInteractions(wineRepository);
			verifyNoInteractions(wineMapper);
		}

	}

	@Nested
	@DisplayName("List by event tests")
	class ListByEventTests {

		@Test
		@DisplayName("should return paged list of mapped DTOs")
		void shouldReturnMappedPage_whenWinesAvailableForEvent() {
			long eventId = 42L;
			Wine wine = TestWineBuilder.builder().withId(1L).withName("Wine 1").build();
			Page<Long> idPage = new PageImpl<>(List.of(1L), pageable, 1);
			Page<Wine> winePage = new PageImpl<>(List.of(wine), pageable, 1);

			given(wineRepository.findIdsByCourseEventId(eventId, pageable)).willReturn(idPage);
			given(wineRepository.findAllWithStylesAndGrapesByIdIn(idPage.getContent())).willReturn(List.of(wine));
			given(wineMapper.toWineResponse(wine)).willReturn(response1);

			Page<WineResponse> result = wineService.listByEvent(eventId, pageable);

			assertThat(result.getTotalElements()).isEqualTo(1);
			assertThat(result.getContent()).containsExactly(response1);
		}

		@Test
		@DisplayName("should return empty page when no wines available for event")
		void shouldReturnEmptyPage_whenNoWinesAvailableForEvent() {
			long eventId = 42L;
			Page<Long> idPage = new PageImpl<>(List.of(), pageable, 0);

			given(wineRepository.findIdsByCourseEventId(eventId, pageable)).willReturn(idPage);

			var result = wineService.listByEvent(eventId, pageable);

			assertThat(result.getContent()).isEmpty();
			assertThat(result.getTotalElements()).isEqualTo(0);
			assertThat(result.getNumber()).isEqualTo(0);
			assertThat(result.getSize()).isEqualTo(10);

			verifyNoInteractions(wineMapper);
			verifyNoMoreInteractions(wineRepository);
		}

	}

	@Nested
	@DisplayName("Find by id tests")
	class FindByIdTests {

		@Test
		@DisplayName("should return mapped DTO when wine exists")
		void shouldReturnMappedDto_whenWineExists() {
			var id = 123L;
			Wine wine = mock(Wine.class);

			given(wineRepository.findById(id)).willReturn(Optional.of(wine));
			given(wineMapper.toWineResponse(wine)).willReturn(response1);

			WineResponse result = wineService.findById(id);

			assertThat(result).isEqualTo(response1);
		}

		@Test
		@DisplayName("should throw WineNotFoundException when wine does not exist")
		void shouldThrowNotFoundException_whenWineDoesNotExist() {
			var id = 999L;

			given(wineRepository.findById(id)).willReturn(Optional.empty());

			assertThatThrownBy(() -> wineService.findById(id)).isInstanceOf(WineNotFoundException.class)
				.hasMessageContaining(String.valueOf(id));

			verifyNoInteractions(wineMapper);
		}

	}

	@Nested
	@DisplayName("Create wine tests")
	class CreateWineTests {

		@Test
		@DisplayName("should create and return mapped DTO")
		void shouldCreateAndReturnMappedDto() {
			var wine = new Wine("Wine Name");
			wine.setWinery("Winery");
			wine.setCountry("Country");
			wine.setRegion("Region");
			wine.setAppellation("Appellation");
			wine.setVintage(Year.of(2023));

			var request = new WineUpsertRequest("Wine Name", "Winery Name", "Country", "Region", "Appellation",
					Year.of(2023), "https://vivino.com", Collections.emptyList(), Collections.emptyList());
			var response = new WineResponse(1L, "Wine Name", "Winery Name", "Country", "Region", "Appellation",
					Year.of(2023), "https://vivino.com", Collections.emptyList(), Collections.emptyList());

			given(wineMapper.fromWineUpsertRequest(request)).willReturn(wine);
			given(wineRepository.save(wine)).willReturn(wine);
			given(wineMapper.toWineResponse(wine)).willReturn(response);

			WineResponse result = wineService.create(request);

			assertThat(result).isEqualTo(response);
		}

		@Test
		@DisplayName("should throw NullPointException when upsertRequest is null")
		void shouldThrowNullPointeException_whenUpsertRequestIsNull() {
			assertThatThrownBy(() -> wineService.create(null)).isInstanceOf(NullPointerException.class);
		}

	}

	@Nested
	@DisplayName("Update wine tests")
	class UpdateWineTests {

		@Test
		@DisplayName("should update and return mapped DTO when wine exists")
		void shouldUpdateAndReturnMappedDto_whenWineExists() {
			long id = 12345L;
			var wine = new Wine("Wine Name");
			wine.setWinery("Winery");
			wine.setCountry("Country");
			wine.setRegion("Region");
			wine.setAppellation("Appellation");
			wine.setVintage(Year.of(2023));

			var request = new WineUpsertRequest("Updated Name", "Updated Winery", "Updated Country", "Updated Region",
					"Updated Appellation", Year.of(2024), "https://vivino.com", Collections.emptyList(),
					Collections.emptyList());
			var response = new WineResponse(1L, "Updated Name", "Updated Winery", "Updated Country", "Updated Region",
					"Updated Appellation", Year.of(2024), "https://vivino.com", Collections.emptyList(),
					Collections.emptyList());

			given(wineRepository.findById(id)).willReturn(Optional.of(wine));
			given(wineMapper.mergeWithWineUpsertRequest(wine, request)).willReturn(wine);
			given(wineRepository.save(wine)).willReturn(wine);
			given(wineMapper.toWineResponse(wine)).willReturn(response);

			WineResponse result = wineService.update(id, request);

			assertThat(result).isEqualTo(response);
		}

		@Test
		@DisplayName("should throw NullPointException when upsertRequest is null")
		void shouldThrowNullPointeException_whenUpsertRequestIsNull() {
			assertThatThrownBy(() -> wineService.update(1, null)).isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("should throw WineNotFoundException when wine does not exist")
		void shouldThrowNotFoundException_whenWineDoesNotExist() {
			var id = 404L;

			var request = new WineUpsertRequest("Updated Name", "Updated Winery", "Updated Country", "Updated Region",
					"Updated Appellation", Year.of(2024), "https://vivino.com", Collections.emptyList(),
					Collections.emptyList());

			given(wineRepository.findById(id)).willReturn(Optional.empty());

			assertThatThrownBy(() -> wineService.update(id, request)).isInstanceOf(WineNotFoundException.class)
				.hasMessageContaining(String.valueOf(id));
			verify(wineRepository, never()).save(any());
			verifyNoInteractions(wineMapper);
		}

	}

	@Nested
	@DisplayName("Delete wine tests")
	class DeleteWineTests {

		@Test
		public void shouldDeleteWine() {
			long id = 11L;

			wineService.delete(id);

			verify(wineRepository).deleteById(id);
			verifyNoInteractions(wineMapper);
		}

	}

}