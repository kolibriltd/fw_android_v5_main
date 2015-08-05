package com.anstar.fieldwork;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.dialog.ProgressDialog;
import com.anstar.common.Const;
import com.anstar.common.Utils;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.ModelDelegates.CommonDelegate;
import com.anstar.models.PhotoAttachmentsInfo;
import com.anstar.models.PhotoAttachmentsInfo.UploadDelegate;
import com.anstar.models.list.AppointmentModelList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

public class AddPhotosActivity extends AppCompatActivity implements OnClickListener,
		UploadDelegate {

	private EditText edtNotes;
	private Button btnSave, btnDeletePhoto;
	private ImageView imgPhoto;
	private int appointment_id = 0, photo_id = 0;
	private AppointmentInfo appointmentInfo = null;
	private TextView txtCount;
	final CharSequence[] items = { "Existing Image", "Take a Photo" };
	private static final int SELECT_PICTURE = 1;
	private final static int CAMERA_PIC_REQUEST = 2;
	private static final int GALLERY_KITKAT_INTENT_CALLED = 4;
	private final static int PIC_CROP = 3;
	private String filepath = "";
	private boolean isEdit = false;
	private PhotoAttachmentsInfo photoinfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_photo);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey(Const.Appointment_Id)) {
				appointment_id = b.getInt(Const.Appointment_Id);
			}
			if (b.containsKey("photoid")) {
				photo_id = b.getInt("photoid");
				isEdit = true;
			}
		}
/*
		ActionBar action = getSupportActionBar();
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Add Picture</font>"));
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
		imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
		txtCount = (TextView) findViewById(R.id.txtCount);
		btnSave = (Button) findViewById(R.id.btnSavePhoto);
		btnDeletePhoto = (Button) findViewById(R.id.btnDeletePhoto);
		if (appointmentInfo != null) {

		}
		imgPhoto.setClickable(true);
		btnSave.setOnClickListener(this);
		imgPhoto.setOnClickListener(this);
		btnDeletePhoto.setOnClickListener(this);
		edtNotes.setMovementMethod(new ScrollingMovementMethod());

		edtNotes.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() <= 300) {
					edtNotes.setEnabled(true);
					txtCount.setText("" + s.length() + " / 300");
				} else {
					Toast.makeText(getApplicationContext(),
							"Notes must be less then 300 character.",
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
		SelectProfilePic();
		if (isEdit) {
			loadData();
			btnDeletePhoto.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onResume() {
		appointmentInfo = AppointmentModelList.Instance().getAppointmentById(
				appointment_id);
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		if (v.equals(btnSave)) {
			savePhotos();
		} else if (v == imgPhoto) {
			SelectProfilePic();
		} else if (v == btnDeletePhoto) {
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(
					AddPhotosActivity.this);
			alt_bld.setMessage("Are you sure want to delete it?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
                                    ProgressDialog.showProgress(AddPhotosActivity.this);
									photoinfo.deletePhoto(new CommonDelegate() {
										@Override
										public void UpdateSuccessFully(boolean b) {
                                            ProgressDialog.hideProgress();
											Toast.makeText(
													getApplicationContext(),
													"Photo has been deleted successfully",
													Toast.LENGTH_LONG).show();
											finish();
										}

										@Override
										public void UpdateFail(
												String ErrorMessage) {
											Toast.makeText(
													getApplicationContext(),
													ErrorMessage,
													Toast.LENGTH_LONG).show();
                                            ProgressDialog.hideProgress();
										}
									});
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

			AlertDialog alert = alt_bld.create();
			alert.setTitle("Alert");
			alert.show();

		}
	}

	private void loadData() {
		StringBuilder sb = new StringBuilder();
		sb.append(ServiceHelper.URL);
		sb.append(ServiceHelper.WORK_ORDERS + "/" + appointment_id + "/"
				+ ServiceHelper.PHOTO_ATTACHMENTS);
		String url = sb.toString();

		photoinfo = PhotoAttachmentsInfo.getPhotoAttachemtByID(photo_id);
		if (photoinfo != null) {
			ContextWrapper cw = new ContextWrapper(
					FieldworkApplication.getContext());
			File directory = cw.getDir(Environment.DIRECTORY_DOWNLOADS,
					Context.MODE_PRIVATE);
			String path = Const.FieldWorkImages + "_" + photoinfo.appointment_occurrence_id + "_"
					+ photoinfo.id;
			File mypath = new File(directory, path + ".jpg");
			Bitmap myBitmap = BitmapFactory
					.decodeFile(mypath.getAbsolutePath());
			if (myBitmap != null)
				imgPhoto.setImageBitmap(myBitmap);
			
			edtNotes.setText(photoinfo.comments);
			filepath = directory+"/"+ path + ".jpg";
		}
	}

	private void savePhotos() {
		String notes = edtNotes.getText().toString();
		// if (notes.length() > 300) {
		// Toast.makeText(getApplicationContext(),
		// "Notes must be less then 300 character.",
		// Toast.LENGTH_LONG).show();
		// }
//		Bitmap bitmapOrg = BitmapFactory.decodeFile(filepath);
//		ByteArrayOutputStream bao = new ByteArrayOutputStream();
//		bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 50, bao);
//		byte[] data = bao.toByteArray();
//		Utils.LogInfo("Iamge size ::: "+data.length);
		if (filepath.trim().length() > 0) {
			if (appointmentInfo != null) {
                ProgressDialog.showProgress(this);
				
				if (isEdit) {
					photoinfo.attachment_file_name = filepath;
					photoinfo.comments = notes;
					photoinfo.UploadPhoto(appointment_id, true,selected_bitmap, this);
				} else {
					PhotoAttachmentsInfo info = new PhotoAttachmentsInfo();
					info.attachment_file_name = filepath;
					info.comments = notes;
					info.UploadPhoto(appointment_id, false,selected_bitmap, this);
				}
			}
		} else {
			Toast.makeText(getApplicationContext(), "Please select photo.",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onBackPressed() {
		String notes = edtNotes.getText().toString();
		if (notes.trim().length() > 0)
			AlertOnBack();
		else
			super.onBackPressed();
	}

	public void SelectProfilePic() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					if (Build.VERSION.SDK_INT >= 19) {
						Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
						intent1.addCategory(Intent.CATEGORY_OPENABLE);
						intent1.setType("image/jpeg");
						startActivityForResult(intent1,
								GALLERY_KITKAT_INTENT_CALLED);
					} else {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_PICK);
						startActivityForResult(Intent.createChooser(intent,
								"Complete action using"), SELECT_PICTURE);
					}

					// Intent b = new Intent();
					// b.setType("image/*");
					// b.setAction(Intent.ACTION_GET_CONTENT);
					// startActivityForResult(
					// Intent.createChooser(b, "Select Picture"),
					// SELECT_PICTURE);

				} else if (item == 1) {
					try {
						if (Build.VERSION.SDK_INT >= 19) {
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							startActivityForResult(intent, CAMERA_PIC_REQUEST);
						} else {
							Intent intent = new Intent(
									"android.media.action.IMAGE_CAPTURE");
							startActivityForResult(intent, CAMERA_PIC_REQUEST);
						}
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	Bitmap selected_bitmap = null;
	
	@SuppressWarnings("ResourceType")
    @SuppressLint("NewApi")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SELECT_PICTURE: {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Uri selectedImage = data.getData();
					filepath = Utils.getPath(getApplicationContext(),
							selectedImage);

					performCrop(selectedImage);
					selected_bitmap = null;
					try {
						selected_bitmap = Utils.decodeUri(
								AddPhotosActivity.this, selectedImage, 300);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					if (selected_bitmap != null) {
						imgPhoto.setImageBitmap(selected_bitmap);
					}
				}
			}
			break;
		}
		case CAMERA_PIC_REQUEST: {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Bitmap imageData = (Bitmap) data.getExtras().get("data");
					Uri selectedImage = getImageUri(AddPhotosActivity.this,
							imageData);
					filepath = Utils.getPath(getApplicationContext(),
							selectedImage);
					if (Build.VERSION.SDK_INT < 19)
						performCrop(selectedImage);
					selected_bitmap = (Bitmap) data.getExtras().get(
							"data");
					if (selected_bitmap != null) {
						imgPhoto.setImageBitmap(selected_bitmap);
					}
				}
			}
			break;
		}
		case GALLERY_KITKAT_INTENT_CALLED: {
			if (resultCode == RESULT_OK) {
				ParcelFileDescriptor parcelFileDescriptor;
				Uri mImageCaptureUri = data.getData();
				try {
					final int takeFlags = data.getFlags()
							& (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
					getContentResolver().takePersistableUriPermission(
							mImageCaptureUri, takeFlags);
					// performCrop(mImageCaptureUri);
					parcelFileDescriptor = getContentResolver()
							.openFileDescriptor(mImageCaptureUri, "r");
					FileDescriptor fileDescriptor = parcelFileDescriptor
							.getFileDescriptor();
					selected_bitmap = BitmapFactory
							.decodeFileDescriptor(fileDescriptor);
					parcelFileDescriptor.close();
					filepath = Utils.getPath(getApplicationContext(),
							mImageCaptureUri);
					if (selected_bitmap != null) {
						imgPhoto.setImageBitmap(selected_bitmap);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
		case PIC_CROP: {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				Bitmap selectedBitmap = extras.getParcelable("data");
				filepath = Environment.getExternalStorageDirectory() + filename;
				Bitmap thumbnail = BitmapFactory.decodeFile(filepath);
				selectedBitmap = thumbnail;
				if (selectedBitmap != null) {
					imgPhoto.setImageBitmap(selectedBitmap);
				}
			}
			break;
		}

		}
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(inContext.getContentResolver(),
				inImage, "temp", null);
		return Uri.parse(path);
	}

	public void AlertOnBack() {
		String message = "You have not saved this data, would you like to save before proceeding?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				AddPhotosActivity.this);

		alt_bld.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								savePhotos();
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

	private String filename = "";

	private void performCrop(Uri picUri) {
		try {

			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(picUri, "image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 800);
			intent.putExtra("outputY", 800);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);
			filename = "/temporary_holder"
					+ Calendar.getInstance().getTimeInMillis() + ".jpg";
			File f = new File(Environment.getExternalStorageDirectory(),
					filename);
			try {
				f.createNewFile();
			} catch (IOException ex) {
				Log.e("io", ex.getMessage());
			}

			Uri uri = Uri.fromFile(f);

			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			startActivityForResult(intent, PIC_CROP);
		} catch (ActivityNotFoundException anfe) {
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	@Override
	public void UploadSuccessFully(String message, boolean isedited, String name) {
        ProgressDialog.hideProgress();
		Toast.makeText(getApplicationContext(),
				"Photo has been added successfully.", Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	public void UploadFailed(String error) {
        ProgressDialog.hideProgress();
		Toast.makeText(getApplicationContext(), "Please try again.",
				Toast.LENGTH_LONG).show();
	}

}
