package com.example.crudapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapplication.EditarAlquilerDialog; // 👈 Importamos tu nuevo diálogo
import com.example.crudapplication.R;
import com.example.crudapplication.entities.AlquilarVehiculo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AlquilerAdapter extends RecyclerView.Adapter<AlquilerAdapter.AlquilerViewHolder> {

    private List<AlquilarVehiculo> listaAlquileres;
    private Context context;

    public AlquilerAdapter(List<AlquilarVehiculo> listaAlquileres, Context context) {
        this.listaAlquileres = listaAlquileres;
        this.context = context;
    }

    // MÉTODO EXTRA: Para actualizar la lista desde el Fragment de forma eficiente
    public void setAlquileres(List<AlquilarVehiculo> nuevaLista) {
        this.listaAlquileres = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlquilerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.alquiler_item, parent, false);
        return new AlquilerViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AlquilerViewHolder holder, int position) {
        // Obtenemos el objeto alquiler de la posición actual (para el clic normal)
        AlquilarVehiculo alquiler = listaAlquileres.get(position);

        holder.tvIdAlquiler.setText("Alquiler #" + alquiler.getID_Alquiler());
        holder.tvIdAuto.setText("Vehículo ID: #" + alquiler.getID_Auto());
        holder.tvIdCliente.setText("Cliente ID: #" + alquiler.getID_Cliente());
        holder.tvFechaInicio.setText(alquiler.getFecha_Inicio());
        holder.tvFechaFin.setText(alquiler.getFecha_Fin());

        // CONTROL DEL CLIC PARA EDITAR REGISTRO
        holder.itemView.setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date fechaFinalizacion = sdf.parse(alquiler.getFecha_Fin());
                Date fechaActual = new Date();

                Calendar calFin = Calendar.getInstance();
                Calendar calHoy = Calendar.getInstance();
                calFin.setTime(fechaFinalizacion);
                calHoy.setTime(fechaActual);

                calFin.set(Calendar.HOUR_OF_DAY, 0); calFin.set(Calendar.MINUTE, 0); calFin.set(Calendar.SECOND, 0); calFin.set(Calendar.MILLISECOND, 0);
                calHoy.set(Calendar.HOUR_OF_DAY, 0); calHoy.set(Calendar.MINUTE, 0); calHoy.set(Calendar.SECOND, 0); calHoy.set(Calendar.MILLISECOND, 0);

                if (calHoy.after(calFin)) {
                    Toast.makeText(context, "Ya no se puede modificar este registro, el alquiler ha finalizado", Toast.LENGTH_LONG).show();
                } else {
                    EditarAlquilerDialog dialogo = EditarAlquilerDialog.newInstance(alquiler);
                    if (context instanceof AppCompatActivity) {
                        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                        dialogo.show(fm, "EditarAlquiler");
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error interno al procesar el formato de fechas", Toast.LENGTH_SHORT).show();
            }
        });

        // CONTROL DEL CLIC LARGO PARA ELIMINAR REGISTRO
        holder.itemView.setOnLongClickListener(v -> {
            AlquilarVehiculo alquiler2 = listaAlquileres.get(holder.getAdapterPosition());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            try {
                Date fechaInicio = sdf.parse(alquiler2.getFecha_Inicio());
                Date fechaFin = sdf.parse(alquiler2.getFecha_Fin());
                Date fechaActual = new Date(); // El día de hoy

                Calendar calInicio = Calendar.getInstance();
                Calendar calFin = Calendar.getInstance();
                Calendar calHoy = Calendar.getInstance();

                calInicio.setTime(fechaInicio);
                calFin.setTime(fechaFin);
                calHoy.setTime(fechaActual);

                // Limpiamos las horas de todos los calendarios para comparar únicamente las fechas exactas
                calInicio.set(Calendar.HOUR_OF_DAY, 0); calInicio.set(Calendar.MINUTE, 0); calInicio.set(Calendar.SECOND, 0); calInicio.set(Calendar.MILLISECOND, 0);
                calFin.set(Calendar.HOUR_OF_DAY, 0); calFin.set(Calendar.MINUTE, 0); calFin.set(Calendar.SECOND, 0); calFin.set(Calendar.MILLISECOND, 0);
                calHoy.set(Calendar.HOUR_OF_DAY, 0); calHoy.set(Calendar.MINUTE, 0); calHoy.set(Calendar.SECOND, 0); calHoy.set(Calendar.MILLISECOND, 0);

                // REGLA: Un alquiler está "En Curso" si HOY se encuentra entre la Fecha de Inicio y la Fecha de Fin (ambas inclusivas)
                boolean estaEnCurso = (!calHoy.before(calInicio)) && (!calHoy.after(calFin));

                if (!estaEnCurso) {
                    // NO está en curso (es decir: es un alquiler futuro/pendiente o ya finalizó/pasado). Se puede borrar.
                    new androidx.appcompat.app.AlertDialog.Builder(context)
                            .setTitle("Eliminar Registro")
                            .setMessage("¿Estás seguro de que deseas eliminar este registro de alquiler?")
                            .setPositiveButton("Sí, eliminar", (dialog, which) -> {

                                Executors.newSingleThreadExecutor().execute(() -> {
                                    com.example.crudapplication.data.AppDB db = com.example.crudapplication.data.AppDB.getInstance(context);

                                    // 1. Eliminar el registro de la base de datos
                                    db.alquilerDAO().eliminarAlquiler(alquiler2);

                                    // 2. IMPORTANTE: Mandamos a llamar la Query inteligente que acabas de ajustar.
                                    // Al borrar el alquiler, esa Query reevaluará el inventario del día de hoy de forma automática.
                                    String fechaHoyStr = sdf.format(new Date());
                                    db.vehiculoDAO().actualizarEstadosVehiculos(fechaHoyStr);

                                    if (context instanceof AppCompatActivity) {
                                        AppCompatActivity activity = (AppCompatActivity) context;
                                        activity.runOnUiThread(() -> {
                                            Toast.makeText(context, "Registro eliminado correctamente", Toast.LENGTH_SHORT).show();

                                            // Notificar al fragmento principal para redibujar el RecyclerView
                                            android.os.Bundle resultado = new android.os.Bundle();
                                            resultado.putBoolean("alquilerGuardado", true);
                                            activity.getSupportFragmentManager().setFragmentResult("solicitudAlquiler", resultado);
                                        });
                                    }
                                });
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();

                } else {
                    // El alquiler está activo en este preciso momento
                    Toast.makeText(context, "No se puede eliminar: El alquiler se encuentra actualmente en curso", Toast.LENGTH_LONG).show();
                }

            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error al procesar la verificación de fechas", Toast.LENGTH_SHORT).show();
            }

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaAlquileres != null ? listaAlquileres.size() : 0;
    }

    public static class AlquilerViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdAuto, tvIdAlquiler, tvIdCliente, tvFechaInicio, tvFechaFin;

        public AlquilerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdAuto = itemView.findViewById(R.id.tvIdAuto);
            tvIdAlquiler = itemView.findViewById(R.id.tvIdAlquiler);
            tvIdCliente = itemView.findViewById(R.id.tvIdCliente);
            tvFechaInicio = itemView.findViewById(R.id.tvFechaInicio);
            tvFechaFin = itemView.findViewById(R.id.tvFechaFin);
        }
    }
}