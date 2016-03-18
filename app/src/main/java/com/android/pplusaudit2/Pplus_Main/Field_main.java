package com.android.pplusaudit2.Pplus_Main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pplusaudit2.AutoUpdateApk.AutoUpdateApk;
import com.android.pplusaudit2.Database.SQLLibrary;
import com.android.pplusaudit2.Database.SQLiteDB;
import com.android.pplusaudit2.General;
import com.android.pplusaudit2.MainActivity;
import com.android.pplusaudit2.MyMessageBox;
import com.android.pplusaudit2._Store.Pplus_main;
import com.android.pplusaudit2.R;
import com.android.pplusaudit2.Settings;

import java.io.File;

/**
 * Created by ULTRABOOK on 9/30/2015.
 */
public class Field_main extends AppCompatActivity {

    SQLLibrary sql;
    private ProgressDialog progressDL;
    MyMessageBox messageBox;

    boolean doubleBackToExitPressedOnce = false;

    File dlpath;

    private String urlGetStores = "http://tcr.chasetech.com/api/user/stores?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.field_main);
        overridePendingTransition(R.anim.slide_up, R.anim.hold);

        dlpath =  new File(new File(getExternalFilesDir(null),""), "Downloads");

        String versionCode = "Trade Check Report v." + General.GetVersionName(this);
        getSupportActionBar().setTitle(versionCode);

        sql = new SQLLibrary(this);
        messageBox = new MyMessageBox(this);

        // SET UP AUTO UPDATE OF APK
        new AutoUpdateApk(this);

        File appfolder = new File(getExternalFilesDir(null),"");
        Settings.captureFolder = new File(appfolder, "Captured Image");
        Settings.captureFolder.mkdirs();

        Settings.signatureFolder = new File(appfolder, "Signatures");
        Settings.signatureFolder.mkdirs();

        final TextView tvwUser = (TextView) findViewById(R.id.tvwUser);

        //ImageView imgLogo = (ImageView) findViewById(R.id.imgLogo);
        TextView tvwLoggedUser = (TextView) findViewById(R.id.tvwUser);
        tvwLoggedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doubleBackToExitPressedOnce) {
                    return;
                }

                doubleBackToExitPressedOnce = true;

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        final AlertDialog alert = new AlertDialog.Builder(Field_main.this).create();
                        alert.setMessage("Do you want to log out?");
                        alert.setTitle("Log out");
                        alert.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        sql.DeleteAllTables();

                                        Settings.captureFolder.delete();
                                        Settings.signatureFolder.delete();

                                        for (String files : General.DOWNLOAD_FILES) {
                                            File fDelete = new File(dlpath, files);
                                            if(!fDelete.delete()) Log.e("Deleting file", "Can't delete " + files);
                                        }

                                        Intent intentmenu = new Intent(Field_main.this, MainActivity.class);
                                        startActivity(intentmenu);
                                        finish();
                                    }
                                });

                        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        alert.dismiss();
                                    }
                                });

                        alert.show();
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);

            }
        });

        tvwUser.setText("USER: " + General.username.toUpperCase());

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });*/

        //MAIN MENU
        //final GridView gridViewMenu = (GridView) this.findViewById(R.id.gridViewMenu);
        final ListView lvwMenu = (ListView) this.findViewById(R.id.lvwDashBoard);
        //gridViewMenu.setAdapter(new Field_MenuAdapter(this, General.mainIconsFont));
        lvwMenu.setAdapter(new Field_MenuAdapter(this, General.mainIconsFont));

        lvwMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position)
                {
/*                    case 0:
                            break;*/
                    case 0: // AUDIT

                        Cursor cursorStores = sql.GetDataCursor(SQLiteDB.TABLE_STORE);
                        if(cursorStores.getCount() <= 0) {
                            messageBox.ShowMessage("Unavailable", "Store records are empty. Please re-download the files.");
                        }
                        else {
                            Intent intentStore = new Intent(Field_main.this, Pplus_main.class);
                            startActivity(intentStore);
                        }

                        break;
                    case 1: // REPORTS
                        break;
                    case 2: // LOGOUT

                        final AlertDialog alert = new AlertDialog.Builder(Field_main.this).create();
                        alert.setMessage("Do you want to log out?");
                        alert.setTitle("Log out");
                        alert.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

/*                                      sql.TruncateTable(SQLiteDB.TABLE_USER);
                                        sql.TruncateTable(SQLiteDB.TABLE_STORE);
                                        sql.TruncateTable(SQLiteDB.TABLE_QUESTION);
                                        sql.TruncateTable(SQLiteDB.TABLE_CATEGORY);
                                        sql.TruncateTable(SQLiteDB.TABLE_GROUP);
                                        sql.TruncateTable(SQLiteDB.TABLE_FORMS);
                                        sql.TruncateTable(SQLiteDB.TABLE_SINGLESELECT);
                                        sql.TruncateTable(SQLiteDB.TABLE_MULTISELECT);
                                        sql.TruncateTable(SQLiteDB.TABLE_COMPUTATIONAL);
                                        sql.TruncateTable(SQLiteDB.TABLE_CONDITIONAL);
                                        sql.TruncateTable(SQLiteDB.TABLE_STORECATEGORY);
                                        sql.TruncateTable(SQLiteDB.TABLE_STORECATEGORYGROUP);
                                        sql.TruncateTable(SQLiteDB.TABLE_STOREQUESTION);*/

/*                                        Settings.captureFolder.delete();
                                        Settings.signatureFolder.delete();*/

/*                                        Intent intentmenu = new Intent(Field_main.this, MainActivity.class);
                                        startActivity(intentmenu);*/
                                        finish();
                                    }
                                });

                        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        alert.dismiss();
                                    }
                                });

                        alert.show();
                    default:
                        break;
                }
            }
        });
    }

/*    public class AsyncLoadStores extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressDL = ProgressDialog.show(Field_main.this, "", "Retrieving data....", true);
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                String urlfinal = urlGetStores + "id=" + General.usercode;
                URL url = new URL(urlfinal);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                final String ex = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageBox.ShowMessage("Exception", ex);
                    }
                });
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }

            try {

                final JSONObject data = new JSONObject(response);

                final ProgressDialog pbardialog = new ProgressDialog(Field_main.this);
                pbardialog.setTitle("Update Stores");
                pbardialog.setMessage("Updating Stores. Please wait...");
                pbardialog.setProgressStyle(pbardialog.STYLE_SPINNER);
                pbardialog.setCancelable(false);

                pbardialog.show();

                Thread threadUpdate = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jArr = data.optJSONArray("stores");

                        try {
                            for (int i = 0; i < jArr.length(); i++) {
                                JSONObject jsonObj = jArr.getJSONObject(i);
                                String id = jsonObj.getString("id");
                                String storecode = jsonObj.getString("store_code");
                                String storename = jsonObj.getString("store");

                                String[] afields = {SQLiteDB.COLUMN_STORE_id, SQLiteDB.COLUMN_STORE_code, SQLiteDB.COLUMN_STORE_name};
                                String[] avalues = {id, storecode, storename};

                                sql.AddRecord(SQLiteDB.TABLE_STORE, afields, avalues);
                            }

                            pbardialog.dismiss();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intentActivity = new Intent(Field_main.this, Pplus_main.class);
                                    startActivity(intentActivity);
                                }
                            });
                        }
                        catch (Exception ex) { pbardialog.dismiss(); }
                    }
                });

                threadUpdate.start();


            }
            catch (JSONException ex) {
                messageBox.ShowMessage("Invalid Account", "Wrong username or password");
            }

            progressDL.dismiss();
        }
    }*/

    @Override
    public void onBackPressed() {
        finish();
    }
}
