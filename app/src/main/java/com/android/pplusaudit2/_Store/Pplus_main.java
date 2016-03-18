package com.android.pplusaudit2._Store;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pplusaudit2.Database.SQLLibrary;
import com.android.pplusaudit2.Database.SQLiteDB;
import com.android.pplusaudit2.General;
import com.android.pplusaudit2.Json.JSON_pplus;
import com.android.pplusaudit2.MyMessageBox;
import com.android.pplusaudit2.R;
import com.android.pplusaudit2._Category.Pplus_Activity;

import java.util.ArrayList;

/**
 * Created by ULTRABOOK on 9/22/2015.
 */
public class Pplus_main extends AppCompatActivity {

    private ArrayList<Pplus_Storeclass> arrStoreList = new ArrayList<Pplus_Storeclass>();

    private String urlGetStores = "http://tcr.chasetech.com/api/user/stores?";

    MyMessageBox messageBox;
    JSON_pplus json;

    SQLLibrary sql;
    SQLiteDB sqLiteDB;

    String storeid;
    TextView tvwStore;
    int templateid;

    String storename;

    int storeGradeMatrix;

    PowerManager powerman;
    PowerManager.WakeLock wlStayAwake;

    //String storeid2;

    private ProgressDialog progressDL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pplus_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        overridePendingTransition(R.anim.slide_in_left, R.anim.hold);

        getSupportActionBar().setTitle("STORES");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        powerman = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
        wlStayAwake = powerman.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "wakelocktag");
        wlStayAwake.acquire();
        sql = new SQLLibrary(this);
        sqLiteDB = new SQLiteDB(this);
    }

    @Override
    protected void onDestroy() {
        wlStayAwake.release();
        super.onDestroy();
    }

    // LOAD STORE SCORES
    public class AsyncLoadStoreScores extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDL = ProgressDialog.show(Pplus_main.this, "", "Getting store scores..");
            arrStoreList.clear();
        }

        @Override
        protected String doInBackground(Void... params) {

            Cursor cursstores = sql.GetDataCursor(SQLiteDB.TABLE_STORE);
            int nstoreid;
            int templateid;
            String templatename;
            boolean isAudited = false;
            boolean isPosted = false;

            cursstores.moveToFirst();
            while(!cursstores.isAfterLast()) {
                isAudited = false;
                isPosted = false;
                nstoreid = cursstores.getInt(cursstores.getColumnIndex(SQLiteDB.COLUMN_STORE_id));

                String storename = cursstores.getString(cursstores.getColumnIndex(SQLiteDB.COLUMN_STORE_name)).trim().replace("\"", "");
                templateid = cursstores.getInt(cursstores.getColumnIndex(SQLiteDB.COLUMN_STORE_audittempid));
                templatename = cursstores.getString(cursstores.getColumnIndex(SQLiteDB.COLUMN_STORE_templatename)).trim().replace("\"", "");
                int nStoreStatus = cursstores.getInt(cursstores.getColumnIndex(SQLiteDB.COLUMN_STORE_status));
                int finalValue = cursstores.getInt(cursstores.getColumnIndex(SQLiteDB.COLUMN_STORE_final));
                int nPosted = cursstores.getInt(cursstores.getColumnIndex(SQLiteDB.COLUMN_STORE_posted));

                if(nStoreStatus > 0) isAudited = true;
                if(nPosted == 1) isPosted = true;

                arrStoreList.add(new Pplus_Storeclass(nstoreid, storename, templateid, templatename, finalValue, isAudited, isPosted));
                cursstores.moveToNext();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            final ListView lvwStore = (ListView) findViewById(R.id.lvwStore);
            Pplus_StoreClassAdapter adapter = new Pplus_StoreClassAdapter(Pplus_main.this, arrStoreList);
            lvwStore.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            progressDL.dismiss();
        }
    }

    // AUDIT BUTTON CLICK
    public void auditOnClickEvent(View v) {

        LinearLayout lnrParent = (LinearLayout) v.getParent();
        LinearLayout lnrStoreParent = (LinearLayout) lnrParent.getChildAt(0);
        tvwStore = (TextView) lnrStoreParent.getChildAt(0);

        String[] storetempid = tvwStore.getTag().toString().split(",");

        storename = String.valueOf(tvwStore.getText());
        templateid = Integer.parseInt(storetempid[0]);
        storeid = storetempid[1];
        General.storeid = storeid;

        Cursor cursStoreid = sql.GetDataCursor(SQLiteDB.TABLE_STORE, SQLiteDB.COLUMN_STORE_id + " = " + storeid);
        cursStoreid.moveToFirst();

        if(cursStoreid.getCount() == 0) {
            Toast.makeText(Pplus_main.this, "No store found.", Toast.LENGTH_SHORT).show();
            return;
        }

        General.Temp_Storeid = cursStoreid.getInt(cursStoreid.getColumnIndex(SQLiteDB.COLUMN_STORE_storeid));
        General.auditTemplateID = String.valueOf(templateid);
        storeGradeMatrix = cursStoreid.getInt(cursStoreid.getColumnIndex(SQLiteDB.COLUMN_STORE_gradematrixid));
        cursStoreid.close();

        new AsyncGetStoreTemplate().execute();
    }

    // PREVIEW BUTTON CLICK
    public void previewOnClickEvent(View v) {

        LinearLayout lnrParent = (LinearLayout) v.getParent();
        LinearLayout lnrStoreParent = (LinearLayout) lnrParent.getChildAt(0);

        tvwStore = (TextView) lnrStoreParent.getChildAt(0);

        String[] storetempid = tvwStore.getTag().toString().split(",");
        storename = String.valueOf(tvwStore.getText());
        storeid = storetempid[1];
        General.storeid = storeid;

        Intent intentPreview = new Intent(Pplus_main.this, Pplus_main_preview.class);
        intentPreview.putExtra("STORE_ID", storeid);
        startActivity(intentPreview);
    }

    // TASK: LOAD STORE TEMPLATE
    public class AsyncGetStoreTemplate extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDL = ProgressDialog.show(Pplus_main.this, "", "Getting store template...Please wait", true);
        }

        @Override
        protected String doInBackground(Void... params) {

            // STORE QUESTIONS
            Cursor curs = sql.GetDataCursor(SQLiteDB.TABLE_STORECATEGORY, SQLiteDB.COLUMN_STORECATEGORY_storeid + " = " + storeid);
            curs.moveToFirst();
            if(curs.getCount() > 0) {
                return null;
            }
            else {
                General.arrBrandSelected = new ArrayList<String>();
                General.arrBrandSelected.clear();

                // GET SECONDRY KEYLIST
                Cursor cursKeylist = sql.GetDataCursor(SQLiteDB.TABLE_SECONDARYKEYLIST);
                cursKeylist.moveToFirst();
                General.arrKeylist = new ArrayList<String>();
                while (!cursKeylist.isAfterLast()) {

                    General.arrKeylist.add(cursKeylist.getString(cursKeylist.getColumnIndex(SQLiteDB.COLUMN_SECONDARYKEYLIST_keygroupid)));
                    cursKeylist.moveToNext();
                }

                SQLiteDatabase dbaseStoreQ = sqLiteDB.getWritableDatabase();

                // GET STORE CATEGORIES
                Cursor cursCategoryOfStore = sql.GetDataCursor(SQLiteDB.TABLE_CATEGORY, SQLiteDB.COLUMN_CATEGORY_audittempid + " = '" + General.auditTemplateID + "'");
                cursCategoryOfStore.moveToFirst();

                String[] afieldsStoreCategories = {
                        SQLiteDB.COLUMN_STORECATEGORY_storeid,
                        SQLiteDB.COLUMN_STORECATEGORY_categoryid,
                        SQLiteDB.COLUMN_STORECATEGORY_initial,
                        SQLiteDB.COLUMN_STORECATEGORY_exempt,
                        SQLiteDB.COLUMN_STORECATEGORY_final,
                        SQLiteDB.COLUMN_STORECATEGORY_status
                };

                String sqlinsertStoreCategories = sql.createInsertBulkQuery(SQLiteDB.TABLE_STORECATEGORY, afieldsStoreCategories);
                SQLiteStatement sqlstatementStoreCategories = dbaseStoreQ.compileStatement(sqlinsertStoreCategories);
                dbaseStoreQ.beginTransaction();

                // INSERT CATEGORIES PER STORE
                if (cursCategoryOfStore.getCount() > 0) {

                    cursCategoryOfStore.moveToFirst();
                    while (!cursCategoryOfStore.isAfterLast()) {

                        sqlstatementStoreCategories.clearBindings();
                        sqlstatementStoreCategories.bindString(1, storeid);
                        sqlstatementStoreCategories.bindString(2, cursCategoryOfStore.getString(cursCategoryOfStore.getColumnIndex(SQLiteDB.COLUMN_CATEGORY_id)));
                        sqlstatementStoreCategories.bindString(3, "0");
                        sqlstatementStoreCategories.bindString(4, "0");
                        sqlstatementStoreCategories.bindString(5, "");
                        sqlstatementStoreCategories.bindString(6, "0");
                        sqlstatementStoreCategories.execute();

                        cursCategoryOfStore.moveToNext();
                    }
                }

                dbaseStoreQ.setTransactionSuccessful();
                dbaseStoreQ.endTransaction();

                // GET GROUP BY CATEGORIES
                Cursor cursCategories = sql.GetDataCursor(SQLiteDB.TABLE_STORECATEGORY, SQLiteDB.COLUMN_STORECATEGORY_storeid + " = '" + storeid + "'");
                cursCategories.moveToFirst();
                while (!cursCategories.isAfterLast()) {

                    String storecategoryid = cursCategories.getString(cursCategories.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORY_id));
                    String categoryid = cursCategories.getString(cursCategories.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORY_categoryid));

                    String[] aFieldsStorecateggroup = {
                            SQLiteDB.COLUMN_STORECATEGORYGROUP_storecategid,
                            SQLiteDB.COLUMN_STORECATEGORYGROUP_groupid,
                            SQLiteDB.COLUMN_STORECATEGORYGROUP_initial,
                            SQLiteDB.COLUMN_STORECATEGORYGROUP_exempt,
                            SQLiteDB.COLUMN_STORECATEGORYGROUP_final,
                            SQLiteDB.COLUMN_STORECATEGORYGROUP_status
                    };

                    Cursor cursGroupPerCategories = sql.RawQuerySelect("SELECT " + SQLiteDB.COLUMN_GROUP_id + "," + SQLiteDB.COLUMN_GROUP_groupid
                            + " FROM " + SQLiteDB.TABLE_GROUP
                            + " WHERE " + SQLiteDB.COLUMN_GROUP_categoryid + " = '" + categoryid + "'"
                            + " ORDER BY " + SQLiteDB.COLUMN_GROUP_grouporder);
                    cursGroupPerCategories.moveToFirst();

                    String sqlinsertStoreCategGroup = sql.createInsertBulkQuery(SQLiteDB.TABLE_STORECATEGORYGROUP, aFieldsStorecateggroup);
                    SQLiteStatement sqlstatementStoreCategGroup = dbaseStoreQ.compileStatement(sqlinsertStoreCategGroup);
                    dbaseStoreQ.beginTransaction();

                    while (!cursGroupPerCategories.isAfterLast()) {

                        String groupid = cursGroupPerCategories.getString(cursGroupPerCategories.getColumnIndex(SQLiteDB.COLUMN_GROUP_id));

                        sqlstatementStoreCategGroup.clearBindings();
                        sqlstatementStoreCategGroup.bindString(1, storecategoryid);
                        sqlstatementStoreCategGroup.bindString(2, groupid);
                        sqlstatementStoreCategGroup.bindString(3, "0");
                        sqlstatementStoreCategGroup.bindString(4, "0");
                        sqlstatementStoreCategGroup.bindString(5, "");
                        sqlstatementStoreCategGroup.bindString(6, "0"); //0 - pending, 1 - partial, 2 - complete
                        sqlstatementStoreCategGroup.execute();

                        cursGroupPerCategories.moveToNext();
                    }

                    dbaseStoreQ.setTransactionSuccessful();
                    dbaseStoreQ.endTransaction();

                    // GET QUESTION PER GROUP
                    Cursor cursGroup = sql.GetDataCursor(SQLiteDB.TABLE_STORECATEGORYGROUP, SQLiteDB.COLUMN_STORECATEGORYGROUP_storecategid + " = '" + storecategoryid + "'");
                    /*Cursor cursGroup = sql.RawQuerySelect("SELECT * FROM " + SQLiteDB.TABLE_STORECATEGORYGROUP
                                                            + " JOIN " + SQLiteDB.TABLE_GROUP
                                                            + " ON " + SQLiteDB.TABLE_GROUP  + "." + SQLiteDB.COLUMN_GROUP_id + " = " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_groupid
                                                            + " JOIN " + SQLiteDB.TABLE_CATEGORY
                                                            + " ON " + SQLiteDB.TABLE_CATEGORY + "." + SQLiteDB.COLUMN_CATEGORY_id + " = " + SQLiteDB.TABLE_GROUP + "." + SQLiteDB.COLUMN_GROUP_categoryid
                                                            + " GROUP BY tblstorecateggroup.id");*/
                    cursGroup.moveToFirst();
                    int a = cursGroup.getCount();


                    while(!cursGroup.isAfterLast()) {

                        String storeCategoryGroupID = cursGroup.getString(cursGroup.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORYGROUP_id));
                        String storegroupID = cursGroup.getString(cursGroup.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORYGROUP_groupid));

                        //String strTempcategoryid = cursGroup.getString(cursGroup.getColumnIndex(SQLiteDB.COLUMN_CATEGORY_categoryid));
                        //String strTempGroupId = cursGroup.getString(cursGroup.getColumnIndex(SQLiteDB.COLUMN_GROUP_groupid));

                        Cursor cursTempGroup = sql.GetDataCursor(SQLiteDB.TABLE_GROUP, SQLiteDB.COLUMN_GROUP_id + " = '" + storegroupID + "'");
                        cursTempGroup.moveToFirst();

                        String strGroupCategoryid = cursTempGroup.getString(cursTempGroup.getColumnIndex(SQLiteDB.COLUMN_GROUP_categoryid));
                        String strTempGroupId = cursTempGroup.getString(cursTempGroup.getColumnIndex(SQLiteDB.COLUMN_GROUP_groupid));

                        Cursor cursTempCategory = sql.GetDataCursor(SQLiteDB.TABLE_CATEGORY, SQLiteDB.COLUMN_CATEGORY_id + " = '" + strGroupCategoryid + "'");
                        cursTempCategory.moveToFirst();
                        String strTempcategoryid = cursTempCategory.getString(cursTempCategory.getColumnIndex(SQLiteDB.COLUMN_CATEGORY_categoryid));

                        General.arrBrandSelected.clear();
                        Cursor cursBrand = sql.RawQuerySelect("SELECT " + SQLiteDB.COLUMN_SECONDARYDISP_brand
                                    + " FROM " + SQLiteDB.TABLE_SECONDARYDISP
                                    + " WHERE " + SQLiteDB.COLUMN_SECONDARYDISP_storeid + " = " + General.Temp_Storeid
                                    + " AND " + SQLiteDB.COLUMN_SECONDARYDISP_categoryid + " = " + strTempcategoryid);

                        cursBrand.moveToFirst();

                        while (!cursBrand.isAfterLast()) {

                            General.arrBrandSelected.add(cursBrand.getString(cursBrand.getColumnIndex(SQLiteDB.COLUMN_SECONDARYDISP_brand)));
                            cursBrand.moveToNext();
                        }

                        // GET QUESTIONS PER GROUP
                        Cursor cursQuestionsPerGroup = sql.RawQuerySelect("SELECT " + SQLiteDB.COLUMN_QUESTION_prompt + "," + SQLiteDB.COLUMN_QUESTION_id + "," + SQLiteDB.COLUMN_QUESTION_groupid + "," + SQLiteDB.COLUMN_QUESTION_questionid
                                + " FROM " + SQLiteDB.TABLE_QUESTION
                                + " WHERE " + SQLiteDB.COLUMN_QUESTION_groupid + " = '" + storegroupID + "'"
                                + " ORDER BY " + SQLiteDB.COLUMN_QUESTION_order);
                        cursQuestionsPerGroup.moveToFirst();

                        String[] aFieldsStorequestion = {
                                SQLiteDB.COLUMN_STOREQUESTION_storecategorygroupid,
                                SQLiteDB.COLUMN_STOREQUESTION_questionid,
                                SQLiteDB.COLUMN_STOREQUESTION_isAnswered,
                                SQLiteDB.COLUMN_STOREQUESTION_initial,
                                SQLiteDB.COLUMN_STOREQUESTION_exempt,
                                SQLiteDB.COLUMN_STOREQUESTION_final
                        };

                        String sqlinsertStoreQuestions = sql.createInsertBulkQuery(SQLiteDB.TABLE_STOREQUESTION, aFieldsStorequestion);
                        SQLiteStatement sqlstatementStoreQuestions = dbaseStoreQ.compileStatement(sqlinsertStoreQuestions);
                        dbaseStoreQ.beginTransaction();

                        while (!cursQuestionsPerGroup.isAfterLast()) {

                            String questionid = cursQuestionsPerGroup.getString(cursQuestionsPerGroup.getColumnIndex(SQLiteDB.COLUMN_QUESTION_id));
                            String strQuestionPrompt = cursQuestionsPerGroup.getString(cursQuestionsPerGroup.getColumnIndex(SQLiteDB.COLUMN_QUESTION_prompt));

                            if(General.arrKeylist.contains(strTempGroupId)) {
                                if(General.arrBrandSelected.contains(strQuestionPrompt)) {
                                    sqlstatementStoreQuestions.clearBindings();
                                    sqlstatementStoreQuestions.bindString(1, storeCategoryGroupID);
                                    sqlstatementStoreQuestions.bindString(2, questionid);
                                    sqlstatementStoreQuestions.bindString(3, "0");
                                    sqlstatementStoreQuestions.bindString(4, "0");
                                    sqlstatementStoreQuestions.bindString(5, "0");
                                    sqlstatementStoreQuestions.bindString(6, "0");
                                    sqlstatementStoreQuestions.execute();
                                }
                            }
                            else {

                                sqlstatementStoreQuestions.clearBindings();
                                sqlstatementStoreQuestions.bindString(1, storeCategoryGroupID);
                                sqlstatementStoreQuestions.bindString(2, questionid);
                                sqlstatementStoreQuestions.bindString(3, "0");
                                sqlstatementStoreQuestions.bindString(4, "0");
                                sqlstatementStoreQuestions.bindString(5, "0");
                                sqlstatementStoreQuestions.bindString(6, "0");
                                sqlstatementStoreQuestions.execute();
                            }

                            cursQuestionsPerGroup.moveToNext();
                        }

                        dbaseStoreQ.setTransactionSuccessful();
                        dbaseStoreQ.endTransaction();

                        cursGroup.moveToNext();
                    }

                    cursCategories.moveToNext();
                }

                dbaseStoreQ.close();


                /*final Cursor cursQuestionsbystore = sql.GetStoreQuestions(SQLiteDB.COLUMN_QUESTION_audittempid + " = " + templateid, dbaseStoreQ, sqLiteDB);
                cursQuestionsbystore.moveToFirst();
                String[] afieldsStoreQuestion = {
                        SQLiteDB.COLUMN_STOREQUESTION_questionid,
                        SQLiteDB.COLUMN_STOREQUESTION_isAnswered
                };

                String sqlinsertStoreQuestions = sql.createInsertBulkQuery(SQLiteDB.TABLE_STOREQUESTION, afieldsStoreQuestion);
                SQLiteStatement sqlstatementStoreQuestion = dbaseStoreQ.compileStatement(sqlinsertStoreQuestions);
                dbaseStoreQ.beginTransaction();

                if (cursQuestionsbystore.getCount() > 0) {

                    cursQuestionsbystore.moveToFirst();
                    while (!cursQuestionsbystore.isAfterLast()) {

                        sqlstatementStoreQuestion.clearBindings();
                        sqlstatementStoreQuestion.bindString(1, storeid);
                        sqlstatementStoreQuestion.bindString(2, cursQuestionsbystore.getString(cursQuestionsbystore.getColumnIndex("questionid")));
                        sqlstatementStoreQuestion.bindString(3, "0");
                        sqlstatementStoreQuestion.execute();

                        cursQuestionsbystore.moveToNext();
                    }
                }

                dbaseStoreQ.setTransactionSuccessful();
                dbaseStoreQ.endTransaction();
                dbaseStoreQ.close();*/

                //sql.UpdateRecord(SQLiteDB.TABLE_STORE, SQLiteDB.COLUMN_STORE_storeid, storeid, new String[]{SQLiteDB.COLUMN_STORE_status}, new String[]{ "1" });
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            progressDL.dismiss();
            Intent intentActivity = new Intent(Pplus_main.this, Pplus_Activity.class);
            intentActivity.putExtra("STORE_ID", storeid);
            intentActivity.putExtra("STORE_NAME", storename);
            intentActivity.putExtra("GRADE_MATRIX", storeGradeMatrix);
            startActivity(intentActivity);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //new AsyncLoadStoreScores().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new AsyncLoadStoreScores().execute();
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_in_right);
    }


}
