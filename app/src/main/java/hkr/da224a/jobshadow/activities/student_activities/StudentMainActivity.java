package hkr.da224a.jobshadow.activities.student_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Stack;

import hkr.da224a.jobshadow.NoSwipeViewPager;
import hkr.da224a.jobshadow.R;
import hkr.da224a.jobshadow.activities.LoginActivity;
import hkr.da224a.jobshadow.activities.MyApplicationsActivity;
import hkr.da224a.jobshadow.fragments.Adapters.ViewPagerAdapter;
import hkr.da224a.jobshadow.fragments.NotificationsFragment;
import hkr.da224a.jobshadow.fragments.OfferFragment;
import hkr.da224a.jobshadow.fragments.SearchFragment;
import hkr.da224a.jobshadow.model.Student;
import hkr.da224a.jobshadow.utils.SQLiteDatabaseHelper;

public class StudentMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Stack<Integer> backStack = new Stack<>();
    private DrawerLayout drawer = null;
    private BottomNavigationView bottomNav;
    private NoSwipeViewPager viewPager;

    public int userID;
    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        MobileAds.initialize(this,"ca-app-pub-9133678383325719~9752466165");
        adView=findViewById(R.id.AdView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email_of_user");
        int ID = 0;

        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        ArrayList<Student> studentList = sqLiteDatabaseHelper.getAllStudents();
        for(int i = 0; i < studentList.size(); i++){
            if(studentList.get(i).getEmailAddress().equals(email)){
                ID = studentList.get(i).getStudentID();
            }
        }
        userID = ID;

        viewPager = (NoSwipeViewPager) findViewById(R.id.main_menu_holder);
        setupViewPager(viewPager);
        viewPager.setPagingEnabled(false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {
                int currentItem = viewPager.getCurrentItem();
                if (backStack.empty())
                    backStack.push(0);

                if (backStack.contains(currentItem)) {
                    backStack.remove(backStack.indexOf(currentItem));
                    backStack.push(currentItem);
                } else {
                    backStack.push(currentItem);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        getSupportActionBar().setTitle("Home");
                        return true;
                    case R.id.navigation_search:
                        viewPager.setCurrentItem(1);
                        getSupportActionBar().setTitle("Search");
                        return true;
                    case R.id.navigation_notifications:
                        viewPager.setCurrentItem(2);
                        getSupportActionBar().setTitle("Notifications");
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }

        if (backStack.size() > 1) {
            backStack.pop();
            int i = backStack.lastElement();
            viewPager.setCurrentItem(i);
            Menu menu = bottomNav.getMenu();
            MenuItem item = menu.getItem(i);
            item.setChecked(true);
            switch (i) {
                case 0:
                    getSupportActionBar().setTitle("Home");
                    break;
                case 1:
                    getSupportActionBar().setTitle("Search");
                    break;
                case 2:
                    getSupportActionBar().setTitle("Notifications");
                    break;
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation_bar view item clicks here.
        int id = item.getItemId();

        Intent intent = null;

        switch (id) {
            case R.id.nav_settings:
                intent = new Intent(StudentMainActivity.this, StudentSettingsActivity.class);
                StudentMainActivity.this.startActivity(intent);
                break;
            case R.id.nav_profile:
                intent = new Intent(StudentMainActivity.this, StudentProfileActivity.class);
                StudentMainActivity.this.startActivity(intent);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                intent = new Intent(StudentMainActivity.this, LoginActivity.class);
                StudentMainActivity.this.startActivity(intent);
                finish();
                break;
            case R.id.nav_applications:
                intent = new Intent(StudentMainActivity.this, MyApplicationsActivity.class);
                intent.putExtra("userID",userID);
                StudentMainActivity.this.startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setupViewPager(NoSwipeViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter =
                new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new OfferFragment(), "Offers");
        viewPagerAdapter.addFragment(new SearchFragment(), "Search");
        viewPagerAdapter.addFragment(new NotificationsFragment(), "Notifications");
        viewPager.setAdapter(viewPagerAdapter);
    }

    public void setToolbar(Toolbar toolbar) {
        AppCompatActivity actionBar = this;
        actionBar.setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        toolbar.setTitle("Home");

    }
}
