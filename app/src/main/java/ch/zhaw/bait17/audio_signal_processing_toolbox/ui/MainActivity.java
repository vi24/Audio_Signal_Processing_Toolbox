package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.Bitcrusher;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.SoftClipper;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.Filter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.FilterUtil;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.AudioView;

/**
 * @author georgrem, stockan1
 */

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MediaListFragment.OnTrackSelectedListener,
        AudioEffectFragment.OnItemSelectedListener {

    private static final String TAG_AUDIO_PLAYER_FRAGMENT = "AUDIO_PLAYER";
    private static final String TAG_FILTER_FRAGMENT = "FILTER";
    private static final String TAG_MEDIA_LIST_FRAGMENT = "MEDIA_LIST";
    private static final String TAG_VISUALISATION_CONFIGURATION_FRAGMENT = "VISUALISATION_CONFIGURATION";
    private static final String TAG_VISUALISATION_FRAGMENT = "VISUALISATION";

    private AudioPlayerFragment audioPlayerFragment;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ArrayList<AudioEffect> audioEffects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initAudioEffects();
        initFragments();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
        initAudioPlayerFragment();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // Problem: AudioPlayerFragment disappears when back button is pushed.
            // Needs a more elegant solution.
            /*
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
            */
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return selectMenuItem(item);
    }

    /**
     * Sends the {@code Track} id to the {@code AudioPlayerFragment}
     */
    @Override
    public void onTrackSelected(int trackPos) {
        if (audioPlayerFragment != null) {
            audioPlayerFragment.setTrack(trackPos);
        }
    }

    /**
     * Sets the title in the AppBar.
     *
     * @param title the title to be set
     */
    @Override
    public void setTitle(CharSequence title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    // When the fragment event fires
    @Override
    public void onFilterItemSelected(List<AudioEffect> audioEffects) {
        if (audioPlayerFragment != null) {
            audioPlayerFragment.setAudioEffects(audioEffects);
        }
    }

    // Layout: onClick event
    public void onClickPlayPauseTrack(View view) {
        if (audioPlayerFragment != null) {
            audioPlayerFragment.playPauseTrack();
        }
    }

    // Layout: onClick event
    public void onClickPreviousTrack(View view) {
        if (audioPlayerFragment != null) {
            audioPlayerFragment.playPreviousTrack();
        }
    }

    // Layout: onClick event
    public void onClickNextTrack(View view) {
        if (audioPlayerFragment != null) {
            audioPlayerFragment.playNextTrack();
        }
    }

    /**
     * Loads all fragments into the {@code FrameLayout} placeholders.
     */
    private void initFragments() {
        Fragment visualisationConfigurationFragment = new ViewFragment();
        audioPlayerFragment = new AudioPlayerFragment();
        List<AudioView> activeViews = ((ViewFragment) visualisationConfigurationFragment).getActiveViews();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, AudioEffectFragment.newInstance(audioEffects),
                TAG_FILTER_FRAGMENT);
        ft.replace(R.id.content_frame, visualisationConfigurationFragment,
                TAG_VISUALISATION_CONFIGURATION_FRAGMENT);
        ft.replace(R.id.content_frame, VisualisationFragment.newInstance(activeViews),
                TAG_VISUALISATION_FRAGMENT);
        ft.replace(R.id.content_frame, new MediaListFragment(), TAG_MEDIA_LIST_FRAGMENT);
        ft.replace(R.id.audio_player_fragment, audioPlayerFragment, TAG_AUDIO_PLAYER_FRAGMENT);
        ft.addToBackStack(null);
        ft.commit();
    }

    private Fragment getFragmentByTag(String tagFragmentName) {
        return getFragmentManager().findFragmentByTag(tagFragmentName);
    }

    /*
        Handle navigation view item clicks here.
        Swap fragments in the main content view.
    */
    private boolean selectMenuItem(@NonNull MenuItem item) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Store containerViewId and associated Fragment
        Fragment fragment = null;
        String title = "";
        String tagFragmentName= "";

        switch (item.getItemId()) {
            case R.id.nav_media_list:
                fragment = getFragmentByTag(TAG_MEDIA_LIST_FRAGMENT);
                title = "My Music";
                tagFragmentName = TAG_MEDIA_LIST_FRAGMENT;
                break;
            case R.id.nav_visualisation:
                Fragment vcf = getFragmentByTag(TAG_VISUALISATION_CONFIGURATION_FRAGMENT);
                List<AudioView> activeViews = ((ViewFragment) vcf).getActiveViews();
                fragment = getFragmentByTag(TAG_VISUALISATION_FRAGMENT);
                ((VisualisationFragment) fragment).setViews(activeViews);
                title = "Visualisation";
                tagFragmentName = TAG_VISUALISATION_FRAGMENT;
                break;
            case R.id.nav_view:
                fragment = getFragmentByTag(TAG_VISUALISATION_CONFIGURATION_FRAGMENT);
                title = "View";
                tagFragmentName = TAG_VISUALISATION_CONFIGURATION_FRAGMENT;
                break;
            case R.id.nav_filter:
                fragment = getFragmentByTag(TAG_FILTER_FRAGMENT);
                title = "Filter";
                tagFragmentName = TAG_FILTER_FRAGMENT;
                break;
            case R.id.nav_about:
                title = "About the app";
                break;
            default:
                break;
        }

        if (fragment != null) {
            ft.replace(R.id.content_frame, fragment, tagFragmentName);
            ft.addToBackStack(null);
        }
        ft.commit();

        setTitle(title);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Finds all filter spec files in the raw resources folder and returns a list of {@code Filter}.
     *
     * @return
     */
    private List<Filter> getAllFilters() {
        List<Filter> filters = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            int rawId = getResources().getIdentifier(field.getName(), "raw",
                    ApplicationContext.getAppContext().getPackageName());
            if (rawId != 0) {
                TypedValue value = new TypedValue();
                getResources().getValue(rawId, value, true);
                String[] s = value.string.toString().split("/");
                String filename = s[s.length - 1];
                if (filename.startsWith("b_fir")) {
                    filters.add(FilterUtil.getFilter(getResources().openRawResource(rawId)));
                }
            }
        }
        return filters;
    }

    private void initAudioEffects() {
        audioEffects = new ArrayList<>();
        audioEffects.add(null);          // No filter
        audioEffects.addAll(getAllFilters());
        audioEffects.add(new Bitcrusher(Constants.BITCRUSHER_DEFAULT_NORM_FREQUENCY,
                Constants.BITCRUSHER_DEFAULT_BITS));
        audioEffects.add(new SoftClipper(Constants.SOFT_CLIPPER_DEFAULT_CLIPPING_FACTOR));
    }

    private void initAudioPlayerFragment() {
        Fragment mlf = getFragmentByTag(TAG_MEDIA_LIST_FRAGMENT);
        List<Track> tracks = ((MediaListFragment) mlf).getTracks();
        RecyclerView recyclerView = ((MediaListFragment) mlf).getRecyclerView();
        if (audioPlayerFragment != null) {
            audioPlayerFragment.setRecyclerView(recyclerView);
            audioPlayerFragment.setTracks(tracks);
        }
    }

}
