package com.example.etoolroom;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactListAdapter extends ArrayAdapter<String> implements SectionIndexer {

    String[] sections;
    List<String> cList;
    List<String> sectionLetters=new ArrayList<String>();

    public ContactListAdapter(Context context, List<String> list) {

        //mult select list
        super(context,android.R.layout.simple_list_item_multiple_choice,list);
        this.cList = list;

        //bubble search scroller
        for (int x = 0; x < cList.size(); x++) {
            String fruit = cList.get(x);
            String ch = fruit.charAt(0)+"";
            ch = ch.toUpperCase(Locale.US);

            sectionLetters.add(ch);
        }

        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);

        sections = new String[sectionList.size()];

        sectionList.toArray(sections);
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return sectionIndex;
    }

    @Override
    public int getSectionForPosition(int position) {
        return position;
    }
}
