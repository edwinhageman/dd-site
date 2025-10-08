package emh.dd_site.wine.entity;

import emh.dd_site.event.entity.Course;

import java.math.BigDecimal;
import java.time.Year;

import static emh.dd_site.RecursionUtils.setPrivateField;

public class TestWineBuilder {

	private final Wine wine;

	private TestWineBuilder(String name) {
		this.wine = new Wine(name);
		setPrivateField(wine, "id", 1L);
	}

	public static TestWineBuilder builder() {
		return new TestWineBuilder("Test Wine");
	}

	public TestWineBuilder withId(Long id) {
		setPrivateField(wine, "id", id);
		return this;
	}

	public TestWineBuilder withName(String name) {
		this.wine.setName(name);
		return this;
	}

	public TestWineBuilder withWinery(String winery) {
		this.wine.setName(winery);
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

	public TestWineBuilder withAppellation(String appellation) {
		this.wine.setRegion(appellation);
		return this;
	}

	public TestWineBuilder withVintage(Year vintage) {
		this.wine.setVintage(vintage);
		return this;
	}

	public TestWineBuilder addStyle(WineStyle style) {
		this.wine.addStyle(style);
		return this;
	}

	public TestWineBuilder addGrape(Grape grape, BigDecimal percentage) {
		this.wine.addGrape(grape, percentage);
		return this;
	}

	public TestWineBuilder addCourse(Course course) {
		this.wine.addCourse(course);
		return this;
	}

	public Wine build() {
		return wine;
	}

}
