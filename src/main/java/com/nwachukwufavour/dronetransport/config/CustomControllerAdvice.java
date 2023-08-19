package com.nwachukwufavour.dronetransport.config;

import com.nwachukwufavour.dronetransport.exception.DroneException;
import com.nwachukwufavour.dronetransport.exception.MedicationException;
import com.nwachukwufavour.dronetransport.exception.ResourceNotFound;
import com.nwachukwufavour.dronetransport.model.ErrorResponse;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@RequiredArgsConstructor
@Configuration
public class CustomControllerAdvice {

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(Exception ex, ServerHttpRequest request) {

        var code = HttpStatus.BAD_REQUEST.value();
        var method = request.getMethod();
        var message = ex.getMessage();
        var path = request.getPath().value();
        String status = HttpStatus.BAD_REQUEST.name(); // 404

        return new ResponseEntity<>(new ErrorResponse(code, status, message, path, method), HttpStatus.valueOf(status));
    }

    @ExceptionHandler(MedicationException.class)
    public ResponseEntity<ErrorResponse> handleMedicationException(Exception ex, ServerHttpRequest request) {

        var code = HttpStatus.BAD_REQUEST.value();
        var method = request.getMethod();
        var message = ex.getMessage();
        var path = request.getPath().value();
        String status = HttpStatus.BAD_REQUEST.name(); // 400

        return new ResponseEntity<>(new ErrorResponse(code, status, message, path, method), HttpStatus.valueOf(status));
    }

    @ExceptionHandler({DroneException.class})
    public ResponseEntity<ErrorResponse> handleDroneException(Exception ex, ServerHttpRequest request) {

        var code = HttpStatus.BAD_REQUEST.value();
        var method = request.getMethod();
        var message = ex.getMessage();
        var path = request.getPath().value();
        String status = HttpStatus.BAD_REQUEST.name();// 400

        return new ResponseEntity<>(new ErrorResponse(code, status, message, path, method), HttpStatus.valueOf(status));
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(Exception ex, ServerHttpRequest request) {

        var code = HttpStatus.NOT_FOUND.value();
        var method = request.getMethod();
        var message = ex.getMessage();
        var path = request.getPath().value();
        String status = HttpStatus.NOT_FOUND.name(); // 400

        return new ResponseEntity<>(new ErrorResponse(code, status, message, path, method), HttpStatus.valueOf(status));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(Exception ex, ServerHttpRequest request) {

        var code = HttpStatus.BAD_REQUEST.value();
        var method = request.getMethod();
        var message = ex.getMessage();
        var path = request.getPath().value();
        String status = HttpStatus.BAD_REQUEST.name(); // 400

        return new ResponseEntity<>(new ErrorResponse(code, status, message, path, method), HttpStatus.valueOf(status));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleWebExchangeBindException(Exception ex, ServerHttpRequest request) {

        var code = HttpStatus.BAD_REQUEST.value();
        String status = HttpStatus.BAD_REQUEST.name();
        var message = Objects.requireNonNull(((WebExchangeBindException) ex).getFieldError()).getDefaultMessage();// 400
        var path = request.getPath().value();
        var method = request.getMethod();

        Map<String, Object> data = new HashMap<>();
        data.put("field", ((WebExchangeBindException) ex).getFieldError().getField());
        data.put("rejVal", ((WebExchangeBindException) ex).getFieldError().getRejectedValue());

        return new ResponseEntity<>(new ErrorResponse(code, status, message, path, method, data),
                HttpStatus.valueOf(status));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, ServerHttpRequest request) {

        var code = HttpStatus.BAD_REQUEST.value();
        String status = HttpStatus.BAD_REQUEST.name();
        var message = "An error has occurred";
        var path = request.getPath().value();
        var method = request.getMethod();

        Map<String, Object> data = new HashMap<>();
        data.put("field", ex.getMessage());
        data.put("rejVal", ex.getClass().getName());

        return new ResponseEntity<>(new ErrorResponse(code, status, message, path, method, data),
                HttpStatus.valueOf(status));
    }

    @ExceptionHandler({DataIntegrityViolationException.class, R2dbcDataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> handleDbRelatedExceptions(Exception ex, ServerHttpRequest request) {

        var code = HttpStatus.BAD_REQUEST.value();
        String status = HttpStatus.BAD_REQUEST.name();
        var message = "Unique index or primary key violation occurred in the DB. Make sure you do not have the same " +
                "value for a key in 2 or more objects. ";
        var path = request.getPath().value();
        var method = request.getMethod();

        Map<String, Object> data = new HashMap<>();
        data.put("field", ex.getMessage());
        data.put("rejVal", ex.getClass().getName());

        return new ResponseEntity<>(new ErrorResponse(code, status, message, path, method, data),
                HttpStatus.valueOf(status));

    }
}