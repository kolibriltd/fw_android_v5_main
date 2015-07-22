package com.anstar.fieldwork;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.common.BaseLoader;
import com.anstar.common.Const;
import com.anstar.common.Utils;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.AttachmentsInfo;
import com.anstar.models.DownloadPdf;
import com.anstar.models.DownloadPdf.DownLoadDelegate;
import com.anstar.models.PdfFormsInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.AttachmentsList;
import com.anstar.models.list.PdfFormsList;

import java.io.File;
import java.util.ArrayList;

public class PdfFormsActivity extends AppCompatActivity {

	private ListView lstMain, lstAttachment;
	int appointment_id;
	private PdfFormsListAdapter m_adapter = null;
	private AttachmentListAdapter m_adapterAttach = null;
	private ArrayList<PdfFormsInfo> m_pdfforms = null;
	private ArrayList<AttachmentsInfo> m_attachmanes = null;
	//ActionBar action = null;
	private BaseLoader mBaseLoader;

	// TextView txtInstruction;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pdf_forms_list);
/*
		action = getSupportActionBar();
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color) + "'>Pdf Forms</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		mBaseLoader = new BaseLoader(this);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		lstMain = (ListView) findViewById(R.id.lstMain);
		lstAttachment = (ListView) findViewById(R.id.lstAttachment);

		m_pdfforms = new ArrayList<PdfFormsInfo>();
		m_attachmanes = new ArrayList<AttachmentsInfo>();

		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
		} else {
			appointment_id = Const.app_id;
		}
		AppointmentInfo appointmentInfo = AppointmentModelList.Instance()
				.getAppointmentById(appointment_id);
		// txtInstruction = (TextView) findViewById(R.id.txtInstruction);
		if (appointmentInfo != null) {
			if (appointmentInfo.instructions != null
					&& appointmentInfo.instructions.length() > 0
					&& !appointmentInfo.instructions.equalsIgnoreCase("null")) {
				// txtInstruction.setText(appointmentInfo.instructions);
			} else {
				// txtInstruction.setText("No Instructions");
			}
		}
		try {
			m_pdfforms = PdfFormsList.Instance().load(appointment_id);
			m_attachmanes = AttachmentsList.Instance().load(appointment_id);
			// if (m_attachmanes.size() <= 0) {
			// for (PdfFormsInfo pdf : m_pdfforms) {
			// AttachmentsInfo info = FieldworkApplication.Connection()
			// .newEntity(AttachmentsInfo.class);
			// info.attached_pdf_form_content_type = pdf.document_content_type;
			// info.attached_pdf_form_file_name = pdf.document_file_name;
			// info.id = -1;
			// info.pdf_id = pdf.id;
			// info.WorkOrderId = pdf.WorkOrderId;
			// info.save();
			// }
			// m_attachmanes = AttachmentsList.Instance().load(appointment_id);
			// } else {
			// ArrayList<String> m_temp = new ArrayList<String>();
			// for (PdfFormsInfo p : m_pdfforms) {
			// m_temp.add(p.document_file_name);
			// }
			// for (AttachmentsInfo a : m_attachmanes) {
			// if (!m_temp.contains(a.attached_pdf_form_file_name)) {
			// PdfFormsInfo temp = PdfFormsInfo
			// .getPdfFormsByfilename(a.attached_pdf_form_file_name);
			// AttachmentsInfo info = FieldworkApplication
			// .Connection().newEntity(AttachmentsInfo.class);
			// info.attached_pdf_form_content_type = temp.document_content_type;
			// info.attached_pdf_form_file_name = temp.document_file_name;
			// info.id = -1;
			// info.pdf_id = temp.id;
			// info.WorkOrderId = temp.WorkOrderId;
			// info.save();
			// }
			// }
			// m_attachmanes = AttachmentsList.Instance().load(appointment_id);
			// }
			// m_lineitems =
			// Utils.Instance().sortMaterialCollections(m_lineitems);
			bindData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.setListViewHeightBasedOnChildren(lstMain);
		Utils.setListViewHeightBasedOnChildren(lstAttachment);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public class PdfFormsListAdapter extends BaseAdapter {
		ArrayList<PdfFormsInfo> m_list = new ArrayList<PdfFormsInfo>();

		public PdfFormsListAdapter(ArrayList<PdfFormsInfo> list) {
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
				rowView = li.inflate(R.layout.main_list_item, null);
				rowView.setTag(holder);
				holder.main_item_text = (TextView) rowView
						.findViewById(R.id.main_item_text);
				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			final PdfFormsInfo pItem = m_list.get(position);
			holder.main_item_text.setText(pItem.name);
			holder.rl_main_list_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// DownloadPdf(pItem);
				}
			});
			return rowView;
		}
	}

	public class AttachmentListAdapter extends BaseAdapter {
		ArrayList<AttachmentsInfo> m_list = new ArrayList<AttachmentsInfo>();

		public AttachmentListAdapter(ArrayList<AttachmentsInfo> list) {
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
			ViewHolderforAttachment holder;
			View rowView = convertView;
			holder = new ViewHolderforAttachment();
			if (rowView == null) {
				LayoutInflater li = getLayoutInflater();
				rowView = li.inflate(R.layout.main_list_item, null);
				rowView.setTag(holder);
				holder.main_item_text = (TextView) rowView
						.findViewById(R.id.main_item_text);
				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolderforAttachment) rowView.getTag();
			}

			final AttachmentsInfo pItem = m_list.get(position);

			final PdfFormsInfo temp = PdfFormsInfo.getPdfFormsByPdfId(
					pItem.pdf_id, appointment_id);
			holder.main_item_text.setText(pItem.attached_pdf_form_file_name);
			holder.rl_main_list_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (pItem.id > 0) {
						DownloadAttachment(pItem);
					} else {
						DownloadPdf(temp);
					}
				}
			});
			return rowView;
		}
	}

	public void DownloadPdf(final PdfFormsInfo pItem) {
		if (pItem != null) {
			mBaseLoader.showProgress();
			DownloadPdf.Instance().downloadPdf(pItem.WorkOrderId, pItem.pid,
					new DownLoadDelegate() {

						@Override
						public void DownLoadSuccessFully(String message) {
							mBaseLoader.hideProgress();

							String filepath = "attachment_" + appointment_id
									+ "_" + pItem.pid + ".pdf";
							File file = new File(DownloadPdf.Instance()
									.getStoragePath(filepath));
							Intent target = new Intent(Intent.ACTION_VIEW);
							target.setDataAndType(Uri.fromFile(file),
									"application/pdf");
							target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
							Intent intent = Intent.createChooser(target,
									"Open File");
							try {
								startActivity(intent);
							} catch (ActivityNotFoundException e) {
								Toast.makeText(
										getApplicationContext(),
										"No Application found for open document.",
										Toast.LENGTH_LONG).show();
							}
						}

						@Override
						public void DownLoadFailed(String error) {

							mBaseLoader.hideProgress();
						}
					});
		}
	}

	public void DownloadAttachment(final AttachmentsInfo pItem) {
		mBaseLoader.showProgress();
		DownloadPdf.Instance().downloadAttachment(pItem.WorkOrderId, pItem.id,
				new DownLoadDelegate() {

					@Override
					public void DownLoadSuccessFully(String message) {
						mBaseLoader.hideProgress();

						String filepath = "attachment_" + appointment_id + "_"
								+ pItem.id + ".pdf";
						File file = new File(DownloadPdf.Instance()
								.getStoragePath(filepath));
						Intent target = new Intent(Intent.ACTION_VIEW);
						target.setDataAndType(Uri.fromFile(file),
								"application/pdf");
						target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						Intent intent = Intent.createChooser(target,
								"Open File");
						try {
							startActivity(intent);
						} catch (ActivityNotFoundException e) {
							Toast.makeText(getApplicationContext(),
									"No Application found for open document.",
									Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void DownLoadFailed(String error) {
						Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
						mBaseLoader.hideProgress();
					}
				});

	}

	public static class ViewHolder {
		TextView main_item_text;
		RelativeLayout rl_main_list_item;
	}

	public static class ViewHolderforAttachment {
		TextView main_item_text;
		RelativeLayout rl_main_list_item;
	}

	private void bindData() {
		if (m_pdfforms.size() > 0) {
			m_adapter = new PdfFormsListAdapter(m_pdfforms);
			lstMain.setAdapter(m_adapter);
		} else {
			Toast.makeText(getApplicationContext(), "No pdf forms.",
					Toast.LENGTH_LONG).show();
		}
		if (m_attachmanes.size() > 0) {
			m_adapterAttach = new AttachmentListAdapter(m_attachmanes);
			lstAttachment.setAdapter(m_adapterAttach);
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// super.onCreateOptionsMenu(menu);
	// getSupportMenuInflater().inflate(R.menu.add_pest_menu, menu);
	// return super.onCreateOptionsMenu(menu);
	// }
	//
	// //
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case R.id.btnAddPest:
	// Intent i = new Intent(PdfFormsActivity.this,
	// AddMaterialActivity.class);
	// startActivity(i);
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }

}
