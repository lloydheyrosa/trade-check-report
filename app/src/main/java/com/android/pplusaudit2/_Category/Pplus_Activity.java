package com.android.pplusaudit2._Category;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pplusaudit2.Database.SQLLibrary;
import com.android.pplusaudit2.Database.SQLiteDB;
import com.android.pplusaudit2.General;
import com.android.pplusaudit2.HttpUtility.HttpUtility;
import com.android.pplusaudit2.MyMessageBox;
import com.android.pplusaudit2.R;
import com.android.pplusaudit2.TCRLib;
import com.android.pplusaudit2._Group.Pplus_Group;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ULTRABOOK on 9/22/2015.
 */
public class Pplus_Activity extends AppCompatActivity {

    private ArrayList<Pplus_ActivityClass> arrActivityList = new ArrayList<Pplus_ActivityClass>();

    File appfolder;
    File dlpath;

    String urlDownload = "http://tcr.chasetech.com/api/download?id=1&type=1";

    HttpUtility httpDownload;
    MyMessageBox messageBox;
    SQLLibrary sqlLibrary;
    SQLiteDB sqLiteDB;
    TCRLib tcrLib;

    private static final int BUFFER_SIZE = 4096;
    private ProgressDialog progressDL;

    String storeid = "";
    String storename;
    int nGradeMatrix;

    General.SCORE_STATUS scoreStatus;

    ListView lvwActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pplus_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        overridePendingTransition(R.anim.slide_in_left, R.anim.hold);

        httpDownload = new HttpUtility(this);
        messageBox = new MyMessageBox(this);
        sqlLibrary = new SQLLibrary(this);
        sqLiteDB = new SQLiteDB(this);
        tcrLib = new TCRLib(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            storename = extras.getString("STORE_NAME");
            storeid = extras.getString("STORE_ID");
            nGradeMatrix = extras.getInt("GRADE_MATRIX");
            getSupportActionBar().setTitle(storename.toUpperCase());
        }

        lvwActivity = (ListView) findViewById(R.id.lvwActivity);
    }

    /*    public class AsyncDownloadFile extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDL = ProgressDialog.show(Pplus_Activity.this, "", "Downloading File ....", true);
        }

        @Override
        protected String doInBackground(Void... params) {

            String saveDir = Uri.fromFile(dlpath).getPath();

            try{
                URL url = new URL(urlDownload);
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

                    // opens an output stream to save into file
                    FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                    int bytesRead = -1;
                    byte[] buffer = new byte[BUFFER_SIZE];

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageBox.ShowMessage("Download", "Download complete");
                        }
                    });

                    return contentType;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageBox.ShowMessage("Failed" , "Reponse: " + responseCode);
                        }
                    });
                }
                httpConn.disconnect();
            }
            catch (Exception ex) { ex.printStackTrace(); }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDL.dismiss();
        }
    }*/

    // LOAD ACTIVITY SCORES
    public class AsyncLoadActivities extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDL = ProgressDialog.show(Pplus_Activity.this, "", "Getting Activity scores...");
            arrActivityList.clear();
        }

        @Override
        protected String doInBackground(Void... params) {

            /*Cursor cursStoreCategory = sqlLibrary.RawQuerySelect("SELECT * FROM tblstorecategory join tblcategory on tblcategory.id = tblstorecategory.category_id where store_id = 93 order by categoryorder");*/

            //int a = cursStoreCategory.getCount();

            Cursor cursStoreCategory = sqlLibrary.RawQuerySelect("SELECT " + SQLiteDB.TABLE_STORECATEGORY + "." + SQLiteDB.COLUMN_STORECATEGORY_id + "," + SQLiteDB.COLUMN_CATEGORY_categoryorder
                    + "," + SQLiteDB.COLUMN_CATEGORY_categorydesc + "," + SQLiteDB.TABLE_STORECATEGORY + "." + SQLiteDB.COLUMN_STORECATEGORY_categoryid
                    + "," + SQLiteDB.COLUMN_STORECATEGORY_final + "," + SQLiteDB.COLUMN_STORECATEGORY_status
                    + " FROM " + SQLiteDB.TABLE_STORECATEGORY
                    + " JOIN " + SQLiteDB.TABLE_CATEGORY + " ON " + SQLiteDB.TABLE_CATEGORY + "." + SQLiteDB.COLUMN_CATEGORY_id + " = " + SQLiteDB.TABLE_STORECATEGORY + "." + SQLiteDB.COLUMN_STORECATEGORY_categoryid
                    + " WHERE " + SQLiteDB.COLUMN_STORECATEGORY_storeid + " = " + General.storeid
                    + " ORDER BY " + SQLiteDB.COLUMN_CATEGORY_categoryorder);


            //Cursor cursQuestions = null;

            cursStoreCategory.moveToFirst();

            while (!cursStoreCategory.isAfterLast()) {
                String storecategid = cursStoreCategory.getString(cursStoreCategory.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORY_id));

                String categFinal = cursStoreCategory.getString(cursStoreCategory.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORY_final));
                String categStatusno = cursStoreCategory.getString(cursStoreCategory.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORY_status));
                String strCategStatus = "";

                strCategStatus = tcrLib.GetStatus(categStatusno);
                scoreStatus = tcrLib.GetScoreStatus(categFinal);

                int categorder = cursStoreCategory.getInt(cursStoreCategory.getColumnIndex(SQLiteDB.COLUMN_CATEGORY_categoryorder));
                String category = cursStoreCategory.getString(cursStoreCategory.getColumnIndex(SQLiteDB.COLUMN_CATEGORY_categorydesc)).trim().replace("\"", "");
                int tempCategoryid = cursStoreCategory.getInt(cursStoreCategory.getColumnIndex(SQLiteDB.COLUMN_CATEGORY_categoryid));

                arrActivityList.add(new Pplus_ActivityClass(categorder, tempCategoryid, category.trim().toUpperCase(), storecategid, strCategStatus, scoreStatus));
                cursStoreCategory.moveToNext();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            Pplus_ActivityAdapter adapter = new Pplus_ActivityAdapter(Pplus_Activity.this, arrActivityList);
            lvwActivity.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            lvwActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    TextView tvwCateg = (TextView) view.findViewById(R.id.tvwActivity);
                    String storecategoryid = String.valueOf(tvwCateg.getTag().toString());

                    //Cursor cursGetcategid = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_STORECATEGORY, SQLiteDB.COLUMN_STORECATEGORY_id + " = '" + storecategoryid + "'");
                    Cursor cursGetcategid = sqlLibrary.RawQuerySelect("SELECT * FROM " + SQLiteDB.TABLE_STORECATEGORY
                                                                + " JOIN " + SQLiteDB.TABLE_CATEGORY + " ON " + SQLiteDB.TABLE_CATEGORY + "." + SQLiteDB.COLUMN_CATEGORY_id + " = " + SQLiteDB.TABLE_STORECATEGORY + "." + SQLiteDB.COLUMN_STORECATEGORY_categoryid
                                                                + " WHERE " + SQLiteDB.TABLE_STORECATEGORY + "." + SQLiteDB.COLUMN_STORECATEGORY_id + " = " + storecategoryid);
                    cursGetcategid.moveToFirst();

                    String categoryid = "";
                    if(cursGetcategid.getCount() > 0) categoryid = cursGetcategid.getString(cursGetcategid.getColumnIndex(SQLiteDB.COLUMN_CATEGORY_categoryid));

                    Intent intentActivity = new Intent(Pplus_Activity.this, Pplus_Group.class);
                    intentActivity.putExtra("STORE_CATEGORY_ID", storecategoryid);
                    intentActivity.putExtra("CATEGORY_ID", categoryid);
                    intentActivity.putExtra("CATEGORY_NAME", String.valueOf(tvwCateg.getText()));
                    startActivity(intentActivity);
                }
            });
            progressDL.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //new AsyncLoadActivities().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new AsyncLoadActivities().execute();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_in_right);
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

        if(id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.hold, R.anim.slide_in_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
