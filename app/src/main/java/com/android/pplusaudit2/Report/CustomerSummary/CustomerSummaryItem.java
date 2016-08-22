package com.android.pplusaudit2.Report.CustomerSummary;

/**
 * Created by Lloyd on 8/22/16.
 */
public class CustomerSummaryItem {

    public final int ID;
    public String storeName;
    public String perfectStore;
    public String osa;
    public String npi;
    public String planogram;
    public Customer customer;

    public CustomerSummaryItem(int ID) {
        this.ID = ID;
    }

}
