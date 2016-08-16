package com.pbm;

import android.content.Context;
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

/**
 * Copyright (c) 2015, Brian Dols <brian.dols@gmail.com>
 * <p/>
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
public class ConditionsArrayAdapter extends ArrayAdapter<Condition> implements OnTaskCompleted {
	private final LayoutInflater inflater;
	private final Context context;

	public ConditionsArrayAdapter(Context context, LayoutInflater inflater, List<Condition> conditions) {
		super(context, R.layout.machine_condition_entry, conditions);
		this.context = context;
		this.inflater = inflater;
		Log.d("com.pbm", "timezone is " + TimeZone.getDefault());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Condition condition = getItem(position);
		convertView = inflater.inflate(R.layout.machine_condition_entry, parent, false);
		TextView conditionText = (TextView) convertView.findViewById(R.id._condition);
		conditionText.setText(condition.getDescription());
		TextView conditionDate = (TextView) convertView.findViewById(R.id._condition_date);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
		format.setTimeZone(TimeZone.getDefault());
		conditionDate.setText(format.format(condition.getDate()));

		TextView usernameTextView = (TextView) convertView.findViewById(R.id._username);
		String username = condition.getUsername();
		if(username != null && !username.isEmpty()) {
			usernameTextView.setText("by " + condition.getUsername());
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
