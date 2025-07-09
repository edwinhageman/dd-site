package emh.dd_site.event.entity;

import static emh.dd_site.RecursionUtils.setPrivateField;

public class TestCourseBuilder {
    private final Course course;

    private TestCourseBuilder(Event event, Integer courseNo, String cook, Dish dish, Wine wine) {
        this.course = new Course(event, courseNo, cook, dish, wine);
        setPrivateField(course, "id", 1L);
    }

    static TestCourseBuilder aCourse(Event event, Dish dish, Wine wine) {
        return new TestCourseBuilder(event, 1, "Test Cook", dish, wine);
    }

    TestCourseBuilder withId(Long id) {
        setPrivateField(course, "id", id);
        return this;
    }

    TestCourseBuilder withEvent(Event event) {
        course.setEvent(event);
        return this;
    }

    TestCourseBuilder withCourseNo(Integer courseNo) {
        course.setCourseNo(courseNo);
        return this;
    }

    TestCourseBuilder withCook(String cook) {
        course.setCook(cook);
        return this;
    }

    TestCourseBuilder withDish(Dish dish) {
        course.setDish(dish);
        return this;
    }

    TestCourseBuilder withWine(Wine wine) {
        course.setWine(wine);
        return this;
    }

    Course build() {
        return course;
    }
}
