package com.example.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pieces.Bloque;
import com.example.pieces.Pieza;
import com.example.pieces.PiezaC;
import com.example.pieces.PiezaI;
import com.example.pieces.PiezaL;
import com.example.pieces.PiezaLI;
import com.example.pieces.PiezaT;
import com.example.pieces.PiezaZ;
import com.example.pieces.PiezaZI;

import java.util.List;
public class TableroTetris extends AppCompatActivity {
    private Bloque[][] tablero;
    private Pieza piezaSiguiente;
    private Pieza piezaActual;
    private Pieza piezaRapida;
    private CreadorPiezas creador;
    private boolean perdido = false;
    private MainActivity mainActivity;
    private int contadorPiezas = 0;
    private int puntos=0;
    private Ventana ventana;
    private int COLUMNAS = 10;
    private int FILAS = 20;
    private int eliminateRows = 0;
    @SuppressLint("ResourceType")
    public TableroTetris(MainActivity mainActivity, Ventana v){
        tablero = new Bloque[20][10];
        creador = new CreadorPiezas(mainActivity);
        piezaActual = creador.crearPieza(2*eliminateRows);
        piezaSiguiente = creador.crearPieza(2*eliminateRows);
        this.mainActivity = mainActivity;
        this.ventana = v;
        for(int i=0; i<FILAS; i++){
            for(int j=0; j<COLUMNAS; j++){
                int[] pos = {i,j};
                tablero[i][j] = new Bloque(false, 0, Color.GRAY, pos);
            }
        }
    }

    public Bloque bloqueEn(int fila, int columna){
        return this.tablero[fila][columna];
    }

    public Pieza getPiezaSig() {
        return piezaSiguiente;
    }
    public int getEliminateRows(){
        return this.eliminateRows;
    }

    public Pieza getPiezaRapida() {
        return piezaRapida;
    }

    public void bajar(Pieza pieza){
       /* Pieza piezaMover;
        if (piezaActualBool) {
            piezaMover = this.getPiezaActual();
        }else{
            piezaMover = this.getPiezaRapida();
        }*/
        pieza.bajar();
        if(!esPosible(pieza)){
            pieza.subir();
            siguientePieza();
            ventana.borrarPieza(pieza);
        }
    }

    public void bajarPiezaRapida(Pieza pieza){
        pieza.bajar();
        if(!esPosible(pieza)){
            pieza.subir();
            posarPiezaActual(pieza);
            ventana.borrarPieza(pieza);
        }
    }
    public void despDcha(Pieza pieza){

        pieza.despDcha();
        if(!esPosible(pieza)){
            pieza.despIzqda();
        }
    }

    public void despIzqda(Pieza pieza){

        pieza.despIzqda();
        if(!esPosible(pieza)){
            pieza.despDcha();
        }
    }

    public void rotarDcha(Pieza pieza){

        pieza.rotarDcha();
        if(!esPosible(pieza)){
            pieza.rotarIzqda();
        }
    }

    public void rotarIzqda(Pieza pieza){

        pieza.rotarIzqda();
        if(!esPosible(pieza)){
            pieza.rotarDcha();
        }
    }

    public boolean esPosible(Pieza pieza){

        List<Bloque> b = pieza.bloquesActivos();

        boolean siBajo = true;

        for(Bloque bloque: b){
            siBajo = (bloque.isInBounds(FILAS,COLUMNAS) && !posicionOcupada(bloque.getPosicion()));
            if(!siBajo){
                break;
            }
        }

        return siBajo;
    }

    public void siguientePieza(){
        if(!comprobarPerdido()) {
            posarPiezaActual(piezaActual);
            this.piezaActual = piezaSiguiente;
            piezaSiguiente = creador.crearPieza(2*eliminateRows);
        }
    }

    public void posarPiezaActual(Pieza pieza){
        int fila_mayor=0;
        int fila_menor=FILAS-1;
        List<Bloque> bloques = pieza.bloquesActivos();
        contadorPiezas++;
        for(Bloque bloque: bloques){
            int pos[] = bloque.getPosicion();
            tablero[pos[0]][pos[1]] = bloque;
            if(fila_mayor<pos[0]){fila_mayor=pos[0];}
            if(fila_menor>pos[0]){fila_menor=pos[0];}
        }
        System.out.println("fila_mayor = " + fila_mayor);
        System.out.println("fila_menor = " + fila_menor);
        borrar_lineas(fila_mayor,fila_menor);
    }

    public Pieza getPiezaActual() {
        return piezaActual;
    }

    public void setPiezaRapida(Pieza piezaRapida) {
        this.piezaRapida = piezaRapida;
    }

    public boolean posicionOcupada(int[] pos) {
        return tablero[pos[0]][pos[1]].isActivo();
    }

    public boolean comprobarPerdido(){
        int i=0;
        while(i < COLUMNAS && !perdido){
            perdido = tablero[2*eliminateRows][i].isActivo();
            i++;
        }
        return perdido;
    }
    public void hard_Drop (Pieza pieza){
        int n = contadorPiezas;
        //Mientras que no se haya posado otra pieza
        while(contadorPiezas - n == 0){
            bajar(pieza);
        }
    }
    public void borrar_lineas(int fila_mayor,int fila_menor){
        int i=fila_mayor;
        while(i>=fila_menor) {
            System.out.println("fila_mayor = " + fila_mayor);
            System.out.println("fila_menor = " + fila_menor);
            System.out.println("i = " + i);
            if(comprobar_fila_llena(i)){
                borrarfila(i);
                mainActivity.sumar_puntuacion( 30);
                fila_menor--;
            }else{
                i--;
            }
        }
    }

    private void borrarfila(int fila) {
        System.out.println("Estoy en borrar fila");
        for (int i = fila; i > 0; i--) {
            for (int j = 0; j < COLUMNAS; j++) {
                tablero[i][j]=new Bloque(tablero[i-1][j]);
                tablero[i][j].bajar();
            }
        }
        for (int i = 0; i < COLUMNAS; i++) {
            int pos[]={0,i};
            tablero[0][i]=new Bloque(false, 0, Color.GRAY,pos);
        }
    }

    public boolean comprobar_fila_llena(int fila){
        for (int j = 0; j < COLUMNAS; j++) {
            if(!tablero[fila][j].isActivo()){return false;}
        }
        return true;
    }

    public boolean piezasChocan(){
        List<Bloque> bloquesActual = piezaActual.bloquesActivos();
        List<Bloque> bloquesRapido = piezaRapida.bloquesActivos();

        boolean seChocan = false;

        for(Bloque ba: bloquesActual){
            for(Bloque br : bloquesRapido){
                if(ba.mismaPosicion(br)){
                    return true;
                }
            }
        }

        return seChocan;
    }
    public void eliminarFilas(){
        eliminateRows++;
        for (int i=0;i<2*eliminateRows;i++){
            for (int j=0;j<getCOLUMNAS();j++){
                tablero[i][j].setColor(Color.BLACK);
            }
        }
    }

    public int getCOLUMNAS() {
        return COLUMNAS;
    }

    public void setCOLUMNAS(int COLUMNAS) {
        this.COLUMNAS = COLUMNAS;
    }

    public int getFILAS() {
        return FILAS;
    }

    public void setFILAS(int FILAS) {
        this.FILAS = FILAS;
    }
    public int getEliminateRows(){
        return this.eliminateRows;
    }
}
