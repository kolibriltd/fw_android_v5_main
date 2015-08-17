package com.anstar.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.dialog.ProgressDialog;
import com.anstar.fieldwork.R;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.LineItemsInfo;
import com.anstar.models.ModelDelegates;
import com.anstar.models.list.LineItemsList;

import java.util.ArrayList;

/**
 * Created by oleg on 13.08.15.
 */
public class AppointmentDetailsFragmentLineItemsListItem extends LinearLayout
        implements View.OnClickListener, View.OnLongClickListener {
    private final Activity mContext;
    private final TextView mTotalPrice;
    private final TableLayout mTable;
    private ArrayList<LineItemsInfo> mLineItems;
    private int mAppointmentId;

    public void setOnListItemInteractionListener(OnListItemInteractionListener listener) {
        mListener = listener;
    }

    private OnListItemInteractionListener mListener;

    public interface OnListItemInteractionListener {

        void onButtonEditClick();

        void onButtonPayNowClick();

        void onLineItemClick(int position);

        void onLineItemDelete();
    }

    public AppointmentDetailsFragmentLineItemsListItem(Activity context) {
        super(context);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_appointment_details_line_items_list_item, this);

        mTable = (TableLayout) findViewById(R.id.tablaLayoutItems);
        mTotalPrice = (TextView) findViewById(R.id.textViewTotalPrice);

        Button button = (Button) findViewById(R.id.buttonEdit);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onButtonEditClick();
                }
            }
        });
        button = (Button) findViewById(R.id.buttonPayNow);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onButtonPayNowClick();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        mListener.onLineItemClick(position);
    }

    @Override
    public boolean onLongClick(final View v) {
        final int position = (int) v.getTag();

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(mContext);
        v.setBackgroundColor(Color
                .parseColor("#09B2F1"));
        alt_bld.setMessage("Are you sure want to delete it?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog,
                                    int id) {
                                dialog.cancel();
                                ProgressDialog.showProgress(mContext);
                                LineItemsInfo
                                        .DeleteLineItem(
                                                mLineItems.get(position).id,
                                                new ModelDelegates.UpdateInfoDelegate() {
                                                    @Override
                                                    public void UpdateSuccessFully(
                                                            ServiceResponse res) {
                                                        try {
                                                            ProgressDialog.hideProgress();
                                                            mLineItems = LineItemsList
                                                                    .Instance()
                                                                    .load(mAppointmentId);
                                                            mListener.onLineItemDelete();
                                                            Toast.makeText(mContext,
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
                                                        Toast.makeText(mContext,
                                                                "There is some error",
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
                                v.setBackgroundColor(Color.TRANSPARENT);
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alt_bld.create();
        alert.setTitle("Alert");
        alert.show();
        return true;
    }

    public void init(int appointmentId) {

        mAppointmentId = appointmentId;
        mLineItems = LineItemsList.Instance().load(appointmentId);
        refresh();
    }

    private void refresh() {

        deleteItems();

        float totalPrice = 0;

        for (int i = 0; i < mLineItems.size(); i++) {
            LineItemsInfo l = mLineItems.get(i);
            TableRow tr = new TableRow(mContext);
            tr.setClickable(true);
            tr.setTag(i);
/*
            tr.setOnClickListener(this);
            tr.setOnLongClickListener(this);
*/

            TextView text = new TextView(mContext);
            text.setText(l.name);
            text.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
            text.setTextColor(getResources().getColorStateList(android.R.color.black));
            text.setMinLines(2);
            tr.addView(text);

            text = new TextView(mContext);
            text.setText(l.quantity);
            text.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
            text.setTextColor(getResources().getColorStateList(android.R.color.black));
            tr.addView(text);

            text = new TextView(mContext);
            text.setText(l.price);
            text.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
            text.setTextColor(getResources().getColorStateList(android.R.color.black));
            text.setGravity(Gravity.RIGHT);
            tr.addView(text);

            mTable.addView(tr, i + 1);

            totalPrice += l.total;
        }

        mTotalPrice.setText(String.format("$%1.2f", totalPrice));
    }

    private void deleteItems() {
        while (mTable.getChildCount() > 2) {
            mTable.removeViewAt(1);
        }
    }
}
