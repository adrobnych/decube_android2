package com.droidbrew.decube;

import java.sql.SQLException;

import com.droidbrew.decube.adapter.QuestionAdapter;
import com.droidbrew.decube.model.Question;
import com.droidbrew.decube.model.QuestionManager;
import com.j256.ormlite.dao.Dao;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;


public class QuestionActivity extends Activity {
	QuestionManager questionManager;
	QuestionAdapter questionAdapter;
	ListView questionList;
	private Dao<Question, Integer> questionDao = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question);

		int currentAPIVersion = android.os.Build.VERSION.SDK_INT;
		if (currentAPIVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		questionManager = ((DecubeApp) getApplication()).getQuestionManager();
		questionDao = ((DecubeApp) getApplication()).getQuestionManager()
				.getQuestionDao();

		try {
			questionAdapter = new QuestionAdapter(getApplicationContext(),
					questionManager.getDataQuestion());
		} catch (SQLException e) {
			Log.e(QuestionActivity.class.getName(), e.getMessage(), e);
		}

		questionList = (ListView) findViewById(R.id.question_edit_list);
		questionList.setAdapter(questionAdapter);
		questionList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				Intent intent = new Intent(QuestionActivity.this,
						EditQuestionActivity.class);
				intent.putExtra("questionId", (int) id);
				startActivity(intent);
				overridePendingTransition(R.animator.left_slide_show, R.animator.left_slide_hide);
				finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, QuestionActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_question, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_question_edit_add:
			AddQuestionDialog();
			break;
		case android.R.id.home:
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
			overridePendingTransition(R.animator.back_slide_show, R.animator.back_slide_hide);
			finish();
			break;
		}
		return true;
	}

	public void AddQuestionDialog() {
		final EditText questionEditText = new EditText(this);
		questionEditText
				.setHint(getString(R.string.questionEdit_activity_dialogAdd_hint));
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(getString(R.string.questionEdit_activity_dialogAdd_title));
		dialog.setMessage(getString(R.string.questionEdit_activity_dialogAdd_masage));
		dialog.setView(questionEditText);
		dialog.setPositiveButton(
				getString(R.string.questionEdit_activity_dialogAdd_pBut),
				new DialogInterface.OnClickListener() {
					@TargetApi(Build.VERSION_CODES.HONEYCOMB)
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (questionEditText.getText().toString().equals("")) {
							return;
						}
						try {
							String str = questionEditText.getText().toString();
							if (!str.substring(str.length() - 1, str.length())
									.equals("?"))
								str += "?";
							questionDao.create(new Question(str));
						} catch (SQLException e) {
							e.printStackTrace();
						}
						Intent intent = new Intent(QuestionActivity.this,
								QuestionActivity.class);
						startActivity(intent);
						finish();
					}
				});
		dialog.setNegativeButton(
				getString(R.string.questionEdit_activity_dialogAdd_nBut),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}
}
