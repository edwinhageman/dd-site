package emh.dd_site.wine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import emh.dd_site.wine.dto.GrapeResponse;
import emh.dd_site.wine.dto.GrapeUpsertRequest;
import emh.dd_site.wine.exception.GrapeNotFoundException;
import emh.dd_site.wine.service.GrapeService;
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

@WebMvcTest(GrapeController.class)
@DisplayName("GrapeController Integration Tests")
public class GrapeControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private GrapeService grapeService;

	private final GrapeResponse testResponse = new GrapeResponse(1, "Test Grape");

	private final GrapeResponse testResponse2 = new GrapeResponse(2, "Test Grape 2");

	private final GrapeResponse upsertResponse = new GrapeResponse(1, "Upsert Grape");

	private final GrapeUpsertRequest testRequest = new GrapeUpsertRequest("Upsert Grape");

	@Nested
	@DisplayName("GET /api/wines/grapes")
	class ListAll {

		@Test
		@DisplayName("should sort by name asc")
		void shouldSortByNameAsc() throws Exception {
			var page = new PageImpl<>(List.of(testResponse, testResponse2), PageRequest.of(1, 10), 2);

			given(grapeService.listAll(any(Pageable.class))).willReturn(page);

			mockMvc.perform(get("/api/wines/grapes").param("page", "1").param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0].id").value(1))
				.andExpect(jsonPath("$.content[0].name").value("Test Grape"))
				.andExpect(jsonPath("$.content[1].id").value(2))
				.andExpect(jsonPath("$.content[1].name").value("Test Grape 2"));

			var captor = ArgumentCaptor.forClass(Pageable.class);
			verify(grapeService).listAll(captor.capture());
			Pageable pageReq = captor.getValue();
			assertThat(pageReq.getPageNumber()).isEqualTo(1);
			assertThat(pageReq.getPageSize()).isEqualTo(10);
			assertThat(pageReq.getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "name"));
		}

	}

	@Nested
	@DisplayName("GET /api/wines/grapes/{id}")
	class GetOne {

		@Test
		@DisplayName("should return a grape by id")
		void shouldReturnGrape() throws Exception {
			given(grapeService.findById(1)).willReturn(testResponse);

			mockMvc.perform(get("/api/wines/grapes/{id}", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Test Grape"));
		}

		@Test
		@DisplayName("should return 404 when grape not found")
		void shouldReturn4040WhenGrapeNotFound() throws Exception {
			given(grapeService.findById(404)).willThrow(new GrapeNotFoundException(404));

			mockMvc.perform(get("/api/wines/grapes/{id}", 404L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Could not find grape 404"));
		}

	}

	@Nested
	@DisplayName("POST /api/wines/grapes")
	class Create {

		@Test
		@DisplayName("should create with valid body")
		void shouldCreate() throws Exception {
			given(grapeService.create(testRequest)).willReturn(upsertResponse);

			mockMvc
				.perform(post("/api/wines/grapes").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(testRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Upsert Grape"));
		}

		@Test
		@DisplayName("should return 400 when receiving invalid request body")
		void shouldReturn400WhenRequestBodyInvalid() throws Exception {
			String invalidJson = """
					{"name":""}
					""";
			mockMvc.perform(post("/api/wines/grapes").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
				.andExpect(status().isBadRequest());

			verifyNoInteractions(grapeService);
		}

	}

	@Nested
	@DisplayName("PUT /api/wines/grapes/{id}")
	class Update {

		@Test
		@DisplayName("should update with valid body")
		void shouldUpdate() throws Exception {
			given(grapeService.update(1, testRequest)).willReturn(upsertResponse);

			mockMvc
				.perform(put("/api/wines/grapes/{id}", 1).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(testRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Upsert Grape"));
		}

		@Test
		@DisplayName("should return 400 when receiving invalid request body")
		void shouldReturn400WhenRequestBodyInvalid() throws Exception {
			String invalidJson = """
					{"name":""}
					""";
			mockMvc.perform(post("/api/wines/grapes").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
				.andExpect(status().isBadRequest());

			verifyNoInteractions(grapeService);
		}

		@Test
		@DisplayName("should return 404 when grape not found")
		void shouldReturn404WhenGrapeNotFound() throws Exception {
			given(grapeService.update(404, testRequest)).willThrow(new GrapeNotFoundException(404));

			mockMvc
				.perform(put("/api/wines/grapes/{id}", 404).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(testRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Could not find grape 404"));
		}

	}

	@Nested
	@DisplayName("DELETE /api/wines/grapes/{id}")
	class Delete {

		@Test
		@DisplayName("should delete and return no content")
		void shouldDelete() throws Exception {
			doNothing().when(grapeService).delete(1);

			mockMvc.perform(delete("/api/wines/grapes/{id}", 1)).andExpect(status().isNoContent());

			verify(grapeService).delete(1);
		}

	}

}
