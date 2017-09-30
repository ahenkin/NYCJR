package net.androidbootcamp.nycjrcalendarv1;

import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class EventList extends ListActivity {

    // List of events for display in this view
    // Populated from EventCalendar in EventList activity
    // [0] - Index to EventCalendar array
    // [1] - Event tag (short description shown in this activity)
    // [2] - Venue name
    // [3] - Event date
    String[][] eventList = new String[4][1500];


    Button btnSearch;
    EditText txtSearch;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    // Set up date picker (only used if searching by date)
    Calendar calendar = Calendar.getInstance();     // Create an instance of the calendar
    final DateFormat fmtDate = DateFormat.getDateInstance();
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        // Set Search button and search text widgets
        btnSearch = (Button) findViewById(R.id.btnSearch);
        txtSearch = (EditText) findViewById(R.id.txtSearch);

        // Set Search button Listener
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Search for event of venue (need 3+ characters)
                if (MainActivity.search.equals("events")||
                    MainActivity.search.equals("venues")) {
                    String searchText = txtSearch.getText().toString();
                    Context context = EventList.this;

                    // Check if have enough search characters
                    if (searchText.length() > 0 && searchText.length() < 3) {
                        Toast.makeText(context, "Please enter at least 3 characters", Toast.LENGTH_LONG).show();
                    // Repopulate Event list with filtered contents
                    } else {
                        Toast.makeText(context, "Searching for " + searchText, Toast.LENGTH_LONG).show();
                        if (MainActivity.search.equals("events"))
                            populateEventList("Events", searchText);
                        else
                            populateEventList("Venues", searchText);
                    }
                }
                // Search for date
                else if (MainActivity.search.equals("dates")) {
                    // Show Date Picker to select date
                    new DatePickerDialog(EventList.this, d,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                    populateEventList("Dates", fmtDate.format(calendar.getTime()));
                }

                // Show filtered Event list
                listView.setAdapter(adapter);
            }
        });

        // Initially, populate Event List with all Events (unfiltered)
        // Set search button and text hint based on the search type
        // button pressed in MainActivity)
        switch (MainActivity.search) {
            case "events":
                btnSearch.setEnabled(true);
                txtSearch.setEnabled(true);
                txtSearch.setHint("3+ letters of event or artist");
                populateEventList("Events", "");
                break;
            case "venues":
                btnSearch.setEnabled(true);
                txtSearch.setEnabled(true);
                txtSearch.setHint("3+ letters of Venue name");
                populateEventList("Venues", "");
                break;
            case "dates":
                btnSearch.setEnabled(true);
                txtSearch.setEnabled(false);
                Date today = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d yyyy");
                String currentDate = formatter.format(today);
                txtSearch.setHint(currentDate);
                populateEventList("Dates", currentDate);
                break;
        };

        // Link event list data to ListView; show the list
        listView = (ListView) findViewById(android.R.id.list);
        adapter = new ArrayAdapter<String>(this, R.layout.custom_list_text,
                eventList[1]);
        listView.setAdapter(adapter);

    }

    // Process Event selection - click handler
    protected void onListItemClick(ListView l, View v, int position, long id){
        Intent details = new Intent(EventList.this, EventDetails.class);

        // Get index of selected event in the complete list of events
        int listIndex = Integer.parseInt(eventList[0][position]);
        // Pass index to Event Details activity
        details.putExtra("eventIndex", listIndex);
        startActivity(details);
    } // End of Listener

    // Populate, sort, and filter event list
    protected void populateEventList (String sortKey, String searchKey) {

        // Sort eventCalendar by the sort key

        // Define index to MainActivity.eventCalendar
        // Index in eventList is fieldIndex+1
        // ([0] is the index to MainActivity.eventCalendar row
        int fieldIndex;
        switch (sortKey) {
            case "Events":
                fieldIndex = 0;
                break;
            case "Venues":
                fieldIndex = 1;
                break;
            case "Dates":
                fieldIndex = 2;
                break;
            default:
                fieldIndex = 0;
                break;
        }

        // Add the first event (for Events and Venues only???)
        int i;
        for (i = 0; i < MainActivity.noOfShows; i++) {
            if (sortKey.equals("Events") || sortKey.equals("Venues")) {
                if (searchKey.length() == 0 ||
                        MainActivity.eventCalendar[i][fieldIndex].toUpperCase().contains(searchKey.toUpperCase())) {
                    populateEventListRow(0, i);
                    i++;
                    break;
                }
            }
            if (sortKey.equals("Dates")) {
                // Date in the search
                Date targetDate = new Date(searchKey); // "ddd, mmm dd, yyyy"
                int searchDate = targetDate.getDate();
                int eventDate =
                        new Integer(MainActivity.eventCalendar[i][fieldIndex].substring(4));        // data in the file

                if(searchKey.length() == 0 || searchDate == eventDate)
                {
                    populateEventListRow(0, i);
                    i++;
                    break;
                }
            }
        }

        // Check if any event matched search key
        // If not, show Toast and unfiltered list
        if (i == MainActivity.noOfShows) {
            Toast.makeText(EventList.this, "No matches found, showing complete list", Toast.LENGTH_LONG).show();
            // Add the first event
            populateEventListRow(0, 0);
            i = 1;
        }

        int j = 0;
        //Add the rest of events starting from the one found above
        for (; i<MainActivity.noOfShows; i++){
            if (sortKey.equals("Events") || sortKey.equals("Venues")) {
                if (searchKey.length() == 0 ||
                        MainActivity.eventCalendar[i][fieldIndex].toUpperCase().contains(searchKey.toUpperCase())) {
                    // If not duplicate event (same event, different date), insert into list
                    if (!MainActivity.eventCalendar[i][0].equals(MainActivity.eventCalendar[i - 1][0])) {
                        j++;
                        populateEventListRow (j, i);
                    }
                    // Else, accumulate dates in Dates field
                    else {
                        eventList[1][j] = eventList[0][j] + ", " + MainActivity.eventCalendar[i][2];
                    }
                }
            }
            else if (sortKey.equals("Dates")){
                // Date in the search
                Date targetDate = new Date(searchKey); // "ddd, mmm dd, yyyy"
                int searchDate = targetDate.getDate();
                int eventDate =
                        new Integer(MainActivity.eventCalendar[i][fieldIndex].substring(4));        // data in the file

                if(searchKey.length() == 0 || searchDate == eventDate)
                {
                    j++;
                    populateEventListRow (j, i);
                }
            }

        }

        //Wipe out the rest of the array. Ideally, need to cut it down to size.
        for (j=j+1; j<MainActivity.noOfShows; j++){
            eventList[0][j] = "";
            eventList[1][j] = "";
            eventList[2][j] = "";
            eventList[3][j] = "";
        }

    }

    protected void populateEventListRow (int targetRow, int sourceRow) {
        eventList[0][targetRow] = Integer.toString(sourceRow); // Store the 1st index to complete list
        eventList[1][targetRow] =
                MainActivity.eventCalendar[sourceRow][0] + "\n"
                        + MainActivity.eventCalendar[sourceRow][1] + ", "   // Venue
                        + MainActivity.eventCalendar[sourceRow][3]          // Date Range
                        + "\n"
                        + MainActivity.eventCalendar[sourceRow][9]          // Show Times
                        + MainActivity.eventCalendar[sourceRow][10];        // Cover charge

        eventList[2][targetRow] = MainActivity.eventCalendar[sourceRow][3];// Why am I storing this data?
        eventList[3][targetRow] = MainActivity.eventCalendar[sourceRow][4];// Why am I storing this data?
    }

} // End of EventList class
