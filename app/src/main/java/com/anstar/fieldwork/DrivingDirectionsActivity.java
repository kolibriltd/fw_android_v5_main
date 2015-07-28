package com.anstar.fieldwork;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.anstar.common.Const;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.ServiceLocationsList;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class DrivingDirectionsActivity extends ActionBarActivity {
    Integer appointment_id;
    GoogleMap map;
    AppointmentInfo appointmentInfo = null;
    ServiceLocationsInfo serviceLocationInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_directions);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            appointment_id = b.getInt(Const.Appointment_Id);
        }
        if (appointment_id == 0) {
            appointment_id = Const.app_id;
        }

        appointmentInfo = AppointmentModelList.Instance().getAppointmentById(
                appointment_id);
        if (appointmentInfo == null) {
            Toast.makeText(this,
                    "Please try again, something went wrong", Toast.LENGTH_LONG)
                    .show();
//////////////			finish();
            return;
        }

        serviceLocationInfo = ServiceLocationsList.Instance()
                .getServiceLocationById(appointmentInfo.service_location_id);

        if (serviceLocationInfo == null) {
            Toast.makeText(this,
                    "Please try again, something went wrong", Toast.LENGTH_LONG)
                    .show();
////////////////			finish();
            return;
        }

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps)).getMap();
        map.setMyLocationEnabled(true);

        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(Double.parseDouble(serviceLocationInfo.lat), Double.parseDouble(serviceLocationInfo.lon))).title("Hello Maps");
        map.addMarker(marker);

    }
}
