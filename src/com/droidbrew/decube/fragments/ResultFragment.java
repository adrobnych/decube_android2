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
import com.droidbrew.decube.model.Question;
import com.droidbrew.decube.model.QuestionManager;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;

public class ResultFragment extends Fragment{
	
	private Activity activity;
	private DecubeApp decubeApp;
	private QuestionManager questionManager;
	private AnswerManager answerManager;
	private CheckBoxAdapter adapter;
	private ListView list = null;
	private List<Answer> randomList;
	private Answer[] arrayRandom;
	private WebView wb;
	private int random;
	private int[] ids;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_result, container, false);
		
		activity = getActivity();
        decubeApp = (DecubeApp) activity.getApplication();
        
        questionManager = ((DecubeApp) activity.getApplication()).getQuestionManager();
		answerManager = ((DecubeApp) activity.getApplication()).getAnswerManager();
        
		wb = ((WebView) view.findViewById(R.id.result));
		wb.setBackgroundColor(Color.TRANSPARENT);
		String summary = "<html><body style='background-color:transparent'> <br/><br/><br/> <h2 align='center'>"
					+ "</h2> </body></html>";
		wb.loadDataWithBaseURL(null, summary, "text/html", "utf-8", null);
		randomList = new ArrayList<Answer>();
		
		return view;
	}
	
	public void setId(int[] ids) {
		this.ids = ids;
		randomResult();
	}
	
	public void randomResult() {
		String str = "<html><body align='left' style='background-color:transparent'> <h3>";
		try {
			for (int i : ids) {
				Question question = questionManager.findQuestionById(i);
				str += "<img src='file:///android_res/drawable/question_ball.png'>" + question.getQuestion() + "<br />";
				randomList = answerManager.findAnswerByQuestionId(i);
				if(randomList.size() == 0){
					List<Answer> emptyAnswers = new ArrayList<Answer>();
					emptyAnswers.add(new Answer(i, "question witout answers!?"));
					randomList.addAll(emptyAnswers);
				}
					
				random = (int) (Math.random() * randomList.size());
				str += "<img src='file:///android_res/drawable/answer_ball.png'>" + randomList.get(random).getAnswer() + "<br />";
			}
		} catch (Exception ex) {
			Log.e(HomeActivity.class.getName(), ex.getMessage(), ex);
		}
		str += "</h3></body></html>";
		wb.setBackgroundColor(Color.TRANSPARENT);
		wb.loadDataWithBaseURL(null, str, "text/html", "utf-8", null);
		//wb.startAnimation(anim);
	}

}
