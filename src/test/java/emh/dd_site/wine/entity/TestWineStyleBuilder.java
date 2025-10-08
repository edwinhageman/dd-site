package emh.dd_site.wine.entity;

import static emh.dd_site.RecursionUtils.setPrivateField;

public class TestWineStyleBuilder {

	private final WineStyle wineStyle;

	private TestWineStyleBuilder(String name) {
		this.wineStyle = new WineStyle(name);
		setPrivateField(wineStyle, "id", 1);
	}

	public static TestWineStyleBuilder builder() {
		return new TestWineStyleBuilder("Test Style");
	}

	public TestWineStyleBuilder withId(Integer id) {
		setPrivateField(wineStyle, "id", id);
		return this;
	}

	public TestWineStyleBuilder withName(String name) {
		this.wineStyle.setName(name);
		return this;
	}

	public WineStyle build() {
		return wineStyle;
	}

}
