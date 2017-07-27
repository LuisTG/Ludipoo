package com.example.alejandro.ludipoo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UnirGrupoActivity extends AppCompatActivity {
    private static final String TAG = "UnirGrupoActivity";
    private ProgressDialog progressDialog;
    Button _btnUnirGrupo;
    EditText _txtUnirGrupo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unir_grupo);
        addEventos();
    }
    public void addEventos(){
        _btnUnirGrupo = (Button) findViewById(R.id.btn_unir_grupo);
        _btnUnirGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unirGrupo();
            }
        });
        _txtUnirGrupo = (EditText) findViewById(R.id.txt_unir_grupo);
    }
    private boolean validate(String groupCode){
        if(groupCode.isEmpty()){
            _txtUnirGrupo.setError("ingresa un código de grupo");
            return false;
        }
        _txtUnirGrupo.setError(null);
        return true;

    }
    private void unirGrupo(){
        Log.d(TAG, "Unir a un Grupo");
        String groupCode = _txtUnirGrupo.getText().toString().trim();
        if(!validate(groupCode)){
            onUnirFailed();
            return;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("grupos/");
        ref.child(groupCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    unir(dataSnapshot.getKey());
                }else{
                    _txtUnirGrupo.setError("el grupo no existe");
                    if(progressDialog!=null) progressDialog.dismiss();
                    _btnUnirGrupo.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        _btnUnirGrupo.setEnabled(false);
        progressDialog = new ProgressDialog(UnirGrupoActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Validando...");
        progressDialog.show();
    }

    private void unir(String group){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String idUsuario = auth.getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user/"+idUsuario);
        ref.child("idGrupo").setValue(group);
        onUnirSuccess();
    }

    private void onUnirSuccess(){
        if(progressDialog!=null) progressDialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        _txtUnirGrupo.setError(null);
        startActivity(intent);
        finish();
    }

    private void onUnirFailed(){
        if(progressDialog!=null) progressDialog.dismiss();
        Toast.makeText(getBaseContext(), "Registro Fallido", Toast.LENGTH_LONG).show();
        _btnUnirGrupo.setEnabled(true);
    }
}
