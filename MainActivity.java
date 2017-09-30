package net.androidbootcamp.nycjrcalendarv1;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Scanner;


public class MainActivity extends AppCompatActivity {

    public static String[][] eventCalendar = new String[1500][12];
    // Contains complete denormalized list of events sorted by Event tag
    // [0] - Event tag (short description)
    // [1] - Venue
    // [2] - First Date (one record for multiple day event)
    // [3] - Last Date Range (date in mm/yy/yyyy format)
        // [2] - Date (one record for each date of the event)
        // [3] - Date Range (Free text, e.g. "Tue Feb 2, Fri Feb 5 - Sun Feb 7")
    // [4] - Complete description including Venue, show times, cover charge)
    // [5] - Address
    // [6] - Transportation
    // [7] - Phone
    // [8] - Web site
    // Added in v2.2.a
    // [9] - Show Times
    // [10] - Cover Charge
    // [11] - Max Cover charge
    public static Integer noOfShows;    // number of records in the array

    static String search;        // values are "event", "venue", "date"
    Button events;
    Button venues;
    Button dates;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define buttons and disable them until the calendar file is completely read
        events = (Button) findViewById(R.id.btnEvents);
        venues = (Button) findViewById(R.id.btnVenues);
        dates = (Button)findViewById(R.id.btnDates);

        events.setEnabled(false);
        venues.setEnabled(false);
        dates.setEnabled(false);

        // Set up Command Buttons Listeners
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search = "events";
                startActivity(new Intent(MainActivity.this, EventList.class));
            }
        });

        venues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search = "venues";
                startActivity(new Intent(MainActivity.this, EventList.class));
            }
        });
        dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search = "dates";
                startActivity(new Intent(MainActivity.this, EventList.class));
            }
        });


        // Start asynch Calendar download
        new CalendarRead().execute();

    }

    class CalendarRead extends AsyncTask<Void, Void, Void> {
    // Note: Returns nothing.
    // Can return e.g. # of records read, and/or error code, e.g. -1, -2, -3 for exceptions

    Context context = MainActivity.this;

        @Override
        // Read Calendar file from the server and populate string array
        protected Void doInBackground(Void... params) {
            // Retrieve pipe-delimited file
            String URL =
                //"http://nycjazzrecord.com/Calendar/Calendar.txt";
                "http://nycjazzrecord.com/Calendar/Test_Files/Calendar201708.txt";
            int i;

            try {
                java.net.URL url = new java.net.URL(URL);
                Scanner input = new Scanner(url.openStream());

                for (i = 0; input.hasNextLine() && i<eventCalendar.length; i++) {
                    String temp = input.nextLine();

                    // To guarantee line break. "\n" by itself did not work
                    temp = temp.replace("\\n", System.getProperty("line.separator"));
                    // eventCalendar[i] = temp.split("\\|",9);
                    // split() may create variable (<9) 2nd dimension. Need to guarantee 9.

                    int iFrom = 0;
                    for (int j=0; j<12; j++) {
                        int iTo = temp.indexOf('|', iFrom);
                        if (iTo != -1) {
                            eventCalendar[i][j] = temp.substring(iFrom, iTo);
                            iFrom = iTo + 1;
                        } else {
                            eventCalendar[i][j] = temp.substring(iFrom, temp.length());
                            break;
                        }
                    }
                }
            noOfShows = i;
            }

            catch (java.net.MalformedURLException ex) {
                Toast.makeText(context, "URL " + URL + " not found", Toast.LENGTH_LONG).show();
            }
            catch (java.io.IOException ex) {
                Toast.makeText(context, "Data access error reading Calendar data", Toast.LENGTH_LONG).show();
            }
            catch(java.lang.RuntimeException ex) {
                Toast.makeText(context, "Runtime error reading Calendar data", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Insert code to enable/unhide buttons
            events.setEnabled(true);
            venues.setEnabled(true);
            dates.setEnabled(true);
            Toast.makeText(context, Integer.toString(noOfShows) + " events downloaded", Toast.LENGTH_LONG).show();

        }
    }
}



