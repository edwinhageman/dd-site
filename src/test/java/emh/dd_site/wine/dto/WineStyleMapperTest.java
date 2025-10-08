package emh.dd_site.wine.dto;

import emh.dd_site.wine.entity.TestWineStyleBuilder;
import emh.dd_site.wine.entity.WineStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WineStyleMapper tests")
public class WineStyleMapperTest {

	private WineStyleMapper mapper;

	private WineStyle testStyle;

	private WineStyleUpsertRequest testRequest;

	@BeforeEach
	void setUp() {
		mapper = new WineStyleMapper();

		testStyle = TestWineStyleBuilder.builder().withId(1).withName("Style Name").build();

		testRequest = new WineStyleUpsertRequest("Upsert Style Name");
	}

	@Nested
	@DisplayName("to WineStyleResponse mapping tests")
	class ToWineStyleResponseTests {

		@Test
		@DisplayName("should return null when entity is null")
		void shouldReturnNullWhenEntityIsNull() {
			assertThat(mapper.toWineStyleResponse(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.toWineStyleResponse(testStyle);

			assertThat(result).isNotNull().satisfies(style -> {
				assertThat(style.id()).isEqualTo(testStyle.getId());
				assertThat(style.name()).isEqualTo(testStyle.getName());
			});
		}

	}

	@Nested
	@DisplayName("from WineStyleUpsertRequest mapping tests")
	class FromWineStyleUpsertRequestTests {

		@Test
		@DisplayName("should return null when request is null")
		void shouldReturnNullWhenRequestIsNull() {
			assertThat(mapper.fromWineStyleUpsertRequest(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.fromWineStyleUpsertRequest(testRequest);

			assertThat(result).isNotNull().satisfies(style -> {
				assertThat(style.getId()).isNull();
				assertThat(style.getName()).isEqualTo(testRequest.name());
			});
		}

	}

	@Nested
	@DisplayName("merge with WineStyleUpsertRequest mapping tests")
	class MergeWithWineStyleUpsertRequestTests {

		@Test
		@DisplayName("should return null when style is null")
		void shouldReturnNullWhenWineStyleIsNull() {
			assertThat(mapper.mergeWithWineStyleUpsertRequest(null, testRequest)).isNull();
		}

		@Test
		@DisplayName("should return style when request is null")
		void shouldReturnWineStyleWhenRequestIsNull() {
			assertThat(mapper.mergeWithWineStyleUpsertRequest(testStyle, null)).isEqualTo(testStyle);
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.mergeWithWineStyleUpsertRequest(testStyle, testRequest);

			assertThat(result).isNotNull().satisfies(style -> {
				assertThat(style.getId()).isEqualTo(testStyle.getId());
				assertThat(style.getName()).isEqualTo(testRequest.name());
			});
		}

	}

}
