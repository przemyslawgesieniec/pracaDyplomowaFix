package com.gesieniec.przemyslaw.aviotsystem.iothandler;

import android.util.Log;

import com.gesieniec.przemyslaw.aviotsystem.systemhandler.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przem on 15.12.2017.
 */

public class DeviceCapabilities {

    private static int id = 0;

    public DeviceCapabilities(String capabilities) {
        /**
         * caps(0) - ipAddress
         * caps(1) - device name
         * caps(2) - messageType
         * caps(3) - macAddress
         * caps(4) - number of switches
         * caps(5-n) - switch state
         * Extentions:
         */
        ordinalModifier = new ArrayList<>();

        String[] caps = capabilities.split(";");
        try {
            ipAddress = caps[0];
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            deviceTypeString = caps[1];
            deviceType = establishDeviceType(caps[1]);

        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            messageType = caps[2];
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            deviceName = caps[3];
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            deviceLocation = caps[4];
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            macAddress = caps[5];
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            numberOfSwitches = Integer.valueOf(caps[6]);
        } catch (ArrayIndexOutOfBoundsException e) {
        } catch (Exception e) {
        }

        states = new ArrayList<>();
        for (int i = 0; i < numberOfSwitches; i++) {
            try {
                states.add(Boolean.parseBoolean(caps[7 + i]));
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }

        if(deviceType.equals(DeviceType.MULTI_SWITCH)){
            ordinalModifier.add("first");
            ordinalModifier.add("second");
            ordinalModifier.add("third");
        }
        else {
            ordinalModifier.add("");
            ordinalModifier.add("");
            ordinalModifier.add("");
        }


//        Log.d("ipAddress;", ipAddress);
//        Log.d("deviceType;", deviceType.toString());
//        Log.d("messageType;", messageType);
//        Log.d("deviceName;", deviceName);
//        Log.d("deviceLocation;", deviceLocation);
//        Log.d("macAddress;", macAddress);


    }

    private DeviceType establishDeviceType(String type) {
        switch (type) {
            case "switch":
                return DeviceType.SWITCH;
            case "multiswitch":
                return DeviceType.MULTI_SWITCH;
            default:
                return DeviceType.NONE;
        }
    }

    private String ipAddress;
    private DeviceType deviceType;



    private String deviceTypeString;
    private String messageType;
    private String deviceName;
    private String deviceLocation;
    private String macAddress;
    private List<Boolean> states;
    private int numberOfSwitches;
    private List<String> ordinalModifier;


    private int ID;


    /**
     * getters
     */
    public int getID() {
        return ID;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public DeviceType getDeviceType() {
        return deviceType;
    }
    public String getMessageType() {
        return messageType;
    }
    public String getDeviceName() {
        return deviceName;
    }
    public String getDeviceLocation() {
        return deviceLocation;
    }
    public String getMacAddress() {
        return macAddress;
    }
    public List<Boolean> getStates() {
        return states;
    }
    public int getNumberOfSwitches() {
        return numberOfSwitches;
    }
    public String getDeviceTypeString() {
        return deviceTypeString;
    }
    public List<String> getOrdinalModifier() {
        return ordinalModifier;
    }

    /**
     * setters
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public void setDeviceLocation(String deviceLocation) {
        this.deviceLocation = deviceLocation;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public void setStates(List<Boolean> states) {
        this.states = states;
    }
    public int getIdBasedOnMAC(String mac) {
        return ApplicationContext.macIdMap.get(mac);
    }
    public void setDeviceTypeString(String deviceTypeString) {
        this.deviceTypeString = deviceTypeString;
    }
}
