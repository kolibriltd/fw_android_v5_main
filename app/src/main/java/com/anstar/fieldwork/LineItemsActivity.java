package com.anstar.fieldwork;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.dialog.ProgressDialog;
import com.anstar.common.Const;
import com.anstar.common.Utils;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.LineItemsInfo;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.LineItemsList;

import java.util.ArrayList;

public class LineItemsActivity extends AppCompatActivity {

	private ListView lstMain;
	int appointment_id;
	private LineItemsListAdapter m_adapter = null;
	private static ArrayList<LineItemsInfo> m_lineitems = null;
	//private ActionBar action = null;
	private int EDIT_LINE_ITEM = 1;
	private int ADD_LINE_ITEM = 2;
	private TextView txtInstruction;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_line_items);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		lstMain = (ListView) findViewById(R.id.lstMain);

		m_lineitems = new ArrayList<LineItemsInfo>();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
		} else {
			appointment_id = Const.app_id;
		}
		AppointmentInfo appointmentInfo = AppointmentModelList.Instance()
				.getAppointmentById(appointment_id);
		txtInstruction = (TextView) findViewById(R.id.txtInstruction);
		if (appointmentInfo != null) {
			if (appointmentInfo.instructions != null
					&& appointmentInfo.instructions.length() > 0
					&& !appointmentInfo.instructions.equalsIgnoreCase("null")) {
				txtInstruction.setText(appointmentInfo.instructions);
			} else {
				txtInstruction.setText("No Instructions");
			}
		}
		setResult(RESULT_OK);
	}

	@Override
	protected void onResume() {
		try {
			m_lineitems = LineItemsList.Instance().load(appointment_id);
			// m_lineitems =
			// Utils.Instance().sortMaterialCollections(m_lineitems);
			bindData();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onResume();
	}

	public class LineItemsListAdapter extends BaseAdapter {
		ArrayList<LineItemsInfo> m_list = new ArrayList<LineItemsInfo>();

		public LineItemsListAdapter(ArrayList<LineItemsInfo> list) {
			m_list = list;
		}

		@Override
		public int getCount() {
			return m_list.size();
		}

		@Override
		public Object getItem(int position) {
			return m_list.get(position);
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
				rowView = li
						.inflate(R.layout.addappt_line_item_list_item, null);
				rowView.setTag(holder);
				holder.txtName = (TextView) rowView.findViewById(R.id.txtName);
				holder.txtQty = (TextView) rowView.findViewById(R.id.txtQty);
				holder.txtPrice = (TextView) rowView
						.findViewById(R.id.txtPrice);

				holder.ll = (LinearLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			final LineItemsInfo lItem = m_list.get(position);
			holder.txtName.setText(lItem.name);
			holder.txtQty.setText(lItem.quantity);
			holder.txtPrice.setText("$" + lItem.price);
			final int pos = position;
			final ViewHolder vholder = holder;
			holder.ll.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(LineItemsActivity.this,
							AddLineItemActivity.class);
					i.putExtra("isedit", true);
					i.putExtra("position", pos);
					i.putExtra("isFromDetails", true);
					i.putExtra(Const.Appointment_Id, appointment_id);
					startActivityForResult(i, EDIT_LINE_ITEM);
				}
			});
			holder.ll.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if (m_lineitems.size() > 1) {

						AlertDialog.Builder alt_bld = new AlertDialog.Builder(
								LineItemsActivity.this);
						vholder.ll.setBackgroundColor(Color
								.parseColor("#09B2F1"));
						alt_bld.setMessage("Are you sure want to delete it?")
								.setCancelable(false)
								.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
												ProgressDialog.showProgress(LineItemsActivity.this);
												LineItemsInfo
														.DeleteLineItem(
																lItem.id,
																new UpdateInfoDelegate() {
																	@Override
																	public void UpdateSuccessFully(
																			ServiceResponse res) {
																		try {
																			ProgressDialog.hideProgress();
																			m_lineitems = LineItemsList
																					.Instance()
																					.load(appointment_id);
																			bindData();
																			Toast.makeText(
																					getApplicationContext(),
																					"Line item deleted successfully",
																					Toast.LENGTH_LONG)
																					.show();
																		} catch (Exception e) {
																			e.printStackTrace();
																		}
																	}

																	@Override
																	public void UpdateFail(
																			String ErrorMessage) {
																		ProgressDialog.hideProgress();
																		Toast.makeText(
																				getApplicationContext(),
																				"There is some error.",
																				Toast.LENGTH_LONG)
																				.show();
																	}
																});
											}
										})
								.setNegativeButton("No",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												vholder.ll
														.setBackgroundColor(Color.TRANSPARENT);
												dialog.cancel();
											}
										});

						AlertDialog alert = alt_bld.create();
						alert.setTitle("Alert");
						alert.show();
					} else {
						Toast.makeText(getApplicationContext(),
								"There should be atleast 1 line item, you can not delete it",
								Toast.LENGTH_LONG).show();
					}
					return false;
				}
			});
			return rowView;
		}
	}

	private static class ViewHolder {
		TextView txtName, txtQty, txtPrice;
		LinearLayout ll;
	}

	private void bindData() {
		if (m_lineitems.size() > 0) {
			m_adapter = new LineItemsListAdapter(m_lineitems);
			lstMain.setAdapter(m_adapter);
		} else {
			m_adapter = new LineItemsListAdapter(m_lineitems);
			lstMain.setAdapter(m_adapter);
			// Toast.makeText(getApplicationContext(), "No Material added",
			// Toast.LENGTH_LONG).show();
		}
		Utils.setListViewHeightBasedOnChildren(lstMain);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == EDIT_LINE_ITEM) {
			if (resultCode == RESULT_OK) {
				int id = data.getIntExtra("position", 0);
				ProgressDialog.showProgress(this);
				m_lineitems.get(id).EditLineItems(new UpdateInfoDelegate() {
					@Override
					public void UpdateSuccessFully(ServiceResponse res) {
						ProgressDialog.hideProgress();
						try {
							m_lineitems = LineItemsList.Instance().load(
									appointment_id);
							bindData();
						} catch (Exception e) {
							e.printStackTrace();
						}
						Toast.makeText(getApplicationContext(),
								"Line item updated successfully",
								Toast.LENGTH_LONG).show();
					}

					@Override
					public void UpdateFail(String ErrorMessage) {
						Toast.makeText(getApplicationContext(),
								"There is some error.", Toast.LENGTH_LONG)
								.show();
						ProgressDialog.hideProgress();
					}
				});
			}
		} else if (requestCode == ADD_LINE_ITEM) {
			if (resultCode == RESULT_OK) {
				try {
					m_lineitems = LineItemsList.Instance().load(appointment_id);
					bindData();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Toast.makeText(getApplicationContext(),
						"Line item added successfully", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.fragment_customer_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btnAddPest:
			Intent i = new Intent(LineItemsActivity.this,
					AddLineItemActivity.class);
			i.putExtra("isFromDetails", true);
			i.putExtra("position", appointment_id);// send workorder id in add
													// and in edit line item
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivityForResult(i, ADD_LINE_ITEM);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void alertToAdd(final LineItemsInfo pest) {
		String message = "Are you sure to add this material?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				LineItemsActivity.this);

		alt_bld.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// call();
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
