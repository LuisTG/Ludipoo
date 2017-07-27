package com.example.alejandro.ludipoo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Alejandro on 20/03/2017.
 */

public class UserModifyActivity extends AppCompatActivity {
    private static final String TAG = "UserModifyActivity";
    private ProgressDialog progressDialog;
    EditText _nameText;
    EditText _emailText;
    EditText _nControlText;
    EditText _passwordText;
    EditText _reEnterPasswordText;
    Button _signupButton;
    TextView _loginLink;
    User usuarioActual;
    private boolean esAlumno=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
        usuarioActual = LoginActivity.getUsuarioActual();
        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _nControlText = (EditText) findViewById(R.id.input_nControl);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _reEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);

        if(getIntent().hasExtra("BAND")){
            esAlumno = true;
        }

        _nameText.setText(usuarioActual.getNombres());
        _emailText.setText(usuarioActual.getEmail());
        _emailText.setEnabled(false);
        _nControlText.setText(usuarioActual.getnControl());

        _signupButton = (Button) findViewById(R.id.btn_signup);

        _loginLink = (TextView) findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyUser();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void modifyUser() {
        Log.d(TAG, "Modify");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(UserModifyActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Actualizando Cuenta...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String nControl = _nControlText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        if(!password.isEmpty()){
            cambiarPassword(password,email,name,nControl);
        }else{
            createUser(email,name,nControl);
        }

    }
    public void cambiarPassword(final String password, final String email, final String name, final String nControl){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                createUser(email,name,nControl);
            }
        });
    }
    public void createUser(final String email,final String nombres, final String nControl) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("user/");
        usuarioActual.setNombres(nombres);
        usuarioActual.setnControl(nControl);
        ref.child(mAuth.getCurrentUser().getUid()).setValue(usuarioActual);
        LoginActivity.refreshUser();
        onSignupSuccess();
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        if(esAlumno){
            intent = new Intent(getApplicationContext(),MainActivity.class);
        }else{
            intent = new Intent(getApplicationContext(),MaestroActivity.class);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        if(progressDialog!=null) progressDialog.dismiss();
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        finish();
        startActivity(intent);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Actualización Fallida", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String nControl = _nControlText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("Al menos 3 caracteres");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Ingresa una dirección de correo valida");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        String patron = "^[E][0-9]{8}$";
        if(nControl.isEmpty() || !nControl.matches(patron)) {
            _nControlText.setError("Ingresa un número de control válido");
            valid = false;
        }else{
            _nControlText.setError(null);
        }

        if (!password.isEmpty() && (password.length() < 4 || password.length() > 10)) {
            _passwordText.setError("entre 4 y 10 caracteres alfanumericos");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (!reEnterPassword.isEmpty() && !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("La contraseña no concuerda");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}