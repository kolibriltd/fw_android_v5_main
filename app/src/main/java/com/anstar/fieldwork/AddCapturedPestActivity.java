package com.anstar.fieldwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.Utils;
import com.anstar.models.InspectionPest;
import com.anstar.models.PestsTypeInfo;
import com.anstar.models.list.InspectionPestsList;
import com.anstar.models.list.PestTypeList;

import java.util.ArrayList;

public class AddCapturedPestActivity extends AppCompatActivity implements
		OnClickListener {
	private Button btnPlus, btnMinus, btnSave;
	private EditText edtQuantity;
	// private LinearLayout rlInner;

	ListView lstCapture;
	//ActionBar action = null;
	PestAdapter adapter = null;
	int inspection_id = 0;
	String barcode = "";

	final int PEST_CODE = 0;
	int pos = 0;
	TextView txtBarcode;

	ArrayList<InspectionPest> m_list = new ArrayList<InspectionPest>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_captured);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			inspection_id = b.getInt("Inspection_id");
			barcode = b.getString("BARCODE");
		}
/*
		action = getSupportActionBar();
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Add Inspection Pest</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		txtBarcode = (TextView) findViewById(R.id.txtBarcode);

		btnPlus = (Button) findViewById(R.id.btnPlus);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnMinus = (Button) findViewById(R.id.btnMinus);
		edtQuantity = (EditText) findViewById(R.id.edtQuantity);
		lstCapture = (ListView) findViewById(R.id.lstCapture);
		txtBarcode.setText(barcode);
		btnPlus.setOnClickListener(this);
		btnMinus.setOnClickListener(this);
		btnSave.setOnClickListener(this);

		m_list = InspectionPestsList.Instance()
				.getInspectionPestByInspectionId(inspection_id);
		if (m_list == null || m_list.size() == 0) {
			addRecord();
		} else {
			loadListView();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void loadListView() {
		adapter = new PestAdapter(m_list);
		lstCapture.setAdapter(adapter);
		// Utils.setListViewHeightBasedOnChildren(lstCapture);
		updateListViewHeight(lstCapture);
	}

	@Override
	public void onClick(View v) {
		if (v == btnPlus) {
			addRecord();
		} else if (v == btnMinus) {
			removeRecord();
		} else if (v == btnSave) {
			Intent i = new Intent();
			if (m_list.size() > 0) {
				ArrayList<InspectionPest> m_temp = new ArrayList<InspectionPest>();
				for (InspectionPest isp : m_list) {
					if (isp.count != 0) {
						m_temp.add(isp);
					}
				}
				((FieldworkApplication) getApplication()).storeObject(
						"CAPTURED_PEST_DATA", m_temp);
			}
			i.putExtra("KEY", "CAPTURED_PEST_DATA");
			setResult(RESULT_OK, i);
			finish();
		}
	}

	public void addRecord() {
		try {
			InspectionPest pest = new InspectionPest();
			pest.count = 0;
			pest.id = Utils.getRandomInt();
			pest.inspection_id = inspection_id;
			pest.pest_type_id = 0;
			m_list.add(pest);
			loadListView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeRecord() {

		if (m_list.size() >= 1) {
			m_list.remove(m_list.size() - 1);
		}
		loadListView();

	}

	public class PestAdapter extends BaseAdapter {
		ArrayList<InspectionPest> m_list = new ArrayList<InspectionPest>();

		public PestAdapter(ArrayList<InspectionPest> list) {
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			View rowView = convertView;
			holder = new ViewHolder();
			if (rowView == null) {
				LayoutInflater li = getLayoutInflater();
				rowView = li.inflate(R.layout.captured_item, null);
				rowView.setTag(holder);
				holder.ll = (LinearLayout) rowView
						.findViewById(R.id.llCaptured_item);
				holder.rlPest = (RelativeLayout) rowView
						.findViewById(R.id.rlPests);
				holder.edtPest = (TextView) rowView.findViewById(R.id.edtPest);
				holder.edtQty = (EditText) rowView.findViewById(R.id.edtQty);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			final InspectionPest ins_pest = m_list.get(position);

			PestsTypeInfo pestinfo = PestTypeList.Instance().getPestById(
					ins_pest.pest_type_id);
			if (pestinfo != null) {
				holder.edtPest.setText(pestinfo.name);
			} else {
				holder.edtPest.setText("");
			}
			if (ins_pest.count > 0) {
				holder.edtQty.setText("" + ins_pest.count);
			} else {
				holder.edtQty.setText("");
			}
			final ViewHolder hd = holder;
			holder.edtQty.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					if (Utils.ConvertToInt(s.toString()) > 0) {
						ins_pest.count = Utils.ConvertToInt(hd.edtQty.getText()
								.toString());
						try {
							ins_pest.save();
						} catch (ActiveRecordException e) {
							e.printStackTrace();
						}
					}
				}
			});

			holder.rlPest.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					pos = position;
					Intent intent = new Intent(AddCapturedPestActivity.this,
							PestTypeListActivity.class);
					intent.putExtra("isFromCapture", true);
					startActivityForResult(intent, PEST_CODE);
				}
			});

			return rowView;
		}
	}

	public static class ViewHolder {
		EditText edtQty;
		TextView edtPest;
		RelativeLayout rlPest;
		LinearLayout ll;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PEST_CODE:
			if (resultCode == RESULT_OK) {
				int p = data.getIntExtra("pest_id", 0);
				m_list.get(pos).pest_type_id = p;
				loadListView();
			}
			break;

		default:
			break;
		}
	}

	public void updateListViewHeight(ListView myListView) {
		ListAdapter myListAdapter = myListView.getAdapter();
		if (myListAdapter == null) {
			return;
		}
		// get listview height
		int totalHeight = 0;
		int adapterCount = myListAdapter.getCount();
		for (int size = 0; size < adapterCount; size++) {
			View listItem = myListAdapter.getView(size, null, myListView);
			listItem.measure(0, 0);
			totalHeight += 314;
		}
		// Change Height of ListView
		ViewGroup.LayoutParams params = myListView.getLayoutParams();
		params.height = totalHeight
				+ (myListView.getDividerHeight() * (adapterCount - 1));
		myListView.setLayoutParams(params);
		myListView.requestLayout();
	}
}
