package emh.dd_site.wine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import emh.dd_site.TestcontainersConfig;
import emh.dd_site.event.entity.Event;
import emh.dd_site.event.repository.CourseRepository;
import emh.dd_site.event.repository.EventRepository;
import emh.dd_site.wine.dto.WineUpsertRequest;
import emh.dd_site.wine.entity.Grape;
import emh.dd_site.wine.entity.Wine;
import emh.dd_site.wine.entity.WineStyle;
import emh.dd_site.wine.repository.GrapeRepository;
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

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfig.class)
@DisplayName("WineController E2E Tests")
public class WineControllerE2EIT {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	WineRepository wineRepository;

	@Autowired
	GrapeRepository grapeRepository;

	@Autowired
	WineStyleRepository wineStyleRepository;

	@Autowired
	EventRepository eventRepository;

	@Autowired
	CourseRepository courseRepository;

	private Grape grape1, grape2;

	private WineStyle style1, style2;

	private Wine wine1, wine2, wine3;

	private Event event;

	@BeforeEach
	void setUp() {
		// deletion order matters -> FK relations
		courseRepository.deleteAll();
		eventRepository.deleteAll();
		wineRepository.deleteAll();
		wineStyleRepository.deleteAll();
		grapeRepository.deleteAll();

		grape1 = grapeRepository.save(new Grape("Grape 1"));
		grape2 = grapeRepository.save(new Grape("Grape 2"));
		style1 = wineStyleRepository.save(new WineStyle("Style 1"));
		style2 = wineStyleRepository.save(new WineStyle("Style 2"));

		wine1 = new Wine("b Wine1");
		wine1.setWinery("Winery1");
		wine1.setCountry("Country1");
		wine1.setRegion("Region1");
		wine1.setAppellation("Appellation1");
		wine1.setVintage(Year.of(2021));
		wine1.setVivinoUrl("https://vivino.com/1");
		wine1.addStyle(style1);

		wine2 = new Wine("a Wine2");
		wine2.setWinery("Winery2");
		wine2.setCountry("Country2");
		wine2.setRegion("Region2");
		wine2.setAppellation("Appellation2");
		wine2.setVintage(Year.of(2022));
		wine2.setVivinoUrl("https://vivino.com/2");
		wine2.addStyle(style2);

		wine3 = new Wine("c Wine3");
		wine3.setWinery("Winery3");
		wine3.setCountry("Country3");
		wine3.setRegion("Region3");
		wine3.setAppellation("Appellation3");
		wine3.setVintage(Year.of(2023));
		wine3.setVivinoUrl("https://vivino.com/3");
		wine3.addStyle(style1);
		wine3.addStyle(style2);

		wine1 = wineRepository.save(wine1);
		wine2 = wineRepository.save(wine2);
		wine3 = wineRepository.save(wine3);

		// added grape relations after persisting wines because of the cascade persist on
		// grape composition
		wine1.addGrape(grape1, BigDecimal.ONE);
		wine2.addGrape(grape2, null);
		wine3.addGrape(grape1, BigDecimal.valueOf(.7));
		wine3.addGrape(grape2, BigDecimal.valueOf(.3));

		wine1 = wineRepository.save(wine1);
		wine2 = wineRepository.save(wine2);
		wine3 = wineRepository.save(wine3);

		event = eventRepository.save(new Event(java.time.LocalDate.of(2025, 1, 1), "Event 1"));
		var course1 = new emh.dd_site.event.entity.Course(event, 1, "Cook 1");
		course1.setWine(wine3);
		var course2 = new emh.dd_site.event.entity.Course(event, 2, "Cook 2");
		course2.setWine(wine2);
		courseRepository.saveAll(java.util.List.of(course1, course2));
	}

	@Nested
	@DisplayName("GET /api/wines")
	class ListAll {

		@Test
		@DisplayName("when wines available then returns page sorted by name")
		void whenWinesAvailable_thenReturnsPageSortedByName() throws Exception {
			mockMvc.perform(get("/api/wines").param("page", "0").param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(3)))
				.andExpect(jsonPath("$.content[0].id").value(wine2.getId()))
				.andExpect(jsonPath("$.content[0].name").value(wine2.getName()))
				.andExpect(jsonPath("$.content[0].winery").value(wine2.getWinery()))
				.andExpect(jsonPath("$.content[0].country").value(wine2.getCountry()))
				.andExpect(jsonPath("$.content[0].region").value(wine2.getRegion()))
				.andExpect(jsonPath("$.content[0].appellation").value(wine2.getAppellation()))
				.andExpect(jsonPath("$.content[0].vintage").value(wine2.getVintage().toString()))
				.andExpect(jsonPath("$.content[0].vivinoUrl").value(wine2.getVivinoUrl()))
				.andExpect(jsonPath("$.content[0].grapeComposition", hasSize(1)))
				.andExpect(jsonPath("$.content[0].styles", hasSize(1)))
				.andExpect(jsonPath("$.content[1].id").value(wine1.getId()))
				.andExpect(jsonPath("$.content[1].grapeComposition", hasSize(1)))
				.andExpect(jsonPath("$.content[1].styles", hasSize(1)))
				.andExpect(jsonPath("$.content[2].grapeComposition", hasSize(2)))
				.andExpect(jsonPath("$.content[2].grapeComposition[*].grape.id")
					.value(containsInAnyOrder(grape1.getId(), grape2.getId())))
				.andExpect(jsonPath("$.content[2].grapeComposition[*].grape.name")
					.value(containsInAnyOrder(grape1.getName(), grape2.getName())))
				.andExpect(jsonPath("$.content[2].styles", hasSize(2)))
				.andExpect(
						jsonPath("$.content[2].styles[*].id").value(containsInAnyOrder(style1.getId(), style2.getId())))
				.andExpect(jsonPath("$.content[2].styles[*].name")
					.value(containsInAnyOrder(style1.getName(), style2.getName())));
		}

		@Test
		@DisplayName("when no wines available then returns empty page")
		void whenNoWinesAvailable_thenReturnsEmptyPage() throws Exception {
			wineRepository.deleteAll();

			mockMvc.perform(get("/api/wines").param("page", "0").param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(0)));
		}

	}

	@Nested
	@DisplayName("GET /api/events/{eventId}/wines")
	class ListAllByEvent {

		@Test
		@DisplayName("when wines available then returns page sorted by name")
		void whenWinesAvailable_thenReturnsPageSortedByName() throws Exception {

			mockMvc.perform(get("/api/events/{eventId}/wines", event.getId()).param("page", "0").param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0].id").value(wine3.getId()))
				.andExpect(jsonPath("$.content[0].name").value(wine3.getName()))
				.andExpect(jsonPath("$.content[0].winery").value(wine3.getWinery()))
				.andExpect(jsonPath("$.content[0].country").value(wine3.getCountry()))
				.andExpect(jsonPath("$.content[0].region").value(wine3.getRegion()))
				.andExpect(jsonPath("$.content[0].appellation").value(wine3.getAppellation()))
				.andExpect(jsonPath("$.content[0].vintage").value(wine3.getVintage().toString()))
				.andExpect(jsonPath("$.content[0].vivinoUrl").value(wine3.getVivinoUrl()))
				.andExpect(jsonPath("$.content[0].grapeComposition", hasSize(2)))
				.andExpect(jsonPath("$.content[0].grapeComposition[0].grape.id").value(grape1.getId()))
				.andExpect(jsonPath("$.content[0].grapeComposition[0].grape.name").value(grape1.getName()))
				.andExpect(jsonPath("$.content[0].grapeComposition[0].percentage").value(BigDecimal.valueOf(.7)))
				.andExpect(jsonPath("$.content[0].styles", hasSize(2)))
				.andExpect(jsonPath("$.content[0].styles[0].id").value(style1.getId()))
				.andExpect(jsonPath("$.content[0].styles[0].name").value(style1.getName()))
				.andExpect(jsonPath("$.content[1].id").value(wine2.getId()));
		}

		@Test
		@DisplayName("when no wines available then returns empty page")
		void whenNoWinesAvailable_thenReturnsEmptyPage() throws Exception {
			wineRepository.deleteAll(List.of(wine2, wine3));
			mockMvc.perform(get("/api/events/1/wines").param("page", "0").param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(0)));
		}

	}

	@Nested
	@DisplayName("GET /api/wines/{id}")
	class GetOne {

		@Test
		@DisplayName("when wine available then returns wine")
		void whenWineAvailable_theReturnsWine() throws Exception {
			var composition = wine1.getGrapeComposition().stream().toList().getFirst();

			mockMvc.perform(get("/api/wines/{id}", wine1.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(wine1.getId()))
				.andExpect(jsonPath("$.name").value(wine1.getName()))
				.andExpect(jsonPath("$.winery").value(wine1.getWinery()))
				.andExpect(jsonPath("$.country").value(wine1.getCountry()))
				.andExpect(jsonPath("$.region").value(wine1.getRegion()))
				.andExpect(jsonPath("$.appellation").value(wine1.getAppellation()))
				.andExpect(jsonPath("$.vintage").value(wine1.getVintage().toString()))
				.andExpect(jsonPath("$.vivinoUrl").value(wine1.getVivinoUrl()))
				.andExpect(jsonPath("$.grapeComposition", hasSize(1)))
				.andExpect(jsonPath("$.styles", hasSize(1)));
		}

		@Test
		@DisplayName("when wine not available returns 404 json problem response")
		void whenWineNotAvailable_thenReturns404() throws Exception {
			mockMvc.perform(get("/api/wines/{id}", 404))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.instance").value("/api/wines/404"));
		}

	}

	@Nested
	@DisplayName("POST /api/wines")
	class Create {

		@Test
		@DisplayName("creates with valid body")
		void creates() throws Exception {
			var req = new WineUpsertRequest("Created Wine", "Created Winery", "Created Country", "Created Region",
					"Created Appellation", Year.of(2025), "https://vivino.com/created", List.of(style1.getId()),
					List.of(new WineUpsertRequest.GrapeCompositionRequest(grape1.getId(), BigDecimal.ONE)));

			mockMvc
				.perform(post("/api/wines").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("Created Wine"))
				.andExpect(jsonPath("$.winery").value("Created Winery"))
				.andExpect(jsonPath("$.country").value("Created Country"))
				.andExpect(jsonPath("$.region").value("Created Region"))
				.andExpect(jsonPath("$.appellation").value("Created Appellation"))
				.andExpect(jsonPath("$.vintage").value("2025"))
				.andExpect(jsonPath("$.vivinoUrl").value("https://vivino.com/created"))
				.andExpect(jsonPath("$.grapeComposition", hasSize(1)))
				.andExpect(jsonPath("$.grapeComposition[0].grape.id").value(grape1.getId()))
				.andExpect(jsonPath("$.grapeComposition[0].grape.name").value(grape1.getName()))
				.andExpect(jsonPath("$.grapeComposition[0].percentage").value(comparesEqualTo(BigDecimal.ONE),
						BigDecimal.class))
				.andExpect(jsonPath("$.styles", hasSize(1)))
				.andExpect(jsonPath("$.styles[0].id").value(style1.getId()))
				.andExpect(jsonPath("$.styles[0].name").value(style1.getName()));
		}

		@Test
		@DisplayName("400 on invalid body")
		void badRequestOnInvalidBody() throws Exception {
			String invalidJson = "{\"name\":\"\"}";

			mockMvc.perform(post("/api/wines").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Invalid Request"))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.instance").value("/api/wines"))
				.andExpect(jsonPath("$.fieldErrors.name").exists());
		}

		@Test
		@DisplayName("400 on invalid json")
		void badRequestOnInvalidJSON() throws Exception {
			String invalidJson = "{\"name\": invalid json";

			mockMvc.perform(post("/api/wines").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Bad Request"))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.instance").value("/api/wines"));
		}

	}

	@Nested
	@DisplayName("PUT /api/wines/{id}")
	class Update {

		@Test
		@DisplayName("updates with valid body")
		void updates() throws Exception {
			var req = new WineUpsertRequest("Updated Wine", "Updated Winery", "Updated Country", "Updated Region",
					"Updated Appellation", Year.of(2000), "https://vivino.com/updated", List.of(style2.getId()), null);

			mockMvc
				.perform(put("/api/wines/{id}", wine1.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated Wine"))
				.andExpect(jsonPath("$.winery").value("Updated Winery"))
				.andExpect(jsonPath("$.country").value("Updated Country"))
				.andExpect(jsonPath("$.region").value("Updated Region"))
				.andExpect(jsonPath("$.appellation").value("Updated Appellation"))
				.andExpect(jsonPath("$.vintage").value("2000"))
				.andExpect(jsonPath("$.vivinoUrl").value("https://vivino.com/updated"))
				.andExpect(jsonPath("$.grapeComposition", hasSize(1)))
				.andExpect(jsonPath("$.styles", hasSize(1)))
				.andExpect(jsonPath("$.styles[0].id").value(style2.getId()));
		}

		@Test
		@DisplayName("400 on invalid body")
		void badRequestOnInvalidBody() throws Exception {
			String invalidJson = "{\"name\":\"\"}";

			mockMvc
				.perform(put("/api/wines/{id}", wine1.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(invalidJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Invalid Request"))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.instance").value("/api/wines/" + wine1.getId()));
		}

		@Test
		@DisplayName("when wine not available returns 404 json problem response")
		void whenWineNotAvailable_thenReturns404() throws Exception {
			var req = new WineUpsertRequest("Updated Wine", "Updated Winery", "Updated Country", "Updated Region",
					"Updated Appellation", Year.of(2000), "https://vivino.com/updated", null, null);

			mockMvc
				.perform(put("/api/wines/{id}", 404).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.instance").value("/api/wines/404"));
		}

	}

	@Nested
	@DisplayName("DELETE /api/wines/{id}")
	class Delete {

		@Test
		@DisplayName("deletes and returns 204")
		void deletes() throws Exception {
			mockMvc.perform(delete("/api/wines/{id}", wine1.getId())).andExpect(status().isNoContent());

			// make sure it is gone
			mockMvc.perform(get("/api/wines/{id}", wine1.getId())).andExpect(status().isNotFound());
		}

	}

}
