package com.example.recipesharing;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

public class Register extends AppCompatActivity {
    TextInputEditText editTextUser;
    TextInputEditText editTextEmail;
    TextInputEditText editTextPassword;
    Button buttonReg;
    // will be used for authenticating with Firebase
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextUser = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, username;
                username = editTextUser.getText().toString();
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(username))
                {
                    Toast.makeText(Register.this, "Enter username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password))
                {
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                // the following is a standard code that is used for authenticating with Firebase
                // using password-based accounts on Android provided by the Firebase website:
                // https://firebase.google.com/docs/auth/android/password-auth?hl=he#java_3
                // I changed it up so it will use toast instead of log

                // I use runOnUiThread because addOnCompleteListener runs asynchronously on a background thread.
                // This means that the code inside the onComplete method, including the toast messages,
                // will execute on a thread other than the main UI thread.
                // note: using toast messages there might sometimes work because of how Firebase
                // manages the thread context for different parts of its authentication and database
                // operations, the SDK might already ensure that some of them will appear on the main UI thread
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task)
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful())
                                        {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null)
                                            {
                                                String userId = user.getUid();
                                                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                                                DatabaseReference dbRef = mDatabase.getReference();
                                                DatabaseReference usersRef = dbRef.child("users"); // Reference to "users" node
                                                DatabaseReference userRef = usersRef.child(userId); // Reference to the user's data
                                                // Update the user's name in the database
                                                userRef.child("name").setValue(username); // Set the user's name
                                                // Display a toast on the main UI thread
                                                runOnUiThread(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        Toast.makeText(Register.this, "the operation was successful", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            else
                                            {
                                                runOnUiThread(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        Toast.makeText(Register.this, "User is null", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                        else
                                        {
                                            Exception exception = task.getException();
                                            if (exception instanceof FirebaseAuthWeakPasswordException)
                                            {
                                                // This exception is thrown when the password is too weak
                                                runOnUiThread(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        Toast.makeText(Register.this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            else if (exception instanceof FirebaseAuthInvalidCredentialsException)
                                            {
                                                // This exception is thrown when the email address is invalid
                                                runOnUiThread(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        Toast.makeText(Register.this, "Invalid email address format..", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            else if (exception instanceof FirebaseAuthUserCollisionException)
                                            {
                                                // This exception is thrown when the email address is already in use
                                                runOnUiThread(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        Toast.makeText(Register.this, "Email address is already in use.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            else
                                            {
                                                runOnUiThread(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        Toast.makeText(Register.this, exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                        );
                // using intent here in order to ensure that asynchronous operations are completed before proceeding with other code
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
                finish();
            }
        });
    }
}