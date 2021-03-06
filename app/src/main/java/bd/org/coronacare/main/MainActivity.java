package bd.org.coronacare.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import bd.org.coronacare.R;
import bd.org.coronacare.main.analysis.AnalysisFragment;
import bd.org.coronacare.main.chat.ChatFragment;
import bd.org.coronacare.main.home.HomeFragment;
import bd.org.coronacare.main.menu.MenuFragment;
import bd.org.coronacare.main.plasma.PlasmaFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);

        toolbar.setNavigationIcon(null);

        if (savedInstanceState==null) {
            showHome();
        }
    }

    private void showHome() {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main_frame, new HomeFragment())
                .commit();
        bottomNavigation.setSelectedItemId(R.id.bn_home);
        toolbar.setTitle("Home");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        String title = null;

        switch (item.getItemId()) {
            case R.id.bn_plasma:
                fragment = new PlasmaFragment();
                title = "Nearby Plasma Donors";
                break;
            case R.id.bn_analysis:
                fragment = new AnalysisFragment();
                title = "Risk Assessment";
                break;
            case R.id.bn_chat:
                fragment = new ChatFragment();
                title = "Chat Room";
                break;
            case R.id.bn_menu:
                fragment = new MenuFragment();
                title = "Menu";
                break;
            default:
                fragment = new HomeFragment();
                title = "Home";
        }

        if (bottomNavigation.getSelectedItemId() == item.getItemId()) {
            return false;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main_frame, fragment)
                .commit();
        toolbar.setTitle(title);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (bottomNavigation.getSelectedItemId()==R.id.bn_home) {
            super.onBackPressed();
        } else {
            showHome();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus(false);
    }

    private void setStatus(boolean online) {
        if (mAuth.getUid()!=null) {
            mDatabase.child("users").child(mAuth.getUid()).child("online").setValue(online);
            mDatabase.child("users").child(mAuth.getUid()).child("lastOnline").setValue(ServerValue.TIMESTAMP);
        }
    }
}