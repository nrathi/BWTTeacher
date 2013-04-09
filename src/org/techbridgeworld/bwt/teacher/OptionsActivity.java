package org.techbridgeworld.bwt.teacher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.widget.Button;

public class OptionsActivity extends Activity {

	private MyApplication application;
	private TextToSpeech tts;
	private Button[] buttons;

	private SensorManager manager;
	private ShakeEventListener listener;
	
	private MediaPlayer player; 
	private String dir; 
	
	private String[] options; 
	private int currentList = 0; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
		application = ((MyApplication) getApplicationContext());
		
		application.prompt = getResources().getString(R.string.options_prompt);
		application.help = getResources().getString(R.string.options_help);

		tts = application.myTTS;
		application.speakOut(application.prompt);
		
		manager = application.myManager;
		listener = application.myListener;
		
		player = new MediaPlayer();
		dir = getApplicationContext().getFilesDir().getPath().toString();
		
		switch (application.category) {
		//Numbers
		case 0:
		case 2:
		case 5:
		case 8:
			options = new String[6];
			options[0] = getResources().getString(R.string.one);
			options[1] = getResources().getString(R.string.two);
			options[2] = getResources().getString(R.string.three);
			options[3] = getResources().getString(R.string.four);
			options[4] = getResources().getString(R.string.five);
			options[5] = getResources().getString(R.string.six);
			break;
		//Letters
		case 3:
		case 6:
		case 9:
			options = new String[30];
			options[0] = getResources().getString(R.string.a);
			options[1] = getResources().getString(R.string.b);
			options[2] = getResources().getString(R.string.c);
			options[3] = getResources().getString(R.string.d);
			options[4] = getResources().getString(R.string.e);
			options[5] = getResources().getString(R.string.next_items);

			options[6] = getResources().getString(R.string.f);
			options[7] = getResources().getString(R.string.g);
			options[8] = getResources().getString(R.string.h);
			options[9] = getResources().getString(R.string.i);
			options[10] = getResources().getString(R.string.j);
			options[11] = getResources().getString(R.string.next_items);
			
			options[12] = getResources().getString(R.string.k);
			options[13] = getResources().getString(R.string.l);
			options[14] = getResources().getString(R.string.m);
			options[15] = getResources().getString(R.string.n);
			options[16] = getResources().getString(R.string.o);
			options[17] = getResources().getString(R.string.next_items);
			
			options[18] = getResources().getString(R.string.p);
			options[19] = getResources().getString(R.string.q);
			options[20] = getResources().getString(R.string.r);
			options[21] = getResources().getString(R.string.s);
			options[22] = getResources().getString(R.string.t);
			options[23] = getResources().getString(R.string.next_items);
			
			options[24] = getResources().getString(R.string.u);
			options[25] = getResources().getString(R.string.v);
			options[26] = getResources().getString(R.string.w);
			options[27] = getResources().getString(R.string.x);
			options[28] = getResources().getString(R.string.y);
			options[29] = getResources().getString(R.string.z);
			break;
		//Learn Dots, Phrases
		case 1:
			options = new String[3];
			options[0] = getResources().getString(R.string.find_dot);
			options[1] = getResources().getString(R.string.good);
			options[2] = getResources().getString(R.string.no);
			break;	
		//Learn Letters, Phrases
		case 4:
			options = new String[5];
			options[0] = getResources().getString(R.string.good);
			options[1] = getResources().getString(R.string.no);
			options[2] = getResources().getString(R.string.please_press);
			options[3] = getResources().getString(R.string.please_write);
			options[4] = getResources().getString(R.string.to_write_the_letter);
			break;
		//Animal Game, Phrases
		case 7: 
			options = new String[9];
			options[0] = getResources().getString(R.string.good);
			options[1] = getResources().getString(R.string.invalid_input);
			options[2] = getResources().getString(R.string.no);
			options[3] = getResources().getString(R.string.please_write);
			options[4] = getResources().getString(R.string.please_write_the_name);
			options[5] = getResources().getString(R.string.next_items);
			
			options[6] = getResources().getString(R.string.press);
			options[7] = getResources().getString(R.string.the_correct_answer_was);
			options[8] = getResources().getString(R.string.to_write_the_letter);
			break;
		//Hangman, Phrases
		case 10: 
			options = new String[15];
			options[0] = getResources().getString(R.string.but_you_have); 
			options[1] = getResources().getString(R.string.dash);
			options[2] = getResources().getString(R.string.good);
			options[3] = getResources().getString(R.string.guess_a_letter);
			options[4] = getResources().getString(R.string.invalid_input);
			options[5] = getResources().getString(R.string.next_items);
			
			options[6] = getResources().getString(R.string.letters);
			options[7] = getResources().getString(R.string.mistake);
			options[8] = getResources().getString(R.string.mistakes);
			options[9] = getResources().getString(R.string.no);
			options[10] = getResources().getString(R.string.so_far);
			options[11] = getResources().getString(R.string.next_items);
			
			options[12] = getResources().getString(R.string.the_new_word);
			options[13] = getResources().getString(R.string.youve_already);
			options[14] = getResources().getString(R.string.youve_made);
			break;
		//Hangman, Words
		case 11: 
			ArrayList<String> arr = application.hangmanWords;
			options = new String[arr.size() + arr.size()/6];
			if(arr != null) {
				int optCount = 0;
				for (int i = 0; i < arr.size(); i++) {
					if((optCount+1) % 6 == 0) {
						options[optCount] = getResources().getString(R.string.next_items);
						i--;
					}
					else {
						options[optCount] = arr.get(i);
					}
					optCount++;
				}
			}
			break;
		default:
			options = null; 
		}
		
		buttons = new Button[6];
		buttons[0] = (Button) findViewById(R.id.one);
		buttons[1] = (Button) findViewById(R.id.two);
		buttons[2] = (Button) findViewById(R.id.three);
		buttons[3] = (Button) findViewById(R.id.four);
		buttons[4] = (Button) findViewById(R.id.five);
		buttons[5] = (Button) findViewById(R.id.six);
		
		makeButtons();
	}

	private void makeButtons() {
		for(int i = 0; i < buttons.length; i++) {
			final int j = 6 * currentList + i; 
			if(j < options.length) {
				buttons[i].setText(options[j]);
				buttons[i].setContentDescription(options[j]);
				buttons[i].setVisibility(View.VISIBLE);
				buttons[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						application.option = options[j];
						Intent intent = new Intent(OptionsActivity.this, RecordActivity.class);
						startActivity(intent);
					}
				});
				buttons[i].setOnHoverListener(new OnHoverListener() {
					@Override
					public boolean onHover(View v, MotionEvent event) {
						if (!player.isPlaying()) {
							FileInputStream fis;
							String filename = options[j].replaceAll(" ", "_");
								try {
									fis = new FileInputStream(dir + "/" + filename + ".m4a");
									player.reset();
									player.setDataSource(fis.getFD());
									fis.close();
									player.prepare();
									player.start();
								} catch (FileNotFoundException e) {
									application.speakOut(options[j]);
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalStateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						}
						return true;
					}
				});
			}
			else
				buttons[i].setVisibility(View.INVISIBLE);
		}
		
		if(buttons[5].getText() == getResources().getString(R.string.next_items)) {
			buttons[5].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					currentList++; 
					makeButtons(); 
				}
			}); 
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		manager.registerListener(listener,
				manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		manager.unregisterListener(listener);
		tts.stop();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}
	
	// If the user presses back, go to the previous list if applicable
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(currentList > 0) {
				currentList--;
				makeButtons();
				return true; 
			}
			else {
				Intent intent = new Intent(OptionsActivity.this, CategoryActivity.class);
				startActivity(intent);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
}