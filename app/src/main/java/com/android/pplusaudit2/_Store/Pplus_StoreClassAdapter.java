package com.android.pplusaudit2._Store;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.pplusaudit2.Database.SQLLibrary;
import com.android.pplusaudit2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ULTRABOOK on 9/22/2015.
 */
public class Pplus_StoreClassAdapter extends BaseAdapter {

    private Context mContext;
    private SQLLibrary sqlLibrary;
    private List<Pplus_Storeclass> storeClassResult = null;
    private ArrayList<Pplus_Storeclass> arrStoreList;

    public Pplus_StoreClassAdapter(Context ctx, ArrayList<Pplus_Storeclass> arrList)
    {
        this.mContext = ctx;
        this.sqlLibrary = new SQLLibrary(ctx);
        storeClassResult = arrList;
        arrStoreList = new ArrayList<Pplus_Storeclass>();
        arrStoreList.addAll(arrList);
    }

    public class ViewHolder {
        TextView tvwStore;
        TextView tvwTemplatename;
        TextView tvwPostingDateTime;
        //TextView tvwScoreStore;
        Button btnPreview;
        Button btnAudit;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewHolder holder;

        if(view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.pplus_layout_store_row, parent, false);

            holder.tvwStore = (TextView) view.findViewById(R.id.tvwStore);
            holder.tvwTemplatename = (TextView) view.findViewById(R.id.tvwTemplatename);
            holder.tvwPostingDateTime = (TextView) view.findViewById(R.id.tvwPostingDatetime);
            //holder.tvwScoreStore = (TextView) view.findViewById(R.id.tvwScoreStore);
            holder.btnPreview = (Button) view.findViewById(R.id.btnPreview);
            holder.btnAudit = (Button) view.findViewById(R.id.btnAudit);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        int storeid = storeClassResult.get(position).StoreID;

        holder.tvwStore.setText(String.valueOf(storeClassResult.get(position).StoreName));
        holder.tvwTemplatename.setText(String.valueOf(storeClassResult.get(position).Tempname));
        holder.tvwPostingDateTime.setText("");

        if(storeClassResult.get(position).isPosted) {
            String postdate = sqlLibrary.GetPostingDateTime(storeid);
            holder.tvwPostingDateTime.setText(postdate.trim());
        }

        holder.tvwStore.setTag(storeClassResult.get(position).Audittemplateid + "," + storeid + "," + storeClassResult.get(position).finalValue);

        if(storeClassResult.get(position).isAudited) {
            holder.btnPreview.setEnabled(true);
        }
        else {
            holder.btnPreview.setEnabled(false);
        }

/*        holder.tvwScoreStore.setText(String.valueOf(arrStoreList.get(position).totalAnswerStore) + " / " + String.valueOf(arrStoreList.get(position).totalQuestionStore));

        if(storeClassResult.get(position).totalAnswerStore > 0)
            holder.tvwScoreStore.setTextColor(view.getResources().getColor(R.color.green));*/

        return view;

    }

    @Override
    public int getCount() {
        return storeClassResult.size();
    }

    @Override
    public Object getItem(int position) {
        return storeClassResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
