package marco.io.opearlovr;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import static android.R.style.Widget;

public class OverlayView extends LinearLayout {
    private final OverlayEye leftEye;
    private final OverlayEye rightEye;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0F);
        params.setMargins(0, 0, 0, 0);

        leftEye = new OverlayEye(context, attrs);
        leftEye.setLayoutParams(params);
        addView(leftEye);

        rightEye = new OverlayEye(context, attrs);
        rightEye.setLayoutParams(params);
        addView(rightEye);

        setDepthFactor(0.01F);
        setColor(Color.rgb(150, 255, 180));
        addContent();
        setVisibility(View.VISIBLE);
        setBackgroundColor(Color.argb(255, 49, 89, 106));

    }

    public boolean setProgress(int p) {
        boolean state;
        leftEye.setProgress(p);
        state = rightEye.setProgress(p);
        return state;
    }
    public void setDepthFactor(float factor) {
        leftEye.setDepthFactor(factor);
        rightEye.setDepthFactor(-factor);
    }
    public void setColor(int color) {
        leftEye.setColor(color);
        rightEye.setColor(color);
    }
    public void addContent() {
        leftEye.addContent();
        rightEye.addContent();
    }
    public void setContent (String text) {
        leftEye.setContent(text);
        rightEye.setContent(text);
    }

    private class OverlayEye extends ViewGroup {
        private Context context;
        private AttributeSet attrs;
        private TextView textView;
        private int textColor;
        private int depthOffset;
        private int viewWidth;
        private ProgressBar progressBar;

        public OverlayEye(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.context = context;
            this.attrs = attrs;
        }
        public void setColor(int color) {
            this.textColor = color;
        }
        public void addContent() {
            textView = new TextView(context, attrs);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(textColor);
            textView.setText("Loading Crime Scene...");
            textView.setX(depthOffset);
            addView(textView);

            progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
            progressBar.setProgress(0);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            //progressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0F));
            progressBar.setX(depthOffset);
            addView(progressBar);

        }
        public void setContent(String text){
            textView.setText(text);
        }
        public boolean setProgress(int p) {
            if ( p <= 99) {
                progressBar.setProgress(p);
                return true;
            }
            else {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                return false;
            }
        }

        public void setDepthFactor(float factor){
            this.depthOffset = (int)(factor * viewWidth);
        }
        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            final int width = right - left;
            final int height = bottom - top;
            final float verticalTextPos = 0.52F;
            float topMargin = height * verticalTextPos;
            textView.layout(0, (int) topMargin, width, bottom);
            progressBar.layout(0, (int) topMargin, width, bottom);
            viewWidth = width;

        }
    }
}
