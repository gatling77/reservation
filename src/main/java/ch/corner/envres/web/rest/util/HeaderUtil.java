package ch.corner.envres.web.rest.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for http header creation.
 *
 */
public class HeaderUtil {

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-environmentreservationApp-alert", message);
        headers.add("X-environmentreservationApp-params", param);
        return headers;
    }

    public static HttpHeaders createWarning(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-environmentreservationApp-warning", message);
        headers.add("X-environmentreservationApp-params", param);
        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("A new " + entityName + " is created with identifier " + param, param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("A " + entityName + " is updated with identifier " + param, param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("A " + entityName + " is deleted with identifier " + param, param);
    }

    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-environmentreservationApp-error", defaultMessage);
        headers.add("X-environmentreservationApp-params", entityName);
        return headers;
    }
}
