package com.droidbrew.decube.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.droidbrew.decube.DecubeApp;
import com.droidbrew.decube.HomeActivity;
import com.droidbrew.decube.R;
import com.droidbrew.decube.adapter.CheckBoxAdapter;
import com.droidbrew.decube.model.Answer;
import com.droidbrew.decube.model.AnswerManager;
import com.droidbrew.decube.model.QuestionManager;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class CheckQuestionFragment extends Fragment{
	private QuestionManager questionManager;
	private AnswerManager answerManager;
	private CheckBoxAdapter checkAdapter;
	private ListView list;
	private Activity activity;
	private DecubeApp decubeApp;
	public static final String TAG = "CheckQuestionFragment";
	private FragmentManager fManager;
	private FragmentTransaction fTrans;
	private ResultFragment resFragment;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_check_question, container, false);
		list = (ListView) view.findViewById(R.id.question_list);
		
		activity = getActivity();
        decubeApp = (DecubeApp) activity.getApplication();
        
		questionManager = ((DecubeApp) activity.getApplication()).getQuestionManager();
		answerManager = ((DecubeApp) activity.getApplication()).getAnswerManager();
		fManager = getActivity().getSupportFragmentManager();
		resFragment = new ResultFragment();
		
		try {
			checkAdapter = new CheckBoxAdapter(
					activity.getApplicationContext(), questionManager.getDataQuestion());
		} catch (SQLException e) {
			Log.e(HomeActivity.class.getName(), e.getMessage(), e);
		}

		
		list.setAdapter(checkAdapter);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		return view;
	}


}
