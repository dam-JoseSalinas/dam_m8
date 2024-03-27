package com.example.asteroides;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Puntuaciones extends ListActivity{
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puntuaciones);
        //setListAdapter(new ArrayAdapter(this,
        //                R.layout.elemento_lista,
        //                R.id.titulo,
        //                MainActivity.almacen.listaPuntuaciones(10)));
        setListAdapter(new MiAdaptador(this,MainActivity.almacen.listaPuntuaciones(10)));
    }
    protected void onListItemClick(ListView listView, View view, int position, long id){
        super.onListItemClick(listView, view,position,id);
        Object o = getListAdapter().getItem(position);
        Toast.makeText(this,"Selección: "+Integer.toString(position) + " - " + o.toString(), Toast.LENGTH_LONG).show();
    }
}
