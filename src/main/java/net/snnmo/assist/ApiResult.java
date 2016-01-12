package net.snnmo.assist;

import org.springframework.http.HttpStatus;

/**
 * Created by TTong on 16-1-12.
 */
public class ApiResult {

    private String message;
    private HttpStatus status = HttpStatus.OK;

    public ApiResult() {};

    public ApiResult(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
