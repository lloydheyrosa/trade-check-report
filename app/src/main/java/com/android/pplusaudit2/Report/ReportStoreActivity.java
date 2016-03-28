package com.android.pplusaudit2.Report;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.pplusaudit2.General;
import com.android.pplusaudit2.R;

public class ReportStoreActivity extends AppCompatActivity {

    private TableLayout tblStore;
    LinearLayout lnrInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_store_activity);

        String title = "STORE REPORT";
        getSupportActionBar().setTitle(title);
        overridePendingTransition(R.anim.slide_up, R.anim.hold);

        tblStore = (TableLayout) findViewById(R.id.tblStoreReport);
        tblStore.removeAllViews();

        String[] aItems = null;

        aItems = new String[] {
                "SSM FAIRVIEW,Thursday March 17 2016,No,99,96,65",
                "SVI FAIRVIEW,Thursday March 17 2016,Yes,99,74,91",
                "SMCO ZABARTE,Tuesday March 8 2016,No,99,41,87",
                "SVI NORTH EDSA,Thursday March 3 2016,No,97,74,91"
        };

        if(General.username.toUpperCase().equals("PAUEY SILVA")) {
            aItems = new String[] {
                    "SVI BF SUCAT,Monday March 28 2016,Yes,98,70,94",
                    "SVI MAKATI,Tuesday March 22 2016,Yes,98,74,91",
                    "SVI MEGAMALL B,Friday March 18 2016,No,98,41,87",
                    "SVI MEGAMALL A,Friday March 18 2016,No,98,74,91"
            };
        }

        for (String items : aItems) {
            lnrInflater = (LinearLayout) LayoutInflater.from(ReportStoreActivity.this).inflate(R.layout.report_store_activity_row, null);
            TextView tvwRow1 = (TextView) lnrInflater.findViewById(R.id.tvwRow1);
            TextView tvwRow2 = (TextView) lnrInflater.findViewById(R.id.tvwRow2);
            TextView tvwRow3 = (TextView) lnrInflater.findViewById(R.id.tvwRow3);
            TextView tvwRow4 = (TextView) lnrInflater.findViewById(R.id.tvwRow4);
            TextView tvwRow5 = (TextView) lnrInflater.findViewById(R.id.tvwRow5);
            TextView tvwRow6 = (TextView) lnrInflater.findViewById(R.id.tvwRow6);

            String[] splitted = items.split(",");

            tvwRow1.setText(splitted[0]);
            tvwRow2.setText(splitted[1]);
            tvwRow3.setText(splitted[2]);
            tvwRow4.setText(splitted[3] + " %");
            tvwRow5.setText(splitted[4] + " %");
            tvwRow6.setText(splitted[5] + " %");

            tblStore.addView(lnrInflater);
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
