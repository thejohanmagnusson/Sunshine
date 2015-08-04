package se.johanmagnusson.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import se.johanmagnusson.android.sunshine.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = ForecastDetailFragment.class.getSimpleName();
    private final String SHARE_TAG = "#SunshineApp";

    private ShareActionProvider shareActionProvider;
    private String forecast;

    private ImageView iconView;
    private TextView dayView;
    private TextView dateView;
    private TextView descriptionView;
    private TextView highView;
    private TextView lowView;
    private TextView humidityView;
    private TextView windView;
    private TextView pressureView;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // These indices are tied to DETAILS_COLUMNS.  If DETAILS_COLUMNS changes, these must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_PRESSURE = 6;
    static final int COL_WEATHER_WIND_SPEED = 7;
    static final int COL_WEATHER_DEGREES = 8;
    static final int COL_WEATHER_WEATHER_ID = 9;

    public ForecastDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        iconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        dayView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        dateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        descriptionView = (TextView) rootView.findViewById(R.id.detail_description_textview);
        highView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        lowView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        humidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        windView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        pressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        //get the action provider from the menu
        MenuItem menuItem = menu.findItem(R.id.action_share);

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(forecast != null)
            shareActionProvider.setShareIntent(createShareForecastIntent());
    }

    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecast + SHARE_TAG);

        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Intent intent = getActivity().getIntent();

        if(intent == null)
            return null;

        CursorLoader cursor = new CursorLoader(
                getActivity(),
                intent.getData(),
                DETAIL_COLUMNS,
                null,
                null,
                null);

        return cursor;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        //check if any data
        if(!data.moveToFirst())
            return;

        int weatherId = data.getInt(COL_WEATHER_WEATHER_ID);
        iconView.setImageResource(R.drawable.ic_launcher);

        long date = data.getLong(COL_WEATHER_DATE);
        String day = Utility.getDayName(getActivity(), date);
        dayView.setText(day);

        String formatedDate = Utility.getFormattedMonthDay(getActivity(), date);
        dateView.setText(formatedDate);

        String description = data.getString(COL_WEATHER_DESC);
        descriptionView.setText(description);

        boolean isMetric = Utility.isMetric(getActivity());
        double high = data.getDouble(COL_WEATHER_MAX_TEMP);
        highView.setText(Utility.formatTemperature(getActivity(), high, isMetric));

        double low = data.getDouble(COL_WEATHER_MIN_TEMP);
        lowView.setText(Utility.formatTemperature(getActivity(), low, isMetric));

        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        humidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

        float windSpeed = data.getFloat(COL_WEATHER_WIND_SPEED);
        float windDegrees = data.getFloat(COL_WEATHER_DEGREES);
        windView.setText(Utility.getFormattedWind(getActivity(), windSpeed, windDegrees));

        float pressure = data.getFloat(COL_WEATHER_PRESSURE);
        pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

        forecast = String.format("%s - %s - %s/%s", date, description, high, low);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (shareActionProvider != null)
            shareActionProvider.setShareIntent(createShareForecastIntent());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
