package com.anstar.models;

import java.util.ArrayList;

import com.anstar.model.helper.ServiceResponse;

public class ModelDelegates {

	public interface LoginDelegate {
		public void LoginDidSuccess(String apikey);

		public void LoginFailedWithError(String error);
	}

	public interface LogoutDelegate {
		public void LogoutDidSuccess();

		public void LogoutFailedWithError(String error);
	}

	public interface ModelDelegate<T> {
		public void ModelLoaded(ArrayList<T> list);

		public void ModelLoadFailedWithError(String error);

	}

	public interface UpdateInfoDelegate {
		public void UpdateSuccessFully(ServiceResponse res);

		public void UpdateFail(String ErrorMessage);
	}

	public interface UpdateCustomerDelegate {
		public void UpdateSuccessFully(CustomerInfo info);

		public void UpdateFail(String ErrorMessage);
	}

	public interface SingleInfoCommonDelegate<T> {
		public void AddSuccessFully(T info);

		public void AddFail(String ErrorMessage);
	}

	public interface UpdateAppointmentDelegate {
		public void UpdateSuccessFully(AppointmentInfo info);

		public void UpdateFail(String ErrorMessage);
	}
	
	public interface CommonDelegate {
		public void UpdateSuccessFully(boolean b);

		public void UpdateFail(String ErrorMessage);
	}

	// public interface LoadModelDelegate<T> {
	// public void ModelLoaded(T obj);
	//
	// public void ModelLoadFailedWithError(String error);
	//
	// }

}
