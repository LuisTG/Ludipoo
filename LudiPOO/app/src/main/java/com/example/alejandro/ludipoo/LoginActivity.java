package com.example.alejandro.ludipoo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Luis on 20/03/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private static User usuarioActual;
    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    public static boolean active = false;

    @Override
    public void onStart(){
        super.onStart();
        active = true;
    }

    @Override
    public void onStop(){
        super.onStop();
        active = false;
    }

    private void inicializarFirebase() {
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("user/");
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("TAG", "Ya estaba iniciada la sesión:" + user.getUid());
                    if(active && !isDestroyed() && (progressDialog==null || !progressDialog.isShowing())){
                        progressDialog = new ProgressDialog(LoginActivity.this,
                                R.style.AppTheme_Dark_Dialog);
                        progressDialog.setCancelable(false);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Iniciando sesión...");
                        progressDialog.show();
                    }
                    ref.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            usuarioActual = dataSnapshot.getValue(User.class);
                            onLoginSuccess(getUsuarioActual().isMaestro());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    Log.d("TAG", "No estaba iniciada la sesión");
                }
            }
        };
        auth.addAuthStateListener(authListener);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inicializarFirebase();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW);

        _emailText = (EditText) findViewById(R.id.email);
        _passwordText = (EditText) findViewById(R.id.password);
        _loginButton = (Button) findViewById(R.id.login);
        _signupLink = (TextView) findViewById(R.id.signup);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");
        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();

        if (!validate(email,password)) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Validando...");
        progressDialog.show();



        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 3000);*/

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    onLoginFailed();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(false);
    }

    public void onLoginSuccess(boolean userType) {
        Intent intent;
        _loginButton.setEnabled(true);
        if(progressDialog!=null) progressDialog.dismiss();
        if(userType){
            intent = new Intent(getApplicationContext(), MaestroActivity.class);
        }else{
            if(getUsuarioActual().getIdGrupo()==null){
                intent = new Intent(getApplicationContext(), UnirGrupoActivity.class);
            }else{
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }
        }
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Inicio fallido", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
        _passwordText.setText("");
        if(progressDialog!=null) progressDialog.dismiss();
    }

    public boolean validate(String email, String password) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("ingresa una dirección email valida");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 18) {
            _passwordText.setError("entre 6 and 18 caracteres alfanumericos");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public static User getUsuarioActual() {
        return usuarioActual;
    }

    public static void refreshUser(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("user/");
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuarioActual = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}