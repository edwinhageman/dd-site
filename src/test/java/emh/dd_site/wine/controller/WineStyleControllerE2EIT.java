package emh.dd_site.wine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import emh.dd_site.TestcontainersConfig;
import emh.dd_site.wine.dto.WineStyleUpsertRequest;
import emh.dd_site.wine.entity.WineStyle;
import emh.dd_site.wine.repository.WineRepository;
import emh.dd_site.wine.repository.WineStyleRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfig.class)
@DisplayName("WineStyleController E2E Tests")
public class WineStyleControllerE2EIT {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	WineRepository wineRepository;

	@Autowired
	WineStyleRepository wineStyleRepository;

	@BeforeEach
	void cleanDb() {
		wineRepository.deleteAll();
		wineStyleRepository.deleteAll();
	}

	private WineStyle persistWineStyle(String name) {
		var style = new WineStyle(name);
		return wineStyleRepository.save(style);
	}

	@Nested
	@DisplayName("GET /api/wines/styles")
	class ListAll {

		@Test
		@DisplayName("when styles available then returns page sorted by name")
		void whenStylesAvailable_thenReturnsPageSortedByName() throws Exception {
			var s1 = persistWineStyle("Style X");
			var s2 = persistWineStyle("Style A");

			mockMvc.perform(get("/api/wines/styles").param("page", "0").param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0].id").value(s2.getId()))
				.andExpect(jsonPath("$.content[0].name").value("Style A"))
				.andExpect(jsonPath("$.content[1].id").value(s1.getId()))
				.andExpect(jsonPath("$.content[1].name").value("Style X"));
		}

		@Test
		@DisplayName("when no styles available then returns empty page")
		void whenNoStylesAvailable_thenReturnsEmptyPage() throws Exception {
			mockMvc.perform(get("/api/wines/styles").param("page", "0").param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(0)));
		}

	}

	@Nested
	@DisplayName("GET /api/wines/{id}")
	class GetOne {

		@Test
		@DisplayName("when style available then returns style")
		void whenStyleAvailable_theReturnsStyle() throws Exception {
			var s = persistWineStyle("Style1");

			mockMvc.perform(get("/api/wines/styles/{id}", s.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(s.getId()))
				.andExpect(jsonPath("$.name").value("Style1"));
		}

		@Test
		@DisplayName("when style not available returns 404 json problem response")
		void whenStyleNotAvailable_thenReturns404() throws Exception {
			mockMvc.perform(get("/api/wines/styles/{id}", 404))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.detail").value("Could not find wine style 404"))
				.andExpect(jsonPath("$.instance").value("/api/wines/styles/404"));
		}

	}

	@Nested
	@DisplayName("POST /api/wines/styles")
	class Create {

		@Test
		@DisplayName("creates with valid body")
		void creates() throws Exception {
			var req = new WineStyleUpsertRequest("Created Style");

			mockMvc
				.perform(post("/api/wines/styles").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("Created Style"));
		}

		@Test
		@DisplayName("400 on invalid body")
		void badRequestOnInvalidBody() throws Exception {
			String invalidJson = "{\"name\":\"\"}";

			mockMvc.perform(post("/api/wines/styles").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Invalid request body"))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.detail").value("Invalid request content."))
				.andExpect(jsonPath("$.instance").value("/api/wines/styles"));
		}

		@Test
		@DisplayName("400 on existing name")
		void badRequestOnExistingName() throws Exception {
			var w = persistWineStyle("Existing Style");
			var req = new WineStyleUpsertRequest("Existing Style");

			mockMvc
				.perform(post("/api/wines/styles").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Constraint violation"))
				.andExpect(jsonPath("$.status").value(409))
				.andExpect(jsonPath("$.detail").value("Data integrity violation."))
				.andExpect(jsonPath("$.instance").value("/api/wines/styles"));
		}

	}

	@Nested
	@DisplayName("PUT /api/wines/styles/{id}")
	class Update {

		@Test
		@DisplayName("updates with valid body")
		void updates() throws Exception {
			var w = persistWineStyle("Old Name");
			var req = new WineStyleUpsertRequest("New Name");

			mockMvc
				.perform(put("/api/wines/styles/{id}", w.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(w.getId()))
				.andExpect(jsonPath("$.name").value("New Name"));
		}

		@Test
		@DisplayName("400 on invalid body")
		void badRequestOnInvalidBody() throws Exception {
			var w = persistWineStyle("Old Name");
			String invalidJson = "{\"name\":\"\"}";

			mockMvc
				.perform(put("/api/wines/styles/{id}", w.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(invalidJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Invalid request body"))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.detail").value("Invalid request content."))
				.andExpect(jsonPath("$.instance").value("/api/wines/styles/" + w.getId()));
		}

		@Test
		@DisplayName("400 on existing name")
		void badRequestOnExistingName() throws Exception {
			persistWineStyle("Existing Style");
			var w = persistWineStyle("Another Style");
			var req = new WineStyleUpsertRequest("Existing Style");

			mockMvc
				.perform(put("/api/wines/styles/{id}", w.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Constraint violation"))
				.andExpect(jsonPath("$.status").value(409))
				.andExpect(jsonPath("$.detail").value("Data integrity violation."))
				.andExpect(jsonPath("$.instance").value("/api/wines/styles/" + w.getId()));
		}

	}

	@Nested
	@DisplayName("DELETE /api/wines/styles/{id}")
	class Delete {

		@Test
		@DisplayName("deletes and returns 204")
		void deletes() throws Exception {
			var w = persistWineStyle("To Delete");

			mockMvc.perform(delete("/api/wines/styles/{id}", w.getId())).andExpect(status().isNoContent());

			// Ensure it’s gone
			mockMvc.perform(get("/api/wines/styles/{id}", w.getId())).andExpect(status().isNotFound());
		}

	}

}
