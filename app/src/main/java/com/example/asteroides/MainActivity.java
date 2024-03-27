package com.example.asteroides;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary libreria;
    public static AlmacenPuntuaciones almacen= new AlmacenPuntuacionesLista();

    //private MediaPlayer mp;
    private Button bAcercaDe;
    private Button bSalir;
    private Button bPlay;
    private Button bConfigurar;
    private Animation animation;


    @Override public void onGesturePerformed(GestureOverlayView ov, Gesture gesture) {

        ArrayList<Prediction> predictions=libreria.recognize(gesture);
        if (predictions.size()>0) {
            String comando = predictions.get(0).name;
            if(comando.equals("play")) {
                lanzarJuego(null);
            } else if(comando.equals("configurar")) {
                lanzarPreferencias(null);
            } else if(comando.equals("acerca_de")) {
                lanzarAcercaDe(null);
            }  else if(comando.equals("cancelar")) {
                finish();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bAcercaDe = findViewById(R.id.button03);
        bAcercaDe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                bAcercaDe.startAnimation(animation);
                lanzarAcercaDe(null);
            }
        });
        //bAcercaDe.setBackgroundResource(R.drawable.degradado);

        TextView texto = (TextView) findViewById(R.id.textView);
        animation = AnimationUtils.loadAnimation(this, R.anim.grio_con_zoom);
        texto.startAnimation(animation);

        bPlay = (Button) findViewById(R.id.button01);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.aparecer);
        bPlay.startAnimation(animation2);

        bConfigurar = (Button) findViewById(R.id.button02);
        Animation animation3 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_derecha);
        bConfigurar.startAnimation(animation3);

        bSalir = findViewById(R.id.button04);
        /*bSalir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });*/

        libreria = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if(!libreria.load()) {
            finish();
        }
        //GestureOverlayView gestureOverlayView = (GestureOverlayView) findViewById(R.id.gestures);
        //gestureOverlayView.addOnGesturePerformedListener(this);
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
       //mp = MediaPlayer.create(this, R.raw.audio);
        //mp.start();
        startService(new Intent(this, ServicioMusica.class));
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
    }
    @Override protected void onStart() {
        super.onStart();
        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
    }
    @Override protected void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        //mp.start();
    }
    @Override protected void onPause() {
        super.onPause();
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        //mp.pause();
    }
    @Override protected void onStop() {
        super.onStop();
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
        //mp.pause();
    }
    @Override protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
        //mp.start();
    }
    @Override protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        //mp.stop();
        stopService(new Intent(this, ServicioMusica.class));
        super.onDestroy();
    }
    @Override protected void onSaveInstanceState(Bundle estado) {
        super.onSaveInstanceState(estado);
        Toast.makeText(this, "onSaveInstanceState", Toast.LENGTH_SHORT).show();
        /*if(mp != null) {
            int pos = mp.getCurrentPosition();
            estado.putInt("posicion", pos);
        }*/
    }
    @Override protected void onRestoreInstanceState(Bundle estado) {
        super.onRestoreInstanceState(estado);
        Toast.makeText(this, "onRestoreInstanceState", Toast.LENGTH_SHORT).show();
        /*if (estado != null && mp != null) {
            int pos = estado.getInt("posicion");
            mp.seekTo(pos);
        }*/
    }


    public void salir(View view){
        finish();
    }

    public void lanzarAcercaDe(View view){
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);
    }

    public void lanzarPreferencias(View view){
        Intent i = new Intent(this, Preferencias.class);
        startActivity(i);
    }

    public void mostrarPreferencias(View view){
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String s = "música: "+ pref.getBoolean("musica",true)
                +", gráficos: " + pref.getString("graficos","?");
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; /** true -> el menú ya está visible */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.acercaDe) {
            lanzarAcercaDe(null);
            return true;
        }
        if (id == R.id.action_settings) {
            lanzarPreferencias(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void lanzarPuntuaciones(View view){
        Intent i = new Intent(this, Puntuaciones.class);
        startActivity(i);
    }

    static final int ACTIV_JUEGO =0;
    public void lanzarJuego(View view) {
        Intent intent = new Intent(this, Juego.class);
        startActivityForResult(intent, ACTIV_JUEGO);
    }

    @Override protected void onActivityResult(int requestode, int resutlCode, Intent data) {
        super.onActivityResult(requestode, resutlCode, data);
        if(requestode==ACTIV_JUEGO && resutlCode==RESULT_OK && data!=null) {
            int puntuacion = data.getExtras().getInt("puntuacion");
            String nombre = "Yo";
            almacen.guardarPuntuacion(puntuacion, nombre, System.currentTimeMillis());
            lanzarPuntuaciones(null);
        }
    }

}