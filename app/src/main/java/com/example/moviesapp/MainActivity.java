package com.example.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DetailFragment.Callback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String CONTAINER_LOG_TAG = "Detail Fragment container";

    public static final int TABLET_DP = 600;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        setTheme(android.R.style.Theme_Holo);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Log.d(LOG_TAG, "Two pane");
            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, new DetailFragment(), CONTAINER_LOG_TAG)
                    .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
            Log.d(LOG_TAG, "One pane");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            MovieAdapter.width = Resources.getSystem().getDisplayMetrics().widthPixels / 3;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            MovieAdapter.width = Resources.getSystem().getDisplayMetrics().widthPixels / 2;
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, CONTAINER_LOG_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }

    public static boolean isTablet(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density  = context.getResources().getDisplayMetrics().density;
        float dpWidth  = outMetrics.widthPixels / density;
        return dpWidth >= TABLET_DP;
    }
}
