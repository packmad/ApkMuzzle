package it.saonzo.apkmuzzle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;

import it.saonzo.rmperm.IOutput;
import it.saonzo.rmperm.Main;

public class PermissionsMangeActivity extends Activity {
    public static final String EXTRA_PERMISSIONSMANAGE = "EXTRA_PERMISSIONSMANAGE";
    public static final String MSG_PERMISSIONSMANAGE = "MSG_PERMISSIONSMANAGE";
    private PermissionAdapter permissionAdapter;
    private String apkpath;
    private boolean noPerms = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        Intent intent = getIntent();
        apkpath = intent.getStringExtra(PermissionsMangeActivity.EXTRA_PERMISSIONSMANAGE);

        if (MainActivity.permsFlag) {
            List<PermissionFlag> pfl = getApkPermission(apkpath);
            noPerms = pfl.isEmpty();
            if (noPerms) {
                Utilities.ShowAlertDialog(
                        this,
                        getResources().getString(R.string.no_perm),
                        getResources().getString(R.string.doesnt_require)
                );
            }
            permissionAdapter = new PermissionAdapter(this, R.layout.permcheck_item, pfl);
            ListView listView = (ListView) findViewById(R.id.permissionlist);
            listView.setAdapter(permissionAdapter);


            Button myButton = (Button) findViewById(R.id.button_save);
            myButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveApk();
                        }
                    });
        } else
            saveApk();

    }


    public void saveApk() {
        String[] apkAndPerms = new String[2];
        apkAndPerms[0] = apkpath;

        if (MainActivity.permsFlag) {
            if (noPerms) {
                Utilities.ShowAlertDialog(
                        this,
                        getResources().getString(R.string.no_perm),
                        getResources().getString(R.string.what_are_remove)
                );
                return;
            }
            boolean noChecks = Iterables.all(
                    permissionAdapter.getItems(),
                    new Predicate<PermissionFlag>() {
                        public boolean apply(PermissionFlag pf) {
                            return !pf.isChecked();
                        }
                    }
            );
            if (noChecks) {
                Utilities.ShowAlertDialog(
                        this,
                        getResources().getString(R.string.no_selection),
                        getResources().getString(R.string.didnt_select));
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (PermissionFlag pf : permissionAdapter.getItems()) {
                if (pf.isChecked()) {
                    sb.append(pf.getName() + ",");
                }
            }
            sb.setLength(sb.length() - 1);
            apkAndPerms[1] = sb.toString();
        } else
            apkAndPerms[1] = null;

        Intent intent = new Intent(this, SaveApkService.class);
        Bundle bundle = new Bundle();
        bundle.putStringArray(MSG_PERMISSIONSMANAGE, apkAndPerms);
        intent.putExtras(bundle);
        startService(intent);

        Intent startMain = new Intent(Intent.ACTION_MAIN); // launch home screen
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private List<PermissionFlag> getApkPermission(String apkPath) {
        JarOutput jo = new JarOutput(IOutput.Level.VERBOSE);
        String[] args = {"--input" , apkPath, "--list"};
        Main.androidMain(jo, args);
        String[] msgs = jo.getMessages().split("\n");
        List<PermissionFlag> permissions = new ArrayList<>();
        for (int i = 1; i < msgs.length-3; i++) {
            permissions.add(new PermissionFlag(msgs[i]));
        }
        return permissions;
    }

    static class ViewHolder {
        TextView title;
        CheckBox checked;
    }

    public class PermissionAdapter extends ArrayAdapter<PermissionFlag> {
        private final Context context;
        private final List<PermissionFlag> modelItems;


        public PermissionAdapter(Context context, int textViewResourceId, List<PermissionFlag> resource) {
            super(context, textViewResourceId, resource);
            this.context = context;
            this.modelItems = resource;
        }

        public List<PermissionFlag> getItems() {
            return modelItems;
        }

        @Override
        public int getCount() {
            return modelItems != null ? modelItems.size() : 0;
        }

        @Override
        public PermissionFlag getItem(int i) {
            return modelItems != null ? modelItems.get(i) : null;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.permcheck_item, null);

                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.checked = (CheckBox) convertView.findViewById(R.id.checked);
                convertView.setTag(holder);

                holder.checked.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        PermissionFlag pf = (PermissionFlag) cb.getTag();
                        pf.setChecked(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            PermissionFlag pf = modelItems.get(position);
            holder.title.setText(pf.getName());
            holder.checked.setChecked(pf.isChecked());
            holder.checked.setTag(pf);

            return convertView;
        }
    }

}
