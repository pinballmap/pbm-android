package com.pbm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Copyright (c) 2015, Brian Dols <brian.dols@gmail.com>
 * <p/>
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
class MachineDetailListAdapter extends ArrayAdapter<Machine> {
	private final TreeMap<Integer, LocationMachineXref> lmxes;
	private final LayoutInflater layoutInflater;
	private List<com.pbm.Machine> filteredMachineList;
	private boolean disableSelectImage;
	private Filter filter;

	public MachineDetailListAdapter(Context context, List<Machine> machines, TreeMap<Integer, LocationMachineXref> lmxes) {
		super(context, R.layout.machine_condition_listview, machines);
		layoutInflater = LayoutInflater.from(context);
		this.filteredMachineList = new ArrayList<Machine>(machines);
		this.lmxes = lmxes;
		this.disableSelectImage = false;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		MachineViewHolder holder;
		View row = convertView;

		if (row == null) {
			row = layoutInflater.inflate(R.layout.machine_condition_listview, parent, false);
			holder = new MachineViewHolder();
			holder.name = (TextView) row.findViewById(R.id.machine_info);
//			holder.metaData = (TextView) row.findViewById(R.id.metaData);
			holder.machineSelectButton = (ImageView) row.findViewById(R.id.machineSelectButton);
			holder.condition = (TextView) row.findViewById(R.id.machine_condition);

			row.setTag(holder);
		} else {
			holder = (MachineViewHolder) row.getTag();
		}

		Machine machine = filteredMachineList.get(position);
		holder.name.setText(machine.name + " " + machine.metaData());
//		holder.metaData.setText(machine.metaData());
		String conditionText = "";
		if (!lmxes.get(machine.id).condition.equals("null") && !lmxes.get(machine.id).condition.equals("")) {
			conditionText += lmxes.get(machine.id).condition;
			if (!lmxes.get(machine.id).conditionDate.equals("null") && !lmxes.get(machine.id).condition.equals("")) {
				conditionText += '\n' + getContext().getString(R.string.updated_on) + " " + lmxes.get(machine.id).conditionDate;
			}
		}
		holder.condition.setText(conditionText);

		if (disableSelectImage) {
			holder.machineSelectButton.setVisibility(View.INVISIBLE);
		}

		return row;
	}

	class MachineViewHolder {
		TextView name;
		TextView metaData;
		ImageView machineSelectButton;
		TextView condition;
	}

}
