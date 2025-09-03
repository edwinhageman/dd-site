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

@DisplayName("DishUpsertRequest validation tests")
class DishUpsertRequestValidatorTest {

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
		DishUpsertRequest dto = new DishUpsertRequest("Dish Name", "Main Ingredient");

		Set<ConstraintViolation<DishUpsertRequest>> violations = validator.validate(dto);

		assertThat(violations).isEmpty();
	}

	@Nested
	@DisplayName("name Validation")
	class NameValidation {

		@Test
		@DisplayName("should accept valid name")
		void shouldAcceptValidName() {
			DishUpsertRequest dto = new DishUpsertRequest("Dish Name", null);

			Set<ConstraintViolation<DishUpsertRequest>> violations = validator.validate(dto);

			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should reject null name")
		void shouldRejectNullName() {
			DishUpsertRequest dto = new DishUpsertRequest(null, "Main Ingredient");

			Set<ConstraintViolation<DishUpsertRequest>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject empty name")
		void shouldRejectEmptyName() {
			DishUpsertRequest dto = new DishUpsertRequest("", "Main Ingredient");

			Set<ConstraintViolation<DishUpsertRequest>> violations = validator.validate(dto);

			assertThat(violations).hasSize(1).element(0).satisfies(violation -> {
				assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
				assertThat(violation.getMessage()).isEqualTo("must not be blank");
			});
		}

		@Test
		@DisplayName("should reject whitespace-only name")
		void shouldRejectWhitespaceOnlyName() {
			DishUpsertRequest dto = new DishUpsertRequest("   ", "Main Ingredient");

			Set<ConstraintViolation<DishUpsertRequest>> violations = validator.validate(dto);

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
			DishUpsertRequest dto = new DishUpsertRequest("Dish Name", null);

			Set<ConstraintViolation<DishUpsertRequest>> violations = validator.validate(dto);

			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should accept empty mainIngredient")
		void shouldAcceptEmptyMainIngredient() {
			DishUpsertRequest dto = new DishUpsertRequest("Dish Name", "");

			Set<ConstraintViolation<DishUpsertRequest>> violations = validator.validate(dto);

			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("should accept any non-empty mainIngredient")
		void shouldAcceptAnyNonEmptyMainIngredient() {
			DishUpsertRequest dto = new DishUpsertRequest("Dish Name", "Tomato");

			Set<ConstraintViolation<DishUpsertRequest>> violations = validator.validate(dto);

			assertThat(violations).isEmpty();
		}

	}

	@Test
	@DisplayName("should report only name violation when name invalid and mainIngredient null")
	void shouldReportOnlyNameViolation() {
		DishUpsertRequest dto = new DishUpsertRequest("", null);

		Set<ConstraintViolation<DishUpsertRequest>> violations = validator.validate(dto);

		assertThat(violations).hasSize(1)
			.extracting(v -> v.getPropertyPath().toString())
			.containsExactlyInAnyOrder("name");
	}

}