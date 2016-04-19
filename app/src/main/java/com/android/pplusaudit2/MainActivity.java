package com.android.pplusaudit2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pplusaudit2.Database.SQLLibrary;
import com.android.pplusaudit2.Database.SQLiteDB;
import com.android.pplusaudit2.Debug.DebugLog;
import com.android.pplusaudit2.Json.JSON_pplus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

import com.android.pplusaudit2.Pplus_Main.Field_main;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    String UrlAPI;
    String urlGet;
    String password;
    String username;

    // DOWNLOAD TXT FILE
    File appfolder;
    File dlpath;

    File storeDIR;
    File categoryDIR;
    File groupDIR;
    File questionDIR;
    File formsDIR;
    File formtypesDIR;
    File singleselectDIR;
    File multiselectDIR;
    File computationalDIR;
    File conditionalDIR;
    File secondarylookupDIR;
    File secondarylistDIR;
    File osalistDIR;
    File osalookupDIR;
    File soslistDIR;
    File soslookupDIR;
    File imageListDIR;
    File imageProductDIR;

    String urlDownload;
    String urlDownloadperFile;
    String urlImage;
    String urlDownloadImage;
    private static final int BUFFER_SIZE = 4096;
    // -------

    MyMessageBox messageBox;
    JSON_pplus json;
    AlertDialog alertDialog;

    SQLLibrary sql;
    SQLiteDB sqLiteDB;

    View mainLayout;

    private ProgressDialog progressDL;

    PowerManager powerman;
    PowerManager.WakeLock wlStayAwake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        powerman = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
        wlStayAwake = powerman.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "wakelocktag");

        final TextView tvwVersion = (TextView) findViewById(R.id.tvwVersion);
        String versionCode = "v. " + General.GetVersionName(this);
        tvwVersion.setText(versionCode);

        UrlAPI =  General.mainURL + "/api/auth";
        urlGet =  General.mainURL + "/api/auth?";
        urlDownload = General.mainURL + "/api/download?";
        urlImage = General.mainURL + "/api/image?";

        messageBox = new MyMessageBox(this);
        json = new JSON_pplus(this);
        sqLiteDB = new SQLiteDB(this);
        sql = new SQLLibrary(this);

        mainLayout = findViewById(R.id.lnrMain);

        appfolder = new File(getExternalFilesDir(null),"");

        dlpath =  new File(appfolder, "Downloads");
        dlpath.mkdirs();

        Settings.postingFolder = new File(appfolder, "Posted");
        Settings.postingFolder.mkdirs();

        Settings.imgFolder = new File(appfolder, "Images");
        Settings.imgFolder.mkdirs();

        Cursor cursExisitingUser = sql.GetDataCursor(SQLiteDB.TABLE_USER);
        cursExisitingUser.moveToFirst();

        if(cursExisitingUser.getCount() == 1) {

            for (String files : General.DOWNLOAD_FILES) {
                File fDownload = new File(dlpath, files);
                if(!fDownload.exists()) {
                    Toast.makeText(MainActivity.this, "Downloaded files are incomplete. Please log in again.", Toast.LENGTH_LONG).show();

                    sql.DeleteAllTables();
                    Settings.captureFolder.delete();
                    Settings.signatureFolder.delete();

                    for (String fileToDelete : General.DOWNLOAD_FILES) {
                        File fDelete = new File(dlpath, fileToDelete);
                        if(!fDelete.delete()) Log.e("Deleting file", "Can't delete " + fileToDelete);
                    }
                    return;
                }
            }

            General.usercode = cursExisitingUser.getString(cursExisitingUser.getColumnIndex("code"));
            General.username = cursExisitingUser.getString(cursExisitingUser.getColumnIndex("name"));
            Intent mainIntent = new Intent(MainActivity.this, Field_main.class);
            startActivity(mainIntent);
            finish();
            return;
        }

        Button btnLogin = (Button) findViewById(R.id.btnLogin);

        final EditText txtUsername = (EditText) findViewById(R.id.txtUsername);
        //final Spinner spnUsername = (Spinner) findViewById(R.id.spnUsername);
        final EditText txtPassword = (EditText) findViewById(R.id.txtPassword);

/*        ArrayList<String> arrUsers = new ArrayList<String>();
        for (int i = 1; i <= 20; i++) {
            arrUsers.add("USER" + i);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, arrUsers);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUsername.setAdapter(dataAdapter);*/

        //txtUsername.setText("anj.castillo@unilever.com");
        //txtPassword.setText("password");


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = txtPassword.getText().toString().trim();
                username = txtUsername.getText().toString().trim();
                //username = String.valueOf(spnUsername.getSelectedItem());

                if(username.isEmpty()) {
                    txtUsername.setError("Username required!");
                    //Snackbar.make(mainLayout, "Username required.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }

                if(password.isEmpty()) {
                    txtPassword.setError("Password required!");
                    return;
                }

                if(username.isEmpty() && password.isEmpty()) {
                    txtUsername.setError("Username required!");
                    txtPassword.setError("Password required!");
                    //Snackbar.make(mainLayout, "Username and password required.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }

                wlStayAwake.acquire();
                //new AsyncGetUser().execute();
                new AsyncPingWebServer().execute();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AsyncPingWebServer extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            progressDL = ProgressDialog.show(MainActivity.this, "", "Checking web server availability.");
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int nRet = 0;

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL(General.mainURL);   // Change to "http://google.com" for www  test.
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(10 * 1000);          // 10 s.
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
                        Log.wtf("Connection", "Success !");
                        nRet = 1;
                    }
                } catch (MalformedURLException mue) {
                    Log.e("MalformedURLException", mue.getMessage());
                    nRet = 3;
                } catch (IOException ie) {
                    Log.e("IOException", ie.getMessage());
                    nRet = 3;
                }
            }
            else {
                nRet = 2;
            }

            return nRet;
        }

        @Override
        protected void onPostExecute(Integer nReturn) {
            progressDL.dismiss();
            if(nReturn == 3) { // EXCEPTION ERROR
                Toast.makeText(MainActivity.this, "Failed to connect to " + General.mainURL, Toast.LENGTH_SHORT).show();
                return;
            }
            if(nReturn == 0) { // WEB SERVER IS DOWN
                alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Server Down");
                alertDialog.setMessage(General.mainURL + " web server is down.\nPlease check the server.");
                alertDialog.setCancelable(true);
                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return;
            }
            if(nReturn == 2) { // NO INTERNET CONNECTION
                Toast.makeText(MainActivity.this, "No internet connection. Please connect to the internet.", Toast.LENGTH_LONG).show();
                return;
            }

            new AsyncGetUser().execute();
        }
    }

    // GET USER FROM WEB
    public class AsyncGetUser extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            progressDL = ProgressDialog.show(MainActivity.this, "", "Verifying user account....", true);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                String urlfinal = urlGet + "email=" + username + "&pwd=" + password;
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
            catch(UnknownHostException e) {
                Log.e("ERROR", e.getMessage(), e);
                final String ex = e.getMessage();
                progressDL.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Please check or connect wifi with internet connection. " + ex, Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }
            catch(MalformedURLException e) {
                Log.e("ERROR", e.getMessage(), e);
                final String ex = e.getMessage();
                progressDL.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "(MalformedURLException) Invalid URL. " + ex, Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }
            catch(IOException e) {
                Log.e("ERROR", e.getMessage(), e);
                final String ex = e.getMessage();
                progressDL.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "(IOException) Stream not found. " + ex, Toast.LENGTH_LONG).show();
                        //Snackbar.make(mainLayout, "(IOException) Stream not found. " + ex, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                progressDL.dismiss();
                return;
            }

            try {
                JSONObject data = new JSONObject(response);
                String usercode = "";
                String name = "";

                if(!data.isNull("msg")) {
                    progressDL.dismiss();
                    String msg = data.getString("msg");
                    Snackbar.make(mainLayout, msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    return;
                }
                else {
                    usercode = data.getString("id");
                    name = data.getString("name");
                }

                String[] afields = { SQLiteDB.COLUMN_USER_code, SQLiteDB.COLUMN_USER_name };
                String[] avalues = { usercode, name };

                Cursor cursUser = sql.GetDataCursor(SQLiteDB.TABLE_USER, SQLiteDB.COLUMN_USER_code + " = " + usercode);
                cursUser.moveToFirst();

                if(cursUser.getCount() > 0) {
                    progressDL.dismiss();
                    General.usercode = usercode;
                    General.username = name;
                    Intent mainIntent = new Intent(MainActivity.this, Field_main.class);
                    startActivity(mainIntent);
                    finish();
                    return;
                }
                sql.TruncateTable(SQLiteDB.TABLE_USER);

                sql.AddRecord(SQLiteDB.TABLE_USER, afields, avalues);
                General.usercode = usercode;
                General.username = name;

                urlDownload = urlDownload + "id=" + usercode;

                progressDL.dismiss();
                new AsyncDownloadFile().execute();

            }
            catch (JSONException ex) {
                progressDL.dismiss();
                Snackbar.make(mainLayout, ex.getMessage().trim(), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                ex.printStackTrace();
            }
        }
    }

    // DOWNLOADING IMAGE
    public class AsyncDownloadImage extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {

            progressDL = new ProgressDialog(MainActivity.this);
            progressDL.setTitle("");
            progressDL.setMessage("Saving product images...");
            progressDL.setProgressStyle(progressDL.STYLE_SPINNER);
            progressDL.setCancelable(false);

            progressDL.show();
        }/*            Cursor cursAllImage = sql.GetDataCursor(SQLiteDB.TABLE_PICTURES);
            cursAllImage.moveToFirst();
            int imgs = cursAllImage.getCount();
            progressDL.setMax(imgs);*/

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDL.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(Void... params) {

            String saveImageDir = Uri.fromFile(Settings.imgFolder).getPath();

            Cursor cursAllImage = sql.GetDataCursor(SQLiteDB.TABLE_PICTURES);
            cursAllImage.moveToFirst();

            if(cursAllImage.getCount() > 0) {
                try{
                    cursAllImage.moveToFirst();
                    while (!cursAllImage.isAfterLast()) {

                        String productImageName = cursAllImage.getString(cursAllImage.getColumnIndex(SQLiteDB.COLUMN_PICTURES_name)).toLowerCase();

                        // CHECK IF EXISTING
                        File fExisting = new File(Settings.imgFolder, productImageName.toLowerCase().trim().replace("\"",""));
                        if(!fExisting.exists()) {
                            imageProductDIR = new File(Settings.imgFolder, productImageName);
                        }
                        else {
                            cursAllImage.moveToNext();
                            continue;
                        }

                        urlDownloadImage = urlImage + "name=" + productImageName;

                        URL url = new URL(urlDownloadImage);
                        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                        final int responseCode = httpConn.getResponseCode();

                        // always check HTTP response code first
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            String fileName = "";
                            String disposition = httpConn.getHeaderField("Content-Disposition");
                            String contentType = httpConn.getContentType();
                            int contentLength = httpConn.getContentLength();

                            if (disposition != null) {
                                // extracts file name from header field
                                int index = disposition.indexOf("filename=");
                                if (index > 0) {
                                    fileName = disposition.substring(index + 10,
                                            disposition.length() - 1);
                                }
                            } else {
                                // extracts file name from URL
/*                                fileName = urlDownloadImage.substring(urlDownloadImage.lastIndexOf("/") + 1, urlDownloadImage.length());*/
                                fileName = productImageName.toLowerCase();
                            }

                            // opens input stream from the HTTP connection
                            InputStream inputStream = httpConn.getInputStream();
                            String saveFilePath = saveImageDir + File.separator + fileName;

                            // opens an output stream to save into file
                            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                            int bytesRead = -1;
                            byte[] buffer = new byte[BUFFER_SIZE];

                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }

                            outputStream.close();
                            inputStream.close();

                        } else {
                            return "Error in downloading images.\nResponse code: " + String.valueOf(responseCode);
                        }

                        httpConn.disconnect();
                        cursAllImage.moveToNext();
                    }
                    cursAllImage.close();
                }
                catch (FileNotFoundException fex)
                {
                    Log.e("FileNotFoundException", fex.getMessage());
                    fex.printStackTrace();
                    return fex.getMessage();
                }
                catch (MalformedURLException mex)
                {
                    Log.e("MalformedURLException", mex.getMessage());
                    mex.printStackTrace();
                    return mex.getMessage();
                }
                catch (IOException ioex)
                {
                    Log.e("IOException", ioex.getMessage());
                    ioex.printStackTrace();
                    return ioex.getMessage();
                }
            }
            else {
                return "No Image data saved.";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDL.dismiss();
            if(s != null) {
                messageBox.ShowMessage("Image Download", s);
                sql.TruncateTable(SQLiteDB.TABLE_USER);
                return;
            }

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Done");
            alertDialog.setMessage("Saving Downloaded data done.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            wlStayAwake.release();
                            Intent mainIntent = new Intent(MainActivity.this, Field_main.class);
                            startActivity(mainIntent);
                            finish();
/*                                                    Intent mainIntent = new Intent(MainActivity.this, Field_main.class);
                                                    startActivity(mainIntent);*/
                        }
                    });
            alertDialog.show();
        }
    }

    // DOWNLOADING FILE
    public class AsyncDownloadFile extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDL = ProgressDialog.show(MainActivity.this, "", "Downloading data....", true);
        }

        @Override
        protected String doInBackground(Void... params) {

            String saveDir = Uri.fromFile(dlpath).getPath();

            try{
                for (String type : General.ARRAY_FILE_LISTS) {

                    urlDownloadperFile = urlDownload + "&type=" + type;

                    URL url = new URL(urlDownloadperFile);
                    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                    final int responseCode = httpConn.getResponseCode();

                    // always check HTTP response code first
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String fileName = "";
                        String disposition = httpConn.getHeaderField("Content-Disposition");
                        String contentType = httpConn.getContentType();
                        int contentLength = httpConn.getContentLength();

                        if (disposition != null) {
                            // extracts file name from header field
                            int index = disposition.indexOf("filename=");
                            if (index > 0) {
                                fileName = disposition.substring(index + 10,
                                        disposition.length() - 1);
                            }
                        } else {
                            // extracts file name from URL
                            fileName = urlDownload.substring(urlDownload.lastIndexOf("/") + 1,
                                    urlDownload.length());
                        }

                        // opens input stream from the HTTP connection
                        InputStream inputStream = httpConn.getInputStream();
                        String saveFilePath = saveDir + File.separator + fileName;

                        if(type.equals(General.STORE_LIST)) storeDIR = new File(dlpath, fileName);
                        if(type.equals(General.CATEGORY_LIST)) categoryDIR = new File(dlpath, fileName);
                        if(type.equals(General.GROUP_LIST)) groupDIR = new File(dlpath, fileName);
                        if(type.equals(General.QUESTION_LIST)) questionDIR = new File(dlpath, fileName);
                        if(type.equals(General.FORM_LIST)) formsDIR = new File(dlpath, fileName);
                        if(type.equals(General.FORM_TYPES)) formtypesDIR = new File(dlpath, fileName);
                        if(type.equals(General.QUESTION_SINGLESELECT)) singleselectDIR = new File(dlpath, fileName);
                        if(type.equals(General.QUESTION_MULTISELECT)) multiselectDIR = new File(dlpath, fileName);
                        if(type.equals(General.QUESTION_COMPUTATIONAL)) computationalDIR = new File(dlpath, fileName);
                        if(type.equals(General.QUESTION_CONDITIONAL)) conditionalDIR = new File(dlpath, fileName);
                        if(type.equals(General.SECONDARY_LOOKUP_LIST)) secondarylookupDIR = new File(dlpath, fileName);
                        if(type.equals(General.SECONDARY_LIST)) secondarylistDIR = new File(dlpath, fileName);
                        if(type.equals(General.OSA_LIST)) osalistDIR = new File(dlpath, fileName);
                        if(type.equals(General.OSA_LOOKUP)) osalookupDIR = new File(dlpath, fileName);
                        if(type.equals(General.SOS_LIST)) soslistDIR = new File(dlpath, fileName);
                        if(type.equals(General.SOS_LOOKUP)) soslookupDIR = new File(dlpath, fileName);
                        if(type.equals(General.IMG_LIST)) imageListDIR = new File(dlpath, fileName);

                        // opens an output stream to save into file
                        FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                        int bytesRead = -1;
                        byte[] buffer = new byte[BUFFER_SIZE];

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        outputStream.close();
                        inputStream.close();

/*                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageBox.ShowMessage("Download", "Download complete");
                            }
                        });*/

                    } else {
                        return "Error in downloading files.\nResponse code: " + String.valueOf(responseCode);
                    }

                    httpConn.disconnect();
                }
            }
            catch (Exception ex)
            {

                ex.printStackTrace();
                return ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDL.dismiss();
            if(s != null) {
                messageBox.ShowMessage("Error in download", s);
                sql.TruncateTable(SQLiteDB.TABLE_USER);
                return;
            }

            new SaveDownloadedData().execute();
        }
    }


    public class SaveDownloadedData extends AsyncTask<Void, String, Boolean> {

        int nMaxprogress = 0;
        LineNumberReader lnReader;
        SQLiteDatabase dbase;
        String errmsg = "";
        String presentFile = "";

        @Override
        protected void onPreExecute() {
            dbase = sqLiteDB.getWritableDatabase();
            progressDL = new ProgressDialog(MainActivity.this);
            progressDL.setTitle("Loading");
            progressDL.setMessage("Storing downloaded data.. Please wait.");
            progressDL.setProgressStyle(progressDL.STYLE_HORIZONTAL);
            progressDL.setCancelable(false);
            progressDL.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDL.incrementProgressBy(1);
            progressDL.setMessage(values[0]);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;

            if(storeDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(storeDIR));
                    while (lnReader.readLine() != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(categoryDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(categoryDIR));
                    while (lnReader.readLine() != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(groupDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(groupDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(questionDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(questionDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(formsDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(formsDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(formtypesDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(formtypesDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(singleselectDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(singleselectDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(multiselectDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(multiselectDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(computationalDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(computationalDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(conditionalDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(conditionalDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(secondarylookupDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(secondarylookupDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(secondarylistDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(secondarylistDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(osalistDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(osalistDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(osalookupDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(osalookupDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(soslistDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(soslistDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(soslookupDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(soslookupDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }
            if(imageListDIR.exists()) {
                try{
                    lnReader = new LineNumberReader(new FileReader(imageListDIR));
                    while ((lnReader.readLine()) != null) { }
                    nMaxprogress += lnReader.getLineNumber();
                }
                catch (IOException ie) { DebugLog.log(ie.getMessage()); }
            }

            progressDL.setMax(nMaxprogress);

            try {

                // STORES
                if(storeDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_STORE);

                    presentFile = storeDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_STORE_storeid,
                            SQLiteDB.COLUMN_STORE_name,
                            SQLiteDB.COLUMN_STORE_gradematrixid,
                            SQLiteDB.COLUMN_STORE_audittempid,
                            SQLiteDB.COLUMN_STORE_templatename,
                            SQLiteDB.COLUMN_STORE_status,
                            SQLiteDB.COLUMN_STORE_initial,
                            SQLiteDB.COLUMN_STORE_exempt,
                            SQLiteDB.COLUMN_STORE_final,
                            SQLiteDB.COLUMN_STORE_startdate,
                            SQLiteDB.COLUMN_STORE_enddate,
                            SQLiteDB.COLUMN_STORE_storecode,
                            SQLiteDB.COLUMN_STORE_account,
                            SQLiteDB.COLUMN_STORE_customercode,
                            SQLiteDB.COLUMN_STORE_customer,
                            SQLiteDB.COLUMN_STORE_regioncode,
                            SQLiteDB.COLUMN_STORE_region,
                            SQLiteDB.COLUMN_STORE_distributorcode,
                            SQLiteDB.COLUMN_STORE_distributor,
                            SQLiteDB.COLUMN_STORE_templatecode,
                            SQLiteDB.COLUMN_STORE_auditid
                    };

                    String sqlinsertStore = sql.createInsertBulkQuery(SQLiteDB.TABLE_STORE, afields);

                    SQLiteStatement sqlstatementStore = dbase.compileStatement(sqlinsertStore); // insert into tblsample (fields1,fields2)
                    dbase.beginTransaction();

                    BufferedReader bReader = new BufferedReader(new FileReader(storeDIR));

                    String line;

                    line = bReader.readLine();

                    while ((line = bReader.readLine()) != null) {
                        final String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                        sqlstatementStore.clearBindings();
                        for (int i = 0; i < afields.length; i++) {
                            sqlstatementStore.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementStore.execute();

                        publishProgress("Saving Store data.." + values[1].trim());
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }


                // CATEGORY
                if(categoryDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_CATEGORY);

                    presentFile = categoryDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_CATEGORY_id,
                            SQLiteDB.COLUMN_CATEGORY_audittempid,
                            SQLiteDB.COLUMN_CATEGORY_categoryorder,
                            SQLiteDB.COLUMN_CATEGORY_categoryid,
                            SQLiteDB.COLUMN_CATEGORY_categorydesc
                    };

                    String sqlinsertCategory = sql.createInsertBulkQuery(SQLiteDB.TABLE_CATEGORY, afields);

                    SQLiteStatement sqlstatementCategory = dbase.compileStatement(sqlinsertCategory); // insert into tblsample (fields1,fields2)
                    dbase.beginTransaction();

                    BufferedReader bReader = new BufferedReader(new FileReader(categoryDIR));

                    String line;

                    line = bReader.readLine();

                    while ((line = bReader.readLine()) != null) {
                        final String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                        sqlstatementCategory.clearBindings();
                        for (int i = 0; i < afields.length; i++) {
                            sqlstatementCategory.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementCategory.execute();

                        publishProgress("Saving Category data.." + values[1].trim());
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }


                // GROUP
                if(groupDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_GROUP);

                    presentFile = groupDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_GROUP_id,
                            SQLiteDB.COLUMN_GROUP_audittempid,
                            SQLiteDB.COLUMN_GROUP_categoryid,
                            SQLiteDB.COLUMN_GROUP_grouporder,
                            SQLiteDB.COLUMN_GROUP_groupid,
                            SQLiteDB.COLUMN_GROUP_groupdesc
                    };

                    String sqlinsertGroup = sql.createInsertBulkQuery(SQLiteDB.TABLE_GROUP, afields);

                    SQLiteStatement sqlstatementGroup = dbase.compileStatement(sqlinsertGroup); // insert into tblsample (fields1,fields2)
                    dbase.beginTransaction();

                    BufferedReader bReader = new BufferedReader(new FileReader(groupDIR));

                    String line;

                    line = bReader.readLine();

                    while ((line = bReader.readLine()) != null) {
                        final String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                        sqlstatementGroup.clearBindings();
                        for (int i = 0; i < afields.length; i++) {
                            sqlstatementGroup.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementGroup.execute();

                        publishProgress("Saving Group data.." + values[1].trim());
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }

                // QUESTIONS
                if(questionDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_QUESTION);

                    presentFile = questionDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_QUESTION_questionid,
                            SQLiteDB.COLUMN_QUESTION_order,
                            SQLiteDB.COLUMN_QUESTION_groupid,
                            SQLiteDB.COLUMN_QUESTION_audittempid,
                            SQLiteDB.COLUMN_QUESTION_formid,
                            SQLiteDB.COLUMN_QUESTION_formtypeid,
                            SQLiteDB.COLUMN_QUESTION_prompt,
                            SQLiteDB.COLUMN_QUESTION_required,
                            SQLiteDB.COLUMN_QUESTION_expectedans,
                            SQLiteDB.COLUMN_QUESTION_exempt,
                            SQLiteDB.COLUMN_QUESTION_brandpic,
                            SQLiteDB.COLUMN_QUESTION_defaultans
                    };

                    String sqlinsertQuestions = sql.createInsertBulkQuery(SQLiteDB.TABLE_QUESTION, afields);
                    SQLiteStatement sqlstatementQuestions = dbase.compileStatement(sqlinsertQuestions);
                    dbase.beginTransaction();

                    BufferedReader bReader = new BufferedReader(new FileReader(questionDIR));

                    String line;

                    line = bReader.readLine();

                    while ((line = bReader.readLine()) != null) {
                        final String[] valuesquestion = line.trim().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); // split with comma delimeter, not including inside the ""

                        sqlstatementQuestions.clearBindings();
                        for (int i = 0; i < valuesquestion.length; i++) {
                            sqlstatementQuestions.bindString((i + 1), valuesquestion[i].trim().replace("\"", ""));
                        }
                        sqlstatementQuestions.execute();

                        //sql.AddRecord(SQLiteDB.TABLE_QUESTION, afields, avalues);

                        publishProgress("Saving questions data.. " + valuesquestion[4]);
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }

                // FORMS
                if(formsDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_FORMS);

                    presentFile = formsDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_FORMS_formid,
                            SQLiteDB.COLUMN_FORMS_audittempid,
                            SQLiteDB.COLUMN_FORMS_typeid,
                            SQLiteDB.COLUMN_FORMS_prompt,
                            SQLiteDB.COLUMN_FORMS_required,
                            SQLiteDB.COLUMN_FORMS_expected,
                            SQLiteDB.COLUMN_FORMS_exempt,
                            SQLiteDB.COLUMN_FORMS_picture,
                            SQLiteDB.COLUMN_FORMS_defaultans
                    };

                    String sqlinsertForms = sql.createInsertBulkQuery(SQLiteDB.TABLE_FORMS, afields);
                    SQLiteStatement sqlstatementForms = dbase.compileStatement(sqlinsertForms);
                    dbase.beginTransaction();

                    BufferedReader brForms = new BufferedReader(new FileReader(formsDIR));

                    String line;

                    line = brForms.readLine();

                    while ((line = brForms.readLine()) != null) {
                        String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                        sqlstatementForms.clearBindings();
                        for (int i = 0; i < values.length; i++) {
                            sqlstatementForms.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementForms.execute();

                        publishProgress("Saving forms data..");
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }


                // FORM TYPES
                if(formtypesDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_FORMTYPE);

                    presentFile = formtypesDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_FORMTYPE_code,
                            SQLiteDB.COLUMN_FORMTYPE_desc
                    };

                    String sqlinsertFormtypes = sql.createInsertBulkQuery(SQLiteDB.TABLE_FORMTYPE, afields);
                    SQLiteStatement sqlstatementFormtypes = dbase.compileStatement(sqlinsertFormtypes);
                    dbase.beginTransaction();

                    BufferedReader brFormtypes = new BufferedReader(new FileReader(formtypesDIR));

                    String line;

                    line = brFormtypes.readLine();

                    while ((line = brFormtypes.readLine()) != null) {
                        final String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                        sqlstatementFormtypes.clearBindings();
                        for (int i = 0; i < values.length; i++) {
                            sqlstatementFormtypes.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementFormtypes.execute();

                        publishProgress("Saving form types..");
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }

                // SINGLE SELECT
                if(singleselectDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_SINGLESELECT);

                    presentFile = singleselectDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_SINGLESELECT_formid,
                            SQLiteDB.COLUMN_SINGLESELECT_optionid,
                            SQLiteDB.COLUMN_SINGLESELECT_option
                    };

                    String sqlinsertSingle = sql.createInsertBulkQuery(SQLiteDB.TABLE_SINGLESELECT, afields);
                    SQLiteStatement sqlstatementSingle = dbase.compileStatement(sqlinsertSingle);
                    dbase.beginTransaction();

                    BufferedReader bSingleselect = new BufferedReader(new FileReader(singleselectDIR));
                    String line;

                    line = bSingleselect.readLine();

                    while ((line = bSingleselect.readLine()) != null) {
                        final String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                        sqlstatementSingle.clearBindings();
                        for (int i = 0; i < values.length; i++)
                        {
                            sqlstatementSingle.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementSingle.execute();

                        publishProgress("Saving single select data..");
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }

                // MULTI SELECT
                if(multiselectDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_MULTISELECT);

                    presentFile = multiselectDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_MULTISELECT_formid,
                            SQLiteDB.COLUMN_MULTISELECT_optionid,
                            SQLiteDB.COLUMN_MULTISELECT_option
                    };

                    String sqlinsertMulti = sql.createInsertBulkQuery(SQLiteDB.TABLE_MULTISELECT, afields);
                    SQLiteStatement sqlstatementMulti = dbase.compileStatement(sqlinsertMulti);
                    dbase.beginTransaction();

                    BufferedReader brMultiSelect = new BufferedReader(new FileReader(multiselectDIR));
                    String line;
                    line = brMultiSelect.readLine();

                    while ((line = brMultiSelect.readLine()) != null) {
                        final String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                        sqlstatementMulti.clearBindings();
                        for (int i = 0; i < values.length; i++) {
                            sqlstatementMulti.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementMulti.execute();

                        publishProgress("Saving multi select data..");
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }

                // COMPUTATIONAL
                if(computationalDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_COMPUTATIONAL);

                    presentFile = computationalDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_COMPUTATIONAL_formid,
                            SQLiteDB.COLUMN_COMPUTATIONAL_formula
                    };

                    String sqlinsertComp = sql.createInsertBulkQuery(SQLiteDB.TABLE_COMPUTATIONAL, afields);
                    SQLiteStatement sqlstatementComp = dbase.compileStatement(sqlinsertComp);
                    dbase.beginTransaction();

                    BufferedReader brComputational = new BufferedReader(new FileReader(computationalDIR));
                    String line;
                    line = brComputational.readLine();

                    while ((line = brComputational.readLine()) != null) {
                        final String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                        sqlstatementComp.clearBindings();
                        for (int i = 0; i < values.length; i++) {
                            sqlstatementComp.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementComp.execute();

                        publishProgress("Saving computational data..");
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }

                // CONDITIONAL
                if(conditionalDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_CONDITIONAL);

                    presentFile = conditionalDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_CONDITIONAL_formid,
                            SQLiteDB.COLUMN_CONDITIONAL_condition,
                            SQLiteDB.COLUMN_CONDITIONAL_conditionformsid,
                            SQLiteDB.COLUMN_CONDITIONAL_optionid
                    };

                    String sqlinsertCond = sql.createInsertBulkQuery(SQLiteDB.TABLE_CONDITIONAL, afields);
                    SQLiteStatement sqlstatementCond = dbase.compileStatement(sqlinsertCond);
                    dbase.beginTransaction();

                    BufferedReader brConditional = new BufferedReader(new FileReader(conditionalDIR));
                    String line;
                    line = brConditional.readLine();

                    while ((line = brConditional.readLine()) != null) {
                        final String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                        sqlstatementCond.clearBindings();
                        for (int i = 0; i < values.length; i++) {
                            sqlstatementCond.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementCond.execute();

                        publishProgress("Saving conditional data..");
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }


                // SECONDARY KEY LIST
                if(secondarylistDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_SECONDARYKEYLIST);

                    presentFile = secondarylistDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_SECONDARYKEYLIST_keygroupid
                    };

                    String sqlinsertKeyList = sql.createInsertBulkQuery(SQLiteDB.TABLE_SECONDARYKEYLIST, afields);
                    SQLiteStatement sqlstatementKeyList = dbase.compileStatement(sqlinsertKeyList); // insert into tblsample (fields1,fields2)
                    dbase.beginTransaction();

                    BufferedReader bReader = new BufferedReader(new FileReader(secondarylistDIR));

                    String line;

                    line = bReader.readLine();

                    while ((line = bReader.readLine()) != null) {
                        final String[] values = line.split(",");


                        sqlstatementKeyList.clearBindings();
                        for (int i = 0; i < afields.length; i++) {
                            sqlstatementKeyList.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementKeyList.execute();

                        publishProgress("Saving Secondary Keylist data..");
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }

                // SECONDARY DISPLAY
                if(secondarylookupDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_SECONDARYDISP);

                    presentFile = secondarylookupDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_SECONDARYDISP_storeid,
                            SQLiteDB.COLUMN_SECONDARYDISP_categoryid,
                            SQLiteDB.COLUMN_SECONDARYDISP_brand
                    };

                    String sqlinsertSecDisp = sql.createInsertBulkQuery(SQLiteDB.TABLE_SECONDARYDISP, afields);
                    SQLiteStatement sqlstatementSecDisp = dbase.compileStatement(sqlinsertSecDisp); // insert into tblsample (fields1,fields2)
                    dbase.beginTransaction();

                    BufferedReader bReader = new BufferedReader(new FileReader(secondarylookupDIR));

                    String line;

                    line = bReader.readLine();

                    while ((line = bReader.readLine()) != null) {
                        final String[] values = line.split(",");


                        sqlstatementSecDisp.clearBindings();
                        for (int i = 0; i < afields.length; i++) {
                            sqlstatementSecDisp.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementSecDisp.execute();

                        publishProgress("Saving Secondary display data..");
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }


                // OSA LIST
                if(osalistDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_OSALIST);

                    presentFile = osalistDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_OSALIST_osakeygroupid
                    };

                    String sqlinsertOsalist = sql.createInsertBulkQuery(SQLiteDB.TABLE_OSALIST, afields);
                    SQLiteStatement sqlstatementOsalist = dbase.compileStatement(sqlinsertOsalist); // insert into tblsample (fields1,fields2)
                    dbase.beginTransaction();

                    BufferedReader bReader = new BufferedReader(new FileReader(osalistDIR));

                    String line;

                    line = bReader.readLine();

                    while ((line = bReader.readLine()) != null) {
                        final String[] values = line.split(",");

                        sqlstatementOsalist.clearBindings();
                        for (int i = 0; i < afields.length; i++) {
                            sqlstatementOsalist.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementOsalist.execute();

                        publishProgress("Saving OSA Lists data..");
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }


                // OSA LOOKUP
                if(osalookupDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_OSALOOKUP);

                    presentFile = osalookupDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_OSALOOKUP_storeid,
                            SQLiteDB.COLUMN_OSALOOKUP_categoryid,
                            SQLiteDB.COLUMN_OSALOOKUP_target,
                            SQLiteDB.COLUMN_OSALOOKUP_total,
                            SQLiteDB.COLUMN_OSALOOKUP_lookupid
                    };

                    String sqlinsertOsalookup = sql.createInsertBulkQuery(SQLiteDB.TABLE_OSALOOKUP, afields);
                    SQLiteStatement sqlstatementOsalookup = dbase.compileStatement(sqlinsertOsalookup); // insert into tblsample (fields1,fields2)
                    dbase.beginTransaction();

                    BufferedReader bReader = new BufferedReader(new FileReader(osalookupDIR));

                    String line;

                    line = bReader.readLine();

                    while ((line = bReader.readLine()) != null) {
                        final String[] values = line.split(",");


                        sqlstatementOsalookup.clearBindings();
                        for (int i = 0; i < afields.length; i++) {
                            sqlstatementOsalookup.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementOsalookup.execute();

                        publishProgress("Saving OSA lookup data..");
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }


                // SOS LIST
                if(soslistDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_SOSLIST);

                    presentFile = soslistDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_SOSLIST_soskeygroupid
                    };

                    String sqlinsertSoslist = sql.createInsertBulkQuery(SQLiteDB.TABLE_SOSLIST, afields);
                    SQLiteStatement sqlstatementSoslist = dbase.compileStatement(sqlinsertSoslist); // insert into tblsample (fields1,fields2)
                    dbase.beginTransaction();

                    BufferedReader bReader = new BufferedReader(new FileReader(soslistDIR));

                    String line;

                    line = bReader.readLine();

                    while ((line = bReader.readLine()) != null) {
                        final String[] values = line.split(",");

                        sqlstatementSoslist.clearBindings();
                        for (int i = 0; i < afields.length; i++) {
                            sqlstatementSoslist.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementSoslist.execute();

                        publishProgress("Saving SOS Lists data..");
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }


                // SOS LOOKUP
                if(soslookupDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_SOSLOOKUP);

                    presentFile = soslookupDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_SOSLOOKUP_storeid,
                            SQLiteDB.COLUMN_SOSLOOKUP_categoryid,
                            SQLiteDB.COLUMN_SOSLOOKUP_sosid,
                            SQLiteDB.COLUMN_SOSLOOKUP_less,
                            SQLiteDB.COLUMN_SOSLOOKUP_value,
                            SQLiteDB.COLUMN_SOSLOOKUP_lookupid
                    };

                    String sqlinsertSoslookup = sql.createInsertBulkQuery(SQLiteDB.TABLE_SOSLOOKUP, afields);
                    SQLiteStatement sqlstatementSoslookup = dbase.compileStatement(sqlinsertSoslookup); // insert into tblsample (fields1,fields2)
                    dbase.beginTransaction();

                    BufferedReader bReader = new BufferedReader(new FileReader(soslookupDIR));

                    String line;

                    line = bReader.readLine();

                    while ((line = bReader.readLine()) != null) {
                        final String[] values = line.split(",");


                        sqlstatementSoslookup.clearBindings();
                        for (int i = 0; i < afields.length; i++) {
                            sqlstatementSoslookup.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementSoslookup.execute();

                        publishProgress("Saving SOS lookup data..");
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }

                // IMAGE LISTS
                if(imageListDIR.exists()) {
                    sql.TruncateTable(SQLiteDB.TABLE_PICTURES);

                    presentFile = imageListDIR.getPath();

                    String[] afields = {
                            SQLiteDB.COLUMN_PICTURES_name
                    };

                    String sqlinsertPictures = sql.createInsertBulkQuery(SQLiteDB.TABLE_PICTURES, afields);
                    SQLiteStatement sqlstatementPictures = dbase.compileStatement(sqlinsertPictures); // insert into tblsample (fields1,fields2)
                    dbase.beginTransaction();

                    BufferedReader bReader = new BufferedReader(new FileReader(imageListDIR));

                    String line;

                    line = bReader.readLine();

                    while ((line = bReader.readLine()) != null) {
                        final String[] values = line.split(",");

                        sqlstatementPictures.clearBindings();
                        for (int i = 0; i < afields.length; i++) {
                            sqlstatementPictures.bindString((i+1), values[i].trim().replace("\"",""));
                        }
                        sqlstatementPictures.execute();

                        publishProgress("Saving brand images data..");
                    }
                    dbase.setTransactionSuccessful();
                    dbase.endTransaction();
                }

                result = true;
            }
            catch (FileNotFoundException fex)
            {
                fex.printStackTrace();
                Log.e("Exception", fex.getMessage());
                errmsg = fex.getMessage() + ", file not found.\nFILE: " + presentFile;
                dbase.setTransactionSuccessful();
                dbase.endTransaction();
            }
            catch (IOException iex) {
                iex.printStackTrace();
                Log.e("Exception", iex.getMessage());
                errmsg = iex.getMessage() + "\nFILE: " + presentFile;
                dbase.setTransactionSuccessful();
                dbase.endTransaction();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                Log.e("Exception", ex.getMessage());
                errmsg = ex.getMessage() + ", Some files are corrupted.\nFILE: " + presentFile;
                dbase.setTransactionSuccessful();
                dbase.endTransaction();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean bResult) {
            progressDL.dismiss();
            alertDialog = new AlertDialog.Builder(MainActivity.this).create();

            // CLOSE DATABASE CONN
            if(dbase.isOpen()) dbase.close();

            if(!bResult) {
                alertDialog.setTitle("Exception error");
                alertDialog.setMessage(errmsg);
                alertDialog.setCancelable(false);
                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sql.TruncateTable(SQLiteDB.TABLE_USER);
                        finish();
                    }
                });
                alertDialog.show();
                return;
            }

            alertDialog.setTitle("Done");
            alertDialog.setMessage("Saving Downloaded data done.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            wlStayAwake.release();
                            Intent mainIntent = new Intent(MainActivity.this, Field_main.class);
                            startActivity(mainIntent);
                            finish();
/*                                                    Intent mainIntent = new Intent(MainActivity.this, Field_main.class);
                                                    startActivity(mainIntent);*/
                        }
                    });
            alertDialog.show();
        }
    }
}
