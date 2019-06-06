package com.ragingclaw.mtgcubedraftsimulator.widget;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of App Widget functionality.
 */
public class CubeDraftWidgetProvider extends AppWidgetProvider {
    private FirebaseAuth mAuth;
    private SharedPreferences.Editor mEditor;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider_layout);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            if(intent.hasExtra(AllMyConstants.WIDGET_INTENT_ACTION_MY_CUBES)) {
                mAuth = FirebaseAuth.getInstance();

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, CubeDraftWidgetProvider.class));

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider_layout);
            }

        }

        super.onReceive(context, intent);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) { RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider_layout);
            mAuth = FirebaseAuth.getInstance();
            String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> names;
            ArrayList<String> cubeNames = new ArrayList<>();

            if(mPreferences.contains(AllMyConstants.CUBE_NAMES)) {
                names = mPreferences.getStringSet(AllMyConstants.CUBE_NAMES, null);

                if((names != null ? names.size() : 0) > 0) {
                    cubeNames.addAll(names);
                } else {
                    cubeNames = null;
                }
            }

            // Service intent
            Intent serviceIntent = new Intent(context, CubeDraftWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.putStringArrayListExtra(AllMyConstants.CUBE_NAMES, cubeNames);
            serviceIntent.putExtra(AllMyConstants.USER_ID, userId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.cubes_widget_list, serviceIntent);
            views.setEmptyView(R.id.cubes_widget_list, R.id.empty_view);

            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.cubes_widget_list);

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

