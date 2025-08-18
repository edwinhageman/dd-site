package emh.dd_site.event.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CourseNotFoundException extends ResponseStatusException {

	public CourseNotFoundException(long id) {
		super(HttpStatusCode.valueOf(404), String.format("Could not find course %d", id));
	}

	public CourseNotFoundException(String message) {
		super(HttpStatusCode.valueOf(404), message);
	}

}
