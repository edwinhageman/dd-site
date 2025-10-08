package emh.dd_site.wine.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Grape tests")
public class GrapeTests {

	private Grape grape;

	private final String testName = "Test Grape";

	@BeforeEach
	void setUp() {
		grape = new Grape(testName);
	}

	@Nested
	@DisplayName("Constructor tests")
	class ConstructorTests {

		@Test
		@DisplayName("Should create with required parameters")
		void shouldCreateWithRequiredParameters() {
			assertThat(grape).isNotNull();
			assertThat(grape.getName()).isEqualTo(testName);
		}

		@Test
		@DisplayName("Should throw NullPointerException when name is null")
		void shouldThrowExceptionWhenNameIsNull() {
			assertThrows(NullPointerException.class, () -> new Grape(null));
		}

	}

}
