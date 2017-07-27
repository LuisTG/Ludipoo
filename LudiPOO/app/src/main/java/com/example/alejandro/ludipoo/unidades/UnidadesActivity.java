package com.example.alejandro.ludipoo.unidades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.alejandro.ludipoo.MaestroActivity;
import com.example.alejandro.ludipoo.R;
import com.example.alejandro.ludipoo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luis on 21-Mar-17.
 */

public class UnidadesActivity extends AppCompatActivity {
    private List<Unidad> items = new ArrayList<>();
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unidades);
        addEventos();
    }

    private void addEventos() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference dr = fd.getReference("unidades/");

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.recycler_unidades);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new UnidadesAdapter(items);
        recycler.setAdapter(adapter);

        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (!child.exists()) {
                        continue;
                    }
                    Unidad tmp = child.getValue(Unidad.class);
                    items.add(tmp);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MaestroActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    class Usuario extends User {
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
