package com.android.pplusaudit2.Report.CustomerSummary;

import java.util.ArrayList;

/**
 * Created by Lloyd on 8/22/16.
 */

public class Customer {

    public final int customerID;
    public int userID;
    public int auditID;
    public String auditName;
    public String account;
    public String customerCode;
    public String customerName;
    public String area;
    public String regionCode;
    public String regionName;
    public String distributorCode;
    public String distributor;
    public String storeCode;
    public String storeName;
    public String channelCode;
    public String template;
    public String createdAt;
    public String updateAt;
    public int perfectStores;
    public double osaAve;
    public double npiAve;
    public double planogramAve;

    public ArrayList<CustomerSummaryItem> customerSummaryItems;

    public Customer(int customerID) {
        this.customerID = customerID;
        this.customerSummaryItems = new ArrayList<>();
    }
}
