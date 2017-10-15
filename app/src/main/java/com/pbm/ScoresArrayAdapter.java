package com.pbm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.text.MessageFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

class ScoresArrayAdapter extends ArrayAdapter<MachineScore> implements OnTaskCompleted {
	private final LayoutInflater inflater;
	private final Context context;

	ScoresArrayAdapter(Context context, LayoutInflater inflater, List<MachineScore> scores) {
		super(context, R.layout.machine_score_entry, scores);
		this.context = context;
		this.inflater = inflater;
		Log.d("com.pbm", "timezone is " + TimeZone.getDefault());
	}

	@SuppressLint("ViewHolder")
	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		final MachineScore score = getItem(position);
		convertView = inflater.inflate(R.layout.machine_score_entry, parent, false);
		TextView scoreText = convertView.findViewById(R.id._score);
		if (score != null) {
			scoreText.setText(MessageFormat.format("{0}", score.getScore()));
		}
		TextView scoreDate = convertView.findViewById(R.id._score_date);
		if (score != null) {
			scoreDate.setText(score.getDate());
		}

		TextView usernameTextView = convertView.findViewById(R.id._username);
		String username = null;
		if (score != null) {
			username = score.getUsername();
		}
		if(username != null && !username.isEmpty()) {
			usernameTextView.setText(String.format("by %s", score.getUsername()));
		} else {
			usernameTextView.setText("");
		}

		return convertView;
	}

	public void onTaskCompleted(String results) throws JSONException, InterruptedException, ExecutionException {
		((PinballMapActivity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
			Toast.makeText(context, "OK, thank you.", Toast.LENGTH_LONG).show();
			}
		});
	}
}
