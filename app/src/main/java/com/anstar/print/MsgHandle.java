/**
 * Message Handle
 * 
 * @author Brother Industries, Ltd.
 * @version 2.2
 */

package com.anstar.print;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anstar.fieldwork.R;
import com.brother.ptouch.sdk.PrinterInfo;

public class MsgHandle extends Handler {

	private Context mContext;
	private String mResult;
	private String mBattery;
	private MsgDialog mDialog;
	private boolean isCancelled = false;

	final public static int FUNC_OTHER = 1;
	final public static int FUNC_SETTING = 2;
	private int funcID = FUNC_OTHER;

	public MsgHandle(Context context, MsgDialog Dialog) {
		funcID = FUNC_OTHER;
		mContext = context;
		mDialog = Dialog;

	}

	/** set the function id */
	public void setFunction(int funcID) {

		this.funcID = funcID;
	}

	/** set the printing result */
	public void setResult(String results) {

		mResult = results;
	}

	/** set the Battery info. */
	public void setBattery(String battery) {
		mBattery = battery;
	}

	/**
	 * Message Handler which deal with the messages from UI thread or print
	 * thread START: start message SDK_EVENT: message from SDK UPDATE: end
	 * message
	 */
	@Override
	public void handleMessage(Message msg) {

		switch (msg.what) {
		case Common.MSG_PRINT_START:
		case Common.MSG_DATA_SEND_START:
			mDialog.showStartMsgDialog(mContext
					.getString(R.string.progressDialogMsg_communication_start));
			break;
		case Common.MSG_TRANSFER_START:
			mDialog.showStartMsgDialog(mContext
					.getString(R.string.progressDialogMsg_transferStart));
			break;
		case Common.MSG_SDK_EVENT:
			String strMsg = msg.obj.toString();
			if (strMsg.equals(PrinterInfo.Msg.MESSAGE_START_COMMUNICATION
					.toString())) {
				mDialog.setMessage(mContext
						.getString(R.string.progressDialogMessage_preparingConnection));
				break;
			} else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_START_CREATE_DATA
					.toString())) {
				mDialog.setMessage(mContext
						.getString(R.string.progressDialogMessage_createData));
				break;
			} else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_START_SEND_DATA
					.toString())) {
				if (funcID == FUNC_SETTING) {
					mDialog.setMessage(mContext
							.getString(R.string.progressDialogMessage_sendingData));
				} else {
					mDialog.setMessage(mContext
							.getString(R.string.progressDialogMessage_sendingPrintingData));
				}
				break;
			} else if (strMsg
					.equals(PrinterInfo.Msg.MESSAGE_START_SEND_TEMPLATE
							.toString())) {
				mDialog.setMessage(mContext
						.getString(R.string.progressDialogMessage_sendingTemplateData));
				break;
			} else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_END_SEND_DATA
					.toString())) {
				if (funcID != FUNC_SETTING) {
					mDialog.setMessage(mContext
							.getString(R.string.progressDialogMessage_startPrint));
				}
				break;
			} else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_END_SEND_TEMPLATE
					.toString())) {
				mDialog.setMessage(mContext
						.getString(R.string.progressDialogMessage_dataSent));
				break;
			} else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_PRINT_COMPLETE
					.toString())) {
				mDialog.setMessage(mContext
						.getString(R.string.progressDialogMessage_printed));
				break;
			} else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_PRINT_ERROR
					.toString())) {
				mDialog.setMessage(mContext
						.getString(R.string.alertDialogMessage_runtimeError));
				break;
			} else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_END_COMMUNICATION
					.toString())) {
				mDialog.setMessage(mContext
						.getString(R.string.progressDialogMessage_CloseConnection));
				break;
			} else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_PAPER_EMPTY
					.toString())) {
				if (!isCancelled)
					mDialog.setMessage(mContext
							.getString(R.string.progressDialogMessage_SetPaper));
				break;
			} else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_START_COOLING
					.toString())) {
				mDialog.setMessage(mContext
						.getString(R.string.progressDialogMessage_CoolingStart));
				break;
			} else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_END_COOLING
					.toString())) {
				mDialog.setMessage(mContext
						.getString(R.string.progressDialogMessage_CoolingEnd));
				break;
			} else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_WAIT_PEEL
					.toString())) {
				mDialog.setMessage(mContext
						.getString(R.string.progressDialogMessage_WaitingPeel));
				break;
			} else {
				break;
			}
		case Common.MSG_PRINT_END:
		case Common.MSG_DATA_SEND_END:
			if (!mBattery.equals("")) {
				mDialog.showPrintCompletMsgDialog((mResult + "\nBattery: " + mBattery));
			} else {
				mDialog.showPrintCompletMsgDialog(mResult);
			}
			
			try {
				mDialog.close();
			} catch (Exception ex) {
			}
			isCancelled = false;
			break;
		case Common.MSG_PRINT_CANCEL:
			mDialog.showStartMsgDialog(mContext
					.getString(R.string.cancel_printer_msg));
			mDialog.disableCancel();
			isCancelled = true;
			break;
		case Common.MSG_WROMG_OS:
			mDialog.showPrintCompletMsgDialog(mContext
					.getString(R.string.ErrorMessage_WRONG_OS));
			break;
		case Common.MSG_NO_USB:
			mDialog.showPrintCompletMsgDialog(mContext
					.getString(R.string.ErrorMessage_NO_USB));
			break;
		default:
			break;
		}
	}
}
