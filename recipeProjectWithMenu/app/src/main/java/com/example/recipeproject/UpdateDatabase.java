package com.example.recipeproject;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateDatabase extends JobService {

    private DatabaseReference msgRef;
    private ValueEventListener listener;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("started job", "start here");
        String messageToUser = params.getExtras().getString("messageToAdd");
        Log.d("check message", messageToUser);
        String idOfUser = params.getExtras().getString("idUser");

        // reference to the user's messages in the Firebase database
        msgRef = FirebaseDatabase.getInstance().getReference().child("messages").child(idOfUser);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean messageExists = false;
                for (DataSnapshot msgSnapshot : snapshot.getChildren()) {
                    String existingMessage = msgSnapshot.getValue(String.class);
                    if (messageToUser.equals(existingMessage)) {
                        messageExists = true;
                        break;
                    }
                }

                if (!messageExists) {
                    // No duplicate found, add the new message
                    DatabaseReference newMsgRef = msgRef.push();
                    newMsgRef.setValue(messageToUser)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "Message saved successfully");
                                } else {
                                    Log.d("Firebase", "Failed to save message");
                                }
                                // Job finished, no need to reschedule
                                jobFinished(params, false);
                            });
                } else {
                    Log.d("Firebase", "Duplicate message, not adding to database");
                    // Job finished, no need to reschedule
                    jobFinished(params, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase", "Failed to read messages: " + error.getMessage());
                // Job finished, no need to reschedule
                jobFinished(params, false);
            }
        };

        msgRef.addListenerForSingleValueEvent(listener);

        // Return true as the job is still doing work in the background
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // removing the listener to prevent it from remaining active
        if (msgRef != null && listener != null) {
            msgRef.removeEventListener(listener);
        }
        // Return true if you want the job to be rescheduled, false otherwise
        return true;
    }
}
