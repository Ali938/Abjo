package coleo.com.abjo.activity.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.beardedhen.androidbootstrap.ColorOfProgress;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import coleo.com.abjo.R;
import coleo.com.abjo.activity.MainActivity;
import coleo.com.abjo.adapter.NavigationAdapter;
import coleo.com.abjo.constants.Constants;
import coleo.com.abjo.data_class.NavigationDrawerItem;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class Heart extends Fragment implements Serializable {

    private static final String TAG = "menu";
    private boolean walk;

    private TextView name;
    private TextView level;
    private TextView point;
    private TextView coin;
    private TextView hour;

    private DuoDrawerLayout drawerLayout;
    private NavigationAdapter adapter;

    public Heart() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_operation, container, false);
        View contentOfDrawer = inflater.inflate(R.layout.inside_of_drawer_layout, container, false);
        View menu = inflater.inflate(R.layout.navigation_menu_layout, container, false);

        FrameLayout menuFrame = view.findViewById(R.id.content_menu_frame);
        menuFrame.removeAllViews();
        menuFrame.addView(menu);

        FrameLayout frame = view.findViewById(R.id.content_frame);
        frame.removeAllViews();
        frame.addView(contentOfDrawer);

        drawerLayout = (DuoDrawerLayout) view.findViewById(R.id.drawer_layout);
        Toolbar toolbar = new Toolbar(Constants.context);
        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(((Activity) Constants.context), drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        RecyclerView nav_list_view = menu.findViewById(R.id.navigation_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(container.getContext());
        nav_list_view.setLayoutManager(mLayoutManager);
        ArrayList<NavigationDrawerItem> arrayList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            arrayList.add(new NavigationDrawerItem());
        }
        adapter = new NavigationAdapter(arrayList, getContext());
        nav_list_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        BootstrapProgressBar progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setBootstrapBrand(new ColorOfProgress());
        name = view.findViewById(R.id.user_name_text_view_id);
        coin = view.findViewById(R.id.coin_of_activity_text);
        point = view.findViewById(R.id.point_text_id);
        level = view.findViewById(R.id.level_text_id);
        hour = view.findViewById(R.id.hour_of_activity_text);

        Button start = view.findViewById(R.id.start_button_id);
        Switch switchUI = view.findViewById(R.id.switch_id);
        switchUI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                walk = isChecked;
            }
        });
        switchUI.setChecked(true);
        walk = true;
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) container.getContext()).showAfterStart(walk);
            }
        });

        drawerLayout.openDrawer();
        drawerLayout.closeDrawer();
        return view;
    }

    public void openNavigation() {
        drawerLayout.openDrawer();
    }



}
