package emh.dd_site.event.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class WineNotFoundException extends ResponseStatusException {

	public WineNotFoundException(long id) {
		super(HttpStatusCode.valueOf(404), String.format("Could not find wine %d", id));
	}

	public WineNotFoundException(String message) {
		super(HttpStatusCode.valueOf(404), message);
	}

}
