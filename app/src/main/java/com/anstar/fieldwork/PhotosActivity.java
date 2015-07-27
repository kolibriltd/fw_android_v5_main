package com.anstar.fieldwork;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.anstar.common.BaseLoader;
import com.anstar.common.Const;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.models.ModelDelegates.CommonDelegate;
import com.anstar.models.PhotoAttachmentsInfo;
import com.anstar.models.list.PhotoAttachmentsList;

import java.io.File;
import java.util.ArrayList;

public class PhotosActivity extends AppCompatActivity {

	private GridView gridMain;
	int appointment_id;
	private PhotosGridAdapter m_adapter = null;
	private ArrayList<PhotoAttachmentsInfo> m_materials = null;
	ActionBar action = null;
	boolean isFromTrapMaterial = false;
	final int EDIT_PHOTO = 1;
	String url = "";
	ArrayList<PhotoAttachmentsInfo> photos = new ArrayList<PhotoAttachmentsInfo>();
	private BaseLoader mBaseLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photos);
/*
		action = getSupportActionBar();
		// action.setTitle("Material List");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color) + "'>Pictures</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		mBaseLoader = new BaseLoader(this);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		gridMain = (GridView) findViewById(R.id.gridMain);
		m_materials = new ArrayList<PhotoAttachmentsInfo>();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
		} else {
			appointment_id = Const.app_id;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(ServiceHelper.URL);
		sb.append(ServiceHelper.WORK_ORDERS + "/" + appointment_id + "/"
				+ ServiceHelper.PHOTO_ATTACHMENTS);
		// sb.append("api_key=");
		// sb.append(Account.getkey());
		url = sb.toString();
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {

			bindData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class PhotosGridAdapter extends BaseAdapter {
		ArrayList<PhotoAttachmentsInfo> m_list = new ArrayList<PhotoAttachmentsInfo>();

		public PhotosGridAdapter(ArrayList<PhotoAttachmentsInfo> list) {
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
				rowView = li.inflate(R.layout.photos_item, null);
				rowView.setTag(holder);
				holder.imgPhoto = (ImageView) rowView
						.findViewById(R.id.imgPhoto);
				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			final PhotoAttachmentsInfo photo = m_list.get(position);
			ContextWrapper cw = new ContextWrapper(
					FieldworkApplication.getContext());
			// path to /data/data/yourapp/app_data/imageDir
			File directory = cw.getDir(Environment.DIRECTORY_DOWNLOADS,
					Context.MODE_PRIVATE);
			String path = Const.FieldWorkImages + "_" + appointment_id + "_"
					+ photo.id;
			File mypath = new File(directory, path + ".jpg");
			Bitmap myBitmap = BitmapFactory
					.decodeFile(mypath.getAbsolutePath());
			if (myBitmap != null)
				holder.imgPhoto.setImageBitmap(myBitmap);

			holder.rl_main_list_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(PhotosActivity.this,
							AddPhotosActivity.class);
					i.putExtra("photoid", photo.id);
					i.putExtra(Const.Appointment_Id, appointment_id);
					startActivityForResult(i, EDIT_PHOTO);
				}
			});
			final ViewHolder vholder = holder;
			holder.rl_main_list_item
					.setOnLongClickListener(new OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							AlertDialog.Builder alt_bld = new AlertDialog.Builder(
									PhotosActivity.this);
							alt_bld.setMessage(
									"Are you sure want to delete it?")
									.setCancelable(false)
									.setPositiveButton(
											"Yes",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													dialog.cancel();
													mBaseLoader.showProgress();
													photo.deletePhoto(new CommonDelegate() {

														@Override
														public void UpdateSuccessFully(
																boolean b) {
															mBaseLoader.hideProgress();
															Toast.makeText(
																	getApplicationContext(),
																	"Photo has been deleted successfully",
																	Toast.LENGTH_LONG)
																	.show();
															bindData();
														}

														@Override
														public void UpdateFail(
																String ErrorMessage) {
															mBaseLoader.hideProgress();
															Toast.makeText(
																	getApplicationContext(),
																	ErrorMessage,
																	Toast.LENGTH_LONG)
																	.show();
														}
													});
												}
											})
									.setNegativeButton(
											"No",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													dialog.cancel();
												}
											});

							AlertDialog alert = alt_bld.create();
							alert.setTitle("Alert");
							alert.show();

							return false;
						}
					});
			return rowView;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case EDIT_PHOTO:
			if (resultCode == RESULT_OK) {
				bindData();
			}
			break;
		default:
			break;
		}
	}

	public static class ViewHolder {
		ImageView imgPhoto;
		RelativeLayout rl_main_list_item;
	}

	private void bindData() {
		photos = new ArrayList<PhotoAttachmentsInfo>();
		photos = PhotoAttachmentsList.Instance().load(appointment_id);
		if (photos != null) {
			m_adapter = new PhotosGridAdapter(photos);
			gridMain.setAdapter(m_adapter);
		}
		if (photos.size() <= 0) {
			Toast.makeText(getApplicationContext(), "No photos added",
					Toast.LENGTH_LONG).show();
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
			if (photos.size() >= 10) {
				Toast.makeText(getApplicationContext(),
						"You can only upload 10 photos.", Toast.LENGTH_LONG)
						.show();
			} else {
				Intent i = new Intent(PhotosActivity.this,
						AddPhotosActivity.class);
				i.putExtra(Const.Appointment_Id, appointment_id);
				startActivity(i);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void alertToAdd(final PhotoAttachmentsInfo pest) {
		String message = "Are you sure to add this picture?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				PhotosActivity.this);

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
