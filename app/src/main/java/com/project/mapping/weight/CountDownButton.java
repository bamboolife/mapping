package com.project.mapping.weight;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.Button;

import com.project.mapping.R;

public class CountDownButton extends Button {
    private String text;
    private String hint = "%d 秒后重发";
    private int second = 60;
    private CountDownTimer timer;
    private String baseText = "";

    public CountDownButton(Context context) {
        this(context, null);
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        baseText = getText().toString();
    }

    private void setHintCount(int i) {
        String text = String.format(hint, i);
        setText(text);
    }

    public void setHintText(String hint) {
        //hint格式为内容+%d,如：“%d秒后重新获取”
        this.hint = hint;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        this.text = text.toString();
        super.setText(text, type);
    }

    public void setCountdownTime(int second) {
        this.second = second;
    }


    public void onStart() {
        baseText = getText().toString();
        if (timer == null) {
            timer = getCountDownTimer(second);
        }
        timer.cancel();
        timer.start();
    }

    private CountDownTimer getCountDownTimer(int time) {
        return new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isClickable())
                    setClickable(false);
                setAlpha(0.4f);
                setHintCount((int) millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                setClickable(true);
                setText(baseText);
                setAlpha(1.0f);
            }
        };
    }

    public void onCancel() {
        if (timer != null) {
            timer.cancel();
        }
    }

}
