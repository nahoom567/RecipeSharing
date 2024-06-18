package com.example.recipeproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TransitionClass extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder surfaceHolder;
    private AnimationThread animationThread;
    public static long ANIMATION_DURATION = 2000;
    private Handler uiHandler;

    public TransitionClass(Context context) {
        super(context);
        init();
    }

    public TransitionClass(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        uiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("TransitionClass", "Surface created");
        animationThread = new AnimationThread(ANIMATION_DURATION);
        animationThread.setOnAnimationEndListener(new Runnable() {
            @Override
            public void run() {
                Log.d("TransitionClass", "Animation completed");
            }
        });
        animationThread.setRunning(true);
        animationThread.start();
        Log.d("TransitionClass", "Animation thread started");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("TransitionClass", "Surface changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("TransitionClass", "Surface destroyed");
        if (animationThread != null) {
            animationThread.setRunning(false);
            boolean retry = true;
            while (retry) {
                try {
                    animationThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            animationThread = null;  // ensuring the thread is properly garbage collected
        }
    }

    private void drawAnimation(Canvas canvas) {
        Log.d("TransitionClass", "Drawing animation...");

        Paint paint = new Paint();

        long startTime = animationThread.getStartTime();
        long elapsedTime = System.currentTimeMillis() - startTime;
        float fraction = Math.min(elapsedTime / (float) ANIMATION_DURATION, 1.0f); // Normalized time (0 to 1 over ANIMATION_DURATION)

        // defining constants for scaling rectangle
        final float RECT_TRANSLATE_X_RATIO = 0.25f;
        final float RECT_TRANSLATE_Y_RATIO = 0.5f;
        final float RECT_SCALE_FACTOR = 1.0f;
        final float RECT_MIN_X = -50;
        final float RECT_MIN_Y = -50;
        final float RECT_MAX_X = 50;
        final float RECT_MAX_Y = 50;

        // defining constants for rotating circle
        final int CIRCLE_RADIUS = 50;
        final int CIRCLE_PATH_RADIUS = 200;

        // defining constants for scaling and rotating triangle
        final float TRIANGLE_TRANSLATE_X_RATIO = 0.75f;
        final float TRIANGLE_SCALE_FACTOR = 0.5f;
        final float TRIANGLE_HALF_SIZE = 50;

        // defining constant for full circle rotation
        final int FULL_CIRCLE_DEGREES = 360;

        // defining constant for maximum color value
        final int MAX_COLOR_VALUE = 255;

        // scaling rectangle with color transition
        paint.setColor(Color.rgb((int) (MAX_COLOR_VALUE * fraction), 0, (int) (MAX_COLOR_VALUE * (1 - fraction))));
        paint.setAlpha((int) (MAX_COLOR_VALUE * fraction));
        canvas.save();
        canvas.translate(getWidth() * RECT_TRANSLATE_X_RATIO, getHeight() * RECT_TRANSLATE_Y_RATIO);
        canvas.scale(1 + fraction * RECT_SCALE_FACTOR, 1 + fraction * RECT_SCALE_FACTOR);
        canvas.drawRect(RECT_MIN_X, RECT_MIN_Y, RECT_MAX_X, RECT_MAX_Y, paint);
        canvas.restore();

        // rotating circle moving along a path
        paint.setColor(Color.rgb(0, (int) (MAX_COLOR_VALUE * fraction), (int) (MAX_COLOR_VALUE * (1 - fraction))));
        paint.setAlpha((int) (MAX_COLOR_VALUE * fraction));
        canvas.save();
        float circleX = (float) (getWidth() / 2 + CIRCLE_PATH_RADIUS * Math.cos(Math.PI * 2 * fraction));
        float circleY = (float) (getHeight() / 2 + CIRCLE_PATH_RADIUS * Math.sin(Math.PI * 2 * fraction));
        canvas.translate(circleX, circleY);
        canvas.rotate(FULL_CIRCLE_DEGREES * fraction);
        canvas.drawCircle(0, 0, CIRCLE_RADIUS, paint);
        canvas.restore();

        // scaling and rotating triangle
        paint.setColor(Color.rgb((int) (MAX_COLOR_VALUE * (1 - fraction)), (int) (MAX_COLOR_VALUE * fraction), 0));
        paint.setAlpha((int) (MAX_COLOR_VALUE * fraction));
        canvas.save();
        canvas.translate(getWidth() * TRIANGLE_TRANSLATE_X_RATIO, getHeight() / 2);
        canvas.scale(1 + TRIANGLE_SCALE_FACTOR * fraction, 1 + TRIANGLE_SCALE_FACTOR * fraction);
        canvas.rotate(FULL_CIRCLE_DEGREES * fraction);
        Path trianglePath = new Path();
        trianglePath.moveTo(-TRIANGLE_HALF_SIZE, TRIANGLE_HALF_SIZE);
        trianglePath.lineTo(TRIANGLE_HALF_SIZE, TRIANGLE_HALF_SIZE);
        trianglePath.lineTo(0, -TRIANGLE_HALF_SIZE);
        trianglePath.close();
        canvas.drawPath(trianglePath, paint);
        canvas.restore();
    }

    public class AnimationThread extends Thread {
        private boolean isRunning = true;
        private long startTime;

        private Runnable onAnimationEndListener;

        public AnimationThread(long duration) {
            // duration of the animation
            ANIMATION_DURATION = duration;
        }

        public void setOnAnimationEndListener(Runnable listener) {
            onAnimationEndListener = listener;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        @Override
        public void run() {
            startTime = System.currentTimeMillis();
            Log.d("AnimationThread", "Animation started");

            while (isRunning) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Canvas canvas = null;
                        try {
                            canvas = surfaceHolder.lockCanvas();
                            if (canvas != null) {
                                synchronized (surfaceHolder) {
                                    drawAnimation(canvas);
                                }
                                surfaceHolder.unlockCanvasAndPost(canvas);
                            }
                        } catch (Exception e) {
                            if (canvas != null) {
                                surfaceHolder.unlockCanvasAndPost(canvas);
                            }
                            Log.e("TransitionClass", "Exception in animation thread", e);
                        }
                    }
                });

                long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime >= ANIMATION_DURATION) {
                    // stopping animation after ANIMATION_DURATION milliseconds
                    isRunning = false;
                    if (onAnimationEndListener != null) {
                        onAnimationEndListener.run();
                    }
                    Log.d("AnimationThread", "Animation duration completed");
                }

                try {
                    // controlling animation speed (e.g., update every 16 milliseconds)
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    Log.e("TransitionClass", "InterruptedException in animation thread", e);
                }
            }
        }
    }
}
