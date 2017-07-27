package com.example.alejandro.ludipoo;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Point;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alejandro.ludipoo.grupos.Grupo;
import com.example.alejandro.ludipoo.reactivos.Reactivo;
import com.example.alejandro.ludipoo.unidades.Unidad;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{
    Button btn;
    List<Unidad> unidades;
    HiloRuleta hilo;
    TextView _txtUnidad;
    Grupo grupoActual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        getGrupo();
        cargarUnidades();

        _txtUnidad = (TextView) findViewById(R.id.txt_unidad);
        _txtUnidad.setText("¡Juega ahora!");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ImageView img = (ImageView)findViewById(R.id.imagen_ruleta);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        matrix = new Matrix();
        matrix.postScale(scalation,scalation);
        matrix.postTranslate(50,0);
        img.setScaleType(ImageView.ScaleType.MATRIX);
        img.setImageMatrix(matrix);
        btn = (Button) findViewById(R.id.button_girar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                girar();
            }
        });
    }
    private void cargarUnidades(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("unidades/");
        unidades = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Unidad tmp = child.getValue(Unidad.class);
                    unidades.add(tmp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getGrupo(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("grupos/");
        ref.child(LoginActivity.getUsuarioActual().getIdGrupo()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Grupo tmp = dataSnapshot.getValue(Grupo.class);
                setGrupo(tmp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setGrupo(Grupo x){
        grupoActual = x;
    }
    public void girar(){

        if(hilo==null || !hilo.isAlive()){
            btn.setEnabled(false);
            hilo = new HiloRuleta();
            hilo.start();
        }
    }
    final float scalation = .63f;
    Matrix matrix;
    public synchronized void girarRuleta(float angs){

        ImageView img = (ImageView)findViewById(R.id.imagen_ruleta);
        float px = img.getDrawable().getBounds().width()*scalation/2.0f +50;
        float py = img.getDrawable().getBounds().height()*scalation/2.0f;
        matrix.postRotate(angs,px,py);
        img.setImageMatrix(matrix);
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

        if (id == R.id.nav_profile) {
            Intent myIntent = new Intent(getApplicationContext(),ProfileActivity.class);
            myIntent.putExtra("ID",FirebaseAuth.getInstance().getCurrentUser().getUid());
            myIntent.putExtra("GRUPO",grupoActual.getNombre());
            myIntent.putExtra("BAND",true);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_sign_out) {
            cerrarSesion();
        } else if(id == R.id.nav_modify_profile){
            Intent myIntent = new Intent(getApplicationContext(),UserModifyActivity.class);
            myIntent.putExtra("BAND",true);
            startActivity(myIntent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private int cambiarUnidad(int unidadActual){
        int tmpUnidad=0;
        do{
             tmpUnidad = (int) (Math.random()*(grupoActual.getUnidadActual()+1));
        }while(tmpUnidad == unidadActual);
        //_txtUnidad.setText("Unidad "+tmpUnidad);
        return tmpUnidad;
    }
    private int unidad;
    private int cont;
    class HiloRuleta extends Thread{
        @Override
        public void run(){
            int limite = (int)(Math.random()*720.0)+1080;
            unidad = cambiarUnidad(-1);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _txtUnidad.setText(unidades.get(unidad).getNombre());
                }
            });
            float vel = 1.5f;
            cont = 0;
            for(int i=0;i<=limite;i++, cont++){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        girarRuleta(1);
                        if(cont%30 == 0){
                            unidad = cambiarUnidad(unidad);
                            _txtUnidad.setText(unidades.get(unidad).getNombre());
                        }
                    }
                });
                try {

                    Thread.sleep((int)vel);
                    vel*=1.001f;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {

                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            generarPregunta(unidad);
        }
    }

    private void generarPregunta(int unidad){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("preguntas");
        databaseReference.orderByChild("unidad").startAt(unidad).endAt(unidad).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Random random = new Random();
                int index = random.nextInt((int) dataSnapshot.getChildrenCount());
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (count == index) {
                        Reactivo react = snapshot.getValue(Reactivo.class);
                        String idReact = snapshot.getKey();
                        cargarPregunta(react,idReact);
                        return;
                    }
                    count++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void cargarPregunta(Reactivo react, String idReact){
        Intent myIntent = new Intent(getApplicationContext(), JuegoActivity.class);
        myIntent.putExtra("Reactivo",(Serializable)react);
        myIntent.putExtra("ID",idReact);
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
