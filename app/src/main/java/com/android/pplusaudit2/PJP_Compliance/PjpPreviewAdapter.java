package com.android.pplusaudit2.PJP_Compliance;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.pplusaudit2.Database.SQLLibrary;
import com.android.pplusaudit2.General;
import com.android.pplusaudit2.R;
import com.android.pplusaudit2._Store.Stores;

import java.util.Collections;
import java.util.List;

/**
 * Created by ULTRABOOK on 6/7/2016.
 */
public class PjpPreviewAdapter extends BaseAdapter {

    Context mContext;
    List<Compliance> storesArrayList = Collections.EMPTY_LIST;
    SQLLibrary sqlLibrary;

    public PjpPreviewAdapter(Context mContext, List<Compliance> storesArrayList) {
        this.mContext = mContext;
        this.storesArrayList = storesArrayList;
        this.sqlLibrary = new SQLLibrary(mContext);
    }

    public class ViewHolder {
        TextView tvwDate;
        TextView tvwTime;
        TextView tvwUser;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewHolder holder;

        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pjppreview_activity_layout_row, parent, false);

            holder.tvwDate = (TextView) convertView.findViewById(R.id.tvwPjpDate);
            holder.tvwTime = (TextView) convertView.findViewById(R.id.tvwPjpTime);
            holder.tvwUser = (TextView) convertView.findViewById(R.id.tvwPjpUsername);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Compliance c = storesArrayList.get(position);

        holder.tvwDate.setText(storesArrayList.get(position).date);
        holder.tvwTime.setText(storesArrayList.get(position).time);
        holder.tvwUser.setText(storesArrayList.get(position).username);

        return convertView;
    }



    @Override
    public int getCount() {
        return storesArrayList.size();
    }

    @Override
    public Compliance getItem(int position) {
        return storesArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return storesArrayList.get(position).complianceID;
    }
}
