package emh.dd_site.wine.dto;

import emh.dd_site.wine.entity.Grape;
import emh.dd_site.wine.entity.TestGrapeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GrapeMapper tests")
public class GrapeMapperTest {

	private GrapeMapper mapper;

	private Grape testGrape;

	private GrapeUpsertRequest testRequest;

	@BeforeEach
	void setUp() {
		mapper = new GrapeMapper();

		testGrape = TestGrapeBuilder.builder().withId(1).withName("Grape Name").build();

		testRequest = new GrapeUpsertRequest("Upsert Grape Name");
	}

	@Nested
	@DisplayName("to GrapeResponse mapping tests")
	class ToGrapeResponseTests {

		@Test
		@DisplayName("should return null when entity is null")
		void shouldReturnNullWhenEntityIsNull() {
			assertThat(mapper.toGrapeResponse(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.toGrapeResponse(testGrape);

			assertThat(result).isNotNull().satisfies(grapeDto -> {
				assertThat(grapeDto.id()).isEqualTo(testGrape.getId());
				assertThat(grapeDto.name()).isEqualTo(testGrape.getName());
			});
		}

	}

	@Nested
	@DisplayName("from GrapeUpsertRequest mapping tests")
	class FromGrapeUpsertRequestTests {

		@Test
		@DisplayName("should return null when request is null")
		void shouldReturnNullWhenRequestIsNull() {
			assertThat(mapper.fromGrapeUpsertRequest(null)).isNull();
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.fromGrapeUpsertRequest(testRequest);

			assertThat(result).isNotNull().satisfies(grape -> {
				assertThat(grape.getId()).isNull();
				assertThat(grape.getName()).isEqualTo(testRequest.name());
			});
		}

	}

	@Nested
	@DisplayName("merge with GrapeUpsertRequest mapping tests")
	class MergeWithGrapeUpsertRequestTests {

		@Test
		@DisplayName("should return null when grape is null")
		void shouldReturnNullWhenGrapeIsNull() {
			assertThat(mapper.mergeWithGrapeUpsertRequest(null, testRequest)).isNull();
		}

		@Test
		@DisplayName("should return grape when request is null")
		void shouldReturnGrapeWhenRequestIsNull() {
			assertThat(mapper.mergeWithGrapeUpsertRequest(testGrape, null)).isEqualTo(testGrape);
		}

		@Test
		@DisplayName("should map all fields correctly")
		void shouldMapAllFieldsCorrectly() {
			var result = mapper.mergeWithGrapeUpsertRequest(testGrape, testRequest);

			assertThat(result).isNotNull().satisfies(grape -> {
				assertThat(grape.getId()).isEqualTo(testGrape.getId());
				assertThat(grape.getName()).isEqualTo(testRequest.name());
			});
		}

	}

}
