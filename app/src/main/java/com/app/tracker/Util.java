package com.app.tracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class Util {

    public static final int REQUEST_READ_PHONE_STATE = 100;

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }


    @SuppressLint({"MissingPermission"})
    public static String getImie(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            List<SubscriptionInfo> info = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
            if (info != null && info.size() > 0) {
                return info.get(0).getIccId();
            }
        }
        return "";
    }

    public static ProgressDialog createProgressDialog(Activity activity, String message, boolean isCancelable) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(isCancelable);
        return progressDialog;
    }

    public static boolean isPermissionGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public static void requestPermission(Activity activity, String[] permission) {
        ActivityCompat.requestPermissions(activity, permission, REQUEST_READ_PHONE_STATE);
    }

    public static String[] requiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        } else {
            return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        }
    }


    public static void showPermissionAlert(Context context, int message) {
        new AlertDialog.Builder(context).setMessage(context.getString(message))
                .setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
                    dialog.dismiss();
                    if (context instanceof Activity)
                        ((Activity) context).finish();
                }).show();
    }

}
