package emh.dd_site.wine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import emh.dd_site.wine.dto.WineStyleResponse;
import emh.dd_site.wine.dto.WineStyleUpsertRequest;
import emh.dd_site.wine.exception.WineStyleNotFoundException;
import emh.dd_site.wine.service.WineStyleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WineStyleController.class)
@DisplayName("WineStyleController Integration Tests")
public class WineStyleControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private WineStyleService wineStyleService;

	private final WineStyleResponse testResponse = new WineStyleResponse(1, "Test Style");

	private final WineStyleResponse testResponse2 = new WineStyleResponse(2, "Test Style 2");

	private final WineStyleResponse upsertResponse = new WineStyleResponse(1, "Upsert Style");

	private final WineStyleUpsertRequest testRequest = new WineStyleUpsertRequest("Upsert Style");

	@Nested
	@DisplayName("GET /api/wines/styles")
	class ListAll {

		@Test
		@DisplayName("should sort by name asc")
		void shouldSortByNameAsc() throws Exception {
			var page = new PageImpl<>(List.of(testResponse, testResponse2), PageRequest.of(1, 10), 2);

			given(wineStyleService.listAll(any(Pageable.class))).willReturn(page);

			mockMvc.perform(get("/api/wines/styles").param("page", "1").param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0].id").value(1))
				.andExpect(jsonPath("$.content[0].name").value("Test Style"))
				.andExpect(jsonPath("$.content[1].id").value(2))
				.andExpect(jsonPath("$.content[1].name").value("Test Style 2"));

			var captor = ArgumentCaptor.forClass(Pageable.class);
			verify(wineStyleService).listAll(captor.capture());
			Pageable pageReq = captor.getValue();
			assertThat(pageReq.getPageNumber()).isEqualTo(1);
			assertThat(pageReq.getPageSize()).isEqualTo(10);
			assertThat(pageReq.getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "name"));
		}

	}

	@Nested
	@DisplayName("GET /api/wines/styles/{id}")
	class GetOne {

		@Test
		@DisplayName("should return a style by id")
		void shouldReturnStyle() throws Exception {
			given(wineStyleService.findById(1)).willReturn(testResponse);

			mockMvc.perform(get("/api/wines/styles/{id}", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Test Style"));
		}

		@Test
		@DisplayName("should return 404 when style not found")
		void shouldReturn4040WhenStyleNotFound() throws Exception {
			given(wineStyleService.findById(404)).willThrow(new WineStyleNotFoundException(404));

			mockMvc.perform(get("/api/wines/styles/{id}", 404L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Could not find wine style 404"));
		}

	}

	@Nested
	@DisplayName("POST /api/wines/styles")
	class Create {

		@Test
		@DisplayName("should create with valid body")
		void shouldCreate() throws Exception {
			given(wineStyleService.create(testRequest)).willReturn(upsertResponse);

			mockMvc
				.perform(post("/api/wines/styles").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(testRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Upsert Style"));
		}

		@Test
		@DisplayName("should return 400 when receiving invalid request body")
		void shouldReturn400WhenRequestBodyInvalid() throws Exception {
			String invalidJson = """
					{"name":""}
					""";
			mockMvc.perform(post("/api/wines/styles").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
				.andExpect(status().isBadRequest());

			verifyNoInteractions(wineStyleService);
		}

	}

	@Nested
	@DisplayName("PUT /api/wines/styles/{id}")
	class Update {

		@Test
		@DisplayName("should update with valid body")
		void shouldUpdate() throws Exception {
			given(wineStyleService.update(1, testRequest)).willReturn(upsertResponse);

			mockMvc
				.perform(put("/api/wines/styles/{id}", 1).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(testRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Upsert Style"));
		}

		@Test
		@DisplayName("should return 400 when receiving invalid request body")
		void shouldReturn400WhenRequestBodyInvalid() throws Exception {
			String invalidJson = """
					{"name":""}
					""";
			mockMvc.perform(post("/api/wines/styles").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
				.andExpect(status().isBadRequest());

			verifyNoInteractions(wineStyleService);
		}

		@Test
		@DisplayName("should return 404 when style not found")
		void shouldReturn404WhenStyleNotFound() throws Exception {
			given(wineStyleService.update(404, testRequest)).willThrow(new WineStyleNotFoundException(404));

			mockMvc
				.perform(put("/api/wines/styles/{id}", 404).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(testRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Could not find wine style 404"));
		}

	}

	@Nested
	@DisplayName("DELETE /api/wines/styles/{id}")
	class Delete {

		@Test
		@DisplayName("should delete and return no content")
		void shouldDelete() throws Exception {
			doNothing().when(wineStyleService).delete(1);

			mockMvc.perform(delete("/api/wines/styles/{id}", 1)).andExpect(status().isNoContent());

			verify(wineStyleService).delete(1);
		}

	}

}
