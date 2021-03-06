package pl.umk.andronetandroidclient.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.*;
import android.support.v4.widget.DrawerLayout;
import interfaces.IDisconnected;
import main.Client;
import main.Connection;
import pl.umk.andronetandroidclient.AndroNetApplication;
import pl.umk.andronetandroidclient.enums.FragmentTag;
import pl.umk.andronetandroidclient.fragments.*;
import pl.umk.andronetandroidclient.R;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment; //Fragment managing the behaviors, interactions and presentation of the navigation drawer.
    private CharSequence mTitle; //Used to store the last screen title
    private Client mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroNetApplication application=(AndroNetApplication) getApplication();
        mClient=application.getClient();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mClient.setDisconnectedAction(new IDisconnected() {
            @Override
            public void disconnected(Connection connection) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        ColorsFragment myFragment = (ColorsFragment)getFragmentManager().findFragmentByTag(FragmentTag.Colors.name());
        if (myFragment!=null && myFragment.isVisible()) {
            myFragment.onResume();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        Fragment fragment;
        FragmentTag tag;
        switch(position)
        {
            case 0:
                fragment=new ChatNameFragment();
                tag=FragmentTag.ChatName;
                break;
            case 1:
                fragment=new DrawerFragment();
                tag=FragmentTag.Drawer;
                break;
            default:
                fragment=new ColorsFragment();
                tag=FragmentTag.Colors;
                break;
        }

        switchFragment(fragment, tag);
    }

    public void showChatFragment(int id)
    {
        Fragment fragment=new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);

        fragment.setArguments(bundle);

        switchFragment(fragment, FragmentTag.Chat);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
        setTitle(mTitle);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_disconnect) {
            onDisconnected();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onDisconnected()
    {
        Intent intent=new Intent(this, ConnectActivity.class);
        startActivity(intent);
        mClient.close();
        finish();
    }

    private void switchFragment(Fragment fragment, FragmentTag tag)
    {
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag.name())
                .commit();
    }
}
