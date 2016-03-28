package com.android.pplusaudit2;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by ULTRABOOK on 9/30/2015.
 */
public class General {

    public static String usercode = "";
    public static String username = "";
    public static String storeid = "";
    public static int Temp_Storeid;
    public static String auditTemplateID = "";
    public static int gradematrixID;

    public static String typefacename = "fonts/fontawesome-webfont.ttf";

    public static ArrayList<String> arrKeylist;
    public static ArrayList<String> arrBrandSelected;

    public static String STATUS_PENDING = "PENDING";
    public static String STATUS_PARTIAL = "PARTIAL";
    public static String STATUS_COMPLETE = "COMPLETE";

    public static String SCORE_STATUS_PASSED = "PASSED";
    public static String SCORE_STATUS_FAILED = "FAILED";

    public static String ICON_STAR_PENDING = "\uf006";
    public static String ICON_STAR_PARTIAL = "\uf123";
    public static String ICON_STAR_COMPLETE = "\uf005";
    public static String ICON_PASSED = "\uf00c";
    public static String ICON_FAILED = "\uf00d";

    public static final String[] aReports = new String[] {
            "USER AUDIT SUMMARY REPORT",
            "STORE REPORT"
    };

    public enum SCORE_STATUS {
        PASSED,
        FAILED,
        NONE
    }

    public enum STATUS {
        NONE,
        PENDING,
        PARTIAL,
        COMPLETE
    }

    public static String extFolder = Environment.getExternalStorageDirectory().getAbsolutePath().trim()+"/PPLUS";

    public static String mainURL = "http://tcr.chasetech.com";
    public static String POSTING_URL = "http://tcr.chasetech.com/api/storeaudit";
    public static String POSTING_IMAGE = "http://tcr.chasetech.com/api/uploadimage";

    public static String QUESTION_IMAGE_CAPTURE = "Pplus2 Image";

    public static HashMap<Integer, ImageView> hmSignature;
    public static HashMap<Integer, ImageView> hmCondSignature;

    public static final String STORE_LIST = "stores";
    public static final String CATEGORY_LIST = "temp_categories";
    public static final String GROUP_LIST = "temp_groups";
    public static final String QUESTION_LIST = "temp_questions";
    public static final String FORM_LIST = "temp_forms";
    public static final String FORM_TYPES = "form_types";
    public static final String QUESTION_SINGLESELECT = "single_selects";
    public static final String QUESTION_MULTISELECT = "multi_selects";
    public static final String QUESTION_COMPUTATIONAL = "formulas";
    public static final String QUESTION_CONDITIONAL = "conditions";

    public static final String SECONDARY_LOOKUP_LIST = "secondary_lookups";
    public static final String SECONDARY_LIST = "secondary_lists";

    public static final String OSA_LIST = "osa_lists";
    public static final String OSA_LOOKUP = "osa_lookups";

    public static final String SOS_LIST = "sos_lists";
    public static final String SOS_LOOKUP = "sos_lookups";

    public static final String IMG_LIST = "image_lists";
    public static final String IMG_PRODUCT = "image_product";

    public static final String[] ARRAY_FILE_LISTS = {
            STORE_LIST,
            CATEGORY_LIST,
            GROUP_LIST,
            QUESTION_LIST,
            FORM_LIST,
            FORM_TYPES,
            QUESTION_SINGLESELECT,
            QUESTION_MULTISELECT,
            QUESTION_COMPUTATIONAL,
            QUESTION_CONDITIONAL,
            SECONDARY_LOOKUP_LIST,
            SECONDARY_LIST,
            OSA_LIST,
            OSA_LOOKUP,
            SOS_LIST,
            SOS_LOOKUP,
            IMG_LIST
    };

    public static final String[] DOWNLOAD_FILES = {
        "conditions.txt",
        "form_types.txt",
        "forms.txt",
        "formula.txt",
        "image_lists.txt",
        "multi_selects.txt",
        "osa_keylist.txt",
        "osa_lookups.txt",
        "questions.txt",
        "secondary_keylist.txt",
        "secondarydisplay.txt",
        "single_selects.txt",
        "sos_keylist.txt",
        "sos_lookups.txt",
        "stores.txt",
        "temp_category.txt",
        "temp_group.txt",
    };


    public static int[] mainIcons = new int[] {
            R.drawable.ic_menu_pjpcalendar,
            R.drawable.ic_menu_audit,
            R.drawable.ic_menu_auditsummary,
            R.drawable.ic_menu_settings,
            R.drawable.ic_menu_logout
    };

/*    public static String[] mainIconsFont = new String[] {
            "\uf073",
            "\uf059",
            "\uf016",
            "\uf1de",
            "\uf011"
    };*/

    public static String[] mainIconsFont = new String[] {
            "\uf059",
            "\uf1ea",
            "\uf011"
    };

/*    public static String[] Menu = {
            "PJP Calendar",
            "Audit",
            "Audit Summary",
            "Settings",
            "Log out"
    };*/

    public static String[] Menu = {
            "Audit:Audit and answer a survey from selected store.",
            "Reports:Generate a report for references.",
            "Log out:Log out user."
    };


    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM/dd/yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getTimeToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getYear() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static int GetVersionCode(Context mContext) {
        PackageManager pm = mContext.getPackageManager();
        String packageName = mContext.getPackageName();
        int flags = PackageManager.GET_PERMISSIONS;
        int vcode = 0;

        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, flags);
            vcode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("NamenotfoundException", e.getMessage());
        }

        return vcode;
    }

    public static String GetVersionName(Context mContext) {
        PackageManager pm = mContext.getPackageManager();
        String packageName = mContext.getPackageName();
        int flags = PackageManager.GET_PERMISSIONS;
        String versionName = "";

        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, flags);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("NamenotfoundException", e.getMessage());
        }

        return versionName;
    }
}
