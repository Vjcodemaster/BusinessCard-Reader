package app_utility;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.autochip.businesscardocr.R;

public class CardFocusView extends View {

    private Paint paint;
    private Path path = new Path();
    private int width, height;
    Canvas canvas;
    private Rect rectangle;


    public CardFocusView(Context context) {
        super(context);
        init();
    }

    public CardFocusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardFocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        this.setBackgroundColor(getResources().getColor(R.color.transparent));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        drawMiddleLine();
        drawLeftUpCorner();
        drawLeftDownCorner();
        drawRightUpCorner();
        drawRightDownCorner();
        drawRectSample();
        drawBlackBackgorund();
    }

    private void drawRectSample() {
        int x = width / 6;
        int y = height / 4;
        int widthLength = x*5;
        int heightLenght = y* 3;
        Paint paint = new Paint();
        // create a rectangle that we'll draw later
        rectangle = new Rect(x, y, widthLength, heightLenght);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.drawRect(rectangle, paint);
    }

    private void drawLeftUpCorner() {
        if (width != 0 && height != 0) {
            int startX = width / 6;
            int startY = height / 4;
            int endX = startX * 2;
            int endY = startY;
            canvas.drawLine(startX, startY, endX, endY, paint);
            endX = startX;
            endY += startX;
            canvas.drawLine(startX, startY, endX, endY, paint);
        }
    }

    private void drawLeftDownCorner() {
        if (width != 0 && height != 0) {
            int startX = width / 6;
            int startY = height / 4 * 3;
            int endX = startX * 2;
            int endY = startY;
            canvas.drawLine(startX, startY, endX, endY, paint);
            endX = startX;
            endY -= startX;
            canvas.drawLine(startX, startY, endX, endY, paint);

        }
    }

    private void drawRightUpCorner() {
        if (width != 0 && height != 0) {
            int startX = (width / 6) * 4;
            int startY = height / 4;
            int endX = startX + (width / 6);
            int endY = startY;
            canvas.drawLine(startX, startY, endX, endY, paint);
            startX = endX;
            endY = startY + (width / 6);
            canvas.drawLine(startX, startY, endX, endY, paint);
        }
    }

    private void drawRightDownCorner() {
        if (width != 0 && height != 0) {
            int startX = (width / 6) * 4;
            int startY = (height / 4) * 3;
            int endX = startX + (width/ 6);
            int endY = startY;
            canvas.drawLine(startX, startY, endX, endY, paint);
            startX = endX;
            endY -= width / 6;
            canvas.drawLine(startX, startY, endX, endY, paint);

        }
    }

    private void drawBlackBackgorund() {
        int x = width / 6;
        int y = height / 4;
        int widthLength = x*5;
        int heightLenght = y* 3;

        RectF rect = new RectF(x,y,widthLength,heightLenght);

        path.addRect(rect,Path.Direction.CW);
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        canvas.clipPath(path);
        canvas.drawColor(getResources().getColor(R.color.semi_transparent));

    }


    private void drawMiddleLine() {
        if (width != 0 && height != 0) {
            int startX = width / 3;
            int startY = height / 2;
            int endX = startX * 2;
            int endY = startY;
            canvas.drawLine(startX, startY, endX, endY, paint);
            paint.setColor(Color.GREEN);
        }
    }

    public void setBounds(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
