package emh.dd_site.event.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateUpdateCourseDto")
public class CreateUpdateCourseDtoValidatorTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			validator = factory.getValidator();
		}
	}

	@Nested
	@DisplayName("courseNo Validation")
	class CourseNoValidation {

		@Test
		@DisplayName("should accept positive courseNo")
		void shouldAcceptPositiveCourseNo() {
			CreateUpdateCourseDto dto = new CreateUpdateCourseDto(1, "Cook", null);

			Set<ConstraintViolation<CreateUpdateCourseDto>> violations = validator.validate(dto);

			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should reject null courseNo")
		void shouldRejectNullCourseNo() {
			CreateUpdateCourseDto dto = new CreateUpdateCourseDto(null, "Cook", null);

			Set<ConstraintViolation<CreateUpdateCourseDto>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("courseNo");
				assertThat(violation.getMessage()).isEqualTo("must not be null");
			});
		}

		@Test
		@DisplayName("should reject zero courseNo")
		void shouldRejectZeroCourseNo() {
			CreateUpdateCourseDto dto = new CreateUpdateCourseDto(0, "Cook", null);

			Set<ConstraintViolation<CreateUpdateCourseDto>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("courseNo");
				assertThat(violation.getMessage()).isEqualTo("must be greater than 0");
			});
		}

		@Test
		@DisplayName("should reject negative courseNo")
		void shouldRejectNegativeCourseNo() {
			CreateUpdateCourseDto dto = new CreateUpdateCourseDto(-1, "Cook", null);

			Set<ConstraintViolation<CreateUpdateCourseDto>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("courseNo");
				assertThat(violation.getMessage()).isEqualTo("must be greater than 0");
			});
		}

	}

	@Nested
	@DisplayName("cook Validation")
	class CookValidation {

		@Test
		@DisplayName("should accept valid cook")
		void shouldAcceptValidCook() {
			CreateUpdateCourseDto dto = new CreateUpdateCourseDto(1, "Cook Name", null);

			Set<ConstraintViolation<CreateUpdateCourseDto>> violations = validator.validate(dto);

			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should reject null cook")
		void shouldRejectNullCook() {
			CreateUpdateCourseDto dto = new CreateUpdateCourseDto(1, null, null);

			Set<ConstraintViolation<CreateUpdateCourseDto>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("cook");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject empty cook")
		void shouldRejectEmptyCook() {
			CreateUpdateCourseDto dto = new CreateUpdateCourseDto(1, "", null);

			Set<ConstraintViolation<CreateUpdateCourseDto>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("cook");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject whitespace-only cook")
		void shouldRejectWhitespaceOnlyCook() {
			CreateUpdateCourseDto dto = new CreateUpdateCourseDto(1, "   ", null);

			Set<ConstraintViolation<CreateUpdateCourseDto>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("cook");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

	}

	@Nested
	@DisplayName("dish Validation")
	class DishValidation {

		@Test
		@DisplayName("should accept null dish")
		void shouldAcceptNullDish() {
			CreateUpdateCourseDto dto = new CreateUpdateCourseDto(1, "Cook", null);

			Set<ConstraintViolation<CreateUpdateCourseDto>> violations = validator.validate(dto);

			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should validate dish properties when not null")
		void shouldValidateDishPropertiesWhenNotNull() {
			CreateUpdateCourseDto dto = new CreateUpdateCourseDto(1, "Cook", new CreateUpdateDishDto("", null));
			Set<ConstraintViolation<CreateUpdateCourseDto>> violations = validator.validate(dto);

			assertThat(violations).isNotEmpty();
		}

	}

	@Test
	@DisplayName("should validate multiple fields")
	void shouldValidateMultipleFields() {
		CreateUpdateCourseDto dto = new CreateUpdateCourseDto(null, "", null);

		Set<ConstraintViolation<CreateUpdateCourseDto>> violations = validator.validate(dto);

		assertThat(violations).hasSize(2)
			.extracting(v -> v.getPropertyPath().toString())
			.containsExactlyInAnyOrder("courseNo", "cook");
	}

}
