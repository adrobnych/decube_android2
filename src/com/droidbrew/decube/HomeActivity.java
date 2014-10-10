package com.droidbrew.decube;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.droidbrew.decube.adapter.CheckBoxAdapter;
import com.droidbrew.decube.fragments.CheckQuestionFragment;
import com.droidbrew.decube.fragments.ResultFragment;
import com.droidbrew.decube.model.Answer;
import com.droidbrew.decube.model.AnswerManager;

public class HomeActivity extends FragmentActivity implements
		SensorEventListener {

	private ResultFragment resFragment;
	private ResultFragment resFragment2;
	private CheckQuestionFragment checkFragment;
	private FragmentManager fManager;
	private FragmentTransaction fTrans;
	private ImageView imgCube;
	private CheckBoxAdapter checkAdapter;
	private SensorManager sensorManager;
	private long lastUpdate;
	private AnswerManager answerManager;
	private AnimationDrawable animCube;
	private boolean isFragment1 = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		int currentAPIVersion = android.os.Build.VERSION.SDK_INT;
		if (currentAPIVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		sensorManager = (SensorManager) this
				.getSystemService(Activity.SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		lastUpdate = System.currentTimeMillis();

		fManager = getSupportFragmentManager();
		answerManager = ((DecubeApp) getApplication()).getAnswerManager();

		resFragment = new ResultFragment();
		resFragment2 = new ResultFragment();
		checkFragment = new CheckQuestionFragment();
		fTrans = fManager.beginTransaction();
		fTrans.add(R.id.container_one, checkFragment);
		fTrans.show(checkFragment);
		fTrans.add(R.id.container_one, resFragment);
		fTrans.hide(resFragment);
		fTrans.add(R.id.container_one, resFragment2);
		fTrans.hide(resFragment2);
		fTrans.commit();
		imgCube();
		animCube = (AnimationDrawable) imgCube.getBackground();
	}
	
	 protected void onPause() {
         super.onPause();
         sensorManager.unregisterListener(this);
     }

	private void onClickCube() {
		
		if(checkAdapter.getState().size() == 0) {
			new AlertDialog.Builder(this)
		    .setTitle("BOOM!")
		    .setMessage("You should select at least one question !!!")
		    .setIcon(android.R.drawable.ic_dialog_alert)
		    .setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            })
		    .show();
		} else {
			fTrans = fManager.beginTransaction();
			fTrans.hide(checkFragment);
			fTrans.setCustomAnimations(R.animator.slide_in_right,
					R.animator.slide_in_right);
			if(isFragment1) {
				fTrans.hide(resFragment);
				fTrans.show(resFragment2);
				isFragment1 = false;
			} else { 
				fTrans.hide(resFragment2);
				fTrans.show(resFragment);
				isFragment1 = true;
			}
			fTrans.commit();
		}
		
	}

	public void imgCube() {
		imgCube = (ImageView) findViewById(R.id.img_cube);
		imgCube.setBackgroundResource(R.animator.anim_cube);
		imgCube.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ListView lv = (ListView) checkFragment.getView().findViewById(R.id.question_list);
				checkAdapter = (CheckBoxAdapter) lv.getAdapter();
				int[] ids = new int[checkAdapter.getState().size()];
				int a = 0;
				List<Answer> randomList = new ArrayList<Answer>();
				for (int i : checkAdapter.getState().keySet()) {
					ids[a] = checkAdapter.getItem(i).getId();
					a++;
				}
				resFragment.setId(ids);
				//onClickCube();
				spinStart();
			}
		});
	}

	public void spinStart() {
		animCube.stop();
		animCube.start();
		ListView lv = (ListView) checkFragment.getView().findViewById(R.id.question_list);
		checkAdapter = (CheckBoxAdapter) lv.getAdapter();
		int[] ids = new int[checkAdapter.getState().size()];
		int a = 0;
		List<Answer> randomList = new ArrayList<Answer>();
		for (int i : checkAdapter.getState().keySet()) {
			ids[a] = checkAdapter.getItem(i).getId();
			a++;
		}
		resFragment.setId(ids);
		resFragment2.setId(ids);
		onClickCube();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_question:
			Intent intent = new Intent(this, QuestionActivity.class);
			startActivity(intent);
			overridePendingTransition(R.animator.left_slide_show, R.animator.left_slide_hide);
			finish();
			break;
		case android.R.id.home:
			fTrans = fManager.beginTransaction();
			fTrans.hide(resFragment);
			fTrans.setCustomAnimations(R.animator.slide_in_right,
					R.animator.slide_in_right);
			fTrans.show(checkFragment);
			fTrans.commit();
			break;
		}
		return true;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float[] values = event.values;
			float x = values[0];
			float y = values[1];
			float z = values[2];
			float accelationSquareRoot = (x * x + y * y + z * z)
					/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
			long actualTime = System.currentTimeMillis();
			if (accelationSquareRoot >= 3) {
				if (actualTime - lastUpdate < 1300) {
					return;
				}
				lastUpdate = actualTime;
				spinStart();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	
}
