package org.techbridgeworld.bwt.teacher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RecordActivity extends Activity {

	private MyApplication application;
	private TextToSpeech tts;
	private Button[] buttons;

	private SensorManager manager;
	private ShakeEventListener listener;
	
	private MediaRecorder recorder;
	private MediaPlayer player;
	private boolean isRecording;
	private boolean hasRecorded; 
	
	private Context context;
	private String dir;
	private String filename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		application = ((MyApplication) getApplicationContext());
		
		application.prompt = getResources().getString(R.string.record_prompt);
		application.help = getResources().getString(R.string.record_help);

		recorder = new MediaRecorder();
		player = new MediaPlayer();
		isRecording = false;
		hasRecorded = false;
		
		context = getApplicationContext();
		dir = context.getFilesDir().getPath().toString();
		
		String options[] = new String[3];
		options[0] = getResources().getString(R.string.playback);
		options[1] = getResources().getString(R.string.save);
		options[2] = getResources().getString(R.string.cancel);
		
		buttons = new Button[3];
		
		//Playback
		buttons[0] = (Button) findViewById(R.id.one);
		buttons[0].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!player.isPlaying()) {
					FileInputStream fis;
					filename = application.option.replaceAll(" ", "_");
						try {
							if(!hasRecorded)
								fis = new FileInputStream(dir + "/" + filename + ".m4a");
							else
								fis = new FileInputStream(dir + "/" + filename + "_temp.m4a");
							player.reset();
							player.setDataSource(fis.getFD());
							fis.close();
							player.prepare();
							player.start();
						} catch (FileNotFoundException e) {
							application.speakOut(application.option);
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
			}
		}); 
		
		//Save
		buttons[1] = (Button) findViewById(R.id.two);
		buttons[1].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				context.deleteFile(filename + ".m4a");
				File oldFile = context.getFileStreamPath(filename + "_temp.m4a");
				File newFile = context.getFileStreamPath(filename + ".m4a");
				oldFile.renameTo(newFile);
	
				Intent intent = new Intent(RecordActivity.this, OptionsActivity.class);
				startActivity(intent);
			}
		});
		
		//Cancel
		buttons[2] = (Button) findViewById(R.id.three);
		buttons[2].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				context.deleteFile(filename + "_temp.m4a");
	
				Intent intent = new Intent(RecordActivity.this, OptionsActivity.class);
				startActivity(intent);
			}
		});
		
		for(int i = 0; i < options.length; i++) {
			buttons[i].setText(options[i]);
			buttons[i].setContentDescription(options[i]);
			buttons[i].setVisibility(View.VISIBLE);
		}
		
		manager = application.myManager;
		listener = application.myListener;
		
		tts = application.myTTS;
		application.speakOut(application.prompt);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
	    int action = event.getAction();
	    if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            if (action == KeyEvent.ACTION_DOWN && isRecording == false) {
            	Log.i("neha", "action down?"); 
				filename = application.option.replaceAll(" ", "_");
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				recorder.setOutputFile(dir + "/" + filename + "_temp.m4a");
				try {
					recorder.prepare();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				recorder.start();
				if(!hasRecorded)
					hasRecorded = true; 
				isRecording = true; 
            }
            else if(action == KeyEvent.ACTION_UP && isRecording == true) {
            	Log.i("neha", "action up?"); 
				recorder.reset();
				isRecording = false; 
            }
            return true;
	    }
	    return super.dispatchKeyEvent(event);
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
}