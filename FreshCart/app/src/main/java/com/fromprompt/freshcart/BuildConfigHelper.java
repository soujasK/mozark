package com.fromprompt.freshcart;

import android.content.Context;
import android.content.pm.PackageManager;

final class BuildConfigHelper {
    private BuildConfigHelper() {
    }

    static String versionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "?";
        }
    }
}
