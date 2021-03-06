package pl.umk.andronetandroidclient.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import interfaces.IListener;
import main.Connection;
import pl.umk.andronetandroidclient.R;
import pl.umk.andronetandroidclient.activities.MainActivity;
import pl.umk.andronetandroidclient.network.enums.Tags;
import pl.umk.andronetandroidclient.utils.KeyboardHider;

/**
 * Created by Lukasz on 2015-01-02.
 */
public class ChatNameFragment extends BaseFragment {

    Button mChatNameButton;
    EditText mNameText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.fragment_chat_name, container, false);
        mChatNameButton=(Button)v.findViewById(R.id.chat_name_button);
        mNameText=(EditText)v.findViewById(R.id.chat_name);
        LinearLayout layout=(LinearLayout)v.findViewById(R.id.chat_name_layout);

        KeyboardHider.setupUI(layout, getActivity());
        setupNetworking();
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    private void setupNetworking()
    {
        mClient.removeListeners();
        mChatNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=mNameText.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(getActivity(), R.string.empty_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                mClient.send(Tags.registerChat.name(), mNameText.getText().toString().trim());
            }
        });
        mClient.addListener(Tags.registerChat.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                int id=(int)o;

                final MainActivity activity = (MainActivity) getActivity();
                activity.showChatFragment(id);
            }
        });
    }
}