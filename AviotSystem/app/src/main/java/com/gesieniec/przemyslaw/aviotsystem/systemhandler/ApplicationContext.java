package com.gesieniec.przemyslaw.aviotsystem.systemhandler;

import android.os.AsyncTask;
import android.os.Build;

import com.gesieniec.przemyslaw.aviotsystem.MainActivity;
import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceHandler;
import com.gesieniec.przemyslaw.aviotsystem.iothandler.devices.CommonDevice;
import com.gesieniec.przemyslaw.aviotsystem.iothandler.messagehandler.BroadcastListener;
import com.gesieniec.przemyslaw.aviotsystem.voicehandler.VoiceRecognition;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by przem on 08.11.2017.
 */

public final class ApplicationContext {



    /**
     * fields
     */
    public enum Language{
        PL_POLISH,
        ENG_ENGLISH
    }

    private Language systemLanguage = Language.ENG_ENGLISH;

    private DeviceHandler deviceHandler;
    private VoiceRecognition voiceRecognition;
    private static ArrayList<CommonDevice> commonDevices;
    private static ArrayList<CommonDevice> disconnectedCommonDevices;
    private SystemCommandHandler systemCommandHandler;
    public static HashMap<String,Integer> macIdMap;


    public static ArrayList<CommonDevice> getDisconnectedCommonDevices() {
        return disconnectedCommonDevices;
    }

    public ApplicationContext(MainActivity mainActivity) {
        /**
         * Initialize all commands available in application
         */
        CommandDataClass.initializeCommandsData();
        deviceHandler = new DeviceHandler();
        voiceRecognition = new VoiceRecognition(mainActivity);
        commonDevices = new ArrayList<>();
        disconnectedCommonDevices = new ArrayList<>();
        systemCommandHandler = new SystemCommandHandler();
        macIdMap = new HashMap<>();

        BroadcastListener asyncTask = new BroadcastListener();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){ // Above Api Level 13
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else{ // Below Api Level 13
            asyncTask.execute();
        }
        deviceHandler.startSendingConsistencyControlMessage();

    }

    /**
     * getters
     */
    public static ArrayList<CommonDevice> getCommonDevices() {
        return commonDevices;
    }
    public Language getSystemLanguage() {
        return systemLanguage;
    }
    
    public VoiceRecognition getVoiceRecognition() {
        return voiceRecognition;
    }

    /**
     * methods
     */
    public static void addCommonDevice(CommonDevice commonDevice) {
        ApplicationContext.commonDevices.add(commonDevice);
        if(macIdMap.get(commonDevice.getMacAddress()) == null){
            macIdMap.put(commonDevice.getMacAddress(),commonDevice.getDeviceId());
        }

    }
    public void setSystemLanguage(Language systemLanguage) {
        this.systemLanguage = systemLanguage;
    }


}
