package com.vitalgroup;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class Annotate extends Activity {
	private byte[] byteArray;
	private Bitmap bitmap;
	private View bitmapView; 
	private Intent returnIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set screen orientation only portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		byteArray  = getIntent().getByteArrayExtra("image");
		setContentView(new ImageEditor(this, byteArray));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.annotate_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.menu_cancel_annotate:
			returnIntent = new Intent();
			setResult(RESULT_CANCELED, returnIntent);
			finish();
			break;
			
		case R.id.menu_reset_annotate:
			setContentView(new ImageEditor(this, byteArray));
			break;
			
		case R.id.menu_save_annotate:
			bitmapView = this.getWindow().getDecorView().findViewById(android.R.id.content);
			bitmap = getViewBitmap(bitmapView);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] bitmapArray = stream.toByteArray();
			returnIntent = new Intent();
			returnIntent.putExtra("image", bitmapArray);
			setResult(RESULT_OK, returnIntent);
			finish();
			break;
		}
		
		return true;
	}
	
	private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e("Bitmap", "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

}


