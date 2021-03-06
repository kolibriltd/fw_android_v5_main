package com.anstar.fieldwork;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.anstar.common.NetworkConnectivity;
import com.anstar.common.NotificationCenter;
import com.anstar.dialog.ConfirmDialog;
import com.anstar.dialog.ProgressDialog;
import com.anstar.internetbroadcast.SyncHelper;
import com.anstar.model.CommonLoader;

public class SettingFragment extends Fragment {

    private EditText edtIP;
    private ToggleButton tbPrint, toggelworkOffline;
    private Button btnSave, btnReload;
    private TextView txtModeNote;
    private Spinner spnModel;
    String model = "PJ662";
    boolean isPrint = false, isOffline = false;
    String ip = "";
    private Button btnLogOut;
    //	static volatile ProgressDialog m_temppd = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);

        edtIP = (EditText) v.findViewById(R.id.edtPrinterIp);
        tbPrint = (ToggleButton) v.findViewById(R.id.toggelPrint);
        toggelworkOffline = (ToggleButton) v.findViewById(R.id.toggelworkOffline);
        btnSave = (Button) v.findViewById(R.id.btnSaveSetting);
        spnModel = (Spinner) v.findViewById(R.id.spnModel);
        txtModeNote = (TextView) v.findViewById(R.id.txtModeNote);
        btnReload = (Button) v.findViewById(R.id.btnReload);
        btnLogOut = (Button) v.findViewById(R.id.buttonLogOut);
        txtModeNote.setVisibility(View.GONE);

        tbPrint.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                isPrint = isChecked;
            }
        });
        toggelworkOffline
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        isOffline = isChecked;
                    }
                });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = edtIP.getText().toString();
                model = spnModel.getSelectedItem().toString();
                SharedPreferences setting = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = setting.edit();
                editor.putBoolean("ISPRINT", isPrint);
                editor.putBoolean("ISOFFLINE", isOffline);
                editor.putString("IP", ip);
                editor.putString("MODEL", model);
                editor.commit();
                if (!isOffline) {
                    Sync();
                } else {
// Oleg !!!!!!!					finish();
                }

            }
        });
        btnReload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertToReload();
            }
        });
        btnLogOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out
                if (NetworkConnectivity.isConnected()) {
                    ConfirmDialog.newInstance("Confirm exit from account?").show(getActivity().getSupportFragmentManager(), "log_out_dialog");
                } else {
                    Toast.makeText(getActivity(),
                            "Signout needs internet connection", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // action.setTitle("Settings");
//		m_temppd = new ProgressDialog(this);
//		m_temppd.setMessage("Please wait...");

        NotificationCenter.Instance().addObserver(SettingFragment.this, "hide",
                "hidedialog", null);
    }

/*	protected void SettingshowProgress() {
        if (m_temppd != null) {
			Utils.LogInfo("Show Progress-->>");
			m_temppd.show();
		}
		// else {
		// m_pd = new ProgressDialog(this);
		// m_pd.setMessage("Please wait...");
		// m_pd.show();
		// }
	}

	protected void SettingshowProgress(String msg) {
		if (m_temppd != null) {
			Utils.LogInfo("Show Progress-->>");
			m_temppd.setCancelable(false);
			m_temppd.setMessage(msg);
			m_temppd.show();
		}
		// else {
		// m_pd = new ProgressDialog(this);
		// m_pd.setCancelable(false);
		// m_pd.setMessage(msg);
		// m_pd.show();
		// }
	}

	protected void SettinghideProgress() {
		if (m_temppd != null) {
			Utils.LogInfo("Hide Progress-->>");
			m_temppd.dismiss();

			// m_pd = null;
		}
	}

/*
	public void hidedialog() {
		// runOnUiThread(new Thread(new Runnable() {
		// @Override
		// public void run() {
		Utils.LogInfo("in settings hide progress function **********&&&&&&&&");
		// hideProgress();
		// finish();
		stopprogress.sendMessage(stopprogress.obtainMessage());
		stopprogress.sendMessage(stopprogress.obtainMessage());
		stopprogress.sendMessage(stopprogress.obtainMessage());
		stopprogress.sendMessage(stopprogress.obtainMessage());
		finish();
		// }
		// }));
	}
*/

/*
	private Handler stopprogress = new Handler() {
		@Override
		public void handleMessage(Message message) {
			SettinghideProgress();
			finish();
		}
	};
*/

    public void Sync() {
        if (NetworkConnectivity.isConnectedwithoutmode()) {
//			SettingshowProgress("Please wait while syncing data to server");
            ProgressDialog.showProgress(getActivity(), "Please wait while syncing data to server");
            SyncHelper.Instance().startSyncing();
        } else {
            Toast.makeText(getActivity(),
                    "Data will be synced when internet connected",
                    Toast.LENGTH_LONG).show();
// Oleg !!!!!!			finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_fragment_setting);
        SharedPreferences setting = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        boolean print = setting.getBoolean("ISPRINT", false);
        boolean isAutoMode = setting.getBoolean("ISOFFLINE", false);
        model = setting.getString("MODEL", "PJ662");
        String printerIp = setting.getString("IP", "");
        tbPrint.setChecked(print);
        toggelworkOffline.setChecked(isAutoMode);
        edtIP.setText(printerIp);
        int dil_id = 0;
        String[] arr = getResources().getStringArray(R.array.printer_model);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equalsIgnoreCase(model)) {
                dil_id = i;
                break;
            }
        }
        spnModel.setSelection(dil_id);
    }

    public void AlertToReload() {
        String message = "This will download your entire customer database.  We strongly suggest that you perform this over a WIFI connection to save on time and cellular usage.  Depending on the size of your database this will take several minutes.";
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(
                getActivity());
        alt_bld.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (NetworkConnectivity.isConnected()) {

                                    CommonLoader cl = new CommonLoader();
                                    cl.setOnLoadListener(new CommonLoader.OnLoadListener() {
                                        @Override
                                        public void onDataLoaded(CommonLoader cl) {
                                            ProgressDialog.hideProgress();
                                            if(cl.isIsCustomerListDownloadError() || cl.isIsCustomerListItemDownloadError()) {
                                                Toast.makeText(getActivity().getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                    ProgressDialog.showProgress(getActivity());
                                    cl.clear();
                                    cl.loadMax();

                                } else {
                                    Toast.makeText(
                                            getActivity(),
                                            "Reloading global data needs internet connection.",
                                            Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alt_bld.create();
        alert.setTitle("Alert");
        alert.show();
    }
}
