package com.ragingclaw.mtgcubedraftsimulator.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class CubeDraftWidgetAdapter implements RemoteViewsService.RemoteViewsFactory {
    private final Context mContext;
    private ArrayList<String> mCubeNames;

    public CubeDraftWidgetAdapter(Context mContext, Intent mIntent) {
        this.mContext = mContext;
        this.mCubeNames = mIntent.getStringArrayListExtra(AllMyConstants.CUBE_NAMES);
    }

    @Override
    public void onCreate() {
        // connect to data source
        initData();
    }

    private void initData() {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if(mPreferences.contains(AllMyConstants.CUBE_NAMES)) {
            Set<String> names = mPreferences.getStringSet(AllMyConstants.CUBE_NAMES, null);
            if(!Objects.requireNonNull(names).isEmpty()) {
                mCubeNames = new ArrayList<>();
                mCubeNames.addAll(names);
            }
        }
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {
        // close data source connection
    }

    @Override
    public int getCount() {
        return (mCubeNames != null) ? mCubeNames.size() : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        if(mCubeNames.size() > 0) {
            String name = mCubeNames.get(position);
            remoteViews.setTextViewText(R.id.cube_name, name);
        }

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
