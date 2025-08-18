package emh.dd_site.event.entity;

import static emh.dd_site.RecursionUtils.setPrivateField;

public class TestDishBuilder {

	private final Dish dish;

	private TestDishBuilder(String name) {
		this.dish = new Dish(name);
		setPrivateField(dish, "id", 1L);
	}

	public static TestDishBuilder aDish() {
		return new TestDishBuilder("Test Dish");
	}

	public TestDishBuilder withId(Long id) {
		setPrivateField(this.dish, "id", id);
		return this;
	}

	public TestDishBuilder withName(String name) {
		this.dish.setName(name);
		return this;
	}

	public TestDishBuilder withMainIngredient(String mainIngredient) {
		this.dish.setMainIngredient(mainIngredient);
		return this;
	}

	public TestDishBuilder withCourse(Course course) {
		this.dish.setCourse(course);
		return this;
	}

	public Dish build() {
		return dish;
	}

}
