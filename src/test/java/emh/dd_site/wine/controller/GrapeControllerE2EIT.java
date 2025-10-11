package emh.dd_site.wine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import emh.dd_site.TestcontainersConfig;
import emh.dd_site.wine.dto.GrapeUpsertRequest;
import emh.dd_site.wine.entity.Grape;
import emh.dd_site.wine.repository.GrapeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfig.class)
@DisplayName("GrapeController E2E Tests")
public class GrapeControllerE2EIT {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	GrapeRepository grapeRepository;

	@BeforeEach
	void cleanDb() {
		grapeRepository.deleteAll();
	}

	private Grape persistGrape(String name) {
		var grape = new Grape(name);
		return grapeRepository.save(grape);
	}

	@Nested
	@DisplayName("GET /api/wines/grapes")
	class ListAll {

		@Test
		@DisplayName("when grapes available then returns page sorted by name")
		void whenGrapesAvailable_thenReturnsPageSortedByName() throws Exception {
			var g1 = persistGrape("Grape X");
			var g2 = persistGrape("Grape A");

			mockMvc.perform(get("/api/wines/grapes").param("page", "0").param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0].id").value(g2.getId()))
				.andExpect(jsonPath("$.content[0].name").value("Grape A"))
				.andExpect(jsonPath("$.content[1].id").value(g1.getId()))
				.andExpect(jsonPath("$.content[1].name").value("Grape X"));
		}

		@Test
		@DisplayName("when no grapes available then returns empty page")
		void whenNoGrapesAvailable_thenReturnsEmptyPage() throws Exception {
			mockMvc.perform(get("/api/wines/grapes").param("page", "0").param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(0)));
		}

	}

	@Nested
	@DisplayName("GET /api/wines/grapes/{id}")
	class GetOne {

		@Test
		@DisplayName("when grape available then returns grape")
		void whenGrapeAvailable_theReturnsGrape() throws Exception {
			var g = persistGrape("Grape1");

			mockMvc.perform(get("/api/wines/grapes/{id}", g.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(g.getId()))
				.andExpect(jsonPath("$.name").value("Grape1"));
		}

		@Test
		@DisplayName("when grape not available returns 404 json problem response")
		void whenGrapeNotAvailable_thenReturns404() throws Exception {
			mockMvc.perform(get("/api/wines/grapes/{id}", 404))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.instance").value("/api/wines/grapes/404"));
		}

	}

	@Nested
	@DisplayName("POST /api/wines/grapes")
	class Create {

		@Test
		@DisplayName("creates with valid body")
		void whenRequestBodyValid_thenCreatesGrape() throws Exception {
			var req = new GrapeUpsertRequest("Created Grape");

			mockMvc
				.perform(post("/api/wines/grapes").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("Created Grape"));
		}

		@Test
		@DisplayName("400 on invalid body")
		void whenRequestBodyInvalid_thenReturns400() throws Exception {
			String invalidJson = "{\"name\":\"\"}";

			mockMvc.perform(post("/api/wines/grapes").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Invalid Request"))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.instance").value("/api/wines/grapes"))
				.andExpect(jsonPath("$.fieldErrors.name").exists());
		}

		@Test
		@DisplayName("400 on invalid json")
		void whenInvalidJson_thenReturns400() throws Exception {
			String invalidJson = "{\"name\": invalid json";

			mockMvc.perform(post("/api/wines/grapes").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Bad Request"))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.instance").value("/api/wines/grapes"));
		}

		@Test
		@DisplayName("409 on existing name")
		void whenNameAlreadyExists_thenReturns409() throws Exception {
			persistGrape("Existing Grape");
			var req = new GrapeUpsertRequest("Existing Grape");

			mockMvc
				.perform(post("/api/wines/grapes").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Constraint Violation"))
				.andExpect(jsonPath("$.status").value(409))
				.andExpect(jsonPath("$.instance").value("/api/wines/grapes"));
		}

	}

	@Nested
	@DisplayName("PUT /api/wines/grapes/{id}")
	class Update {

		@Test
		@DisplayName("updates with valid body")
		void whenRequestBodyValid_thenUpdatesGrape() throws Exception {
			var g = persistGrape("Old Name");
			var req = new GrapeUpsertRequest("New Name");

			mockMvc
				.perform(put("/api/wines/grapes/{id}", g.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(g.getId()))
				.andExpect(jsonPath("$.name").value("New Name"));
		}

		@Test
		@DisplayName("400 on invalid body")
		void whenRequestBodyInvalid_thenReturns400() throws Exception {
			var g = persistGrape("Old Name");
			String invalidJson = "{\"name\":\"\"}";

			mockMvc
				.perform(put("/api/wines/grapes/{id}", g.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(invalidJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Invalid Request"))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.instance").value("/api/wines/grapes/" + g.getId()))
				.andExpect(jsonPath("$.fieldErrors.name").exists());
		}

		@Test
		@DisplayName("409 on existing name")
		void whenNameAlreadyExists_thenReturns409() throws Exception {
			persistGrape("Existing Grape");
			var g = persistGrape("Another Grape");
			var req = new GrapeUpsertRequest("Existing Grape");

			mockMvc
				.perform(put("/api/wines/grapes/{id}", g.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Constraint Violation"))
				.andExpect(jsonPath("$.status").value(409))
				.andExpect(jsonPath("$.instance").value("/api/wines/grapes/" + g.getId()));
		}

		@Test
		@DisplayName("400 on invalid json")
		void badRequestOnInvalidJSON() throws Exception {
			var g = persistGrape("Existing Grape");
			String invalidJson = "{\"name\": invalid json";

			mockMvc
				.perform(put("/api/wines/grapes/{id}", g.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(invalidJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Bad Request"))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.instance").value("/api/wines/grapes/" + g.getId()));
		}

		@Test
		@DisplayName("when grape not available returns 404 json problem response")
		void whenGrapeNotAvailable_thenReturns404() throws Exception {
			var req = new GrapeUpsertRequest("Existing Grape");

			mockMvc
				.perform(put("/api/wines/grapes/{id}", 404).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.detail").value("Could not find grape 404"))
				.andExpect(jsonPath("$.instance").value("/api/wines/grapes/404"));
		}

	}

	@Nested
	@DisplayName("DELETE /api/wines/grapes/{id}")
	class Delete {

		@Test
		@DisplayName("deletes and returns 204")
		void deletes() throws Exception {
			var g = persistGrape("To Delete");

			mockMvc.perform(delete("/api/wines/grapes/{id}", g.getId())).andExpect(status().isNoContent());

			// Ensure it’s gone
			mockMvc.perform(get("/api/wines/grapes/{id}", g.getId())).andExpect(status().isNotFound());
		}

	}

}
