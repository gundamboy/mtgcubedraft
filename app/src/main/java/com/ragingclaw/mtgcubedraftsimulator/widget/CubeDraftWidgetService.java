package com.ragingclaw.mtgcubedraftsimulator.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class CubeDraftWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CubeDraftWidgetAdapter(this.getApplicationContext(), intent);
    }
}
