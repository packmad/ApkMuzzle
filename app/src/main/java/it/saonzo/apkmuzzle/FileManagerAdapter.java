package it.saonzo.apkmuzzle;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FileManagerAdapter extends ArrayAdapter<String> {

    Context mContext;
    List<FolderLayout.Item> mQuestionArrayList;
    int mLayoutResourceId;



    public FileManagerAdapter(Context context, int layoutResourceId, List<FolderLayout.Item> questionsArrayList) {
        super(context, layoutResourceId);
        mContext = context;
        this.mQuestionArrayList = questionsArrayList;
        this.mLayoutResourceId = layoutResourceId;
    }

    @Override
    public int getCount() {
        return mQuestionArrayList.size();
    }


    @Override
    public View getView(int position, View row, ViewGroup parent) {
        String question = mQuestionArrayList.get(position).getName();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        row = inflater.inflate(this.mLayoutResourceId, parent, false);
        TextView questionTxtView = (TextView) row.findViewById(R.id.rowtext);
        questionTxtView.setText(question);
        if (question.equals(MainActivity.APPFOLDERNAME + '/'))
            questionTxtView.setTextColor(Color.RED);
        else if (question.contains("/"))
            questionTxtView.setTextColor(Color.parseColor("#FFA500"));
        else if (question.contains(".apk"))
            questionTxtView.setTextColor(Color.GREEN);

        return row;
    }
}