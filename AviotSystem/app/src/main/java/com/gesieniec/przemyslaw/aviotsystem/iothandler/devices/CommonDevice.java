package com.gesieniec.przemyslaw.aviotsystem.iothandler.devices;

import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceAction;
import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceCapabilities;
import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceType;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

/**
 * Created by przem on 08.11.2017.
 */

public abstract class CommonDevice {


    private static int id = 0;
    /**
     * fields
     */
    protected String name;
    protected String location;
    protected InetAddress deviceAddress;
    protected String macAddress;
    protected HashMap<String,DeviceAction> actionMapENG;
    protected HashMap<String,DeviceAction> actionMapPL;
    protected List<String> actionList;
    protected boolean isDataUpdated = false;
    protected int deviceId;
    protected int deviceStatusCounter = 3;
    protected boolean isUpdated = false;

    /**
     * getters
     */
    public String getName() {
        return name;
    }
    public String getLocation() {
        return location;
    }
    public InetAddress getDeviceAddress() {
        return deviceAddress;
    }
    public String getMacAddress() {
        return macAddress;
    }
    public List<String> getActionList() {
        return actionList;
    }
    public int getDeviceStatusCounter() {
        return deviceStatusCounter;
    }
    public int getDeviceId() {
        return deviceId;
    }

    /**
     * setters
     */

    public void setName(String name) {
        this.name = name;
    }
    public void setLocation(String location) {
        this.location = location;
    }


    public CommonDevice(String name, String location,InetAddress deviceAddress,String macAddress) {
        this.name = name;
        this.location = location;
        this.deviceAddress = deviceAddress;
        this.macAddress = macAddress;
        deviceId = id;
        id+=100;

    }
    public void restoreDeviceStatusCounter(){
        deviceStatusCounter = 3;
    }
    public void decreseDeviceStatusCounter(){
        deviceStatusCounter --;
    }

    public abstract DeviceType getDeviceType();
    public abstract HashMap<String, DeviceAction> getActionMapENG();
    public abstract String getMessageToSend(DeviceCapabilities capabilities);
    public abstract void updateDeviceWithCapabilities(DeviceCapabilities deviceCapabilities);
    public abstract String getMessageBasedOnAction(DeviceAction action);

}
