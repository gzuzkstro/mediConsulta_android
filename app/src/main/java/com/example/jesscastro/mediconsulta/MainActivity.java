package com.example.jesscastro.mediconsulta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.antonyt.infiniteviewpager.InfiniteViewPager;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import hirondelle.date4j.DateTime;


public class MainActivity extends AppCompatActivity {

    private CaldroidCustomFragment caldroidFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            //Creating the CaldroidFragment and its arguments for the Calendar inside Timeline screen.
            caldroidFragment = new CaldroidCustomFragment();
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            caldroidFragment.setArguments(args);

            //Instances of each fragment (timeline and calendar)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.FrameLayout_Timeline, new TimelineFragment())
                    .add(R.id.FrameLayout_Calendar, caldroidFragment)
                    .commit();

            // Setup listener.
            final CaldroidListener listener = new CaldroidListener() {

                @Override
                public void onSelectDate(Date date, View view) {
                    Toast.makeText(getApplicationContext(), date.toString(), Toast.LENGTH_SHORT).show();

                    //**Works for changing colors!
                    caldroidFragment.setBackgroundResourceForDate(R.color.Fuchsia, date);
                    caldroidFragment.setTextColorForDate(R.color.Black, date);
                    caldroidFragment.refreshView();
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
}


//
class Appointment {
    String status;

    public Appointment() {
        status = "Null";
    }
}