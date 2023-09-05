package com.example.recipesharing;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class updateDatabase extends JobService
{
    @Override
    public boolean onStartJob(JobParameters params)
    {
        String messageToUser = params.getExtras().getString("messageToUser");
        DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference().child("messages");
        DatabaseReference dbRef = msgRef.push();
        dbRef.child(messageToUser);
        // the job does not need to be rescheduled
        jobFinished(params, false); // Set to true if you want to reschedule
        return true; // Return true if you're doing work on a separate thread
    }

    // using onStopJob in case the job was stopped prematurely due to external conditions like network loss
    @Override
    public boolean onStopJob(JobParameters params)
    {
        // Return true if you want the job to be rescheduled, false otherwise
        return true;
    }
}
