package com.android.pplusaudit2._Store;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pplusaudit2.Database.SQLLibrary;
import com.android.pplusaudit2.Database.SQLiteDB;
import com.android.pplusaudit2.Debug.DebugLog;
import com.android.pplusaudit2.ErrorLogs.AutoErrorLog;
import com.android.pplusaudit2.General;
import com.android.pplusaudit2.MyMessageBox;
import com.android.pplusaudit2.R;
import com.android.pplusaudit2.Settings;
import com.android.pplusaudit2.TCRLib;
import com.android.pplusaudit2._Category.Category;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ULTRABOOK on 10/28/2015.
 */
public class StorePreviewActivity extends AppCompatActivity {

    SQLLibrary sqlLibrary;
    TCRLib tcrLib;

    Typeface fontIcon;

    String storename;
    String tempStoreId;
    String previewStoreID;
    String storeTemplate;
    String storeCode;
    String startDate;
    String endDate;
    int storeFinalValue;
    String strOsa = "";
    String strNpi = "";
    String strPlanogram = "";
    String strPerfectStore = "";

    ProgressDialog progressDL;
    File filepathToSend;
    String strFilenameToSend;

    AlertDialog postDialog;
    ProgressDialog pDialog;

    ListView lvwPreview;
    MyMessageBox messageBox;

    Cursor cursStore;

    String strDetailsBody;
    String strSummaryBody;

    String strImageFolder;

    PowerManager powerman;
    PowerManager.WakeLock wlStayAwake;

    TextView tvwOsa;
    TextView tvwNpi;
    TextView tvwPlanogram;
    TextView tvwPerfectStore;

    boolean toggleAudit;

    ArrayList<Category> arrCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_preview_layout_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        overridePendingTransition(R.anim.slide_up, R.anim.hold);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Thread.setDefaultUncaughtExceptionHandler(new AutoErrorLog(this, General.errlogFile));

        getSupportActionBar().setTitle("AUDIT SUMMARY");

        fontIcon = Typeface.createFromAsset(getAssets(), General.typefacename);
        powerman = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
        wlStayAwake = powerman.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "wakelocktag");

        sqlLibrary = new SQLLibrary(this);
        tcrLib = new TCRLib(this);
        messageBox = new MyMessageBox(this);
        toggleAudit = false;

        arrCategories = new ArrayList<>();
        lvwPreview = (ListView) findViewById(R.id.lvwPreview);

        Bundle eData = getIntent().getExtras();

        if (eData != null) {
            previewStoreID = eData.getString("STORE_ID");

            cursStore = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_STORE, SQLiteDB.COLUMN_STORE_id + " = '" + previewStoreID + "'");
            cursStore.moveToFirst();

            storename = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_name)).trim();
            tempStoreId = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_storeid)).trim();
            storeTemplate = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_templatename)).trim();
            storeCode = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_storecode)).trim();
            startDate = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_startdate)).trim();
            endDate = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_enddate)).trim();
            storeFinalValue = cursStore.getInt(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_final));
            strOsa = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_osa));
            strNpi = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_npi));
            strPlanogram = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_planogram));
            strPerfectStore = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_perfectstore));

            //LoadAuditSummary();
            new LoadAuditSummary().execute();
        }

        TextView tvwStoreTemplate = (TextView) findViewById(R.id.tvwStoreTemplate);
        tvwOsa = (TextView) findViewById(R.id.tvwOsa);
        tvwNpi = (TextView) findViewById(R.id.tvwNpi);
        tvwPlanogram = (TextView) findViewById(R.id.tvwPlanogram);
        tvwPerfectStore = (TextView) findViewById(R.id.tvwPerfectStorePerc);

        strOsa = strOsa == null ? "" : strOsa;
        strNpi = strNpi == null ? "" : strNpi;
        strPlanogram = strPlanogram == null ? "" : strPlanogram;
        strPerfectStore = strPerfectStore == null ? "" : strPerfectStore;

        strOsa = strOsa.trim().equals("") ? "0.00 %" : strOsa + " %";
        strNpi = strNpi.trim().equals("") ? "0.00 %" : strNpi + " %";
        strPlanogram = strPlanogram.trim().equals("") ? "0.00 %" : strPlanogram + " %";
        strPerfectStore = strPerfectStore.trim().equals("")  ? "0.00 %" : strPerfectStore + " %";

        String strTemplate = storename + " - " + storeTemplate;
        tvwStoreTemplate.setText(strTemplate);
        tvwOsa.setText(strOsa);
        tvwNpi.setText(strNpi);
        tvwPlanogram.setText(strPlanogram);
        tvwPerfectStore.setText(strPerfectStore);

        strFilenameToSend = General.usercode + "_" + storeCode + ".csv";
        filepathToSend = new File(Settings.postingFolder, strFilenameToSend);

        Button btnBack = (Button) findViewById(R.id.btnBackPreview);
        Button btnPost = (Button) findViewById(R.id.btnPostAudit);
        TextView tvwOsaIcon = (TextView) findViewById(R.id.tvwOsaIcon);
        TextView tvwNpiIcon = (TextView) findViewById(R.id.tvwNpiIcon);
        TextView tvwPlanoIcon = (TextView) findViewById(R.id.tvwPlanogramIcon);
        TextView tvwPerfectIcon = (TextView) findViewById(R.id.tvwPerfectIcon);

        tvwOsaIcon.setTypeface(fontIcon);
        tvwNpiIcon.setTypeface(fontIcon);
        tvwPlanoIcon.setTypeface(fontIcon);
        tvwPerfectIcon.setTypeface(fontIcon);

        tvwOsaIcon.setText("\uf274");
        tvwNpiIcon.setText("\uf080");
        tvwPlanoIcon.setText("\uf00b");
        tvwPerfectIcon.setText("\uf087");

        btnPost.setTypeface(fontIcon);
        btnBack.setTypeface(fontIcon);
        btnPost.setText("\uf1d8" + " POST AUDIT");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(!CheckDateValidation(startDate, endDate)) {
            btnPost.setEnabled(false);
            General.ShowMessage(StorePreviewActivity.this, "End of posting", "End date of posting");
        }

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDialog = new AlertDialog.Builder(StorePreviewActivity.this).create();
                postDialog.setTitle("Post survey");
                postDialog.setMessage("Do you want to post this survey result?");
                postDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        postDialog.dismiss();
                        wlStayAwake.acquire();
                        new CheckInternet().execute();
                    }
                });
                postDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { postDialog.dismiss(); }
                });
                postDialog.show();
            }
        });
    }

    public class CheckInternet extends AsyncTask<Void, Void, Boolean> {
        String errmsg = "";

        @Override
        protected void onPreExecute() {
            progressDL = ProgressDialog.show(StorePreviewActivity.this, "", "Checking internet connection.");
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
            else errmsg = "No internet connection.";

            return result;
        }

        @Override
        protected void onPostExecute(Boolean bResult) {
            progressDL.dismiss();
            if(!bResult) {
                Toast.makeText(StorePreviewActivity.this, errmsg, Toast.LENGTH_SHORT).show();
                return;
            }

            new AsyncGenerateTextFile().execute();
        }
    }

    public boolean CheckDateValidation(String dtFrom, String dtTo) {
        boolean result = false;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date dateToday = new Date();
            Date dateFrom = dateFormat.parse(dtFrom);
            Date dateTo = dateFormat.parse(dtTo);

            String strDateToday = dateFormat.format(dateToday);

            if((dateToday.after(dateFrom) || strDateToday.equals(dtFrom)) && (dateToday.before(dateTo) || strDateToday.equals(dtTo))) {
                result = true;
            }
        }
        catch (ParseException pex) {
            pex.printStackTrace();
            DebugLog.log("ParseException: " + pex.getMessage());
        }

        return result;
    }

    public boolean CheckIfPosted(String storeid) {
        boolean res = false;

        Cursor cursCheck = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_STORE, SQLiteDB.COLUMN_STORE_id + " = '" + storeid + "' AND " + SQLiteDB.COLUMN_STORE_posted + " = '1'");
        cursCheck.moveToFirst();
        if(cursCheck.getCount() > 0) res = true;

        return res;
    }

    public class LoadAuditSummary extends AsyncTask<Void, Void, Boolean> {

        private String errmsg;

        @Override
        protected void onPreExecute() {
            pDialog = ProgressDialog.show(StorePreviewActivity.this, "", "Loading audit summary.");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean ret = true;

            strDetailsBody = "";
            strSummaryBody = "";

            // STORE CATEGORY
            Cursor cursStoreCategory = sqlLibrary.RawQuerySelect("SELECT " + SQLiteDB.TABLE_STORECATEGORY + "." + SQLiteDB.COLUMN_STORECATEGORY_id + "," + SQLiteDB.COLUMN_CATEGORY_categoryorder
                    + "," + SQLiteDB.COLUMN_CATEGORY_categorydesc + "," + SQLiteDB.TABLE_STORECATEGORY + "." + SQLiteDB.COLUMN_STORECATEGORY_categoryid
                    + "," + SQLiteDB.COLUMN_STORECATEGORY_final + "," + SQLiteDB.COLUMN_STORECATEGORY_status + "," + SQLiteDB.TABLE_CATEGORY + "." + SQLiteDB.COLUMN_CATEGORY_categoryid + " AS webCategid"
                    + " FROM " + SQLiteDB.TABLE_STORECATEGORY
                    + " JOIN " + SQLiteDB.TABLE_CATEGORY + " ON " + SQLiteDB.TABLE_CATEGORY + "." + SQLiteDB.COLUMN_CATEGORY_id + " = " + SQLiteDB.TABLE_STORECATEGORY + "." + SQLiteDB.COLUMN_STORECATEGORY_categoryid
                    + " WHERE " + SQLiteDB.COLUMN_STORECATEGORY_storeid + " = " + previewStoreID
                    + " ORDER BY " + SQLiteDB.COLUMN_CATEGORY_categoryorder);
            cursStoreCategory.moveToFirst();

            while (!cursStoreCategory.isAfterLast()) {

                int storecategoryID = cursStoreCategory.getInt(cursStoreCategory.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORY_id));
                String categoryName = cursStoreCategory.getString(cursStoreCategory.getColumnIndex(SQLiteDB.COLUMN_CATEGORY_categorydesc));
                int categOrder = cursStoreCategory.getInt(cursStoreCategory.getColumnIndex(SQLiteDB.COLUMN_CATEGORY_categoryorder));
                int categoryid = cursStoreCategory.getInt(cursStoreCategory.getColumnIndex(SQLiteDB.COLUMN_CATEGORY_categoryid));
                String categoryFinal = cursStoreCategory.getString(cursStoreCategory.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORY_final));
                String categStatusno = cursStoreCategory.getString(cursStoreCategory.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORY_status));
                int webCategoryId = cursStoreCategory.getInt(cursStoreCategory.getColumnIndex("webCategid"));

                String strCategStatus = tcrLib.GetStatus(categStatusno);
                General.SCORE_STATUS categoryScore = tcrLib.GetScoreStatus(categoryFinal);

                arrCategories.add(new Category(categOrder, categoryid, categoryName, String.valueOf(storecategoryID), strCategStatus, categoryScore, webCategoryId));

                // STORE CATEGORY GROUP
                Cursor cursStoreCategoryGroups = sqlLibrary.RawQuerySelect("SELECT tblstorecateggroup.id, tblgroup.groupdesc, " + SQLiteDB.TABLE_GROUP + "." + SQLiteDB.COLUMN_GROUP_groupid + ", " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_final
                        + "," + SQLiteDB.COLUMN_STORECATEGORYGROUP_status  + "," + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_exempt
                        + "," + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_initial
                        + " FROM " + SQLiteDB.TABLE_STORECATEGORYGROUP
                        + " JOIN " + SQLiteDB.TABLE_GROUP + " ON " + SQLiteDB.TABLE_GROUP + "." + SQLiteDB.COLUMN_GROUP_id + " = " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_groupid
                        + " WHERE " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_storecategid + " = " + storecategoryID
                        + " ORDER BY " + SQLiteDB.COLUMN_GROUP_grouporder);
                cursStoreCategoryGroups.moveToFirst();

                while (!cursStoreCategoryGroups.isAfterLast()) {

                    String groupDesc = cursStoreCategoryGroups.getString(cursStoreCategoryGroups.getColumnIndex(SQLiteDB.COLUMN_GROUP_groupdesc));
                    int storeCategroupID = cursStoreCategoryGroups.getInt(cursStoreCategoryGroups.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORYGROUP_id));
                    String groupExempt = cursStoreCategoryGroups.getString(cursStoreCategoryGroups.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORYGROUP_exempt));
                    String groupInitial = cursStoreCategoryGroups.getString(cursStoreCategoryGroups.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORYGROUP_initial));
                    String groupFinal = cursStoreCategoryGroups.getString(cursStoreCategoryGroups.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORYGROUP_final));

                    if(!sqlLibrary.HasQuestionsPerGroup(storeCategroupID)) {
                        cursStoreCategoryGroups.moveToNext();
                        continue;
                    }

                    // STORE QUESTION
                    Cursor cursStoreQuestion = sqlLibrary.RawQuerySelect("SELECT * FROM " + SQLiteDB.TABLE_STOREQUESTION
                            + " JOIN " + SQLiteDB.TABLE_QUESTION + " ON " + SQLiteDB.TABLE_QUESTION + "." + SQLiteDB.COLUMN_QUESTION_id + " = " + SQLiteDB.TABLE_STOREQUESTION + "." + SQLiteDB.COLUMN_STOREQUESTION_questionid
                            + " WHERE " + SQLiteDB.COLUMN_STOREQUESTION_storecategorygroupid + " = " + storeCategroupID
                            + " ORDER BY " + SQLiteDB.COLUMN_QUESTION_order);
                    cursStoreQuestion.moveToFirst();

                    String answer = "";
                    String fullAnswer = "";
                    String formtypedesc = "";
                    int finalValue = 0;
                    int exemptValue = 0;
                    int initialValue = 0;

                    while (!cursStoreQuestion.isAfterLast()) {
                        String prompt = cursStoreQuestion.getString(cursStoreQuestion.getColumnIndex(SQLiteDB.COLUMN_QUESTION_prompt)).trim();
                        int formtypeid = cursStoreQuestion.getInt(cursStoreQuestion.getColumnIndex(SQLiteDB.COLUMN_QUESTION_formtypeid));
                        int formid = cursStoreQuestion.getInt(cursStoreQuestion.getColumnIndex(SQLiteDB.COLUMN_QUESTION_formid));
                        fullAnswer = "";
                        try {
                            answer = cursStoreQuestion.getString(cursStoreQuestion.getColumnIndex(SQLiteDB.COLUMN_STOREQUESTION_answer)).trim();
                            if(!answer.equals("")) fullAnswer = sqlLibrary.GetAnswer(answer, formtypeid, formid);
                            finalValue = cursStoreQuestion.getInt(cursStoreQuestion.getColumnIndex(SQLiteDB.COLUMN_STOREQUESTION_final));
                            exemptValue = cursStoreQuestion.getInt(cursStoreQuestion.getColumnIndex(SQLiteDB.COLUMN_STOREQUESTION_exempt));
                            initialValue = cursStoreQuestion.getInt(cursStoreQuestion.getColumnIndex(SQLiteDB.COLUMN_STOREQUESTION_initial));
                        }
                        catch (NullPointerException nex) { }

                        formtypedesc = sqlLibrary.GetFormTypeDesc(formtypeid);

                        // FOR MULTI LINE, REPLACE ENDLINES WITH WHITESPACE
                        if(formtypeid == 5) {
                            fullAnswer = fullAnswer.trim().replace("\n", " ");
                        }

                        strDetailsBody += categoryName + "|";
                        strDetailsBody += groupDesc + "|";
                        strDetailsBody += prompt + "|";
                        strDetailsBody += formtypedesc + "|";
                        strDetailsBody += fullAnswer + "|";
                        strDetailsBody += finalValue + "|";
                        strDetailsBody += exemptValue + "|";
                        strDetailsBody += initialValue;
                        strDetailsBody += "\n";

                        // FOR CONDITIONAL
                        if(formtypeid == 12) {
                            Cursor cursConditional = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_CONDITIONAL, SQLiteDB.COLUMN_CONDITIONAL_formid + " = '" + formid + "' AND " + SQLiteDB.COLUMN_CONDITIONAL_condition + " = '" + fullAnswer.trim().toUpperCase() + "'");
                            cursConditional.moveToFirst();

                            if(cursConditional.getCount() == 0) {
                                cursConditional.close();
                                cursStoreQuestion.moveToNext();
                                continue;
                            }

                            String[] aCondformids = null;

                            try {
                                aCondformids = cursConditional.getString(cursConditional.getColumnIndex(SQLiteDB.COLUMN_CONDITIONAL_conditionformsid)).trim().split("\\^");
                            }
                            catch (NullPointerException nex) {
                                nex.printStackTrace();
                                cursConditional.close();
                                cursStoreQuestion.moveToNext();
                                continue;
                            }

                            if(aCondformids.length > 0) {
                                for (String conformid : aCondformids) {

                                    if(conformid.equals("")) continue;

                                    Cursor cursforms = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_FORMS, SQLiteDB.COLUMN_FORMS_formid + " = '" + conformid + "'");
                                    cursforms.moveToFirst();

                                    String condPrompt = cursforms.getString(cursforms.getColumnIndex(SQLiteDB.COLUMN_FORMS_prompt));
                                    int condtypeid = cursforms.getInt(cursforms.getColumnIndex(SQLiteDB.COLUMN_FORMS_typeid));
                                    formtypedesc = sqlLibrary.GetFormTypeDesc(condtypeid);

                                    // GET CHILD ANSWERS OF CONDITIONAL
                                    Cursor cursCondAns = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_CONDITIONAL_ANSWERS, SQLiteDB.COLUMN_CONDANS_conditionalformid + " = '" + conformid + "' AND " + SQLiteDB.COLUMN_CONDANS_conditionalformtypeid + " = '" + condtypeid + "'");
                                    cursCondAns.moveToFirst();
                                    String childAnswer = "";
                                    if (cursCondAns.getCount() > 0) {
                                        childAnswer = cursCondAns.getString(cursCondAns.getColumnIndex(SQLiteDB.COLUMN_CONDANS_conditionalanswer)).trim();
                                    }

                                    cursCondAns.close();

                                    fullAnswer = childAnswer;

                                    // FOR MULTI LINE, REPLACE ENDLINES WITH WHITESPACE
                                    if(condtypeid == 5 && !childAnswer.equals("")) {
                                        fullAnswer = childAnswer.trim().replace("\n", " ");
                                    }

                                    // SINGLE ITEM
                                    if (condtypeid == 10 && !childAnswer.equals("")) { // set full answer for single item
                                        int nSingleselectId = Integer.valueOf(childAnswer);
                                        // Get option id by formid and singleselect id
                                        Cursor cursCondSingle = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_SINGLESELECT, SQLiteDB.COLUMN_SINGLESELECT_formid + " = '" + conformid + "' AND " + SQLiteDB.COLUMN_SINGLESELECT_id + " = '" + nSingleselectId + "'");
                                        cursCondSingle.moveToFirst();
                                        String condOptionid = cursCondSingle.getString(cursCondSingle.getColumnIndex(SQLiteDB.COLUMN_SINGLESELECT_optionid)).trim();
                                        fullAnswer = sqlLibrary.GetAnswer(condOptionid, condtypeid, Integer.valueOf(conformid));
                                        cursCondSingle.close();
                                    }

                                    // MULTI ITEM
                                    if (condtypeid == 9 && !childAnswer.equals("")) { // set full answer for multi item
                                        String[] arrAns = childAnswer.split(",");
                                        String ans = "";
                                        for (String cboxAns : arrAns) {
                                            Cursor cursMulti = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_MULTISELECT, SQLiteDB.COLUMN_MULTISELECT_optionid + " = '" + cboxAns + "'");
                                            cursMulti.moveToNext();
                                            ans += cursMulti.getString(cursMulti.getColumnIndex(SQLiteDB.COLUMN_MULTISELECT_option)).trim() + "-";
                                            cursMulti.close();
                                        }
                                        fullAnswer = ans;
                                    }

                                    // LABEL
                                    if (condtypeid == 1) {
                                        fullAnswer = "";
                                        finalValue = 0;
                                        exemptValue = 0;
                                        initialValue = 0;
                                    }

                                    strDetailsBody += categoryName + "|";
                                    strDetailsBody += groupDesc + "|";
                                    strDetailsBody += condPrompt + "|";
                                    strDetailsBody += formtypedesc + "|";
                                    strDetailsBody += fullAnswer + "|";
                                    strDetailsBody += finalValue + "|";
                                    strDetailsBody += exemptValue + "|";
                                    strDetailsBody += initialValue;
                                    strDetailsBody += "\n";

                                    cursforms.close();
                                }
                            }

                            cursConditional.close();
                        }

                        cursStoreQuestion.moveToNext();
                    }

                    if(!toggleAudit) {
                        strSummaryBody += "audit_summary\n";
                        toggleAudit = true;
                    }
                    strSummaryBody += categoryName + "|" + groupDesc + "|" + groupFinal + "|" + groupExempt + "|" + groupInitial;
                    strSummaryBody += "\n";

                    cursStoreQuestion.close();
                    cursStoreCategoryGroups.moveToNext();
                }
                cursStoreCategoryGroups.close();
                cursStoreCategory.moveToNext();
            }

            strDetailsBody += strSummaryBody;
            cursStoreCategory.close();

            return ret;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pDialog.dismiss();
            if(!aBoolean) {
                Toast.makeText(StorePreviewActivity.this, errmsg, Toast.LENGTH_LONG).show();
                return;
            }
            lvwPreview.setAdapter(new PreviewCategoryAdapter(StorePreviewActivity.this, arrCategories));
            lvwPreview.setSmoothScrollbarEnabled(true);

            DisplayPercentages();
        }
    }

    private void DisplayPercentages() {

    }

    public class AsyncGenerateTextFile extends AsyncTask<Void, Void, Boolean> {

        private String exError;

        @Override
        protected void onPreExecute() {
            progressDL = ProgressDialog.show(StorePreviewActivity.this, "", "Creating textfile....", true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if(filepathToSend.exists()) filepathToSend.delete();

            try
            {
                FileWriter writer = new FileWriter(filepathToSend);

                String sBody = "";

                cursStore.moveToFirst();

                if(cursStore.getCount() > 0) {

                    String account = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_account));
                    String customerCode = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_customercode));
                    String customer = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_customer));
                    String regionCode = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_regioncode));
                    String region = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_region));
                    String distributorCode = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_distributorcode));
                    String distributor = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_distributor));
                    String templateCode = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_templatecode));
                    String auditId = cursStore.getString(cursStore.getColumnIndex(SQLiteDB.COLUMN_STORE_auditid));

                    sBody += General.usercode + "|"
                            + auditId + "|"
                            + account + "|"
                            + customerCode + "|"
                            + customer + "|"
                            + regionCode + "|"
                            + region + "|"
                            + distributorCode + "|"
                            + distributor + "|"
                            + storeCode + "|"
                            + storename + "|"
                            + templateCode + "|"
                            + storeTemplate + "|"
                            + storeFinalValue + "|"
                            + strOsa + "|"
                            + strNpi + "|"
                            + strPlanogram;

                    sBody += "\n";
                    sBody += strDetailsBody;
                }

                writer.append(sBody);
                writer.flush();
                writer.close();
                return true;
            }
            catch(IOException e)
            {
                progressDL.dismiss();
                e.printStackTrace();
                Log.e("IOException", e.getMessage());
                exError = e.getMessage();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if(!s) {
                wlStayAwake.release();
                progressDL.dismiss();
                Toast.makeText(StorePreviewActivity.this, exError, Toast.LENGTH_LONG).show();
                return;
            }
            progressDL.dismiss();
            new AsyncPostFiles(filepathToSend, strFilenameToSend, General.POSTING_URL).execute();
        }
    }

    public class AsyncPostImages extends AsyncTask<Integer, Integer, Boolean> {

        private Integer maxnum = 0;
        private String response;
        private ArrayList<String> aStrFilename;
        private ArrayList<File> aFileImage;

        public AsyncPostImages(Integer nMax, ArrayList<String> arrStrFilename, ArrayList<File> arrfilePic) {
            this.maxnum = nMax;
            this.aStrFilename = arrStrFilename;
            this.aFileImage = arrfilePic;
        }

        @Override
        protected void onPreExecute() {
            progressDL = ProgressDialog.show(StorePreviewActivity.this, "", "Posting images.");
            progressDL.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDL.setMax(this.maxnum);
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            Boolean bReturn = false;

            String attachmentName = "data";
            String attachmentFileName;
            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";

            response = "";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1*1024*1024;

            try {

                for (int i = 0; i < aFileImage.size(); i++) {

                    attachmentFileName = aStrFilename.get(i);
                    FileInputStream fileInputStream = new FileInputStream(aFileImage.get(i)); // text file to upload

                    HttpURLConnection httpUrlConnection = null;
                    URL url = new URL(General.POSTING_IMAGE + "/" + strImageFolder); // url to post
                    httpUrlConnection = (HttpURLConnection) url.openConnection();
                    httpUrlConnection.setUseCaches(false);
                    httpUrlConnection.setDoOutput(true);

                    httpUrlConnection.setRequestMethod("POST");
                    httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
                    httpUrlConnection.setRequestProperty(
                            "Content-Type", "multipart/form-data;boundary=" + boundary);

                    DataOutputStream request = new DataOutputStream(
                            httpUrlConnection.getOutputStream());

                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"" +
                            attachmentName + "\";filename=\"" + attachmentFileName + "\"" + crlf);
                    request.writeBytes(crlf);

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // Read file
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        request.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    //I want to send only 8 bit black & white bitmaps
/*                byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
                for (int i = 0; i < bitmap.getWidth(); ++i) {
                    for (int j = 0; j < bitmap.getHeight(); ++j) {
                        //we're interested only in the MSB of the first byte,
                        //since the other 3 bytes are identical for B&W images
                        pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                    }
                }

                request.write(pixels);*/

                    request.writeBytes(crlf);
                    request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
                    request.flush();
                    request.close();

                    InputStream responseStream = new
                            BufferedInputStream(httpUrlConnection.getInputStream());

                    BufferedReader responseStreamReader =
                            new BufferedReader(new InputStreamReader(responseStream));

                    String line = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((line = responseStreamReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    responseStreamReader.close();

                    response = stringBuilder.toString();

                    responseStream.close();
                    httpUrlConnection.disconnect();
                }
                bReturn =  true;
            }
            catch (IOException ex) {
                response = ex.getMessage() != null ? ex.getMessage() : "Slow or unstable internet connection.";
                Log.e("Posting Image", response);
                ex.printStackTrace();
            }

            return bReturn;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDL.dismiss();
            if(!aBoolean) {
                Toast.makeText(StorePreviewActivity.this, response, Toast.LENGTH_LONG).show();
                return;
            }

            postDialog = new AlertDialog.Builder(StorePreviewActivity.this).create();
            postDialog.setTitle("Success");
            postDialog.setMessage("Survey is successfully posted!\n\nMessage: " + response);
            postDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    postDialog.dismiss();
                    finish();
                }
            });
            postDialog.show();
        }
    }

    // send file
    public class AsyncPostFiles extends AsyncTask<Void, Void, String> {

        private final File fileToSend;
        private final String postingURL;
        private final String strFilename;

        public AsyncPostFiles(File filepath, String strFilename, String url) {
            this.fileToSend = filepath;
            this.postingURL = url.trim();
            this.strFilename = strFilename;
        }

        @Override
        protected void onPreExecute() {
            progressDL = ProgressDialog.show(StorePreviewActivity.this, "", "Posting audit summary result.", true);
        }

        @Override
        protected String doInBackground(Void... params) {

            String attachmentName = "data";
            String attachmentFileName = strFilename;
            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";

            String response = "";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1*1024*1024;

            try {

                FileInputStream fileInputStream = new FileInputStream(fileToSend); // text file to upload
                HttpURLConnection httpUrlConnection = null;
                URL url = new URL(postingURL); // url to post
                httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setUseCaches(false);
                httpUrlConnection.setDoOutput(true);

                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
                httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream request = new DataOutputStream(
                        httpUrlConnection.getOutputStream());

                request.writeBytes(twoHyphens + boundary + crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"" +
                        attachmentName + "\";filename=\"" + attachmentFileName + "\"" + crlf);
                request.writeBytes(crlf);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                    request.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                //I want to send only 8 bit black & white bitmaps
/*                byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
                for (int i = 0; i < bitmap.getWidth(); ++i) {
                    for (int j = 0; j < bitmap.getHeight(); ++j) {
                        //we're interested only in the MSB of the first byte,
                        //since the other 3 bytes are identical for B&W images
                        pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                    }
                }

                request.write(pixels);*/

                request.writeBytes(crlf);
                request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
                request.flush();
                request.close();

                InputStream responseStream = new
                        BufferedInputStream(httpUrlConnection.getInputStream());

                BufferedReader responseStreamReader =
                        new BufferedReader(new InputStreamReader(responseStream));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();

                response = stringBuilder.toString();
                responseStream.close();
                httpUrlConnection.disconnect();

                return response;
            }
            catch (final MalformedURLException ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageBox.ShowMessage("MalformedURLException", ex.getMessage());
                        ex.printStackTrace();
                    }
                });
            }
            catch (final ProtocolException pex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageBox.ShowMessage("ProtocolException", pex.getMessage());
                        pex.printStackTrace();
                    }
                });
            }
            catch (final IOException ioex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageBox.ShowMessage("IOException", ioex.getMessage());
                        ioex.printStackTrace();
                    }
                });
            }
            finally {
                return response;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDL.dismiss();
            wlStayAwake.release();
            if(s == null) {
                Toast.makeText(StorePreviewActivity.this, "Something went wrong, posting of survey is cancelled", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                JSONObject data = new JSONObject(s);

                if (!data.isNull("msg")) {
                    String status = data.getString("status");
                    String msg = data.getString("msg");

                    if(status.equals("1")) {
                        postDialog = new AlertDialog.Builder(StorePreviewActivity.this).create();
                        postDialog.setTitle("Unsuccessful");
                        postDialog.setMessage("Survey not posted. There's a problem in posting data to web server: \n" + msg);
                        postDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postDialog.dismiss();
                            }
                        });
                        postDialog.show();
                        return;
                    }

                    sqlLibrary.ExecSQLWrite("UPDATE " + SQLiteDB.TABLE_STORE
                            + " SET " + SQLiteDB.COLUMN_STORE_posted + " = '1', "
                            + SQLiteDB.COLUMN_STORE_postingdate + " = '" + General.getDateToday() + "', " + SQLiteDB.COLUMN_STORE_postingtime + " = '" + General.getTimeToday() + "'  WHERE " + SQLiteDB.COLUMN_STORE_id + " = '" + previewStoreID + "'");

                    // posting successful
                    strImageFolder = data.getString("audit_id");

                    new AsyncPostFile2(filepathToSend, strFilenameToSend, General.POSTING_DETAILS_URL).execute();
                }
                else {
                    postDialog = new AlertDialog.Builder(StorePreviewActivity.this).create();
                    postDialog.setTitle("Unsuccessful");
                    postDialog.setMessage("Error in posting survey");
                    postDialog.setCancelable(false);
                    postDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            postDialog.dismiss();
                        }
                    });
                }
            }
            catch (JSONException jex) {
                jex.printStackTrace();
                Log.e("JSONException", jex.getMessage());
            }
        }
    }

    public class AsyncPostFile2 extends AsyncTask<Void, Void, Boolean> {

        private final File fileToSend;
        private final String postingURL;
        private final String strFilename;
        String response = "";
        String error = "";

        public AsyncPostFile2(File filepath, String strFilename, String url) {
            this.fileToSend = filepath;
            this.postingURL = url.trim();
            this.strFilename = strFilename;
        }

        @Override
        protected void onPreExecute() {
            progressDL = ProgressDialog.show(StorePreviewActivity.this, "", "Posting audit details.", true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean result = false;

            String attachmentName = "data";
            String attachmentFileName = strFilename;
            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";

            String response = "";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1*1024*1024;

            try {

                FileInputStream fileInputStream = new FileInputStream(fileToSend); // text file to upload
                HttpURLConnection httpUrlConnection = null;
                URL url = new URL(postingURL); // url to post
                httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setUseCaches(false);
                httpUrlConnection.setDoOutput(true);

                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
                httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream request = new DataOutputStream(
                        httpUrlConnection.getOutputStream());

                request.writeBytes(twoHyphens + boundary + crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"" +
                        attachmentName + "\";filename=\"" + attachmentFileName + "\"" + crlf);
                request.writeBytes(crlf);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                    request.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                //I want to send only 8 bit black & white bitmaps
/*                byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
                for (int i = 0; i < bitmap.getWidth(); ++i) {
                    for (int j = 0; j < bitmap.getHeight(); ++j) {
                        //we're interested only in the MSB of the first byte,
                        //since the other 3 bytes are identical for B&W images
                        pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                    }
                }

                request.write(pixels);*/

                request.writeBytes(crlf);
                request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
                request.flush();
                request.close();

                InputStream responseStream = new
                        BufferedInputStream(httpUrlConnection.getInputStream());

                BufferedReader responseStreamReader =
                        new BufferedReader(new InputStreamReader(responseStream));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();

                response = stringBuilder.toString();
                responseStream.close();
                httpUrlConnection.disconnect();

                result = true;
            }
            catch (final MalformedURLException ex) {
                ex.printStackTrace();
                error = ex.getMessage();
            }
            catch (final ProtocolException pex) {
                pex.printStackTrace();
                error = pex.getMessage();
            }
            catch (final IOException ioex) {
                ioex.printStackTrace();
                error = ioex.getMessage();
            }
            finally {
                return result;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDL.dismiss();
            if(!aBoolean) {
                Toast.makeText(StorePreviewActivity.this, error, Toast.LENGTH_LONG).show();
                return;
            }

            // STORE CATEGORY
            Cursor cursStoreCategory = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_STORECATEGORY, SQLiteDB.COLUMN_STORECATEGORY_storeid + " = '" + previewStoreID + "' AND " + SQLiteDB.COLUMN_STORECATEGORY_status + " > '0'");
            cursStoreCategory.moveToFirst();

            int totalImages = 0;
            ArrayList<String> aStrFilenames = new ArrayList<>();
            ArrayList<File> aFileImages = new ArrayList<>();

            while (!cursStoreCategory.isAfterLast()) {

                int storecategoryID = cursStoreCategory.getInt(cursStoreCategory.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORY_id));

                // STORE CATEGORY GROUP
                Cursor cursStoreCategoryGroups = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_STORECATEGORYGROUP, SQLiteDB.COLUMN_STORECATEGORYGROUP_storecategid + " = '" + storecategoryID + "' AND " + SQLiteDB.COLUMN_STORECATEGORYGROUP_status + " > '0'");
                cursStoreCategoryGroups.moveToFirst();

                while (!cursStoreCategoryGroups.isAfterLast()) {

                    int storeCategroupID = cursStoreCategoryGroups.getInt(cursStoreCategoryGroups.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORYGROUP_id));

                    if(!sqlLibrary.HasQuestionsPerGroup(storeCategroupID)) {
                        cursStoreCategoryGroups.moveToNext();
                        continue;
                    }

                    // STORE QUESTION IMAGES
                    Cursor cursImages = sqlLibrary.RawQuerySelect("SELECT " + SQLiteDB.COLUMN_STOREQUESTION_storecategorygroupid + "," + SQLiteDB.COLUMN_STOREQUESTION_answer + "," + SQLiteDB.COLUMN_STOREQUESTION_isAnswered
                            + " FROM " + SQLiteDB.TABLE_STOREQUESTION
                            + " JOIN " + SQLiteDB.TABLE_QUESTION + " ON " + SQLiteDB.TABLE_QUESTION + "." + SQLiteDB.COLUMN_QUESTION_id + " = " + SQLiteDB.TABLE_STOREQUESTION + "." + SQLiteDB.COLUMN_STOREQUESTION_questionid
                            + " WHERE " + SQLiteDB.COLUMN_QUESTION_formtypeid + " = '2' AND " + SQLiteDB.COLUMN_STOREQUESTION_isAnswered + " = '1' AND " + SQLiteDB.COLUMN_STOREQUESTION_storecategorygroupid + " = " + storeCategroupID
                            + " ORDER BY " + SQLiteDB.COLUMN_QUESTION_order);
                    cursImages.moveToFirst();
                    totalImages += cursImages.getCount();

                    while (!cursImages.isAfterLast()) {
                        String strFilename = cursImages.getString(cursImages.getColumnIndex(SQLiteDB.COLUMN_STOREQUESTION_answer)).trim();
                        File file = new File(Settings.captureFolder, strFilename);
                        aStrFilenames.add(strFilename);
                        aFileImages.add(file);
                        cursImages.moveToNext();
                    }
                    cursImages.close();

                    cursStoreCategoryGroups.moveToNext();
                }

                cursStoreCategoryGroups.close();
                cursStoreCategory.moveToNext();
            }

            cursStoreCategory.close();

            new AsyncPostImages(totalImages, aStrFilenames, aFileImages).execute();
        }
    }

    // GET OSA LIST AND LOOKUP
    private boolean GetOsaListLookup(int correctAnswer, int formgroupID, int categoryid) {

        boolean res = false;

        Cursor cursOsalist = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_OSALIST, SQLiteDB.COLUMN_OSALIST_osakeygroupid + " = '" + formgroupID + "'");
        cursOsalist.moveToFirst();

        if(cursOsalist.getCount() > 0) {

            Cursor cursGetOsalookup = sqlLibrary.GetDataCursor(SQLiteDB.TABLE_OSALOOKUP, SQLiteDB.COLUMN_OSALOOKUP_storeid + " = " + tempStoreId + " AND " + SQLiteDB.COLUMN_OSALOOKUP_categoryid + " = " + categoryid);
            cursGetOsalookup.moveToFirst();

            if(cursGetOsalookup.getCount() > 0) {
                int target = cursGetOsalookup.getInt(cursGetOsalookup.getColumnIndex(SQLiteDB.COLUMN_OSALOOKUP_target));
                if(correctAnswer >= target)
                    res = true;
            }
        }

        return res;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition( R.anim.hold, R.anim.slide_down );
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
