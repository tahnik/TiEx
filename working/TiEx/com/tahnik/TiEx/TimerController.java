package com.tahnik.TiEx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by tm305 on 02/04/2016.
 */
public class TimerController{
    static Timer timer = new Timer();
    final static int delay = 1000;
    final static int period = 1000;
    static int userSeconds;
    static String seconds;
    static String minutes;
    AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    AtomicBoolean atomicPause = new AtomicBoolean(false);
    Thread pauseThread;
    @FXML
    TextField timerInput;
    @FXML
    Button timerStart;
    @FXML
    Button timerReset;
    @FXML
    Label timerMinutes;
    @FXML
    Label timerSeconds;
    TimerTask timerTask;

    public void startTimer(){
        atomicBoolean.set(false);
        atomicPause.set(false);
        timer.cancel();
        timer = new Timer();
        userSeconds = Integer.parseInt(timerInput.getText()) * 60;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                userSeconds--;
                if(userSeconds == 0){
                    timer.cancel();
                }
                if(userSeconds%60 == 0){
                    seconds = "00";
                }else if(String.valueOf(userSeconds%60).length() < 2){
                    seconds = "0" + userSeconds%60;
                }else {
                    seconds = userSeconds%60 + "";
                }


                if(userSeconds/60 == 0){
                    minutes = "00";
                }else if(String.valueOf(userSeconds/60).length() < 2){
                    minutes = "0" + userSeconds/60;
                } else {
                    minutes = userSeconds/60 + "";
                }
                if(atomicBoolean.get()){
                    seconds = "00";
                    minutes = "00";
                    timer.cancel();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        timerMinutes.setText(minutes);
                    }
                });
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        timerSeconds.setText(seconds);
                    }
                });
                if(atomicPause.get()){
                    synchronized (timerTask){
                        try {
                            timerTask.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        timer.schedule(timerTask, delay, period);
    }
    public void cancelTimer(){
        timerSeconds.setText("00");
        timerMinutes.setText("00");
        synchronized (timerTask){
            timerTask.notifyAll();
        }
        atomicBoolean.set(true);
        //timer.cancel();
    }
    public void pauseTimer(){
        //System.out.println(atomicPause.get());
        if(atomicPause.get()){
            atomicPause.set(false);
            synchronized (timerTask) {
                timerTask.notifyAll();
            }
        }else{
            //System.out.println("I'm here");
            atomicPause.set(true);
        }
    }
}
