/**
 * PdfPrint for printing
 * 
 * @author Brother Industries, Ltd.
 * @version 2.2
 */
package com.anstar.print;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.brother.ptouch.sdk.PrinterInfo.ErrorCode;

public class PdfPrint extends BasePrint {

    private int startIndex;
    private int endIndex;
    private String mPdfFile;

    public PdfPrint(Context context, MsgHandle mHandle, MsgDialog mDialog) {
        super(context, mHandle, mDialog);
    }

    /** get print pdf pages*/
    public int getPdfPages(String file) {

        return mPrinter.getPDFPages(file);

    }

    /** set print pdf pages*/
    public void setPrintPage(int start, int end) {

        startIndex = start;
        endIndex = end;
    }

    /** set print data*/
    public void setFiles(String file) {
        mPdfFile = file;

    }

    /** do the particular print */
    @Override
    protected void doPrint() {

        for (int i = startIndex; i <= endIndex; i++) {
        	
            mPrintResult = mPrinter.printPDF(mPdfFile, i);

            if (mPrintResult.errorCode != ErrorCode.ERROR_NONE) {
                break;
            }
        }
    }

}
