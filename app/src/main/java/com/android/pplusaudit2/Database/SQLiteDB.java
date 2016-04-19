package com.android.pplusaudit2.Database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.pplusaudit2.MyMessageBox;

/**
 * Created by LLOYD on 9/30/2015.
 */
public class SQLiteDB extends SQLiteOpenHelper {
    MyMessageBox messageBox;

    private static final String DATABASE_NAME = "unileverdb";
    private static final String TAG = "SettingsProvider";
    private static final int DATABASE_VERSION = 2;

    public SQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //USER TABLE
    public static final String TABLE_USER = "tbluser";
    public static final String COLUMN_USER_id = "userid";
    public static final String COLUMN_USER_code = "code";
    public static final String COLUMN_USER_name = "name";

    private static final String DATABASE_CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_USER_code + " numeric, "
            + COLUMN_USER_name + " text)";

    //STORE TABLE
    public static final String TABLE_STORE = "tblstore";
    public static final String COLUMN_STORE_id = "id";
    public static final String COLUMN_STORE_posted = "posted";
    public static final String COLUMN_STORE_postingdate = "posting_date";
    public static final String COLUMN_STORE_postingtime = "posting_time";
    public static final String COLUMN_STORE_storeid = "storeid";
    public static final String COLUMN_STORE_name = "store";
    public static final String COLUMN_STORE_gradematrixid = "grade_matrix_id";
    public static final String COLUMN_STORE_audittempid = "audit_template_id";
    public static final String COLUMN_STORE_templatename = "template_name";
    public static final String COLUMN_STORE_status = "status";
    public static final String COLUMN_STORE_initial = "store_initial";
    public static final String COLUMN_STORE_exempt = "store_exempt";
    public static final String COLUMN_STORE_final = "store_final";
    public static final String COLUMN_STORE_startdate = "start_date";
    public static final String COLUMN_STORE_enddate = "end_date";
    public static final String COLUMN_STORE_storecode = "storecode";
    public static final String COLUMN_STORE_account = "account";
    public static final String COLUMN_STORE_customercode = "customer_code";
    public static final String COLUMN_STORE_customer = "customer";
    public static final String COLUMN_STORE_regioncode = "region_code";
    public static final String COLUMN_STORE_region = "region";
    public static final String COLUMN_STORE_distributorcode = "distributor_code";
    public static final String COLUMN_STORE_distributor = "distributor";
    public static final String COLUMN_STORE_templatecode = "template_code";
    public static final String COLUMN_STORE_auditid = "auditID"; // new field

    private static final String DATABASE_CREATE_TABLE_STORE = "CREATE TABLE " + TABLE_STORE + "("
            + COLUMN_STORE_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_STORE_posted + " numeric, "
            + COLUMN_STORE_postingdate + " text, "
            + COLUMN_STORE_postingtime + " text, "
            + COLUMN_STORE_storeid + " numeric, "
            + COLUMN_STORE_name + " text, "
            + COLUMN_STORE_gradematrixid + " numeric, "
            + COLUMN_STORE_audittempid + " numeric, "
            + COLUMN_STORE_templatename + " text, "
            + COLUMN_STORE_status + " int, "
            + COLUMN_STORE_initial + " int, "
            + COLUMN_STORE_exempt + " int, "
            + COLUMN_STORE_final + " int, "
            + COLUMN_STORE_startdate + " text, "
            + COLUMN_STORE_enddate + " text, "
            + COLUMN_STORE_storecode + " text, "
            + COLUMN_STORE_account + " text, "
            + COLUMN_STORE_customercode + " text, "
            + COLUMN_STORE_customer + " text, "
            + COLUMN_STORE_regioncode + " text, "
            + COLUMN_STORE_region + " text, "
            + COLUMN_STORE_distributorcode + " text, "
            + COLUMN_STORE_distributor + " text, "
            + COLUMN_STORE_templatecode + " text, "
            + COLUMN_STORE_auditid + " numeric)";


    // CATEGORY TABLE
    public static final String TABLE_CATEGORY = "tblcategory";
    public static final String COLUMN_CATEGORY_id = "id";
    public static final String COLUMN_CATEGORY_audittempid = "audit_template_id";
    public static final String COLUMN_CATEGORY_categoryorder = "categoryorder";
    public static final String COLUMN_CATEGORY_categoryid = "category_id";
    public static final String COLUMN_CATEGORY_categorydesc = "categorydesc";

    private static final String DATABASE_CREATE_TABLE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + "("
            + COLUMN_CATEGORY_id + " integer PRIMARY KEY, "
            + COLUMN_CATEGORY_audittempid + " numeric, "
            + COLUMN_CATEGORY_categoryorder + " numeric, "
            + COLUMN_CATEGORY_categoryid + " numeric, "
            + COLUMN_CATEGORY_categorydesc + " text)";

    // GROUP TABLE
    public static final String TABLE_GROUP = "tblgroup";
    public static final String COLUMN_GROUP_id = "id";
    public static final String COLUMN_GROUP_audittempid = "audit_template_id";
    public static final String COLUMN_GROUP_categoryid = "category_id";
    public static final String COLUMN_GROUP_grouporder = "grouporder";
    public static final String COLUMN_GROUP_groupid = "formgroupid";
    public static final String COLUMN_GROUP_groupdesc = "groupdesc";

    private static final String DATABASE_CREATE_TABLE_GROUP = "CREATE TABLE " + TABLE_GROUP + "("
            + COLUMN_GROUP_id + " integer PRIMARY KEY, "
            + COLUMN_GROUP_audittempid + " numeric, "
            + COLUMN_GROUP_categoryid + " numeric, "
            + COLUMN_GROUP_grouporder + " numeric, "
            + COLUMN_GROUP_groupid + " numeric, "
            + COLUMN_GROUP_groupdesc + " text)";

    //QUESTIONS TABLE
    public static final String TABLE_QUESTION = "tblquestion";
    public static final String COLUMN_QUESTION_id = "id";
    public static final String COLUMN_QUESTION_questionid = "questionid";
    public static final String COLUMN_QUESTION_order = "questionorder";
    public static final String COLUMN_QUESTION_groupid = "audit_template_group_id";
    public static final String COLUMN_QUESTION_audittempid = "audit_template_id";
    public static final String COLUMN_QUESTION_formid = "form_id";
    public static final String COLUMN_QUESTION_formtypeid = "form_type_id";
    public static final String COLUMN_QUESTION_prompt = "prompt";
    public static final String COLUMN_QUESTION_brandpic = "picture";
    public static final String COLUMN_QUESTION_required = "required";
    public static final String COLUMN_QUESTION_expectedans = "expected_answer";
    public static final String COLUMN_QUESTION_exempt = "exempt";
    //NEW COLUMNS
    public static final String COLUMN_QUESTION_defaultans = "default_answer";

    private static final String DATABASE_CREATE_TABLE_QUESTION = "CREATE TABLE " + TABLE_QUESTION + "("
            + COLUMN_QUESTION_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_QUESTION_questionid + " numeric, "
            + COLUMN_QUESTION_order + " numeric, "
            + COLUMN_QUESTION_groupid + " numeric, "
            + COLUMN_QUESTION_audittempid + " numeric, "
            + COLUMN_QUESTION_formid + " numeric, "
            + COLUMN_QUESTION_formtypeid + " numeric, "
            + COLUMN_QUESTION_prompt + " text, "
            + COLUMN_QUESTION_brandpic + " text, "
            + COLUMN_QUESTION_required + " integer, "
            + COLUMN_QUESTION_expectedans + " text, "
            + COLUMN_QUESTION_exempt + " integer, "
            + COLUMN_QUESTION_defaultans + " text)";

    //FORMS TABLE
    public static final String TABLE_FORMS = "tblforms";
    public static final String COLUMN_FORMS_id = "id";
    public static final String COLUMN_FORMS_formid = "formid";
    public static final String COLUMN_FORMS_audittempid = "audittemplateid";
    public static final String COLUMN_FORMS_typeid = "typeid";
    public static final String COLUMN_FORMS_prompt = "prompt";
    public static final String COLUMN_FORMS_picture = "formPicture";
    public static final String COLUMN_FORMS_required = "required";
    public static final String COLUMN_FORMS_expected = "expected_answer";
    public static final String COLUMN_FORMS_exempt = "exempt";
    //NEW COLUMNS
    public static final String COLUMN_FORMS_defaultans = "default_answer";

    private static final String DATABASE_CREATE_TABLE_FORMS = "CREATE TABLE " + TABLE_FORMS + "("
            + COLUMN_FORMS_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_FORMS_formid + " numeric, "
            + COLUMN_FORMS_audittempid + " numeric, "
            + COLUMN_FORMS_typeid + " numeric, "
            + COLUMN_FORMS_prompt + " text, "
            + COLUMN_FORMS_picture + " text, "
            + COLUMN_FORMS_required + " integer, "
            + COLUMN_FORMS_expected + " text, "
            + COLUMN_FORMS_exempt + " integer, "
            + COLUMN_FORMS_defaultans + " text)";

    //SINGLE SELECT QUESTION TABLE
    public static final String TABLE_SINGLESELECT = "tblsingleselect";
    public static final String COLUMN_SINGLESELECT_id = "singleselectid";
    public static final String COLUMN_SINGLESELECT_formid = "formid";
    public static final String COLUMN_SINGLESELECT_optionid = "optionid";
    public static final String COLUMN_SINGLESELECT_option = "option";

    private static final String DATABASE_CREATE_TABLE_SINGLESELECT = "CREATE TABLE " + TABLE_SINGLESELECT + "("
            + COLUMN_SINGLESELECT_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_SINGLESELECT_formid + " numeric, "
            + COLUMN_SINGLESELECT_optionid + " numeric, "
            + COLUMN_SINGLESELECT_option + " text)";


    //MULTI SELECT QUESTION TABLE
    public static final String TABLE_MULTISELECT = "tblmultiselect";
    public static final String COLUMN_MULTISELECT_id = "multiselectid";
    public static final String COLUMN_MULTISELECT_formid = "formid";
    public static final String COLUMN_MULTISELECT_optionid = "optionid";
    public static final String COLUMN_MULTISELECT_option = "option";

    private static final String DATABASE_CREATE_TABLE_MULTISELECT= "CREATE TABLE " + TABLE_MULTISELECT + "("
            + COLUMN_MULTISELECT_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_MULTISELECT_formid + " numeric, "
            + COLUMN_MULTISELECT_optionid + " numeric, "
            + COLUMN_MULTISELECT_option + " text)";


    //COMPUTATIONAL QUESTION TABLE
    public static final String TABLE_COMPUTATIONAL = "tblcomputational";
    public static final String COLUMN_COMPUTATIONAL_id = "compid";
    public static final String COLUMN_COMPUTATIONAL_formid = "formid";
    public static final String COLUMN_COMPUTATIONAL_formula = "formula";

    private static final String DATABASE_CREATE_TABLE_COMPUTATIONAL = "CREATE TABLE " + TABLE_COMPUTATIONAL + "("
            + COLUMN_COMPUTATIONAL_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_COMPUTATIONAL_formid + " numeric, "
            + COLUMN_COMPUTATIONAL_formula+ " text)";

    //CONDITIONAL QUESTION TABLE
    public static final String TABLE_CONDITIONAL = "tblconditional";
    public static final String COLUMN_CONDITIONAL_id = "condid";
    public static final String COLUMN_CONDITIONAL_formid = "formid";
    public static final String COLUMN_CONDITIONAL_condition = "condition";
    public static final String COLUMN_CONDITIONAL_conditionformsid = "condformsid";
    //NEW COLUMNS
    public static final String COLUMN_CONDITIONAL_optionid = "cond_option_id";

    private static final String DATABASE_CREATE_TABLE_CONDITIONAL = "CREATE TABLE " + TABLE_CONDITIONAL + "("
            + COLUMN_CONDITIONAL_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_CONDITIONAL_formid + " numeric, "
            + COLUMN_CONDITIONAL_condition + " text, "
            + COLUMN_CONDITIONAL_conditionformsid + " text, "
            + COLUMN_CONDITIONAL_optionid + " numeric)";

    // CONDITIONAL ANSWERS TABLE
    public static final String TABLE_CONDITIONAL_ANSWERS = "tblconditional_answers";
    public static final String COLUMN_CONDANS_id = "answerid";
    public static final String COLUMN_CONDANS_questionid = "store_question_id";
    public static final String COLUMN_CONDANS_conditionalformtypeid = "conditional_formtype_id";
    public static final String COLUMN_CONDANS_conditionalformid = "conditional_form_id";
    public static final String COLUMN_CONDANS_conditionalanswer = "conditional_answer";

    private static final String DATABASE_CREATE_TABLE_CONDITIONAL_ANSWERS = "CREATE TABLE " + TABLE_CONDITIONAL_ANSWERS + "("
            + COLUMN_CONDANS_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_CONDANS_questionid + " numeric, "
            + COLUMN_CONDANS_conditionalformid + " numeric, "
            + COLUMN_CONDANS_conditionalformtypeid + " numeric, "
            + COLUMN_CONDANS_conditionalanswer + " text)";


    //STORE CATEGORY TABLE
    public static final String TABLE_STORECATEGORY = "tblstorecategory";
    public static final String COLUMN_STORECATEGORY_id = "id";
    public static final String COLUMN_STORECATEGORY_storeid = "store_id";
    public static final String COLUMN_STORECATEGORY_categoryid = "category_id";
    public static final String COLUMN_STORECATEGORY_initial = "initial";
    public static final String COLUMN_STORECATEGORY_exempt = "exempt";
    public static final String COLUMN_STORECATEGORY_final = "final";
    public static final String COLUMN_STORECATEGORY_status = "storecategstatus";

    private static final String DATABASE_CREATE_TABLE_STORECATEGORY = "CREATE TABLE " + TABLE_STORECATEGORY + "("
            + COLUMN_STORECATEGORY_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_STORECATEGORY_storeid + " numeric, "
            + COLUMN_STORECATEGORY_categoryid + " numeric, "
            + COLUMN_STORECATEGORY_initial + " int, "
            + COLUMN_STORECATEGORY_exempt + " int, "
            + COLUMN_STORECATEGORY_final + " int, "
            + COLUMN_STORECATEGORY_status + " int)";


    //STORE CATEGORY GROUP TABLE
    public static final String TABLE_STORECATEGORYGROUP = "tblstorecateggroup";
    public static final String COLUMN_STORECATEGORYGROUP_id = "id";
    public static final String COLUMN_STORECATEGORYGROUP_storecategid = "store_category_id";
    public static final String COLUMN_STORECATEGORYGROUP_groupid = "group_id";
    public static final String COLUMN_STORECATEGORYGROUP_initial = "initial";
    public static final String COLUMN_STORECATEGORYGROUP_exempt = "exempt";
    public static final String COLUMN_STORECATEGORYGROUP_final = "final";
    public static final String COLUMN_STORECATEGORYGROUP_status = "scgroupstatus";

    private static final String DATABASE_CREATE_TABLE_STORECATEGORYGROUP = "CREATE TABLE " + TABLE_STORECATEGORYGROUP + "("
            + COLUMN_STORECATEGORYGROUP_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_STORECATEGORYGROUP_storecategid + " numeric, "
            + COLUMN_STORECATEGORYGROUP_groupid + " numeric, "
            + COLUMN_STORECATEGORYGROUP_initial + " int, "
            + COLUMN_STORECATEGORYGROUP_exempt + " int, "
            + COLUMN_STORECATEGORYGROUP_final + " int, "
            + COLUMN_STORECATEGORYGROUP_status + " int)";

    //STORE QUESTION TABLE
    public static final String TABLE_STOREQUESTION = "tblstorequestion";
    public static final String COLUMN_STOREQUESTION_id = "storequestionid";
    public static final String COLUMN_STOREQUESTION_storecategorygroupid = "store_category_group_id";
    public static final String COLUMN_STOREQUESTION_questionid = "questionid";
    public static final String COLUMN_STOREQUESTION_answer = "answer";
    public static final String COLUMN_STOREQUESTION_isAnswered = "isAnswered";
    public static final String COLUMN_STOREQUESTION_initial= "initial";
    public static final String COLUMN_STOREQUESTION_exempt = "exempt";
    public static final String COLUMN_STOREQUESTION_final = "final";

    private static final String DATABASE_CREATE_TABLE_STOREQUESTION = "CREATE TABLE " + TABLE_STOREQUESTION + "("
            + COLUMN_STOREQUESTION_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_STOREQUESTION_storecategorygroupid + " numeric, "
            + COLUMN_STOREQUESTION_questionid + " numeric, "
            + COLUMN_STOREQUESTION_answer + " text, "
            + COLUMN_STOREQUESTION_isAnswered + " int, "
            + COLUMN_STOREQUESTION_initial + " int, "
            + COLUMN_STOREQUESTION_exempt + " int, "
            + COLUMN_STOREQUESTION_final + " int)";

    //FORM TYPE TABLE
    public static final String TABLE_FORMTYPE = "tblformtype";
    public static final String COLUMN_FORMTYPE_id = "formtypeid";
    public static final String COLUMN_FORMTYPE_code = "formtypecode";
    public static final String COLUMN_FORMTYPE_desc = "formtypedesc";

    private static final String DATABASE_CREATE_TABLE_FORMTYPE = "CREATE TABLE " + TABLE_FORMTYPE + "("
            + COLUMN_FORMTYPE_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_FORMTYPE_code + " text, "
            + COLUMN_FORMTYPE_desc + " text)";

    //SECONDARY DISPLAY TABLE
    public static final String TABLE_SECONDARYDISP = "tblsecondarydisp";
    public static final String COLUMN_SECONDARYDISP_id = "id";
    public static final String COLUMN_SECONDARYDISP_storeid = "store_id";
    public static final String COLUMN_SECONDARYDISP_categoryid = "category_id";
    public static final String COLUMN_SECONDARYDISP_brand = "brand";

    private static final String DATABASE_CREATE_TABLE_SECONDARYDISP = "CREATE TABLE " + TABLE_SECONDARYDISP + "("
            + COLUMN_SECONDARYDISP_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_SECONDARYDISP_storeid + " numeric, "
            + COLUMN_SECONDARYDISP_categoryid + " numeric, "
            + COLUMN_SECONDARYDISP_brand + " text)";


    //SECONDARY KEY LIST TABLE
    public static final String TABLE_SECONDARYKEYLIST = "tblsecondarykeylist";
    public static final String COLUMN_SECONDARYKEYLIST_id = "id";
    public static final String COLUMN_SECONDARYKEYLIST_keygroupid = "key_group_id";

    private static final String DATABASE_CREATE_TABLE_SECONDARYKEYLIST = "CREATE TABLE " + TABLE_SECONDARYKEYLIST + "("
            + COLUMN_SECONDARYKEYLIST_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_SECONDARYKEYLIST_keygroupid + " numeric)";

    //OSA LIST TABLE
    public static final String TABLE_OSALIST = "tblosalist";
    public static final String COLUMN_OSALIST_id = "id";
    public static final String COLUMN_OSALIST_osakeygroupid = "osa_key_group_id";

    private static final String DATABASE_CREATE_TABLE_OSALIST = "CREATE TABLE " + TABLE_OSALIST + "("
            + COLUMN_OSALIST_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_OSALIST_osakeygroupid + " numeric)";


    //OSA LOOKUP TABLE
    public static final String TABLE_OSALOOKUP = "tblosalookup";
    public static final String COLUMN_OSALOOKUP_id = "id";
    public static final String COLUMN_OSALOOKUP_storeid = "osa_lookup_store_id";
    public static final String COLUMN_OSALOOKUP_categoryid = "osa_lookup_category_id";
    public static final String COLUMN_OSALOOKUP_target = "target";
    public static final String COLUMN_OSALOOKUP_total = "total";
    public static final String COLUMN_OSALOOKUP_lookupid = "lookup_id";

    private static final String DATABASE_CREATE_TABLE_OSALOOKUP = "CREATE TABLE " + TABLE_OSALOOKUP + "("
            + COLUMN_OSALOOKUP_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_OSALOOKUP_storeid + " numeric, "
            + COLUMN_OSALOOKUP_categoryid + " numeric, "
            + COLUMN_OSALOOKUP_target + " numeric, "
            + COLUMN_OSALOOKUP_total + " numeric, "
            + COLUMN_OSALOOKUP_lookupid + " numeric)";


    //SOS LIST TABLE
    public static final String TABLE_SOSLIST= "tblsoslist";
    public static final String COLUMN_SOSLIST_id = "id";
    public static final String COLUMN_SOSLIST_soskeygroupid = "sos_key_group_id";

    private static final String DATABASE_CREATE_TABLE_SOSLIST = "CREATE TABLE " + TABLE_SOSLIST + "("
            + COLUMN_SOSLIST_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_SOSLIST_soskeygroupid + " numeric)";

    //SOS LOOKUP TABLE
    public static final String TABLE_SOSLOOKUP = "tblsoslookup";
    public static final String COLUMN_SOSLOOKUP_id = "id";
    public static final String COLUMN_SOSLOOKUP_storeid = "sos_lookup_store_id";
    public static final String COLUMN_SOSLOOKUP_categoryid = "sos_lookup_category_id";
    public static final String COLUMN_SOSLOOKUP_sosid = "sos_id";
    public static final String COLUMN_SOSLOOKUP_less = "less";
    public static final String COLUMN_SOSLOOKUP_value = "value";
    public static final String COLUMN_SOSLOOKUP_lookupid = "sos_lookup_id";

    private static final String DATABASE_CREATE_TABLE_SOSLOOKUP = "CREATE TABLE " + TABLE_SOSLOOKUP + "("
            + COLUMN_SOSLOOKUP_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_SOSLOOKUP_storeid + " numeric, "
            + COLUMN_SOSLOOKUP_categoryid + " numeric, "
            + COLUMN_SOSLOOKUP_sosid + " numeric, "
            + COLUMN_SOSLOOKUP_less + " numeric, "
            + COLUMN_SOSLOOKUP_value + " numeric, "
            + COLUMN_SOSLOOKUP_lookupid + " numeric)";

    //PICTURES TABLE
    public static final String TABLE_PICTURES = "tblpictures";
    public static final String COLUMN_PICTURES_id = "id";
    public static final String COLUMN_PICTURES_name = "picname";

    private static final String DATABASE_CREATE_TABLE_PICTURES = "CREATE TABLE " + TABLE_PICTURES + "("
            + COLUMN_PICTURES_id + " integer PRIMARY KEY autoincrement, "
            + COLUMN_PICTURES_name + " text)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DATABASE_CREATE_TABLE_USER);
            db.execSQL(DATABASE_CREATE_TABLE_STORE);
            db.execSQL(DATABASE_CREATE_TABLE_CATEGORY);
            db.execSQL(DATABASE_CREATE_TABLE_GROUP);
            db.execSQL(DATABASE_CREATE_TABLE_QUESTION);
            db.execSQL(DATABASE_CREATE_TABLE_FORMS);
            db.execSQL(DATABASE_CREATE_TABLE_FORMTYPE);
            db.execSQL(DATABASE_CREATE_TABLE_SINGLESELECT);
            db.execSQL(DATABASE_CREATE_TABLE_MULTISELECT);
            db.execSQL(DATABASE_CREATE_TABLE_COMPUTATIONAL);
            db.execSQL(DATABASE_CREATE_TABLE_CONDITIONAL);
            db.execSQL(DATABASE_CREATE_TABLE_CONDITIONAL_ANSWERS);
            db.execSQL(DATABASE_CREATE_TABLE_STORECATEGORY);
            db.execSQL(DATABASE_CREATE_TABLE_STORECATEGORYGROUP);
            db.execSQL(DATABASE_CREATE_TABLE_STOREQUESTION);
            db.execSQL(DATABASE_CREATE_TABLE_SECONDARYDISP);
            db.execSQL(DATABASE_CREATE_TABLE_SECONDARYKEYLIST);
            db.execSQL(DATABASE_CREATE_TABLE_OSALIST);
            db.execSQL(DATABASE_CREATE_TABLE_OSALOOKUP);
            db.execSQL(DATABASE_CREATE_TABLE_SOSLIST);
            db.execSQL(DATABASE_CREATE_TABLE_SOSLOOKUP);
            db.execSQL(DATABASE_CREATE_TABLE_PICTURES);
            db.execSQL("CREATE INDEX userIndex ON " + TABLE_USER + " (" + COLUMN_USER_id + ")");
        }
        catch (Exception ex) {
            messageBox.ShowExceptionError(ex, "Error in Database");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(newVersion > oldVersion) {
            try {
                // ADDING COLUMNS

/*                db.execSQL("ALTER TABLE " + TABLE_QUESTION + " ADD COLUMN " + COLUMN_QUESTION_defaultans + " TEXT DEFAULT 0");
                db.execSQL("ALTER TABLE " + TABLE_FORMS + " ADD COLUMN " + COLUMN_FORMS_defaultans + " TEXT DEFAULT 0");
                db.execSQL("ALTER TABLE " + TABLE_CONDITIONAL + " ADD COLUMN " + COLUMN_CONDITIONAL_optionid + " NUMERIC DEFAULT 0");*/

                // 2
                db.execSQL("ALTER TABLE " + TABLE_STORE + " ADD COLUMN " + COLUMN_STORE_auditid + " NUMERIC");

                Log.w(TAG, "Upgrading settings database from version " + oldVersion + " to "
                        + newVersion);
            }
            catch (SQLException err) {
                Log.wtf("SQLException", err.getMessage());
            }
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(newVersion < oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORMTYPE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SINGLESELECT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MULTISELECT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPUTATIONAL);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONDITIONAL);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONDITIONAL_ANSWERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORECATEGORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORECATEGORYGROUP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOREQUESTION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SECONDARYDISP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SECONDARYKEYLIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_OSALIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_OSALOOKUP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOSLIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOSLOOKUP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURES);
            onCreate(db);

            Log.w(TAG, "Downgrading settings database from version " + oldVersion + " to "
                    + newVersion);
        }
    }
}
