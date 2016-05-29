package com.example.jesscastro.mediconsulta;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jesús Castro on 22/5/2016.
 */
public class TimelineFragment extends Fragment {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    OnDayAnalyzed dayListener;

    public TimelineFragment() {
    }

    /*
    * Setting up the interface to use for fragment to fragment communication
    */
    public interface OnDayAnalyzed {
        public void onFullDayFound(Date date);
    }

    /*
    * Uses a Day object to update the timeline
    * */
    public void updateTimeline(Day dayResult){

        //Clear collections in order to add new data
        listDataHeader.clear();
        listDataChild.clear();

        int count = 0;
        for(dayAppointment da :dayResult.getDayAppointments()) {

            //Assign header content
            org.joda.time.DateTime startDate = org.joda.time.DateTime.parse(da.getStart());
            org.joda.time.DateTime endDate = org.joda.time.DateTime.parse(da.getEnd());
            DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm");
            listDataHeader.add(fmt.print(startDate) + " - " + fmt.print(endDate));

            //Assign expandable content
            List<String> childData = new ArrayList<String>();
            childData.add("Paciente: " + da.getPatientName());
            childData.add("Motivo: " + da.getDescription());

            listDataChild.put(listDataHeader.get(count), childData);
        }

        // Notify to see changes in UI
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            dayListener = (OnDayAnalyzed) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement Listener");
        }
    }

    /*
        * Preparing the list data, does nothing
        */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        //-- El header de las listas son las horas
        listDataHeader.add("01:00 - 02:00");
        listDataHeader.add("02:00 - 03:00");
        listDataHeader.add("03:00 - 04:00");
        listDataHeader.add("04:00 - 05:00");
        listDataHeader.add("05:00 - 06:00");

        //Para efectos del ejemplo se tiene información estática
        List<String> childData = new ArrayList<String>();
        childData.add("Número de teléfono: 0424-9090336"); //subString(20)
        childData.add("Ubicación: UCAB Guayana");   //subString(11)

        listDataChild.put(listDataHeader.get(0), childData );
        listDataChild.put(listDataHeader.get(1), childData );
        listDataChild.put(listDataHeader.get(2), childData );
        listDataChild.put(listDataHeader.get(3), childData );
        listDataChild.put(listDataHeader.get(4), childData );
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        // get the listview
        expListView = (ExpandableListView) rootView.findViewById(R.id.list_dayAppointments);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        //setClickListeners(expListView);

        return rootView;
    }

    /*
     * Setting up the listeners for the expandable list items.
     */
    private void setClickListeners(ExpandableListView expListView){

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getActivity(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getActivity(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getActivity(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();

                //Intent to open details of the patient
                Intent intent = new Intent(parent.getContext(), PatientDetailsActivity.class);

                //Adding patient id to the intent in order to fetch the data
                intent.putExtra("patient_id",groupPosition);
                startActivity(intent);
                return false;
            }
        });
    }

    /*
    * Implementation of the custom expandable list adapter.
    */
    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;

        // Header of expandable elements
        private List<String> _listDataHeader;

        // Child data for each element, in format of header title, child title
        private HashMap<String, List<String>> _listDataChild;
        private StringBuilder time;


        public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<String>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
            time = new StringBuilder();
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);

            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = infalInflater.inflate(R.layout.fragment_timeline_explist_item, null);

                TextView txtListChild = (TextView) convertView
                        .findViewById(R.id.childitem_text);

                txtListChild.setText(childText);

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.fragment_timeline_explist_header, null);
            }

            TextView ListHeader = (TextView) convertView
                    .findViewById(R.id.groupheader_text);

            String headerTitle = (String) getGroup(groupPosition);

            ListHeader.setTypeface(null, Typeface.BOLD);
            ListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}