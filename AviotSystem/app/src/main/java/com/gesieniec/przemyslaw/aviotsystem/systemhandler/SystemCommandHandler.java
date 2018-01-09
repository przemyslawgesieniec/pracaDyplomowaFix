package com.gesieniec.przemyslaw.aviotsystem.systemhandler;

import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceCapabilities;
import com.gesieniec.przemyslaw.aviotsystem.iothandler.devices.CommonDevice;
import com.gesieniec.przemyslaw.aviotsystem.taskdispatcher.ITaskDispatcherListener;
import com.gesieniec.przemyslaw.aviotsystem.taskdispatcher.TaskDispatcher;
import com.gesieniec.przemyslaw.aviotsystem.voicehandler.VoiceCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przem on 14.11.2017.
 */

public class SystemCommandHandler implements ITaskDispatcherListener {

    private String systemAnswer;
    private SystemCommandType systemCommandType = SystemCommandType.NONE;
    public enum SystemCommandType{
        REJECT,
        ACCEPT,
        WARNING,
        NONE
    }


    public SystemCommandHandler() {
        TaskDispatcher.addListener(this);
    }

    /**
     * getters
     */
    public String getSystemAnswer() {
        return systemAnswer;
    }
    public SystemCommandType getSystemCommandType() {
        return systemCommandType;
    }

    @Override
    public void handleDispatchedVoiceCommandExecution(VoiceCommand voiceCommand) {
        if(voiceCommand.getVoiceCommandType() == VoiceCommand.VoiceCommandType.SYSTEM_RELATED){
            executeSystemVoiceCommand(voiceCommand.getBestMatchCommand());
        }
    }

    private void executeSystemVoiceCommand(String command){

        for(String cmnd : CommandDataClass.getSystemStatusCommandsENG()) {
            if (cmnd.equals(command)) {
                List<String> connectedDevices = checkConnectedDevices();
                systemCommandType = SystemCommandType.ACCEPT;
                systemAnswer = "You have " + connectedDevices.size() + " connected devices: " + connectedDevices.toString();
                TaskDispatcher.newTask(TaskDispatcher.SystemTaskContext.EXECUTE_SYSTEM_COMMAND, this);
                return;
            }
        }
        for(String cmnd : CommandDataClass.getSystemStatusCommandsPL()){
            if(cmnd.equals(command)){
                List<String> connectedDevices = checkConnectedDevices();
                systemCommandType = SystemCommandType.ACCEPT;
                systemAnswer = "Masz "+connectedDevices.size()+" połączonych urzadzeń: "+connectedDevices.toString();
                TaskDispatcher.newTask(TaskDispatcher.SystemTaskContext.EXECUTE_SYSTEM_COMMAND, this);
                return;
            }
        }
        for (String cmnd : CommandDataClass.getConsoleCrearingCommandsENG()) {
            if (cmnd.equals(command)) {
                systemCommandType = SystemCommandType.ACCEPT;
                systemAnswer = "Console cleared";
                TaskDispatcher.newTask(TaskDispatcher.SystemTaskContext.EXECUTE_SYSTEM_COMMAND, this);
                return;
            }
        }
        systemCommandType = SystemCommandType.REJECT;
    }

    private List<String> checkConnectedDevices(){
        List<CommonDevice> commonDevices = ApplicationContext.getCommonDevices();
        List<String> commonDevicesName = new ArrayList<>();
        for(CommonDevice device : commonDevices){
            commonDevicesName.add(device.getName());
        }
        return commonDevicesName;
    }
    /**
     * DO NOT IMPLEMENT !
     */
    @Override
    public void handleDispatchedSystemCommandExecution(SystemCommandHandler systemCommandHandler) {

    }

    @Override
    public void handleDispatchedIoTCommandExecution(List<String> data) {}

    @Override
    public void handleDispatchedIoTCommandExecution(String capabilities) {

    }

    @Override
    public void handleDispatchedIoTUpdateCommandExecution(DeviceCapabilities capabilities) {

    }

    @Override
    public void handleDispatchedGUICommandExecution(DeviceCapabilities capabilities) {

    }

    @Override
    public void handleDispatchedUpdateDeviceDataCommandExecution(DeviceCapabilities capabilities) {

    }

    @Override
    public void handleDispatchedIoTConsistencyControl(DeviceCapabilities capabilities) {

    }

    @Override
    public void handleDispatchedIoTDeviceNotResponding(CommonDevice data) {

    }

}
