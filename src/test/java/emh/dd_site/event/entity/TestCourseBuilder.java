package emh.dd_site.event.entity;

import emh.dd_site.event.WineType;

import java.time.LocalDate;

import static emh.dd_site.RecursionUtils.setPrivateField;

public class TestCourseBuilder {

	private final Course course;

	private TestCourseBuilder(Event event, Integer courseNo, String cook, Dish dish, Wine wine) {
		this.course = new Course(event, courseNo, cook);
		this.course.setDish(dish);
		this.course.setWine(wine);
		setPrivateField(course, "id", 1L);
	}

	public static TestCourseBuilder aCourse() {
		return new TestCourseBuilder(new Event(LocalDate.of(2025, 1, 1), "Host name"), 1, "Test Cook",
				new Dish("Dish name"), new Wine("Wine name", WineType.RED, "Merlot", "France"));
	}

	public TestCourseBuilder withId(Long id) {
		setPrivateField(course, "id", id);
		return this;
	}

	public TestCourseBuilder withEvent(Event event) {
		course.setEvent(event);
		return this;
	}

	public TestCourseBuilder withCourseNo(Integer courseNo) {
		course.setCourseNo(courseNo);
		return this;
	}

	public TestCourseBuilder withCook(String cook) {
		course.setCook(cook);
		return this;
	}

	public TestCourseBuilder withDish(Dish dish) {
		course.setDish(dish);
		return this;
	}

	public TestCourseBuilder withWine(Wine wine) {
		course.setWine(wine);
		return this;
	}

	public Course build() {
		return course;
	}

}
