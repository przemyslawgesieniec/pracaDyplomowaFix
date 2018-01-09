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
    private HashMap<Integer, String> ordinalSpecifier;
    private ArrayList<DeviceAction> deviceActionOnList;
    private ArrayList<DeviceAction> deviceActionOffList;


    public MultiSwitch(ArrayList<Boolean> switchStatusList, String name, String location, InetAddress deviceAddress, String macAddress) {
        super(name, location, deviceAddress, macAddress);
        this.switchStatusList = new ArrayList<>(switchStatusList);
        Log.d("switchStatusList: ", "" + switchStatusList.size());
        Log.d("thi.switchStatusList: ", this.switchStatusList.size() + "");

        triggeringONCommandsENG = new ArrayList<>();
        triggeringOFFCommandsENG = new ArrayList<>();
        actionMapENG = new HashMap<>();
        deviceActionOnList = new ArrayList<>();
        deviceActionOffList = new ArrayList<>();
        ordinalSpecifier = new HashMap<>();
        fillDeviceActions();
        fillOrdinalSpecifier();
        fillActionMap();

        actionList = new ArrayList<>(actionMapENG.keySet());
        Log.d("MultiSwitch al size: ", "" + actionList.size());
        for (String action : actionList) {
            Log.d("MultiSwitch action: ", action);
        }


    }

    private void fillDeviceActions() {
        deviceActionOnList.add(DeviceAction.ON_FIRST);
        deviceActionOnList.add(DeviceAction.ON_SECOND);

        deviceActionOffList.add(DeviceAction.OFF_FIRST);
        deviceActionOffList.add(DeviceAction.OFF_SECOND);
    }


    private void fillActionMap() {

        for (int i = 0; i < switchStatusList.size(); i++) {

            /**
             * English Commands ON
             */
            for(DeviceAction action: deviceActionOnList){
                actionMapENG.put("turn on " + ordinalSpecifier.get(i), action);
                actionMapENG.put("switch on " + ordinalSpecifier.get(i), action);
                actionMapENG.put("illuminate " + ordinalSpecifier.get(i), action);
            }

            /**
             * English Commands OFF
             */
            for(DeviceAction action: deviceActionOffList) {
                actionMapENG.put("turn off " + ordinalSpecifier.get(i), action);
                actionMapENG.put("switch off " + ordinalSpecifier.get(i), action);
            }
        }

    }

    private void fillOrdinalSpecifier() {
        ordinalSpecifier.put(0, "first");
        ordinalSpecifier.put(1, "second");
        ordinalSpecifier.put(2, "third");
        ordinalSpecifier.put(3, "fourth");
        ordinalSpecifier.put(4, "fifth");
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
                switchStatusList.set(0, true);
                break;
            case OFF_FIRST:
                switchStatusList.set(0, false);
                break;
            case ON_SECOND:
                switchStatusList.set(1, true);
                break;
            case OFF_SECOND:
                switchStatusList.set(1, false);
                break;
        }
        return toString() + ";" + booleanToString(switchStatusList.get(0)) + ";" + booleanToString(switchStatusList.get(1));
    }

    private String getMessageBasedOnCurrentState() {
        if (isUpdated) {
            return toString() + ";" + booleanToString(switchStatusList.get(0)) + ";" + booleanToString(switchStatusList.get(1));
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
