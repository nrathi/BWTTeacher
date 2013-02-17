package org.techbridgeworld.bwt.teacher;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PasswordActivity extends Activity implements TextToSpeech.OnInitListener {
 
	private TextToSpeech tts;
	private Vibrator vibrator;
	
	private Button one, two, three, four;
	private String password_prompt; 
	private String actualPassword = "1123";
	private String enteredPassword = ""; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_password);
		
		one = (Button) findViewById(R.id.one);
		one.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				enteredPassword += "1"; 
			}	
		});
		
		two = (Button) findViewById(R.id.two);
		two.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				enteredPassword += "2"; 
			}	
		});
		
		three = (Button) findViewById(R.id.three);
		three.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				enteredPassword += "3"; 
			}	
		});
		
		four = (Button) findViewById(R.id.four);
		four.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				enteredPassword += "4"; 
			}	
		});
		
		password_prompt = getResources().getString(R.string.password_prompt);

		tts = new TextToSpeech(this, this);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		final Handler handler = new Handler();
		final Runnable runnable = new Runnable()
		{
		    @Override
			public void run() 
		    {
		        if(enteredPassword.equals(actualPassword)) {
		        	speakOut("Correct."); 
					Intent intent = new Intent(PasswordActivity.this, HomeActivity.class);
					startActivity(intent);
		        }
		        else if(enteredPassword.length() >= 4) {
		        	speakOut("Incorrect.");
		        	int dot = 200;
		        	long[] pattern = {0, dot, dot, dot}; 
		        	vibrator.vibrate(pattern, -1);
		        	enteredPassword = ""; 
		        	handler.postDelayed(this, 500); 
		        }
		        else
		        	handler.postDelayed(this, 500);
		    }
		};

		handler.postDelayed(runnable, 500);
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
				Log.e("TTS", "This language is not supported");
			else
				speakOut(password_prompt);
		}
		else
			Log.e("TTS", "Initilization Failed!");
	}
	
	private void speakOut(String text) {
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}
}