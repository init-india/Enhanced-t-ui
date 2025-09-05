// File: app/src/main/java/ohi/andre/consolelauncher/tuils/OutlineTextView.java

package ohi.andre.consolelauncher.tuils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class OutlineTextView extends AppCompatTextView {

    public static String SHADOW_TAG = "hasShadow";
    public static int redrawTimes = 1;
    private int drawTimes = -1;

    public OutlineTextView(Context context) {
        super(context);
    }

    public OutlineTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OutlineTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        if(drawTimes == -1) {
            drawTimes = getTag() == null ? 1 : redrawTimes;
        }

        for(int c = 0; c < drawTimes; c++) super.draw(canvas);
    }
}
