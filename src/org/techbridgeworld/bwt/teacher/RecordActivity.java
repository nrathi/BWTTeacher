package org.techbridgeworld.bwt.teacher;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

public class RecordActivity extends Activity implements TextToSpeech.OnInitListener {

	private TextToSpeech tts;
	
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetectorCompat detector; 
	
	private String record_prompt;
	private TextView teacher_record;
	
	private int category;
	private String[] options;
	private int numOptions; 
	private int currentOption = 0; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_record);
		
		Bundle extras = getIntent().getExtras();
		category = (Integer) extras.get("category");
		Log.i("neha", "category = " + category);
		
		record_prompt = getResources().getString(R.string.record_prompt);
		teacher_record = (TextView) findViewById(R.id.teacher_record);
		
		switch(category) {
			case 0:
				record_prompt = record_prompt.replace("xxx", "number");
				teacher_record.setText("one");
				teacher_record.setContentDescription("one"); 
				
				numOptions = 6;
				options = new String[numOptions]; 
				options[0] = getResources().getString(R.string.one);
				options[1] = getResources().getString(R.string.two);
				options[2] = getResources().getString(R.string.three);
				options[3] = getResources().getString(R.string.four);
				options[4] = getResources().getString(R.string.five);
				options[5] = getResources().getString(R.string.six);
				break;
			case 1:
				record_prompt = record_prompt.replace("xxx", "letter");
				teacher_record.setText("a");
				teacher_record.setContentDescription("a"); 
				
				numOptions = 26;
				options = new String[numOptions]; 
				options[0] = getResources().getString(R.string.a);
				options[1] = getResources().getString(R.string.b);
				options[2] = getResources().getString(R.string.c);
				options[3] = getResources().getString(R.string.d);
				options[4] = getResources().getString(R.string.e);
				options[5] = getResources().getString(R.string.f);
				options[6] = getResources().getString(R.string.g);
				options[7] = getResources().getString(R.string.h);
				options[8] = getResources().getString(R.string.i);
				options[9] = getResources().getString(R.string.j);
				options[10] = getResources().getString(R.string.k);
				options[11] = getResources().getString(R.string.l);
				options[12] = getResources().getString(R.string.m);
				options[13] = getResources().getString(R.string.n);
				options[14] = getResources().getString(R.string.o);
				options[15] = getResources().getString(R.string.p);
				options[16] = getResources().getString(R.string.q);
				options[17] = getResources().getString(R.string.r);
				options[18] = getResources().getString(R.string.s);
				options[19] = getResources().getString(R.string.t);
				options[20] = getResources().getString(R.string.u);
				options[21] = getResources().getString(R.string.v);
				options[22] = getResources().getString(R.string.w);
				options[23] = getResources().getString(R.string.x);
				options[24] = getResources().getString(R.string.y);
				options[25] = getResources().getString(R.string.z);
				break;
			case 2:
				record_prompt = record_prompt.replace("xxx", "phrase");
				teacher_record.setText("Dot Practice");
				teacher_record.setContentDescription("Dot Practice"); 
				
				numOptions = 15; 
				options = new String[numOptions]; 
				options[0] = getResources().getString(R.string.dot_practice);
				options[1] = getResources().getString(R.string.find_dot);
				options[2] = getResources().getString(R.string.free_play);
				options[3] = getResources().getString(R.string.free_spelling);
				options[4] = getResources().getString(R.string.good);
				options[5] = getResources().getString(R.string.learn_dots);
				options[6] = getResources().getString(R.string.learn_letters);
				options[7] = getResources().getString(R.string.letter_practice);
				options[8] = getResources().getString(R.string.no);
				options[9] = getResources().getString(R.string.now_press);
				options[10] = getResources().getString(R.string.please_press);
				options[11] = getResources().getString(R.string.please_write);
				options[12] = getResources().getString(R.string.press);
				options[13] = getResources().getString(R.string.to_write_the_letter);
				options[14] = getResources().getString(R.string.try_again);
				break;
			default:
		}
		
		tts = new TextToSpeech(this, this);
		detector = new GestureDetectorCompat(this, new MyGestureListener());
	}
	
	@Override 
	public boolean onTouchEvent(MotionEvent event){ 
		this.detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public void onInit(int status) {	
		Log.i("neha", "init, record_prompt = " + record_prompt); 
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
				Log.e("TTS", "This language is not supported");
			else
				speakOut(record_prompt);
		}
		else
			Log.e("TTS", "Initilization Failed!");
	}
	
	private void speakOut(String text) {
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
			// Swipe up
			if (event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				Intent intent = new Intent(RecordActivity.this, CategoryActivity.class);
				startActivity(intent);
			}
			
			// Swipe left
			else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				currentOption = (currentOption - 1) % options.length; 
				if(currentOption == -1) 
					currentOption += options.length;
				teacher_record.setText(options[currentOption]);
				teacher_record.setContentDescription(options[currentOption]);
			}
			
			// Swipe right
			else {
				currentOption = (currentOption + 1) % options.length; 
				teacher_record.setText(options[currentOption]);
				teacher_record.setContentDescription(options[currentOption]);
			}

			return true;
		}
	}
}