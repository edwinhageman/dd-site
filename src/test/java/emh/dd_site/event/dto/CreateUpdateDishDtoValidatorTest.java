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

@DisplayName("CreateUpdateDishDto")
class CreateUpdateDishDtoValidatorTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			validator = factory.getValidator();
		}
	}

	@Test
	@DisplayName("should accept valid dish")
	void shouldAcceptValidDish() {
		CreateUpdateDishDto dto = new CreateUpdateDishDto("Dish Name", "Main Ingredient");

		Set<ConstraintViolation<CreateUpdateDishDto>> violations = validator.validate(dto);

		assertThat(violations).isEmpty();
	}

	@Nested
	@DisplayName("name Validation")
	class NameValidation {

		@Test
		@DisplayName("should accept valid name")
		void shouldAcceptValidName() {
			CreateUpdateDishDto dto = new CreateUpdateDishDto("Dish Name", null);

			Set<ConstraintViolation<CreateUpdateDishDto>> violations = validator.validate(dto);

			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should reject null name")
		void shouldRejectNullName() {
			CreateUpdateDishDto dto = new CreateUpdateDishDto(null, "Main Ingredient");

			Set<ConstraintViolation<CreateUpdateDishDto>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject empty name")
		void shouldRejectEmptyName() {
			CreateUpdateDishDto dto = new CreateUpdateDishDto("", "Main Ingredient");

			Set<ConstraintViolation<CreateUpdateDishDto>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject whitespace-only name")
		void shouldRejectWhitespaceOnlyName() {
			CreateUpdateDishDto dto = new CreateUpdateDishDto("   ", "Main Ingredient");

			Set<ConstraintViolation<CreateUpdateDishDto>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

	}

	@Nested
	@DisplayName("mainIngredient Validation")
	class MainIngredientValidation {

		@Test
		@DisplayName("should accept null mainIngredient")
		void shouldAcceptNullMainIngredient() {
			CreateUpdateDishDto dto = new CreateUpdateDishDto("Dish Name", null);

			Set<ConstraintViolation<CreateUpdateDishDto>> violations = validator.validate(dto);

			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should accept empty mainIngredient")
		void shouldAcceptEmptyMainIngredient() {
			CreateUpdateDishDto dto = new CreateUpdateDishDto("Dish Name", "");

			Set<ConstraintViolation<CreateUpdateDishDto>> violations = validator.validate(dto);

			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should accept any non-empty mainIngredient")
		void shouldAcceptAnyNonEmptyMainIngredient() {
			CreateUpdateDishDto dto = new CreateUpdateDishDto("Dish Name", "Tomato");

			Set<ConstraintViolation<CreateUpdateDishDto>> violations = validator.validate(dto);

			assertThat(violations).isEmpty();
		}

	}

	@Test
	@DisplayName("should report only name violation when name invalid and mainIngredient null")
	void shouldReportOnlyNameViolation() {
		CreateUpdateDishDto dto = new CreateUpdateDishDto("", null);

		Set<ConstraintViolation<CreateUpdateDishDto>> violations = validator.validate(dto);

		assertThat(violations).hasSize(1)
			.extracting(v -> v.getPropertyPath().toString())
			.containsExactlyInAnyOrder("name");
	}

}