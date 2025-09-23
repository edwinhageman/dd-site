package emh.dd_site.event.service;

import emh.dd_site.event.WineType;
import emh.dd_site.event.dto.WineMapper;
import emh.dd_site.event.dto.WineResponse;
import emh.dd_site.event.dto.WineUpsertRequest;
import emh.dd_site.event.entity.Wine;
import emh.dd_site.event.exception.WineNotFoundException;
import emh.dd_site.event.repository.WineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;

import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
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
		response1 = new WineResponse(1L, "Wine 1", WineType.RED, "Grape 1", "Country 1", "Region 1", Year.of(2023),
				Collections.emptyList());
		response2 = new WineResponse(2L, "Wine 2", WineType.RED, "Grape 2", "Country 2", "Region 2", Year.of(2023),
				Collections.emptyList());
	}

	@Nested
	@DisplayName("List wines tests")
	class ListAllWinesTests {

		@Test
		@DisplayName("should return paged list of mapped DTOs")
		void shouldReturnMappedDtoList_whenWinesAvailable() {
			Wine wine1 = mock(Wine.class);
			Wine wine2 = mock(Wine.class);
			Page<Wine> winePage = new PageImpl<>(List.of(wine1, wine2), pageable, 2);

			given(wineRepository.findAll(pageable)).willReturn(winePage);
			given(wineMapper.toWineResponse(wine1)).willReturn(response1);
			given(wineMapper.toWineResponse(wine2)).willReturn(response2);

			Page<WineResponse> result = wineService.listAll(pageable);

			assertThat(result.getTotalElements()).isEqualTo(2);
			assertThat(result.getContent()).containsExactly(response1, response2);
		}

		@Test
		@DisplayName("should return empty page when no wines available")
		void shouldReturnEmptyPage_whenNoWinesAvailable() {
			Page<Wine> winePage = new PageImpl<>(List.of(), pageable, 0);

			given(wineRepository.findAll(pageable)).willReturn(winePage);

			Page<WineResponse> result = wineService.listAll(pageable);

			assertThat(result.getTotalElements()).isEqualTo(0);
			assertThat(result.getContent()).isEmpty();
			assertThat(result.getNumber()).isEqualTo(0);
			assertThat(result.getSize()).isEqualTo(10);
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
			Wine wine = mock(Wine.class);
			Page<Wine> winePage = new PageImpl<>(List.of(wine), pageable, 1);

			given(wineRepository.findByEventId(eventId, pageable)).willReturn(winePage);
			given(wineMapper.toWineResponse(wine)).willReturn(response1);

			Page<WineResponse> result = wineService.listByEvent(eventId, pageable);

			assertThat(result.getTotalElements()).isEqualTo(1);
			assertThat(result.getContent()).containsExactly(response1);
		}

		@Test
		@DisplayName("should return empty page when no wines available for event")
		void shouldReturnEmptyPage_whenNoWinesAvailableForEvent() {
			long eventId = 42L;
			Page<Wine> winePage = new PageImpl<>(List.of(), pageable, 0);

			given(wineRepository.findByEventId(eventId, pageable)).willReturn(winePage);

			var result = wineService.listByEvent(eventId, pageable);

			assertThat(result.getContent()).isEmpty();
			assertThat(result.getTotalElements()).isEqualTo(0);
			assertThat(result.getNumber()).isEqualTo(0);
			assertThat(result.getSize()).isEqualTo(10);

			verifyNoInteractions(wineMapper);
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
			var wine = new Wine("Wine Name", WineType.RED, "Grape", "Country");
			wine.setRegion("Region");
			wine.setYear(Year.of(2023));

			var request = new WineUpsertRequest("Wine Name", WineType.RED, "Grape", "Country", "Region", Year.of(2023));
			var response = new WineResponse(1L, "Wine Name", WineType.RED, "Grape", "Country", "Region", Year.of(2023),
					Collections.emptyList());

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
			var wine = new Wine("Wine Name", WineType.RED, "Grape", "Country");
			wine.setRegion("Region");
			wine.setYear(Year.of(2023));

			var request = new WineUpsertRequest("Updated Name", WineType.WHITE, "Updated Grape", "Updated Country",
					"Updated Region", Year.of(2024));
			var response = new WineResponse(1L, "Updated Name", WineType.WHITE, "Updated Grape", "Updated Country",
					"Updated Region", Year.of(2024), Collections.emptyList());

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
			assertThatThrownBy(() -> wineService.create(null)).isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("should throw WineNotFoundException when wine does not exist")
		void shouldThrowNotFoundException_whenWineDoesNotExist() {
			var id = 404L;

			var request = new WineUpsertRequest("Updated Name", WineType.WHITE, "Updated Grape", "Updated Country",
					"Updated Region", Year.of(2024));

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