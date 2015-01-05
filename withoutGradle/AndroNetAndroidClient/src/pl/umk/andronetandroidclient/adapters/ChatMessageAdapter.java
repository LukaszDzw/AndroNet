package pl.umk.andronetandroidclient.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import pl.umk.andronetandroidclient.R;
import pl.umk.andronetandroidclient.utils.ChatMessage;

import java.util.ArrayList;

/**
 * Created by Lukasz on 2015-01-03.
 */
public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    private Context mContext;
    private ArrayList<ChatMessage> mMessages;
    private SparseArray<String> mUsers;
    private int mMyUserId;

    public ChatMessageAdapter(Context context,
                              ArrayList<ChatMessage> values,
                              int myUserId,
                              SparseArray<String> users)
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
        String name=mUsers.get(message.id);
        if(name==null) name="unknown";

        View rowView;
        if (message.id == mMyUserId) {
            rowView = inflater.inflate(R.layout.view_chat_text_my, parent, false);
        }
        else
        {
            rowView = inflater.inflate(R.layout.view_chat_text_other, parent, false);
        }

        TextView dateView=(TextView)rowView.findViewById(R.id.chat_message_time);
        dateView.setText(new String(message.time));


        String messageWithUser=new StringBuilder(name).append(": ").append(message.message).toString();
        TextView text = (TextView)rowView.findViewById(R.id.chat_message);
        text.setText(messageWithUser);

        return rowView;
    }
}
