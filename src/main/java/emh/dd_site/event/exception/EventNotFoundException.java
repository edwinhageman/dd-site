package emh.dd_site.event.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class EventNotFoundException extends ResponseStatusException {

	public EventNotFoundException(long id) {
		super(HttpStatusCode.valueOf(404), String.format("Could not find event %d", id));
	}

	public EventNotFoundException(String message) {
		super(HttpStatusCode.valueOf(404), message);
	}

}
