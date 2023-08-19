package com.nwachukwufavour.dronetransport.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

@Data
public class ResponseData {

    private String status;
    private String message;
    private Object additionalInfo;

    // customizing timestamp serialization format
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;

//    private String timestamp = new Date().toString();

    public ResponseData() {
        timestamp = new Date();
        additionalInfo = "no data";
    }

    public ResponseData(String status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    public ResponseData(String status, String message, Object additionalInfo) {
        this();
        this.status = status;
        this.message = message;
        this.additionalInfo = additionalInfo;
    }

    public String toMessageString() {
        var s = new HashMap<>();
        s.put("status", status);
        s.put("message", message);
        s.put("timestamp", new Date());
        return new JSONObject(s).toString();
    }

    public String toString() {
        var s = new HashMap<>();
        s.put("DroneSerialNo", status);
        s.put("DroneState", message);
        s.put("additionalInfo", additionalInfo);
        s.put("timestamp", new Date());
        return new JSONObject(s).toString();
    }
}
