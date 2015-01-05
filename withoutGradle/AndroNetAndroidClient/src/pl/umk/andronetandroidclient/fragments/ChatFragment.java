package pl.umk.andronetandroidclient.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import interfaces.IListener;
import main.Connection;
import pl.umk.andronetandroidclient.R;
import pl.umk.andronetandroidclient.adapters.ChatMessageAdapter;
import pl.umk.andronetandroidclient.network.enums.Tags;
import pl.umk.andronetandroidclient.utils.ChatMessage;
import pl.umk.andronetandroidclient.utils.ChatUser;

import java.util.*;

public class ChatFragment extends BaseFragment {

    private ListView mChatListView;
    private Button mChatButton;
    private EditText mChatText;
    private RelativeLayout mActiveUsers;
    private TextView mActiveUsersCounter;
    private int mMyId;

    private ChatMessageAdapter mChatMessageAdapter;
    private ArrayList<ChatMessage> mMessages;
    private SparseArray<String> mUsers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        mChatListView=(ListView)v.findViewById(R.id.chat_list);
        mChatButton=(Button)v.findViewById(R.id.chat_button);
        mChatText=(EditText)v.findViewById(R.id.chat_text);
        mActiveUsers=(RelativeLayout)v.findViewById(R.id.chat_active);
        mActiveUsersCounter=(TextView)v.findViewById(R.id.chat_users_counter);
        LinearLayout layout=(LinearLayout)v.findViewById(R.id.chat_layout);

        mActiveUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        setupUI(layout);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null){
            mMyId = bundle.getInt("id", 0);
        }

        mMessages = new ArrayList<>();
        mUsers = new SparseArray<>();
        mChatMessageAdapter=new ChatMessageAdapter(getActivity(), mMessages, mMyId, mUsers);
        mChatListView.setAdapter(mChatMessageAdapter);
        setupNetworking();
    }

    @Override
    public void onDestroy()
    {
        mClient.removeListeners();
        mClient.send(Tags.disconnected.name(), new Object());
        super.onDestroy();;
    }

    public void showDialog() {
        ArrayList<String> items=new ArrayList<>();

        for(int i=0; i<mUsers.size(); i++)
        {
            int key=mUsers.keyAt(i);
            items.add(mUsers.get(key));
        }

        CharSequence[] charItems = items.toArray(new CharSequence[items.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.active)
                .setItems(charItems, null).setPositiveButton(R.string.ok, null);
        builder.create().show();
    }

    private void setupNetworking()
    {
        mClient.removeListeners();

        //send message
        mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = mChatText.getText().toString();
                if(message.length()>0) {
                    mClient.send(Tags.chatMessage.name(), message);
                    mChatText.setText("");
                }
            }
        });

        //get user
        mClient.addListener(Tags.getChatUser.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                ChatUser newUser = (ChatUser) o;
                mUsers.put(newUser.id, newUser.name);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActiveUsersCounter.setText(String.valueOf(mUsers.size()));
                    }
                });
            }
        });

        //disconnected
        mClient.addListener(Tags.disconnected.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                int id=(Integer) o;
                mUsers.remove(id);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActiveUsersCounter.setText(mUsers.size());
                    }
                });
            }
        });

        //get messages
        mClient.addListener(Tags.chatMessage.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                ChatMessage message = (ChatMessage) o;
                mMessages.add(message);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mChatMessageAdapter.notifyDataSetChanged();
                        mChatListView.setSelection(mChatListView.getCount() - 1); // scroll to down
                    }
                });
            }
        });

        mClient.send(Tags.getChatUser.name(), new Object());
    }

}
