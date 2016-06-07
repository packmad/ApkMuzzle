package it.saonzo.apkmuzzle;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InstalledAppsActivity extends Activity {
    InstalledAppsActivity iaa = this;
    ListView apps;
    PackageManager packageManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installedapps);


        packageManager = getPackageManager();
        final List <PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_META_DATA); // all apps in the phone
        final List <PackageInfo> packageList1 = packageManager.getInstalledPackages(0);

        apps = (ListView) findViewById(R.id.listView1);
        apps.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                PackageInfo pi = packageList1.get(arg2);
                String publicSourceDir = pi.applicationInfo.publicSourceDir;
                String packageName = pi.packageName;
                //Log.d("############", ">>> " + publicSourceDir+" "+packageName);
                File src = new File(publicSourceDir);
                File dst = new File(MainActivity.FOLDER_FILE.getPath() + "/" + packageName + ".apk");
                try {
                    dst.createNewFile();
                } catch (IOException e) {
                    //TODO
                }
                Utilities.fileCopy(src, dst);
                Intent intent = new Intent(iaa, PermissionsMangeActivity.class);
                intent.putExtra(PermissionsMangeActivity.EXTRA_PERMISSIONSMANAGE, dst.getAbsolutePath());
                startActivity(intent);
                }
        });
        try {
            packageList1.clear();
            for (int n = 0; n < packageList.size(); n++)
            {

                PackageInfo PackInfo = packageList.get(n);
                if (((PackInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) != true)
                //check weather it is system app or user installed app
                {
                    try
                    {

                        packageList1.add(packageList.get(n)); // add in 2nd list if it is user installed app
                        Collections.sort(packageList1,new Comparator <PackageInfo >()
                                // this will sort App list on the basis of app name
                        {
                            public int compare(PackageInfo o1,PackageInfo o2)
                            {
                                return o1.applicationInfo.loadLabel(getPackageManager()).toString()
                                        .compareToIgnoreCase(o2.applicationInfo.loadLabel(getPackageManager())
                                                .toString());// compare and return sorted packagelist.
                            }
                        });


                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ListAdapter Adapter = new InstalledAppsAdapter(this, packageList1, packageManager);

        apps.setAdapter(Adapter);

    }
}