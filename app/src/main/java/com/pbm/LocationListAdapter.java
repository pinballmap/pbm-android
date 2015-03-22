package com.pbm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LocationListAdapter extends ArrayAdapter<com.pbm.Location> {
	private List<com.pbm.Location> locations;
	private List<com.pbm.Location> filteredLocationList;
	private Context context;
	private Filter filter;

	public LocationListAdapter(Context context, List<com.pbm.Location> locations) {
		super(context, R.layout.location_list_listview, locations);

		this.context = context;
		this.locations = new ArrayList<com.pbm.Location>(locations);
		this.filteredLocationList = new ArrayList<com.pbm.Location>(locations);
	}
	
	@SuppressLint("InflateParams")
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View row = convertView;

		if (row == null) {
			LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.location_list_listview, null);

			holder = new ViewHolder();
			holder.name = (TextView) row.findViewById(R.id.machine_info);
			holder.distance = (TextView) row.findViewById(R.id.distance);
			holder.numMachines = (TextView) row.findViewById(R.id.numMachines);

			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		Location location = filteredLocationList.get(position);
		holder.name.setText(location.name);
		holder.distance.setText(location.milesInfo);
		holder.numMachines.setText(Integer.toString(location.numMachines((Activity) context)));
		
		return row;
	}

	class ViewHolder {
		TextView name;
		TextView distance;
		TextView numMachines;
	}

	public Filter getFilter() {
	    if (filter == null) {
	        filter = new LocationFilter();
	    }

	    return filter;
	}
	
	@SuppressLint("DefaultLocale")
	private class LocationFilter extends Filter {
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			String filter = constraint.toString().toLowerCase();

			if(constraint == null || constraint.length() == 0) {
			    ArrayList<com.pbm.Location> list = new ArrayList<com.pbm.Location>(locations);
			    results.values = list;
			    results.count = list.size();
			} else {
			    ArrayList<com.pbm.Location> newValues = new ArrayList<com.pbm.Location>();
			    for(int i = 0; i < locations.size(); i++) {
			        com.pbm.Location item = locations.get(i);
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
		    filteredLocationList = (ArrayList<com.pbm.Location>) results.values;

		    clear();

		    for (com.pbm.Location location : filteredLocationList) {
		    	add(location);
		    }

		    notifyDataSetChanged();
		}
    }
}
