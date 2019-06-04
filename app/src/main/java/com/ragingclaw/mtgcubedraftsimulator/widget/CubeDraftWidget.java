package com.ragingclaw.mtgcubedraftsimulator.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.activities.MainActivity;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class CubeDraftWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cube_draft_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cube_draft_widget);

        if(intent.getAction() != null) {
            if (AllMyConstants.WIDGET_INTENT_ACTION_NEW_CUBE.equals(intent.getAction())) {
                Timber.tag("fart").i("new cube clicked");
                Intent intent1 = new Intent(context, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setAction(AllMyConstants.WIDGET_INTENT_ACTION_NEW_CUBE);
                intent1.putExtra(AllMyConstants.WIDGET_INTENT_ACTION_NEW_CUBE, "action");
                intent.setAction(AllMyConstants.WIDGET_INTENT_ACTION_NEW_CUBE);
                PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 1, intent1, 0);
                views.setOnClickPendingIntent(R.id.new_cube_widget_button, pendingIntent1);
                context.startActivity(intent1);
            }

            if (AllMyConstants.WIDGET_INTENT_ACTION_MY_CUBES.equals(intent.getAction())) {
                Timber.tag("fart").i("my cubes clicked");
                Intent intent2 = new Intent(context, MainActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setAction(AllMyConstants.WIDGET_INTENT_ACTION_MY_CUBES);
                PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);
                views.setOnClickPendingIntent(R.id.my_cubes_widget_button, pendingIntent2);
                context.startActivity(intent2);
            }
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        // this widget wont actually update. its two buttons...

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cube_draft_widget);

            Intent newCubeIntent = new Intent(context, CubeDraftWidget.class);
            newCubeIntent.setAction(AllMyConstants.WIDGET_INTENT_ACTION_NEW_CUBE);
            PendingIntent pendingNewCubeIntent = PendingIntent.getBroadcast(context, 1, newCubeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.new_cube_widget_button, getNewCubeIntent(context, AllMyConstants.WIDGET_INTENT_ACTION_NEW_CUBE));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.new_cube_widget_button);


            Intent myCubesIntent = new Intent(context, CubeDraftWidget.class);
            myCubesIntent.setAction(AllMyConstants.WIDGET_INTENT_ACTION_MY_CUBES);
            PendingIntent pendingMyCubesIntent = PendingIntent.getBroadcast(context, 0, myCubesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.my_cubes_widget_button, getMyCubesIntent(context, AllMyConstants.WIDGET_INTENT_ACTION_MY_CUBES));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.my_cubes_widget_button);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }


        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    protected PendingIntent getMyCubesIntent(Context context, String action) {
        Intent intent = new Intent(context, CubeDraftWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    protected PendingIntent getNewCubeIntent(Context context, String action) {
        Intent intent = new Intent(context, CubeDraftWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 1, intent, 0);
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

