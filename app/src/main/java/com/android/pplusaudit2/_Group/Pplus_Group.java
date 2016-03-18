package com.android.pplusaudit2._Group;

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
import com.android.pplusaudit2.MyMessageBox;
import com.android.pplusaudit2.R;
import com.android.pplusaudit2.TCRLib;
import com.android.pplusaudit2._Questions.Pplus_Questions;

import java.util.ArrayList;

/**
 * Created by ULTRABOOK on 9/22/2015.
 */
public class Pplus_Group extends AppCompatActivity {

    private ArrayList<Pplus_GroupClass> arrGroupList = new ArrayList<Pplus_GroupClass>();

    SQLLibrary sqlLibrary;
    TCRLib tcrLib;
    MyMessageBox messageBox;

    int totalAnswered = 0;
    int totalSurveys = 0;
    General.SCORE_STATUS scoreStatus;

    String storeCategoryId;
    String categoryname;
    String categoryid;

    private ProgressDialog progressGroupDL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pplus_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        overridePendingTransition(R.anim.slide_in_left, R.anim.hold);

        sqlLibrary = new SQLLibrary(this);
        messageBox = new MyMessageBox(this);
        tcrLib = new TCRLib(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //String[] value = extras.getString("CATEGORY_AND_TEMPID").split(",");
            storeCategoryId = extras.getString("STORE_CATEGORY_ID").trim();
            categoryname = extras.getString("CATEGORY_NAME").trim();
            categoryid = extras.getString("CATEGORY_ID").trim();
            getSupportActionBar().setTitle(categoryname.toUpperCase());
        }
    }

    public class AsyncLoadGroups extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            progressGroupDL = ProgressDialog.show(Pplus_Group.this, "", "Getting group scores...");
            arrGroupList.clear();
        }

        @Override
        protected String doInBackground(Void... params) {

            Cursor cursorGroup = sqlLibrary.RawQuerySelect("SELECT tblstorecateggroup.id, tblgroup.groupdesc, " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_final
                    + "," + SQLiteDB.COLUMN_STORECATEGORYGROUP_status
                    + " FROM " + SQLiteDB.TABLE_STORECATEGORYGROUP
                    + " JOIN " + SQLiteDB.TABLE_GROUP + " ON " + SQLiteDB.TABLE_GROUP + "." + SQLiteDB.COLUMN_GROUP_id + " = " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_groupid
                    + " WHERE " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_storecategid + " = " + storeCategoryId
                    + " ORDER BY " + SQLiteDB.COLUMN_GROUP_grouporder);

            /*Cursor cursorGroup = sqlLibrary.RawQuerySelect("SELECT " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_storecategid + "," + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_id + "," + SQLiteDB.COLUMN_STORECATEGORYGROUP_groupid
                    + "," + SQLiteDB.COLUMN_GROUP_groupdesc + "," + SQLiteDB.TABLE_STORECATEGORY + "." + SQLiteDB.COLUMN_STORECATEGORY_categoryid
                    + " FROM " + SQLiteDB.TABLE_STORECATEGORYGROUP + " JOIN " + SQLiteDB.TABLE_GROUP + " ON "
                    + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_groupid + " = " + SQLiteDB.TABLE_GROUP + "." + SQLiteDB.COLUMN_GROUP_groupid
                    + " JOIN " + SQLiteDB.TABLE_STORECATEGORY + " ON " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_storecategid + " = " + SQLiteDB.TABLE_STORECATEGORY + "." + SQLiteDB.COLUMN_STORECATEGORY_id
                    + " WHERE " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_storecategid + " = " + storeCategoryId
                    + " GROUP BY " + SQLiteDB.COLUMN_STORECATEGORYGROUP_groupid + " ORDER BY " + SQLiteDB.COLUMN_GROUP_grouporder);*/

            cursorGroup.moveToFirst();

            while (!cursorGroup.isAfterLast()) {

                int storeCategoryGroupID = cursorGroup.getInt(cursorGroup.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORYGROUP_id));

                if(!sqlLibrary.HasQuestionsByGroup(storeCategoryGroupID)) {
                    String[] aFields = new String[] {
                            SQLiteDB.COLUMN_STORECATEGORYGROUP_status,
                            SQLiteDB.COLUMN_STORECATEGORYGROUP_initial,
                            SQLiteDB.COLUMN_STORECATEGORYGROUP_final
                    };
                    String[] aValues = new String[] {
                            "2",
                            "1",
                            "1"
                    };
                    sqlLibrary.UpdateRecord(SQLiteDB.TABLE_STORECATEGORYGROUP, SQLiteDB.COLUMN_STORECATEGORYGROUP_id, String.valueOf(storeCategoryGroupID), aFields, aValues);
                    cursorGroup.moveToNext();
                    continue;
                }

                String grpFinalAnswer = cursorGroup.getString(cursorGroup.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORYGROUP_final));
                String grpStatusno = cursorGroup.getString(cursorGroup.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORYGROUP_status));
                String grpStatus = "";

                grpStatus = tcrLib.GetStatus(grpStatusno);
                scoreStatus = tcrLib.GetScoreStatus(grpFinalAnswer);

                String desc = cursorGroup.getString(cursorGroup.getColumnIndex(SQLiteDB.COLUMN_GROUP_groupdesc)).trim().replace("\"", "").toUpperCase();

                arrGroupList.add(new Pplus_GroupClass(storeCategoryGroupID, desc, String.valueOf(storeCategoryGroupID), grpStatus, scoreStatus));
                cursorGroup.moveToNext();
            }

            cursorGroup.close();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            progressGroupDL.dismiss();

            ListView lvwGroup = (ListView) findViewById(R.id.lvwGroup);
            Pplus_GroupAdapter adapter = new Pplus_GroupAdapter(Pplus_Group.this, arrGroupList);
            lvwGroup.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            lvwGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    TextView tvwGroup = (TextView) view.findViewById(R.id.tvwGroup);
                    String storecateggroupid = String.valueOf(tvwGroup.getTag().toString());

                    Intent intentQuestion = new Intent(Pplus_Group.this, Pplus_Questions.class);
                    intentQuestion.putExtra("STORE_CATEGORY_GROUP_ID", storecateggroupid);
                    intentQuestion.putExtra("STORE_CATEGORY_ID", storeCategoryId);
                    intentQuestion.putExtra("GROUP_DESC", String.valueOf(tvwGroup.getText()));
                    intentQuestion.putExtra("CATEGORY_ID", categoryid);

                    // VALIDATE IF GROUP HAS QUESTIONS
                    Cursor cursGetQuestions = sqlLibrary.RawQuerySelect("SELECT COUNT(*) AS totquestions FROM " + SQLiteDB.TABLE_STOREQUESTION
                            + " WHERE " + SQLiteDB.COLUMN_STOREQUESTION_storecategorygroupid + " = '" + storecateggroupid + "'");
                    cursGetQuestions.moveToFirst();
                    int totquestions = cursGetQuestions.getInt(cursGetQuestions.getColumnIndex("totquestions"));
                    cursGetQuestions.close();

                    if (totquestions > 0) startActivity(intentQuestion);
                    else {
                        messageBox.ShowMessage("Questions", "No questions found in this group.");
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //new AsyncLoadGroups().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new AsyncLoadGroups().execute();
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
