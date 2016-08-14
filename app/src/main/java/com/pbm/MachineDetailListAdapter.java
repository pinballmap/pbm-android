package com.pbm;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

class MachineDetailListAdapter extends ArrayAdapter<Machine> {
	private final TreeMap<Integer, LocationMachineXref> lmxes;
	private final LayoutInflater layoutInflater;
	private List<com.pbm.Machine> filteredMachineList;
	private boolean disableSelectImage;

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
			holder.machineSelectButton = (ImageView) row.findViewById(R.id.machineSelectButton);
			holder.condition = (TextView) row.findViewById(R.id.machine_condition);

			row.setTag(holder);
		} else {
			holder = (MachineViewHolder) row.getTag();
		}

		Machine machine = filteredMachineList.get(position);
        holder.name.setText(Html.fromHtml("<b>" + machine.name + "</b>" + " " + "<i>" + machine.metaData() + "</i>"));
		String conditionText = "";
		LocationMachineXref lmx = lmxes.get(machine.id);
		if (!lmx.condition.equals("null") && !lmx.condition.equals("")) {
			conditionText += lmx.condition;
			if (!lmx.conditionDate.equals("null") && !lmx.condition.equals("")) {
				conditionText += '\n' + getContext().getString(R.string.updated_on) + " " + lmx.conditionDate;
			}

			String lastUpdatedByUsername = lmx.lastUpdatedByUsername;
			if(lastUpdatedByUsername != null && !lastUpdatedByUsername.isEmpty()) {
				conditionText += " by " + lastUpdatedByUsername;
			}

			holder.condition.setText(conditionText);
		} else {
			holder.condition.setVisibility(View.GONE);
		}

		if (disableSelectImage) {
			holder.machineSelectButton.setVisibility(View.INVISIBLE);
		}

		return row;
	}

	class MachineViewHolder {
		TextView name;
		ImageView machineSelectButton;
		TextView condition;
	}

}
