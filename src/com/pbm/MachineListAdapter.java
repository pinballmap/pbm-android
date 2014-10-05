package com.pbm;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MachineListAdapter extends BaseAdapter implements ListAdapter {
	private LayoutInflater mInflater;
	private List<com.pbm.Machine> machines;
	private boolean disableSelectImage;

	public MachineListAdapter(Context context, List<com.pbm.Machine> machines, boolean disableSelectImage) {
		mInflater = LayoutInflater.from(context);
		this.machines = machines;
		
		this.disableSelectImage = disableSelectImage;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		MachineViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.machine_list_listview, null);
			holder = new MachineViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.metaData = (TextView) convertView.findViewById(R.id.metaData);
			holder.machineSelectButton = (ImageView) convertView.findViewById(R.id.machineSelectButton);

			convertView.setTag(holder);
		} else {
			holder = (MachineViewHolder) convertView.getTag();
		}

		Machine machine = machines.get(position);
		holder.name.setText(machine.name);
		holder.metaData.setText(machine.metaData());
		
		if (disableSelectImage) {
			holder.machineSelectButton.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}
	
	class MachineViewHolder {
		TextView name;
		TextView metaData;
		ImageView machineSelectButton;
	}

	public void registerDataSetObserver(DataSetObserver observer) { }
	public void unregisterDataSetObserver(DataSetObserver observer) { }
	public boolean hasStableIds() { return false; }
	public int getItemViewType(int position) { return 0; }
	public int getViewTypeCount() { return 1; }
	public boolean isEmpty() { return false; }
	public boolean areAllItemsEnabled() { return true; }
	public boolean isEnabled(int position) { return true; }
	public int getCount() { return machines.size(); }
	public Object getItem(int position) { return position; }
	public long getItemId(int position) { return position; }
}
