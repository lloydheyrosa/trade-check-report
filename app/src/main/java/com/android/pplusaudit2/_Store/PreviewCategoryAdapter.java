package com.android.pplusaudit2._Store;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.pplusaudit2.Database.SQLLibrary;
import com.android.pplusaudit2.Database.SQLiteDB;
import com.android.pplusaudit2.General;
import com.android.pplusaudit2.R;
import com.android.pplusaudit2.TCRLib;
import com.android.pplusaudit2._Category.Pplus_Activity;
import com.android.pplusaudit2._Category.Pplus_ActivityClass;

import java.util.ArrayList;

/**
 * Created by ULTRABOOK on 2/18/2016.
 */
public class PreviewCategoryAdapter extends BaseAdapter {

    private Context mContext;
    private SQLLibrary sqlLibrary;
    private TCRLib tcrLib;
    private ArrayList<Pplus_ActivityClass> arrCategory;

    public PreviewCategoryAdapter(Context ctx, ArrayList<Pplus_ActivityClass> arrayList) {
        this.mContext = ctx;
        this.sqlLibrary = new SQLLibrary(ctx);
        this.tcrLib = new TCRLib(ctx);
        this.arrCategory = arrayList;
    }

    public class ViewHolder {
        TextView tvwPreviewCateg;
        TextView tvwPreviewCategStatus;
        TableLayout tblPreviewGroup;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewHolder holder;

        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pplus_layout_store_preview_row, parent, false);

            holder.tvwPreviewCateg = (TextView) convertView.findViewById(R.id.tvwPreviewCateg);
            holder.tvwPreviewCategStatus = (TextView) convertView.findViewById(R.id.tvwPreviewCategStatus);
            holder.tblPreviewGroup = (TableLayout) convertView.findViewById(R.id.tblPreviewGroup);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvwPreviewCateg.setText(arrCategory.get(position).activityName);

        String status = "";
        switch (arrCategory.get(position).categoryScoreStatus) {
            case PASSED:
                status = General.SCORE_STATUS_PASSED;
                holder.tvwPreviewCategStatus.setTextColor(mContext.getResources().getColor(R.color.green));
                break;
            case FAILED:
                holder.tvwPreviewCategStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                status = General.SCORE_STATUS_FAILED;
                break;
            default:
                break;
        }

        holder.tvwPreviewCategStatus.setText(status);

        // STORE CATEGORY GROUP
        Cursor cursStoreCategoryGroups = sqlLibrary.RawQuerySelect("SELECT tblstorecateggroup.id, tblgroup.groupdesc, " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_final
                + "," + SQLiteDB.COLUMN_STORECATEGORYGROUP_status  + "," + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_exempt
                + "," + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_initial
                + " FROM " + SQLiteDB.TABLE_STORECATEGORYGROUP
                + " JOIN " + SQLiteDB.TABLE_GROUP + " ON " + SQLiteDB.TABLE_GROUP + "." + SQLiteDB.COLUMN_GROUP_id + " = " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_groupid
                + " WHERE " + SQLiteDB.TABLE_STORECATEGORYGROUP + "." + SQLiteDB.COLUMN_STORECATEGORYGROUP_storecategid + " = " + arrCategory.get(position).categoryAndTempid
                + " ORDER BY " + SQLiteDB.COLUMN_GROUP_grouporder);
        cursStoreCategoryGroups.moveToFirst();

        holder.tblPreviewGroup.removeAllViews();

        if(cursStoreCategoryGroups.getCount() > 0) {
            while (!cursStoreCategoryGroups.isAfterLast()) {
                TableRow row = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.pplus_layout_store_preview_grouprow, null);

                TextView tvwGrouppreview = (TextView) row.findViewById(R.id.tvwGrouppreview);
                TextView tvwImgStatus = (TextView) row.findViewById(R.id.tvwImgStatus);

                String groupDesc = cursStoreCategoryGroups.getString(cursStoreCategoryGroups.getColumnIndex(SQLiteDB.COLUMN_GROUP_groupdesc));
                String grpScoreno = cursStoreCategoryGroups.getString(cursStoreCategoryGroups.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORYGROUP_final));
                int storeCategroupID = cursStoreCategoryGroups.getInt(cursStoreCategoryGroups.getColumnIndex(SQLiteDB.COLUMN_STORECATEGORYGROUP_id));

                if(!sqlLibrary.HasQuestionsByGroup(storeCategroupID)) {
                    cursStoreCategoryGroups.moveToNext();
                    continue;
                }

                General.SCORE_STATUS grpScoreStatus = tcrLib.GetScoreStatus(grpScoreno);

                tvwGrouppreview.setText(groupDesc.toUpperCase());

                String groupStatus = "";
                switch (grpScoreStatus) {
                    case PASSED:
                        groupStatus = General.SCORE_STATUS_PASSED;
                        tvwImgStatus.setTextColor(mContext.getResources().getColor(R.color.green));
                        break;
                    case FAILED:
                        tvwImgStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                        groupStatus = General.SCORE_STATUS_FAILED;
                        break;
                    default:
                        break;
                }

                tvwImgStatus.setText(groupStatus);

                cursStoreCategoryGroups.moveToNext();
                holder.tblPreviewGroup.addView(row);
            }
        }

        cursStoreCategoryGroups.close();


        return convertView;
    }

    @Override
    public int getCount() {
        return arrCategory.size();
    }

    @Override
    public Pplus_ActivityClass getItem(int position) {
        return arrCategory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrCategory.get(position).activityID;
    }
}
