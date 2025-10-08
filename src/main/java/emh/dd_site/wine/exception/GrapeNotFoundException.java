package emh.dd_site.wine.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class GrapeNotFoundException extends ResponseStatusException {

	public GrapeNotFoundException(int id) {
		super(HttpStatusCode.valueOf(404), String.format("Could not find grape %d", id));
	}

	public GrapeNotFoundException(String message) {
		super(HttpStatusCode.valueOf(404), message);
	}

}
