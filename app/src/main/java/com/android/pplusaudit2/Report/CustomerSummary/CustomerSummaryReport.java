package com.android.pplusaudit2.Report.CustomerSummary;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.pplusaudit2.General;
import com.android.pplusaudit2.R;
import com.android.pplusaudit2.Report.AuditSummary.AuditAdapter;
import com.android.pplusaudit2.Report.StoreSummary.StoreItem;
import com.android.pplusaudit2.Report.StoreSummary.ReportStoreAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CustomerSummaryReport extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private ArrayList<Customer> arrCustomerItems;
    private ArrayList<Customer> arrCustomerLoader;
    private ArrayList<CustomerSummaryItem> arrCustSummarySubItems;
    private long selectedAuditID;
    private CustomerAdapter customersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_summary_report_activity);

        String title = "CUSTOMER SUMMARY REPORT";
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);

        arrCustomerItems = new ArrayList<>();
        arrCustomerLoader = new ArrayList<>();
        arrCustSummarySubItems = new ArrayList<>();

        final Spinner spnAudit = (Spinner) findViewById(R.id.spnAudit);
        Button btnProcess = (Button) findViewById(R.id.btnProcess);

        AuditAdapter dataAdapter = new AuditAdapter(CustomerSummaryReport.this, android.R.layout.simple_dropdown_item_1line, General.arraylistAudits);
        spnAudit.setAdapter(dataAdapter);

        ListView lvwCustomers = (ListView) findViewById(R.id.lvwCustomers);
        customersAdapter = new CustomerAdapter(CustomerSummaryReport.this, arrCustomerItems);
        lvwCustomers.setAdapter(customersAdapter);

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedAuditID = spnAudit.getSelectedItemId();
                new CheckInternet().execute();
            }
        });
    }

    private class CheckInternet extends AsyncTask<Void, Void, Boolean> {
        String errmsg = "";

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CustomerSummaryReport.this, "", "Checking internet connection.");
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
                Toast.makeText(CustomerSummaryReport.this, errmsg, Toast.LENGTH_SHORT).show();
                return;
            }

            new FetchCustomerSummaryReport().execute();
        }
    }

    private class FetchCustomerSummaryReport extends AsyncTask<Void, Void, Boolean> {
        String errormsg = "";

        @Override
        protected void onPreExecute() {
            arrCustomerLoader.clear();
            progressDialog = ProgressDialog.show(CustomerSummaryReport.this, "", "Fetching Customer Summary report. Please wait.");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;

            String response = "";

            try {

                URL url = new URL(General.URL_REPORT_CUSTOMER_SUMMARY + "/" + selectedAuditID + "/user/" + General.usercode);
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

                if(response.trim().contains("No reports found.")) {
                    errormsg = new JSONObject(response).getString("msg");
                    return false;
                }

                if (!response.trim().equals("")) {

                    JSONArray dataArray = new JSONArray(response);

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) dataArray.get(i);

                        Customer customer = new Customer(i);

                        arrCustomerLoader.add(customer);
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
                Toast.makeText(CustomerSummaryReport.this, errormsg, Toast.LENGTH_LONG).show();
                return;
            }

            new FetchCustomerSummarySubReport().execute();
        }
    }

    private class FetchCustomerSummarySubReport extends AsyncTask<Void, Void, Boolean> {

        String errormsg = "";

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CustomerSummaryReport.this, "", "Fetching Customer Summary report. Please wait.");
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            boolean result = false;
            String response = "";

            try {

                for (Customer customer : arrCustomerItems) {

                    arrCustSummarySubItems.clear();

                    URL url = new URL(General.URL_REPORT_CUSTOMER_SUMMARY
                            + "/" + customer.customerCode
                            + "/region/" + customer.regionCode
                            + "/template/" + customer.channelCode
                            + "/audit/" + selectedAuditID
                            + "/user/" + General.usercode);

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

                    if(response.trim().contains("No reports found.")) {
                        errormsg = new JSONObject(response).getString("msg");
                        return false;
                    }

                    if (!response.trim().equals("")) {
                        JSONArray dataArray = new JSONArray(response);

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) dataArray.get(i);

                            CustomerSummaryItem customerSummaryItem = new CustomerSummaryItem(i);
                            customerSummaryItem.customer = customer;

                            arrCustSummarySubItems.add(customerSummaryItem);
                        }

                        customer.customerSummaryItems.addAll(arrCustSummarySubItems);
                        result = true;
                    }
                    else errormsg = "Web response error.";
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
                Toast.makeText(CustomerSummaryReport.this, errormsg, Toast.LENGTH_LONG).show();
            }

            arrCustomerItems.clear();
            arrCustomerItems.addAll(arrCustomerLoader);
            customersAdapter.notifyDataSetChanged();
        }
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
