package com.gesieniec.przemyslaw.aviotsystem.view.devices;

import android.app.Activity;
import android.graphics.Color;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gesieniec.przemyslaw.aviotsystem.R;
import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceCapabilities;
import com.gesieniec.przemyslaw.aviotsystem.taskdispatcher.TaskDispatcher;

/**
 * Created by przem on 26.11.2017.
 */
public class SwitchInstanceFragment extends android.support.v4.app.Fragment {

    //TODO: CLEAN UP: przeniesc buttony do xml'a
    private String capabilities;
    private int deviceID;
    private boolean detailsState = false;

    LinearLayout ll;
    LinearLayout l2;
    LinearLayout l3;
    LinearLayout l4;
    LinearLayout l5;

    Switch s;
    ImageButton imgBtn;
    Button editBtn;
    Button saveBtn;
    ImageButton closeBtn;

    TextView deviceName;
    TextView deviceLocation;

    TextInputEditText deviceNameInput;
    TextInputEditText deviceLocationInput;
    TextInputEditText deviceWifiSSIDInput;
    TextInputEditText deviceWifiPASSInput;

    String oldWifiPassword;
    String oldWifiSSID;

    DeviceCapabilities deviceCapabilities;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** Inflating the layout for this fragment **/
        capabilities = getArguments().getString("capabilities");
        deviceID = getArguments().getInt("fragmentID");
        deviceCapabilities = new DeviceCapabilities(getArguments().getString("capabilities"));
        View v = inflater.inflate(R.layout.switch_instance_fragment, null);

        setViewElements(v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        this.view = view;
        view.findViewById(R.id.deviceSettings).setVisibility(RelativeLayout.GONE);
        Log.d("onCreateView.getId();", "" + view.getId());
        super.onViewCreated(view, savedInstanceState);
        ll = (LinearLayout) view.findViewById(R.id.switchContainer);
        l2 = (LinearLayout) view.findViewById(R.id.horizontalSpace);
        l3 = (LinearLayout) view.findViewById(R.id.detailsContainer);
        l4 = (LinearLayout) view.findViewById(R.id.mainFragmentContainer);
        l5 = (LinearLayout) view.findViewById(R.id.settingsContainer);

        view.findViewById(R.id.detailsContainer).setVisibility(LinearLayout.GONE);

        /**
         * State switch
         */
        s = new Switch(getActivity());
        s.setGravity(Gravity.FILL_VERTICAL);
        s.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        s.setChecked(deviceCapabilities.getStates().get(0));
        s.setId(deviceCapabilities.getIdBasedOnMAC(deviceCapabilities.getMacAddress()));
        Log.d("oSWITCH IDIDIDIDIDIID, ",""+deviceCapabilities.getID());
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceCapabilities.getStates().set(0, (!deviceCapabilities.getStates().get(0)));
                Log.d("button state ", String.valueOf(deviceCapabilities.getStates().get(0)));
                TaskDispatcher.newTask(TaskDispatcher.GuiTaskContext.SWITCH_STATE_CHANGED, deviceCapabilities);
            }
        });
        ll.addView(s);

        /**
         * Details dropdown arrow  button
         */


        //TODO : optymalizacja kodu onCLICK !
        imgBtn = new ImageButton(getActivity());
        imgBtn.setBackgroundColor(Color.TRANSPARENT);
        imgBtn.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        imgBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (detailsState) {
                    view.findViewById(R.id.detailsContainer).setVisibility(LinearLayout.GONE);
                    float deg = v.getRotation() + 180F;
                    v.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
                    detailsState = false;
                } else {
                    view.findViewById(R.id.detailsContainer).setVisibility(LinearLayout.VISIBLE);
                    float deg = v.getRotation() + 180F;
                    v.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
                    detailsState = true;
                }

            }
        });
        l2.addView(imgBtn, 0);

        /**
         * Edit button
         */

        editBtn = (Button) view.findViewById(R.id.btn_edit);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDeviceFragmentData();
                saveOldData();
            }
        });

        /**
         * Save button
         */
        saveBtn = (Button) view.findViewById(R.id.btn_saveChanges);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SAVE SAVE SAVE;", "save button clicked !!!!");
                if (isAnyDataChanged()) {
                    if(validateInputData()){

                        deviceName.setText(deviceNameInput.getText().toString());
                        deviceLocation.setText(deviceLocationInput.getText().toString());
                        deviceCapabilities.setDeviceName(deviceName.getText().toString());
                        deviceCapabilities.setDeviceLocation(deviceLocation.getText().toString());
                        TaskDispatcher.newTask(TaskDispatcher.GuiTaskContext.UPDATE_DEVICE_DATA,deviceCapabilities);
                        Log.d("SAVE SAVE SAVE;", "data has changed !");
                    }
                    Toast.makeText(getContext(),"Invalid data, you can not save",Toast.LENGTH_LONG);
                }
                view.findViewById(R.id.deviceSettings).setVisibility(RelativeLayout.GONE);
                view.findViewById(R.id.contentContainer).setVisibility(LinearLayout.VISIBLE);
                InputMethodManager inputMethodManager =(InputMethodManager)getContext().getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        /**
         *  Close Btn
         */
        closeBtn = (ImageButton) view.findViewById(R.id.settingsCloseBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.deviceSettings).setVisibility(RelativeLayout.GONE);
                view.findViewById(R.id.contentContainer).setVisibility(LinearLayout.VISIBLE);
            }
        });
    }


    private void setViewElements(View v) {
        deviceName = ((TextView) (v.findViewById(R.id.deviceName)));
        deviceName.setText(deviceCapabilities.getDeviceName());

        deviceLocation = ((TextView) (v.findViewById(R.id.deviceLocation)));
        deviceLocation.setText(deviceCapabilities.getDeviceLocation());

        ((TextView) (v.findViewById(R.id.ipAddress))).append(deviceCapabilities.getIpAddress());
        ((TextView) (v.findViewById(R.id.macAddress))).append(deviceCapabilities.getMacAddress());
        ((TextView) (v.findViewById(R.id.deviceType))).append(deviceCapabilities.getDeviceTypeString());

    }


    /**
     * Device settings fields
     */
    private void insertEditFields() {
        deviceNameInput = (TextInputEditText) view.findViewById(R.id.deviceNameInputField);
        deviceLocationInput = (TextInputEditText) view.findViewById(R.id.deviceLocationInputField);
        deviceWifiSSIDInput = (TextInputEditText) view.findViewById(R.id.devicessidInputField);
        deviceWifiPASSInput = (TextInputEditText) view.findViewById(R.id.devicepassInputField);

        deviceNameInput.setText(deviceCapabilities.getDeviceName());
        deviceLocationInput.setText(deviceCapabilities.getDeviceLocation());
        deviceWifiSSIDInput.setText(getWiFiSSID());


    }

    private void editDeviceFragmentData() {
        view.findViewById(R.id.deviceSettings).setVisibility(RelativeLayout.VISIBLE);
        view.findViewById(R.id.contentContainer).setVisibility(LinearLayout.GONE);
        insertEditFields();
    }


    private String getWiFiSSID() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().WIFI_SERVICE);
        WifiInfo wifiInfo;

        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            return wifiInfo.getSSID();
        }
        return "none";
    }

    private void saveOldData() {
        oldWifiPassword = deviceWifiPASSInput.getText().toString();
        oldWifiSSID = deviceWifiSSIDInput.getText().toString();
    }

    private boolean isAnyDataChanged() {
        boolean returnValue = false;
        if (!oldWifiSSID.equals(deviceWifiSSIDInput.getText().toString())) {
            Log.d("isAnyDataChanged", "deviceWifiSSIDInput changed");
            returnValue = true;
        }
        if (!oldWifiPassword.equals(deviceWifiPASSInput.getText().toString())) {
            Log.d("isAnyDataChanged", "deviceWifiPASSInput changed");
            returnValue = true;
        }
        if (!deviceLocation.equals(deviceLocationInput.getText().toString())) {
            Log.d("isAnyDataChanged", "deviceLocationInput changed");
            returnValue = true;
        }
        if (!deviceName.equals(deviceNameInput.getText().toString())) {
            Log.d("isAnyDataChanged", "deviceNameInput changed");
            returnValue = true;
        }
        return returnValue;
    }
    private boolean validateInputData(){
        if(deviceNameInput.getText().toString().length() == 0){
            deviceNameInput.setError("Device name is required");
            Log.d("validateInputData", "Device name is required");
            return false;
        }
        if(deviceLocationInput.getText().toString().length() == 0){
            deviceLocationInput.setError("Device location is required");
            Log.d("validateInputData", "Device location is required");
            return false;
        }
        return true;
    }

}