package fr.treeptik.cloudunit.config;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.json.ui.HttpErrorServer;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomResponseHandler {

	@ExceptionHandler({ CheckException.class, ServiceException.class,
			NoSuchMessageException.class, NumberFormatException.class,
			ClassCastException.class })
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody
	HttpErrorServer handleException(CheckException e) {
		return new HttpErrorServer(e.getMessage());
	}

}
