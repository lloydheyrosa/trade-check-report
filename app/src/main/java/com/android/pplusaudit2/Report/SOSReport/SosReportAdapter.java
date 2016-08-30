package com.android.pplusaudit2.Report.SOSReport;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Lloyd on 8/26/16.
 */

public class SosReportAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SosItem> sosItemArrayList;

    public SosReportAdapter(Context mContext, ArrayList<SosItem> sosItemArrayList) {
        this.mContext = mContext;
        this.sosItemArrayList = sosItemArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public int getCount() {
        return sosItemArrayList.size();
    }

    @Override
    public SosItem getItem(int position) {
        return sosItemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return sosItemArrayList.get(position).sosID;
    }
}
