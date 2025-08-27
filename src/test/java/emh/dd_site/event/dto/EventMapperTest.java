
package emh.dd_site.event.dto;

import emh.dd_site.event.entity.Event;
import emh.dd_site.event.entity.TestEventBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EventMapper tests")
class EventMapperTest {

	private final EventMapper mapper = new EventMapper();

	private LocalDate testDate;

	private Event testEvent;

	private EventUpsertRequest testRequest;

	@BeforeEach
	void setUp() {
		testDate = java.time.LocalDate.of(2024, 1, 1);
		testEvent = TestEventBuilder.anEvent()
			.withId(1L)
			.withDate(testDate)
			.withHost("Original Host")
			.withLocation("Original Location")
			.build();
		testRequest = new EventUpsertRequest(testDate.plusDays(1), "Upsert Host", "Upsert Location");
	}

	@Nested
	@DisplayName("Event to EventResponse mapping tests")
	class ToEventResponseTests {

		@Test
		@DisplayName("should return null when entity is null")
		void shouldReturnNullWhenEntityIsNull() {
			assertThat(mapper.toEventResponse(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			// when
			EventResponse result = mapper.toEventResponse(testEvent);

			// then
			assertThat(result).isNotNull().satisfies(dto -> {
				assertThat(dto.id()).isEqualTo(testEvent.getId());
				assertThat(dto.date()).isEqualTo(testEvent.getDate());
				assertThat(dto.host()).isEqualTo(testEvent.getHost());
				assertThat(dto.location()).isEqualTo(testEvent.getLocation());
			});
		}

		@Test
		@DisplayName("should handle null location")
		void shouldHandleNullLocation() {
			// given
			testEvent.setLocation(null);

			// when
			EventResponse result = mapper.toEventResponse(testEvent);

			// then
			assertThat(result).isNotNull().satisfies(dto -> {
				assertThat(dto.id()).isEqualTo(testEvent.getId());
				assertThat(dto.date()).isEqualTo(testEvent.getDate());
				assertThat(dto.host()).isEqualTo(testEvent.getHost());
				assertThat(dto.location()).isNull();
			});
		}

	}

	@Nested
	@DisplayName("Event from EventUpsertRequest mapping tests")
	class FromEventUpsertRequestTests {

		@Test
		@DisplayName("should return null when request is null")
		void shouldReturnNullWhenRequestIsNull() {
			assertThat(mapper.fromEventUpsertRequest(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			// when
			var result = mapper.fromEventUpsertRequest(testRequest);

			// then
			assertThat(result).isNotNull().satisfies(event -> {
				assertThat(event.getId()).isNull();
				assertThat(event.getDate()).isEqualTo(testRequest.date());
				assertThat(event.getHost()).isEqualTo(testRequest.host());
				assertThat(event.getLocation()).isEqualTo(testRequest.location());
			});
		}

		@Test
		@DisplayName("should handle null location")
		void shouldHandleNullLocation() {
			// given
			testRequest = new EventUpsertRequest(testDate, "Test Host", null);

			// when
			var result = mapper.fromEventUpsertRequest(testRequest);

			// then
			assertThat(result).isNotNull().satisfies(event -> {
				assertThat(event.getId()).isNull();
				assertThat(event.getDate()).isEqualTo(testDate);
				assertThat(event.getHost()).isEqualTo("Test Host");
				assertThat(event.getLocation()).isEqualTo(null);
			});
		}

	}

	@Nested
	@DisplayName("Merge with EventUpsertRequest tests")
	class MergeWithEventUpsertRequestTests {

		@Test
		@DisplayName("should return null when event is null")
		void shouldReturnNullWhenEventIsNull() {
			assertThat(mapper.mergeWithEventUpsertRequest(null, testRequest)).isNull();
		}

		@Test
		@DisplayName("should return event when request is null")
		void shouldReturnEventWhenRequestIsNull() {
			assertThat(mapper.mergeWithEventUpsertRequest(testEvent, null)).isEqualTo(testEvent);
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			// when
			var result = mapper.mergeWithEventUpsertRequest(testEvent, testRequest);

			// then
			assertThat(result).isNotNull().satisfies(event -> {
				assertThat(event.getId()).isEqualTo(testEvent.getId());
				assertThat(event.getDate()).isEqualTo(testRequest.date());
				assertThat(event.getHost()).isEqualTo(testRequest.host());
				assertThat(event.getLocation()).isEqualTo(testRequest.location());
			});
		}

		@Test
		@DisplayName("should handle null location")
		void shouldHandleNullLocation() {
			// given
			var request = new EventUpsertRequest(testDate, "Test Host", null);

			// when
			var result = mapper.mergeWithEventUpsertRequest(testEvent, request);

			// then
			assertThat(result).isNotNull().satisfies(event -> {
				assertThat(event.getId()).isEqualTo(testEvent.getId());
				assertThat(event.getDate()).isEqualTo(testDate);
				assertThat(event.getHost()).isEqualTo("Test Host");
				assertThat(event.getLocation()).isEqualTo(null);
			});
		}

	}

}
