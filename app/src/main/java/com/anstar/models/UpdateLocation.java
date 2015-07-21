package com.anstar.models;

import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;

public class UpdateLocation implements ServiceHelperDelegate {

	public String lat = "";
	public String lon = "";

	public void updateLoc(String lat, String lon)
			throws Exception {
		String json = "{\"lat\":\"" + lat + "\",\"lat\":\""+lon+"\"}";
		String url = "user/coordinates";
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.POST, json);
		caller.startRequest(this);
	}

	@Override
	public void CallFinish(ServiceResponse res) {
		if (!res.isError()) {

		} else {
		}
	}

	@Override
	public void CallFailure(String ErrorMessage) {
	}

}
