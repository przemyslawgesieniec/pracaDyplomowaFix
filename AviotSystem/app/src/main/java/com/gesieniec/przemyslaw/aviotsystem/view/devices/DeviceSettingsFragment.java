package com.gesieniec.przemyslaw.aviotsystem.view.devices;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gesieniec.przemyslaw.aviotsystem.R;
import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceCapabilities;
import com.gesieniec.przemyslaw.aviotsystem.taskdispatcher.TaskDispatcher;

/**
 * Created by przem on 26.11.2017.
 */

public class DeviceSettingsFragment extends android.support.v4.app.Fragment {

    DeviceCapabilities deviceCapabilities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** Inflating the layout for this fragment **/
        View v = inflater.inflate(R.layout.device_settings_fragment, null);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }


}