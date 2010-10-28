package com.pbm;

import java.net.URLEncoder;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class ConditionEdit extends PBMUtil {
	private Location location;
	private Machine machine;
	private InputMethodManager inputMethodManager;

	@SuppressWarnings("static-access")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conditionedit);

		Bundle extras = getIntent().getExtras();
		location = (Location) extras.get("Location");
		machine = (Machine) extras.get("Machine");

		EditText condition = (EditText)findViewById(R.id.condition);
		condition.setText(machine.condition);

		inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(inputMethodManager.SHOW_FORCED, 0);
	}

	private void updateCondition(String condition) {
		EditText currText = (EditText) findViewById(R.id.condition);

		sendOneWayRequestToServer("condition=" + URLEncoder.encode(condition) + ";location_no=" + location.locationNo + ";machine_no=" + machine.machineNo);

		Toast.makeText(getBaseContext(), "Thanks for updating that comment!", Toast.LENGTH_LONG).show();
		inputMethodManager.hideSoftInputFromWindow(currText.getWindowToken(), 0);

		setResult(REFRESH_RESULT);
		this.finish();
	}

	public void clickHandler(View view) {		
		switch (view.getId()) {
		case R.id.submitCondition :
			EditText currText = (EditText) findViewById(R.id.condition);
			updateCondition(currText.getText().toString());

			break;
		case R.id.deleteCondition :
			updateCondition(new String(" "));
			break;
		default:
			break;
		}
	} 
}