package com.gesieniec.przemyslaw.aviotsystem.view;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gesieniec.przemyslaw.aviotsystem.R;

/**
 * Created by przem on 26.11.2017.
 */

public class ManualControlFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.manual_control_fragment, container, false);
        return rootView;
    }
}

