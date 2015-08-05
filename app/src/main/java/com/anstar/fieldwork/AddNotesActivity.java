package com.anstar.fieldwork;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.dialog.ProgressDialog;
import com.anstar.common.Const;
import com.anstar.common.Utils;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.AppointmentConditionsInfo;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.RecomendationInfo;
import com.anstar.models.list.AppointmentConditionsList;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.RecomendationsList;

import java.util.ArrayList;

public class AddNotesActivity extends AppCompatActivity implements OnClickListener {

	private EditText edtNotes,edtPrivateNotes;
	private Button btnSave;
	private int appointment_id = 0;
	private AppointmentInfo appointmentInfo = null;
	private TextView txtCount;
	private ListView lstRecomendations, lstConditions;
	private ImageView imgAddRec, imgAddConditions;
	private final int ADD_REC = 1;
	private final int ADD_CON = 2;
	private RecListAdapter m_adapter = null;
	private ConListAdapter m_cadapter = null;
	static ArrayList<String> recids = new ArrayList<String>();
	static ArrayList<String> conids = new ArrayList<String>();
	private ArrayList<RecomendationInfo> mReclist = new ArrayList<RecomendationInfo>();
	private ArrayList<AppointmentConditionsInfo> mConlist = new ArrayList<AppointmentConditionsInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_notes);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey(Const.Appointment_Id)) {
				appointment_id = b.getInt(Const.Appointment_Id);
			}
		}
		if (appointment_id == 0) {
			appointment_id = Const.app_id;
		}
/*
		ActionBar action = getSupportActionBar();
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color) + "'>Notes</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		appointmentInfo = AppointmentModelList.Instance().getAppointmentById(
				appointment_id);
		edtNotes = (EditText) findViewById(R.id.edtNotes);
		edtPrivateNotes = (EditText) findViewById(R.id.edtPrivateNotes);
		txtCount = (TextView) findViewById(R.id.txtCount);
		lstRecomendations = (ListView) findViewById(R.id.lstRecomendations);
		imgAddRec = (ImageView) findViewById(R.id.imgAddRec);
		lstConditions = (ListView) findViewById(R.id.lstConditions);
		imgAddConditions = (ImageView) findViewById(R.id.imgAddConditions);
		btnSave = (Button) findViewById(R.id.btnSaveNotes);
		if (appointmentInfo != null) {
			if (appointmentInfo.notes != null
					&& appointmentInfo.notes.length() > 0
					&& !appointmentInfo.notes.equalsIgnoreCase("null")) {
				edtNotes.setText(appointmentInfo.notes);
				edtPrivateNotes.setText(appointmentInfo.private_notes);
				txtCount.setText("" + appointmentInfo.notes.length()
						+ " / 2000");
			} else {
				txtCount.setText("0/ 2000");
			}
		}
		btnSave.setOnClickListener(this);
		imgAddRec.setOnClickListener(this);
		imgAddConditions.setOnClickListener(this);
		edtNotes.setMovementMethod(new ScrollingMovementMethod());
		edtPrivateNotes.setMovementMethod(new ScrollingMovementMethod());
		try {
			AppointmentConditionsList.Instance().load(
					new ModelDelegate<AppointmentConditionsInfo>() {
						@Override
						public void ModelLoaded(
								ArrayList<AppointmentConditionsInfo> list) {
							conids = new ArrayList<String>();
							if (appointmentInfo.appointment_condition_ids != null
									&& appointmentInfo.appointment_condition_ids
											.size() > 0) {
								mConlist = AppointmentConditionsList
										.Instance()
										.getSelectedConList(
												appointmentInfo.appointment_condition_ids);
								conids.addAll(appointmentInfo.appointment_condition_ids);
								m_cadapter = new ConListAdapter(mConlist);
								lstConditions.setAdapter(m_cadapter);
								Utils.setListViewHeightBasedOnChildren(lstConditions);
							}

						}

						@Override
						public void ModelLoadFailedWithError(String error) {

						}
					});
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			RecomendationsList.Instance().load(
					new ModelDelegate<RecomendationInfo>() {
						@Override
						public void ModelLoaded(
								ArrayList<RecomendationInfo> list) {
							recids = new ArrayList<String>();
							if (appointmentInfo.recommendation_ids != null
									&& appointmentInfo.recommendation_ids
											.size() > 0) {
								mReclist = RecomendationsList
										.Instance()
										.getSelectedRecList(
												appointmentInfo.recommendation_ids);
								recids.addAll(appointmentInfo.recommendation_ids);
								m_adapter = new RecListAdapter(mReclist);
								lstRecomendations.setAdapter(m_adapter);
								Utils.setListViewHeightBasedOnChildren(lstRecomendations);
							}

						}

						@Override
						public void ModelLoadFailedWithError(String error) {

						}
					});
		} catch (Exception e) {
			// TODO: handle exception
		}

		edtNotes.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() <= 2000) {
					edtNotes.setEnabled(true);
					txtCount.setText("" + s.length() + " / 2000");
				} else {
					Toast.makeText(getApplicationContext(),
							"Notes must be less then 2000 character.",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
//		edtPrivateNotes.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				if (s.length() <= 2000) {
//					edtPrivateNotes.setEnabled(true);
//					txtCount.setText("" + s.length() + " / 2000");
//				} else {
//					Toast.makeText(getApplicationContext(),
//							"Notes must be less then 2000 character.",
//							Toast.LENGTH_LONG).show();
//				}
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//
//			}
//		});
	}

	@Override
	public void onClick(View v) {
		if (v.equals(btnSave)) {
			saveNotes();
		} else if (v == imgAddRec) {
			Intent i = new Intent(AddNotesActivity.this,
					RecomendationsListActivity.class);
			startActivityForResult(i, ADD_REC);
		} else if (v == imgAddConditions) {
			Intent i = new Intent(AddNotesActivity.this,
					ConditionsListActivity.class);
			startActivityForResult(i, ADD_CON);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ADD_REC:
			if (resultCode == RESULT_OK) {
				mReclist = RecomendationsList.Instance().getSelectedRecList(
						recids);
				m_adapter = new RecListAdapter(mReclist);
				lstRecomendations.setAdapter(m_adapter);
				Utils.setListViewHeightBasedOnChildren(lstRecomendations);
			}
			break;
		case ADD_CON:
			if (resultCode == RESULT_OK) {
				mConlist = AppointmentConditionsList.Instance()
						.getSelectedConList(conids);
				m_cadapter = new ConListAdapter(mConlist);
				lstConditions.setAdapter(m_cadapter);
				Utils.setListViewHeightBasedOnChildren(lstConditions);
			}
			break;
		default:
			break;
		}
	}

	private void saveNotes() {
		String notes = edtNotes.getText().toString();
		String privatenotes = edtPrivateNotes.getText().toString();
		
//		if (notes.trim().length() > 0) {
			if (notes.length() > 2000) {
				Toast.makeText(getApplicationContext(),
						"Notes must be less then 2000 character.",
						Toast.LENGTH_LONG).show();
			} else {
				if (appointmentInfo != null) {
					ProgressDialog.showProgress(this);

					AppointmentInfo.saveNotes(appointmentInfo.getID(), notes,privatenotes,
							recids, conids, new UpdateInfoDelegate() {

								@Override
								public void UpdateSuccessFully(
										ServiceResponse res) {
									ProgressDialog.hideProgress();
									if (!res.isError()) {
										Toast.makeText(getApplicationContext(),
												"Notes save successfully",
												Toast.LENGTH_LONG).show();
										finish();
									} else {
										Toast.makeText(getApplicationContext(),
												res.getErrorMessage(),
												Toast.LENGTH_LONG).show();
									}
								}

								@Override
								public void UpdateFail(String ErrorMessage) {
									ProgressDialog.hideProgress();
									Toast.makeText(getApplicationContext(),
											ErrorMessage, Toast.LENGTH_LONG)
											.show();
								}
							});
				}
			}
//		} else {
//			Toast.makeText(getApplicationContext(),
//					"Please insert notes first.", Toast.LENGTH_LONG).show();
//		}
	}

	@Override
	public void onBackPressed() {
		String notes = edtNotes.getText().toString();
		if (notes.trim().length() > 0)
			AlertOnBack();
		else
			super.onBackPressed();
	}

	public void AlertOnBack() {
		String message = "You have not saved this data, would you like to save before proceeding?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				AddNotesActivity.this);

		alt_bld.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								saveNotes();
								dialog.cancel();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						finish();
					}
				});

		AlertDialog alert = alt_bld.create();
		alert.setTitle("Alert");
		alert.show();
	}

	public class RecListAdapter extends BaseAdapter {
		ArrayList<RecomendationInfo> adapterlist = new ArrayList<RecomendationInfo>();

		public RecListAdapter(ArrayList<RecomendationInfo> list) {
			adapterlist = list;
		}

		@Override
		public int getCount() {
			return adapterlist.size();
		}

		@Override
		public Object getItem(int position) {
			return adapterlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			View rowView = convertView;
			holder = new ViewHolder();
			if (rowView == null) {
				LayoutInflater li = getLayoutInflater();
				rowView = li.inflate(R.layout.main_list_item, null);
				rowView.setTag(holder);
				holder.main_item_text = (TextView) rowView
						.findViewById(R.id.main_item_text);
				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			final RecomendationInfo rec = adapterlist.get(position);
			holder.main_item_text.setText(rec.name);
			holder.rl_main_list_item
					.setOnLongClickListener(new OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							return false;
						}
					});
			return rowView;
		}
	}

	public class ConListAdapter extends BaseAdapter {
		ArrayList<AppointmentConditionsInfo> adapterlist = new ArrayList<AppointmentConditionsInfo>();

		public ConListAdapter(ArrayList<AppointmentConditionsInfo> list) {
			adapterlist = list;
		}

		@Override
		public int getCount() {
			return adapterlist.size();
		}

		@Override
		public Object getItem(int position) {
			return adapterlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			View rowView = convertView;
			holder = new ViewHolder();
			if (rowView == null) {
				LayoutInflater li = getLayoutInflater();
				rowView = li.inflate(R.layout.main_list_item, null);
				rowView.setTag(holder);
				holder.main_item_text = (TextView) rowView
						.findViewById(R.id.main_item_text);
				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			final AppointmentConditionsInfo rec = adapterlist.get(position);
			holder.main_item_text.setText(rec.name);
			holder.rl_main_list_item
					.setOnLongClickListener(new OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							return false;
						}
					});
			return rowView;
		}
	}

	private static class ViewHolder {
		TextView main_item_text;
		RelativeLayout rl_main_list_item;
	}

}
