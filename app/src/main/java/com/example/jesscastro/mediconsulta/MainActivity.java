package com.example.jesscastro.mediconsulta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.antonyt.infiniteviewpager.InfiniteViewPager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CaldroidListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;


public class MainActivity extends AppCompatActivity
    implements TimelineFragment.OnDayAnalyzed {

    private CaldroidCustomFragment caldroidFragment;
    private TimelineFragment timelineFragment;
    private Day[] allDays;

    public void onFullDayFound(Date date) {
        caldroidFragment.setBackgroundResourceForDate(R.color.Fuchsia, date);
        caldroidFragment.setTextColorForDate(R.color.Black, date);
        caldroidFragment.refreshView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            //Creating the CaldroidFragment and its arguments for the Calendar inside Timeline screen.
            caldroidFragment = new CaldroidCustomFragment();
            timelineFragment = new TimelineFragment();

            Bundle args = new Bundle();

            Calendar cal = Calendar.getInstance();

            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            caldroidFragment.setArguments(args);

            //Instances of each fragment (timeline and calendar)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.FrameLayout_Timeline, timelineFragment)
                    .add(R.id.FrameLayout_Calendar, caldroidFragment)
                    .commit();

            // Setup listener.
            final CaldroidListener listener = new CaldroidListener() {

                @Override
                public void onSelectDate(Date date, View view) {
                    Toast.makeText(getApplicationContext(), date.toString(), Toast.LENGTH_SHORT).show();

                    //TODO aquí va el codigo para buscar el día en la coleccion
                    //Buscar el día
                    //Enviar el día a timelineFragment para actualizar

                    /*
                    caldroidFragment.setBackgroundResourceForDate(R.color.Fuchsia, date);
                    caldroidFragment.setTextColorForDate(R.color.Black, date);
                    caldroidFragment.refreshView();
                    */
                }

                /*
                @Override
                public void onChangeMonth(int month, int year) {
                    String text = "month: " + month + " year: " + year;
                    Toast.makeText(getApplicationContext(), text,
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongClickDate(Date date, View view) {
                    Toast.makeText(getApplicationContext(),
                            "Long click " + format.format(date),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCaldroidViewCreated() {
                    if (caldroidFragment.getLeftArrowButton() != null) {
                        Toast.makeText(getApplicationContext(),
                                "Caldroid view is created", Toast.LENGTH_SHORT)
                                .show();
                    }
                }*/

            };

            // Setup Caldroid
            caldroidFragment.setCaldroidListener(listener);

            // Sends HTTP request to get the days
            FetchDayTask getJson = new FetchDayTask();
            getJson.execute();
        }
    }


    // -- Calendar fragment custom set-up.

    // Caldroid Custom Adapter
    public class CaldroidCustomAdapter extends CaldroidGridAdapter {

        public CaldroidCustomAdapter(Context context, int month, int year,
                                     HashMap<String, Object> caldroidData,
                                     HashMap<String, Object> extraData) {
            super(context, month, year, caldroidData, extraData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View cellView = convertView;

            // For reuse
            if (convertView == null) {
                cellView = inflater.inflate(R.layout.normal_date_cell, null);
            }

            int topPadding = cellView.getPaddingTop();
            int leftPadding = cellView.getPaddingLeft();
            int bottomPadding = cellView.getPaddingBottom();
            int rightPadding = cellView.getPaddingRight();

            TextView tv1 = (TextView) cellView.findViewById(R.id.calendar_tv);

            tv1.setTextColor(Color.BLACK);

            // Get dateTime of this cell
            DateTime dateTime = this.datetimeList.get(position);
            Resources resources = context.getResources();

            // Set color of the dates in previous / next month
            if (dateTime.getMonth() != month) {
                tv1.setTextColor(resources
                        .getColor(com.caldroid.R.color.caldroid_darker_gray));
            }

            boolean shouldResetDiabledView = false;
            boolean shouldResetSelectedView = false;

            // Customize for disabled dates and date outside min/max dates
            if ((minDateTime != null && dateTime.lt(minDateTime))
                    || (maxDateTime != null && dateTime.gt(maxDateTime))
                    || (disableDates != null && disableDates.indexOf(dateTime) != -1)) {

                tv1.setTextColor(CaldroidFragment.disabledTextColor);
                if (CaldroidFragment.disabledBackgroundDrawable == -1) {
                    cellView.setBackgroundResource(com.caldroid.R.drawable.disable_cell);
                } else {
                    cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
                }

                if (dateTime.equals(getToday())) {
                    cellView.setBackgroundResource(com.caldroid.R.drawable.red_border_gray_bg);
                }

            } else {
                shouldResetDiabledView = true;
            }

            // Customize for selected dates
            if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
                cellView.setBackgroundColor(resources
                        .getColor(com.caldroid.R.color.caldroid_sky_blue));

                tv1.setTextColor(Color.BLACK);

            } else {
                shouldResetSelectedView = true;
            }

            if (shouldResetDiabledView && shouldResetSelectedView) {
                // Customize for today
                if (dateTime.equals(getToday())) {
                    cellView.setBackgroundResource(com.caldroid.R.drawable.red_border);
                } else {
                    cellView.setBackgroundResource(com.caldroid.R.drawable.cell_bg);
                }
            }

            tv1.setText("" + dateTime.getDay());

            // Somehow after setBackgroundResource, the padding collapse.
            // This is to recover the padding
            cellView.setPadding(leftPadding, topPadding, rightPadding,
                    bottomPadding);

            // Set custom color if required
            setCustomResources(dateTime, cellView, tv1);

            InfiniteViewPager pager = (InfiniteViewPager) findViewById(R.id.months_infinite_pager);
            int height = pager.getHeight() / 6;

            cellView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, height));

            return cellView;
        }
    }

    @SuppressLint("ValidFragment")
    public class CaldroidCustomFragment extends CaldroidFragment {

        @Override
        public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
            // TODO Auto-generated method stub
            return new CaldroidCustomAdapter(getActivity(), month, year,
                    getCaldroidData(), extraData);
        }

    }

    public class FetchDayTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchDayTask.class.getSimpleName();
        String dayJsonStr;

        @Override
        protected String[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            dayJsonStr = null;

            try {
                // Construct the URL to ask for the days of a single doctor
                // http://<ip del servidor>:1305/api/days/22824486

                String dayRequest_url =
                        "http://192.168.0.102:1305/api/days/22824486";

                URL url = new URL(dayRequest_url);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                dayJsonStr = buffer.toString();

                //dayJsonStr = "{ \"date\": \"2016-05-22T00:00:00.000Z\",\"dayAppointments\": [{\"start\": \"2016-05-22T07:00:00.000Z\",\"end\": \"2016-05-22T09:00:00.000Z\",\"eventType\": \"Consulta\",\"patientID\": \"5642196\",\"patientName\": \"Ninfa Araque\",\"description\": \"Best mom ever.\"}],\"full\": true,\"medicID\": \"22824486\"}";
                Log.v(LOG_TAG, "Day string: " + dayJsonStr);

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] results) {

            if (dayJsonStr != null) {

                Gson gson = new GsonBuilder().create();
                allDays = gson.fromJson(dayJsonStr, Day[].class);

                //Sets the timezone to avoid wrong date coloring in the calendar
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

                for(Day dayResult : allDays){

                    //Uses Joda to parse the ISO date format
                    org.joda.time.DateTime fullDate = org.joda.time.DateTime.parse(dayResult.getDate());

                    if(dayResult.getFull().equals("true")){

                        //Sets the date to "full" color in the calendar
                        onFullDayFound(fullDate.toDate());
                    }

                    Log.v(LOG_TAG, "Day string: " + fullDate.toString());
                    Log.v(LOG_TAG, "Day string: " + fullDate.toDate().toString());
                }
            }
        }
    }
}


// Classes used to parse the json string obtained
class Day {
    private String date;
    private dayAppointment[] dayAppointments;
    private String full;
    private String medicID;

    public String getDate() {
        return date;
    }

    public String getFull() {
        return full;
    }

    public String getMedicID() {
        return medicID;
    }

    public dayAppointment[] getDayAppointments() {
        return dayAppointments;
    }
}

class dayAppointment {
    private String start;
    private String end;

    public String getEnd() {
        return end;
    }

    public String getStart() {
        return start;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDescription() {
        return description;
    }

    private String eventType;
    private String patientID;
    private String patientName;
    private String description;
}