package com.gesieniec.przemyslaw.aviotsystem.voicehandler;

import android.util.Log;

import com.gesieniec.przemyslaw.aviotsystem.iothandler.devices.CommonDevice;
import com.gesieniec.przemyslaw.aviotsystem.systemhandler.ApplicationContext;
import com.gesieniec.przemyslaw.aviotsystem.systemhandler.CommandDataClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przem on 02.11.2017.
 */

public class VoiceCommandHandler {

    private VoiceCommand voiceCommand;

    public VoiceCommandHandler(VoiceCommand voiceCommand) {
        this.voiceCommand = voiceCommand;
    }


    private enum CommandMathResult {
        FULL_MATCH,
        PARTIAL_MATCH,
        NO_MATCH
    }

    public void interpreteCommand(List<String> capturedVoiceResult) {
        parseVoiceResultToCommand(capturedVoiceResult);
    }

    private void parseVoiceResultToCommand(List<String> capturedVoiceResult) {
        CommandMathResult match = CommandMathResult.NO_MATCH;
        List<String> capturedVoiceResultLowerCase = new ArrayList<>(StringArrayToLowerCase(capturedVoiceResult));
        for (String possibleCommand : capturedVoiceResultLowerCase) {
            /**
             * Try to find keywords in possible command
             */
            match = tryMatchWithKeyWords(possibleCommand);

            if (match == CommandMathResult.FULL_MATCH) {
                Log.d("VoiceCommandHandler", "full match");
                voiceCommand.setVoiceCommandType(VoiceCommand.VoiceCommandType.DEVICE_RELATED);
                voiceCommand.setBestMatchCommand(possibleCommand);
                Log.d("VoiceCommandHandler", "full match possible command: " + possibleCommand);
                return;
            } else if (match == CommandMathResult.PARTIAL_MATCH) {

                Log.d("VoiceCommandHandler", "partial match");
                Log.d("VoiceCommandHandler", "possible command: " + possibleCommand);
                voiceCommand.setVoiceCommandType(VoiceCommand.VoiceCommandType.DEVICE_RELATED);
                voiceCommand.setBestMatchCommand(possibleCommand);
                return;
            } else if (tryToMachWitchSystemCommands(possibleCommand)) {
                Log.d("VoiceCommandHandler", "system command check" + possibleCommand);
                voiceCommand.setVoiceCommandType(VoiceCommand.VoiceCommandType.SYSTEM_RELATED);
                voiceCommand.setBestMatchCommand(possibleCommand);
                return;
            }
        }

        if (match == CommandMathResult.NO_MATCH || match == CommandMathResult.PARTIAL_MATCH) {
            voiceCommand.setBestMatchCommand(capturedVoiceResultLowerCase.get(0));
        }
    }

    private boolean checkPartialMatchPossibility(String mDeviceName) {

        int deviceNameOccurrences = 0;
        for (String device : CommandDataClass.getDevicesListENG()) {
            if (mDeviceName.compareTo(device) == 0) {
                deviceNameOccurrences++;
            }
        }
        if (deviceNameOccurrences == 1) {
            return true;
        }
        return false;
    }

    private boolean tryToMachWitchSystemCommands(String possibleCommand) {
        for (String systemCommand : CommandDataClass.getSystemStatusCommandsENG()) {
            if (systemCommand.equals(possibleCommand)) {
                voiceCommand.setCommandLanguage(VoiceCommand.Language.ENG_ENGLISH);
                return true;
            }
        }
        for (String systemCommand : CommandDataClass.getSystemStatusCommandsPL()) {
            if (systemCommand.equals(possibleCommand)) {
                voiceCommand.setCommandLanguage(VoiceCommand.Language.PL_POLISH);
                return true;
            }
        }
        for (String systemCommand : CommandDataClass.getConsoleCrearingCommandsENG()) {
            if (systemCommand.equals(possibleCommand)) {
                voiceCommand.setCommandLanguage(VoiceCommand.Language.ENG_ENGLISH);
                return true;
            }
        }

        return false;
    }


    private CommandMathResult tryMatchWithKeyWords(String possibleCommand) {
        Log.d("VoiceCommandHandler", "tryMatchWithKeyWords");
        int PL_score = 0;
        int ENG_score = 0;
        String mAction = null;
        String mDeviceName = null;
        String mPlace = null;
        boolean mNegation = false;
        /**
         * ENG
         */

        for (CommonDevice device : ApplicationContext.getCommonDevices()) {
            int i = 1;
            Log.d("VoiceCommandHandler ", "match with connected Devices");
            Log.d("Device nr", ""+i);
            Log.d("Device name", device.getName());
            Log.d("Device location", device.getLocation());
            for (String action: device.getActionList()){
                Log.d("Device action", action);
            }

            for (String action : device.getActionList()) {
                if (possibleCommand.contains(action)) {
                    mAction = action;
                    ENG_score++;
                }
            }
            if (possibleCommand.contains(device.getName())) {
                mDeviceName = device.getName();
                ENG_score++;
            }

            if (possibleCommand.contains(device.getLocation())) {
                mPlace = device.getLocation();
                ENG_score++;
            }
            i++;
        }

        for (String negation : CommandDataClass.getNegationENG()) {
            if (possibleCommand.contains(negation)) {
                mNegation = true;
                ENG_score++;
            }
        }




    /**
     * PL
     */
//        for (String action:CommandDataClass.getActionsListPL()){
//            if(possibleCommand.contains(action)){
//                mAction = action;
//                PL_score++;
//            }
//        }
//        for (String device:CommandDataClass.getDevicesListPL()){
//            if(possibleCommand.contains(device)){
//                mDeviceName = device;
//                PL_score++;
//            }
//        }
//        for (String place:CommandDataClass.getPlacesListPL()){
//            if(possibleCommand.contains(place)){
//                mPlace = place;
//                PL_score++;
//            }
//        }
//        for (String negation:CommandDataClass.getNegationPL()){
//            if(possibleCommand.contains(negation)){
//                mNegation = true;
//                PL_score++;
//            }
//        }
    /**
     * Determine language based on achieved scores
     */
        if(ENG_score >=PL_score)

    {
        voiceCommand.setCommandLanguage(VoiceCommand.Language.ENG_ENGLISH);
    }
        else

    {
        voiceCommand.setCommandLanguage(VoiceCommand.Language.PL_POLISH);
    }

    /**
     * COMMON
     */
        if(mAction !=null&&mDeviceName !=null&&mPlace !=null)

    {
        voiceCommand.setAction(mAction);
        voiceCommand.setDeviceName(mDeviceName);
        voiceCommand.setPlace(mPlace);
        voiceCommand.setNegation(mNegation);
        return CommandMathResult.FULL_MATCH;
    }
        if(mAction !=null&&mDeviceName !=null)

    {
        voiceCommand.setAction(mAction);
        voiceCommand.setDeviceName(mDeviceName);
        voiceCommand.setNegation(mNegation);
        if (checkPartialMatchPossibility(mDeviceName)) {
            return CommandMathResult.PARTIAL_MATCH;
        }

    }
        return CommandMathResult.NO_MATCH;
}

    private List<String> StringArrayToLowerCase(List<String> capturedVoiceResult) {
        List<String> capturedVoiceResultLowerCase = new ArrayList<>();
        for (String possibleCommand : capturedVoiceResult) {
            capturedVoiceResultLowerCase.add(possibleCommand.toLowerCase());
        }
        return capturedVoiceResultLowerCase;
    }
}
