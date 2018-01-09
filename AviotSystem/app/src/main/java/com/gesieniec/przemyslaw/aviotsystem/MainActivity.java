package com.gesieniec.przemyslaw.aviotsystem;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceCapabilities;
import com.gesieniec.przemyslaw.aviotsystem.iothandler.DeviceType;
import com.gesieniec.przemyslaw.aviotsystem.iothandler.devices.CommonDevice;
import com.gesieniec.przemyslaw.aviotsystem.systemhandler.ApplicationContext;
import com.gesieniec.przemyslaw.aviotsystem.systemhandler.SystemCommandHandler;
import com.gesieniec.przemyslaw.aviotsystem.taskdispatcher.ITaskDispatcherListener;
import com.gesieniec.przemyslaw.aviotsystem.taskdispatcher.TaskDispatcher;
import com.gesieniec.przemyslaw.aviotsystem.view.devices.MultiSwitchInstanceFragment;
import com.gesieniec.przemyslaw.aviotsystem.view.ManualControlFragment;
import com.gesieniec.przemyslaw.aviotsystem.view.VoiceControlFragment;
import com.gesieniec.przemyslaw.aviotsystem.view.devices.SwitchInstanceFragment;
import com.gesieniec.przemyslaw.aviotsystem.voicehandler.VoiceCommand;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ITaskDispatcherListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private VoiceControlFragment voiceControlFragment;
    private ManualControlFragment manualControlFragment;
    private ApplicationContext applicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * layout
         */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        /**
         * application ctxt
         */
        TaskDispatcher.addListener(this);
        applicationContext = new ApplicationContext(this);


    }

    /**
     * layout related
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    voiceControlFragment = new VoiceControlFragment();
                    return voiceControlFragment;
                case 1:
                    manualControlFragment = new ManualControlFragment();
                    return manualControlFragment;
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    /**
     * Voice Control related
     */


    public void onClickStartStopCapturing(View view) {
        if (voiceControlFragment.getAviotButtonState() == false) {
            applicationContext.getVoiceRecognition().getSpeechRecognizer().startListening(applicationContext.getVoiceRecognition().getSpeechRecognizerIntent());
            voiceControlFragment.setAviotButtonState(true);
        } else {
            applicationContext.getVoiceRecognition().getSpeechRecognizer().stopListening();
            voiceControlFragment.setAviotButtonState(false);
        }
        scrollDownScrollView();
    }

    public void setAviotButtonState(boolean aviotButtonState) {
        voiceControlFragment.setAviotButtonState(aviotButtonState);
    }


    /**
     * Task dispatcher related
     */
    @Override
    public void handleDispatchedVoiceCommandExecution(VoiceCommand arg) {
        TextView t = new TextView(this);
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_console);
        t.setText("You:  " + arg.getBestMatchCommand());
        if ((arg.getVoiceCommandType() != VoiceCommand.VoiceCommandType.INVALID)) {
            t.setTextColor(Color.rgb(255, 255, 255));
            ll.addView(t);
            scrollDownScrollView();
        } else {
            t.setTextColor(Color.rgb(255, 255, 40));
            ll.addView(t);
            writeAviotMessage("I can not do that");
            //STUB();
        }

    }


    @Override
    public void handleDispatchedSystemCommandExecution(SystemCommandHandler systemCommandHandler) {
        if (systemCommandHandler.getSystemAnswer().equals("Console cleared")) {
            if (((LinearLayout) findViewById(R.id.ll_console)).getChildCount() > 0) {
                ((LinearLayout) findViewById(R.id.ll_console)).removeAllViews();
            }
        }
        writeAviotMessage(systemCommandHandler.getSystemAnswer());
    }

    @Override
    public void handleDispatchedIoTCommandExecution(List<String> data) {
        writeAviotMessage("New device trying to connect");
    }

    @Override
    public void handleDispatchedIoTCommandExecution(String capabilities) {
        writeAviotMessage("New device: Switch, connected");
        addManualControlFragment(capabilities);
    }


    @Override
    public void handleDispatchedGUICommandExecution(DeviceCapabilities capabilities) {

    }

    @Override
    public void handleDispatchedUpdateDeviceDataCommandExecution(DeviceCapabilities capabilities) {

    }

    @Override
    public void handleDispatchedIoTConsistencyControl(DeviceCapabilities capabilities) {
        //DO NOT IMPLEMENT
    }

    @Override
    public void handleDispatchedIoTDeviceNotResponding(CommonDevice data) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                writeAviotMessage("Device: " + data.getName() + " disconnected");
                removeManualControlFragment(data);
            }
        });
    }


    @Override
    public void handleDispatchedIoTUpdateCommandExecution(DeviceCapabilities capabilities) {

        String state = "";
        int index = 0;

        for (int i = 0; i < capabilities.getStates().size(); i++) {
            Switch s = (Switch) findViewById(capabilities.getIdBasedOnMAC(capabilities.getMacAddress())+i);
            boolean oldState = s.isChecked();
            if(oldState != capabilities.getStates().get(i)){
                s.setChecked(capabilities.getStates().get(i));
                state = "OFF";
                if (capabilities.getStates().get(i)) {
                    state = "ON";
                }
                index = i;
                break;
            }
        }
        writeAviotMessage( capabilities.getOrdinalModifier().get(index) +" "+ capabilities.getDeviceName() + " is now " + state);
    }


    private void writeAviotMessage(String msg) {
        TextView systemResponse = new TextView(this);
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_console);
        systemResponse.setTextColor(Color.rgb(114, 156, 239));
        systemResponse.setText("AVIOT: " + msg);
        ll.addView(systemResponse);
        scrollDownScrollView();
    }

    /**
     * device fragment related
     */
    private int deviceID = 0;

    private void addManualControlFragment(String capabilities) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("capabilities", capabilities);
        bundle.putInt("fragmentID", deviceID);
        DeviceCapabilities caps = new DeviceCapabilities(capabilities);
        if (caps.getDeviceType() == DeviceType.SWITCH) {
            SwitchInstanceFragment device = new SwitchInstanceFragment();
            device.setArguments(bundle);
            fragmentTransaction.add(R.id.ll_devices, device, "device" + deviceID);
            deviceID+=100;
            fragmentTransaction.commit();
        } else if (caps.getDeviceType() == DeviceType.MULTI_SWITCH) {
            MultiSwitchInstanceFragment device = new MultiSwitchInstanceFragment();
            device.setArguments(bundle);
            fragmentTransaction.add(R.id.ll_devices, device, "device" + deviceID);
            deviceID+=100;
            fragmentTransaction.commit();
        }
    }

    private void removeManualControlFragment(CommonDevice device) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("device" + device.getDeviceId());
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    /**
     * Manual Control related
     */

    private void scrollDownScrollView() {
        final ScrollView sv = (ScrollView) findViewById(R.id.scrollViewConsole);
        sv.post(() -> sv.fullScroll(View.FOCUS_DOWN));
    }


}
