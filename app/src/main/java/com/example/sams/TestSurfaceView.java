package com.example.sams;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    boolean a = true;
    boolean b = true;
    boolean c = true;

    public TestSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getContext(), getHolder());
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.requestStop();
        boolean retry = true;
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (a && b && c) {
            a = false;
            drawThread.color = Color.GREEN;
        } else if (b && c) {
            b = false;
            drawThread.color = Color.BLUE;
        } else if (c) {
            drawThread.color = Color.RED;
            a = true;
            b = true;
            c = true;
        }
        return super.onTouchEvent(event);
    }
}

class DrawThread extends Thread {

    int color = Color.RED;
    private SurfaceHolder surfaceHolder;
    private volatile boolean running = true;

    public DrawThread(Context context, SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    public void requestStop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                try {
                    canvas.drawColor(color);
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}