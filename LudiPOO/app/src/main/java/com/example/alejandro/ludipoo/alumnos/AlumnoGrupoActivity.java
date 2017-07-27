package com.example.alejandro.ludipoo.alumnos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.alejandro.ludipoo.grupos.GrupoActivity;
import com.example.alejandro.ludipoo.R;
import com.example.alejandro.ludipoo.User;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Luis on 21-Mar-17.
 */

public class AlumnoGrupoActivity extends AppCompatActivity {
    private String idGrupo;
    private String nombreGrupo;
    private int imgGrupo;
    private List<Usuario> items = new ArrayList<>();
    private TextView _txtIdGrupo, _txtNombre;
    private ImageView _imgGrupo;
    private Spinner _spinnerUnidad;
    private Button _botonActualizar;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    public static int unidadActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumno_grp);
        _txtIdGrupo = (TextView) findViewById(R.id.txt_grupo_matricula);
        _txtNombre = (TextView) findViewById(R.id.txt_grupo_nombre);
        _imgGrupo = (ImageView) findViewById(R.id.img_grupo);
        _botonActualizar = (Button) findViewById(R.id.btn_actualizar);
        _spinnerUnidad = (Spinner) findViewById(R.id.spinner_unidad);
        idGrupo = getIntent().getStringExtra("ID");
        nombreGrupo = getIntent().getStringExtra("Nombre");
        imgGrupo = getIntent().getIntExtra("Imagen",R.drawable.group);
        unidadActual = getIntent().getIntExtra("Unidad",0);
        _spinnerUnidad.setSelection(unidadActual);
        _txtIdGrupo.setText(idGrupo);
        _txtNombre.setText(nombreGrupo);
        _imgGrupo.setImageResource(imgGrupo);
        addEventos();
    }
    private void addEventos(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String mtro = auth.getCurrentUser().getUid();
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference dr = fd.getReference("user/");

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.recycler_alumnos);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new AlumnoGrupoAdapter(items, idGrupo, nombreGrupo, imgGrupo);
        recycler.setAdapter(adapter);

        dr.orderByChild("idGrupo").equalTo(idGrupo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(!child.exists()) {
                        continue;
                    }
                    User tmp = child.getValue(User.class);
                    Usuario usr = new Usuario(tmp.getEmail(),tmp.getNombres(),tmp.getnControl(),tmp.isMaestro(),tmp.getIdGrupo(),child.getKey());
                    items.add(usr);
                }
                Collections.sort(items);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        _botonActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int unidad = _spinnerUnidad.getSelectedItemPosition();
                actualizarUnidad(unidad);
            }
        });
    }

    private void actualizarUnidad(int unidad){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("grupos/");
        db.child(idGrupo).child("unidadActual").setValue(unidad);
        unidadActual = unidad;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),GrupoActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
    class Usuario extends User implements Serializable{

        private String idUser;

        public String getIdUser() {
            return idUser;
        }

        public void setIdUser(String idUser) {
            this.idUser = idUser;
        }

        public Usuario(String email, String nombres, String nControl, boolean maestro, String idGrupo, String idUser) {
            super(email, nombres, nControl, maestro, idGrupo);
            this.idUser = idUser;
        }
    }
}
