package com.pbm;

import android.annotation.SuppressLint;
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

public class MachineListAdapter extends ArrayAdapter<com.pbm.Machine> {
	private List<com.pbm.Machine> machines;
	private List<com.pbm.Machine> filteredMachineList;
	private boolean disableSelectImage;
	private Filter filter;

	public MachineListAdapter(Context context, List<com.pbm.Machine> machines, boolean disableSelectImage) {
		super(context, R.layout.machine_list_listview, machines);

		this.machines = new ArrayList<Machine>(machines);
		this.filteredMachineList = new ArrayList<Machine>(machines);

		this.disableSelectImage = disableSelectImage;
	}

	@SuppressLint("InflateParams")
	public View getView(final int position, View convertView, ViewGroup parent) {
		MachineViewHolder holder;
		View row = convertView;

		if (row == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = layoutInflater.inflate(R.layout.machine_list_listview, null);

			holder = new MachineViewHolder();
			holder.name = (TextView) row.findViewById(R.id.machine_info);
			holder.metaData = (TextView) row.findViewById(R.id.metaData);
			holder.machineSelectButton = (ImageView) row.findViewById(R.id.machineSelectButton);
//			holder.condition = (TextView) row.findViewById(R.id.machine_condition);

			row.setTag(holder);
		} else {
			holder = (MachineViewHolder) row.getTag();
		}

		Machine machine = filteredMachineList.get(position);
		holder.name.setText(machine.name);
		holder.metaData.setText(machine.metaData());
//		holder.condition.setText(machine.getCondition());

		if (disableSelectImage) {
			holder.machineSelectButton.setVisibility(View.INVISIBLE);
		}

		return row;
	}

	class MachineViewHolder {
		TextView name;
		TextView metaData;
		ImageView machineSelectButton;
//		TextView condition;
	}

	public Filter getFilter() {
		if (filter == null) {
			filter = new MachineFilter();
		}

		return filter;
	}

	@SuppressLint("DefaultLocale")
	private class MachineFilter extends Filter {
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			String filter = constraint.toString().toLowerCase();

			if (constraint == null || constraint.length() == 0) {
				ArrayList<com.pbm.Machine> list = new ArrayList<com.pbm.Machine>(machines);
				results.values = list;
				results.count = list.size();
			} else {
				ArrayList<com.pbm.Machine> newValues = new ArrayList<com.pbm.Machine>();
				for (int i = 0; i < machines.size(); i++) {
					com.pbm.Machine item = machines.get(i);
					if (item.name.toLowerCase().contains(filter)) {
						newValues.add(item);
					}
				}
				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@SuppressWarnings("unchecked")
		protected void publishResults(CharSequence constraint, FilterResults results) {
			filteredMachineList = (ArrayList<com.pbm.Machine>) results.values;

			clear();

			for (Machine machine : filteredMachineList) {
				add(machine);
			}

			notifyDataSetChanged();
		}
	}
}
