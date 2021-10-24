package com.github.bytemania.adapter.in.web.server;

import com.github.bytemania.adapter.in.web.server.dto.ResponseError;
import com.github.bytemania.adapter.in.web.server.impl.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static String getFullURL(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    @ExceptionHandler({ValidationException.class})
    public final ResponseEntity<ResponseError> handleValidationError(ValidationException e,
                                                                     HttpServletRequest httpRequest) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", e, httpRequest);
    }

    @ExceptionHandler({IllegalStateException.class})
    public final ResponseEntity<ResponseError> handleIllegalStateException(IllegalStateException e,
                                                                           HttpServletRequest httpRequest) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service Error", e, httpRequest);
    }

    @ExceptionHandler({Throwable.class})
    public final ResponseEntity<ResponseError> handleThrowable(Throwable e,
                                                               HttpServletRequest httpRequest) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", e, httpRequest);
    }

    private ResponseEntity<ResponseError> buildResponse(HttpStatus httpStatus,
                                                        String error,
                                                        Throwable e,
                                                        HttpServletRequest httpRequest) {

        log.warn("Error processing the Servlet url: " + getFullURL(httpRequest), e);

        ResponseError responseError = ResponseError
                .builder()
                .status(httpStatus)
                .error(error)
                .message(e.getMessage())
                .path(httpRequest.getPathInfo())
                .build();
        return new ResponseEntity<>(responseError, responseError.getStatus());
    }
}
