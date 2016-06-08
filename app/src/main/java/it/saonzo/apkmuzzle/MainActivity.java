package it.saonzo.apkmuzzle;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {
    public static final String APP_FOLDER_NAME = "ApkMuzzle";
    public static final String FOLDER_PATH =
            Environment.getExternalStorageDirectory().toString() + '/' + APP_FOLDER_NAME;
    public static final String CUSTOM_METHODS_RES = "custom.dex";
    public static final File FOLDER_FILE = new File(FOLDER_PATH);
    public static final File CUSTOM_FILE = new File(FOLDER_PATH, CUSTOM_METHODS_RES);
    private static final int PERM_REQUEST = 1;
    public static boolean adsFlag;
    public static boolean permsFlag;
    private Switch adsSwitch;
    private Switch permsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adsSwitch = (Switch) findViewById(R.id.SwitchAds);
        permsSwitch = (Switch) findViewById(R.id.SwitchPerms);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (read != PackageManager.PERMISSION_GRANTED || write != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERM_REQUEST
                );
        } else {
            prepareFileAndFolders();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERM_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prepareFileAndFolders();
                } else {
                    throw new AssertionError("This app need the READ_EXTERNAL_STORAGE permission");
                }
            }

        }
    }

    private void prepareFileAndFolders() {
        FOLDER_FILE.mkdirs();
        if (!FOLDER_FILE.exists())
            throw new AssertionError("Error creating " + FOLDER_FILE.toString());
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
            } catch (IOException ioe) {
                throw new AssertionError("Error creating " + CUSTOM_FILE.toString());
            }
        }
    }

    private boolean atLeastOneSwitchEnabled() {
        adsFlag = adsSwitch.isChecked();
        permsFlag = permsSwitch.isChecked();
        if (adsFlag || permsFlag)
            return true;
        Utilities.ShowAlertDialog(
                this,
                getResources().getString(R.string.no_choice),
                getResources().getString(R.string.no_choice_msg)
        );
        return false;
    }


    public void readApk(View view) {
        if (atLeastOneSwitchEnabled()) {
            Intent intent = new Intent(this, FileManagerActivity.class);
            startActivity(intent);
        }
    }

    public void getInstalledApp(View view) {
        if (atLeastOneSwitchEnabled()) {
            Intent intent = new Intent(this, InstalledAppsActivity.class);
            startActivity(intent);
        }
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
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=X5CSSCY72LZBC&lc=IT&item_name=Six110&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedhttps://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=X5CSSCY72LZBC&lc=IT&item_name=Six110&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted")
            );
            startActivity(browserIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
