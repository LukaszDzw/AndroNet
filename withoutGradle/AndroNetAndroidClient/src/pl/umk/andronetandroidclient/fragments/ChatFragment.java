package pl.umk.andronetandroidclient.fragments;

import android.os.Bundle;
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
    private int mMyId;

    private ChatMessageAdapter mChatMessageAdapter;
    private ArrayList<ChatMessage> mMessages;
    private ArrayList<ChatUser> mUsers;

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

        LinearLayout layout=(LinearLayout)v.findViewById(R.id.chat_layout);
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
        mUsers = new ArrayList<>();
        mChatMessageAdapter=new ChatMessageAdapter(getActivity(), mMessages, mMyId, mUsers);
        mChatListView.setAdapter(mChatMessageAdapter);

        setupNetworking();
    }

    @Override
    public void onStop()
    {
        mClient.send(Tags.disconnected.name(), new Object());
        super.onStop();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    private void setupNetworking()
    {
        mClient.removeListeners();

        //send message
        mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mClient.send(Tags.chatMessage.name(), mChatText.getText().toString());
                        mChatText.setText("");
                    }
                });
            }
        });

        //get users
        mClient.addListener(Tags.getChatUsers.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                ArrayList<ChatUser> newUsers = (ArrayList<ChatUser>) o; //Serialization problem

                mUsers.addAll(newUsers);
                setupAfterGotUsers();
            }
        });

        mClient.send(Tags.getChatUsers.name(), new Object());
    }

    private void setupAfterGotUsers()
    {
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
                    }}
        );

        //disconnected
        mClient.addListener(Tags.disconnected.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                int id=(Integer) o;
                mUsers.remove(id);
            }
        });
    }
}
