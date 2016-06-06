package it.saonzo.apkmuzzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import java.io.File;

import it.saonzo.apkmuzzle.FolderLayout;
import it.saonzo.apkmuzzle.IFolderItemListener;
import it.saonzo.apkmuzzle.R;
import it.saonzo.rmperm.Main;

public class FileManagerActivity extends Activity implements IFolderItemListener {
    FolderLayout localFolders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        localFolders = (FolderLayout)findViewById(R.id.localfolders);
        localFolders.setIFolderItemListener(this);
        localFolders.setDir(MainActivity.FOLDER_PATH); // default directory

    }

    // when you can't read a file
    public void OnCannotFileRead(File file) {
        new AlertDialog.Builder(this)
                .setTitle('[' + file.getName() + ']' + getResources().getString(R.string.folder_cant))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                        }).show();

    }

    // when you click a file
    public void OnFileClicked(File file) {
        Intent intent = new Intent(this, PermissionsMangeActivity.class);
        intent.putExtra(PermissionsMangeActivity.EXTRA_PERMISSIONSMANAGE, file.getAbsolutePath());
        startActivity(intent);
    }

}