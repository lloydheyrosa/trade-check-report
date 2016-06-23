package com.android.pplusaudit2.PJP_Compliance;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.pplusaudit2.Database.SQLLibrary;
import com.android.pplusaudit2.Database.SQLiteDB;
import com.android.pplusaudit2.ErrorLogs.AutoErrorLog;
import com.android.pplusaudit2.General;
import com.android.pplusaudit2.R;
import com.android.pplusaudit2._Store.Stores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PjpActivity extends AppCompatActivity {

    ListView rvwPjpStores;
    ProgressDialog progressDialog;
    SQLLibrary sqlLibrary;

    ArrayList<Stores> arrStores;
    private String TAG;
    private boolean isNetworkEnabled;
    private boolean isGpsEnabled;
    private LocationManager locManager;
    private LocationListener locListener;
    private double dblLatitude;
    private double dblLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pjp_activity_layout);

        TAG = PjpActivity.this.getLocalClassName();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Thread.setDefaultUncaughtExceptionHandler(new AutoErrorLog(this, General.errlogFile));
        overridePendingTransition(R.anim.slide_in_left, R.anim.hold);

        sqlLibrary = new SQLLibrary(this);
        arrStores = new ArrayList<>();
        rvwPjpStores = (ListView) findViewById(R.id.lvwPjpStores);

        this.locListener = new LocationPjpListener();
        this.locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        new LoadStores().execute();
    }

    class LocationPjpListener implements LocationListener {

        public void onLocationChanged(Location location) {
            if (location != null) {
                if (ActivityCompat.checkSelfPermission(PjpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PjpActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }

                locManager.removeUpdates(locListener);

                dblLatitude = location.getLatitude();
                dblLongitude = location.getLongitude();

                String cityname = "";
                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

                try {
                    List<Address> addresses = gcd.getFromLocation(dblLatitude, dblLongitude, 1);
                    if (addresses.size() > 0) {
                        System.out.println(((Address) addresses.get(0)).getLocality());
                        cityname = addresses.get(0).getLocality();
                    }
                    ((Address) addresses.get(0)).getLocality();
                } catch (IOException ex) {
                    String err = "Can't get location.";
                    String excErr = ex.getMessage() != null ? ex.getMessage() : err;
                    General.errorLog.appendLog(excErr, TAG);
                }

                String gpsLoc = "Coordinates: " + dblLatitude + ", " + dblLongitude + ". City: " + cityname;
                //General.messageBox(MainActivity.this, "GPS Location", gpsLoc);
            }
        }

        public void onProviderDisabled(String provider) { }
        public void onProviderEnabled(String provider) { }
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }

    public class LoadStores extends AsyncTask<Void, Void, Boolean> {
        private String errorMsg;
        List<Stores> lstStores;

        @Override
        protected void onPreExecute() {
            lstStores = new ArrayList<>();
            progressDialog = ProgressDialog.show(PjpActivity.this, "", "Loading Stores. Please wait");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean res = false;

            try {
                Cursor cursorPjp = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_STORE);

                if (cursorPjp.moveToFirst()) {
                    while (!cursorPjp.isAfterLast()) {

                        int nstoreid = cursorPjp.getInt(cursorPjp.getColumnIndex(SQLiteDB.COLUMN_STORE_id));

                        String storeCode = cursorPjp.getString(cursorPjp.getColumnIndex(SQLiteDB.COLUMN_STORE_storecode));
                        String webStoreid = cursorPjp.getString(cursorPjp.getColumnIndex(SQLiteDB.COLUMN_STORE_storeid));

                        String storename = cursorPjp.getString(cursorPjp.getColumnIndex(SQLiteDB.COLUMN_STORE_name)).trim().replace("\"", "");
                        int templateid = cursorPjp.getInt(cursorPjp.getColumnIndex(SQLiteDB.COLUMN_STORE_audittempid));
                        String templatename = cursorPjp.getString(cursorPjp.getColumnIndex(SQLiteDB.COLUMN_STORE_templatename)).trim().replace("\"", "");
                        boolean isAudited = cursorPjp.getInt(cursorPjp.getColumnIndex(SQLiteDB.COLUMN_STORE_status)) > 0;
                        int finalValue = cursorPjp.getInt(cursorPjp.getColumnIndex(SQLiteDB.COLUMN_STORE_final));
                        boolean isPosted = cursorPjp.getInt(cursorPjp.getColumnIndex(SQLiteDB.COLUMN_STORE_posted)) == 1;

                        Stores newStore = new Stores(nstoreid, storeCode, webStoreid, storename, templateid, templatename, finalValue, isAudited, isPosted);

                        Cursor cursor = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_PJPCOMP, SQLiteDB.COLUMN_PJPCOMP_storeid + " = '" + nstoreid + "' AND " + SQLiteDB.COLUMN_PJPCOMP_date + " = '" + General.getDateToday() + "'");
                        cursor.moveToFirst();
                        int count = cursor.getCount();
                        if (count > 0) {
                            newStore.dateChecked = cursor.getString(cursor.getColumnIndex(SQLiteDB.COLUMN_PJPCOMP_date)).trim();
                            newStore.timeChecked = cursor.getString(cursor.getColumnIndex(SQLiteDB.COLUMN_PJPCOMP_time)).trim();
                            newStore.isChecked = true;
                        }

                        lstStores.add(newStore);
                        cursorPjp.moveToNext();
                    }
                }

                cursorPjp.close();
                res = true;
            } catch (Exception ex) {
                errorMsg = "Data Error in stores.";
                errorMsg = ex.getMessage() != null ? ex.getMessage() : errorMsg;
                General.errorLog.appendLog(errorMsg, TAG);
            }

            return res;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            if (!aBoolean) {
                General.messageBox(PjpActivity.this, "Loading stores", errorMsg);
                return;
            }
            arrStores.clear();
            arrStores.addAll(lstStores);
            StoreCompAdapter adapter = new StoreCompAdapter(PjpActivity.this, arrStores);
            rvwPjpStores.setAdapter(adapter);
        }
    }

    // btnCheckin
    public void onClickCheckIn(View v) {

        if (CheckGpsLocation()) {

            final Stores storeSel = (Stores) v.getTag();

            Cursor cursor = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_PJPCOMP, SQLiteDB.COLUMN_PJPCOMP_storeid + " = '" + storeSel.storeID + "' AND " + SQLiteDB.COLUMN_PJPCOMP_date + " = '" + General.getDateToday() + "'");
            cursor.moveToFirst();
            int count = cursor.getCount();
            if (count == 0) {

                new AlertDialog.Builder(PjpActivity.this)
                        .setTitle("Check in")
                        .setMessage("Do you want to check this store for audit?")
                        .setPositiveButton("Check in", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                RecordGpsLocation();
                                AddPjpRecord(storeSel);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else
                Toast.makeText(PjpActivity.this, "This store is already checked in.", Toast.LENGTH_LONG).show();
        }
    }

    public void AddPjpRecord(Stores storeSelected) {

        String[] aFields = new String[]{
                SQLiteDB.COLUMN_PJPCOMP_usercode,
                SQLiteDB.COLUMN_PJPCOMP_storeid,
                SQLiteDB.COLUMN_PJPCOMP_webstoreid,
                SQLiteDB.COLUMN_PJPCOMP_date,
                SQLiteDB.COLUMN_PJPCOMP_time,
        };

        String[] aValues = new String[]{
                General.usercode,
                String.valueOf(storeSelected.storeID),
                storeSelected.webStoreID,
                General.getDateToday(),
                General.getTimeToday()
        };

        try {
            sqlLibrary.AddRecord(SQLiteDB.TABLE_PJPCOMP, aFields, aValues);
            Toast.makeText(PjpActivity.this, "Successfully checked in for " + storeSelected.storeName, Toast.LENGTH_LONG).show();
            new LoadStores().execute();
        } catch (Exception ex) {
            Toast.makeText(PjpActivity.this, "Error in saving: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean RecordGpsLocation() {
        boolean result = false;

        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return result;
            }

            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
            result = true;
        }
        catch (Exception ex) {
            String strMsg = "Error in getting gps and network provider.";
            String err = ex.getMessage() != null ? ex.getMessage() : strMsg;
            General.errorLog.appendLog(err, TAG);
            Toast.makeText(this, strMsg, Toast.LENGTH_LONG).show();
        }

        return result;
    }

    private boolean CheckGpsLocation() {
        boolean result = false;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return result;
        }

        isNetworkEnabled = false;
        isGpsEnabled = false;

        try {
            isNetworkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            isGpsEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            String strMsg = "Error in getting gps and network provider.";
            String err = ex.getMessage() != null ? ex.getMessage() : strMsg;
            General.errorLog.appendLog(err, TAG);
            return result;
        }

        if (!isGpsEnabled) {
            showGPSDisabledAlertToUser();
            return result;
        }

        if (isNetworkEnabled || isGpsEnabled)
            result = true;
        else
            Toast.makeText(PjpActivity.this, "GPS Not available in this device.", Toast.LENGTH_LONG).show();

        return result;
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PjpActivity.this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_in_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.hold, R.anim.slide_in_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
