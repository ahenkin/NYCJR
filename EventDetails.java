package net.androidbootcamp.nycjrcalendarv1;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventDetails extends AppCompatActivity {

    int position;   // Position of the selected Event in the EventCalendar array

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Read event Position from activity extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = extras.getInt("eventIndex");
        }

        // Show eventDetails (final is needed b/c these are accessed from listeners)
        final TextView eventDetails = (TextView)findViewById(R.id.txtEventDetails);
        final TextView venueAddress = (TextView)findViewById(R.id.txtVenueAddress);
        final TextView venuetransportation = (TextView)findViewById(R.id.txtTransportation);
        final TextView venuePhone = (TextView)findViewById(R.id.txtPhone);
        final TextView venueWebsite = (TextView)findViewById(R.id.txtWebsite);

        eventDetails.setText(
                MainActivity.eventCalendar[position][1] + ", " +  // Venue, dates
                MainActivity.eventCalendar[position][3] + "\n" +
                MainActivity.eventCalendar[position][4] + "\n" +  // Description
                MainActivity.eventCalendar[position][9] + ", " +  // Times, Covers
                MainActivity.eventCalendar[position][10]);
        //eventDetails.setText(MainActivity.eventCalendar[position][4]+
        //        "\n"+MainActivity.eventCalendar[position][1] +
        //        " " + MainActivity.eventCalendar[position][3]);
        venueAddress.setText(MainActivity.eventCalendar[position][5]);
        venuetransportation.setText(MainActivity.eventCalendar[position][6]);
        venuePhone.setText(MainActivity.eventCalendar[position][7]);
        venueWebsite.setText(MainActivity.eventCalendar[position][8]);

        // Phone Call Listener
        venuePhone.setOnClickListener (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
            Intent phoneCall = new Intent(Intent.ACTION_DIAL);
            String phoneNo = "tel:" + venuePhone.getText().toString();
            phoneCall.setData(Uri.parse(phoneNo));

            startActivity(phoneCall);
            }
        });

        // Website Call Listener - do we ned it? Seems to work w/o explicit one

        // Command Buttons Listeners
        Button events = (Button) findViewById(R.id.btnEvents);
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.search = "events";
                startActivity(new Intent(EventDetails.this, EventList.class));
            }
        });

        Button venues = (Button)findViewById(R.id.btnVenues);
        venues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.search = "venues";
                startActivity(new Intent(EventDetails.this, EventList.class));
            }
        });

        Button dates = (Button)findViewById(R.id.btnDates);
        dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.search = "dates";
                startActivity(new Intent(EventDetails.this, EventList.class));
            }
        });
    }
}
