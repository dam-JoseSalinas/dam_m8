package com.example.asteroides;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import java.util.List;
import java.util.Vector;

public class VistaJuego extends View implements SensorEventListener {
    private Activity padre;
    public void setPadre(Activity padre) {
        this.padre = padre;
    }
    public void salir() {
        Bundle bundle = new Bundle();
        bundle.putInt("puntuacion", puntuacion);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        padre.setResult(Activity.RESULT_OK, intent);
        padre.finish();
    }

    private int puntuacion = 0;
    //MediaPlayer mpDisparo, mpExplosion;
    SoundPool soundPool;
    int idDisparo, idExplosion;
    private float mX=0, mY=0;
    private boolean disparo=false;

    //nave
    private Grafico nave;
    private int giroNave;
    private float aceleacionNave;
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;

    private Drawable drawableAsteroide[] = new Drawable[3];

    //asteroide
    private Vector<Grafico> Asteroides;
    private int numAsteroides = 5;
    private int numFragmentos = 3;

    //misil
    //private Grafico misil;
    private Vector<Grafico> Misiles;
    private Vector<Boolean> misilesActivos;
    private static int PASO_VELOCIDAD_MISIL = 12;
    //private boolean misilActivo = false;
    //private int tiempoMisil;
    private Vector<Integer> tiempoMisiles;
    private int numMisiles;

    //sensores
    private SensorManager mSensorManager;
    private Context miContext;

    private ThreadJuego thread  = new ThreadJuego();
    private static int PERIODO_PROCESO = 50;
    private long ultimoProceso = 0;

    public ThreadJuego getThread() {
        return thread;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float valor = event.values[1];
        if (!hayValorIncial) {
            valorIncial = valor;
            hayValorIncial = true;
        }
        giroNave=(int) (valor-valorIncial)/3;
    }

    private boolean hayValorIncial = false;
    private float valorIncial;
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                disparo=true;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dy<6 && dx>6) {
                    giroNave = Math.round((x - mX) / 2);
                    disparo = false;
                } else if (dx<6 && dy>6) {
                    aceleacionNave = Math.abs(Math.round((mY - y) / 25));
                    disparo = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                giroNave = 0;
                aceleacionNave = 0;
                if (disparo) {
                    ActivarMisil();
                }
                break;
        }
        mX=x; mY=y;
        return true;
    }

    public VistaJuego(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //mpDisparo = MediaPlayer.create(context, R.raw.disparo);
        //mpExplosion = MediaPlayer.create(context, R.raw.explosion);
        soundPool = new SoundPool.Builder().setMaxStreams(5).build();
        idDisparo = soundPool.load(context, R.raw.disparo, 0);
        idExplosion = soundPool.load(context, R.raw.explosion, 0);
        //drawableAsteroide
        Drawable drawableNave, drawableMisil;
        //drawableAsteroide = ContextCompat.getDrawable(context, R.drawable.asteroide1);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        numFragmentos = Integer.parseInt(pref.getString("fragmentos", "3"));
        if (pref.getString("graficos", "1").equals("0")) {
            //ASTEROIDE VECTORIAL
            Path pathAsteroide = new Path();
            pathAsteroide.moveTo((float) 0.3, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.3);
            pathAsteroide.lineTo((float) 0.8, (float) 0.2);
            pathAsteroide.lineTo((float) 1.0, (float) 0.4);
            pathAsteroide.lineTo((float) 0.8, (float) 0.6);
            pathAsteroide.lineTo((float) 0.9, (float) 0.9);
            pathAsteroide.lineTo((float) 0.8, (float) 1.0);
            pathAsteroide.lineTo((float) 0.4, (float) 1.0);
            pathAsteroide.lineTo((float) 0.0, (float) 0.6);
            pathAsteroide.lineTo((float) 0.0, (float) 0.2);
            pathAsteroide.lineTo((float) 0.3, (float) 0.2);

            for (int i = 0 ; i<3;i++) {
                ShapeDrawable dAsteroide = new ShapeDrawable(
                        new PathShape(pathAsteroide, 1, 1));
                dAsteroide.getPaint().setColor(Color.WHITE);
                dAsteroide.getPaint().setStyle(Paint.Style.STROKE);
                dAsteroide.setIntrinsicWidth(50 - i * 14);
                dAsteroide.setIntrinsicHeight(50 - i * 14);
                drawableAsteroide[i] = dAsteroide;
            }

            //drawableAsteroide = dAsteroide;




            //NAVE VECTORIAL
            Path pathNave = new Path();
            pathNave.moveTo((float) 0.0, (float) 0.0);
            pathNave.lineTo((float) 1.0, (float) 0.5);
            pathNave.lineTo((float) 0.0, (float) 1.0);
            pathNave.lineTo((float) 0.0, (float) 0.0);

            ShapeDrawable dNave = new ShapeDrawable(new PathShape(pathNave, 1, 1));
            dNave.getPaint().setColor(Color.WHITE);
            dNave.getPaint().setStyle(Paint.Style.STROKE);
            dNave.setIntrinsicWidth(20);
            dNave.setIntrinsicHeight(15);
            drawableNave = dNave;

            //MISIL VECTORIAL
            ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
            dMisil.getPaint().setColor(Color.WHITE);
            dMisil.getPaint().setStyle(Paint.Style.STROKE);
            dMisil.setIntrinsicWidth(15);
            dMisil.setIntrinsicHeight(3);
            drawableMisil = dMisil;




            setBackgroundColor(Color.BLACK);





        } else if (pref.getString("graficos", "1").equals("1")) {
            //drawableAsteroide = ContextCompat.getDrawable(context, R.drawable.asteroide1);
            drawableNave = ContextCompat.getDrawable(context, R.drawable.nave);
            drawableMisil = ContextCompat.getDrawable(context, R.drawable.misil1);
            drawableAsteroide[0] = context.getResources().getDrawable(R.drawable.asteroide1);
            drawableAsteroide[1] = context.getResources().getDrawable(R.drawable.asteroide2);
            drawableAsteroide[2] = context.getResources().getDrawable(R.drawable.asteroide3);
        } else {
            //drawableAsteroide = ContextCompat.getDrawable(context, R.drawable.asteroide1_svg);
            drawableNave = ContextCompat.getDrawable(context, R.drawable.nave);
            drawableMisil = ContextCompat.getDrawable(context, R.drawable.misil1);
            drawableAsteroide[0] = context.getResources().getDrawable(R.drawable.asteroide1);
            drawableAsteroide[1] = context.getResources().getDrawable(R.drawable.asteroide2);
            drawableAsteroide[2] = context.getResources().getDrawable(R.drawable.asteroide3);
        }



        nave = new Grafico(this, drawableNave);

        /*
        Asteroides = new Vector<Grafico>();
        for (int i = 0; i < numAsteroides; i++) {
            Grafico asteroide = new Grafico(this, drawableAsteroide);
            asteroide.setIncY(Math.random() * 4 - 2);
            asteroide.setIncX(Math.random() * 4 - 2);
            asteroide.setAngulo((int) (Math.random() * 360));
            asteroide.setRotacion((int)(Math.random() * 8 - 4));
            Asteroides.add(asteroide);
        }*/
        Asteroides = new Vector<Grafico>();
        for (int i = 0; i < numAsteroides; i++) {
            Grafico asteroide = new Grafico(this, drawableAsteroide[0]);
            asteroide.setIncY(Math.random() * 4 - 2);
            asteroide.setIncX(Math.random() * 4 - 2);
            asteroide.setAngulo((int) (Math.random() * 360));
            asteroide.setRotacion((int)(Math.random() * 8 - 4));
            Asteroides.add(asteroide);
        }

        //misil = new Grafico(this, drawableMisil);
        Misiles = new Vector<Grafico>();
        tiempoMisiles = new Vector<Integer>();
        misilesActivos = new Vector<Boolean>();
        for (int i=0; i<numMisiles; i++) {
            Misiles.add(new Grafico(this, drawableMisil));
            misilesActivos.add(false);
            tiempoMisiles.add(0);
        }

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if(!listSensors.isEmpty()) {
            Sensor orientationSensor = listSensors.get(0);
            mSensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
        }
        miContext = context;

    }

    @Override
    protected void onSizeChanged(int ancho, int alto, int ancho_enter, int alto_enter) {
        super.onSizeChanged(ancho, alto, ancho_enter, alto_enter);


        for (Grafico asteroide : Asteroides) {
            do {
                asteroide.setPosX(Math.random() * (ancho - asteroide.getAncho()));
                asteroide.setPosY(Math.random() * (alto - asteroide.getAlto()));
            } while (asteroide.distancia(nave)<(ancho+alto)/5);
        }
        nave.setPosX(ancho/2-nave.getAncho()/2);
        nave.setPosY(alto/2-nave.getAncho()/2);
        
        ultimoProceso = System.currentTimeMillis();
        thread.start();
    }


    @Override
    protected synchronized void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        nave.dibujarGrafico(canvas);
        //if(misilActivo) misil.dibujarGrafico(canvas);
        for(int p=0;p<Misiles.size();p++) {
            if(misilesActivos.elementAt(p)) Misiles.elementAt(p).dibujarGrafico(canvas);
        }
        for (Grafico asteroide :    Asteroides) {
            asteroide.dibujarGrafico(canvas);
        }
    }

    protected synchronized void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        if ((ultimoProceso + PERIODO_PROCESO) > ahora) {
            return;
        }

        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora;
        nave.setAngulo((int) (nave.getAngulo() + giroNave * retardo));
        double nIncX = nave.getIncX() + aceleacionNave * Math.cos(Math.toRadians(nave.getAngulo())) * retardo;
        double nIncY = nave.getIncY() + aceleacionNave * Math.sin(Math.toRadians(nave.getAngulo())) * retardo;

        if (Math.hypot(nIncX, nIncY) <= Grafico.getMaxVelocidad()) {
            nave.setIncX(nIncX);
            nave.setIncY(nIncY);
        }
        nave.incrementaPos(retardo);
        for (Grafico asteriode : Asteroides) {
            asteriode.incrementaPos(retardo);
        }
        /*
        if(misilActivo) {
            misil.incrementaPos(retardo);
            tiempoMisil -= retardo;
            if(tiempoMisil < 0) {
                misilActivo = false;
            } else {
                for (int i = 0; i < Asteroides.size(); i++) {
                    if(misil.verificaColision(Asteroides.elementAt(i))) {
                        destruyeAsteroide(i);
                        break;
                    }
                }
            }
        }*/
        for (int p=0;p<Misiles.size();p++) {
            if(misilesActivos.elementAt(p)) {
                Misiles.elementAt(p).incrementaPos(retardo);
                tiempoMisiles.set(p, tiempoMisiles.get(p)-(int)retardo);
                if (tiempoMisiles.elementAt(p)<0) {
                    misilesActivos.set(p, false);
                } else {
                    for(int i = 0; i <Asteroides.size();i++) {
                        if(Misiles.elementAt(p).verificaColision(Asteroides.elementAt(i))) {
                            Asteroides.remove(i);
                            misilesActivos.set(p, false);
                            break;
                        }
                    }
                }
            }
        }
        for (Grafico asteroide:Asteroides) {
            if(asteroide.verificaColision(nave)) {
                salir();
            }
        }
    }

    private void destruyeAsteroide(int retardo) {
    /*
        Asteroides.remove(i);
        misilActivo = false;
    }*/
        for(int p=0;p<Misiles.size();p++) {
            if(misilesActivos.elementAt(p)) {
                Misiles.elementAt(p).incrementaPos(retardo);
                tiempoMisiles.set(p, tiempoMisiles.get(p)-(int)retardo);
                if(tiempoMisiles.elementAt(p)<0) {
                    misilesActivos.set(p, false);
                } else {
                    for(int i = 0; i < Asteroides.size(); i++) {
                        if(Misiles.elementAt(p).verificaColision(Asteroides.elementAt(i))) {
                            int tam;
                            if(Asteroides.get(i).getDrawable() != drawableAsteroide[2]) {
                                if(Asteroides.get(i).getDrawable() == drawableAsteroide[1]) tam = 2;
                                else tam = 1;
                                for (int n = 0;n<numFragmentos;n++) {
                                    Grafico asteroide = new Grafico(this, drawableAsteroide[tam]);
                                    asteroide.setPosX(Asteroides.get(i).getPosX());
                                    asteroide.setPosY(Asteroides.get(i).getPosY());
                                    asteroide.setIncX(Math.random()*7-2-tam);
                                    asteroide.setIncY(Math.random()*7-2-tam);
                                    asteroide.setAngulo((int) (Math.random()*360));
                                    asteroide.setRotacion((int)(Math.random()*8-4));
                                    Asteroides.add(asteroide);
                                }
                            }
                            Asteroides.remove(i);
                            misilesActivos.set(p, false);
                            puntuacion += 1000;
                            //mpExplosion.start();
                            soundPool.play(idExplosion, 1,0, 0, 0, 2);
                            if (Asteroides.isEmpty()) {
                                salir();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void ActivarMisil() {
        /*misil.setPosX(nave.getPosX());
        misil.setPosY(nave.getPosY());
        misil.setAngulo(nave.getAngulo());
        misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
        misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
        tiempoMisil = (int) Math.min(this.getWidth() / Math.abs(misil.getIncX()), this.getHeight() / Math.abs(misil.getIncY())) - 2;
        misilActivo = true;*/
        for(int p=0; p<Misiles.size();p++) {
            if(!misilesActivos.elementAt(p)) {
                Misiles.elementAt(p).setPosX(nave.getPosX());

                Misiles.elementAt(p).setPosY(nave.getPosY());

                Misiles.elementAt(p).setAngulo(nave.getAngulo());

                Misiles.elementAt(p).setIncX(Math.cos(Math.toRadians(Misiles.elementAt(p).getAngulo())) * PASO_VELOCIDAD_MISIL);
                Misiles.elementAt(p).setIncY(Math.sin(Math.toRadians(Misiles.elementAt(p).getAngulo())) * PASO_VELOCIDAD_MISIL);
                tiempoMisiles.set(p, (int) Math.min(this.getWidth() / Math.abs(Misiles.elementAt(p).getIncX()), this.getHeight() / Math.abs(Misiles.elementAt(p).getIncY())) - 2);
                misilesActivos.set(p, true);
                //mpDisparo.start();
                soundPool.play(idDisparo, 1, 0, 1, 0, 2);
                break;

            }
        }
    }

    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        super.onKeyUp(codigoTecla, evento);
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleacionNave = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = 0;
                break;
            default:
                procesada = false;
                break;
        }
        return procesada;
    }

    @Override
    public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
        super.onKeyDown(codigoTecla, evento);
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleacionNave = +PASO_ACELERACION_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                giroNave = -PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = +PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                ActivarMisil();
                break;
            default:
                procesada = false;
                break;
        }
        return procesada;
    }

    public void activarSensores() {
        mSensorManager = (SensorManager) miContext.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if(!listSensors.isEmpty()) {
            Sensor orientationSensor = listSensors.get(0);
            mSensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }
    public void desactivarSensores(){
        mSensorManager.unregisterListener(this);
    }


    public class ThreadJuego extends Thread {
        private boolean pausa, corriendo;
        @Override
        public void run() {
            corriendo = true;
            while (corriendo) {
                actualizaFisica();
                synchronized (this) {
                    while (pausa) {
                        try {
                            wait();
                        } catch (Exception e) {}
                    }
                }
            }
        }
        public synchronized void pausar() {pausa = true;}
        public synchronized void reanudar() {
            pausa = false;
            notify();
        }
        public void detener() {
            corriendo = false;
            if(pausa) reanudar();
        }

    }


}
