package emh.dd_site.wine.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WineStyleUpsertRequest validation tests")
class WineStyleUpsertRequestValidatorTest {

	private Validator validator;

	private final WineStyleUpsertRequest validRequest = new WineStyleUpsertRequest("name");

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
			var request = new WineStyleUpsertRequest(null);
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject empty name")
		void shouldRejectEmptyName() {

			var request = new WineStyleUpsertRequest("");
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject whitespace-only name")
		void shouldRejectWhitespaceOnlyName() {

			var request = new WineStyleUpsertRequest("  ");
			var violations = validator.validate(request);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

	}

}