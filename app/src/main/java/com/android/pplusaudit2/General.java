package com.android.pplusaudit2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.android.pplusaudit2.AutoUpdateApk.AutoUpdate;
import com.android.pplusaudit2.ErrorLogs.ErrorLog;
import com.android.pplusaudit2.Report.AuditSummary.Audit;
import com.android.pplusaudit2._Store.Stores;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Created by ULTRABOOK on 9/30/2015.
 */
public class General {

    public static String TAG = "Debug";
    public static String errlogFile = "errorlogs.txt";

    public static boolean BETA = false;

    public static String versionName = "";
    public static int versionCode = 0;
    public static int oldversionCode = 0;
    public static String deviceID = "";

    public static boolean hasUpdate = false;
    public static AutoUpdate mainAutoUpdate;

    public static String dateLog = "";

    public static String usercode = "";
    public static String userFullName = "";
    public static String userName = "";
    public static String userPassword = "";
    public static String savedHashKey = "";
    public static int gradematrixID;

    public static String typefacename = "fonts/fontawesome-webfont.ttf";

    public static ArrayList<String> arrSecondaryKeylist;
    public static ArrayList<String> arrBrandSelected;

    public static String STATUS_PENDING = "PENDING";
    public static String STATUS_PARTIAL = "PARTIAL";
    static String STATUS_COMPLETE = "COMPLETE";

    public static String SCORE_STATUS_PASSED = "PASSED";
    public static String SCORE_STATUS_FAILED = "FAILED";

    public static String ICON_STAR_PENDING = "\uf006";
    public static String ICON_STAR_PARTIAL = "\uf123";
    public static String ICON_STAR_COMPLETE = "\uf005";
    public static String ICON_PASSED = "\uf00c";
    public static String ICON_FAILED = "\uf00d";

    public static ArrayList<Audit> arraylistAudits;
    public static ArrayList<String> arrPendingStores = new ArrayList<>();

    public static Stores selectedStore;

    public static boolean isAdminMode = false;

    public static String GetApiLevelDevice() {
        return android.os.Build.VERSION.RELEASE;
    }

    public enum SCORE_STATUS {
        PASSED,
        FAILED,
        NONE
    }

    public static String extFolder = Environment.getExternalStorageDirectory().getAbsolutePath().trim()+"/PPLUS";

    public static String mainURL = BETA ? "http://testtcr.chasetech.com" : "http://tcr2.chasetech.com";

    public static String POSTING_URL = mainURL + "/api/storeaudit";
    public static String URL_UPLOAD_CHECKIN = mainURL + "/api/uploadcheckin";
    public static String POSTING_DETAILS_URL = mainURL + "/api/uploaddetails";
    public static String POSTING_IMAGE = mainURL + "/api/uploadimage";

    public static String URL_REPORT_AUDITS = mainURL + "/api/postedaudits";
    public static String URL_REPORT_STORESUMMARY = mainURL + "/api/storesummaryreport";
    public static String URL_REPORT_USERSUMMARY = mainURL + "/api/usersummaryreport";

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
    public static final String NPI_LIST = "npi_lists";
    public static final String PLANOGRAM_LIST = "plano_lists";
    public static final String PERFECT_CATEGORY_LIST = "perfect_category_lists";
    public static final String PERFECT_GROUP_LIST = "perfect_group_lists";

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
            IMG_LIST,
            NPI_LIST,
            PLANOGRAM_LIST,
            PERFECT_CATEGORY_LIST,
            PERFECT_GROUP_LIST
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
        "plano_keylist.txt",
        "npi_keylist.txt",
        "perfect_category_lists.txt",
        "perfect_group_lists.txt",
    };

    public static String[] mainIconsFont = new String[] {
            "\uf059",
            "\uf274",
            "\uf1ea",
            "\uf011"
    };

    public static String[] mainIconsFont_admin = new String[] {
            "\uf059",
            "\uf011"
    };

    public static String[] Menu = {
            "Audit:Audit and answer a survey from selected store.",
            "PJP Compliance:Start in checking in stores for auditing.",
            "Reports:Generate a report for references.",
            "Log out:Log out user."
    };

    public static String[] Menu_admin = {
            "Audit:Audit and answer a survey from selected store.",
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

    public static String getDeviceOsVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static int getVersionCode(Context mContext) {
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

    public static String getVersionName(Context mContext) {
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

    public static int crc32(String str) {
        byte bytes[] = str.getBytes();
        Checksum checksum = new CRC32();
        checksum.update(bytes,0,bytes.length);
        return (int) checksum.getValue();
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER.toUpperCase();
        String model = Build.MODEL.toUpperCase();
        if (model.startsWith(manufacturer)) {
            return model;
        }
        return manufacturer + " " + model;
    }

    public static String getDeviceID(Context mContext) {
       return android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    public static void messageBox(Context mContext, String title, String msg) {
        new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(msg)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    static String cleanString(String strValue) {
        return strValue.trim().replace("\uFEFF", "").replace("\"", "");
    }
}
