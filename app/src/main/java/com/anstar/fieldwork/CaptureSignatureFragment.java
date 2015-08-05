package com.anstar.fieldwork;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anstar.dialog.ProgressDialog;
import com.anstar.common.Const;
import com.anstar.common.JsonCreator;
import com.anstar.common.Utils;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.SignaturePoints;
import com.anstar.models.UserInfo;
import com.anstar.models.list.AppointmentModelList;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CaptureSignatureFragment extends Fragment implements
		OnClickListener {

	ArrayList<SignaturePoints> m_points = null;
	private Canvas mCanvas;
	private LinearLayout llSignCanvas;
	Button btnCancel, btnClear, btnSave;
	SignatureView signatureview;
	String signtype = "";
	TextView txtSignType, txtSignName, txtLicInfo;
	AppointmentInfo appointment_info = null;
	int a_id = 0;
	float Cfactor = 0;
	private CaptureSignatureFragmentListener mCaptureSignatureFragmentListener;
	// Container Activity must implement this interface
	public interface CaptureSignatureFragmentListener {
		void onCaptureSignatureSaved();
		void onCaptureSignatureCancelClicked();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_capture_siganture, container, false);

		llSignCanvas = (LinearLayout) v.findViewById(R.id.llSignCanvas);
		txtSignType = (TextView) v.findViewById(R.id.txtSignatureType);
		txtSignName = (TextView) v.findViewById(R.id.txtSignName);
		txtLicInfo = (TextView) v.findViewById(R.id.txtSignLic);
		btnCancel = (Button) v.findViewById(R.id.btnCancel);
		btnClear = (Button) v.findViewById(R.id.btnClear);
		btnSave = (Button) v.findViewById(R.id.btnSave);
		btnCancel.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnSave.setOnClickListener(this);

		loadView load = new loadView();
		load.execute();

		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = getArguments();
		if (b != null) {
			if (b.containsKey("cutomer")) {
				signtype = b.getString("cutomer");
			}
			if (b.containsKey("tech")) {
				signtype = b.getString("tech");
			}
			if (b.containsKey(Const.Appointment_Id)) {
				a_id = b.getInt(Const.Appointment_Id);
			}
		}
		m_points = new ArrayList<>();
		appointment_info = AppointmentModelList.Instance().getAppointmentById(
				a_id);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCaptureSignatureFragmentListener = (CaptureSignatureFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement CaptureSignatureFragmentListener");
		}
	}

	public class SignatureView extends View {

		private static final float STROKE_WIDTH = 2f;

		/** Need to track this so the dirty region can accommodate the stroke. **/
		private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

		private Paint paint = new Paint();
		private Path path = new Path();

		/**
		 * Optimizes painting by invalidating the smallest possible area.
		 */
		private float lastTouchX;
		private float lastTouchY;
		private final RectF dirtyRect = new RectF();

		public SignatureView(Context context) {
			super(context);

			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeWidth(STROKE_WIDTH);
		}

		/**
		 * Erases the signature.
		 */
		public void clear() {
			m_points = new ArrayList<SignaturePoints>();
			path = new Path();
			mCanvas = new Canvas();
			mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
			draw(mCanvas);
			this.invalidate();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// if (m_points.size() > 0) {
			// for (SignaturePoints points : m_points) {
			// canvas.drawLine(points.lx * Cfactor, points.ly * Cfactor,
			// points.mx * Cfactor, points.my * Cfactor, paint);
			// }
			// }
			canvas.drawPath(path, paint);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			float eventX = event.getX();
			float eventY = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				path.moveTo(eventX, eventY);
				lastTouchX = eventX;
				lastTouchY = eventY;
				// There is no end point yet, so don't waste cycles
				// invalidating.
				return true;

			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_UP:
				// Start tracking the dirty region.
				resetDirtyRect(eventX, eventY);

				// When the hardware tracks events faster than they are
				// delivered, the
				// event will contain a history of those skipped points.
				int historySize = event.getHistorySize();
				for (int i = 0; i < historySize; i++) {
					float historicalX = event.getHistoricalX(i);
					float historicalY = event.getHistoricalY(i);
					expandDirtyRect(historicalX, historicalY);
					path.lineTo(historicalX, historicalY);
					// signInfo = new ArrayList<SignaturePoints>();
				}

				// After replaying history, connect the line to the touch point.
				path.lineTo(eventX, eventY);
				SignaturePoints info = new SignaturePoints();
				info.lx = eventX;
				info.ly = eventY;
				info.mx = lastTouchX;
				info.my = lastTouchY;
				m_points.add(info);
				break;

			default:
				Utils.LogInfo("Ignored touch event: " + event.toString());
				return false;
			}

			// Include half the stroke width to avoid clipping.
			invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
					(int) (dirtyRect.top - HALF_STROKE_WIDTH),
					(int) (dirtyRect.right + HALF_STROKE_WIDTH),
					(int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

			lastTouchX = eventX;
			lastTouchY = eventY;

			return true;
		}

		/**
		 * Called when replaying history to ensure the dirty region includes all
		 * points.
		 */
		private void expandDirtyRect(float historicalX, float historicalY) {
			if (historicalX < dirtyRect.left) {
				dirtyRect.left = historicalX;
			} else if (historicalX > dirtyRect.right) {
				dirtyRect.right = historicalX;
			}
			if (historicalY < dirtyRect.top) {
				dirtyRect.top = historicalY;
			} else if (historicalY > dirtyRect.bottom) {
				dirtyRect.bottom = historicalY;
			}
		}

		/**
		 * Resets the dirty region when the motion event occurs.
		 */
		private void resetDirtyRect(float eventX, float eventY) {

			// The lastTouchX and lastTouchY were set when the ACTION_DOWN
			// motion event occurred.
			dirtyRect.left = Math.min(lastTouchX, eventX);
			dirtyRect.right = Math.max(lastTouchX, eventX);
			dirtyRect.top = Math.min(lastTouchY, eventY);
			dirtyRect.bottom = Math.max(lastTouchY, eventY);
		}
	}

	public class SaveSignatureData extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ProgressDialog.showProgress(getActivity());
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<HashMap<String, Object>> mainlist = new ArrayList<HashMap<String, Object>>();
			for (SignaturePoints point : m_points) {
				HashMap<String, Object> inner = new HashMap<String, Object>();
				inner.put("lx", point.lx);
				inner.put("ly", point.ly);
				inner.put("mx", point.mx);
				inner.put("my", point.my);
				mainlist.add(inner);
			}
			JSONArray arr = JsonCreator.getJsonArray(mainlist);
			if (signtype.equalsIgnoreCase(Const.Customer)) {
				AppointmentInfo.saveSignature(appointment_info.getID(),
						arr.toString(), Const.Customer);
			} else {
				AppointmentInfo.saveSignature(appointment_info.getID(),
						arr.toString(), Const.Technitian);
			}
			// MainActivity.reloadAppointment();
			// AppoinmentInfo temp = MainActivity.appointmentInfo;
			// ServiceHelper.Instance().saveGeneralAppointment(temp);
			// ServiceHelper.Instance().saveSignature(info.Customer_Id,
			// signInfo);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			ProgressDialog.hideProgress();
/*
			Toast.makeText(getActivity(),
					"Your signature is successfully saved...",
					Toast.LENGTH_LONG).show();
			Intent i = new Intent(CaptureSignatureFragment.this,
					SignatureActivity.class);
			startActivity(i);
			finish();
*/
			mCaptureSignatureFragmentListener.onCaptureSignatureSaved();
		}
	}

	public void loadValue() {
		if (signtype.equalsIgnoreCase(Const.Customer)) {
			txtSignType.setText(Const.Customer);
			m_points = SignaturePoints.parse(
					appointment_info.customer_signature, Const.Customer);
			// CustomerInfo info = CustomerList.Instance().getCustomer(
			// appointment_info.customer_id);
			txtSignName.setVisibility(View.GONE);
			txtLicInfo.setVisibility(View.GONE);
		} else {
			txtLicInfo.setVisibility(View.VISIBLE);
			UserInfo user = UserInfo.Instance().getUser();
			if (user != null) {
				txtLicInfo.setText("LIC# " + user.license_number);
			}
			txtSignType.setText(Const.Technitian);
			m_points = SignaturePoints.parse(
					appointment_info.technician_signature, Const.Technitian);
			if (appointment_info.technician_signature_name != null
					&& (!appointment_info.technician_signature_name
							.equalsIgnoreCase("null"))) {
				txtSignName.setText(appointment_info.technician_signature_name);
			}
		}
		float viewWidth = llSignCanvas.getWidth() + 500;
		float viewHight = llSignCanvas.getHeight() + 500;
		float highestPoint = getHighest(m_points);
		float factorx = viewWidth / highestPoint;
		float factory = viewHight / highestPoint;
		Cfactor = factorx > factory ? factory : factorx;
		if (Cfactor < 0) {
			Cfactor = (float) 0.5;
		}
		m_points = null;
		m_points = new ArrayList<SignaturePoints>();
		signatureview = new SignatureView(getActivity());
		signatureview.measure(500, 500);
		llSignCanvas.addView(signatureview);
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

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			loadValue();

		}
	}

	@Override
	public void onClick(View v) {
		if (v == btnCancel) {
/*
			Intent i = new Intent(CaptureSignatureFragment.this,
					SignatureActivity.class);
			startActivity(i);
			finish();
*/
			mCaptureSignatureFragmentListener.onCaptureSignatureCancelClicked();
		} else if (v == btnClear) {
			signatureview.clear();
		} else if (v == btnSave) {
			SaveSignatureData data = new SaveSignatureData();
			data.execute();
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
