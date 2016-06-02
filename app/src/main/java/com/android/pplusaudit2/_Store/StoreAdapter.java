package com.android.pplusaudit2._Store;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.pplusaudit2.Database.SQLLibrary;
import com.android.pplusaudit2.ErrorLogs.AutoErrorLog;
import com.android.pplusaudit2.General;
import com.android.pplusaudit2.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ULTRABOOK on 9/22/2015.
 */
public class StoreAdapter extends BaseAdapter {

    private Context mContext;
    private SQLLibrary sqlLibrary;
    private List<Stores> storeClassResult = null;
    private ArrayList<Stores> arrStoreList;

    public StoreAdapter(Context ctx, ArrayList<Stores> arrList)
    {
        this.mContext = ctx;
        this.sqlLibrary = new SQLLibrary(ctx);
        storeClassResult = arrList;
        arrStoreList = new ArrayList<Stores>();
        arrStoreList.addAll(arrList);
        Thread.setDefaultUncaughtExceptionHandler(new AutoErrorLog(ctx, General.errlogFile));
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
            view = inflater.inflate(R.layout.store_activity_layout_row, parent, false);

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

        holder.tvwStore.setText(String.valueOf(storeClassResult.get(position).storeName));
        holder.tvwTemplatename.setText(String.valueOf(storeClassResult.get(position).Tempname));
        holder.tvwPostingDateTime.setText("");

        if(storeClassResult.get(position).isPosted) {
            String postdate = sqlLibrary.GetPostingDateTime(storeid);
            holder.tvwPostingDateTime.setText(postdate.trim());
        }

        holder.tvwStore.setTag(storeClassResult.get(position).Audittemplateid + "," + storeid + "," + storeClassResult.get(position).finalValue + "," + storeClassResult.get(position).storeName);
        holder.btnPreview.setTag(storeClassResult.get(position).Audittemplateid + "," + storeid + "," + storeClassResult.get(position).finalValue + "," + storeClassResult.get(position).storeName);
        holder.btnAudit.setTag(storeClassResult.get(position).Audittemplateid + "," + storeid + "," + storeClassResult.get(position).finalValue + "," + storeClassResult.get(position).storeName);

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

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        storeClassResult.clear();

        if(charText.length() == 0) {
            storeClassResult.addAll(arrStoreList);
        }
        else {
            for (Stores store : arrStoreList)
            {
                if (store.storeName.toLowerCase(Locale.getDefault()).contains(charText) ||
                        store.storeCode.toLowerCase(Locale.getDefault()).contains(charText) ||
                        store.webStoreID.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    storeClassResult.add(store);
                }
            }
        }

        notifyDataSetChanged();
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
