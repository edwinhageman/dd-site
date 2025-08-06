package emh.dd_site.event.entity;

import emh.dd_site.event.WineType;

import java.time.Year;

import static emh.dd_site.RecursionUtils.setPrivateField;

public class TestWineBuilder {

	private final Wine wine;

	private TestWineBuilder(String name, WineType type, String grape, String country) {
		this.wine = new Wine(name, type, grape, country);
		setPrivateField(wine, "id", 1L);
	}

	public static TestWineBuilder aWine() {
		return new TestWineBuilder("Test Wine", WineType.WHITE, "Test Grape", "Test Country");
	}

	public TestWineBuilder withId(Long id) {
		setPrivateField(wine, "id", id);
		return this;
	}

	public TestWineBuilder withName(String name) {
		this.wine.setName(name);
		return this;
	}

	public TestWineBuilder withType(WineType type) {
		this.wine.setType(type);
		return this;
	}

	public TestWineBuilder withGrape(String grape) {
		this.wine.setGrape(grape);
		return this;
	}

	public TestWineBuilder withCountry(String country) {
		this.wine.setCountry(country);
		return this;
	}

	public TestWineBuilder withRegion(String region) {
		this.wine.setRegion(region);
		return this;
	}

	public TestWineBuilder withYear(Year year) {
		this.wine.setYear(year);
		return this;
	}

	Wine build() {
		return wine;
	}

}
