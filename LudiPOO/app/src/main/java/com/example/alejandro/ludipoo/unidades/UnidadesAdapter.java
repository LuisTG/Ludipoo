package com.example.alejandro.ludipoo.unidades;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.alejandro.ludipoo.R;
import com.example.alejandro.ludipoo.reactivos.ReactivosActivity;

import java.util.List;

public class UnidadesAdapter extends RecyclerView.Adapter<UnidadesAdapter.UnidadViewHolder> {

    private List<Unidad> items;

    public static class UnidadViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public Context context;
        public TextView _nombre;
        public TextView _unidad;
        public Button _btnDetalles;
        public UnidadViewHolder(View v) {
            super(v);
            context = v.getContext();
            _nombre = (TextView) v.findViewById(R.id.txt_reactivo_pregunta);
            _unidad = (TextView) v.findViewById(R.id.txt_numero_unidad);
            _btnDetalles = (Button) v.findViewById(R.id.btn_detalles);
        }
    }

    public UnidadesAdapter(List<Unidad> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public UnidadViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.unidad_cardview, viewGroup, false);
        return new UnidadViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final UnidadViewHolder viewHolder, final int i) {
        viewHolder._nombre.setText(items.get(i).getNombre());
        viewHolder._unidad.setText(String.valueOf(items.get(i).getId()+1));
        viewHolder._btnDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetailsSuccess(viewHolder.context, items.get(i).getId());
            }
        });
    }
    private void onDetailsSuccess(Context context, int idUnidad){
        Intent myIntent = new Intent(context.getApplicationContext(), ReactivosActivity.class);
        myIntent.putExtra("ID",idUnidad);
        context.startActivity(myIntent);
        //finish();
    }
}
