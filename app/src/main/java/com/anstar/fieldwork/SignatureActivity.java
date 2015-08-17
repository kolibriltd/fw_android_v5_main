package com.anstar.fieldwork;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.anstar.common.Const;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.SignaturePoints;
import com.anstar.models.list.AppointmentModelList;

import java.util.ArrayList;
import java.util.Collections;

public class SignatureActivity extends AppCompatActivity implements OnClickListener {
	private Button btnClearCustomerSign, btnClearTechnicianSign;
	private LinearLayout llCustomerSignCanvas, llTechnitianSignCanvas;
	private Canvas mCanvas;
	private CustomerSignatureView viewCustSign;
	private TechSignatureView viewTechSign;
	private int width;
	private ArrayList<SignaturePoints> CustomersignInfo;
	private ArrayList<SignaturePoints> TechsignInfo;
	AppointmentInfo appointment_info = null;
	int a_id = 0;
	//ActionBar action = null;
	float Cfactor = 0;
	float Tfactor = 0;
	int height = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_signature);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			a_id = b.getInt(Const.Appointment_Id);
		}
		if (a_id == 0) {
			a_id = Const.app_id;
		}
		// a_id = Const.app_id;
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		appointment_info = AppointmentModelList.Instance().getAppointmentById(
				a_id);
/*
		action = getSupportActionBar();
		// action.setTitle("Signature");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color) + "'>Signature</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/
/*
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);
*/

		// btnDone = (Button) findViewById(R.id.btnDone);
		llCustomerSignCanvas = (LinearLayout) findViewById(R.id.llCustomerSignCanvas);
		btnClearCustomerSign = (Button) findViewById(R.id.btnClearCustomerSign);
		llTechnitianSignCanvas = (LinearLayout) findViewById(R.id.llTechnitianSignCanvas);
		btnClearTechnicianSign = (Button) findViewById(R.id.btnClearTechnicianSign);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		width = displaymetrics.widthPixels;
		height = displaymetrics.heightPixels;
		CustomersignInfo = new ArrayList<SignaturePoints>();
		TechsignInfo = new ArrayList<SignaturePoints>();

		// LoadStatus();

		btnClearCustomerSign.setOnClickListener(this);
		btnClearTechnicianSign.setOnClickListener(this);

	};

	@Override
	protected void onResume() {
		super.onResume();
		loadView load = new loadView();
		load.execute();

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
			float viewWidth = llCustomerSignCanvas.getWidth();
			float viewHight = llCustomerSignCanvas.getHeight() + 300;
			float ChighestPoint = 0;
			float ThighestPoint = 0;

			float factorx = 0;
			float factory = 0;
			if (appointment_info != null) {
				CustomersignInfo = SignaturePoints.parse(
						appointment_info.customer_signature, Const.Customer);
				ChighestPoint = getHighest(CustomersignInfo);
				factorx = viewWidth / ChighestPoint;
				factory = viewHight / ChighestPoint;
				Cfactor = factorx > factory ? factory : factorx;
				if (Cfactor < 0) {
					Cfactor = (float) 0.5;
				}
				Cfactor = (float) 0.5;
				TechsignInfo = SignaturePoints
						.parse(appointment_info.technician_signature,
								Const.Technitian);
				ThighestPoint = getHighest(TechsignInfo);
				factorx = viewWidth / ThighestPoint;
				factory = viewHight / ThighestPoint;
				Tfactor = factorx > factory ? factory : factorx;
				if (Tfactor < 0) {
					Tfactor = (float) 0.5;
				}
				Tfactor = (float) 0.5;
				viewCustSign = new CustomerSignatureView(
						getApplicationContext());
				viewCustSign.onMeasure(300, 150);
				llCustomerSignCanvas.addView(viewCustSign);
				viewTechSign = new TechSignatureView(getApplicationContext());
				viewTechSign.onMeasure(300, 150);
				llTechnitianSignCanvas.addView(viewTechSign);
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
			if (CustomersignInfo.size() > 0) {
				float h = 0;
				if (height > 950) {
					h = (float) 0.3;
				} else {
					h = Cfactor;
				}
				if (width <= 480) {
					Cfactor = (float) 0.450;
				}
				for (SignaturePoints points : CustomersignInfo) {
					canvas.drawLine(points.lx * Cfactor, points.ly * h,
							points.mx * Cfactor, points.my * h, paint);
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
			if (TechsignInfo.size() > 0) {
				float h = 0;
				if (height > 950) {
					h = (float) 0.3;
				} else {
					h = Tfactor;
				}
				if (width <= 480) {
					Tfactor = (float) 0.450;
				}
				for (SignaturePoints points : TechsignInfo) {
					canvas.drawLine(points.lx * Tfactor, points.ly * h,
							points.mx * Tfactor, points.my * h, paint);
				}
			}
			canvas.drawPath(path, paint);

		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

	}

	@Override
	public void onClick(View v) {
		if (v == btnClearCustomerSign) {
			// viewCustSign.clearCustomer();
			Intent i = new Intent(SignatureActivity.this,
					CaptureSignatureActivity.class);
			i.putExtra("cutomer", Const.Customer);
			i.putExtra(Const.Appointment_Id, a_id);
			startActivity(i);
			finish();
		} else if (v == btnClearTechnicianSign) {
			// viewTechSign.clearTech();
			Intent i = new Intent(SignatureActivity.this,
					CaptureSignatureActivity.class);
			i.putExtra("tech", Const.Technitian);
			i.putExtra(Const.Appointment_Id, a_id);
			startActivity(i);
			finish();
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
