package emh.dd_site.wine.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class WineStyleNotFoundException extends ResponseStatusException {

	public WineStyleNotFoundException(int id) {
		super(HttpStatusCode.valueOf(404), String.format("Could not find wine style %d", id));
	}

	public WineStyleNotFoundException(String message) {
		super(HttpStatusCode.valueOf(404), message);
	}

}
