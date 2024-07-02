package com.wyj.view.text;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.wyj.view.R;

/**
 *  https://www.jianshu.com/p/c58fc1fc31dd
 */
public class TextStudyActivity extends AppCompatActivity {

    private static final String TAG = "TextStudyActivity";

    private TextView tvText;
    private TextView tvText2;
    private TextView tvText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_study);

        tvText = findViewById(R.id.tv_text);
        tvText2 = findViewById(R.id.tv_text2);
        tvText3 = findViewById(R.id.tv_text3);
        tvText2.setIncludeFontPadding(false);
        findViewById(R.id.btn_font_metrics).setOnClickListener(view -> {
                    getFontMetrics();
                }
        );


    }

    private void getFontMetrics() {
        TextPaint paint = tvText.getPaint();
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        LogUtils.d("getFontMetrics fontMetrics top:" + fontMetrics.top);
        LogUtils.d("getFontMetrics fontMetrics ascent:" + fontMetrics.ascent);
        LogUtils.d("getFontMetrics fontMetrics descent:" + fontMetrics.descent);
        LogUtils.d("getFontMetrics fontMetrics bottom:" + fontMetrics.bottom);
        LogUtils.d("getFontMetrics fontMetrics leading:" + fontMetrics.leading);

        float height = fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading;
        LogUtils.d("getFontMetrics height:" + height);

        float paintFontMetricsHeight = paint.getFontMetrics(fontMetrics);
        LogUtils.d("getFontMetrics paintFontMetricsHeight:" + paintFontMetricsHeight);

        float height2 = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading;
        LogUtils.d("getFontMetrics height2:" + height2);
        String textStr = "给我一杯酒啊";
        Rect rect = new Rect();
        paint.getTextBounds(textStr, 0, textStr.length(), rect);
        LogUtils.d("getFontMetrics textBound height:" + rect.height());

        int measuredHeight = tvText.getMeasuredHeight();
        int layoutHeight = tvText.getLayout().getHeight();
        LogUtils.d("getFontMetrics measuredHeight:" + measuredHeight + " layoutHeight:" + layoutHeight);


        Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        LogUtils.d("getFontMetrics fontMetricsInt height:" + (fontMetricsInt.bottom - fontMetricsInt.top));

        LogUtils.d("getFontMetrics tvText2 height:" + tvText2.getMeasuredHeight());

        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        float sp14 = scaledDensity * 14;
        float density = getResources().getDisplayMetrics().density;
        int dp14 = getResources().getDimensionPixelSize(R.dimen.dp_14);
        LogUtils.d("getFontMetrics sp14:" + sp14 + " density * 14:" + density * 14 + " dp14:" + dp14);

        LogUtils.d("getFontMetrics textView3 height:" + tvText3.getMeasuredHeight());



    }
}