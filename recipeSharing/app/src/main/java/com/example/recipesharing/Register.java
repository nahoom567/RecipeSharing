package com.example.recipesharing;

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
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(Register.this, "Account created.",
                                                    Toast.LENGTH_SHORT).show();
                                            // saving username during registration
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null)
                                            {
                                                String userId = user.getUid();
                                                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                                                DatabaseReference dbRef = mDatabase.getReference();
                                                DatabaseReference usersRef = dbRef.child("users"); // Reference to "users" node
                                                DatabaseReference userRef = usersRef.child(userId); // Reference to the user's data
                                                userRef.child("name").setValue(username); // Set the user's name
                                                Toast.makeText(Register.this, "Added user.",
                                                        Toast.LENGTH_LONG).show();
                                                // checking if users has already been created
                                                /*
                                                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (!snapshot.exists())
                                                        {
                                                            // The "users" node does not exist
                                                            usersRef.setValue("users");
                                                            String userId = user.getUid();
                                                            usersRef.child(userId).child("name").setValue(username);
                                                            Toast.makeText(Register.this, "Added user.",
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(Register.this, "Authentication failed during users process",
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                                 */
                                                // adding "users" node (doesn't matter if it already exists)
                                            }
                                            else
                                            {
                                                // If sign in fails, display a message to the user.
                                                Toast.makeText(Register.this, "Authentication failed at the start",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        else
                                        {
                                            Exception exception = task.getException();
                                            if (exception instanceof FirebaseAuthWeakPasswordException)
                                            {
                                                // This exception is thrown when the password is too weak
                                                Toast.makeText(Register.this, "Password must be at least 6 characters.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            else if (exception instanceof FirebaseAuthInvalidCredentialsException)
                                            {
                                                // This exception is thrown when the email address is invalid
                                                Toast.makeText(Register.this, "Invalid email address format.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            else if (exception instanceof FirebaseAuthUserCollisionException)
                                            {
                                                // This exception is thrown when the email address is already in use
                                                Toast.makeText(Register.this, "Email address is already in use.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(Register.this, exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
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