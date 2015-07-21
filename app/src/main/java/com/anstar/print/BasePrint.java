/**
 * BasePrint for printing
 * 
 * @author Brother Industries, Ltd.
 * @version 2.2
 */

package com.anstar.print;

import java.util.Set;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.anstar.fieldwork.BaseActivity;
import com.anstar.fieldwork.R;
import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterInfo.ErrorCode;
import com.brother.ptouch.sdk.PrinterInfo.Model;
import com.brother.ptouch.sdk.PrinterStatus;

public abstract class BasePrint {

    protected SharedPreferences sharedPreferences;
    protected String customSetting;
    public static Printer mPrinter;
    protected PrinterStatus mPrintResult;
    protected PrinterInfo mPrinterInfo;
    protected Context mContext;
    protected MsgHandle mHandle;
    protected MsgDialog mDialog;
    protected LabelInfo mLabelInfo;

	protected abstract void doPrint();

    public BasePrint(Context context, MsgHandle handle, MsgDialog dialog) {

        mContext = context;
        mDialog = dialog;
        mHandle = handle;
        mDialog.setHandle(mHandle);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // initialization for print
        mPrinterInfo = new PrinterInfo();
        mPrinter = new Printer();
        mLabelInfo = new LabelInfo();
        mPrinterInfo = mPrinter.getPrinterInfo();
       // mPrinter.setMessageHandle(mHandle, Common.MSG_SDK_EVENT);
    }
    

    public static void cancel(){
    	if(mPrinter != null) mPrinter.cancel();
    }
    /** set PrinterInfo */
    public void setPrinterInfo() {

        getPreferences();
        setCustomPaper();
        mPrinter.setPrinterInfo(mPrinterInfo);
        mPrinter.setLabelInfo(mLabelInfo);
    	if(mPrinterInfo.port == PrinterInfo.Port.USB){
			while(true){
				if(Common.mUsbRequest != 0) break;
			}					
			if(Common.mUsbRequest != 1) return;
    	}
    }

    /** get PrinterInfo */
    public PrinterInfo getPrinterInfo() {
    	getPreferences();
        return mPrinterInfo;
    }

    /** get Printer */
    public Printer getPrinter() {

        return mPrinter;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {

        mPrinter.setBluetooth(bluetoothAdapter);
    }

    @TargetApi(12)
    public UsbDevice getUsbDevice(UsbManager usbManager){
    	return mPrinter.getUsbDevice(usbManager);
    }
    
    private void getPreferences() {
        String macaddress = "", d = "";
        Set<BluetoothDevice> pairedDevices = BaseActivity.bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
//            	if(device.getName().equalsIgnoreCase("PJ_662")){
            	d = device.getName();
            		macaddress = device.getAddress();
            		break;
//            	}
            }
        }
    	SharedPreferences setting = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = setting.edit();
		String model  = setting.getString("MODEL", "PJ662");
		if(model.equalsIgnoreCase("PJ663"))
			mPrinterInfo.printerModel = PrinterInfo.Model.PJ_663;
		else
			mPrinterInfo.printerModel = PrinterInfo.Model.PJ_662;
		mPrinterInfo.port = PrinterInfo.Port.BLUETOOTH;
		mPrinterInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAGE;
		mPrinterInfo.halftone = PrinterInfo.Halftone.PATTERNDITHER;
		mPrinterInfo.printMode = PrinterInfo.PrintMode.ORIGINAL;
		mPrinterInfo.paperSize = PrinterInfo.PaperSize.A4;
		mPrinterInfo.macAddress = macaddress;
		//mDialog.showAlertDialog("mac address :: name", macaddress+"  ::  "+d);
		Log.i("BORTHER PRINTER DEMO","MAC ADDRESS :::: "+macaddress);
		 mPrinterInfo.macAddress = sharedPreferences.getString("macAddress", "");
		mPrinterInfo.pjDensity = 7;
    }
    
    
    /** get the printer settings from the SharedPreferences */
    private void getPreferencesold() {
    	String input = "";
        mPrinterInfo.printerModel =
            PrinterInfo.Model.valueOf(sharedPreferences.getString("printerModel", ""));
        mPrinterInfo.port = PrinterInfo.Port.valueOf(sharedPreferences.getString("port", ""));
        mPrinterInfo.ipAddress = sharedPreferences.getString("address", "");
        mPrinterInfo.macAddress = sharedPreferences.getString("macAddress", "");
        if(isLabelPrinter(mPrinterInfo.printerModel)){       	
        	mPrinterInfo.paperSize = PrinterInfo.PaperSize.CUSTOM;
    		switch(mPrinterInfo.printerModel){
    		case QL_710W:
    		case QL_720NW:			
    			mLabelInfo.labelNameIndex = LabelInfo.QL700.valueOf(sharedPreferences.getString("paperSize", "")).ordinal();
    			mLabelInfo.isAutoCut = Boolean.parseBoolean(sharedPreferences.getString("autoCut", ""));
    			mLabelInfo.isEndCut = Boolean.parseBoolean(sharedPreferences.getString("endCut", ""));
    			break;
    		case PT_E550W:			
     		case PT_P750W:	
    			String paper = sharedPreferences.getString("paperSize", "");
    			mLabelInfo.labelNameIndex = LabelInfo.PT.valueOf(paper).ordinal();
    			mLabelInfo.isAutoCut = Boolean.parseBoolean(sharedPreferences.getString("autoCut", ""));
    			mLabelInfo.isEndCut = Boolean.parseBoolean(sharedPreferences.getString("endCut", ""));
    			mLabelInfo.isHalfCut = Boolean.parseBoolean(sharedPreferences.getString("halfCut", ""));
    			mLabelInfo.isSpecialTape = Boolean.parseBoolean(sharedPreferences.getString("specialType", ""));

     			break;
    		
    		default:
    			break;
    		}
        }else{
            mPrinterInfo.paperSize = PrinterInfo.PaperSize.valueOf(sharedPreferences.getString("paperSize", ""));
        }
        mPrinterInfo.orientation = PrinterInfo.Orientation.valueOf(sharedPreferences.getString("orientation", ""));
        input = sharedPreferences.getString("numberOfCopies", "");
        if(input.equals("")) input = "1";
        mPrinterInfo.numberOfCopies = Integer.parseInt(input);
        mPrinterInfo.halftone = PrinterInfo.Halftone.valueOf(sharedPreferences.getString("halftone", ""));
        mPrinterInfo.printMode = PrinterInfo.PrintMode.valueOf(sharedPreferences.getString("printMode", ""));
        mPrinterInfo.pjCarbon = Boolean.parseBoolean(sharedPreferences.getString("pjCarbon", ""));
        input = sharedPreferences.getString("pjDensity", "");
        if(input.equals("")) input = "5";
        mPrinterInfo.pjDensity = Integer.parseInt(input);
        mPrinterInfo.pjFeedMode = PrinterInfo.PjFeedMode.valueOf(sharedPreferences.getString("pjFeedMode", ""));
        mPrinterInfo.align = PrinterInfo.Align.valueOf(sharedPreferences.getString("align", ""));
        input = sharedPreferences.getString("leftMargin", "");
        if(input.equals("")) input = "0";
        mPrinterInfo.margin.left = Integer.parseInt(input);
        mPrinterInfo.valign = PrinterInfo.VAlign.valueOf(sharedPreferences.getString("valign", ""));
        input = sharedPreferences.getString("topMargin", "");
        if(input.equals("")) input = "0";
        mPrinterInfo.margin.top = Integer.parseInt(input);
        input = sharedPreferences.getString("customPaperWidth", "");
        if(input.equals("")) input = "0";
        mPrinterInfo.customPaperWidth = Integer.parseInt(input);
        input = sharedPreferences.getString("customPaperLength", "");
        if(input.equals("")) input = "0";
        mPrinterInfo.customPaperLength = Integer.parseInt(input);
        input = sharedPreferences.getString("customFeed", "");
        if(input.equals("")) input = "0";
        mPrinterInfo.customFeed = Integer.parseInt(input);
        customSetting = sharedPreferences.getString("customSetting", "");
        mPrinterInfo.paperPosition = PrinterInfo.Align.valueOf(sharedPreferences.getString("paperPostion","LEFT"));
        mPrinterInfo.rjDensity = Integer.parseInt(sharedPreferences.getString("rjDensity", ""));
        mPrinterInfo.rotate180 = Boolean.parseBoolean(sharedPreferences.getString("rotate180", ""));
        mPrinterInfo.peelMode = Boolean.parseBoolean(sharedPreferences.getString("peelMode", ""));
      
        if(mPrinterInfo.printerModel == Model.TD_4000 || 
                mPrinterInfo.printerModel == Model.TD_4100N){
            mPrinterInfo.isAutoCut = Boolean.parseBoolean(sharedPreferences.getString("autoCut", ""));
            mPrinterInfo.isCutAtEnd = Boolean.parseBoolean(sharedPreferences.getString("endCut", ""));
        }
        
   }

    /** Launch the thread to print */
    public void print() {

        PrinterThread printTread = new PrinterThread();
        printTread.start();
    }

    /** Thread for printing */
    protected class PrinterThread extends Thread {
        @Override
        public void run() {

        	//set info. for printing
	        setPrinterInfo();
	        
            // start message
            Message msg = mHandle.obtainMessage(Common.MSG_PRINT_START);
            mHandle.sendMessage(msg);

            mPrintResult = new PrinterStatus();

            boolean b = mPrinter.startCommunication();

            doPrint();

            mPrinter.endCommunication();

            // end message
            mHandle.setResult(showResult());
            mHandle.setBattery(getBattery());
            msg = mHandle.obtainMessage(Common.MSG_PRINT_END);
            mHandle.sendMessage(msg);
        }
    }
  
    /** Launch the thread to get the printer's status */
    public void getPrinterStatus() {

        getStatusThread getTread = new getStatusThread();
        getTread.start();
    }

    /** Thread for getting the printer's status */
    class getStatusThread extends Thread {
        @Override
        public void run() {

	        //set info. for printing
	        setPrinterInfo();
        	
            // start message
            Message msg = mHandle.obtainMessage(Common.MSG_PRINT_START);
            mHandle.sendMessage(msg);

            mPrintResult = new PrinterStatus();
            mPrintResult = mPrinter.getPrinterStatus();

            // end message
            mHandle.setResult(showResult());
            mHandle.setBattery(getBattery());
            msg = mHandle.obtainMessage(Common.MSG_PRINT_END);
            mHandle.sendMessage(msg);

        }
    }

   /** set custom paper for RJ and TD */
    private void setCustomPaper() {

        switch (mPrinterInfo.printerModel) {
            case RJ_4030:
            case RJ_4040:
            case RJ_3050:
            case RJ_3150:	
            case TD_2020:
            case TD_2120N:
            case TD_2130N:
            case TD_4100N:
            case TD_4000:
                String filePath = Common.CUSTOM_PAPER_FOLDER + customSetting;
                mPrinter.setCustomPaper(mPrinterInfo.printerModel, filePath);
                break;
            default:
                break;
        }
    }

    /** get the end message of print */
    protected String showResult() {

        String result = "";
        if (mPrintResult.errorCode == ErrorCode.ERROR_NONE) {
            result = mContext.getString(R.string.ErrorMessage_ERROR_NONE);
        } else {
            result =  mPrintResult.errorCode.toString();
        }

        return result;
    }

    /** show information of battery */
    protected String getBattery() {

        String battery = "";
        if (mPrinterInfo.printerModel == PrinterInfo.Model.MW_260) {
            if (mPrintResult.batteryLevel > 80) {
                battery = mContext.getString(R.string.battery_full);
            } else if (30 <= mPrintResult.batteryLevel && mPrintResult.batteryLevel <= 80) {
                battery = mContext.getString(R.string.battery_middle);
            } else if (0 <= mPrintResult.batteryLevel && mPrintResult.batteryLevel < 30) {
                battery = mContext.getString(R.string.battery_weak);
            }
        } else if (mPrinterInfo.printerModel == PrinterInfo.Model.RJ_4030
            || mPrinterInfo.printerModel == PrinterInfo.Model.RJ_4040  
                    || mPrinterInfo.printerModel == PrinterInfo.Model.RJ_3050  
                            || mPrinterInfo.printerModel == PrinterInfo.Model.RJ_3150  

                    || mPrinterInfo.printerModel == PrinterInfo.Model.TD_2020 
            || mPrinterInfo.printerModel == PrinterInfo.Model.TD_2120N 
            || mPrinterInfo.printerModel == PrinterInfo.Model.TD_2130N
            || mPrinterInfo.printerModel == PrinterInfo.Model.PT_E550W
            || mPrinterInfo.printerModel == PrinterInfo.Model.PT_P750W) {
            switch (mPrintResult.batteryLevel) {
                case 0:
                    battery = mContext.getString(R.string.battery_full);
                    break;
                case 1:
                    battery = mContext.getString(R.string.battery_middle);
                    break;
                case 2:
                    battery = mContext.getString(R.string.battery_weak);
                    break;
                case 3:
                    battery = mContext.getString(R.string.battery_charge);
                    break;
                case 4:
                    battery = mContext.getString(R.string.ac_adapter);
                    break;
                default:
                    break;
            }
        } else {
            switch (mPrintResult.batteryLevel) {
                case 0:
                    battery =  mContext.getString(R.string.ac_adapter);
                    break;
                case 1:
                    battery = mContext.getString(R.string.battery_weak);
                    break;
                case 2:
                    battery = mContext.getString(R.string.battery_middle);
                    break;
                case 3:
                    battery = mContext.getString(R.string.battery_full);
                    break;
                default:
                    break;
            }
        }
        if(mPrintResult.errorCode != ErrorCode.ERROR_NONE) battery = "";
        return battery;
    }

    protected boolean isLabelPrinter(PrinterInfo.Model model){
    	switch(model){
    	case QL_710W:
    	case QL_720NW:
    	case PT_E550W:
    	case PT_P750W:
    		return true;
    	default:
    		return false;
    	}
    }
}
