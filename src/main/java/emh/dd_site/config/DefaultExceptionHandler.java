package emh.dd_site.config;

import jakarta.annotation.Nonnull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(@Nonnull MethodArgumentNotValidException ex,
			@Nonnull HttpHeaders headers, @Nonnull HttpStatusCode status, @Nonnull WebRequest request) {
		var fieldErrors = ex.getFieldErrors()
			.stream()
			.collect(Collectors.groupingBy(FieldError::getField,
					Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
		var globalErrors = ex.getGlobalErrors()
			.stream()
			.filter(d -> d.getDefaultMessage() != null)
			.collect(Collectors.toMap(ObjectError::getObjectName, DefaultMessageSourceResolvable::getDefaultMessage));

		var body = ProblemDetail.forStatusAndDetail(status, "Invalid request content.");
		if (!fieldErrors.isEmpty()) {
			body.setProperty("fieldErrors", fieldErrors);
		}
		if (!globalErrors.isEmpty()) {
			body.setProperty("errors", globalErrors);
		}

		return handleExceptionInternal(ex, body, headers, status, request);
	}

}
