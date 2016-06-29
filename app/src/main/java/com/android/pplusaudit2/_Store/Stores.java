package com.android.pplusaudit2._Store;

import com.android.pplusaudit2.PJP_Compliance.Compliance;

import java.util.ArrayList;

/**
 * Created by ULTRABOOK on 9/22/2015.
 */
public class Stores {

    public final int storeID;
    public final String storeName;
    public final String storeCode;
    public final String webStoreID;
    public final int auditTemplateId;
    public final String Tempname;
    public final int finalValue;
    public final boolean isAudited;
    public final boolean isPosted;
    public boolean isChecked = false;
    public String dateCheckedIn = "";
    public String timeChecked = "";
    public String addressChecked = "";
    public final int gradeMatrixId;

    public int auditID = 0;
    public String account = "";
    public String customerCode = "";
    public String customer = "";
    public String area = "";
    public String regionCode = "";
    public String region = "";
    public String distributorCode = "";
    public String distributor = "";
    public ArrayList<Compliance> complianceArrayList = new ArrayList<>();

    public Stores(int id, String code, String webstoreid, String storename, int templateid, String templatename, int finalval, boolean bAns, boolean posted, int gmatrixID) {

        this.storeID = id;
        this.storeName = storename;
        this.auditTemplateId = templateid;
        this.Tempname = templatename;
        this.storeCode = code;
        this.webStoreID = webstoreid;
        this.finalValue = finalval;
        this.isAudited = bAns;
        this.isPosted = posted;
        this.gradeMatrixId = gmatrixID;
    }
}
