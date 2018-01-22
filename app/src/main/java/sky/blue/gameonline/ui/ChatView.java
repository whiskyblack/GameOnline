package sky.blue.gameonline.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import sky.blue.gameonline.R;

/**
 * Created by Yami on 1/21/2018.
 */

public class ChatView extends View {
    int w, h, radius=0;
    Paint paint;
    RectF rectF;
    boolean show=false;

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint=new Paint();
        paint.setColor(Color.parseColor("#55000000"));
        paint.setStyle(Paint.Style.FILL);
        rectF=new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w=w;
        this.h=h;
        rectF.left=0;
        rectF.top=0;
        rectF.right=w;
        rectF.bottom=h;
    }

    public void show(boolean show){
        this.show=show;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (show) {
            canvas.save();
            if (radius < w + 10) {
                radius += 50;
                canvas.drawCircle(w, 0, radius, paint);
            } else {
                canvas.drawRoundRect(rectF, 5, 5, paint);
            }
            canvas.restore();
        } else {
            if (radius>0){
                radius-=50;
                canvas.drawCircle(w, 0, radius, paint);
            }
        }

        delay();
    }

    private void delay(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        }, 10);
    }
}
