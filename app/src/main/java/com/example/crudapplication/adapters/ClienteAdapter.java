package com.example.crudapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapplication.R;
import com.example.crudapplication.entities.Cliente;

import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    List<Cliente> lista;
    private Context miContext;
    final private clickLista miClick;
    final private clickEliminar miEliminar;
    final private clickEditar miEditar;

    public ClienteAdapter(List<Cliente> lista,
                          Context context,
                          clickLista listener,
                          clickEliminar miEliminar,
                          clickEditar miEditar) {
        this.lista = lista;
        miContext = context;
        miClick = listener;
        this.miEliminar = miEliminar;
        this.miEditar = miEditar;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cliente_item_layout, parent, false);

        return new ClienteViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = lista.get(position);

        holder.nombre.setText(cliente.nombre);
        holder.dui.setText(cliente.dui);
        holder.telefono.setText(cliente.telefono);
        holder.correoElectronico.setText(cliente.correoElectronico);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ClienteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nombre, dui, telefono, correoElectronico;
        ImageButton btnEliminar, btnEditar;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.tvNombre);
            dui = itemView.findViewById(R.id.tvDui);
            telefono = itemView.findViewById(R.id.tvTelefono);
            correoElectronico = itemView.findViewById(R.id.tvCorreoElectronico);

            itemView.setOnClickListener(this);

            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnEliminar.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                Cliente cliente = lista.get(pos);
                miEliminar.onEliminar(cliente, pos);
            });

            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEditar.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                Cliente cliente = lista.get(pos);
                miEditar.onEditar(cliente);
            });
        }

        @Override
        public void onClick(View view) {
            int itemPresionado = getAdapterPosition();

            if(itemPresionado != RecyclerView.NO_POSITION){
                Cliente cliente = lista.get(itemPresionado);
                miClick.clickItem(cliente);
            }

        }
    }

    public interface clickLista {
        void clickItem(Cliente cliente);
    }

    public interface clickEliminar {
        void onEliminar(Cliente cliente, int position);
    }

    public interface clickEditar {
        void onEditar(Cliente cliente);
    }

    public void actualizarLista(List<Cliente> clientes) {
        this.lista = clientes;
        notifyDataSetChanged();
    }
}