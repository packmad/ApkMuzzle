package it.saonzo.apkmuzzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SaveResultActivity extends Activity {
    private String newApkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saveapkresult);

        ArrayList<String> all = new ArrayList<String>();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String[] apkAndMsgs = bundle.getStringArray(SaveApkService.MSG_SAVEAPKSERVICE);
            if (apkAndMsgs.length > 0) {
                newApkPath = apkAndMsgs[0];
                ArrayList<String> errors = new ArrayList<String>(Arrays.asList(apkAndMsgs[1].split("\n")));
                ArrayList<String> messages = new ArrayList<String>(Arrays.asList(apkAndMsgs[2].split("\n")));
                all.addAll(errors);
                all.addAll(messages);
                if (all.size() == 0) {
                    all.add(getResources().getString(R.string.no_messages));
                } else {
                    all.removeAll(Arrays.asList("", null));
                }
            }
        }
        ListView list = (ListView) findViewById(R.id.resultlist);
        MyListAdapter mAdapter = new MyListAdapter(this, all);
        list.setAdapter(mAdapter);
    }

    public void installApk(View view) {
        File newApkFile = new File(newApkPath);
        final PackageInfo pi = getPackageManager().getPackageArchiveInfo(newApkFile.toString(), 0);
        if (pi != null) {
            boolean installed = false;
            try {
                getPackageManager().getPackageInfo(pi.packageName, PackageManager.GET_ACTIVITIES);
                installed = true;
            } catch (PackageManager.NameNotFoundException e) {
                installed = false;
            }
            if (installed) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder
                        .setTitle(getResources().getString(R.string.already_installed))
                        .setMessage(getResources().getString(R.string.need_uninstall))
                        .setPositiveButton(getResources().getString(R.string.yes),  new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", pi.packageName, null));
                                startActivity(intent);
                            }})
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return;
            }
        }

        if (newApkFile.exists() && !newApkFile.isDirectory()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(newApkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Utilities.ShowAlertDialog(
                    this,
                    getResources().getString(R.string.errors),
                    getResources().getString(R.string.were_errors)
            );
        }
    }

    private class MyListAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> mData;

        public MyListAdapter(final Context context, final List<String> mData) {
            this.mData = mData;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mData != null ? mData.size() : 0;
        }

        @Override
        public Object getItem(int i) {
            return mData != null ? mData.get(i) : null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txtView;
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.txt_item, parent, false);
                txtView = (TextView) convertView.findViewById(R.id.message);
                convertView.setTag(txtView);

            } else {
                txtView = (TextView) convertView.getTag();
            }
            String str = mData.get(position);
            if (mData != null && txtView!=null) {
                txtView.setText(str);
            }
            return convertView;
        }

    }

}
