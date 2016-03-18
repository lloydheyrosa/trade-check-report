package com.android.pplusaudit2._Category;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.pplusaudit2.General;
import com.android.pplusaudit2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ULTRABOOK on 9/22/2015.
 */
public class Pplus_ActivityAdapter extends BaseAdapter {

    private Context mContext;
    private List<Pplus_ActivityClass> activityResultList = null;
    private ArrayList<Pplus_ActivityClass> arrActivityList;
    private Typeface menuFontIcon;

    public Pplus_ActivityAdapter(Context ctx, ArrayList<Pplus_ActivityClass> arrList)
    {
        this.mContext = ctx;
        activityResultList = arrList;
        arrActivityList = new ArrayList<Pplus_ActivityClass>();
        arrActivityList.addAll(arrList);
        this.menuFontIcon = Typeface.createFromAsset(mContext.getAssets(), General.typefacename);
    }

    public class ViewHolder {
        TextView tvwActivity;
        TextView tvwCategoryStatus;
        TextView tvwCategoryScoreStatus;
        TextView tvwIconStatus;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewHolder holder;

        if(view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.pplus_layout_activity_row, parent, false);

            holder.tvwActivity = (TextView) view.findViewById(R.id.tvwActivity);
            holder.tvwCategoryStatus = (TextView) view.findViewById(R.id.tvwCategoryStatus);
            holder.tvwCategoryScoreStatus = (TextView) view.findViewById(R.id.tvwCategScoreStatus);
            holder.tvwIconStatus = (TextView) view.findViewById(R.id.tvwIconStatusCateg);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvwActivity.setText(String.valueOf(activityResultList.get(position).activityName));
        holder.tvwActivity.setTag(activityResultList.get(position).categoryAndTempid);

        String categoryStatus = activityResultList.get(position).categoryStatus.trim().toUpperCase();
        String categoryScoreStatus = "";

        holder.tvwCategoryStatus.setText(categoryStatus);
        holder.tvwIconStatus.setTypeface(menuFontIcon);

        if(categoryStatus.equals(General.STATUS_PENDING)) {
            holder.tvwCategoryStatus.setTextColor(view.getResources().getColor(R.color.red));
            holder.tvwIconStatus.setTextColor(view.getResources().getColor(R.color.gray));
            holder.tvwIconStatus.setText(General.ICON_STAR_PENDING);
        }
        else if (categoryStatus.equals(General.STATUS_PARTIAL)) {
            holder.tvwCategoryStatus.setTextColor(view.getResources().getColor(R.color.colorAccentDark));
            holder.tvwIconStatus.setTextColor(view.getResources().getColor(R.color.yellow));
            holder.tvwIconStatus.setText(General.ICON_STAR_PARTIAL);
        }
        else {
            holder.tvwCategoryStatus.setTextColor(view.getResources().getColor(R.color.green));
            holder.tvwIconStatus.setTextColor(view.getResources().getColor(R.color.green));
            holder.tvwIconStatus.setText(General.ICON_STAR_COMPLETE);
        }

        switch (activityResultList.get(position).categoryScoreStatus) {
            case PASSED:
                categoryScoreStatus = General.ICON_PASSED + " " + General.SCORE_STATUS_PASSED;
                holder.tvwCategoryScoreStatus.setTextColor(view.getResources().getColor(R.color.green));
                break;
            case FAILED:
                categoryScoreStatus = General.ICON_FAILED + " " + General.SCORE_STATUS_FAILED;
                holder.tvwCategoryScoreStatus.setTextColor(view.getResources().getColor(R.color.red));
                break;
            default:
                break;
        }


        holder.tvwCategoryScoreStatus.setTypeface(menuFontIcon);
        holder.tvwCategoryScoreStatus.setText(categoryScoreStatus);
        notifyDataSetChanged();
        return view;

    }

    @Override
    public int getCount() {
        return activityResultList.size();
    }

    @Override
    public Object getItem(int position) {
        return activityResultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
