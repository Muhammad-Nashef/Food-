package com.example.foodplus;

import android.os.Handler;
import android.widget.TextView;

public class TypewriterAnimation {
    private final TextView textView;
    private CharSequence text;
    private int index;
    private long delay = 150;
    private final Handler handler = new Handler();

    public TypewriterAnimation(TextView textView) {
        this.textView = textView;
    }

    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            textView.setText(text.subSequence(0, index++));
            if (index <= text.length()) {
                handler.postDelayed(characterAdder, delay);
            }
        }
    };

    public void animateText(CharSequence text) {
        if (text.length() > 15) {
            text = text.subSequence(0, 15) + "...";
        }
        this.text = text;
        index = 0;
        textView.setText("");
        handler.removeCallbacks(characterAdder);
        handler.postDelayed(characterAdder, delay);
    }

    public void setCharacterDelay(long millis) {
        this.delay = millis;
    }
}
