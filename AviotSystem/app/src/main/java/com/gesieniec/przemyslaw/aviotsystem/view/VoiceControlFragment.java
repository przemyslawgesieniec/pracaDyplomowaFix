package com.gesieniec.przemyslaw.aviotsystem.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gesieniec.przemyslaw.aviotsystem.R;
import com.gesieniec.przemyslaw.aviotsystem.systemhandler.ApplicationContext;

/**
 * Created by przem on 26.11.2017.
 */

public class VoiceControlFragment extends Fragment {
        private boolean aviotButtonState = false;
        private ApplicationContext applicationContext;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.voice_control_fragment, container, false);
                return rootView;
        }



    public void setAviotButtonState(boolean aviotButtonState) {
        this.aviotButtonState = aviotButtonState;
    }

    public boolean getAviotButtonState() {
        return aviotButtonState;
    }
}