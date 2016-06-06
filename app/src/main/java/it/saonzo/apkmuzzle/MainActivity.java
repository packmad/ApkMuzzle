package it.saonzo.apkmuzzle;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {
    public static final String APPFOLDERNAME = "ApkMuzzle";
    public static final String FOLDER_PATH =
            Environment.getExternalStorageDirectory().toString() + '/' + APPFOLDERNAME;
    public static final String CUSTOM_METHODS_RES = "custom.dex";
    public static final File FOLDER_FILE = new File(FOLDER_PATH);
    public static final File CUSTOM_FILE = new File(FOLDER_PATH, CUSTOM_METHODS_RES);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(
                this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean b = FOLDER_FILE.mkdirs();

                    if (!CUSTOM_FILE.exists()) {
                        try {
                            AssetManager assetManager = getAssets();
                            InputStream in = assetManager.open(CUSTOM_METHODS_RES);
                            OutputStream out = new FileOutputStream(CUSTOM_FILE);
                            byte[] buffer = new byte[1024];
                            int read;
                            while ((read = in.read(buffer)) != -1) {
                                out.write(buffer, 0, read);
                            }
                            in.close();
                            out.flush();
                            out.close();
                        }
                        catch (IOException ioe) {
                            //TODO
                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }


    public void readApk(View view) {
        Intent intent = new Intent(this, FileManagerActivity.class);
        startActivity(intent);
    }

    public void getInstalledApp(View view) {
        Intent intent = new Intent(this, InstalledAppsActivity.class);
        startActivity(intent);
    }

    public void exit(View view) {
        finish();
        System.exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_donate) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=X5CSSCY72LZBC&lc=IT&item_name=Six110&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedhttps://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=X5CSSCY72LZBC&lc=IT&item_name=Six110&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted"));
            startActivity(browserIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
