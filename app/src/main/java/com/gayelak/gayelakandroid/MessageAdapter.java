package com.gayelak.gayelakandroid;

/**
 * Created by radibarq on 2/21/18.
 */
import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.w3c.dom.Text;
import java.util.ArrayList;
import java.util.HashMap;

public class MessageAdapter extends BaseAdapter   {

    ArrayList<HashMap<String, String>> messages;
    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;
    private LayoutInflater myInflater;

    private LayoutInflater layoutInflater;
    public MessageAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        messages = new ArrayList<HashMap<String, String>>();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    public void addMessage(HashMap<String, String> message)
    {

        messages.add(message);
    }

    @Override
    public Object getItem(int position) {

        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        //show message on left or right, depending on if
        //it's incoming or outgoing
        String messageText = messages.get(i).get("messageText");
        String messageFrom= messages.get(i).get("messageFrom");

        int res = 0;

        if (messageFrom.matches(LoginActivity.user.UserId))
        {
            res = R.layout.message;
        }

        else
        {
            res = R.layout.message_left;
        }

        convertView = layoutInflater.inflate(res, viewGroup, false);

        TextView txtMessage = (TextView) convertView.findViewById(R.id.message_text);
        txtMessage.setText(messageText);

      //  if (!messageFrom.matches(LoginActivity.user.UserName))
        //{
          //  TextView txtFrom = (TextView) convertView.findViewById(R.id.message_user);
            //txtFrom.setText(messageFrom);
       // }]

        return convertView;
    }



}
