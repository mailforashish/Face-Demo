package com.zeeplive.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zeeplive.app.R;
import com.zeeplive.app.response.language.LanguageData;
import com.zeeplive.app.utils.SessionManager;

import java.util.ArrayList;

public class LanguageAdapter extends BaseAdapter {

    Context context;
    ArrayList<LanguageData> languageDataArrayList;
    LayoutInflater inflter;

    public LanguageAdapter(Context context, ArrayList<LanguageData> languageDataArrayList) {
        this.context = context;
        this.languageDataArrayList = languageDataArrayList;
        this.inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return languageDataArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return languageDataArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.grid_language, null); // inflate the layout
        TextView icon = (TextView) view.findViewById(R.id.tv_landata); // get the reference of ImageView

        if (languageDataArrayList.get(i).getLanguage().equals("search")) {
            icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
            icon.setText("");
        } else {
            icon.setText(languageDataArrayList.get(i).getLanguage());
        }

        if (languageDataArrayList.get(i).getId() == new SessionManager(context).gettLangState()) {
            icon.setTextColor(context.getResources().getColor(R.color.colorPink));
        }
        return view;
    }
}
