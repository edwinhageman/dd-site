package emh.dd_site.wine.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("WineStyle tests")
public class WineStyleTests {

	private WineStyle wineStyle;

	private final String testName = "Test Style";

	@BeforeEach
	void setUp() {
		wineStyle = new WineStyle(testName);
	}

	@Nested
	@DisplayName("Constructor tests")
	class ConstructorTests {

		@Test
		@DisplayName("Should create WineStyle with required parameters")
		void shouldCreateWithRequiredParameters() {
			assertThat(wineStyle).isNotNull();
			assertThat(wineStyle.getName()).isEqualTo(testName);
		}

		@Test
		@DisplayName("Should throw NullPointerException when name is null")
		void shouldThrowExceptionWhenNameIsNull() {
			assertThrows(NullPointerException.class, () -> new WineStyle(null));
		}

	}

}
