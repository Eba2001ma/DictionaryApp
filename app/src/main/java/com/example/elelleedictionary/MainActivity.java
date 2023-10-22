package com.example.elelleedictionary;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    MenuItem menuSetting;
    DictionaryFragment dictionaryFragment;
    BookmarkFragment bookmarkFragment;
    Toolbar toolbar;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new DBHelper(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(this,drawer,toolbar, "Open Navigation drawer","Close Navigation drawer");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dictionaryFragment = new DictionaryFragment();
        bookmarkFragment = BookmarkFragment.getNewInstance(dbHelper);
        goToFragment(dictionaryFragment, true);
        dictionaryFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                String id = Global.getState(MainActivity.this, "dic_type");
                int dicType = id == null?R.id.action_eng_oro : Integer.valueOf(id);
                goToFragment(DetailFragment.getNewInstance(value,dbHelper,dicType),false);
            }
        });
        bookmarkFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                String id = Global.getState(MainActivity.this, "dic_type");
                int dicType = id == null?R.id.action_eng_oro : Integer.valueOf(id);
                goToFragment(DetailFragment.getNewInstance(value,dbHelper,dicType),false);
            }
        });
        EditText edit_search = findViewById(R.id.edit_search);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dictionaryFragment.filterValue(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void saveContentView(int activity_main) {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menuSetting = menu.findItem(R.id.action_settings);

        // Find the NavigationView and retrieve the rate and share menu items
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu navMenu = navigationView.getMenu();
        MenuItem menuRate = navMenu.findItem(R.id.nav_rate);
        MenuItem menuShare = navMenu.findItem(R.id.nav_share);

        // Set the visibility of rate and share options
        menuRate.setVisible(true);
        menuShare.setVisible(true);

        // Add click listeners to rate and share options
        menuRate.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openAppRating();
                return true;
            }
        });

        menuShare.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                shareApp();
                return true;
            }
        });


        String id = Global.getState(this, "dic_type");
        if (id!=null)
            onOptionsItemSelected(menu.findItem(Integer.valueOf(id)));
        else {
            ArrayList<String> source = dbHelper.getWord(R.id.action_eng_oro);
            dictionaryFragment.resetDataSource(source);
        }
        return true;
    }
/*
    @Override public boolean onOptionsItemSelected (MenuItem item){
       int id = item.getItemId();

        if (R.id.action_settings==id)return true;
        Global.saveState(this, "dic_type", String.valueOf(id));

        ArrayList<String> source = dbHelper.getWord(id);
        if (id==R.id.action_eng_oro){

            dictionaryFragment.resetDataSource(source);
            menuSetting.setIcon(getDrawable(R.drawable.english_oromo_1));

        } else if (id==R.id.action_oro_eng) {
            dictionaryFragment.resetDataSource(source);
            menuSetting.setIcon(getDrawable(R.drawable.oromo_english_1));

        }


        return super.onOptionsItemSelected(item);
    }
  */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // Handle the settings action
            return true;
        } else if (id == R.id.nav_rate) {
            // Handle the rate action
            openAppRating();
            return true;
        } else if (id == R.id.nav_share) {
            // Handle the share action
            shareApp();
            return true;
        }

        Global.saveState(this, "dic_type", String.valueOf(id));

        ArrayList<String> source = dbHelper.getWord(id);
        if (id == R.id.action_eng_oro) {
            dictionaryFragment.resetDataSource(source);
            menuSetting.setIcon(getDrawable(R.drawable.english_oromo_1));
        } else if (id == R.id.action_oro_eng) {
            dictionaryFragment.resetDataSource(source);
            menuSetting.setIcon(getDrawable(R.drawable.oromo_english_1));
        }

        return super.onOptionsItemSelected(item);
    }

    private void openAppRating() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Could not open Play Store", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareApp() {
        String appUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome app: " + appUrl);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_bookmark){
            String activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getSimpleName();
            if (!activeFragment.equals(BookmarkFragment.class.getSimpleName())){
                goToFragment(bookmarkFragment, false);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    void goToFragment(Fragment fragment, boolean isTop){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container,fragment);
        if (!isTop)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getSimpleName();
        if (activeFragment.equals((BookmarkFragment.class.getSimpleName()))){
            menuSetting.setVisible(false);
            toolbar.findViewById(R.id.edit_search).setVisibility(View.GONE);
            toolbar.setTitle("Bookmark");
        }else {
            menuSetting.setVisible(true);
            toolbar.findViewById(R.id.edit_search).setVisibility(View.VISIBLE);
            toolbar.setTitle("");
        }
        return true;
    }
}