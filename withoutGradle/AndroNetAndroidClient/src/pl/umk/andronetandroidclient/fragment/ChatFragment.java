package pl.umk.andronetandroidclient.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import interfaces.IDisconnected;
import interfaces.IListener;
import main.Client;
import main.Connection;
import pl.umk.andronetandroidclient.AndroNetApplication;
import pl.umk.andronetandroidclient.R;
import pl.umk.andronetandroidclient.network.enums.Tags;

import java.util.ArrayList;

public class ChatFragment extends BaseFragment {

    private ListView mChatListView;
    private Button mChatButton;
    private EditText mChatText;

    private ArrayAdapter<String> mTextAdapter;
    private ArrayList<String> mMessages;

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

        mMessages=new ArrayList<String>();
        mTextAdapter=new ArrayAdapter<String>(this.getActivity(), R.layout.view_chat_list_text, mMessages);
        mChatListView.setAdapter(mTextAdapter);

        setup();

        return v;
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    private void setup()
    {
        mClient.removeListeners();
        mClient.addListener(Tags.chatMessage.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                final String message = (String) o;
                mMessages.add(message);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextAdapter.notifyDataSetChanged();
                    }
                });
            }}
        );

        mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mClient.send(Tags.chatMessage.name(), mChatText.getText().toString());
                    }
                });
            }
        });

        mClient.setDisconnectedAction(new IDisconnected() {
            @Override
            public void disconnected(Connection connection) {
                System.out.println("koniec połączenia");
            }
        });
    }
}
