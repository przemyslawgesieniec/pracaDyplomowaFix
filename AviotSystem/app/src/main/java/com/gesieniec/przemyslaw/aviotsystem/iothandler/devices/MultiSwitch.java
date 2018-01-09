package com.gesieniec.przemyslaw.aviotsystem.iothandler.devices;

import android.util.Log;

import java.util.ArrayList;

import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceAction;
import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceCapabilities;
import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceType;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by przem on 09.01.2018.
 */

public class MultiSwitch extends CommonDevice {

    private ArrayList<Boolean> switchStatusList;
    private ArrayList<String> triggeringONCommandsENG;
    private ArrayList<String> triggeringOFFCommandsENG;


    public MultiSwitch(ArrayList<Boolean> switchStatusList, String name, String location, InetAddress deviceAddress, String macAddress) {
        super(name, location, deviceAddress, macAddress);
        switchStatusList = new ArrayList<>(switchStatusList);
        triggeringONCommandsENG = new ArrayList<>();
        triggeringOFFCommandsENG = new ArrayList<>();
        fillTriggeringCommandsList();

    }

    protected void fillTriggeringCommandsList() {

//        for()
        /**
         * English Commands ON
         */
        triggeringONCommandsENG.add("turn on");
        triggeringONCommandsENG.add("switch on");
        triggeringONCommandsENG.add("illuminate");
        /**
         * English Commands OFF
         */
        triggeringOFFCommandsENG.add("switch off");
        triggeringOFFCommandsENG.add("turn off");
    }

    @Override
    public String toString() {
        return "MultiSwitch";
    }

    @Override
    public DeviceType getDeviceType() {
        return null;
    }

    @Override
    public HashMap<String, DeviceAction> getActionMapENG() {
        return null;
    }


    @Override
    public String getMessageToSend(DeviceCapabilities capabilities) {
        return getMessageBasedOnCurrentState();

    }

    @Override
    public void updateDeviceWithCapabilities(DeviceCapabilities deviceCapabilities) {
        Log.d("MultiSwitch", "updateDeviceWithCapabilities");
        isUpdated = false;
        isDataUpdated = false;
        for (int i = 0; i < switchStatusList.size(); i++) {
            if (switchStatusList.get(i) != deviceCapabilities.getStates().get(i)) {
                switchStatusList.set(i, deviceCapabilities.getStates().get(i));
                isUpdated = true;
            }
        }
        if (!name.equals(deviceCapabilities.getDeviceName())) {
            name = deviceCapabilities.getDeviceName();
            isDataUpdated = true;
        }
        if (!location.equals(deviceCapabilities.getDeviceLocation())) {
            location = deviceCapabilities.getDeviceLocation();
            isDataUpdated = true;
        }
    }

    @Override
    public String getMessageBasedOnAction(DeviceAction action) {
        switch (action) {
            case ON_FIRST:
                switchStatusList.set(0,true);
                break;
            case OFF_FIRST:
                switchStatusList.set(0,false);
                break;
            case ON_SECOND:
                switchStatusList.set(1,true);
                break;
            case OFF_SECOND:
                switchStatusList.set(1,false);
                break;
        }
        return toString() + ";" + booleanToString(switchStatusList.get(0)) + ";" + booleanToString(switchStatusList.get(1));
    }

    private String getMessageBasedOnCurrentState() {
        if (isUpdated) {
            for (int i = 0; i < switchStatusList.size(); i++) {

            }
        }
        if (isDataUpdated) {
            Log.d("isDataUpdated", "" + isDataUpdated);
            String action = "UpdateDeviceData;";
            action += name;
            action += ";";
            action += location;
            action += ";";
            return action;
        }
        isDataUpdated = false;
        return null;
    }

    private String booleanToString(boolean state) {
        return state ? "true" : "false";
    }
}
