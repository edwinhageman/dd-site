package emh.dd_site.wine.controller;

import emh.dd_site.wine.dto.GrapeResponse;
import emh.dd_site.wine.dto.GrapeUpsertRequest;
import emh.dd_site.wine.service.GrapeService;
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
@DisplayName("GrapeController Unit Tests")
public class GrapeControllerTests {

	@Mock
	private GrapeService grapeService;

	@InjectMocks
	private GrapeController grapeController;

	private GrapeResponse testResponse;

	private GrapeUpsertRequest testRequest;

	@BeforeEach
	void setUp() {
		testResponse = new GrapeResponse(1, "Grape Name");
		testRequest = new GrapeUpsertRequest("Upsert Grape Name");
	}

	@Nested
	@DisplayName("GET /api/wines/grapes")
	class ListTests {

		@Test
		@DisplayName("should call service.listAll and return paged dto list sorted by name")
		void shouldCallServiceListAllAndReturnPagedDtoListSortedByName() {
			var pageRequest = PageRequest.of(0, 10);
			var alteredPageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
			var page = new PageImpl<>(Collections.singletonList(testResponse), alteredPageRequest, 1);

			given(grapeService.listAll(alteredPageRequest)).willReturn(page);

			var response = grapeController.listGrapes(pageRequest);

			assertThat(response.getContent()).containsExactly(testResponse);
			assertThat(response.getMetadata()).isNotNull();
			assertThat(response.getMetadata().number()).isEqualTo(0);
			assertThat(response.getMetadata().size()).isEqualTo(10);
		}

	}

	@Nested
	@DisplayName("GET /api/wines/grapes/{id}")
	class GetOneTests {

		@Test
		@DisplayName("should call service.findById and return dto")
		void shouldCallServiceFindByIdReturnDto() {
			given(grapeService.findById(1)).willReturn(testResponse);

			var response = grapeController.getGrapeById(1);

			assertThat(response).isEqualTo(testResponse);
			verify(grapeService).findById(1);
		}

	}

	@Nested
	@DisplayName("POST /api/wines/grapes")
	class CreateTests {

		@Test
		@DisplayName("should call service.create and return dto")
		void shouldCallServiceCreateAndReturnDto() {
			given(grapeService.create(testRequest)).willReturn(testResponse);

			var response = grapeController.createGrape(testRequest);

			assertThat(response).isEqualTo(testResponse);
			verify(grapeService).create(testRequest);
		}

	}

	@Nested
	@DisplayName("PUT /api/wines/grapes/{id}")
	class UpdateTests {

		@Test
		@DisplayName("should call service.update and return dto")
		void shouldCallServiceAndMapperAndReturnDto() {
			var id = 3;

			given(grapeService.update(id, testRequest)).willReturn(testResponse);

			var response = grapeController.updateGrape(id, testRequest);

			assertThat(response).isEqualTo(testResponse);
			verify(grapeService).update(id, testRequest);
		}

	}

	@Nested
	@DisplayName("DELETE /api/wines/grapes/{id}")
	class DeleteTests {

		@Test
		@DisplayName("should call service.delete and return no content")
		void shouldCallServiceDeleteAndReturnNoContent() {
			var response = grapeController.deleteGrape(1);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
			verify(grapeService).delete(1);
		}

	}

}
