package emh.dd_site.event.dto;

import emh.dd_site.event.entity.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

	public EventResponse toEventResponse(Event entity) {
		if (entity == null) {
			return null;
		}
		return new EventResponse(entity.getId(), entity.getDate(), entity.getHost(), entity.getLocation());
	}

	public Event fromEventUpsertRequest(EventUpsertRequest request) {
		if (request == null) {
			return null;
		}
		var event = new Event(request.date(), request.host());
		event.setLocation(request.location());
		return event;
	}

	public Event mergeWithEventUpsertRequest(Event event, EventUpsertRequest request) {
		if (event == null) {
			return null;
		}
		if (request == null) {
			return event;
		}
		event.setDate(request.date());
		event.setHost(request.host());
		event.setLocation(request.location());
		return event;
	}

}
