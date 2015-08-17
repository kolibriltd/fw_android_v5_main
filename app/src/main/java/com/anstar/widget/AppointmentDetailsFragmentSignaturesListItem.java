package com.anstar.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.anstar.common.Const;
import com.anstar.fieldwork.R;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.SignaturePoints;
import com.anstar.models.list.AppointmentModelList;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by oleg on 13.08.15.
 */
public class AppointmentDetailsFragmentSignaturesListItem extends LinearLayout{
    private final Context mContext;
    private Button mBtnClearCustomerSign, mBtnClearTechnicianSign;
    private RelativeLayout mLLCustomerSignCanvas, mLLTechnitianSignCanvas;
    private Canvas mCanvas;
    private CustomerSignatureView mViewCustSign;
    private TechSignatureView mViewTechSign;
    private int mWidth;
    private int mHeight = 0;
    private ArrayList<SignaturePoints> mCustomersignInfo;
    private ArrayList<SignaturePoints> mTechsignInfo;
    AppointmentInfo mAppointmentInfo = null;
    int mAppointmentId = 0;
    //ActionBar action = null;
    float mCFactor = 0;
    float mTFactor = 0;

    public void setOnListItemInteractionListener(OnListItemInteractionListener listener) {
        mListener = listener;
    }

    private OnListItemInteractionListener mListener;

    public interface OnListItemInteractionListener {

        void onSignatureCustomerClick();
        void onSignatureTechnicianClick();
    }

    public AppointmentDetailsFragmentSignaturesListItem(Activity context) {
        super(context);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_appointment_details_signatures_list_item, this);

        mLLCustomerSignCanvas = (RelativeLayout) findViewById(R.id.rlCustomerSignCanvas);
        mBtnClearCustomerSign = (Button) findViewById(R.id.btnClearCustomerSign);
        mLLTechnitianSignCanvas = (RelativeLayout) findViewById(R.id.rlTechnitianSignCanvas);
        mBtnClearTechnicianSign = (Button) findViewById(R.id.btnClearTechnicianSign);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mWidth = displaymetrics.widthPixels;
        mHeight = displaymetrics.heightPixels;
        mCustomersignInfo = new ArrayList<SignaturePoints>();
        mTechsignInfo = new ArrayList<SignaturePoints>();

        // LoadStatus();

        mLLCustomerSignCanvas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onSignatureCustomerClick();
            }
        });

        mLLTechnitianSignCanvas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onSignatureTechnicianClick();
            }
        });
    }

    public void init(int appointmentId) {

        mAppointmentId = appointmentId;
        mAppointmentInfo = AppointmentModelList.Instance().getAppointmentById(
                appointmentId);
        mCustomersignInfo = new ArrayList<SignaturePoints>();
        mTechsignInfo = new ArrayList<SignaturePoints>();
        new loadView().execute();
    }

    public class loadView extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("WrongCall")
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            float viewWidth = mLLCustomerSignCanvas.getWidth();
            float viewHight = mLLCustomerSignCanvas.getHeight() + 300;
            float cHighestPoint = 0;
            float tHighestPoint = 0;

            float factorX = 0;
            float factorY = 0;
            if (mAppointmentInfo != null) {
                mCustomersignInfo = SignaturePoints.parse(
                        mAppointmentInfo.customer_signature, Const.Customer);
                cHighestPoint = getHighest(mCustomersignInfo);
                factorX = viewWidth / cHighestPoint;
                factorY = viewHight / cHighestPoint;
                mCFactor = factorX > factorY ? factorY : factorX;
                if (mCFactor < 0) {
                    mCFactor = (float) 0.5;
                }
                mCFactor = (float) 0.5;
                mTechsignInfo = SignaturePoints
                        .parse(mAppointmentInfo.technician_signature,
                                Const.Technitian);
                tHighestPoint = getHighest(mTechsignInfo);
                factorX = viewWidth / tHighestPoint;
                factorY = viewHight / tHighestPoint;
                mTFactor = factorX > factorY ? factorY : factorX;
                if (mTFactor < 0) {
                    mTFactor = (float) 0.5;
                }
                mTFactor = (float) 0.5;
                mViewCustSign = new CustomerSignatureView(
                        mContext.getApplicationContext());
                mViewCustSign.onMeasure(300, 150);
                mLLCustomerSignCanvas.addView(mViewCustSign);
                mViewTechSign = new TechSignatureView(
                        mContext.getApplicationContext());
                mViewTechSign.onMeasure(300, 150);
                mLLTechnitianSignCanvas.addView(mViewTechSign);
            }
        }
    }

    public class CustomerSignatureView extends View {

        private static final float STROKE_WIDTH = 2f;
        private Paint paint = new Paint();
        private Path path = new Path();

        public CustomerSignatureView(Context context) {
            super(context);
            this.setWillNotDraw(false);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (mCustomersignInfo.size() > 0) {
                float h = 0;
                if (mHeight > 950) {
                    h = (float) 0.3;
                } else {
                    h = mCFactor;
                }
                if (mWidth <= 480) {
                    mCFactor = (float) 0.450;
                }
                for (SignaturePoints points : mCustomersignInfo) {
                    canvas.drawLine(points.lx * mCFactor, points.ly * h,
                            points.mx * mCFactor, points.my * h, paint);
                }
            }
            canvas.drawPath(path, paint);

        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    public class TechSignatureView extends View {

        private static final float STROKE_WIDTH = 2f;

        /** Need to track this so the dirty region can accommodate the stroke. **/
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

        private Paint paint = new Paint();
        private Path path = new Path();

        public TechSignatureView(Context context) {
            super(context);

            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (mTechsignInfo.size() > 0) {
                float h = 0;
                if (mHeight > 950) {
                    h = (float) 0.3;
                } else {
                    h = mTFactor;
                }
                if (mWidth <= 480) {
                    mTFactor = (float) 0.450;
                }
                for (SignaturePoints points : mTechsignInfo) {
                    canvas.drawLine(points.lx * mTFactor, points.ly * h,
                            points.mx * mTFactor, points.my * h, paint);
                }
            }
            canvas.drawPath(path, paint);

        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    public float getHighest(ArrayList<SignaturePoints> m_list) {
        if (m_list != null && m_list.size() > 0) {
            ArrayList<Float> temp = new ArrayList<Float>();
            for (SignaturePoints s : m_list) {
                temp.add(s.lx);
                temp.add(s.ly);
                temp.add(s.mx);
                temp.add(s.my);
            }
            float h = Collections.max(temp);
            return h;
        } else {
            return 0;
        }

    }

}
