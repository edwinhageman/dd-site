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

    static TestWineBuilder aWine() {
        return new TestWineBuilder("Test Wine", WineType.WHITE, "Test Grape", "Test Country");
    }

    TestWineBuilder withId(Long id) {
        setPrivateField(wine, "id", id);
        return this;
    }

    TestWineBuilder withName(String name) {
        this.wine.setName(name);
        return this;
    }

    TestWineBuilder withType(WineType type) {
        this.wine.setType(type);
        return this;
    }

    TestWineBuilder withGrape(String grape) {
        this.wine.setGrape(grape);
        return this;
    }

    TestWineBuilder withCountry(String country) {
        this.wine.setCountry(country);
        return this;
    }

    TestWineBuilder withRegion(String region) {
        this.wine.setRegion(region);
        return this;
    }

    TestWineBuilder withYear(Year year) {
        this.wine.setYear(year);
        return this;
    }

    Wine build() {
        return wine;
    }
}
