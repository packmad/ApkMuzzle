package it.saonzo.apkmuzzle;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import java.io.File;
import java.util.ArrayList;

import it.saonzo.rmperm.IOutput;
import it.saonzo.rmperm.Main;


public class SaveApkService  extends IntentService {
    public static final String MSG_SAVEAPKSERVICE = "MSG_SAVEAPKSERVICE";
    private final int notifyID = 0;
    private String apkPath;
    private String apkName;
    private String perms;


    public SaveApkService() {
        super("SaveApkService");
    }

    public SaveApkService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle b = intent.getExtras();
        String[] apkAndPerms = b.getStringArray(PermissionsMangeActivity.MSG_PERMISSIONSMANAGE);
        apkPath = apkAndPerms[0];
        apkName = getApkFileName(apkPath);
        perms = apkAndPerms[1];

        NotificationCompat.Builder startNotification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_notification_overlay)
                        .setContentTitle("ApkMuzzle")
                        .setContentText(getResources().getString(R.string.removing_permissions) + ' ' + apkName)
                        .setAutoCancel(true)
                        .setNumber(1);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notifyID, startNotification.build());

        File newApk = new File(MainActivity.FOLDER_FILE, apkName + "_muzzle.apk");
        JarOutput jo = new JarOutput(IOutput.Level.VERBOSE);
        Main.androidMain(jo, getArgs(newApk));
        startNotification.setContentText(getResources().getString(R.string.finished_removing) +' '+ apkName).setNumber(2);
        mNotificationManager.notify(notifyID, startNotification.build());

        Intent saveApkResultIntent = new Intent(this, SaveResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArray(MSG_SAVEAPKSERVICE, new String[]{newApk.toString(), jo.getErrors(), jo.getMessages()});
        saveApkResultIntent.putExtras(bundle);
        saveApkResultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(saveApkResultIntent);
    }

    private String getApkFileName(String path) {
        if (path.substring(path.length() - 4, path.length()).equals(".apk")) {
            String out = path.substring(0, path.length() - 4);
            out = path.substring(out.lastIndexOf("/")+1, out.length());
            return out;
        }
        return null;
    }


    private String[] getArgs(File newApk) {
        boolean rmPerms = MainActivity.permsFlag;
        boolean rmAds = MainActivity.adsFlag;

        ArrayList<String> out = new ArrayList<>();
        out.add("--input");
        out.add(apkPath);
        out.add("--output");
        out.add(newApk.toString());

        if (rmPerms) {
            out.add("--remove");
            out.add("--custom-methods");
            out.add(MainActivity.CUSTOM_FILE.toString());
            out.add("--permissions");
            out.add(perms);
        }
        if (rmPerms && rmAds) {
            out.add("--ads");
        } else if (rmAds) {
            out.add("--removeads");
        }
        String[] ret = new String[out.size()];
        ret = out.toArray(ret);
        return ret;
    }

}
