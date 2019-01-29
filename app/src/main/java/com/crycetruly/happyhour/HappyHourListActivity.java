package com.crycetruly.happyhour;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crycetruly.happyhour.fragments.DrinksandFood;
import com.crycetruly.happyhour.model.HappyHour;
import com.crycetruly.happyhour.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class HappyHourListActivity extends AppCompatActivity {

    private ViewPager view_pager;
    private TabLayout tab_layout;
    private List<HappyHour> happyHours=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy_hour_list);

        initToolbar();
        initComponent();

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.blue_600);
    }

    private void initComponent() {
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(view_pager);

        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);

        view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                String [] categories=getResources().getStringArray(R.array.shop_category_title);
                getSupportActionBar().setTitle(String.valueOf(categories[position]));



            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                return   new DrinksandFood();
                case 1:
                    return   new DrinksandFood();

                case 2:
                    return   new DrinksandFood();

                case 3:
                    return   new DrinksandFood();

                case 4:
                    return   new DrinksandFood();

                case 5:
                    return   new DrinksandFood();
                case 6:
                    return   new DrinksandFood();

                case 7:
                    return   new DrinksandFood();

                case 8:
                    return   new DrinksandFood();

                case 9:
                    return   new DrinksandFood();

            }


         return null;
        }

        @Override
        public int getCount() {
            return 10;
        }



        @Override
        public CharSequence getPageTitle(int position) {
            String [] categories=getResources().getStringArray(R.array.shop_category_title);
           return String.valueOf(categories[position]);
            }



        }
    }
