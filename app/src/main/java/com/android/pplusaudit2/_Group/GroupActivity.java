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
import android.widget.Toast;

import com.android.pplusaudit2.Database.SQLLibrary;
import com.android.pplusaudit2.Database.SQLiteDB;
import com.android.pplusaudit2.ErrorLogs.AutoErrorLog;
import com.android.pplusaudit2.ErrorLogs.ErrorLog;
import com.android.pplusaudit2.General;
import com.android.pplusaudit2.MyMessageBox;
import com.android.pplusaudit2.R;
import com.android.pplusaudit2.TCRLib;
import com.android.pplusaudit2._Questions.QuestionsActivity;

import java.util.ArrayList;

/**
 * Created by ULTRABOOK on 9/22/2015.
 */
public class GroupActivity extends AppCompatActivity {

    private ArrayList<Group> arrGroupList = new ArrayList<Group>();
    private ArrayList<Group> lstGroups = new ArrayList<Group>();

    private SQLLibrary sqlLibrary;
    private TCRLib tcrLib;
    private MyMessageBox messageBox;

    private String storeCategoryId;
    private String categoryid;

    private ListView lvwGroup;
    private String TAG = "";

    private ProgressDialog progressGroupDL;
    private ErrorLog errorLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_activity_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        overridePendingTransition(R.anim.slide_in_left, R.anim.hold);

        Thread.setDefaultUncaughtExceptionHandler(new AutoErrorLog(this, General.errlogFile));
        TAG = GroupActivity.this.getLocalClassName();

        errorLog = new ErrorLog(General.errlogFile, this);

        sqlLibrary = new SQLLibrary(this);
        messageBox = new MyMessageBox(this);
        tcrLib = new TCRLib(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            storeCategoryId = extras.getString("STORE_CATEGORY_ID").trim();
            String categoryname = extras.getString("CATEGORY_NAME").trim();
            categoryid = extras.getString("CATEGORY_ID").trim();
            getSupportActionBar().setTitle(categoryname.toUpperCase());
        }

        lvwGroup = (ListView) findViewById(R.id.lvwGroup);
    }

    private class AsyncLoadGroups extends AsyncTask<Void, Void, Boolean> {

        private String errMsg;

        @Override
        protected void onPreExecute() {
            progressGroupDL = ProgressDialog.show(GroupActivity.this, "", "Getting group scores...");
            arrGroupList.clear();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            boolean result = false;

            try {

                Cursor cursorGroup = sqlLibrary.RawQuerySelect("SELECT tblstorecateggroup.id, tblgroup.groupdesc, " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_final
                        + "," + SQLiteDB.COLUMN_STORECATEGORYGROUP_status
                        + " FROM " + SQLiteDB.TABLE_STORECATEGORYGROUP
                        + " JOIN " + SQLiteDB.TABLE_GROUP + " ON " + SQLiteDB.TABLE_GROUP + "." + SQLiteDB.COLUMN_GROUP_id + " = " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_groupid
                        + " WHERE " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_storecategid + " = " + storeCategoryId
                        + " ORDER BY " + SQLiteDB.COLUMN_GROUP_grouporder);

                cursorGroup.moveToFirst();

                while (!cursorGroup.isAfterLast()) {

                    int storeCategoryGroupID = cursorGroup.getInt(cursorGroup.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORYGROUP_id));

                    if (!sqlLibrary.HasQuestionsPerGroup(storeCategoryGroupID)) {
                        String[] aFields = new String[]{
                                SQLiteDB.COLUMN_STORECATEGORYGROUP_status,
                                SQLiteDB.COLUMN_STORECATEGORYGROUP_initial,
                                SQLiteDB.COLUMN_STORECATEGORYGROUP_final
                        };
                        String[] aValues = new String[]{
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
                    General.SCORE_STATUS scoreStatus = tcrLib.GetScoreStatus(grpFinalAnswer);

                    String desc = cursorGroup.getString(cursorGroup.getColumnIndex(SQLiteDB.COLUMN_GROUP_groupdesc)).trim().replace("\"", "").toUpperCase();

                    arrGroupList.add(new Group(storeCategoryGroupID, desc, String.valueOf(storeCategoryGroupID), grpStatus, scoreStatus));
                    cursorGroup.moveToNext();
                }

                cursorGroup.close();
                result = true;
            }
            catch (Exception ex) {
                errMsg = "Can't load groups.";
                String exErr = ex.getMessage() != null ? ex.getMessage() : errMsg;
                errorLog.appendLog(exErr, TAG);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean bResult) {

            if(!bResult) {
                Toast.makeText(GroupActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                return;
            }

            lstGroups.clear();
            lstGroups.addAll(arrGroupList);
            GroupAdapter adapter = new GroupAdapter(GroupActivity.this, lstGroups);
            lvwGroup.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            progressGroupDL.dismiss();

            lvwGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    TextView tvwGroup = (TextView) view.findViewById(R.id.tvwGroup);
                    String storecateggroupid = String.valueOf(tvwGroup.getTag().toString());

                    Intent intentQuestion = new Intent(GroupActivity.this, QuestionsActivity.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
