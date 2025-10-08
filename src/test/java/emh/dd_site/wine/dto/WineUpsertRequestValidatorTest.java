package emh.dd_site.wine.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Year;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WineUpsertRequest validation tests")
class WineUpsertRequestValidatorTest {

	private Validator validator;

	private final WineUpsertRequest validRequest = new WineUpsertRequest("name", "winery", "country", "region",
			"appellation", Year.of(2000), "https://vivino.com", Collections.emptyList(), Collections.emptyList());

	@BeforeEach
	void setUp() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			validator = factory.getValidator();
		}
	}

	@Nested
	@DisplayName("name validation")
	class NameValidation {

		@Test
		@DisplayName("should accept valid name")
		void shouldAcceptValidName() {
			var violations = validator.validate(validRequest);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should reject null name")
		void shouldRejectNullName() {
			var request = new WineUpsertRequest(null, "winery", "country", "region", "appellation", Year.of(2000),
					"https://vivino.com", Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject empty name")
		void shouldRejectEmptyName() {

			var request = new WineUpsertRequest("", "winery", "country", "region", "appellation", Year.of(2000),
					"https://vivino.com", Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject whitespace-only name")
		void shouldRejectWhitespaceOnlyName() {

			var request = new WineUpsertRequest("  ", "winery", "country", "region", "appellation", Year.of(2000),
					"https://vivino.com", Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

	}

	@Nested
	@DisplayName("winery validation")
	class WineryValidation {

		@ParameterizedTest
		@ValueSource(strings = { "winery", "", "   " })
		@NullSource
		@DisplayName("should accept valid winery")
		void shouldAcceptValidWinery(String value) {
			var request = new WineUpsertRequest("name", value, "country", "region", "appellation", Year.of(2000),
					"https://vivino.com", Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

	}

	@Nested
	@DisplayName("country validation")
	class CountryValidation {

		@ParameterizedTest
		@ValueSource(strings = { "country", "", "   " })
		@NullSource
		@DisplayName("should accept valid country")
		void shouldAcceptValidCountry(String value) {
			var request = new WineUpsertRequest("name", "winery", value, "region", "appellation", Year.of(2000),
					"https://vivino.com", Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

	}

	@Nested
	@DisplayName("region validation")
	class RegionValidation {

		@ParameterizedTest
		@ValueSource(strings = { "region", "", "   " })
		@NullSource
		@DisplayName("should accept valid region")
		void shouldAcceptValidRegion(String value) {
			var request = new WineUpsertRequest("name", "winery", "country", value, "appellation", Year.of(2000),
					"https://vivino.com", Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

	}

	@Nested
	@DisplayName("appellation validation")
	class AppellationValidation {

		@ParameterizedTest
		@ValueSource(strings = { "appellation", "", "   " })
		@NullSource
		@DisplayName("should accept valid appellation")
		void shouldAcceptValidAppellation(String value) {
			var request = new WineUpsertRequest("name", "winery", "country", "region", value, Year.of(2000),
					"https://vivino.com", Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

	}

	@Nested
	@DisplayName("vintage validation")
	class VintageValidation {

		@Test
		@DisplayName("should accept valid vintage")
		void shouldAcceptValidYear() {
			var request = new WineUpsertRequest("name", "winery", "country", "region", "appellation", Year.of(2000),
					"https://vivino.com", Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should accept null year")
		void shouldAcceptNullYear() {
			var request = new WineUpsertRequest("name", "winery", "country", "region", "appellation", null,
					"https://vivino.com", Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

	}

	@Nested
	@DisplayName("vivino url validation")
	class VivinoUrlValidation {

		@Test
		@DisplayName("should accept valid url")
		void shouldAcceptValidVivinoUrl() {
			var request = new WineUpsertRequest("name", "winery", "country", "region", "appellation", Year.of(2000),
					"https://vivino.com", Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should accept null url")
		void shouldAcceptNullYear() {
			var request = new WineUpsertRequest("name", "winery", "country", "region", "appellation", Year.of(2000),
					null, Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should reject invalid url")
		void shouldRejectNullName() {
			var request = new WineUpsertRequest("name", "winery", "country", "region", "appellation", Year.of(2000),
					"https://www.vivinoo.com", Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("vivinoUrl");
				assertThat(violation.getMessage()).isEqualTo("must be a valid URL");
			});
		}

	}

	@Nested
	@DisplayName("styles validation")
	class StylesValidation {

		@Test
		@DisplayName("should accept valid styles")
		void shouldAcceptValidStyles() {
			var request = new WineUpsertRequest("name", "winery", "country", "region", "appellation", Year.of(2000),
					"https://vivino.com", Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should accept null styles")
		void shouldAcceptNullStyles() {
			var request = new WineUpsertRequest("name", "winery", "country", "region", "appellation", Year.of(2000),
					"https://vivino.com", null, Collections.emptyList());
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

	}

	@Nested
	@DisplayName("grapes validation")
	class GrapesValidation {

		@Test
		@DisplayName("should accept valid grapes")
		void shouldAcceptValidGrapes() {
			var request = new WineUpsertRequest("name", "winery", "country", "region", "appellation", Year.of(2000),
					"https://vivino.com", Collections.emptyList(), Collections.emptyList());
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should accept null grapes")
		void shouldAcceptNullGrapes() {
			var request = new WineUpsertRequest("name", "winery", "country", "region", "appellation", Year.of(2000),
					"https://vivino.com", Collections.emptyList(), null);
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

	}

}