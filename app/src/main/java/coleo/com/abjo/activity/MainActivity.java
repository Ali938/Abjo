package coleo.com.abjo.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;

import coleo.com.abjo.R;
import coleo.com.abjo.activity.fragments.AfterStartFragment;
import coleo.com.abjo.activity.fragments.Heart;
import coleo.com.abjo.activity.fragments.LeaderBoard;
import coleo.com.abjo.activity.fragments.Profile;
import coleo.com.abjo.activity.login.Splash;
import coleo.com.abjo.activity.menu.AboutActivity;
import coleo.com.abjo.activity.menu.MassageActivity;
import coleo.com.abjo.activity.menu.RuleActivity;
import coleo.com.abjo.activity.menu.ShareActivity;
import coleo.com.abjo.adapter.NavigationAdapter;
import coleo.com.abjo.constants.Constants;
import coleo.com.abjo.data_class.DateAction;
import coleo.com.abjo.data_class.LeaderBoardData;
import coleo.com.abjo.data_class.NavigationDrawerItem;
import coleo.com.abjo.data_class.ProfileData;
import coleo.com.abjo.server_class.ServerClass;
import coleo.com.abjo.service.SaverReceiver;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class MainActivity extends AppCompatActivity implements Serializable {

    final Fragment fragment1 = new Profile();
    final Fragment fragment2 = new Heart();
    final Fragment fragment3 = new LeaderBoard();
    final Fragment fragment4 = new AfterStartFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment2;
    int mainFragmentNumber = 0;
    Context context = this;

    private long lastTime = 0;

    private TabLayout tabLayout;
    private ImageView menuButton;
    private ImageView share;
    private DuoDrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TypefaceProvider.registerDefaultIconSets();
        Constants.context = this;
        LayoutInflater inflater = getLayoutInflater();
        View menu = inflater.inflate(R.layout.navigation_menu_layout, null, false);

        FrameLayout menuFrame = findViewById(R.id.content_menu_frame);
        menuFrame.removeAllViews();
        menuFrame.addView(menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = new Toolbar(Constants.context);
        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(((Activity) Constants.context), drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        RecyclerView nav_list_view = menu.findViewById(R.id.navigation_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        nav_list_view.setLayoutManager(mLayoutManager);
        ArrayList<NavigationDrawerItem> arrayList = new ArrayList<>();

        arrayList.add(new NavigationDrawerItem(" معرفی به دوستان ", R.mipmap.share_app, new Intent(this, ShareActivity.class)));
        arrayList.add(new NavigationDrawerItem(" درباره ما ", R.mipmap.about_us, new Intent(this, AboutActivity.class)));
        arrayList.add(new NavigationDrawerItem(" پیام ها ", R.mipmap.massage, new Intent(this, MassageActivity.class)));
        arrayList.add(new NavigationDrawerItem(" قوانین و ضوابط ", R.mipmap.laws, new Intent(this, RuleActivity.class)));
        arrayList.add(new NavigationDrawerItem(" خروج از حساب کاربری ", R.mipmap.exit, new Intent(this, Splash.class)));

        NavigationAdapter adapter = new NavigationAdapter(arrayList, this);
        nav_list_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        fm.beginTransaction().add(R.id.main_container, fragment1, "1").hide(fragment1).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").commit();
        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment4, "4").hide(fragment4).commit();

        tabLayout = findViewById(R.id.navigation);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                drawerLayout.closeDrawer();
                int icon = 0;
                switch (tab.getPosition()) {
                    case 0: {
//                        ServerClass.getLeaderBoard(context);
                        icon = R.drawable.leader_board_selected;
                        fm.beginTransaction().hide(active).show(fragment3).commit();
                        active = fragment3;
                        break;
                    }
                    case 1: {
                        icon = R.drawable.heart_selected;
                        if (mainFragmentNumber == 0) {
                            fm.beginTransaction().hide(active).show(fragment2).commit();
                            active = fragment2;
                        } else {
                            if (mainFragmentNumber == 1) {
                                fm.beginTransaction().hide(active).show(fragment4).commit();
                                active = fragment4;
                            }
                        }
                        break;
                    }
                    case 2: {
//                        ServerClass.getHistory(context, 0);
                        icon = R.drawable.profile_selected;
                        fm.beginTransaction().hide(active).show(fragment1).commit();
                        active = fragment1;
                        break;
                    }
                }
                tab.setIcon(icon);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int icon = 0;
                switch (tab.getPosition()) {
                    case 0: {
                        icon = R.drawable.leader_board;
                        break;
                    }
                    case 1: {
                        icon = R.drawable.heart;
                        break;
                    }
                    case 2: {
                        icon = R.drawable.profile;
                        break;
                    }
                }
                tab.setIcon(icon);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleNavigation();
            }
        });

        share = findViewById(R.id.share_button_id);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share.setEnabled(false);
                Intent intent = new Intent(context, ShareActivity.class);
                //todo valid code
                intent.putExtra(Constants.DATA_INVITE_CODE,"adadasdas");
                startActivity(intent);
            }
        });


        tabLayout.getTabAt(0).setText("");
        tabLayout.getTabAt(0).setIcon(R.drawable.leader_board_selected);
        tabLayout.getTabAt(1).setText("");
        tabLayout.getTabAt(1).setIcon(R.drawable.heart);
        tabLayout.getTabAt(2).setText("");
        tabLayout.getTabAt(2).setIcon(R.drawable.profile);

        tabLayout.selectTab(tabLayout.getTabAt(1));
        drawerLayout.openDrawer();
        drawerLayout.closeDrawer();

        Bundle extra = getIntent().getExtras();
//        boolean temp = extra.getBoolean(Constants.FROM_NOTIFICATION, false);
        boolean temp = false;
        if (!Constants.getLastAction().equals(Constants.ACTION.STOP_FOREGROUND_ACTION)) {
            temp = true;
        }
        if (temp) {
            if (ServerClass.isNetworkConnected(context))
                showAfterStartFromNotification();
            else
                Toast.makeText(context, "اینترنت خود را برسی کنید", Toast.LENGTH_LONG).show();
        }

        ComponentName receiver = new ComponentName(context, SaverReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }



    public void toggleNavigation(){
        if (drawerLayout.isDrawerOpen())
            drawerLayout.closeDrawer();
        else
            drawerLayout.openDrawer();
    }

    public void closeNavigation() {
        if (drawerLayout.isDrawerOpen())
            drawerLayout.closeDrawer();
    }

    public void openNavigation() {
        if (!drawerLayout.isDrawerOpen())
            drawerLayout.openDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.context = this;
        share.setEnabled(true);
//        checkPermission();
//        ServerClass.getProfile(this, true);
    }

    public boolean checkFullPermission() {
        if (Constants.checkPermission(this)) {
            return Constants.checkLocation(this);
        }
        return false;
    }

    public void checkPermission() {
        if (Constants.checkPermission(this)) {
            Log.i("MAIN_ACTIVITY", "onResume: have permission");
        }
    }

    public void noPermission() {
        Toast.makeText(this, "برنامه برای عملکرد بهتر نیاز به دسترسی های لازم دارد",
                Toast.LENGTH_LONG).show();
    }

    public void stopTimer(){
        ((AfterStartFragment) fragment4).pauseFromNotification();
    }

    public void startTimer(){
        ((AfterStartFragment) fragment4).resumeFromNotification();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Do not call super class method here.
//        super.onSaveInstanceState(outState);
    }

    public void showAfterStart(boolean isStep, ProfileData data) {
        mainFragmentNumber = 1;
        fm.beginTransaction().hide(active).show(fragment4).commit();
        active = fragment4;
        ((AfterStartFragment) fragment4).startServiceFromOut(isStep, data);
        menuButton.setVisibility(View.INVISIBLE);
    }

    public void updateLeaderBoard(ArrayList<LeaderBoardData> arrayList) {
        ((LeaderBoard) fragment3).update(arrayList);
    }

    public void updateHistory(ArrayList<DateAction> dateActions) {
        ((Profile) fragment1).update(dateActions);
    }

    public void showAfterStartFromNotification() {
        mainFragmentNumber = 1;
        fm.beginTransaction().hide(active).show(fragment4).commit();
        active = fragment4;
//        ServerClass.getProfile(context, false);
        menuButton.setVisibility(View.INVISIBLE);
    }

    public void backToMain() {
        mainFragmentNumber = 0;
        fm.beginTransaction().hide(active).show(fragment2).commit();
        active = fragment2;
        menuButton.setVisibility(View.VISIBLE);
        closeNavigation();
    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (lastTime + 2000 > now) {
            finish();
        } else {
            lastTime = now;
            Toast.makeText(this, "برای خروج دوباره بازگشت را بزنید", Toast.LENGTH_LONG).show();
        }
    }

    public void updateProfile(ProfileData data) {
        ((Heart) fragment2).updateProfile(data);
    }

    public void updateProfileFromNotif(ProfileData data) {
        ((AfterStartFragment) fragment4).startServiceFromNotification(data);
    }

}
