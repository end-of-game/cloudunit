/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import fr.treeptik.cloudunit.json.ui.HttpErrorServer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class RestHandlerException
    extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ServiceException.class, JsonMappingException.class})
    protected ResponseEntity<Object> handleServiceException(Exception ex,
                                                            WebRequest request) {
        ex.printStackTrace();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(ex,
            new HttpErrorServer(ex.getLocalizedMessage()), headers,
            HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {CheckException.class})
    protected ResponseEntity<Object> handleCheckedeException(Exception ex,
                                                             WebRequest request) {
        ex.printStackTrace();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(ex,
            new HttpErrorServer(ex.getLocalizedMessage()), headers,
            HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    protected ResponseEntity<Object> handleNullPointerException(Exception ex,
                                                                WebRequest request) {
        ex.printStackTrace();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(
            ex,
            new HttpErrorServer(
                "An unkown error has occured! Server response : NullPointerException"),
            headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {OutOfMemoryError.class})
    protected ResponseEntity<Object> handleOutOfMemoryError(Exception ex,
                                                            WebRequest request) {
        ex.printStackTrace();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(
            ex,
            new HttpErrorServer(
                "An unkown error has occured! Server response : OutOfMemoryError"),
            headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {ClassCastException.class})
    protected ResponseEntity<Object> handleClassCastException(Exception ex,
                                                              WebRequest request) {
        ex.printStackTrace();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(
            ex,
            new HttpErrorServer(
                "An unkown error has occured! Server response : ClassCastException"),
            headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {IllegalStateException.class})
    protected ResponseEntity<Object> handleIllegalStateException(Exception ex,
                                                                 WebRequest request) {
        ex.printStackTrace();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(
            ex,
            new HttpErrorServer(
                "An unkown error has occured! Server response : IllegalStateException"),
            headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<Object> handleIllegalArgumentException(
        Exception ex, WebRequest request) {
        ex.printStackTrace();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(
            ex,
            new HttpErrorServer(
                "An unkown error has occured! Server response : IllegalArgumentException"),
            headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    protected ResponseEntity<Object> handleNoSuchElementException(Exception ex,
                                                                  WebRequest request) {
        ex.printStackTrace();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(
            ex,
            new HttpErrorServer(
                "An unkown error has occured! Server response :  NoSuchElementException"),
            headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {ArithmeticException.class})
    protected ResponseEntity<Object> handleArithmeticException(Exception ex,
                                                               WebRequest request) {
        ex.printStackTrace();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(
            ex,
            new HttpErrorServer(
                "An unkown error has occured! Server response :   ArithmeticException"),
            headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {IndexOutOfBoundsException.class})
    protected ResponseEntity<Object> handleIndexOutOfBoundsException(
        Exception ex, WebRequest request) {
        ex.printStackTrace();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(
            ex,
            new HttpErrorServer(
                "An unkown error has occured! Server response :   IndexOutOfBoundsException"),
            headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
