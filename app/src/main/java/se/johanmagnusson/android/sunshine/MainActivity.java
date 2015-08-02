package se.johanmagnusson.android.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private final String FORECAST_FRAGMENT_TAG = "Forecast_Fragment";
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        location = Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_container, new ForecastFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check if location has been changed

        if(!location.equals(Utility.getPreferredLocation(this))) {
            ForecastFragment forecastFragment = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECAST_FRAGMENT_TAG);

            if(forecastFragment != null){
                forecastFragment.OnLocationShanged();
                location = Utility.getPreferredLocation(this);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        location = Utility.getPreferredLocation(this);

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
