package com.anstar.fieldwork;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.dialog.ProgressDialog;
import com.anstar.model.CommonLoader;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.models.Account;
import com.anstar.models.ModelDelegates.LoginDelegate;
import com.anstar.models.list.AppointmentModelList;

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

public class LoginFragment extends Fragment {

    private Button btnLogin;
    private EditText edtUsername, edtPassword;

    private OnLoginFragmentInteractionListener mOnLoginFragmentInteractionListener;

    // Container Activity must implement this interface
    public interface OnLoginFragmentInteractionListener {
        void onLoginDidSuccess();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        edtUsername = (EditText) v.findViewById(R.id.edtUsername);
        edtPassword = (EditText) v.findViewById(R.id.edtPassword);

        btnLogin.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLogin()) {
            Account account = Account.getUser();
            if (account != null) {
                edtUsername.setText(account.UserName);
                edtPassword.setText(account.Password);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mOnLoginFragmentInteractionListener = (OnLoginFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }

    public void login() {
        final String username = edtUsername.getText().toString();
        final String password = edtPassword.getText().toString();
        if (username.length() <= 0 || password.length() <= 0) {
            Toast.makeText(getActivity(),
                    "Username or Password can not be blank.", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (username.contains(" ")) {
            Toast.makeText(getActivity(),
                    "Username should not contains space", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (!NetworkConnectivity.isConnected()) {
            Toast.makeText(getActivity(),
                    "You need internet connectivity to login.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        ProgressDialog.showProgress(getActivity());

        String url = ServiceHelper.URL + ServiceHelper.LOGIN + "?email="
                + username + "&password=" + password;
        Account account = new Account();
        try {
            account.authenticate(new LoginDelegate() {

                @Override
                public void LoginFailedWithError(String error) {
                    ProgressDialog.hideProgress();
                    Toast.makeText(getActivity(),
                            "Your username or password are incorrect", Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void LoginDidSuccess(String response) {
                    ProgressDialog.hideProgress();
                    String key = "";
                    JSONObject obj;
                    try {
                        obj = new JSONObject(response);
                        key = obj.optString("api_key");
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

                                mOnLoginFragmentInteractionListener.onLoginDidSuccess();
                            } else {
                                isNotSameUser(username, password, key);
                            }
                        } else {
                            isNotSameUser(username, password, key);
                        }
                    } catch (JSONException e1) {
                        Utils.LogException(e1);
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

            Account account = FieldworkApplication.Connection().newEntity(
                    Account.class);
            account.UserName = username;
            account.Password = password;
            account.ApiKey = key;
            account.isLogin = true;
            account.save();

            CommonLoader cl = new CommonLoader();
            cl.setOnLoadListener(new CommonLoader.OnLoadListener() {
                @Override
                public void onDataLoaded(CommonLoader cl) {
                    ProgressDialog.hideProgress();
                    if(cl.isIsCustomerListDownloadError() || cl.isIsCustomerListItemDownloadError()) {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                    }
                    mOnLoginFragmentInteractionListener.onLoginDidSuccess();
                }
            });
            ProgressDialog.showProgress(getActivity());
            cl.clear();
            cl.loadMax();

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
