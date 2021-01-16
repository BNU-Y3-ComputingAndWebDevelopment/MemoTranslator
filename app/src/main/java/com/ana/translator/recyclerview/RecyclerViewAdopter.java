package com.ana.translator.recyclerview;

import android.content.Context;
import android.util.Log;


import java.util.List;

public class RecyclerViewAdopter extends SingleLayoutAdopter {

    private static final String TAG = "RecyclerViewAdopter";

    private List<Object> mData;
    private Context mContext;

    public RecyclerViewAdopter(Context mContext, int layoutId, List<Object> mData) {
        super(layoutId);
        this.mData = mData;
        this.mContext=mContext;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return mData.get(position);
    }

    @Override
    protected void itemOnClick(Object view) {
    }

    @Override
    public int getItemCount() {

        return mData.size();
    }

    public void setData(Object data) {
        if ((mData != null) && (!mData.contains(data))) {
            mData.add(data);
            //mDataFiltered.add(data);
            Log.e(TAG, "setData: call notifyDataSetChanged");
            notifyDataSetChanged();
        }

    }

    public void setListData(List<Object> data) {

        if ((mData != null)) {
            mData.clear();
            mData.addAll(data);
            Log.e(TAG, "setListData: with size " + data.size());
            notifyDataSetChanged();
        }

    }
}
