package com.nipunbirla.boxloader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.nipunbirla.boxloaderlib.R;

/**
 * Created by Nipun on 6/14/2017.
 */

public class BoxLoaderView extends View {

    private static final int BLV_FRAME_RATE = 2;
    private static final int BLV_DEFAULT_SPEED = 10;
    private static final int BLV_DEFAULT_STROKE_WIDTH = 20;
    private static final int BLV_DEFAULT_STROKE_COLOR = Color.WHITE;
    private static final int BLV_DEFAULT_LOADER_COLOR = Color.BLUE;

    private int blvSpeed;
    private int blvStrokeWidth;
    private int blvStrokeColor;
    private int blvLoaderColor;
    private boolean blvDirChange = false;
    private Box blvBox, blvOutBox;
    private Handler blvHandler;


    public BoxLoaderView(Context context) {
        super(context);
        init(context, null);
    }

    public BoxLoaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        blvHandler = new Handler();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Options, 0, 0);
            blvStrokeColor = a.getColor(R.styleable.Options_blvStrokeColor, BLV_DEFAULT_STROKE_COLOR);
            blvLoaderColor = a.getColor(R.styleable.Options_blvLoaderColor, BLV_DEFAULT_LOADER_COLOR);
            blvStrokeWidth = a.getInt(R.styleable.Options_blvStrokeWidth, BLV_DEFAULT_STROKE_WIDTH);
            blvSpeed = a.getInt(R.styleable.Options_blvSpeed, BLV_DEFAULT_SPEED);
            a.recycle();
        } else {
            blvStrokeColor = BLV_DEFAULT_STROKE_COLOR;
            blvLoaderColor = BLV_DEFAULT_LOADER_COLOR;
            blvStrokeWidth = BLV_DEFAULT_STROKE_WIDTH;
            blvSpeed = BLV_DEFAULT_SPEED;
        }
    }

    public void setBlvSpeed(int blvSpeed) {
        this.blvSpeed = blvSpeed;
    }

    public void setBlvStrokeWidth(int blvStrokeWidth) {
        this.blvStrokeWidth = blvStrokeWidth;
    }

    public void setBlvStrokeColor(int color) {
        blvStrokeColor = color;
    }

    public void setBlvLoaderColor(int color) {
        blvLoaderColor = color;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (blvOutBox == null) {
            blvOutBox = new Box(left, top, right, bottom, blvStrokeColor, 10);
            blvOutBox.getPaint().setStrokeWidth(blvStrokeWidth);
        }
        if (blvBox == null) {
            blvBox = new Box(left + blvStrokeWidth, top + blvStrokeWidth, right / 2 - blvStrokeWidth, bottom / 2 - blvStrokeWidth, blvLoaderColor, 10);
            blvBox.setDx(blvSpeed);
            blvBox.setDy(blvSpeed);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(blvOutBox.getLeft(), blvOutBox.getTop(), blvOutBox.getRight(), blvOutBox.getBottom(), blvOutBox.getPaint());
        blvDirChange = blvBox.bounce(canvas, blvStrokeWidth);
        rectifyBoundaries(canvas, blvBox);
        canvas.drawRect(blvBox.getLeft(), blvBox.getTop(), blvBox.getRight(), blvBox.getBottom(), blvBox.getPaint());
        blvHandler.postDelayed(r, blvDirChange ? BLV_FRAME_RATE * 20 : BLV_FRAME_RATE);
    }

    private void rectifyBoundaries(Canvas canvas, Box box) {
        if (box.getLeft() < blvStrokeWidth) {
            box.getrect().left = blvStrokeWidth;
        }
        if (box.getTop() < blvStrokeWidth) {
            box.getrect().top = blvStrokeWidth;
        }
        if (box.getRight() > canvas.getWidth() - blvStrokeWidth) {
            box.getrect().right = canvas.getWidth() - blvStrokeWidth;
        }
        if (box.getBottom() > canvas.getHeight() - blvStrokeWidth) {
            box.getrect().bottom = canvas.getHeight() - blvStrokeWidth;
        }
    }

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    private static class Box {
        private int c, r, dx, dy, dir;
        private Rect rect;
        private Paint paint;

        //dir : 0 : right, 1 : down, 2 : left, 3 : up

        public Box(int left, int top, int right, int bottom, int color, int radius) {
            rect = new Rect(left, top, right, bottom);
            c = color;
            r = radius;
            paint = new Paint();
            paint.setColor(c);
            dx = 0;
            dy = 0;
            dir = 0;
        }

        public void setColor(int col) {
            c = col;
        }

        public void goTo(int l, int t, int r, int b) {
            rect.left = l;
            rect.top = t;
            rect.right = r;
            rect.bottom = b;
        }

        public void setDx(int speed) {
            dx = speed;
        }

        public void setDy(int speed) {
            dy = speed;
        }

        public int getLeft() {
            return rect.left;
        }

        public int getTop() {
            return rect.top;
        }

        public int getRight() {
            return rect.right;
        }

        public int getBottom() {
            return rect.bottom;
        }

        public int getRadius() {
            return r;
        }

        public Paint getPaint() {
            return paint;
        }

        public void increaseRight() {
            rect.right += dx;
        }

        public void decreaseRight() {
            rect.right -= dx;
        }

        public void increaseLeft() {
            rect.left += dx;
        }

        public void decreaseLeft() {
            rect.left -= dx;
        }

        public void increaseTop() {
            rect.top += dy;
        }

        public void decreaseTop() {
            rect.top -= dy;
        }

        public void increaseBottom() {
            rect.bottom += dy;
        }

        public void decreaseBottom() {
            rect.bottom -= dy;
        }

        public Rect getrect() {
            return rect;
        }

        //Bounce of edge
        public boolean bounce(Canvas canvas, int strokeWidth) {
            switch (dir) {
                case 0:
                    if (rect.right < canvas.getWidth() - strokeWidth) {
                        increaseRight();
                    } else {
                        increaseLeft();
                        if (rect.left > canvas.getWidth() / 2) {
                            dir++;
                            return true;
                        }
                    }
                    break;
                case 1:
                    if (rect.bottom < canvas.getHeight() - strokeWidth) {
                        increaseBottom();
                    } else {
                        increaseTop();
                        if (rect.top > canvas.getHeight() / 2) {
                            dir++;
                            return true;
                        }
                    }
                    break;
                case 2:
                    if (rect.left > strokeWidth) {
                        decreaseLeft();
                    } else {
                        decreaseRight();
                        if (rect.right < canvas.getWidth() / 2) {
                            dir++;
                            return true;
                        }
                    }
                    break;
                case 3:
                    if (rect.top > strokeWidth) {
                        decreaseTop();
                    } else {
                        decreaseBottom();
                        if (rect.bottom < canvas.getHeight() / 2) {
                            dir = 0;
                            return true;
                        }
                    }
                    break;
            }
            return false;
        }
    }

}
