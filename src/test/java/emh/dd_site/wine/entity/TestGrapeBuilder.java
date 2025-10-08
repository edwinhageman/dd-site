package emh.dd_site.wine.entity;

import static emh.dd_site.RecursionUtils.setPrivateField;

public class TestGrapeBuilder {

	private final Grape grape;

	private TestGrapeBuilder(String name) {
		this.grape = new Grape(name);
		setPrivateField(grape, "id", 1);
	}

	public static TestGrapeBuilder builder() {
		return new TestGrapeBuilder("Test Grape");
	}

	public TestGrapeBuilder withId(Integer id) {
		setPrivateField(grape, "id", id);
		return this;
	}

	public TestGrapeBuilder withName(String name) {
		this.grape.setName(name);
		return this;
	}

	public Grape build() {
		return grape;
	}

}
