package com.survos.tracker.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.survos.tracker.Constants.Constants;
import com.survos.tracker.R;
import com.survos.tracker.TraccarActivity;
import com.survos.tracker.TraccarService;
import com.survos.tracker.data.DBInterface;
import com.survos.tracker.data.DatabaseColumns;
import com.survos.tracker.data.Logger;
import com.survos.tracker.data.SQLiteLoader;
import com.survos.tracker.data.TableLocationPoints;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MapHomeActivity extends ActionBarActivity implements DBInterface.AsyncDbQueryCallback,
        LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleMap mGmap;
    private Switch mSwitch;
    private SharedPreferences mSharedPreferences;

    private TextView mLocationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_home);

        if (savedInstanceState == null) {
            mGmap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            loadLocationPoints();
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mSwitch = (Switch) findViewById(R.id.location_switch);
        mLocationText = (TextView) findViewById(R.id.location_text);

        if (mSharedPreferences.getBoolean(TraccarActivity.KEY_STATUS, false)) {
            mSwitch.setChecked(true);
        } else {
            mSwitch.setChecked(false);
        }
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(new Intent(MapHomeActivity.this, TraccarService.class));
                    mSharedPreferences.edit().putBoolean(TraccarActivity.KEY_STATUS, true).commit();
                } else {
                    mSharedPreferences.edit().putBoolean(TraccarActivity.KEY_STATUS, false).commit();
                    stopService(new Intent(MapHomeActivity.this, TraccarService.class));
                }
            }
        });

        DBInterface.queryAsync(Constants.QueryTokens.QUERY_LOCATION_POINTS, null, null, true, TableLocationPoints.NAME,
                null, null, null, null, null, null, "30", this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            final Intent settings = new Intent(this,
                    TraccarActivity.class);
            startActivity(settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInsertComplete(int taskId, Object cookie, long insertRowId) {

    }

    @Override
    public void onDeleteComplete(int taskId, Object cookie, int deleteCount) {

    }

    @Override
    public void onUpdateComplete(int taskId, Object cookie, int updateCount) {

    }

    @Override
    public void onQueryComplete(int taskId, Object cookie, Cursor cursor) {

        cursor.moveToFirst();

        Logger.d("CURSOR", cursor.getCount() + "");
        Marker[] markers = new Marker[cursor.getCount()];
        for (int i = 0; i < cursor.getCount(); i++) {
            if (i + 1 == cursor.getCount()) {
                Log.d("timee", cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME)));
                mLocationText.setText("Last location updated at " +
                        cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME)));
            }
            markers[i] = mGmap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseColumns.LATITUDE))),
                            Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseColumns.LONGITUDE)))))
                    .title(cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME)) + " provider : " +

                            cursor.getString(cursor.getColumnIndex(DatabaseColumns.PROVIDER))));
//            markers[i]
//                    .title(cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME))+" provider : "+
//
//                            cursor.getString(cursor.getColumnIndex(DatabaseColumns.PROVIDER)));
            cursor.moveToNext();

        }

        if(cursor.getCount()!=0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);

            try {
                mGmap.moveCamera(cu);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void loadLocationPoints() {
        getSupportLoaderManager().restartLoader(Constants.LoaderIds.LOAD_LOCATION, null, MapHomeActivity.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        if (loaderId == Constants.LoaderIds.LOAD_LOCATION) {


            return new SQLiteLoader(this, false, TableLocationPoints.NAME, null,
                    null, null, null, null, null, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == Constants.LoaderIds.LOAD_LOCATION) {

            Logger.d("TAG", "Cursor Loaded with count: %d", cursor.getCount());

            cursor.moveToFirst();

            DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
            Logger.d("CURSOR", cursor.getCount() + "");
            Marker[] markers = new Marker[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++) {

                if (i + 1 == cursor.getCount()) {
                    mLocationText.setText("Last location updated at " +
                                    getDate(Long.parseLong(cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME))),"hh:mma"));

                    mGmap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseColumns.LATITUDE))),
                                    Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseColumns.LONGITUDE)))))
                            .title(cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME)) + " provider : " +

                                    cursor.getString(cursor.getColumnIndex(DatabaseColumns.PROVIDER))));
                }

//            markers[i]
//                    .title(cursor.getString(cursor.getColumnIndex(DatabaseColumns.TIME))+" provider : "+
//
//                            cursor.getString(cursor.getColumnIndex(DatabaseColumns.PROVIDER)));
                cursor.moveToNext();

            }

        }
    }
    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}