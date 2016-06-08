package it.saonzo.apkmuzzle;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Utilities {

    public static void copyFile(File src, File dst) throws IOException {
        if (!dst.exists())
            dst.createNewFile();

        FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dst);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.flush();
        out.close();
    }

    public static void ShowAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(context.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static boolean isAppInstalled(Context context, String packName) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> pkgAppsList = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        for (ResolveInfo ri : pkgAppsList) {
           if (ri.activityInfo.packageName.equals(packName))
               return true;
        }
        return false;
    }

    public static String getFilenameWithoutExtension(File f) {
        String fName = f.getName();
        return fName.substring(0,fName.length()-4);
    }
}
