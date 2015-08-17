package com.anstar.widget;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anstar.common.Const;
import com.anstar.fieldwork.R;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.PdfFormsInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.PdfFormsList;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by oleg on 13.08.15.
 */
public class AppointmentDetailsFragmentPDFFormsListItem extends LinearLayout{
    private final Context mContext;
    private final LinearLayout mTable;
    private int mAppointmentId;
    private AppointmentInfo mAppointmentInfo;
    private ArrayList<PdfFormsInfo> mPdfForms;

    public void setOnListItemInteractionListener(OnListItemInteractionListener listener) {
        mListener = listener;
    }

    private OnListItemInteractionListener mListener;

    public interface OnListItemInteractionListener {
        void onButtonAddClick(int appointmentId);
    }

    public AppointmentDetailsFragmentPDFFormsListItem(Context context) {
        super(context);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_appointment_details_pdf_forms_list_item, this);

        mTable = (LinearLayout) findViewById(R.id.linearLayoutList);

        Button buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onButtonAddClick(mAppointmentId);
                }
            }
        });
    }

    public void init(int appointmentId) {

        mTable.removeAllViews();

        mAppointmentId = appointmentId;
        mAppointmentInfo = AppointmentModelList.Instance().getAppointmentById(appointmentId);
        mPdfForms = PdfFormsList.Instance().load(appointmentId);

        for (PdfFormsInfo pdf : mPdfForms) {
            TextView text = new TextView(mContext);
            text.setText(pdf.name);
            text.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
            text.setTextColor(getResources().getColorStateList(android.R.color.black));
            text.setMinLines(2);

            mTable.addView(text);
        }
    }

    public boolean isFileExist(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public String getStoragePath(String filename) {
        String path = "";
        if (isSDCARDMounted()) {
            path = Environment.getExternalStorageDirectory().getPath();
        }
        path = path + File.separator + Const.DIRECTORY_NAME;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }

        path = path + File.separator + filename;

        return path;
    }

    public boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }
}
