package com.nwachukwufavour.dronetransport.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.Date;

@Data
public class ErrorResponse {

    private int code;

    private String status;

    private String message;

    private String path;

    private HttpMethod method;

    private Object data;

    // customizing timestamp serialization format
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;

    public ErrorResponse() {
        timestamp = new Date();
    }

    public ErrorResponse(int code, String status, String message, String path, HttpMethod method) {
        this();

        this.code = code;
        this.status = status;
        this.message = message;
        this.path = path;
        this.method = method;
    }

    public ErrorResponse(int code, String status, String message, String path, HttpMethod method, Object data) {
        this();

        this.code = code;
        this.status = status;
        this.message = message;
        this.path = path;
        this.method = method;
        this.data = data;
    }

    public ErrorResponse(Object data) {
        this();
        this.data = data;
    }
}