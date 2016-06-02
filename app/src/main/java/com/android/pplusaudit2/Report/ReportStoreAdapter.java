package com.android.pplusaudit2.Report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.pplusaudit2.ErrorLogs.AutoErrorLog;
import com.android.pplusaudit2.General;
import com.android.pplusaudit2.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by ULTRABOOK on 5/10/2016.
 */
public class ReportStoreAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<StoreReport> arrStoreReports;

    public ReportStoreAdapter(Context mContext, ArrayList<StoreReport> arrStoreReports) {
        this.mContext = mContext;
        this.arrStoreReports = arrStoreReports;
        Thread.setDefaultUncaughtExceptionHandler(new AutoErrorLog(mContext, General.errlogFile));
    }

    public class ViewHolder {
        TextView tvwStoreName;
        TextView tvwAuditName;
        TextView tvwPerfectStore;
        TextView tvwOSA;
        TextView tvwNPI;
        TextView tvwPlanogram;
        TextView tvwPostingDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.report_store_activity_row, parent, false);
            holder = new ViewHolder();
            holder.tvwStoreName = (TextView) convertView.findViewById(R.id.tvwStorename);
            holder.tvwAuditName = (TextView) convertView.findViewById(R.id.tvwAuditName);
            holder.tvwPerfectStore = (TextView) convertView.findViewById(R.id.tvwPerfectStore);
            holder.tvwOSA = (TextView) convertView.findViewById(R.id.tvwOSA);
            holder.tvwNPI = (TextView) convertView.findViewById(R.id.tvwNPI);
            holder.tvwPlanogram = (TextView) convertView.findViewById(R.id.tvwPlanogram);
            holder.tvwPostingDate = (TextView) convertView.findViewById(R.id.tvwPostingdate);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvwStoreName.setText(arrStoreReports.get(position).storeName);
        holder.tvwAuditName.setText(arrStoreReports.get(position).auditName);
        holder.tvwPerfectStore.setText(String.format("%.2f", arrStoreReports.get(position).perfectStore) + " %");
        holder.tvwOSA.setText(String.valueOf(arrStoreReports.get(position).osa));
        holder.tvwNPI.setText(String.valueOf(arrStoreReports.get(position).npi));
        holder.tvwPlanogram.setText(String.valueOf(arrStoreReports.get(position).planogram));
        holder.tvwPostingDate.setText(arrStoreReports.get(position).updateAt);

        return convertView;
    }

    @Override
    public int getCount() {
        return arrStoreReports.size();
    }

    @Override
    public long getItemId(int position) {
        return arrStoreReports.get(position).ID;
    }

    @Override
    public StoreReport getItem(int position) {
        return arrStoreReports.get(position);
    }
}
