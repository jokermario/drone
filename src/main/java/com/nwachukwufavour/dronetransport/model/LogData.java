package com.nwachukwufavour.dronetransport.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LogData {
    private String droneSerialNo;
    private String droneState;
    private int batteryLevel;
    private String timestamp = new Date().toString();

    public String toString() {
        var s = new HashMap<>();
        s.put("DroneSerialNo", droneSerialNo);
        s.put("DroneState", droneState);
        s.put("BatteryLevel", batteryLevel);
        s.put("timestamp", timestamp);
        return new JSONObject(s).toString();
    }
}
