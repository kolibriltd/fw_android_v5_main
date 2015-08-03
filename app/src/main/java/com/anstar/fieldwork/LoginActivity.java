package com.anstar.fieldwork;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.BaseLoader;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.models.Account;
import com.anstar.models.ApplicationMethodInfo;
import com.anstar.models.MeasurementInfo;
import com.anstar.models.ModelDelegates.LoginDelegate;
import com.anstar.models.UserInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.BaitConditionsList;
import com.anstar.models.list.BillingTermsList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.DeviceTypesList;
import com.anstar.models.list.DilutionRatesList;
import com.anstar.models.list.LocationInfoList;
import com.anstar.models.list.MaterialList;
import com.anstar.models.list.PestTypeList;
import com.anstar.models.list.PhotoAttachmentsList;
import com.anstar.models.list.RecomendationsList;
import com.anstar.models.list.ServiceRoutesList;
import com.anstar.models.list.ServicesList;
import com.anstar.models.list.StatusList;
import com.anstar.models.list.TaxRateList;
import com.anstar.models.list.TrapConditionsList;
import com.anstar.models.list.TrapTypesList;
import com.anstar.models.list.WorkHistoryList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
	private Button btnLogin;
	private EditText edtUsername, edtPassword;
	//private ActionBar action = null;
    private BaseLoader mBaseLoader;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

        mBaseLoader = new BaseLoader(this);
		mBaseLoader.setDataLoadedListener(new BaseLoader.DataLoadedListener(){
			@Override
			public void onDataLoaded() {
				Intent i = new Intent(LoginActivity.this,
						DashboardActivity.class);
				startActivity(i);
				finish();
			}
		});

		btnLogin = (Button) findViewById(R.id.btnLogin);
		edtUsername = (EditText) findViewById(R.id.edtUsername);
		edtPassword = (EditText) findViewById(R.id.edtPassword);

		// key= af785da4b8ab55b14bc58edff0b0637e16d49ce237691a71
		// edtUsername.setText("samcom@gmail.com");
		// edtPassword.setText("samir");

		// edtUsername.setText("beau@anstarproducts.com");
		// edtPassword.setText("secretsy");

		// edtUsername.setText("jklescewski@gmail.com");
		// edtPassword.setText("joe");
//		edtUsername.setText("samcom.technobrains@gmail.com");
//		edtPassword.setText("password");
		// jklescewski@gmail.com
		// pass: joe - 21679af9f30953f7b6fef23abfbb3b71b11da3b7b7da2810
		//action = getSupportActionBar();
		// action.setTitle(Html.fromHtml("<font color='#34495E'>Login</font>"));
		// action.setTitle("Login");
		//action.hide();

/*
		if (isLogin()) {
			Intent i = new Intent(this, DashboardActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
		}
*/
		btnLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				login();
			}
		});
		// GetBandWidth();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isLogin()) {
			Account account = Account.getUser();
			if (account != null) {
				edtUsername.setText(account.UserName);
				edtPassword.setText(account.Password);
			}
		}
	}

	public void login() {
		final String username = edtUsername.getText().toString();
		final String password = edtPassword.getText().toString();
		if (username.length() <= 0 || password.length() <= 0) {
			Toast.makeText(LoginActivity.this,
					"Username or Password can not be blank.", Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (username.contains(" ")) {
			Toast.makeText(LoginActivity.this,
					"Username should not contains space", Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (!NetworkConnectivity.isConnected()) {
			Toast.makeText(LoginActivity.this,
					"You need internet connectivity to login.",
					Toast.LENGTH_LONG).show();
			return;
		}
        mBaseLoader.showProgress("Please wait");
		String url = ServiceHelper.URL + ServiceHelper.LOGIN + "?email="
				+ username + "&password=" + password;
		Account account = new Account();
		try {
			account.authenticate(new LoginDelegate() {

				@Override
				public void LoginFailedWithError(String error) {
                    mBaseLoader.hideProgress();
					Toast.makeText(FieldworkApplication.getContext(),
							"Your username or password are incorrect", Toast.LENGTH_LONG)
							.show();
					// String key =
					// "f1226b1322e112d0d4aad8de2ad4d6d8563d29acc2ad2cb6";
					// Account account;
					// try {
					// account = FieldworkApplication.Connection().newEntity(
					// Account.class);
					// account.UserName = username;
					// account.Password = password;
					// account.ApiKey = key;
					// account.isLogin = true;
					// account.save();
					// } catch (ActiveRecordException e) {
					// e.printStackTrace();
					// }
					// AppointmentModelList.Instance().ClearDB();
					// Intent i = new Intent(LoginActivity.this,
					// DashboardActivity.class);
					// startActivity(i);
					// finish();
				}

				@Override
				public void LoginDidSuccess(String response) {
					String key = "";
					JSONObject obj;
					try {
						obj = new JSONObject(response);
						key = obj.optString("api_key");
					} catch (JSONException e1) {
						Utils.LogException(e1);
					}
					Account ac = Account.getUser();
					if (ac != null) {
						if (ac.ApiKey.equalsIgnoreCase(key)) {
							ac.isLogin = true;
							try {
								ac.save();
							} catch (ActiveRecordException e) {
								e.printStackTrace();
							}
							AppointmentModelList.Instance().ClearDB();

							Intent i = new Intent(LoginActivity.this,
									DashboardActivity.class);
							startActivity(i);
							finish();
						} else {
							isNotSameUser(username, password, key);
						}
					} else {
						isNotSameUser(username, password, key);
					}
				}
			}, url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isLogin() {
		Account account = Account.getUser();
		if (account != null) {
			if (account.isLogin) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void isNotSameUser(String username, String password, String key) {
		try {
			FieldworkApplication.Connection().delete(Account.class);
			CustomerList.Instance().ClearDB();
			DilutionRatesList.Instance().ClearDB();
			LocationInfoList.Instance().ClearDB();
			StatusList.Instance().ClearDB();
			MeasurementInfo.ClearDB();
			ApplicationMethodInfo.ClearDB();
			MaterialList.Instance().ClearDB();
			PestTypeList.Instance().ClearDB();
			UserInfo.Instance().ClearDB();
			DeviceTypesList.Instance().ClearDB();
			BaitConditionsList.Instance().ClearDB();
			TrapConditionsList.Instance().ClearDB();
			TrapTypesList.Instance().ClearDB();
			TaxRateList.Instance().ClearDB();
			ServiceRoutesList.Instance().ClearDB();
			ServicesList.Instance().ClearDB();
			BillingTermsList.Instance().ClearDB();
			RecomendationsList.Instance().ClearDB();
			AppointmentModelList.Instance().ClearDB();
			WorkHistoryList.Instance().ClearDB();
			PhotoAttachmentsList.Instance().ClearDB();

			Account account = FieldworkApplication.Connection().newEntity(
					Account.class);
			account.UserName = username;
			account.Password = password;
			account.ApiKey = key;
			account.isLogin = true;
			account.save();

            mBaseLoader.loadAllData(false);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void GetBandWidth() {
		SampleCall call = new SampleCall();
		call.execute();
	}

	public class SampleCall extends AsyncTask<Void, Void, Float> {

		@Override
		protected Float doInBackground(Void... params) {
			float speed = GetSpeed();
			return speed;
		}

		@Override
		protected void onPostExecute(Float result) {
			super.onPostExecute(result);
			float speed = result;
			Utils.LogInfo("BAND WIDTH--->>>" + speed);
		}
	}

	public float GetSpeed() {
		long startTime = System.currentTimeMillis();
		HttpGet httpRequest;
		try {
			// https://lh4.ggpht.com/fBe3NrSOGjyEGtHBvMt8vMXzCoyHhvQrBe_TdsX3zV0MwmlXroXwAmrguuZAV0IFGGM=h900
			httpRequest = new HttpGet(
					new URL(
							"http://www.coolpctips.com/wp-content/uploads/2012/06/tracking-internet-usage.jpeg")
							.toURI());

			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = (HttpResponse) httpClient
					.execute(httpRequest);
			long endTime = System.currentTimeMillis();

			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufHttpEntity;
			bufHttpEntity = new BufferedHttpEntity(entity);

			// You can re-check the size of your file
			final long contentLength = bufHttpEntity.getContentLength();

			// Log
			Log.d("TAG", "[BENCHMARK] Dowload time :" + (endTime - startTime)
					+ " ms");

			// Bandwidth : size(KB)/time(s)
			float bandwidth = contentLength / ((endTime - startTime) * 1000);
			return bandwidth;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
