package emh.dd_site.event.entity;

import java.time.LocalDate;

import static emh.dd_site.RecursionUtils.setPrivateField;

public class TestEventBuilder {

	private final Event event;

	private TestEventBuilder(LocalDate date, String host) {
		this.event = new Event(date, host);
		setPrivateField(event, "id", 1L);
	}

	public static TestEventBuilder anEvent() {
		return new TestEventBuilder(LocalDate.now(), "Test Host");
	}

	public TestEventBuilder withId(Long id) {
		setPrivateField(event, "id", id);
		return this;
	}

	public TestEventBuilder withDate(LocalDate date) {
		event.setDate(date);
		return this;
	}

	public TestEventBuilder withHost(String host) {
		event.setHost(host);
		return this;
	}

	public TestEventBuilder withLocation(String location) {
		event.setLocation(location);
		return this;
	}

	public Event build() {
		return event;
	}

}
