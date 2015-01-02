package pl.umk.andronetandroidclient.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import main.Client;
import pl.umk.andronetandroidclient.R;

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
        mNameText=(EditText)v.findViewById(R.id.chat_text);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    private void setup()
    {
        mChatNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}