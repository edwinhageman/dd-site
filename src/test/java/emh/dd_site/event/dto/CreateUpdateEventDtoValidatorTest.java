
package emh.dd_site.event.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateUpdateEventDto")
class CreateUpdateEventDtoValidatorTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			validator = factory.getValidator();
		}
	}

	@Nested
	@DisplayName("Date Validation")
	class DateValidation {

		@Test
		@DisplayName("should accept valid date")
		void shouldAcceptValidDate() {
			CreateUpdateEventDto dto = new CreateUpdateEventDto(LocalDate.now(), "Test Host", "Test Location");

			Set<ConstraintViolation<CreateUpdateEventDto>> violations = validator.validate(dto);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should reject null date")
		void shouldRejectNullDate() {
			CreateUpdateEventDto dto = new CreateUpdateEventDto(null, "Test Host", "Test Location");

			Set<ConstraintViolation<CreateUpdateEventDto>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("date");
				assertThat(violation.getMessage()).isEqualTo("must not be null");
			});
		}

	}

	@Nested
	@DisplayName("Host Validation")
	class HostValidation {

		@Test
		@DisplayName("should accept valid host")
		void shouldAcceptValidHost() {
			CreateUpdateEventDto dto = new CreateUpdateEventDto(LocalDate.now(), "Test Host", "Test Location");

			Set<ConstraintViolation<CreateUpdateEventDto>> violations = validator.validate(dto);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should reject null host")
		void shouldRejectNullHost() {
			CreateUpdateEventDto dto = new CreateUpdateEventDto(LocalDate.now(), null, "Test Location");

			Set<ConstraintViolation<CreateUpdateEventDto>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("host");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject empty host")
		void shouldRejectEmptyHost() {
			CreateUpdateEventDto dto = new CreateUpdateEventDto(LocalDate.now(), "", "Test Location");

			Set<ConstraintViolation<CreateUpdateEventDto>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("host");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject whitespace-only host")
		void shouldRejectWhitespaceOnlyHost() {
			CreateUpdateEventDto dto = new CreateUpdateEventDto(LocalDate.now(), "   ", "Test Location");

			Set<ConstraintViolation<CreateUpdateEventDto>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("host");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

	}

	@Nested
	@DisplayName("Location Validation")
	class LocationValidation {

		@Test
		@DisplayName("should accept valid location")
		void shouldAcceptValidLocation() {
			CreateUpdateEventDto dto = new CreateUpdateEventDto(LocalDate.now(), "Test Host", "Test Location");

			Set<ConstraintViolation<CreateUpdateEventDto>> violations = validator.validate(dto);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should accept null location")
		void shouldAcceptNullLocation() {
			CreateUpdateEventDto dto = new CreateUpdateEventDto(LocalDate.now(), "Test Host", null);

			Set<ConstraintViolation<CreateUpdateEventDto>> violations = validator.validate(dto);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should accept empty location")
		void shouldAcceptEmptyLocation() {
			CreateUpdateEventDto dto = new CreateUpdateEventDto(LocalDate.now(), "Test Host", "");

			Set<ConstraintViolation<CreateUpdateEventDto>> violations = validator.validate(dto);
			assertThat(violations).isEmpty();
		}

	}

	@Test
	@DisplayName("should validate multiple fields")
	void shouldValidateMultipleFields() {
		CreateUpdateEventDto dto = new CreateUpdateEventDto(null, "", null);

		Set<ConstraintViolation<CreateUpdateEventDto>> violations = validator.validate(dto);

		assertThat(violations).hasSize(2)
			.extracting(violation -> violation.getPropertyPath().toString())
			.containsExactlyInAnyOrder("date", "host");
	}

}
