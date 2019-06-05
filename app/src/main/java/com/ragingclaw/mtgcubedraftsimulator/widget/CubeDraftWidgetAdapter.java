package com.ragingclaw.mtgcubedraftsimulator.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

public class CubeDraftWidgetAdapter implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private int appWidgetId;
    private String userId;
    private ArrayList<String> mCubeNames = new ArrayList<>();

    public CubeDraftWidgetAdapter(Context mContext, Intent mIntent) {
        this.mContext = mContext;
        this.appWidgetId = mIntent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.mCubeNames = mIntent.getStringArrayListExtra(AllMyConstants.CUBE_NAMES);
        this.userId = mIntent.getStringExtra(AllMyConstants.USER_ID);
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
            if(names == null) {
                mCubeNames.add("");
            } else {
                if(mCubeNames.size() > 0) {
                    mCubeNames.clear();
                    mCubeNames.addAll(names);
                }
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
