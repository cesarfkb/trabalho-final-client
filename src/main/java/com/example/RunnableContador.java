package com.example;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RunnableContador implements Runnable{
    private AtomicInteger seg = new AtomicInteger(), min = new AtomicInteger(), hora = new AtomicInteger();
    private AtomicBoolean pausar = new AtomicBoolean(false);

    public RunnableContador() {
        seg.set(0);
        min.set(0);
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        while(true) {
            if (pausar.get()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(1000);
                    if (seg.get() == 59) {
                        seg.set(0);
                        min.incrementAndGet();
                        Thread.sleep(1000);
                    } else if (min.get() == 59) {
                        min.set(0);
                        hora.incrementAndGet();
                        Thread.sleep(1000);
                    }
                    seg.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getSeg() {
        return seg.get();
    }

    public int getMin() {
        return min.get();
    }

    public int getHora() {
        return hora.get();
    }

    public void setPausar(boolean pausar) {
        this.pausar.set(pausar);
    }

    public int getTempoTotal() {
        return (hora.get()*3600)+(min.get()*60)+seg.get();
    }

    public static int[] calculaTempo(int tempo) {
        int horas = tempo / 3600;
        if (horas > 0) {
            tempo -= horas * 3600;
        }
        int min = tempo / 60;
        if (min > 0) {
            tempo -= min * 60;
        }
        int segundos = tempo;
        return new int[]{horas,min,segundos};
    }
}
