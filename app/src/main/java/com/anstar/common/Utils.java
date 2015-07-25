package com.anstar.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;

import com.anstar.fieldwork.R;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceCaller.RequestMethod;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.DeviceTypesInfo;
import com.anstar.models.LocationAreaInfo;
import com.anstar.models.MaterialInfo;
import com.anstar.models.PestsTypeInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

/**
 * The class contains common utility functions which helps whole application
 * 
 * @author sam
 * 
 */
public class Utils {
	private static volatile Utils _instance = null;


	public static void showAnimatedFragment(AppCompatActivity activity, Fragment fragment, String tag, boolean add) {
		FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.fragment_animation_pop_enter, R.anim.fragment_animation_pop_exit,
				R.anim.fragment_animation_enter, R.anim.fragment_animation_exit);
		transaction.replace(R.id.container, fragment, tag);
		if (add) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}
	/**
	 * Get the Instance of the Utils Class
	 * 
	 * @return Utils Object
	 */
	public static Utils Instance() {
		if (_instance == null) {
			synchronized (Utils.class) {
				_instance = new Utils();
			}
		}
		return _instance;
	}

	/**
	 * This is the function which joins the String Array with delimiter. Android
	 * does not contains any kind of function works like this.
	 * 
	 * @param s
	 *            - List of String (CharSequence)
	 * @param delimiter
	 * @return String
	 */
	public String join(List<? extends CharSequence> s, String delimiter) {
		int capacity = 0;
		int delimLength = delimiter.length();
		Iterator<? extends CharSequence> iter = s.iterator();
		if (iter.hasNext()) {
			capacity += iter.next().length() + delimLength;
		}

		StringBuilder buffer = new StringBuilder(capacity);
		iter = s.iterator();
		if (iter.hasNext()) {
			buffer.append(iter.next());
			while (iter.hasNext()) {
				buffer.append(delimiter);
				buffer.append(iter.next());
			}
		}
		return buffer.toString();
	}

	public ArrayList<String> Split(String str, String delimiter) {
		ArrayList<String> arr = new ArrayList<String>();
		String[] ss = str.split(delimiter);
		for (int i = 0; i < ss.length; i++) {
			arr.add(ss[i]);
		}
		return arr;
	}

	private float m_density = 0;

	public void setDensity(float density) {
		m_density = density;
	}

	public float getDensity() {
		return m_density;
	}

	/**
	 * Log Application Exception over here. To keep consistancy, this function
	 * was developed
	 * 
	 * @param ex
	 *            - Exception
	 */
	public static void LogException(Exception ex) {
		Log.d("FIELDWORK Exception",
				"FIELDWORK Exception -- > " + ex.getMessage() + "\n" + ex);
	}

	/**
	 * Log any debug info over here. To keep consistancy, this function was
	 * developed
	 * 
	 * @param message
	 */
	public static void LogInfo(String message) {
		Log.i("FIELDWORK", "FIELDWORK -- >" + message);
		writeContent(message);
	}

	/**
	 * Convert String value to Boolean
	 * 
	 * @param val
	 * @return boolean
	 */
	public static boolean ParseBoolean(String val) {
		try {
			boolean bl = Boolean.valueOf(val);
			return bl;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get dip unit from the pixel for the perticulat density Density stored at
	 * the starting point of the application, from the splash screen
	 * 
	 * @param pixel
	 * @return dip
	 */
	public int getDipFromPixel(int pixel) {
		if (m_density <= 0) {
			return pixel;
		}
		float density = m_density;
		int dip = (int) (pixel * density);
		return dip;
	}

	/**
	 * Convert String to Integer Value
	 * 
	 * @param val
	 * @return Integer Value
	 */
	public static int ConvertToInt(String val) {
		int intval = 0;
		try {
			intval = Integer.parseInt(val);
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return intval;
	}

	public static double ConvertToDouble(String val) {
		double d = 0;
		try {
			d = Double.parseDouble(val);
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return d;
	}

	public static long ConvertToLong(String val) {
		long d = 0;
		try {
			d = Long.parseLong(val);
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return d;
	}

	public static boolean ConvertToBoolean(String val, boolean defaultval) {
		boolean flag = defaultval;
		try {
			flag = Boolean.valueOf(val);
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return flag;
	}

	/**
	 * Convert String to Float value
	 * 
	 * @param val
	 * @return Float Value
	 */
	public static float ConvertToFloat(String val) {
		float intval = 0;
		try {
			intval = Float.parseFloat(val);
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return intval;
	}

	/**
	 * Convert String to java.utils.Date
	 * 
	 * @param val
	 * @return Date
	 */
	public static Date ConvertToDate(String val) {
		Date retval = null;
		try {
			if (val == null || val.length() <= 0) {
				return retval;
			}
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			retval = dateformat.parse(val);
		} catch (Exception e) {
			Utils.LogException(e);
		}

		return retval;
	}

	public static Date ConvertToDate(String val, String format) {
		Date retval = null;
		try {
			if (val == null || val.length() <= 0) {
				return retval;
			}
			SimpleDateFormat dateformat = new SimpleDateFormat(format);
			retval = dateformat.parse(val);
		} catch (Exception e) {
			Utils.LogException(e);
		}

		return retval;
	}

	public static Date ConvertToDateComarator(String val) {
		Date retval = null;
		try {
			if (val == null || val.length() <= 0) {
				return retval;
			}
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
			retval = dateformat.parse(val);
		} catch (Exception e) {
			Utils.LogException(e);
		}

		return retval;
	}

	public static int[] convertIntegers(ArrayList<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			if(integers.get(i) > 0 )
				ret[i] = integers.get(i).intValue();
		}
	
		return ret;
	}

	/**
	 * This Function is used only on development process which reads the static
	 * service response from the Asset file and do not call to service each
	 * time.
	 * 
	 * @param fileName
	 * @param context
	 * @return Json String
	 */
	@SuppressWarnings("static-access")
	public String ReadFromfile(String fileName, Context context) {
		StringBuilder ReturnString = new StringBuilder();
		InputStream fIn = null;
		InputStreamReader isr = null;
		BufferedReader input = null;
		try {
			fIn = context.getResources().getAssets()
					.open(fileName, context.MODE_WORLD_READABLE);
			isr = new InputStreamReader(fIn);
			input = new BufferedReader(isr);
			String line = "";
			while ((line = input.readLine()) != null) {
				ReturnString.append(line);
			}
		} catch (Exception e) {
			e.getMessage();
		} finally {
			try {
				if (isr != null)
					isr.close();
				if (fIn != null)
					fIn.close();
				if (input != null)
					input.close();
			} catch (Exception e2) {
				e2.getMessage();
			}
		}
		return ReturnString.toString();
	}

	public static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String getUTCForCSharp(String javaUtc) {
		try {
			return String
					.valueOf(adjustTimezoneOffset(Long.parseLong(javaUtc)) * 10000 + 621355968000000000L);
		} catch (Exception e) {
		}
		return "";
	}

	public static long adjustTimezoneOffset(long utcMillies) {
		TimeZone tz = TimeZone.getTimeZone("CET");
		long offset = tz.getOffset(new Date().getTime());
		return utcMillies + offset;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			if (listItem instanceof ViewGroup)
				listItem.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);

	}

	public static String RenameFileToNewPath(String oldpath) {
		String path = oldpath;
		path = path.replace("/mnt/sdcard/", "");
		String new_path = path.replace(".ck", "");
		File sdcard = Environment.getExternalStorageDirectory();
		File from = new File(sdcard, oldpath);
		File to = new File(sdcard, new_path);
		from.renameTo(to);
		return to.toString();
	}

	public static void RenameFileToOldPath(String newpath) {
		String path = newpath;
		String old = path + ".ck";
		File sdcard = Environment.getExternalStorageDirectory();
		File from = new File(sdcard, newpath);
		File to = new File(sdcard, old);
		from.renameTo(to);
	}

	public static boolean isSameDate(Date d1, Date d2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s = sdf.format(d1);
		try {
			d1 = sdf.parse(s);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		s = sdf.format(d2);
		try {
			d2 = sdf.parse(s);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		int days1 = (int) (d1.getTime() / 86400);
		int days2 = (int) (d2.getTime() / 86400);
		return days1 == days2;
	}

	public static void sendEmail(String contactEmail, Activity act) {
		final Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND);
		// Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
		// "mailto", contactEmail, null));
		emailIntent.setType("vnd.android.cursor.dir/email");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { contactEmail });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
		act.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

	public static void callPhone(String number, Activity act) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + number));
		act.startActivity(callIntent);
	}

	public static Address getLocationFromAddress(String Add, Activity act) {
		Geocoder coder = new Geocoder(act);
		List<Address> address;
		// Add =
		// "L D College Of Engineering, 120 Circular Road,University Area,Ahmedabad,Gujarat 380015";
		try {
			address = coder.getFromLocationName(Add, 5);
			if (address == null) {
				return null;
			}
			Address location = address.get(0);
			location.getLatitude();
			location.getLongitude();

			return location;
		} catch (Exception e) {
			Utils.LogException(e);
			return null;
		}
	}

	public static Location getCurrentLocation(Activity act) {
		try {
			LocationManager lm = (LocationManager) act
					.getSystemService(Context.LOCATION_SERVICE);
			boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if (!isGPS) {
				Utils.LogInfo("GPS is Not Active ....................");
				Intent intent = new Intent(
						"android.location.GPS_ENABLED_CHANGE");
				intent.putExtra("enabled", true);
				act.sendBroadcast(intent);
			}

			Location location = lm
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location == null) {
				location = lm
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			// Utils.LogInfo("LOCATION Lat----->>>" + location.getLatitude()
			// + "  LOCATION Log----->>>" + location.getLongitude());
			return location;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getRandomInt() {
		int max = 100;
		int min = 1;
		Random r = new Random();
		int i = r.nextInt(max - min) + min;
		i = i * (-1);
		return i;
	}

	public String ChangeDateFormat(String currentFormat,
			String convertedFormat, String date) {
		String newDateStr = "";
		SimpleDateFormat sdf = new SimpleDateFormat(currentFormat);
		try {
			Date dateObj = sdf.parse(date);
			SimpleDateFormat postFormater = new SimpleDateFormat(
					convertedFormat);
			newDateStr = postFormater.format(dateObj);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return newDateStr;
	}

	public ArrayList<PestsTypeInfo> sortPestCollections(List<PestsTypeInfo> list) {
		if (list != null) {
			Collections.sort(list, AppComparators.Instance().PestByAtoZ);
			return new ArrayList<PestsTypeInfo>(list);
		}
		return new ArrayList<PestsTypeInfo>();
	}

	public ArrayList<MaterialInfo> sortMaterialCollections(
			List<MaterialInfo> list) {
		if (list != null) {
			Collections.sort(list, AppComparators.Instance().MaterialByAtoZ);
			return new ArrayList<MaterialInfo>(list);
		}
		return new ArrayList<MaterialInfo>();
	}

	public ArrayList<LocationAreaInfo> sortLocationCollections(
			List<LocationAreaInfo> list) {
		if (list != null) {
			Collections
					.sort(list, AppComparators.Instance().LocationAreaByAtoZ);
			return new ArrayList<LocationAreaInfo>(list);
		}
		return new ArrayList<LocationAreaInfo>();
	}

	public ArrayList<DeviceTypesInfo> sortDeviceTypesCollections(
			List<DeviceTypesInfo> list) {
		if (list != null) {
			Collections.sort(list, AppComparators.Instance().DeviceTypeByAtoZ);
			return new ArrayList<DeviceTypesInfo>(list);
		}
		return new ArrayList<DeviceTypesInfo>();
	}

	public String getFormatedDate(String date, String oldformat,
			String newformat) {
		String newDateStr = "";
		SimpleDateFormat sdf = new SimpleDateFormat(oldformat);
		try {
			Date dateObj = sdf.parse(date);
			SimpleDateFormat postFormater = new SimpleDateFormat(newformat);
			newDateStr = postFormater.format(dateObj);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return newDateStr;
	}

	public static HashMap<String, String> printerErrors = new HashMap<String, String>();
	static {
		printerErrors.put("ERROR_NOT_SAME_MODEL",
				"Error : Found a different type of printer");
		printerErrors.put("ERROR_BROTHER_PRINTER_NOT_FOUND",
				"Error : Cannot find a Brother printer");
		printerErrors.put("ERROR_PAPER_EMPTY", "Error : Paper empty");
		printerErrors.put("ERROR_BATTERY_EMPTY", "Error : Battery weak");
		printerErrors.put("ERROR_COMMUNICATION_ERROR",
				"Error : Failed to retrieve printer status");
		printerErrors.put("ERROR_OVERHEAT", "Error : Overheat error");
		printerErrors.put("ERROR_PAPER_JAM", "Error : Paper Jam");
		printerErrors.put("ERROR_HIGH_VOLTAGE_ADAPTER",
				"Error : High-voltage adaptor");
		printerErrors.put("ERROR_CHANGE_CASSETTE",
				"Error : Cassette-change during printing");
		printerErrors.put("ERROR_FEED_OR_CASSETTE_EMPTY",
				"Error : Feed error or paper empty");
		printerErrors.put("ERROR_SYSTEM_ERROR", "Error : System error");
		printerErrors.put("ERROR_NO_CASSETTE", "Error : No paper-cassette");
		printerErrors.put("ERROR_WRONG_CASSETTE_DIRECT",
				"Error : Wrong paper-cassette direction");
		printerErrors.put("ERROR_CREATE_SOCKET_FAILED",
				"Error : Failed to create socket");
		printerErrors.put("ERROR_CONNECT_SOCKET_FAILED",
				"Error : Failed to connect ?1");
		printerErrors.put("ERROR_GET_OUTPUT_STREAM_FAILED",
				"Error : Failed to retrieve output stream");
		printerErrors.put("ERROR_GET_INPUT_STREAM_FAILED",
				"Error : Failed to retrieve input stream");
		printerErrors.put("ERROR_CLOSE_SOCKET_FAILED",
				"Error : Failed to close socket");
		printerErrors.put("ERROR_OUT_OF_MEMORY",
				"Error : Insufficient memory ?2");
		printerErrors
				.put("ERROR_SET_OVER_MARGIN", "Error : Over set margin ?3");
		printerErrors.put("ERROR_NO_SD_CARD", "Error : No SD card");
		printerErrors.put("ERROR_FILE_NOT_SUPPORTED",
				"Error : Non supported file");
		printerErrors.put("ERROR_EVALUATION_TIMEUP",
				"Error : Expired trial period");
		printerErrors.put("ERROR_WRONG_CUSTOM_INFO",
				"Error : Wrong information in");
		printerErrors.put("custom paper setting file", "ERROR_NO_ADDRESS");
		printerErrors.put("Error: not set IP and MAC address",
				"ERROR_NO_MATCH_ADDRESS");
		printerErrors.put("Error: IP and Mac address is not match",
				"for selected printer");
		printerErrors.put("ERROR_FILE_NOT_FOUND", "Error: File do not exist");
		printerErrors.put("ERROR_TEMPLATE_FILE_NOT_MATCH_MODEL",
				"Error: Template file is not a match for selected printer");
		printerErrors.put("ERROR_TEMPLATE_NOT_TRANS_MODEL",
				"Error: Selected printer model do not support Template print");
		printerErrors.put("ERROR_COVER_OPEN", "Error: The cover is open (RJ)");
	}

	public void copy(File src, File dst) throws IOException {
		FileInputStream inStream = new FileInputStream(src);
		FileOutputStream outStream = new FileOutputStream(dst);
		FileChannel inChannel = inStream.getChannel();
		FileChannel outChannel = outStream.getChannel();
		inChannel.transferTo(0, inChannel.size(), outChannel);
		inStream.close();
		outStream.close();
	}

	public static void writeContent(String content) {
		if (content.length() <= 0) {
			return;
		}
		File file = new File(Environment.getExternalStorageDirectory(),
				"FIELDWORK_LOG" + ".txt");
		try {

			Date dt = new Date();
			SimpleDateFormat format = new SimpleDateFormat(
					"dd-MM-yyyy HH:mm:ss");
			String date = format.format(dt);

			FileWriter writer = new FileWriter(file, true);
			writer.append(date + " : " + content + "\r\n");
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			// handle exception
		} catch (Exception e) {
			// handle exception
		}

	}

	public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
			throws FileNotFoundException {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri),
				null, o);

		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;

		while (true) {
			if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(c.getContentResolver()
				.openInputStream(uri), null, o2);
	}

	public static void sendLocationPeriodic(String lat, String lng) {
		String json = "{\"lat\": \"%s\", \"lng\": \"%s\"}";
		json = String.format(json, lat, lng);
		ServiceCaller caller = new ServiceCaller(ServiceHelper.SendLocation,
				RequestMethod.POST, json);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {

			}

			@Override
			public void CallFailure(String ErrorMessage) {

			}
		});

	}

	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

}
