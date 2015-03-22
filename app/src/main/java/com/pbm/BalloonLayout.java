package com.pbm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class BalloonLayout extends LinearLayout {

	public BalloonLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void dispatchDraw(@NonNull Canvas canvas) {
		Paint panelPaint = new Paint();
		panelPaint.setARGB(0, 0, 0, 0);

		RectF panelRect = new RectF();
		panelRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
		canvas.drawRoundRect(panelRect, 5, 5, panelPaint);

		RectF baloonRect = new RectF();
		baloonRect.set(0, 0, getMeasuredWidth(), 2 * (getMeasuredHeight() / 3));
		panelPaint.setARGB(230, 255, 255, 255);
		canvas.drawRoundRect(baloonRect, 10, 10, panelPaint);

		Path balloonTip = new Path();
		balloonTip.moveTo(5 * (getMeasuredWidth() / 8), 2 * (getMeasuredHeight() / 3));
		balloonTip.lineTo(getMeasuredWidth() / 2, getMeasuredHeight());
		balloonTip.lineTo(3 * (getMeasuredWidth() / 4), 2 * (getMeasuredHeight() / 3));

		canvas.drawPath(balloonTip, panelPaint);

		super.dispatchDraw(canvas);
	}
}