package fr.treeptik.cloudunit.json.ui;

import org.apache.http.HttpStatus;

public class HttpErrorServer extends JsonResponse {

	private static final long serialVersionUID = 1L;

	public HttpErrorServer(String message) {
		super(HttpStatus.SC_INTERNAL_SERVER_ERROR, message, "");
	}
}
