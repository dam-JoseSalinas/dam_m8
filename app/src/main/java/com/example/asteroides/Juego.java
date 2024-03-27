package com.example.asteroides;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Juego extends AppCompatActivity {
    private VistaJuego vistaJuego;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);
        vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
        vistaJuego.setPadre(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        vistaJuego.desactivarSensores();
        vistaJuego.getThread().pausar();
    }
    @Override
    protected void onResume() {
        super.onResume();
        vistaJuego.activarSensores();
        vistaJuego.getThread().reanudar();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        vistaJuego.getThread().detener();
    }
}
