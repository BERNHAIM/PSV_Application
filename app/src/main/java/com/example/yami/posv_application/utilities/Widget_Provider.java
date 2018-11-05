package com.example.yami.posv_application.utilities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.yami.posv_application.R;
import com.example.yami.posv_application.activities.MainActivity;

public class Widget_Provider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews views = buildViews(context);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private PendingIntent buildActivityIntent(Context context){

        Intent intent = new Intent("yami.posv_application.widget.ACTION_CALL_ACTIVITY")
                .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

        return pi;
    }

    private PendingIntent buildSettingIntent(Context context){
        Intent intent = new Intent("yami.posv_application.widget.SETTING")
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

        return pi;
    }

    private RemoteViews buildViews(Context context){
        //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.simple_widget_layout);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setOnClickPendingIntent(R.id.sms_send_btn, buildActivityIntent(context));
        views.setOnClickPendingIntent(R.id.setting_btn, buildSettingIntent(context));
        return views;
    }
}