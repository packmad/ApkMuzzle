package it.saonzo.apkmuzzle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FolderLayout extends LinearLayout implements AdapterView.OnItemClickListener {

    public class Item implements Comparable<Item> {
        private String name;
        private String path;

        public Item(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        @Override
        public int compareTo(Item another) {
            return this.getName().compareTo(another.getName());
        }
    }

    private Context context;
    private IFolderItemListener folderListener;
    private List<Item> items = null;
    private String root = "/";
    private TextView myPath;
    private ListView lstView;

    public FolderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.folderview, this);

        myPath = (TextView) findViewById(R.id.path);
        lstView = (ListView) findViewById(R.id.list);
        getDir(root, lstView);
    }

    public void setIFolderItemListener(IFolderItemListener folderItemListener) {
        this.folderListener = folderItemListener;
    }

    // set Directory for view at anytime
    public void setDir(String dirPath){
        getDir(dirPath, lstView);
    }

    private void getDir(String dirPath, ListView v) {
        myPath.setText(getResources().getString(R.string.location) + ": < " + dirPath + " >");
        items = new ArrayList<Item>();

        File f = new File(dirPath);
        if (!f.exists())
            throw new AssertionError(f.toString() + " doesn't exists");
        File[] files = f.listFiles();

        if (!dirPath.equals(root)) {
            Item r = new Item(root, root);
            Item p = new Item("../", f.getParent());
            items.add(r);
            items.add(p);
        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String fname = file.getName();
            if (file.isDirectory())
                fname = fname + "/";
            Item item = new Item(fname, file.getPath());
            items.add(item);
        }
        Collections.sort(items.subList(2, items.size()));
        setItemList();

    }

    // can manually set Item to display, if you want
    public void setItemList(){
        FileManagerAdapter fileList = new FileManagerAdapter(context, R.layout.filesystemrow, items);
        lstView.setAdapter(fileList);
        lstView.setOnItemClickListener(this);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        String path = items.get(position).getPath();
        File file = new File(path);
        if (file.isDirectory()) {
            if (file.canRead())
                getDir(path, l);
            else {
                // what to do when folder is unreadable
                if (folderListener != null) {
                    folderListener.OnCannotFileRead(file);

                }
            }
        } else {
            //  what to do when file is clicked
            if (folderListener != null) {
                folderListener.OnFileClicked(file);
            }

        }
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        onListItemClick((ListView) arg0, arg0, arg2, arg3);
    }

}