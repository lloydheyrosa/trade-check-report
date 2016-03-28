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

public class ReportAuditActivity extends AppCompatActivity {

    TableLayout tblAudit;
    LinearLayout lnrInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_audit_activity);

        String title = "REPORTS";
        getSupportActionBar().setTitle(title);
        overridePendingTransition(R.anim.slide_up, R.anim.hold);

        tblAudit = (TableLayout) findViewById(R.id.tblAuditReport);
        tblAudit.removeAllViews();

        String[] aItems = new String[] {
                "Username," + General.username.toUpperCase(),
                "Month,March",
                "Stores Mapped,20",
                "Stores Audited,4",
                "Perfect Stores,1",
                "% Achievement,25 %",
                "Category Doors,40",
                "% Achievement,58 %",
        };

        for (String items : aItems) {
            lnrInflater = (LinearLayout) LayoutInflater.from(ReportAuditActivity.this).inflate(R.layout.report_audit_activity_row, null);
            TextView tvwItem = (TextView) lnrInflater.findViewById(R.id.tvwRowItem);
            TextView tvwValue = (TextView) lnrInflater.findViewById(R.id.tvwRowValue);

            String[] splitted = items.split(",");
            String item = splitted[0];
            String value = splitted[1];
            tvwItem.setText(item);
            tvwValue.setText(value);

            tblAudit.addView(lnrInflater);
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
        overridePendingTransition( R.anim.hold, R.anim.slide_down );
    }
}
