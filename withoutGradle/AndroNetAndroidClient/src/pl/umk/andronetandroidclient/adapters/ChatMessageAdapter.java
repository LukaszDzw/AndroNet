package pl.umk.andronetandroidclient.adapters;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import pl.umk.andronetandroidclient.R;
import pl.umk.andronetandroidclient.utils.ChatMessage;
import pl.umk.andronetandroidclient.utils.ChatUser;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Lukasz on 2015-01-03.
 */
public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    private Context mContext;
    private ArrayList<ChatMessage> mMessages;
    private ArrayList<ChatUser> mUsers;
    private int mMyUserId;

    public ChatMessageAdapter(Context context,
                              ArrayList<ChatMessage> values,
                              int myUserId,
                              ArrayList<ChatUser> users)
    {
        super(context, R.layout.view_chat_text_my, values);
        mContext = context;
        mMessages = values;
        mMyUserId=myUserId;
        mUsers=users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ChatMessage message=mMessages.get(position);

        View rowView;
        if (message.id == mMyUserId) {
            rowView = inflater.inflate(R.layout.view_chat_text_my, parent, false);
        }
        else
        {
            rowView = inflater.inflate(R.layout.view_chat_text_other, parent, false);
        }

        TextView dateView=(TextView)rowView.findViewById(R.id.chat_message_time);
        dateView.setText(getTime());

        String name=mUsers.
        String messageWithUser=new StringBuilder(name).append(": ").append(message.message).toString();
        TextView text = (TextView)rowView.findViewById(R.id.chat_message);
        text.setText(messageWithUser);

        return rowView;
    }

    private String getTime()
    {

        Time now = new Time();
        now.setToNow();
        return now.format("%k:%M:%S");

        /*
        Calendar c = Calendar.getInstance();
        return c.getTime().toString();*/
    }
}
