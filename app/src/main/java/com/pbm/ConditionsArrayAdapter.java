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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

class ConditionsArrayAdapter extends ArrayAdapter<Condition> implements OnTaskCompleted {
	private final LayoutInflater inflater;
	private final Context context;

	ConditionsArrayAdapter(Context context, LayoutInflater inflater, List<Condition> conditions) {
		super(context, R.layout.machine_condition_entry, conditions);
		this.context = context;
		this.inflater = inflater;
		Log.d("com.pbm", "timezone is " + TimeZone.getDefault());
	}

	@SuppressLint("ViewHolder")
	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		final Condition condition = getItem(position);
		convertView = inflater.inflate(R.layout.machine_condition_entry, parent, false);
		TextView conditionText = convertView.findViewById(R.id._condition);

		if (condition != null) {
			conditionText.setText(condition.getDescription());
		}

		TextView conditionDate = convertView.findViewById(R.id._condition_date);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
		format.setTimeZone(TimeZone.getDefault());

		if (condition != null) {
			conditionDate.setText(format.format(condition.getDate()));
		}

		TextView usernameTextView = convertView.findViewById(R.id._username);
		String username = null;
		if (condition != null) {
			username = condition.getUsername();
		}
		if(username != null && !username.isEmpty()) {
			usernameTextView.setText(String.format("by %s", condition.getUsername()));
		} else {
			usernameTextView.setText("");
		}

		return convertView;
	}

	public void onTaskCompleted(String results) throws JSONException, InterruptedException, ExecutionException {
		((PinballMapActivity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
			Toast.makeText(context, "OK, machine deleted.", Toast.LENGTH_LONG).show();
			}
		});
	}
}
