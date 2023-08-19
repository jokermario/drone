package com.nwachukwufavour.dronetransport.utility;

import com.nwachukwufavour.dronetransport.model.LogData;

public class LogUtil {

    public static String logData(String droneSerialNo, String droneState, int batteryLevel) {
        return new LogData().toBuilder()
                .droneSerialNo(droneSerialNo)
                .droneState(droneState)
                .batteryLevel(batteryLevel)
                .build().toString();
    }
}
