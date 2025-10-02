package emh.dd_site.wine.controller;

import emh.dd_site.wine.WineType;
import emh.dd_site.wine.dto.WineResponse;
import emh.dd_site.wine.dto.WineUpsertRequest;
import emh.dd_site.wine.service.WineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.time.Year;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("WineController Unit Tests")
class WineControllerTest {

	@Mock
	private WineService wineService;

	@InjectMocks
	private WineController wineController;

	private WineResponse testResponse;

	private WineUpsertRequest testRequest;

	@BeforeEach
	void setUp() {
		testResponse = new WineResponse(1L, "Wine Name", WineType.RED, "Grape Name", "Country", "Region", Year.of(2025),
				Collections.emptyList());
		testRequest = new WineUpsertRequest("Wine Name", WineType.RED, "Grape Name", "Country", "Region",
				Year.of(2025));
	}

	@Nested
	@DisplayName("GET /api/wines")
	class ListWines {

		@Test
		@DisplayName("should return paged list of wines sorted by name")
		void shouldReturnPagedListWithNameSort() {
			Pageable pageRequest = PageRequest.of(0, 10);
			Pageable alteredPageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
			Page<WineResponse> page = new PageImpl<>(Collections.singletonList(testResponse), alteredPageRequest, 1);

			given(wineService.listAll(alteredPageRequest)).willReturn(page);

			var response = wineController.listWines(pageRequest);

			assertThat(response.getContent()).containsExactly(testResponse);
			assertThat(response.getMetadata()).isNotNull();
			assertThat(response.getMetadata().number()).isEqualTo(0);
			assertThat(response.getMetadata().size()).isEqualTo(10);
		}

	}

	@Nested
	@DisplayName("GET /api/events/{eventId}/wines")
	class ListWinesByEventTests {

		@Test
		@DisplayName("should return paged list of wines sorted by event date")
		void shouldReturnPagedListWithEventDateSort() {
			long eventId = 2L;
			Pageable pageRequest = PageRequest.of(0, 10);
			Pageable alteredPageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "event.date"));
			Page<WineResponse> page = new PageImpl<>(Collections.singletonList(testResponse), alteredPageRequest, 1);

			given(wineService.listByEvent(eventId, alteredPageRequest)).willReturn(page);

			var response = wineController.listWinesByEvent(eventId, pageRequest);

			assertThat(response.getContent()).containsExactly(testResponse);
			assertThat(response.getMetadata()).isNotNull();
			assertThat(response.getMetadata().number()).isEqualTo(0);
			assertThat(response.getMetadata().size()).isEqualTo(10);
		}

	}

	@Nested
	@DisplayName("GET /api/wines/{id}")
	class GetOneWineTests {

		@Test
		@DisplayName("should return single wine")
		void shouldReturnSingleWine() {
			given(wineService.findById(1L)).willReturn(testResponse);

			var response = wineController.getWineById(1L);

			assertThat(response).isEqualTo(testResponse);
			verify(wineService).findById(1L);
		}

	}

	@Nested
	@DisplayName("POST /api/wines")
	class CreateWineTests {

		@Test
		@DisplayName("should create and return wine")
		void shouldCreateAndReturnWine() {
			given(wineService.create(testRequest)).willReturn(testResponse);

			var response = wineController.createWine(testRequest);

			assertThat(response).isEqualTo(testResponse);
			verify(wineService).create(testRequest);
		}

	}

	@Nested
	@DisplayName("PUT /api/wines/{id}")
	class UpdateWineTests {

		@Test
		@DisplayName("should update and return wine")
		void shouldUpdateAndReturnWine() {
			long id = 3L;

			given(wineService.update(id, testRequest)).willReturn(testResponse);

			var response = wineController.updateWine(id, testRequest);

			assertThat(response).isEqualTo(testResponse);
			verify(wineService).update(id, testRequest);
		}

	}

	@Nested
	@DisplayName("DELETE /api/wines/{id}")
	class DeleteWineTests {

		@Test
		@DisplayName("should delete and return no content")
		void shouldDeleteAndReturnNoContent() {
			var response = wineController.deleteWine(1L);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
			verify(wineService).delete(1L);
		}

	}

}