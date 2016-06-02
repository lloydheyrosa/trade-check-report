package com.android.pplusaudit2.Report;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.pplusaudit2.ErrorLogs.AutoErrorLog;
import com.android.pplusaudit2.General;
import com.android.pplusaudit2.R;
import com.android.pplusaudit2.Report.AuditSummary.AuditAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ReportStoreActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    ArrayList<StoreReport> arrStoreReports;
    long selectedAuditID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_store_activity);
        Thread.setDefaultUncaughtExceptionHandler(new AutoErrorLog(this, General.errlogFile));

        String title = "STORE REPORT";
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        overridePendingTransition(R.anim.slide_up, R.anim.hold);

        arrStoreReports = new ArrayList<>();
        final Spinner spnAudit = (Spinner) findViewById(R.id.spnAudit);
        Button btnProcess = (Button) findViewById(R.id.btnProcess);

        AuditAdapter dataAdapter = new AuditAdapter(ReportStoreActivity.this, android.R.layout.simple_dropdown_item_1line, General.arraylistAudits);
        spnAudit.setAdapter(dataAdapter);

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedAuditID = spnAudit.getSelectedItemId();
                new CheckInternet().execute();
            }
        });
    }

    public class CheckInternet extends AsyncTask<Void, Void, Boolean> {
        String errmsg = "";

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ReportStoreActivity.this, "", "Checking internet connection.");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if(activeNetwork != null) {
                if(activeNetwork.isFailover()) errmsg = "Internet connection fail over.";
                result = activeNetwork.isAvailable() || activeNetwork.isConnectedOrConnecting();
            }
            else errmsg = "Not connected to the internet.";

            return result;
        }

        @Override
        protected void onPostExecute(Boolean bResult) {
            progressDialog.dismiss();
            if(!bResult) {
                Toast.makeText(ReportStoreActivity.this, errmsg, Toast.LENGTH_SHORT).show();
                return;
            }

            new GetStoreReports().execute();
        }
    }

    public class GetStoreReports extends AsyncTask<Void, Void, Boolean> {
        String errormsg = "";
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ReportStoreActivity.this, "", "Processing Report.");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;

            String response = "";
            arrStoreReports.clear();

            try {
                URL url = new URL(General.URL_REPORT_STORESUMMARY + "/" + selectedAuditID + "/user/" + General.usercode);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                urlConnection.disconnect();
                response = stringBuilder.toString();

                if(response.trim().contains("No Report Available")) {
                    String msg = new JSONObject(response).getString("msg");
                    errormsg = msg;
                    arrStoreReports.clear();
                    return result;
                }

                if (!response.trim().equals("")) {

                    JSONArray dataArray = new JSONArray(response);

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) dataArray.get(i);

                        StoreReport storeReport = new StoreReport();
                        storeReport.ID = jsonObject.getInt("id");
                        storeReport.userID = jsonObject.getInt("user_id");
                        storeReport.auditID = jsonObject.getInt("audit_id");
                        storeReport.account = jsonObject.getString("account");
                        storeReport.customerCode = jsonObject.getString("customer_code");
                        storeReport.customer = jsonObject.getString("customer");
                        storeReport.area = jsonObject.getString("area");
                        storeReport.regionCode = jsonObject.getString("region");
                        storeReport.storeName = jsonObject.getString("store_name");
                        storeReport.perfectStore = jsonObject.getDouble("perfect_store");
                        storeReport.osa = jsonObject.getDouble("osa");
                        storeReport.npi = jsonObject.getDouble("npi");
                        storeReport.planogram = jsonObject.getDouble("planogram");
                        storeReport.updateAt = jsonObject.getString("updated_at");
                        storeReport.auditName = jsonObject.getString("audit_name");

                        arrStoreReports.add(storeReport);
                    }

                    result = true;
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
                Log.e("IO Error", ex.getMessage());
                errormsg = ex.getLocalizedMessage();
            }
            catch (JSONException ex) {
                ex.printStackTrace();
                Log.e("IO Error", ex.getMessage());
                errormsg = ex.getLocalizedMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean bResult) {
            progressDialog.dismiss();
            if(!bResult) {
                Toast.makeText(ReportStoreActivity.this, errormsg, Toast.LENGTH_LONG).show();
            }

            ListView lvwStoreReport = (ListView) findViewById(R.id.lvwStoreReport);
            ReportStoreAdapter storeAdapter = new ReportStoreAdapter(ReportStoreActivity.this, arrStoreReports);
            lvwStoreReport.setAdapter(storeAdapter);
            storeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
