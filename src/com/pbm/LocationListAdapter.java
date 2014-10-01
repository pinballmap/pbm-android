package com.pbm;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class LocationListAdapter extends BaseAdapter implements ListAdapter {
	private LayoutInflater mInflater;
	private List<com.pbm.Location> locations;
	private Context context;

	public LocationListAdapter(Context context, List<com.pbm.Location> locations) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.locations = locations;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.location_list_listview, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.distance = (TextView) convertView.findViewById(R.id.distance);
			holder.numMachines = (TextView) convertView.findViewById(R.id.numMachines);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Location location = locations.get(position);
		holder.name.setText(location.name);
		holder.distance.setText(location.milesInfo);
		holder.numMachines.setText(Integer.toString(location.numMachines((Activity) context)));
		
		convertView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent();						
				com.pbm.Location location = locations.get(position);
				
				myIntent.putExtra("Location", location);
				myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
				((Activity) context).startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);
			}
		});
		
		return convertView;
	}

	class ViewHolder {
		TextView name;
		TextView distance;
		TextView numMachines;
	}

	public void registerDataSetObserver(DataSetObserver observer) { }
	public void unregisterDataSetObserver(DataSetObserver observer) { }
	public boolean hasStableIds() { return false; }
	public int getItemViewType(int position) { return 0; }
	public int getViewTypeCount() { return 1; }
	public boolean isEmpty() { return false; }
	public boolean areAllItemsEnabled() { return true; }
	public boolean isEnabled(int position) { return true; }
	public int getCount() { return locations.size(); }
	public Object getItem(int position) { return position; }
	public long getItemId(int position) { return position; }
	
}
