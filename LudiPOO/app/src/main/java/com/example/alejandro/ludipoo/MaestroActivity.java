package com.example.alejandro.ludipoo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alejandro.ludipoo.grupos.Grupo;
import com.example.alejandro.ludipoo.grupos.GrupoActivity;
import com.example.alejandro.ludipoo.reactivos.CreaReactivoActivity;
import com.example.alejandro.ludipoo.unidades.UnidadesActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class MaestroActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MaestroActivity";
    private ProgressDialog progressDialog;
    private TextView _txtNombre;
    private TextView _txtHora;
    private Button _btnViewGroups;
    private Button _btnGenerate;
    private Button _btnCrearGrupo;
    private Button _btnCrearReactivo;
    private Spinner _spinnerEntrada;
    private Spinner _spinnerSalida;
    private EditText _txtEnrollment;
    private EditText _txtGroup;
    private String userID;
    private ImageView _imgMaestro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maestro_nav);
        addEventos();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void addEventos(){
        _txtHora = (TextView) findViewById(R.id.txtview_hora);
        _txtNombre = (TextView) findViewById(R.id.txt_nombre);
        _txtEnrollment = (EditText) findViewById(R.id.enrollment);
        _txtGroup = (EditText) findViewById(R.id.txt_group);
        _btnViewGroups = (Button) findViewById(R.id.button_ver_grupos);
        _btnGenerate = (Button) findViewById(R.id.btn_generate_key);
        _btnCrearGrupo = (Button) findViewById(R.id.btn_crear_grupo);
        _spinnerEntrada = (Spinner) findViewById(R.id.spinner_entrada);
        _spinnerSalida = (Spinner) findViewById(R.id.spinner_salida);
        _imgMaestro = (ImageView) findViewById(R.id.img_maestro);

        _btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _txtEnrollment.setText(Grupo.generateKey());
            }
        });
        _btnViewGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MaestroActivity.this, GrupoActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

        _btnCrearGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearGrupo();
            }
        });
        String array[] = new String[16];
        for(int i=7;i<23;i++){
            array[i-7] = Integer.toString(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,array);
        _spinnerEntrada.setAdapter(adapter);
        _spinnerSalida.setAdapter(adapter);

        // Se cargan los datos del maestro
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mtro = database.getReference("user/").child(userID);
        mtro.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    llenarDatos(user.getNombres());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void llenarDatos(String nombre){
        _txtNombre.setText("Nombre: "+nombre);
        _imgMaestro.setImageResource(R.drawable.maestro);
    }
    private boolean validate(String nombre, String matricula, int hEntrada, int hSalida){
        boolean valid = true;

        if (nombre.length() < 3) {
            _txtGroup.setError("ingresa un nombre con al menos 3 caracteres");
            valid = false;
        } else {
            _txtGroup.setError(null);
        }

        if (matricula.isEmpty()) {
            _txtEnrollment.setError("genere una matricula para el grupo");
            valid = false;
        } else {
            _txtEnrollment.setError(null);
        }

        if(hEntrada >= hSalida){
            _txtHora.setError("seleccione una hora de entrada y salida válidos");
            valid = false;
        }else{
            _txtHora.setError(null);
        }
        return valid;
    }

    public void crearGrupo() {
        Log.d(TAG, "Signup");
        String name = _txtGroup.getText().toString().trim();
        String matricula = _txtEnrollment.getText().toString().trim();
        int horaEntrada = _spinnerEntrada.getSelectedItemPosition()+7;
        System.out.println(horaEntrada);
        int horaSalida = _spinnerSalida.getSelectedItemPosition()+7;
        if (!validate(name,matricula,horaEntrada,horaSalida)) {
            onCreateFailed();
            return;
        }

        _btnCrearGrupo.setEnabled(false);

        progressDialog = new ProgressDialog(MaestroActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creando Grupo...");
        progressDialog.show();
        crearGrupo(name,matricula,horaEntrada,horaSalida);
    }
    private void subirImagen(){
        final StorageReference strRef = FirebaseStorage.getInstance().getReference("torre.jpg");
        _imgMaestro.setDrawingCacheEnabled(true);
        _imgMaestro.buildDrawingCache();
        Bitmap bm = _imgMaestro.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        UploadTask uploadTask = strRef.putBytes(byteArray);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }
    private void crearGrupo(String name, String matricula, int horaEntrada, int horaSalida){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("grupos/");

        Grupo nuevoGrupo = new Grupo(R.drawable.group,userID,name,matricula,horaEntrada,horaSalida);

        subirImagen();
        ref.child(matricula).setValue(nuevoGrupo);
        ref.child(matricula).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Grupo tmp = dataSnapshot.getValue(Grupo.class);
                onCreateGroupSuccess();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
    private void onCreateFailed(){
        Toast.makeText(getBaseContext(), "No se pudo crear el grupo", Toast.LENGTH_LONG).show();
        _btnCrearGrupo.setEnabled(true);
    }
    private void onCreateGroupSuccess(){
        if(progressDialog!=null) progressDialog.dismiss();
        Toast.makeText(getBaseContext(), "Grupo creado con éxito", Toast.LENGTH_LONG).show();
        _txtEnrollment.setText("");
        _txtGroup.setText("");
        _btnCrearGrupo.setEnabled(true);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.principal, menu);
        TextView nombre = (TextView) findViewById(R.id.navigation_user_name);
        TextView correo = (TextView) findViewById(R.id.navigation_user_email);
        nombre.setText(LoginActivity.getUsuarioActual().getNombres());
        correo.setText(LoginActivity.getUsuarioActual().getEmail());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_maestro_sign_out) {
            cerrarSesion();
        } else if(id == R.id.nav_maestro_modificar_reactivos){
            modificarReactivo();
        } else if(id== R.id.nav_maestro_crear_reactivos){
            crearReactivo();
        } else if(id==R.id.nav_modify_profile){
            Intent myIntent = new Intent(getApplicationContext(),UserModifyActivity.class);

            startActivity(myIntent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void modificarReactivo(){
        Intent myIntent = new Intent(MaestroActivity.this, UnidadesActivity.class);
        startActivity(myIntent);
        finish();
    }

    private void crearReactivo(){
        Intent myIntent = new Intent(MaestroActivity.this, CreaReactivoActivity.class);
        startActivity(myIntent);
        finish();
    }

    private void cerrarSesion(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
        mAuth.signOut();
        startActivity(myIntent);
        finish();
    }
}
