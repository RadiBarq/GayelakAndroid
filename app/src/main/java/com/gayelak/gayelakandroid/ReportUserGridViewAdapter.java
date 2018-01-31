package com.gayelak.gayelakandroid;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gayelak.gayelakandroid.R;

import org.w3c.dom.Text;

/**
 * Created by radibarq on 1/29/18.
 */

public class ReportUserGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    public ReportUserGridViewAdapter(Context c)
    {
        mContext = c;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public int getCount() {

        return imagesArray.length;

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        view = mInflater.inflate(R.layout.report_user_grid_view_layout, parent, false);
        view.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 500));
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(imagesArray[position]);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(textsArray[position]);
        return view;
    }


    private int[] imagesArray = {

            R.drawable.chat, R.drawable.thief, R.drawable.volcano, R.drawable.suspects, R.drawable.zombie,
            R.drawable.syringe, R.drawable.spam, R.drawable.dislike, R.drawable.mailbox

    };



    private String[] textsArray = {

      "اسلوب عدائي", "محتال", "لم يظهر في القائات", "اسلوب شائك", "غبر فعال", "يبيع بضائع ممنوعة", "يرسل رسائل مزعجة", "مزور بضائع", "امور اخرى"

    };

}
