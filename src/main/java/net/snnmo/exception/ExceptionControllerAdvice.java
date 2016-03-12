package net.snnmo.exception;

import net.snnmo.assist.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by TTong on 16-1-13.
 */
@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult> exception(Exception e) {

        ApiResult result = new ApiResult();

        String message = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();

        result.serError(true);
        result.setMessage(message);
        result.setStatus(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<ApiResult>(result, result.getStatus());
    }
}
