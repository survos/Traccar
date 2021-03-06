/*
 * Copyright 2012 - 2014 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.survos.tracker;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.edmodo.rangebar.RangeBar;
import com.survos.tracker.Constants.Constants;
import com.survos.tracker.data.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;


/**
 * Main user interface
 */
@SuppressWarnings("deprecation")
public class TraccarActivity extends PreferenceActivity implements View.OnClickListener {

    public static final String LOG_TAG = "traccar";

    public static final String KEY_ID = "id";
    public static final String KEY_SUBJECT_ID = "subject_id";
//    public static final String KEY_DEVICE_NAME = "device_name";
//    public static final String KEY_ADDRESS = "address";
//    public static final String KEY_PORT = "port";
    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_STATUS = "status";
    public static final String KEY_RESTRICT_TIME = "time_restrict";
    public static final String KEY_RESTRICT_START_TIME = "restrict_start_time";
    public static final String KEY_RESTRICT_STOP_TIME = "restrict_stop_time";

    String oldVal;
    /**
     * holds the dialog for selecting time range
     */
    private Dialog mSelectRestrictTimeDialog;

    /**
     * variable for holding sharedpreferences
     */
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mSharedPreferences = getPreferenceScreen().getSharedPreferences();

        initPreferences();
        oldVal = mSharedPreferences.getString(KEY_SUBJECT_ID, null);
        if (mSharedPreferences.getBoolean(KEY_STATUS, false))
            startService(new Intent(this, TraccarService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(
                preferenceChangeListener);
    }

    @Override
    protected void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                preferenceChangeListener);
        super.onPause();
    }

    OnSharedPreferenceChangeListener preferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_STATUS)) {
                if (sharedPreferences.getBoolean(KEY_STATUS, false)) {
                    startService(new Intent(TraccarActivity.this, TraccarService.class));
                } else {
                    stopService(new Intent(TraccarActivity.this, TraccarService.class));
                }
            } else if (key.equals(KEY_ID)) {
                findPreference(KEY_ID).setSummary(sharedPreferences.getString(KEY_ID, null));
//            } else if (key.equals(KEY_ADDRESS)) {
//                findPreference(KEY_ADDRESS).setSummary(sharedPreferences.getString(KEY_ADDRESS, null));

            } else if (key.equals(KEY_RESTRICT_TIME)) {
                if (!sharedPreferences.getBoolean(KEY_RESTRICT_TIME, false)) {
                    findPreference(KEY_RESTRICT_TIME).setSummary(getResources().
                            getString(R.string.select_time_interval));
                } else {
                    openSelectRestrictTimeDialog();
                }
//            } else if (key.equals(KEY_PORT)) {
//
//                findPreference(KEY_PORT).setSummary(sharedPreferences.getString(KEY_PORT,
//                        getResources().getString(R.string.settings_port_summary)));

            } else if (key.equals(KEY_INTERVAL)) {
                findPreference(KEY_INTERVAL).setSummary(sharedPreferences.getString(KEY_INTERVAL,
                        getResources().getString(R.string.settings_interval_summary)));

            }else if (key.equals(KEY_SUBJECT_ID)) {
                Log.d("aaa","new val =>"+sharedPreferences.getString(KEY_SUBJECT_ID, null)+" and old=>"+oldVal);
                if(Utils.validateSubjectID(Integer.parseInt(sharedPreferences.getString(KEY_SUBJECT_ID, null)))) {
                    Log.d("aaa", "inside if");
                    findPreference(KEY_SUBJECT_ID).setSummary(sharedPreferences.getString(KEY_SUBJECT_ID, null));
                }
                else
                {
                    Log.d("aaa","inside else");
                    sharedPreferences.edit().putString(KEY_SUBJECT_ID, oldVal).commit();
                    PreferenceScreen screen = (PreferenceScreen) findPreference("preferencescreen");
                    screen.onItemClick( null, null, 2, 0 );
                    screen.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Log.d("aaa","preference change");
                            if(Utils.validateSubjectID(Integer.parseInt(sharedPreferences.getString(KEY_SUBJECT_ID, null))))
                                 findPreference(KEY_SUBJECT_ID).setSummary(sharedPreferences.getString(KEY_SUBJECT_ID, null));
                            return false;
                        }
                    });
                    Toast.makeText(TraccarActivity.this,"Enter Valid SubjectId",Toast.LENGTH_SHORT).show();
                }


            }/*else if (key.equals(KEY_DEVICE_NAME)) {
//                findPreference(KEY_DEVICE_NAME).setSummary(sharedPreferences.getString(KEY_DEVICE_NAME, null));

            }*/
        }
    };

    private void openSelectRestrictTimeDialog() {

        // custom dialog
        mSelectRestrictTimeDialog = new Dialog(this);
        mSelectRestrictTimeDialog.setContentView(R.layout.layout_restrict_time_dialog);
        mSelectRestrictTimeDialog.setTitle("Restrict Time");

        final TextView timeDifferenceText = (TextView) mSelectRestrictTimeDialog.findViewById(R.id.time_difference);
        final Button setButton = (Button) mSelectRestrictTimeDialog.findViewById(R.id.set_button);


        //setting up range bar
        RangeBar rangebar = (RangeBar) mSelectRestrictTimeDialog.findViewById(R.id.rangebar);
        rangebar.setTickCount(24);
        rangebar.setTickHeight(25);
        rangebar.setBarWeight(6);
        rangebar.setBarColor(229999999);
        int startTime = mSharedPreferences.getInt(KEY_RESTRICT_START_TIME, 0);
        int stopTime = mSharedPreferences.getInt(KEY_RESTRICT_STOP_TIME, 24);

        rangebar.setThumbIndices(startTime , stopTime - 1);


        String startString = "",stopString = "";
        try {
            DateFormat f1 = new SimpleDateFormat("HH");
            Date d = f1.parse(startTime + "");
            DateFormat f2 = new SimpleDateFormat("hh:mma");
            stopString = f2.format(f1.parse(startTime + "")).toLowerCase();
            startString = f2.format(d).toLowerCase();

        }
        catch (ParseException e) {

        }

        timeDifferenceText.setText(startString + " - " + stopString);


        rangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {

                mSharedPreferences.edit().putInt(KEY_RESTRICT_START_TIME, leftThumbIndex + 1).commit();
                mSharedPreferences.edit().putInt(KEY_RESTRICT_STOP_TIME, rightThumbIndex + 1).commit();

                String startString = "",stopString = "";
                try {
                    DateFormat f1 = new SimpleDateFormat("HH");
                    Date d = f1.parse(leftThumbIndex + "");
                    DateFormat f2 = new SimpleDateFormat("hh:mma");
                    stopString = f2.format(f1.parse(rightThumbIndex + 1 + "")).toLowerCase();
                    startString = f2.format(d).toLowerCase();

                }
                catch (ParseException e) {

                }

                findPreference(KEY_RESTRICT_TIME).setSummary(startString + " - " + stopString);
                timeDifferenceText.setText(startString + " - " + stopString);

            }
        });

        setButton.setOnClickListener(this);
        mSelectRestrictTimeDialog.show();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_map_home, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//
//
//            final Intent settings = new Intent(this,
//                    TraccarActivity.class);
//            startActivity(settings);
//            return true;
//        }
//
////        else  if (id == R.id.status) {
////
////            startActivity(new Intent(this, StatusActivity.class));
////
////            return true;
////        }
//        else  if (id == R.id.about) {
//
//
//            startActivity(new Intent(this, AboutActivity.class));
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TraccarService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void initPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        String id = telephonyManager.getDeviceId();

        String id = getPrimaryEmailAccount();
        Log.d("divyesh", "email=>"+id);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        if (!sharedPreferences.contains(KEY_ID)) {
            sharedPreferences.edit().putString(KEY_ID, id).commit();
        }

//        String serverAddress = sharedPreferences.getString(KEY_ADDRESS, getResources().getString(R.string.settings_address_summary));
//
//        findPreference(KEY_PORT).setSummary(sharedPreferences.getString(KEY_PORT,
//                getResources().getString(R.string.settings_port_summary)));

      /*  findPreference(KEY_INTERVAL).setSummary(sharedPreferences.getString(KEY_INTERVAL,
                getResources().getString(R.string.settings_interval_summary)));*/
        findPreference(KEY_INTERVAL).setSummary(sharedPreferences.getString(KEY_INTERVAL,""+60));

        findPreference("mobilenumber").setSummary(sharedPreferences.getString("mobilenumber",sharedPreferences.getString("mobilenumber","")));

        findPreference(KEY_SUBJECT_ID).setSummary(sharedPreferences.getString(KEY_SUBJECT_ID,
                getResources().getString(R.string.settings_subject_id_summary)));

//        findPreference(KEY_DEVICE_NAME).setSummary(sharedPreferences.getString(KEY_DEVICE_NAME,
//                getResources().getString(R.string.settings_device_name_summary)));


        findPreference(KEY_ID).setSummary(sharedPreferences.getString(KEY_ID, id));
//        findPreference(KEY_ADDRESS).setSummary(sharedPreferences.getString(KEY_ADDRESS, serverAddress));

        if (sharedPreferences.getBoolean(KEY_RESTRICT_TIME, false)) {
            int startTime = sharedPreferences.getInt(KEY_RESTRICT_START_TIME, 0);
            int stopTime = sharedPreferences.getInt(KEY_RESTRICT_STOP_TIME, 24);

            String startString = "",stopString = "";
            try {
                DateFormat f1 = new SimpleDateFormat("HH");
                Date d = f1.parse(startTime+1 + "");
                DateFormat f2 = new SimpleDateFormat("hh:mma");
                stopString = f2.format(f1.parse(stopTime+1 + "")).toLowerCase();
                startString = f2.format(d).toLowerCase();

            }
            catch (ParseException e) {

            }

            findPreference(KEY_RESTRICT_TIME).setSummary(startString + " - " + stopString);
        } else {
//            findPreference(KEY_RESTRICT_TIME).setSummary(getResources().getString(R.string.select_time_interval));
            findPreference(KEY_RESTRICT_TIME).setSummary("12:00am - 11:59pm");
        }


    }

    private String getPrimaryEmailAccount() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        String possibleEmail = "";
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                possibleEmail = account.name;
            }
        }
        return possibleEmail;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.set_button) {
            mSelectRestrictTimeDialog.dismiss();
        }
    }
}
