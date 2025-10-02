package emh.dd_site.wine.dto;

import emh.dd_site.wine.WineType;
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

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WineUpsertRequest validation tests")
class WineUpsertRequestValidatorTest {

	private Validator validator;

	private WineUpsertRequest validRequest = new WineUpsertRequest("name", WineType.UNKNOWN, "grape", "country",
			"region", Year.of(2000));

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
			var request = new WineUpsertRequest(null, WineType.UNKNOWN, "grape", "country", "region", Year.of(2000));
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject empty name")
		void shouldRejectEmptyName() {

			var request = new WineUpsertRequest("", WineType.UNKNOWN, "grape", "country", "region", Year.of(2000));
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject whitespace-only name")
		void shouldRejectWhitespaceOnlyName() {

			var request = new WineUpsertRequest("  ", WineType.UNKNOWN, "grape", "country", "region", Year.of(2000));
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

	}

	@Nested
	@DisplayName("type validation")
	class TypeValidation {

		@Test
		@DisplayName("should accept valid type")
		void shouldAcceptValidType() {
			var violations = validator.validate(validRequest);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should reject null type")
		void shouldRejectNullType() {
			var request = new WineUpsertRequest("name", null, "grape", "country", "region", Year.of(2000));
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("type");
				assertThat(violation.getMessage()).isEqualTo("must not be null");
			});
		}

	}

	@Nested
	@DisplayName("grape validation")
	class GrapeValidation {

		@Test
		@DisplayName("should accept valid grape")
		void shouldAcceptValidGrape() {
			var violations = validator.validate(validRequest);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should reject null grape")
		void shouldRejectNullGrape() {
			var request = new WineUpsertRequest("name", WineType.UNKNOWN, null, "country", "region", Year.of(2000));
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("grape");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject empty grape")
		void shouldRejectEmptyGrape() {

			var request = new WineUpsertRequest("name", WineType.UNKNOWN, "", "country", "region", Year.of(2000));
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("grape");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject whitespace-only grape")
		void shouldRejectWhitespaceOnlyGrape() {

			var request = new WineUpsertRequest("name", WineType.UNKNOWN, "  ", "country", "region", Year.of(2000));
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("grape");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

	}

	@Nested
	@DisplayName("country validation")
	class CountryValidation {

		@Test
		@DisplayName("should accept valid country")
		void shouldAcceptValidCountry() {
			var violations = validator.validate(validRequest);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should reject null country")
		void shouldRejectNullCountry() {
			var request = new WineUpsertRequest("name", WineType.UNKNOWN, "grape", null, "region", Year.of(2000));
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("country");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject empty country")
		void shouldRejectEmptyCountry() {

			var request = new WineUpsertRequest("name", WineType.UNKNOWN, "grape", "", "region", Year.of(2000));
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("country");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject whitespace-only country")
		void shouldRejectWhitespaceOnlyCountry() {

			var request = new WineUpsertRequest("name", WineType.UNKNOWN, "grape", "  ", "region", Year.of(2000));
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("country");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
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
			var request = new WineUpsertRequest("name", WineType.UNKNOWN, "grape", "country", value, Year.of(2000));
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

	}

	@Nested
	@DisplayName("year validation")
	class YearValidation {

		@Test
		@DisplayName("should accept valid year")
		void shouldAcceptValidYear() {
			var request = new WineUpsertRequest("name", WineType.UNKNOWN, "grape", "country", "country", Year.of(2000));
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should accept null year")
		void shouldAcceptNullYear() {
			var request = new WineUpsertRequest("name", WineType.UNKNOWN, "grape", "country", "country", null);
			var violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

	}

}