package emh.dd_site.wine.controller;

import emh.dd_site.wine.dto.WineStyleResponse;
import emh.dd_site.wine.dto.WineStyleUpsertRequest;
import emh.dd_site.wine.service.WineStyleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("WineStyleController Unit Tests")
public class WineStyleControllerTests {

	@Mock
	private WineStyleService wineStyleService;

	@InjectMocks
	private WineStyleController wineStyleController;

	private WineStyleResponse testResponse;

	private WineStyleUpsertRequest testRequest;

	@BeforeEach
	void setUp() {
		testResponse = new WineStyleResponse(1, "Style Name");
		testRequest = new WineStyleUpsertRequest("Upsert Style Name");
	}

	@Nested
	@DisplayName("GET /api/wines/styles")
	class ListWines {

		@Test
		@DisplayName("should call service.listAll and return paged dto list sorted by name")
		void shouldCallServiceListAllAndReturnPagedDtoListSortedByName() {
			var pageRequest = PageRequest.of(0, 10);
			var alteredPageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
			var page = new PageImpl<>(Collections.singletonList(testResponse), alteredPageRequest, 1);

			given(wineStyleService.listAll(alteredPageRequest)).willReturn(page);

			var response = wineStyleController.listWineStyles(pageRequest);

			assertThat(response.getContent()).containsExactly(testResponse);
			assertThat(response.getMetadata()).isNotNull();
			assertThat(response.getMetadata().number()).isEqualTo(0);
			assertThat(response.getMetadata().size()).isEqualTo(10);
		}

	}

	@Nested
	@DisplayName("GET /api/wines/styles/{id}")
	class GetOneWineTests {

		@Test
		@DisplayName("should call service.findById and return dto")
		void shouldCallServiceFindByIdReturnDto() {
			given(wineStyleService.findById(1)).willReturn(testResponse);

			var response = wineStyleController.getWineStyleById(1);

			assertThat(response).isEqualTo(testResponse);
			verify(wineStyleService).findById(1);
		}

	}

	@Nested
	@DisplayName("POST /api/wines/styles")
	class CreateWineTests {

		@Test
		@DisplayName("should call service.create and return dto")
		void shouldCallServiceCreateAndReturnDto() {
			given(wineStyleService.create(testRequest)).willReturn(testResponse);

			var response = wineStyleController.createWineStyle(testRequest);

			assertThat(response).isEqualTo(testResponse);
			verify(wineStyleService).create(testRequest);
		}

	}

	@Nested
	@DisplayName("PUT /api/wines/styles/{id}")
	class UpdateTests {

		@Test
		@DisplayName("should call service.update and return dto")
		void shouldCallServiceAndMapperAndReturnDto() {
			var id = 3;

			given(wineStyleService.update(id, testRequest)).willReturn(testResponse);

			var response = wineStyleController.updateWineStyle(id, testRequest);

			assertThat(response).isEqualTo(testResponse);
			verify(wineStyleService).update(id, testRequest);
		}

	}

	@Nested
	@DisplayName("DELETE /api/wines/styles/{id}")
	class DeleteTests {

		@Test
		@DisplayName("should call service.delete and return no content")
		void shouldCallServiceDeleteAndReturnNoContent() {
			var response = wineStyleController.deleteWineStyle(1);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
			verify(wineStyleService).delete(1);
		}

	}

}
