package com.android.pplusaudit2.PJP_Compliance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pplusaudit2.Database.SQLLibrary;
import com.android.pplusaudit2.Database.SQLiteDB;
import com.android.pplusaudit2.General;
import com.android.pplusaudit2.R;
import com.android.pplusaudit2._Store.Stores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ULTRABOOK on 6/7/2016.
 */
public class StoreCompAdapter extends BaseAdapter {

    Context mContext;
    List<Stores> storesArrayList = Collections.EMPTY_LIST;
    SQLLibrary sqlLibrary;

    public StoreCompAdapter(Context mContext, List<Stores> storesArrayList) {
        this.mContext = mContext;
        this.storesArrayList = storesArrayList;
        this.sqlLibrary = new SQLLibrary(mContext);
    }

/*    public class PjpViewHolder extends RecyclerView.ViewHolder {
        TextView tvwTitle;
        TextView tvwDetails;

        public PjpViewHolder(View itemView) {
            super(itemView);
            this.tvwTitle = (TextView) itemView.findViewById(R.id.tvwPjpStoreName);
            this.tvwDetails = (TextView) itemView.findViewById(R.id.tvwPjpDetails);
        }
    }

    @Override
    public PjpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.pjp_activity_layout_row, parent, false);
        PjpViewHolder viewHolder = new PjpViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PjpViewHolder holder, int position) {
        holder.tvwTitle.setText(storesArrayList.get(position).storeName);
        holder.tvwDetails.setText("Check in: ");
    }

    @Override
    public int getItemCount() {
        return storesArrayList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }*/

    public class PjpViewHolder {
        TextView tvwTitle;
        TextView tvwDetails;
        Button btnPreview;
        Button btnCheckin;
        RelativeLayout relMain;
        LinearLayout lnrButtons;
    }

        @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final PjpViewHolder holder;

            if(convertView == null) {
                holder = new PjpViewHolder();
                convertView = inflater.inflate(R.layout.pjp_activity_layout_row, parent, false);

                holder.tvwTitle = (TextView) convertView.findViewById(R.id.tvwPjpStoreName);
                holder.tvwDetails = (TextView) convertView.findViewById(R.id.tvwPjpDetails);
                holder.btnPreview = (Button) convertView.findViewById(R.id.btnPreviewPjp);
                holder.btnCheckin = (Button) convertView.findViewById(R.id.btnCheckInPjp);
                holder.relMain = (RelativeLayout) convertView.findViewById(R.id.relMainPjp);
                holder.lnrButtons = (LinearLayout) convertView.findViewById(R.id.lnrPjpButtons);

                convertView.setTag(holder);
            }
            else {
                holder = (PjpViewHolder) convertView.getTag();
            }
            holder.tvwTitle.setText(storesArrayList.get(position).storeName);

            String checkMsg = "Check in: ";

            if(storesArrayList.get(position).isChecked) {
                checkMsg += storesArrayList.get(position).dateChecked + " " + storesArrayList.get(position).timeChecked;
                holder.relMain.setBackgroundColor(mContext.getResources().getColor(R.color.color_highlight));
                holder.lnrButtons.setBackgroundColor(mContext.getResources().getColor(R.color.color_highlight));
            }
            else {
                holder.relMain.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                holder.lnrButtons.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }

            holder.tvwDetails.setText(checkMsg);

            holder.btnCheckin.setTag(storesArrayList.get(position));
            holder.btnPreview.setTag(storesArrayList.get(position));

            holder.btnPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    General.selectedStore = (Stores) v.getTag();
                    mContext.startActivity(new Intent(mContext, PjpPreviewActivity.class));
                }
            });

            return convertView;
    }



    @Override
    public int getCount() {
        return storesArrayList.size();
    }

    @Override
    public Stores getItem(int position) {
        return storesArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return storesArrayList.get(position).storeID;
    }
}
