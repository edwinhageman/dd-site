package emh.dd_site.event.entity;

import java.time.LocalDate;

import static emh.dd_site.RecursionUtils.setPrivateField;

public class TestEventBuilder {

	private final Event event;

	private TestEventBuilder(LocalDate date, String host) {
		this.event = new Event(date, host);
		setPrivateField(event, "id", 1L);
	}

	static TestEventBuilder anEvent() {
		return new TestEventBuilder(LocalDate.now(), "Test Host");
	}

	TestEventBuilder withId(Long id) {
		setPrivateField(event, "id", id);
		return this;
	}

	TestEventBuilder withDate(LocalDate date) {
		event.setDate(date);
		return this;
	}

	TestEventBuilder withHost(String host) {
		event.setHost(host);
		return this;
	}

	TestEventBuilder withLocation(String location) {
		event.setLocation(location);
		return this;
	}

	Event build() {
		return event;
	}

}
