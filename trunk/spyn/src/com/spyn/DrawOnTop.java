/*
 * Daniela Rosner 
 * 
 * Spyn!
 * 
 */

package com.spyn;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class DrawOnTop extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {

		DrawOnTopMethod mDraw = new DrawOnTopMethod(this);
		addContentView(mDraw, new LayoutParams
		(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
}

    class DrawOnTopMethod extends View {

    	public DrawOnTopMethod(Context context) {
    	super(context);
    	// TODO Auto-generated constructor stub
    	}

    	@Override
    	protected void onDraw(Canvas canvas) {
    	// TODO Auto-generated method stub

    	Paint paint = new Paint();
    	paint.setStyle(Paint.Style.FILL);
    	paint.setColor(Color.RED);
    	paint.setTextAlign(Align.LEFT);
    	paint.setTextSize(24);
    	canvas.drawText("Place bottom of knit here.", 20, 440, paint);

    	super.onDraw(canvas);
    	}

    }