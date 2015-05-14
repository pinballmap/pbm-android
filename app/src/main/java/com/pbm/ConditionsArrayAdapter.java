package com.pbm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Copyright (c) 2015, Brian Dols <brian.dols@gmail.com>
 * <p/>
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
public class ConditionsArrayAdapter extends ArrayAdapter<Condition> {
	private final LayoutInflater inflater;

	public ConditionsArrayAdapter(Context context, LayoutInflater inflater, List<Condition> conditions) {
		super(context, R.layout.machine_condition_entry, conditions);
		this.inflater = inflater;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Condition condition = getItem(position);
		convertView = inflater.inflate(R.layout.machine_condition_entry, parent, false);
		TextView conditionText = (TextView) convertView.findViewById(R.id._condition);
		conditionText.setText(condition.getDescription());
		TextView conditionDate = (TextView) convertView.findViewById(R.id._condition_date);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
		conditionDate.setText(format.format(condition.getDate()));
		return convertView;
	}
}
