package com.example.meetapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private final String DOES_NOT_EXIST = "";
    private static long back_pressed;
    private CharSequence userPassword = "";
    private CharSequence userEmail = "";
    private TextView feedbackToUser;
    private EditText userPasswordInput;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        //TODO remove it if you don't want to log in every time you reset the app
        FirebaseAuth.getInstance().signOut();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToGroupDisplayScreen(currentUser.getUid());
        } else {
            setContentView(R.layout.activity_login);
            handleUserInputOfEmailAndPassword();
            handleNewSignIn();
        }
    }


    void handleUserInputOfEmailAndPassword(){
        final Button loginBtn = findViewById(R.id.loginBtn);
        feedbackToUser = findViewById(R.id.InvalidEmailOrPassword);
        feedbackToUser.setText("");
        userPasswordInput = findViewById(R.id.enterPasswordInput);
        handleEmailInput(loginBtn);
        handlePasswordInput(loginBtn);
    }


    private void handleEmailInput(final Button loginBtn) {
        EditText userEmailInput = findViewById(R.id.enterEmailInput);
        userEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    userEmail = "";
                    loginBtn.setBackgroundResource(R.drawable.disabled_button_background);
                }
                else {
                    handleChangeInEmailInput(s, loginBtn);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleChangeInEmailInput(CharSequence s, Button loginBtn) {
        userEmail = s.toString();
        if(!userPassword.equals("")){
            loginBtn.setBackgroundResource(R.drawable.green_round_background);
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginClick();
            }
        });
    }


    private void handlePasswordInput(final Button loginBtn) {
        userPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    loginBtn.setBackgroundResource(R.drawable.disabled_button_background);
                    userPassword = "";
                }else {
                    handleChangeInPasswordInput(s, loginBtn);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleChangeInPasswordInput(CharSequence s, Button loginBtn) {
        feedbackToUser.setText("");
        userPassword = s.toString();
        if(!userEmail.equals("")){
            loginBtn.setBackgroundResource(R.drawable.green_round_background);
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginClick();
            }
        });
    }


    void handleLoginClick(){
        if(!userEmail.equals("") && !userPassword.equals("")){
            logIn(userEmail.toString(), userPassword.toString());
        }
    }

    public void goToGroupDisplayScreen(String currentUserId) {
        final Intent goToGroupsScreen = new Intent(getApplicationContext(), GroupsDisplayActivity.class);
        goToGroupsScreen.putExtra("userId", currentUserId);
        usersRef = db.collection("users").document(currentUserId);
        usersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    goToGroupsScreen.putExtra("userName", (String) document.get("name"));
                    startActivityForResult(goToGroupsScreen, 1);
                }
            }
        });
    }

    private void logIn(String userEmail, String userPassword){
        final AppCompatActivity activityRef = this;
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToGroupDisplayScreen(user.getUid());
                        } else {
                            feedbackToUser.setText(activityRef.getString(R.string.InvalidEmailOrPassword));
                            userPasswordInput.setText("");
                            userPasswordInput.setSelectAllOnFocus(true);
                            popKeyboardUp();
                        }
                    }
                });
    }



    private void popKeyboardUp() {
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(userPasswordInput.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
        userPasswordInput.requestFocus();
    }


    private void handleNewSignIn(){
        final Button signUpBtn = findViewById(R.id.registerNow);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSignUpScreen = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(goToSignUpScreen, 1);
            }
        });
    }


    @Override
    public void onBackPressed()
    {
        final int EXIT_DELAY = 2000;
        if (back_pressed + EXIT_DELAY > System.currentTimeMillis())
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}