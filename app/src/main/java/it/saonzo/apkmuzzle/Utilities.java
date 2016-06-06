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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class Utilities {
    private final static int BUFSIZE = 1024;

    public static void fileCopy(File src, File dst) {
        try{
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[BUFSIZE];
            int len;
            while ((len = in.read(buf)) > 0){
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
        catch(IOException e){
            //TODO
        }

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
