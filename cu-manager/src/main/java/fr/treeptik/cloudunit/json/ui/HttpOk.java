package fr.treeptik.cloudunit.json.ui;

import org.apache.http.HttpStatus;

/**
 * Created by nicolas on 01/08/2014.
 */
public class HttpOk extends JsonResponse {

    public HttpOk() {
        super(HttpStatus.SC_OK, "", null);
    }

    public HttpOk(String location) {
        super(HttpStatus.SC_OK, "", location);
    }
}
