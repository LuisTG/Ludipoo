package com.example.alejandro.ludipoo.reactivos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alejandro.ludipoo.R;
import com.example.alejandro.ludipoo.alumnos.AlumnoGrupoActivity;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Alejandro on 05/04/2017.
 */

public class ReactivosAdapter extends RecyclerView.Adapter<ReactivosAdapter.ReactivoViewHolder> {

    private List<Reactivo> items;
    private List<String> ids;

    public static class ReactivoViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public Context context;
        public TextView _pregunta;
        public Button _btnDetalles;
        public ReactivoViewHolder(View v) {
            super(v);
            context = v.getContext();
            _pregunta = (TextView) v.findViewById(R.id.txt_reactivo_pregunta);
            _btnDetalles = (Button) v.findViewById(R.id.btn_detalles);
        }
    }

    public ReactivosAdapter(List<Reactivo> items, List<String> ids) {
        this.items = items;
        this.ids = ids;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ReactivoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.reactivo_cardview, viewGroup, false);
        return new ReactivoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ReactivoViewHolder viewHolder, final int i) {
        viewHolder._pregunta.setText(items.get(i).getPregunta());
        viewHolder._btnDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetailsSuccess(viewHolder.context, ids.get(i), items.get(i));
            }
        });
    }
    private void onDetailsSuccess(Context context, String idPregunta, Reactivo react){
        Intent myIntent = new Intent(context.getApplicationContext(), ModificaReactivoActivity.class);
        myIntent.putExtra("ID",idPregunta);
        myIntent.putExtra("Reactivo",(Serializable)react);
        context.startActivity(myIntent);
        //finish();
    }
}
