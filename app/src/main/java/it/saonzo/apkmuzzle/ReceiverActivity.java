package it.saonzo.apkmuzzle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


public class ReceiverActivity extends Activity {
private static final String EVOZI = "http://apps.evozi.com/apk-downloader/?id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type.equals("text/plain")) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null && sharedText.contains("play.google.com/store")) {
                String packName = sharedText.substring(sharedText.indexOf("=")+1);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(EVOZI+packName));
                startActivity(browserIntent);
            }
        }
        else {
            Utilities.ShowAlertDialog(
                    this,
                    getResources().getString(R.string.errors),
                    getResources().getString(R.string.must_share)
            );
        }
    }
}
