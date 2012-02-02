package ca.joshadam;

import android.app.Activity;
import android.os.Bundle;

public class A2_P1Activity extends Activity {
    private A2P1Canvas canvas;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		canvas = new A2P1Canvas(this);
		setContentView(canvas);
		canvas.requestFocus();
    }
}