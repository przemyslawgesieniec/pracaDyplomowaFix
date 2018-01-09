package com.gesieniec.przemyslaw.aviotsystem.taskdispatcher;

import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceCapabilities;
import com.gesieniec.przemyslaw.aviotsystem.iothandler.devices.CommonDevice;
import com.gesieniec.przemyslaw.aviotsystem.systemhandler.SystemCommandHandler;
import com.gesieniec.przemyslaw.aviotsystem.voicehandler.VoiceCommandHandler;
import com.gesieniec.przemyslaw.aviotsystem.voicehandler.VoiceCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przem on 01.11.2017.
 * <p>
 * Purpose of this class is to outsource tasks to proper components
 * triggered by any other component.
 * <p>
 * eg. VoiceHandler after receiving a voiceCommand has several action to realize:
 * change status in GUI, send message to IoT device.
 * These two actions are delegating to GUI component, and Message Handler.
 */

public class TaskDispatcher {


//    public enum TaskContext{
//        GUI_COMMAND,
//        SEND_UDP_MESSAGE,
//        IOT_CONSISTENCY_REQUEST
//    }

    public enum VoiceTaskContext {
        EXECUTE_VOICE_COMMAND
    }

    public enum GuiTaskContext {
        SWITCH_STATE_CHANGED,
        UPDATE_DEVICE_DATA

    }

    public enum SystemTaskContext {
        EXECUTE_SYSTEM_COMMAND
    }

    public enum IoTTaskContext {
        ATTACH_REQUEST,
        ATTACH_COMPLETE,
        UPDATE_DEVICE_DATA,
        CONSISTENCY_MESSGE_RECEIVED,
        DEVICE_NOT_RESPONDING
    }

    private static ArrayList<ITaskDispatcherListener> listeners = new ArrayList<>();

    public static void addListener(ITaskDispatcherListener listener) {
        listeners.add(listener);
    }

    public static void newTask(VoiceTaskContext cause, VoiceCommand data) {
        switch (cause) {
            case EXECUTE_VOICE_COMMAND:
                List<String> voiceResults = data.getRawCommand();
                if (voiceResults != null) {
                    VoiceCommandHandler voiceCommandHandler = new VoiceCommandHandler(data);
                    voiceCommandHandler.interpreteCommand(voiceResults);
                    try {
                        for (ITaskDispatcherListener executeVoiceCommandListener : listeners) {
                            executeVoiceCommandListener.handleDispatchedVoiceCommandExecution(data);//->GUI Update //->Send UDP MSG
                        }
                    } catch (Exception e) {
                    }
                }
                break;
        }
    }

    public static void newTask(SystemTaskContext cause, SystemCommandHandler data) {
        switch (cause) {
            case EXECUTE_SYSTEM_COMMAND:
                if (data.getSystemCommandType() != SystemCommandHandler.SystemCommandType.NONE) {
                    for (ITaskDispatcherListener executeSystemCommandListener : listeners) {
                        executeSystemCommandListener.handleDispatchedSystemCommandExecution(data);
                    }
                }
                break;
        }
    }

    public static void newTask(IoTTaskContext cause, List<String> data) {
        switch (cause) {
            case ATTACH_REQUEST:
                for (ITaskDispatcherListener executeSystemCommandListener : listeners) {
                    executeSystemCommandListener.handleDispatchedIoTCommandExecution(data);
                }
                break;


        }
    }

    public static void newTask(IoTTaskContext cause, String data) {
        switch (cause) {
            case ATTACH_COMPLETE:
                for (ITaskDispatcherListener executeSystemCommandListener : listeners) {
                    executeSystemCommandListener.handleDispatchedIoTCommandExecution(data);
                }
                break;

        }
    }

    public static void newTask(IoTTaskContext cause, DeviceCapabilities data) {
        switch (cause) {
            case UPDATE_DEVICE_DATA:
                for (ITaskDispatcherListener executeSystemCommandListener : listeners) {
                    executeSystemCommandListener.handleDispatchedIoTUpdateCommandExecution(data);
                }
                break;
            case CONSISTENCY_MESSGE_RECEIVED:
                for (ITaskDispatcherListener executeSystemCommandListener : listeners) {
                    executeSystemCommandListener.handleDispatchedIoTConsistencyControl(data);
                }
        }
    }
    public static void newTask(IoTTaskContext cause, CommonDevice data) {
        switch (cause){
            case DEVICE_NOT_RESPONDING:
                for (ITaskDispatcherListener executeSystemCommandListener : listeners) {
                    executeSystemCommandListener.handleDispatchedIoTDeviceNotResponding(data);
                }
        }
    }

    //    }
    public static void newTask(GuiTaskContext cause, DeviceCapabilities capabilities) {
        switch (cause) {
            case SWITCH_STATE_CHANGED:
                for (ITaskDispatcherListener executeSystemCommandListener : listeners) {
                    executeSystemCommandListener.handleDispatchedGUICommandExecution(capabilities);
                }
                break;
            case UPDATE_DEVICE_DATA:
                capabilities.setMessageType("updatecapabilities");
                for (ITaskDispatcherListener executeSystemCommandListener : listeners) {
                    executeSystemCommandListener.handleDispatchedUpdateDeviceDataCommandExecution(capabilities);
                }
                break;
        }
    }
}
