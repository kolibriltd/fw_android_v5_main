package com.anstar.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.anstar.common.Const;
import com.anstar.dialog.ProgressDialog;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.fieldwork.R;
import com.anstar.models.ModelDelegates;
import com.anstar.models.PhotoAttachmentsInfo;
import com.anstar.models.list.PhotoAttachmentsList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by oleg on 13.08.15.
 */
public class AppointmentDetailsFragmentPhotosListItem extends LinearLayout {
    private final Activity mContext;
    private final LayoutInflater mInflater;
    private GridView gridMain;
    private int mAppointmentId;
    private PhotosGridAdapter mPhotosAdapter = null;
    private ArrayList<PhotoAttachmentsInfo> photos = new ArrayList<PhotoAttachmentsInfo>();
    private HashMap<Integer, Bitmap> mBitmapsPool;


    public void setOnListItemInteractionListener(OnListItemInteractionListener listener) {
        mListener = listener;
    }

    private OnListItemInteractionListener mListener;

    public interface OnListItemInteractionListener {

        void onEditPhoto(int appointmentId, int id);

        void onAddPhoto(int appointmentId);

        void onRefresh();
    }

    public AppointmentDetailsFragmentPhotosListItem(Activity context) {
        super(context);

        mContext = context;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.fragment_appointment_details_photos_list_item, this);

        gridMain = (GridView) findViewById(R.id.gridMain);
        mPhotosAdapter = new PhotosGridAdapter();
        gridMain.setAdapter(mPhotosAdapter);

        Button buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    if (photos.size() >= 10) {
                        Toast.makeText(mContext,
                                "You can only upload 10 photos.", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        mListener.onAddPhoto(mAppointmentId);
                    }
                }
            }
        });
    }

    public void init(int appointmentId) {

        mAppointmentId = appointmentId;
        mBitmapsPool = new HashMap<>();
        photos = PhotoAttachmentsList.Instance().load(mAppointmentId);
        for (int i = 0; i < photos.size(); i++) {
            PhotoAttachmentsInfo photo = photos.get(i);
            ContextWrapper cw = new ContextWrapper(FieldworkApplication.getContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir(Environment.DIRECTORY_DOWNLOADS, Context.MODE_PRIVATE);
            String path = Const.FieldWorkImages + "_" + mAppointmentId + "_" + photo.id;
            File file = new File(directory, path + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 70, 70);
            mBitmapsPool.put(i, bitmap);
        }
        mPhotosAdapter.notifyDataSetChanged();
    }

    public class PhotosGridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Object getItem(int position) {
            return photos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View rowView = convertView;
            if (rowView == null) {
                holder = new ViewHolder();
                rowView = mInflater.inflate(R.layout.photos_item, null);
                holder.imgPhoto = (ImageView) rowView
                        .findViewById(R.id.imgPhoto);
                holder.rl_main_list_item = (RelativeLayout) rowView
                        .findViewById(R.id.rl_main_list_item);
                rowView.setTag(R.id.tag_1, holder);
            } else {
                holder = (ViewHolder) rowView.getTag(R.id.tag_1);
            }

            Bitmap myBitmap = mBitmapsPool.get(position);
            holder.imgPhoto.setImageBitmap(myBitmap);
            holder.rl_main_list_item.setTag(R.id.tag_2, position);

            holder.rl_main_list_item
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onEditPhoto(mAppointmentId, photos.get((int) v.getTag(R.id.tag_2)).id);
                        }
                    });

            holder.rl_main_list_item
                    .setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            final int imageId = (int) v.getTag(R.id.tag_2);
                            AlertDialog.Builder alt_bld = new AlertDialog.Builder(
                                    mContext);
                            alt_bld.setMessage(
                                    "Are you sure want to delete it?")
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            "Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                    ProgressDialog.showProgress(mContext);
                                                    photos.get(imageId).deletePhoto(new ModelDelegates.CommonDelegate() {

                                                        @Override
                                                        public void UpdateSuccessFully(
                                                                boolean b) {
                                                            ProgressDialog.hideProgress();
                                                            Toast.makeText(mContext, "Photo has been deleted successfully", Toast.LENGTH_LONG)
                                                                    .show();
                                                            mListener.onRefresh();
                                                        }

                                                        @Override
                                                        public void UpdateFail(
                                                                String ErrorMessage) {
                                                            ProgressDialog.hideProgress();
                                                            Toast.makeText(mContext, ErrorMessage, Toast.LENGTH_LONG)
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

                            return true;
                        }
                    });
            return rowView;
        }
    }

    public static class ViewHolder {
        ImageView imgPhoto;
        RelativeLayout rl_main_list_item;
    }
}
