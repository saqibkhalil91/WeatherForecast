package com.weatherdetector.activities;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.weatherdetector.Entities.Temperature;
import com.weatherdetector.utilities.ConnectionDetector;
import com.weatherdetector.utilities.CoordinateInterface;
import com.weatherdetector.utilities.GpsTrack;
import com.weatherdetector.utilities.LatLongAsyncTask;
import com.weatherdetector.utilities.TemperatureInterface;
import com.weatherdetector.utilities.CityTemperatureAsyncTask;
import com.weatherdetector.utilities.Validation;

public class MainActivity extends Activity implements
		android.view.View.OnClickListener {
	private EditText place;
	private ImageView done;
	private TextView temp, atmosphere, visibility, humidity, windspeed;
	private ImageButton btnShowLocation;
	private String latitude;
	private String longitude;
	private ConnectionDetector cd;
	private CityTemperatureAsyncTask api;
	private ImageView mclear;
	private boolean checkingConnection;
	private String cityname1;
	// GPSTracker class
	private GpsTrack gps;
	private ImageView degC, degF;
	private ImageView weatherImage;
	private Validation validater;

	float teminF, timeC;
	int temperatureUnit_flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initComponents();

	}

	private void initComponents() {
		place = (EditText) findViewById(R.id.edtplace);
		done = (ImageView) findViewById(R.id.btndone);
		done.setOnClickListener(this);
		temp = (TextView) findViewById(R.id.tvtemp);
		atmosphere = (TextView) findViewById(R.id.tvtempatmosphere);
		visibility = (TextView) findViewById(R.id.tvtempVisibility);
		humidity = (TextView) findViewById(R.id.tvtemphumidity);
		windspeed = (TextView) findViewById(R.id.tvwindspeed);
		btnShowLocation = (ImageButton) findViewById(R.id.btnShowLocation);
		btnShowLocation.setOnClickListener(this);

		degC = (ImageView) findViewById(R.id.degC);
		degC.setOnClickListener(this);
		mclear = (ImageView) findViewById(R.id.btnclear);
		mclear.setOnClickListener(this);
		degF = (ImageView) findViewById(R.id.degF);
		degF.setOnClickListener(this);
		weatherImage = (ImageView) findViewById(R.id.imageView1);
		validater = new Validation();
		cd = new ConnectionDetector(this);
		checkingConnection = cd.isConnectingToInternet();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Converts to celcius
	private float convertFahrenheitToCelcius(float fahrenheit) {
		return ((fahrenheit - 32) * 5 / 9);
	}

	// Converts to fahrenheit
	private float convertCelciusToFahrenheit(float celsius) {
		return ((celsius * 9) / 5) + 32;
	}

	private void setImageResource(String weathrCondition) {
		Log.d("weather", weathrCondition);
		if (weathrCondition.contains("Cloudy")) {

			weatherImage.setImageResource(R.drawable.cloudy);
		} else if (weathrCondition.contains("Partial")) {
			weatherImage.setImageResource(R.drawable.partialcloudy);
		} else if (weathrCondition.contains("Haze")
				|| weathrCondition.contains("Smoke")) {
			weatherImage.setImageResource(R.drawable.haze);
		} else if (weathrCondition.contains("Rainy")) {
			weatherImage.setImageResource(R.drawable.raining);
		} else if (weathrCondition.contains("Suny")) {
			weatherImage.setImageResource(R.drawable.suny);
		} else if (weathrCondition.contains("Snow")) {
			weatherImage.setImageResource(R.drawable.snow);
		} else if (weathrCondition.contains("Thunder")) {
			weatherImage.setImageResource(R.drawable.thunder);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {

		case R.id.listicon:
			if (checkingConnection == true) {
				LocationFound();
			} else {
				NOInterNet();
			}
			// search action
			return true;

		default:
		}
		return super.onOptionsItemSelected(item);
	}

	private void LocationFound() {
		Intent i = new Intent(this, PakistanCitiesTemperature.class);
		startActivity(i);
	}

	private void showTemperatureDetail() {
		String cityName;
		cityName = place.getText().toString();
		api = new CityTemperatureAsyncTask(this);
		String city = cityName.trim();
		if (city.equals("null") || city.contains(".") || city.contains(",")
				|| city.length() == 3) {
			Toast.makeText(this, "Enter correct city Name", Toast.LENGTH_LONG)
					.show();
		} else if (validater.validate(city)) {
			Toast.makeText(this, "Enter correct city Name", Toast.LENGTH_LONG)
					.show();
		} else {
			api.execute(city);
		}

		api.setApiResulListener(new TemperatureInterface() {

			@Override
			public void setList(Temperature weatherTemperature) {
				// TODO Auto-generated method stub

				teminF = Float.parseFloat(weatherTemperature.getTemperature());
				if (temperatureUnit_flag == 0) {
					DecimalFormat numberFormat = new DecimalFormat("#.0");

					String temF = numberFormat.format(teminF);
					temp.setText("" + temF + "F");
				} else if (temperatureUnit_flag == 1) {
					timeC = convertFahrenheitToCelcius(teminF);
					DecimalFormat numberFormat = new DecimalFormat("#.0");

					String tempC = numberFormat.format(timeC);
					temp.setText("" + tempC + "C");
				}
				String atmospher = weatherTemperature.getAtmoshphere();
				atmosphere.setText("Atmosphere is " + atmospher);
				setImageResource(atmospher);
				humidity.setText("Humidity is "
						+ weatherTemperature.getHumidity());
				visibility.setText("Visibility is "
						+ weatherTemperature.getVisibility());
				windspeed.setText("Wind Speed is "
						+ weatherTemperature.getWindSpeed() + "mph");

			}
		});
	}

	private void NOInterNet() {

		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle("Internet Issue");
		helpBuilder.setMessage("Your internet is not available");

		helpBuilder.setPositiveButton("Thank you",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

					}
				});
		// Remember, create doesn't show the dialog
		AlertDialog helpDialog = helpBuilder.create();
		helpDialog.show();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int key = v.getId();

		switch (key) {
		case R.id.btndone:

			if (checkingConnection == true) {
				try {

					showTemperatureDetail();

				} catch (Exception e) {
					e.getMessage();
				}

			} else {
				NOInterNet();

			}

			break;
		case R.id.btnShowLocation:
			gps = new GpsTrack(MainActivity.this);
			// check if GPS enabled

			cd = new ConnectionDetector(this);
			checkingConnection = cd.isConnectingToInternet();
			if (checkingConnection == true) {
				if (gps.canGetLocation()) {

					latitude = gps.getLatitude() + "";
					longitude = gps.getLongitude() + "";
					LatLongAsyncTask api = new LatLongAsyncTask(this);
					api.execute(latitude, longitude);
					api.setApiResulNameListener(new CoordinateInterface() {

						@Override
						public void setCityName(String cityName) {
							// TODO Auto-generated method stub
							cityname1 = cityName;
							place.setText(cityname1);
						}
					});

				} else {
					// can't get location
					// GPS or Network is not enabled
					// Ask user to enable GPS/network in settings
					gps.showSettingsAlert();
				}
				try {

					// showTemperatureDetail();

				} catch (Exception e) {

					// Toast.makeText(this,
					// "No Result found please type city Name",
					// Toast.LENGTH_LONG).show();
				}

			} else {
				NOInterNet();

			}

			break;
		case R.id.degC:
			temperatureUnit_flag = 1;

			timeC = convertFahrenheitToCelcius(teminF);
			DecimalFormat numberFormat = new DecimalFormat("#.0");

			String temCC = numberFormat.format(timeC);
			// String tempp=timeC+"";
			temp.setText("" + temCC + "C");

			break;

		case R.id.degF:
			if (temperatureUnit_flag == 1) {
				DecimalFormat numberFormatt = new DecimalFormat("#.0");

				double temppp = convertCelciusToFahrenheit(timeC);
				String temFF = numberFormatt.format(temppp);
				temp.setText("" + temFF + "F");
			}

			break;
		case R.id.btnclear:
			String value = "";
			place.setText(value);

			break;
		default:
			break;
		}
	}

}
