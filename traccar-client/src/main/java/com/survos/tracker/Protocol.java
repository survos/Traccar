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

import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.survos.tracker.data.DBInterface;
import com.survos.tracker.data.DatabaseColumns;
import com.survos.tracker.data.TableLocationPoints;
import com.survos.tracker.data.TableServerCache;

/**
 * Protocol formatting
 */
public class Protocol implements DBInterface.AsyncDbQueryCallback{

    public  final int INSERT_LOCATION = 1;
    public  final int INSERT_MESSAGE_CACHE = 2;

    /**
     * Format device id message
     */
    public static String createLoginMessage(String id) {
        StringBuilder s = new StringBuilder("$PGID,");
        Formatter f = new Formatter(s, Locale.ENGLISH);

        s.append(id);

        byte checksum = 0;
        for (byte b : s.substring(1).getBytes()) {
            checksum ^= b;
        }
        f.format("*%02x\r\n", (int) checksum);
        f.close();

        return s.toString();
    }

    /**
     * Format location message
     */
    public  String createLocationMessage( Location l, double battery) {
        StringBuilder s = new StringBuilder(true ? "$TRCCR2," : "$GPRMC,");
        Formatter f = new Formatter(s, Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.setTimeInMillis(l.getTime());

       // if (extended) {

            f.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS.%1$tL,A,", calendar);

            //TODO add to the format
            Log.d("provider",l.getProvider());
            Log.d("accuracy",l.getAccuracy()+"");


            f.format("%.6f,%.6f,", l.getLatitude(), l.getLongitude());
            f.format("%.2f,%.2f,", l.getSpeed() * 1.943844, l.getBearing());
            f.format("%.2f,", l.getAltitude());
            f.format("%.0f,", battery);
            f.format("%s,",l.getProvider());
            f.format("%.2f,",l.getAccuracy());

        ContentValues values = new ContentValues();
        values.put(DatabaseColumns.ID,l.getTime()+"");
        values.put(DatabaseColumns.ACCURACY,l.getAccuracy()+"");
        values.put(DatabaseColumns.ALTITUDE,l.getAltitude()+"");
        values.put(DatabaseColumns.BATTERY_PERCENTAGE,battery+"");
        values.put(DatabaseColumns.BEARING,l.getBearing()+"");
        values.put(DatabaseColumns.LATITUDE,l.getLatitude()+"");
        values.put(DatabaseColumns.LONGITUDE,l.getLongitude()+"");
        values.put(DatabaseColumns.PROVIDER,l.getProvider()+"");
        values.put(DatabaseColumns.TYPE,"traccar2");
        values.put(DatabaseColumns.SPEED,(l.getSpeed()*1.943844)+"");

        DBInterface.insertAsync(INSERT_LOCATION,null,null,TableLocationPoints.NAME,null,values,
                false,this);


//        } else {
//
//            f.format("%1$tH%1$tM%1$tS.%1$tL,A,", calendar);
//
//            double lat = l.getLatitude();
//            double lon = l.getLongitude();
//            f.format("%02d%07.4f,%c,", (int) Math.abs(lat), Math.abs(lat) % 1 * 60, lat < 0 ? 'S' : 'N');
//            f.format("%03d%07.4f,%c,", (int) Math.abs(lon), Math.abs(lon) % 1 * 60, lon < 0 ? 'W' : 'E');
//
//            double speed = l.getSpeed() * 1.943844; // speed in knots
//            f.format("%.2f,%.2f,", speed, l.getBearing());
//            f.format("%1$td%1$tm%1$ty,,", calendar);
//            f.format("%s,",l.getProvider());
//            f.format("%.2f,",l.getAccuracy());
//
//        }

        byte checksum = 0;
        for (byte b : s.substring(1).getBytes()) {
            checksum ^= b;
        }
        f.format("*%02x\r\n", (int) checksum);
        f.close();

        ContentValues valueCache = new ContentValues();
        valueCache.put(DatabaseColumns.MESSAGE,s.toString());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TrackerApplication.getStaticContext());


        if(!sharedPreferences.getBoolean(ClientController.KEY_CONNECTED,false)){
            DBInterface.insertAsync(INSERT_MESSAGE_CACHE,null,null, TableServerCache.NAME,null,valueCache,
                    false,this);
        }

        return s.toString();
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

    }
}

