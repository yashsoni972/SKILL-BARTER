package com.yashsoni.skillbarter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.yashsoni.skillbarter.databinding.ActivityMainBinding;
import com.yashsoni.skillbarter.ui.chat.ChatListFragment;
import com.yashsoni.skillbarter.ui.discover.DiscoverFragment;
import com.yashsoni.skillbarter.ui.home.HomeFragment;
import com.yashsoni.skillbarter.ui.profile.ProfileFragment;
import com.yashsoni.skillbarter.ui.requests.RequestsFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadFragment(new HomeFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                fragment = new DiscoverFragment();
            } else if (itemId == R.id.nav_requests) {
                fragment = new RequestsFragment();
            } else if (itemId == R.id.nav_chat) {
                fragment = new ChatListFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    public void selectBottomTab(int itemId) {
        binding.bottomNavigation.setSelectedItemId(itemId);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
