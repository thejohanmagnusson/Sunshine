package se.johanmagnusson.android.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback{

    private final String DETAIL_FRAGMENT_TAG = "DFTAG";
    private boolean twoPane;

    private String mlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mlocation = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);

        //check if two pane
        if(findViewById(R.id.weather_detail_container) != null) {
            twoPane = true;
            //add detail fragment if not added already
            if (savedInstanceState == null)
                getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, new ForecastDetailFragment(), DETAIL_FRAGMENT_TAG).commit();
        }
        else {
            twoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        //don´t use today layout on list item in two pane mode
        ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        forecastFragment.setUseTodayLayout(!twoPane);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check if location has been changed
        String location = Utility.getPreferredLocation(this);

        if(mlocation != null && !mlocation.equals(location)) {
            ForecastFragment forecastFragment = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);

            if(forecastFragment != null){
                forecastFragment.OnLocationShanged();
            }

            //dynamic fragment so has no view id so find by tag instead.
            ForecastDetailFragment detailFragment = (ForecastDetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if(detailFragment != null){
                detailFragment.onLocationChanged(location);
            }

            mlocation = location;
        }
    }

    //callback from forecast fragment
    @Override
    public void onItemSelected(Uri contentUri) {

        //if two pane replace fragment in fragment container
        if(twoPane){
            Bundle args = new Bundle();
            args.putParcelable(ForecastDetailFragment.DETAIL_URI, contentUri);

            ForecastDetailFragment detailFragment = new ForecastDetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, detailFragment, DETAIL_FRAGMENT_TAG).commit();
        }
        //if on pane start new activity
        else{
            Intent intent = new Intent(this, DetailActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mlocation = Utility.getPreferredLocation(this);

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
            Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        else if (id == R.id.action_location) {
            openPreferedLocationMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferedLocationMap(){
        String location = Utility.getPreferredLocation(this);

        Intent locationIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("geo:0,0?q=" + location);

        locationIntent.setData(uri);

        if(locationIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(locationIntent);
        }
    }

}
