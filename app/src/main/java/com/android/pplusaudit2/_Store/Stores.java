package com.android.pplusaudit2._Store;

/**
 * Created by ULTRABOOK on 9/22/2015.
 */
public class Stores {

    public final int StoreID;
    public final String storeName;
    public final String storeCode;
    public final String webStoreID;
    public final int Audittemplateid;
    public final String Tempname;
    public final int finalValue;
/*    public final int totalQuestionStore;
    public final int totalAnswerStore;*/
    public final boolean isAudited;
    public final boolean isPosted;

    public Stores(int id, String code, String webstoreid, String storename, int templateid, String templatename, int finalval, boolean bAns, boolean posted) {

        this.StoreID = id;
        this.storeName = storename;
        this.Audittemplateid = templateid;
        this.Tempname = templatename;
        this.storeCode = code;
        this.webStoreID = webstoreid;
/*        this.totalAnswerStore = totans;
        this.totalQuestionStore = totsurvey;*/
        this.finalValue = finalval;
        this.isAudited = bAns;
        this.isPosted = posted;
    }
}
