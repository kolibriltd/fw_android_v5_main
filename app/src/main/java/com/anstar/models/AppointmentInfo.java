package com.anstar.models;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.Const;
import com.anstar.common.JsonCreator;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.internetbroadcast.ServiceCallerSync;
import com.anstar.model.helper.MySSLSocketFactory;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.model.mapper.ModelMapper;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.AttachmentsList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.InspectionList;
import com.anstar.models.list.InvoiceList;
import com.anstar.models.list.LineItemsList;
import com.anstar.models.list.MaterialUsagesList;
import com.anstar.models.list.PaymentsList;
import com.anstar.models.list.PdfFormsList;
import com.anstar.models.list.PhotoAttachmentsList;
import com.anstar.models.list.TargetPestList;
import com.anstar.models.list.TrapList;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AppointmentInfo extends ActiveRecordBase {

    @ModelMapper(JsonKey = "created_at")
    public String created_at;
    @ModelMapper(JsonKey = "customer_signature")
    public String customer_signature;
    @ModelMapper(JsonKey = "duration")
    public int duration;
    @ModelMapper(JsonKey = "id")
    public int id;
    @ModelMapper(JsonKey = "report_number")
    public int report_number = 0;
    @ModelMapper(JsonKey = "notes")
    public String notes;
    @ModelMapper(JsonKey = "private_notes")
    public String private_notes;
    @ModelMapper(JsonKey = "confirmed")
    public boolean confirmed = false;
    @ModelMapper(JsonKey = "instructions")
    public String instructions;
    @ModelMapper(JsonKey = "price2")
    public String price;
    @ModelMapper(JsonKey = "starts_at")
    public String starts_at;
    @ModelMapper(JsonKey = "status")
    public String status;
    @ModelMapper(JsonKey = "technician_signature")
    public String technician_signature;
    @ModelMapper(JsonKey = "technician_signature_name")
    public String technician_signature_name;
    @ModelMapper(JsonKey = "updated_at")
    public String updated_at;
    @ModelMapper(JsonKey = "customer_id")
    public int customer_id;
    @ModelMapper(JsonKey = "service_location_id")
    public int service_location_id;
    @ModelMapper(JsonKey = "ends_at")
    public String ends_at;
    @ModelMapper(JsonKey = "started_at_time")
    public String started_at_time;
    @ModelMapper(JsonKey = "finished_at_time")
    public String finished_at_time;
    @ModelMapper(JsonKey = "square_feet")
    public int square_feet = 0;
    @ModelMapper(JsonKey = "wind_direction")
    public String wind_direction = "";
    @ModelMapper(JsonKey = "wind_speed")
    public String wind_speed = "";
    @ModelMapper(JsonKey = "temperature")
    public String temperature = "";
    // @ModelMapper(JsonKey = "has_attached_form")
    // public boolean has_attached_form = false;
    @ModelMapper(JsonKey = "worker_lat")
    public String worker_lat = "";
    @ModelMapper(JsonKey = "worker_lng")
    public String worker_lng = "";
    @ModelMapper(JsonKey = "starts_at_time")
    public String starts_at_time;
    @ModelMapper(JsonKey = "ends_at_time")
    public String ends_at_time;
    @ModelMapper(JsonKey = "discount")
    public String discount = ""; // percents, fractional here
    @ModelMapper(JsonKey = "discount_amount")
    public String discount_amount = ""; // discount value, read-only
    @ModelMapper(JsonKey = "tax_amount")
    public String tax_amount = "";
    @ModelMapper(JsonKey = "purchase_order_no")
    public String purchase_order_no = "";
    @ModelMapper(JsonKey = "starts_at_date")
    public String starts_at_date = "";

    @ModelMapper(JsonKey = "recommendation_ids", IsArray = true)
    public ArrayList<String> recommendation_ids;
    @ModelMapper(JsonKey = "appointment_condition_ids", IsArray = true)
    public ArrayList<String> appointment_condition_ids;

    public int inspection_records;
    public int material_usages;
    public String pests_targets;

    public int customer_sign_id = 0;
    public int Tech_sign_id = 0;
    public int status_id = 0;
    public int notes_id = 0;
    public int environment_id = 0;
    public boolean isdirty = false;
    private UpdateInfoDelegate m_delegate = null;

    public static void saveSignature(long app_id, String json, String type) {
        try {
            AppointmentInfo app = FieldworkApplication.Connection().findByID(
                    AppointmentInfo.class, app_id);
            if (app != null) {
                if (type.equalsIgnoreCase(Const.Customer)) {
                    app.customer_signature = json;
                    if (NetworkConnectivity.isConnected()) {
                        app.save();
                        app.UpdateSignature(json, Const.Customer);
                    } else {
                        app.customer_sign_id = Utils.getRandomInt();
                        app.save();
                        AppointmentInfo.updateDirtyFlag(app_id);
                    }

                } else {
                    app.technician_signature = json;
                    if (NetworkConnectivity.isConnected()) {
                        app.save();
                        app.UpdateSignature(json, Const.Technitian);
                    } else {
                        app.Tech_sign_id = Utils.getRandomInt();
                        app.save();
                        AppointmentInfo.updateDirtyFlag(app_id);
                    }

                }
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }

    public static void saveStatus(long app_id, String status,
                                  UpdateInfoDelegate delegate) {

        try {
            AppointmentInfo app = FieldworkApplication.Connection().findByID(
                    AppointmentInfo.class, app_id);
            if (app != null) {
                app.status = StatusInfo.getValueByName(status);
                if (NetworkConnectivity.isConnected()) {
                    app.save();
                    app.UpdateStatus(status, delegate);
                } else {
                    app.status_id = Utils.getRandomInt();
                    app.save();
                    ServiceResponse response = new ServiceResponse();
                    response.StatusCode = 200;
                    delegate.UpdateSuccessFully(response);
                }
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }

    public static void SaveEnvironMent(long app_id, int square_feet,
                                       String wind_direction, String wind_speed, String temperature,
                                       UpdateInfoDelegate delegate) {

        try {
            AppointmentInfo app = FieldworkApplication.Connection().findByID(
                    AppointmentInfo.class, app_id);
            if (app != null) {
                app.square_feet = square_feet;
                app.wind_direction = wind_direction;
                app.wind_speed = wind_speed;
                app.temperature = temperature;
                if (NetworkConnectivity.isConnected()) {
                    app.save();
                    app.UpdateEnvironment(square_feet, wind_direction,
                            wind_speed, temperature, delegate);
                } else {
                    app.environment_id = Utils.getRandomInt();
                    app.save();
                    ServiceResponse response = new ServiceResponse();
                    response.StatusCode = 200;
                    delegate.UpdateSuccessFully(response);
                    AppointmentInfo.updateDirtyFlag(app_id);
                }
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }

    public static void AddAppointment(
            final ModelDelegates.UpdateAppointmentDelegate delegate,
            AppointmentInfo info, ArrayList<LineItemsInfo> lineinfos) {
        // {"work_order":{"status":"Scheduled","service_location_id":
        // 14896,"customer_id":
        // 32045,"starts_at":"2014-07-17T00:00:00+06:00","starts_at_date":"07/17/2014",
        // "starts_at_time":"6:12 AM",
        // "duration":195,"ends_at":"2014-07-27T03:15:00+06:00","discount":
        // "0.0", "tax_amount": "10.0","notes":"Some notes","callback": false,
        // "hide_invoice_information":
        // false,"line_items_attributes":[{"payable_id":123,"payable_type":
        // "Service", "type": "service", "name": "Main","quantity":
        // 1.23,"price":1.23,"total":1.23,"taxable": true}]}}
        if (NetworkConnectivity.isConnected()) {
            // material.UpdateMaterial(name, epa, price);
            HashMap<String, Object> orderHash = new HashMap<String, Object>();
            orderHash.put("service_location_id", info.service_location_id);
            orderHash.put("customer_id", info.customer_id);
            // orderHash.put("starts_at", info.starts_at);
            // orderHash.put("ends_at", info.starts_at);
            orderHash.put("starts_at_date", info.starts_at_date);
            orderHash.put("starts_at_time", info.starts_at_time);
            orderHash.put("ends_at_date", info.starts_at_date);
            orderHash.put("ends_at_time", info.ends_at_time);
            orderHash.put("instructions", info.instructions);
            orderHash.put("purchase_order_no", info.purchase_order_no);
            orderHash.put("discount", info.discount_amount);
            orderHash.put("status", "Scheduled");
            orderHash.put("tax_amount", info.tax_amount);
            boolean flag = false;
            UserInfo uinfo = UserInfo.Instance().getUser();

            if (info.technician_signature_name != null) {
                String[] strarr = info.technician_signature_name.split(",");
                int[] intarr = new int[strarr.length];
                for (int i = 0; i < strarr.length; i++) {
                    intarr[i] = Utils.ConvertToInt(strarr[i]);
                }
                orderHash.put("service_route_ids", intarr);
                if (info.technician_signature_name.contains(String
                        .valueOf(uinfo.service_route_id)))
                    flag = true;
            }
            List<HashMap<String, Object>> linelst = new ArrayList<HashMap<String, Object>>();
            for (LineItemsInfo linfo : lineinfos) {
                HashMap<String, Object> lineitemHash = new HashMap<String, Object>();
                lineitemHash.put("payable_type", linfo.type);
                lineitemHash.put("payable_id", linfo.payable_id);
                lineitemHash.put("type", linfo.type.toLowerCase());
                lineitemHash.put("name", linfo.name);
                lineitemHash.put("quantity", linfo.quantity);
                lineitemHash.put("price", linfo.price);
                lineitemHash.put("total", linfo.total);
                lineitemHash.put("taxable", linfo.taxable);
                linelst.add(lineitemHash);
            }
            final boolean ismyroute = flag;
            JSONArray arr = JsonCreator.getJsonArray(linelst);

            orderHash.put("line_items_attributes", arr);

            JSONObject obj = JsonCreator.getJsonObject(orderHash);

            ServiceCaller caller = new ServiceCaller(ServiceHelper.WORK_ORDERS,
                    ServiceCaller.RequestMethod.POST, "{\"work_order\":"
                    + obj.toString() + "}");
            caller.startRequest(new ServiceHelperDelegate() {
                @Override
                public void CallFinish(ServiceResponse res) {
                    try {
                        if (ismyroute) {

                            // {"work_order":{"id":173136,"created_at":"2014-08-04T06:56:17-05:00","updated_at":"2014-08-04T06:56:17-05:00","starts_at":"2014-07-04T13:00:00-05:00","duration":-23407080,"status":"Scheduled","notes":null,"technician_signature":null,"technician_signature_name":null,"customer_signature":null,"service_location_id":14896,"customer_id":32045,"ends_at":"1970-01-01T14:00:00-06:00","started_at_time":null,"finished_at_time":null,"instructions":null,"square_feet":null,"wind_direction":null,"wind_speed":null,"temperature":null,"starts_at_time":" 1:00 PM","ends_at_time":" 2:00 PM","starts_at_date":"07/04/2014","ends_at_date":"01/01/1970","worker_lat":null,"worker_lng":null,"recommendation_ids":[],"appointment_condition_ids":[],"corrective_action_ids":[],"discount":"0.0","tax_amount":"0.0","discount_amount":"0.0","hide_invoice_information":false,"is_editing_now":false,"purchase_order_no":"po","invoice":null,"inspection_records":[],"material_usages":[],"pests_targets":[],"recommendations":[],"corrective_actions":[],"appointment_conditions":[],"line_items":[{"id":128239,"payable_id":3,"payable_type":"Service","type":"service","name":"Bedbug Inspection","quantity":"1.0","price":"275.25","total":"275.25","taxable":false}],"attachments":[],"pdf_forms":[]}}
                            JSONObject obj = new JSONObject(res.RawResponse);
                            JSONObject workorder = obj
                                    .getJSONObject("work_order");
                            // int id = workorder.optInt("id");
                            ModelMapHelper<AppointmentInfo> mapper = new ModelMapHelper<AppointmentInfo>();
                            AppointmentInfo info = mapper.getObject(
                                    AppointmentInfo.class, workorder);

                            try {
                                TrapList.Instance().load(
                                        new ModelDelegate<TrapScanningInfo>() {

                                            @Override
                                            public void ModelLoaded(
                                                    ArrayList<TrapScanningInfo> list) {
                                            }

                                            @Override
                                            public void ModelLoadFailedWithError(
                                                    String error) {
                                            }
                                        }, info.customer_id,
                                        info.service_location_id);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            LineItemsList.Instance().parseLineItems(workorder,
                                    info.id);

                            if (info != null) {
                                info.save();
                            }
                        }
                        delegate.UpdateSuccessFully(new AppointmentInfo());

                    } catch (Exception e) {
                        delegate.UpdateFail("There is some error");
                    }
                }

                @Override
                public void CallFailure(String ErrorMessage) {
                    delegate.UpdateFail("There is some error, please try again.");

                }
            });
        } else {
            delegate.UpdateFail("Please check your internet connection. This feature is only available when you are online");
        }
    }

    public static void saveAppointementNew(Activity act, long app_id,
                                           String status, String start_at, String Startedat, String finishat,
                                           UpdateInfoDelegate delegate) {

        try {
            AppointmentInfo app = FieldworkApplication.Connection().findByID(
                    AppointmentInfo.class, app_id);
            Location l = Utils.getCurrentLocation(act);
            if (app != null) {
                app.status = StatusInfo.getValueByName(status);
                app.starts_at = start_at;
                app.started_at_time = Startedat;
                app.finished_at_time = finishat;
                if (l != null) {
                    app.worker_lat = String.valueOf(l.getLatitude());
                    app.worker_lng = String.valueOf(l.getLongitude());
                }
                if (NetworkConnectivity.isConnected()) {
                    app.save();
                    app.UpdateAppointmentNew(status, delegate);
                } else {
                    app.status_id = Utils.getRandomInt();
                    app.save();
                    ServiceResponse response = new ServiceResponse();
                    response.StatusCode = 200;
                    delegate.UpdateSuccessFully(response);
                    updateDirtyFlag(app_id);
                }
            }

        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }

    public static void saveNotes(long app_id, String notes, String pnotes,
                                 ArrayList<String> rec_ids, ArrayList<String> con_ids,
                                 UpdateInfoDelegate delegate) {
        try {
            AppointmentInfo app = FieldworkApplication.Connection().findByID(
                    AppointmentInfo.class, app_id);
            if (app != null) {
                app.notes = notes;
                app.private_notes = pnotes;
                app.recommendation_ids = rec_ids;
                app.appointment_condition_ids = con_ids;
                if (NetworkConnectivity.isConnected()) {
                    app.save();
                    app.UpdateNotes(notes, rec_ids, con_ids, delegate);
                } else {
                    app.notes_id = Utils.getRandomInt();
                    app.save();
                    ServiceResponse response = new ServiceResponse();
                    response.StatusCode = 200;
                    delegate.UpdateSuccessFully(response);
                    AppointmentInfo.updateDirtyFlag(app_id);
                }
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }

    public static void UpdateTaxAmount(final float tax, final float disc,
                                       final int apptid) {
        String data = "{\"tax_amount\":%f}";
        data = String.format(data, tax);
        String url = String.format("work_orders/%d", apptid);
        ServiceCaller caller = new ServiceCaller(url,
                ServiceCaller.RequestMethod.PUT, data);
        caller.startRequest(new ServiceHelperDelegate() {

            @Override
            public void CallFinish(ServiceResponse res) {

            }

            @Override
            public void CallFailure(String ErrorMessage) {

            }
        });
    }

    public static void savePrice(long app_id) {
        try {
            AppointmentInfo app = FieldworkApplication.Connection().findByID(
                    AppointmentInfo.class, app_id);
            if (app != null) {
                if (NetworkConnectivity.isConnected()) {
                    if (app.price.length() > 0) {
                        app.UpdatePrice(app.price);
                    }
                } else {
                    updateDirtyFlag(app_id);
                }
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }

    public static void updateDirtyFlag(int app_id) {
        try {
            List<AppointmentInfo> app = FieldworkApplication.Connection().find(
                    AppointmentInfo.class,
                    CamelNotationHelper.toSQLName("id") + "=?",
                    new String[]{String.valueOf(app_id)});
            if (app != null && app.size() > 0) {
                app.get(0).isdirty = true;
                app.get(0).save();
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }

    public static void updateDirtyFlag(long app_id) {
        try {
            AppointmentInfo app = FieldworkApplication.Connection().findByID(
                    AppointmentInfo.class, app_id);
            if (app != null) {
                app.isdirty = true;
                app.save();
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }

    public static void refresh() {
        AppointmentModelList.Instance().ClearDB();
        try {
            AppointmentModelList.Instance().load(
                    new ModelDelegate<AppointmentInfo>() {
                        @Override
                        public void ModelLoaded(ArrayList<AppointmentInfo> list) {
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void PrintPdf(int app_id) {
        String url = String.format("work_orders/%d", app_id);
        StringBuilder sb = new StringBuilder();

        sb.append(ServiceHelper.URL);
        sb.append(url + "/" + ServiceHelper.GETPDF + "?");
        sb.append("api_key=");
        sb.append(Account.getkey());

        // @Override
        // public void UpdateFail(String ErrorMessage) {
        // }
        // }, sb.toString());
        // DownloadFile(new UpdateInfoDelegate() {
        //
        // @Override
        // public void UpdateSuccessFully(ServiceResponse res) {
        // String path = "";
        // File extStore = Environment.getExternalStorageDirectory();
        // path = extStore + "/FieldWorkDocument/ServiceReport.pdf";
        // Utils.LogInfo("PDF PATH----->>>" + path);
        // print(path);
        //
        // }
        //
        // @Override
        // public void UpdateFail(String ErrorMessage) {
        // }
        // }, sb.toString());
    }

    public static String DownloadFile(String fileURL) {
        File file = null;

        if (isSDCARDMounted()) {
            String extStorageDirectory = Environment
                    .getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "FieldWorkDocument");
            folder.mkdir();
            file = new File(folder, "ServiceReport.pdf");
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            // File mediaDir = new File("/sdcard/FieldWorkDocument");
            // if (!mediaDir.exists()) {
            // mediaDir.mkdir();
            // }
            // file = new File("/sdcard/FieldWorkDocument/ServiceReport.pdf");
            // try {
            // file.createNewFile();
            // } catch (IOException e2) {
            // e2.printStackTrace();
            // }

            ContextWrapper cw = new ContextWrapper(
                    FieldworkApplication.getContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir(Environment.DIRECTORY_DOWNLOADS,
                    Context.MODE_PRIVATE);
            // Create imageDir
            String path = "ServiceReport.pdf";
            file = new File(directory, path);
            try {
                file.createNewFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        // File file = null;
        try {
            file = File.createTempFile("ServiceReport", ".pdf");
            FileOutputStream f = new FileOutputStream(file);
            // URL u = new URL(fileURL);
            // HttpURLConnection c = (HttpURLConnection) u.openConnection();
            // c.setRequestMethod("GET");
            // c.setDoOutput(true);
            // c.connect();
            // // InputStream in = c.getInputStream();
            // InputStream in = new BufferedInputStream(u.openStream());

            HttpClient client = getNewHttpClient();
            HttpRequestBase request = null;
            request = new HttpGet(fileURL);
            request.setHeader("Accept", "*/*");
            request.setHeader("Mobile-App-Version",
                    ServiceCaller.getHeaderVersion());
            request.setHeader("Content-Type", "text/plain; charset=utf-8");
            request.getParams().setParameter("http.socket.timeout", 20000);
            request.setHeader("Cache-Control", "no-cache");

            HttpResponse response = client.execute(request);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream in = entity.getContent();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.close();
            }
        } catch (Exception e) {

            e.printStackTrace();
            return "";
        }

        return file.getAbsolutePath();
    }

    public static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static void print(String path) {

        SharedPreferences setting = PreferenceManager
                .getDefaultSharedPreferences(FieldworkApplication.getContext());
        String printerIp = setting.getString("IP", "");

        PrinterInfo printerInfo = new PrinterInfo();
        printerInfo.printerModel = PrinterInfo.Model.PJ_663;
        if (printerIp.length() > 0) {
            printerInfo.ipAddress = printerIp;
        } else {
            printerInfo.ipAddress = "169.254.100.1";
        }
        printerInfo.paperSize = PrinterInfo.PaperSize.A4;
        printerInfo.orientation = PrinterInfo.Orientation.PORTRAIT;
        printerInfo.numberOfCopies = Integer.parseInt("1");
        printerInfo.halftone = PrinterInfo.Halftone.PATTERNDITHER;
        printerInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAGE;
        printerInfo.pjDensity = Integer.parseInt("1");
        printerInfo.align = PrinterInfo.Align.CENTER;
        printerInfo.valign = PrinterInfo.VAlign.MIDDLE;
        printerInfo.rjDensity = Integer.parseInt("1");
        // printerInfo.port = PrinterInfo.Port.valueOf("");
        // printerInfo.pjCarbon = Boolean.parseBoolean("true");
        // printerInfo.pjFeedMode = PrinterInfo.PjFeedMode.PJ_FEED_MODE_FREE;
        // printerInfo.margin.left = Integer.parseInt("");
        // printerInfo.margin.top = Integer.parseInt("");
        // printerInfo.customPaperWidth = Integer.parseInt("");
        // printerInfo.customPaperLength = Integer.parseInt("");
        // printerInfo.customFeed = Integer.parseInt("");
        // printerInfo.macAddress = "";

        Printer p = new Printer();
        try {
            p.setPrinterInfo(printerInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PrinterThread thread = new PrinterThread(path, p);
        thread.start();

    }

    public static boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();
        if (status.equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public Date getStartAtDate() {
        String d[] = this.starts_at.split("T");
        Date date = Utils.ConvertToDate(d[0]);
        return date;
    }

    public void UpdateStatus(String status, UpdateInfoDelegate delegate) {
        m_delegate = delegate;
        String data = "{\"status\":\"%s\"}";
        data = String.format(data, status);
        String url = String.format("work_orders/%d", this.id);
        ServiceCaller caller = new ServiceCaller(url,
                ServiceCaller.RequestMethod.PUT, data);
        caller.startRequest(new ServiceHelperDelegate() {

            @Override
            public void CallFinish(ServiceResponse res) {
                m_delegate.UpdateSuccessFully(res);
            }

            @Override
            public void CallFailure(String ErrorMessage) {
                m_delegate.UpdateFail(ErrorMessage);
                Toast.makeText(FieldworkApplication.getContext(), ErrorMessage,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void UpdateEnvironment(int square_feet, String wind_direction,
                                  String wind_speed, String temperature, UpdateInfoDelegate delegate) {
        m_delegate = delegate;
        String data = "{\"work_order\":{\"square_feet\":\"%d\",\"wind_direction\":\"%s\",\"wind_speed\":\"%s\",\"temperature\":\"%s\"}}";
        data = String.format(data, square_feet, wind_direction, wind_speed,
                temperature);
        String url = String.format("work_orders/%d", this.id);
        ServiceCaller caller = new ServiceCaller(url,
                ServiceCaller.RequestMethod.PUT, data);
        caller.startRequest(new ServiceHelperDelegate() {

            @Override
            public void CallFinish(ServiceResponse res) {
                m_delegate.UpdateSuccessFully(res);
            }

            @Override
            public void CallFailure(String ErrorMessage) {
                m_delegate.UpdateFail(ErrorMessage);
                Toast.makeText(FieldworkApplication.getContext(), ErrorMessage,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void UpdateAppointment(String status, UpdateInfoDelegate delegate) {
        m_delegate = delegate;
        String json = "{\"work_order\":{\"starts_at\":\"%s\",\"started_at_time\":\"%s\",\"finished_at_time\":\"%s\",\"status\":\"%s\"}}";
        json = String.format(json, this.starts_at, this.started_at_time,
                this.finished_at_time, status);
        String url = String.format("work_orders/%d", this.id);
        ServiceCaller caller = new ServiceCaller(url,
                ServiceCaller.RequestMethod.PUT, json);
        caller.startRequest(new ServiceHelperDelegate() {

            @Override
            public void CallFinish(ServiceResponse res) {
                m_delegate.UpdateSuccessFully(res);
            }

            @Override
            public void CallFailure(String ErrorMessage) {
                m_delegate.UpdateFail(ErrorMessage);
                Toast.makeText(FieldworkApplication.getContext(), ErrorMessage,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void UpdateAppointmentNew(String status, UpdateInfoDelegate delegate) {
        m_delegate = delegate;
        String json = "{\"work_order\": {\"starts_at_date\":\"%s\",\"started_at_time\":\"%s\",\"finished_at_time\":\"%s\",\"price2\": \"%s\",\"status\": \"%s\",\"worker_lat\": \"%s\",\"worker_lng\": \"%s\",\"tax_amount\":%f###}}";
        String paymentJson = ",\"invoice_attributes\": {\"payments_attributes\": [{\"id\":\"%s\",\"amount\":\"%s\", \"payment_method\":\"%s\", \"check_number\":\"%s\",\"created_from_mobile\":true,\"payment_date\":\"%s\"}]}";
        PaymentInfo info = PaymentInfo.getPaymentsInfoByAppId(this.id);
        if (status.contains("Complete") && info != null) {
            String payment_id = "";
            if (info.PaymentId > 0) {
                payment_id = "" + info.PaymentId;
            }
            Date dt = new Date();
            if (Utils.ConvertToFloat(info.amount) > 0) {
                String payment_date = Utils.Instance().ChangeDateFormat(
                        "EEE MMM dd HH:mm:ss zzz yyyy", "MM/dd/yyyy",
                        dt.toString());
                paymentJson = String.format(paymentJson, payment_id,
                        info.amount, info.payment_method, info.check_number,
                        payment_date);
                json = json.replace("###", paymentJson);
            } else {
                json = json.replace("###", "");
            }
        } else {
            json = json.replace("###", "");
        }
        // 2013-08-24
        String d = this.starts_at.split("T")[0];
        String date = Utils.Instance().ChangeDateFormat("yyyy-MM-dd",
                "MM/dd/yyyy", d);
        json = String.format(json, date, this.started_at_time,
                this.finished_at_time, this.price, status, this.worker_lat,
                this.worker_lng, Utils.ConvertToFloat(this.tax_amount));
        String url = String.format("work_orders/%d", this.id);
        ServiceCaller caller = new ServiceCaller(url,
                ServiceCaller.RequestMethod.PUT, json);
        caller.startRequest(new ServiceHelperDelegate() {

            @Override
            public void CallFinish(ServiceResponse res) {
                // m_delegate.UpdateSuccessFully(res);
                // loadappointment();
                storeWorkOrderResponse(res, true);
                // RetriveData(null);
            }

            @Override
            public void CallFailure(String ErrorMessage) {
                m_delegate.UpdateFail(ErrorMessage);
                Toast.makeText(FieldworkApplication.getContext(), ErrorMessage,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void UpdateSignature(String signaturejson, String type) {
        String data = "";
        if (type.equalsIgnoreCase(Const.Customer)) {
            signaturejson = signaturejson.replace("\"", "\\\"");
            data = "{\"customer_signature\":\"" + signaturejson + "\"}";
        } else {
            signaturejson = signaturejson.replace("\"", "\\\"");
            data = "{\"technician_signature\":\"" + signaturejson + "\"}";
        }
        String url = String.format("work_orders/%d", this.id);
        ServiceCaller caller = new ServiceCaller(url,
                ServiceCaller.RequestMethod.PUT, data);
        caller.startRequest(new ServiceHelperDelegate() {

            @Override
            public void CallFinish(ServiceResponse res) {

            }

            @Override
            public void CallFailure(String ErrorMessage) {
                Toast.makeText(FieldworkApplication.getContext(), ErrorMessage,
                        Toast.LENGTH_LONG).show();

            }
        });

    }

    public void UpdateNotes(String Notes, ArrayList<String> recids,
                            ArrayList<String> conids, UpdateInfoDelegate delegate) {
        m_delegate = delegate;
        Utils.LogInfo("ARRAY PASS IN FUCNTION REC IDS :::: "
                + recids.toString() + " CONDITION ID :::: " + conids.toString());
        HashMap<String, Object> orderHash = new HashMap<String, Object>();
        String rids = "";

        ArrayList<Integer> recarr = new ArrayList<Integer>();
        if (recids != null && recids.size() > 0) {
            rids = Utils.Instance().join(recids, ",");
            for (int i = 0; i < recids.size(); i++) {
                if (!recids.get(i).contains("[") && recids.get(i).length() > 0) {
                    recarr.add((Integer) Integer.parseInt(recids.get(i)));
                }
            }
        }

        ArrayList<Integer> conarr = new ArrayList<Integer>();
        if (conids != null && conids.size() > 0) {
            rids = Utils.Instance().join(conids, ",");
            for (int i = 0; i < conids.size(); i++) {
                if (!conids.get(i).contains("[") && conids.get(i).length() > 0) {
                    conarr.add((Integer) Integer.parseInt(conids.get(i)));
                }
            }
        }
        // int[] recarr = new int[] {};
        // if (recids != null && recids.size() > 0) {
        // recarr = new int[recids.size()];
        // rids = Utils.Instance().join(recids, ",");
        // for (int i = 0; i < recids.size(); i++) {
        // recarr[i] = Integer.parseInt(recids.get(i));
        // }
        // }
        // String cids = "";
        // int[] conarr = new int[] {};
        // if (conids != null && conids.size() > 0) {
        // conarr = new int[conids.size()];
        // cids = Utils.Instance().join(conids, ",");
        // for (int i = 0; i < conids.size(); i++) {
        // if (!conids.get(i).contains("[") && conids.get(i).length() > 0)
        // conarr[i] = Integer.parseInt(conids.get(i));
        // }
        // }
        orderHash.put("notes", this.notes);
        orderHash.put("private_notes", this.private_notes);
        orderHash.put("recommendation_ids", Utils.convertIntegers(recarr));
        orderHash.put("appointment_condition_ids",
                Utils.convertIntegers(conarr));
        JSONObject data1 = JsonCreator.getJsonObject(orderHash);
        Utils.LogInfo("NOTES SAVE JSON :::: " + data1.toString());

        String url = String.format("work_orders/%d", this.id);
        ServiceCaller caller = new ServiceCaller(url,
                ServiceCaller.RequestMethod.PUT, data1.toString());
        caller.startRequest(new ServiceHelperDelegate() {

            @Override
            public void CallFinish(ServiceResponse res) {
                m_delegate.UpdateSuccessFully(res);
            }

            @Override
            public void CallFailure(String ErrorMessage) {
                m_delegate.UpdateFail(ErrorMessage);
                Toast.makeText(FieldworkApplication.getContext(), ErrorMessage,
                        Toast.LENGTH_LONG).show();

            }
        });
    }

    public void UpdatePrice(String Price) {

        String data = "{\"work_order\":{\"price2\":\"%s\"}}";
        data = String.format(data, Price);

        String url = String.format("work_orders/%d", this.id);
        ServiceCaller caller = new ServiceCaller(url,
                ServiceCaller.RequestMethod.PUT, data);
        caller.startRequest(new ServiceHelperDelegate() {

            @Override
            public void CallFinish(ServiceResponse res) {

            }

            @Override
            public void CallFailure(String ErrorMessage) {

            }
        });

    }

    public void LoadRelatedData() {
        try {
            TargetPestList pest = new TargetPestList();
            // pest.load(null, this.id);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void syncCustSignature(final AppointmentInfo info) {
        String data = "";
        info.customer_signature = info.customer_signature.replace("\"", "\\\"");
        data = "{\"customer_signature\":\"" + info.customer_signature + "\"}";
        String url = String.format("work_orders/%d", info.id);
        ServiceCaller caller = new ServiceCaller(url,
                ServiceCaller.RequestMethod.PUT, data);
        caller.startRequest(new ServiceHelperDelegate() {

            @Override
            public void CallFinish(ServiceResponse res) {
                info.customer_sign_id = 0;
                try {
                    info.save();
                } catch (ActiveRecordException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void CallFailure(String ErrorMessage) {

            }
        });
    }

    public void syncTechSignature(final AppointmentInfo info) {
        String data = "";
        info.technician_signature = info.technician_signature.replace("\"",
                "\\\"");
        data = "{\"technician_signature\":\"" + info.technician_signature
                + "\"}";
        String url = String.format("work_orders/%d", info.id);
        ServiceCaller caller = new ServiceCaller(url,
                ServiceCaller.RequestMethod.PUT, data);
        caller.startRequest(new ServiceHelperDelegate() {

            @Override
            public void CallFinish(ServiceResponse res) {
                info.Tech_sign_id = 0;
                try {
                    info.save();
                } catch (ActiveRecordException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void CallFailure(String ErrorMessage) {

            }
        });
    }

    public void syncAppointmentNew(final AppointmentInfo info) {
        Utils.LogInfo("syncAppointmentNew :::: ");
        String json = "{\"work_order\": {\"starts_at_date\":\"%s\",\"started_at_time\":\"%s\",\"finished_at_time\":\"%s\",\"price2\": \"%s\",\"status\": \"%s\",\"worker_lat\": \"%s\",\"worker_lng\": \"%s\",\"tax_amount\":%f###}}";
        String paymentJson = ",\"invoice_attributes\": {\"payments_attributes\": [{\"id\":\"%s\",\"amount\":\"%s\", \"payment_method\":\"%s\", \"check_number\":\"%s\",\"created_from_mobile\":true}]}";
        PaymentInfo payments = PaymentInfo.getPaymentsInfoByAppId(this.id);
        if (status.contains("Complete") && payments != null) {
            String payment_id = "";
            if (payments.PaymentId > 0) {
                payment_id = "" + payments.PaymentId;
            }
            paymentJson = String.format(paymentJson, payment_id,
                    payments.amount, payments.payment_method,
                    payments.check_number);
            json = json.replace("###", paymentJson);
        } else {
            json = json.replace("###", "");
        }
        // 2013-08-24
        String d = this.starts_at.split("T")[0];
        String date = Utils.Instance().ChangeDateFormat("yyyy-MM-dd",
                "MM/dd/yyyy", d);
        json = String.format(json, date, this.started_at_time,
                this.finished_at_time, this.price, status, this.worker_lat,
                this.worker_lng, Utils.ConvertToFloat(this.tax_amount));
        String url = String.format("work_orders/%d", info.id);
        ServiceCaller caller = new ServiceCaller(url,
                ServiceCaller.RequestMethod.PUT, json);
        caller.startRequest(new ServiceHelperDelegate() {

            @Override
            public void CallFinish(final ServiceResponse res) {
                // loadappointment();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        Utils.LogInfo("syncAppointmentNew RetriveData :::: ");
                        storeWorkOrderResponse(res, true);
                        // RetriveData(null);
                        super.onPostExecute(result);
                    }
                }.execute();

            }

            @Override
            public void CallFailure(String ErrorMessage) {

                Toast.makeText(FieldworkApplication.getContext(), ErrorMessage,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setPrefereces() {
        // SharedPreferences.Editor editor = sharedPreferences.edit();
        // editor.putString("printer", printerInfo.printerModel.toString());
        // editor.putString("port", printerInfo.port.toString());
        // editor.putString("address", printerInfo.ipAddress.toString());
        // editor.putString("macAddress", printerInfo.macAddress.toString());
        // editor.putString("paperSize", printerInfo.paperSize.toString());
        // editor.putString("orientation", printerInfo.orientation.toString());
        // editor.putString("numberOfCopies",
        // Integer.toString(printerInfo.numberOfCopies));
        // editor.putString("halftone", printerInfo.halftone.toString());
        // editor.putString("printMode", printerInfo.printMode.toString());
        // editor.putString("pjCarbon", Boolean.toString(printerInfo.pjCarbon));
        // editor.putString("pjDensity",
        // Integer.toString(printerInfo.pjDensity));
        // editor.putString("pjFeedMode", printerInfo.pjFeedMode.toString());
        // editor.putString("align", printerInfo.align.toString());
        // editor.putString("leftMargin",
        // Integer.toString(printerInfo.margin.left));
        // editor.putString("valign", printerInfo.valign.toString());
        // editor.putString("topMargin",
        // Integer.toString(printerInfo.margin.top));
        // editor.putString("customPaperWidth",
        // Integer.toString(printerInfo.customPaperWidth));
        // editor.putString("customPaperLength",
        // Integer.toString(printerInfo.customPaperLength));
        // editor.putString("customFeed",
        // Integer.toString(printerInfo.customFeed));
        // editor.putString("customSetting",
        // sharedPreferences.getString("customSetting", ""));
        // editor.putString("rjDensity",
        // Integer.toString(printerInfo.rjDensity));
        // editor.commit();
    }

    public void loadappointment() {
        AppointmentModelList.Instance().ClearDB();
        CustomerList.Instance().ClearDB();
        try {
            AppointmentModelList.Instance().load(
                    new ModelDelegate<AppointmentInfo>() {
                        @Override
                        public void ModelLoaded(ArrayList<AppointmentInfo> list) {
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ClearDataByAppoointmentId() {
        InvoiceList.Instance().ClearDB(this.id);
        MaterialUsagesList.Instance().ClearDB(this.id);
        TargetPestList.Instance().ClearDB(this.id);
        InspectionList.Instance().ClearDB(this.id);
        TrapList.Instance().ClearDB(this.customer_id);
        PaymentsList.Instance().ClearDB(this.id);
        LineItemsList.Instance().ClearDB(this.id);
        PdfFormsList.Instance().ClearDB(this.id);
        AttachmentsList.Instance().ClearDB(this.id);
        PhotoAttachmentsList.Instance().ClearDB(this.id);

        ContextWrapper cw = new ContextWrapper(
                FieldworkApplication.getContext());
        File directory = cw.getDir(Environment.DIRECTORY_DOWNLOADS,
                Context.MODE_PRIVATE);

        // deleteDirectory(directory);
        // CustomerInfo info = CustomerList.Instance().getCustomerById(
        // this.customer_id);
        // if (info != null) {
        // try {
        // info.delete();
        // } catch (ActiveRecordException e) {
        // e.printStackTrace();
        // }
        // }
    }

    public void deleteDirectory(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
            file.delete();
        }
    }

    public void RetriveData(final UpdateInfoDelegate delegate) {
        if (NetworkConnectivity.isConnected()) {
            ServiceHelper helper = new ServiceHelper(ServiceHelper.WORK_ORDERS
                    + "/" + this.id);
            m_delegate = delegate;
            helper.call(new ServiceHelperDelegate() {

                @Override
                public void CallFinish(ServiceResponse res) {
                    if (!res.isError()) {
                        storeWorkOrderResponse(res, false);
                    }
                }

                @Override
                public void CallFailure(String ErrorMessage) {
                    if (delegate != null) {
                        delegate.UpdateFail(ErrorMessage);
                    }
                }
            });
        } else {
            if (delegate != null) {
                delegate.UpdateFail("Please check your intenet connection to retrive customer information.");
            }
        }

    }

    public String getAppointmentJson() {
        // Main Appointment Object
        HashMap<String, Object> orderHash = new HashMap<String, Object>();
        // orderHash.put("starts_at", this.starts_at);
        // orderHash.put("ends_at", this.starts_at);
        orderHash.put("starts_at_date", this.starts_at_date);
        orderHash.put("starts_at_time", this.starts_at_time);
        orderHash.put("ends_at_date", this.starts_at_date);
        orderHash.put("ends_at_time", this.ends_at_time);
        orderHash.put("started_at_time", this.started_at_time);
        orderHash.put("finished_at_time", this.finished_at_time);
        orderHash.put("status", this.status);
        orderHash.put("tax_amount", Utils.ConvertToFloat(this.tax_amount));
        if (this.customer_sign_id < 0) {
            orderHash.put("customer_signature", this.customer_signature);
        }
        if (this.Tech_sign_id < 0) {
            orderHash.put("technician_signature", this.technician_signature);
        }

        // Payment
        PaymentInfo payments = PaymentInfo.getPaymentsInfoByAppId(this.id);
        if (payments != null) {
            if (this.status.equalsIgnoreCase("complete")
                    && Utils.ConvertToFloat(payments.amount) > 0) {
                String paymentJson = "{\"payments_attributes\": "
                        + "[{\"id\":\"%s\",\"amount\":\"%s\", \"payment_method\":\"%s\","
                        + " \"check_number\":\"%s\",\"created_from_mobile\":true,\"payment_date\":\"%s\"}]}";
                String payment_id = "";
                if (payments.PaymentId > 0) {
                    payment_id = "" + payments.PaymentId;
                }
                Date dt = new Date();
                String payment_date = Utils.Instance().ChangeDateFormat(
                        "EEE MMM dd HH:mm:ss zzz yyyy", "MM/dd/yyyy",
                        dt.toString());
                paymentJson = String.format(paymentJson, payment_id,
                        payments.amount, payments.payment_method,
                        payments.check_number, payment_date);
                try {
                    JSONObject pay = new JSONObject(paymentJson);
                    orderHash.put("invoice_attributes", pay);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        // HashMap List For Line Item
        ArrayList<LineItemsInfo> lineinfos = LineItemsList.Instance().loadAll(
                this.id);
        if (lineinfos != null && lineinfos.size() > 0) {
            List<HashMap<String, Object>> linelst = new ArrayList<HashMap<String, Object>>();
            for (LineItemsInfo linfo : lineinfos) {
                // Create Single Object for Line Item
                HashMap<String, Object> lineitemHash = new HashMap<String, Object>();
                if (linfo.isDeleted) {
                    linfo.DeleteLineObjectJson();
                    lineitemHash.put("id", linfo.id);
                    lineitemHash.put("_destroy", true);
                    linelst.add(lineitemHash);
                    continue;
                }
                if (linfo.id > 0) {
                    lineitemHash.put("id", linfo.id);
                }
                lineitemHash.put("payable_type", linfo.type);
                lineitemHash.put("payable_id", linfo.payable_id);
                lineitemHash.put("type", linfo.type.toLowerCase());
                lineitemHash.put("name", linfo.name);
                lineitemHash.put("quantity", linfo.quantity);
                lineitemHash.put("price", linfo.price);
                lineitemHash.put("total", linfo.total);
                lineitemHash.put("taxable", linfo.taxable);
                linelst.add(lineitemHash);
            }
            JSONArray arr = JsonCreator.getJsonArray(linelst);
            orderHash.put("line_items_attributes", arr);
        }
        // Material
        ArrayList<MaterialUsage> usages = MaterialUsagesList.Instance()
                .loadAll(this.id);
        if (usages != null && usages.size() > 0) {
            ArrayList<String> m_jobj = new ArrayList<String>();
            for (MaterialUsage m : usages) {
                // if (m.id > 0)
                // m.deleteSync();
                if (!m.isDeleted)
                    m_jobj.add(m.GetMaterialObjectJson());
                String deleted = m.DeleteMaterialObjectJson();
                if (deleted.length() > 0) {
                    m_jobj.add(deleted);
                }
            }
            String material = Utils.Instance().join(m_jobj, ",");
            try {
                JSONArray mat = new JSONArray("[" + material + "]");
                orderHash.put("material_usages_attributes", mat);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // inspection
        ArrayList<InspectionInfo> inspectionlist = InspectionList.Instance()
                .loadAll(this.id);
        if (inspectionlist != null && inspectionlist.size() > 0) {
            ArrayList<String> m_jobj = new ArrayList<String>();
            for (InspectionInfo i : inspectionlist) {
                // if (m.id > 0)
                // m.deleteSync();
                if (!i.isDeleted)
                    m_jobj.add(i.GetInspectionJson());
                String deleted = i.DeleteInspectionObjectJson();
                if (deleted.length() > 0) {
                    m_jobj.add(deleted);
                }
                // m_jobj.add(m.GetInspectionJson());
            }
            String inspection = Utils.Instance().join(m_jobj, ",");

            try {
                JSONArray inn = new JSONArray("[" + inspection + "]");
                orderHash.put("inspection_records_attributes", inn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // photo attachment
        ArrayList<PhotoAttachmentsInfo> photos = PhotoAttachmentsInfo
                .getDeletedAttachmentsByWorkerId(this.id);
        List<HashMap<String, Object>> photolst = new ArrayList<HashMap<String, Object>>();
        if (photos != null && photos.size() > 0) {
            for (final PhotoAttachmentsInfo pic : photos) {
                HashMap<String, Object> photo = new HashMap<String, Object>();
                photo.put("id", pic.id);
                photo.put("_destroy", true);
                photolst.add(photo);
            }
            JSONArray arr = JsonCreator.getJsonArray(photolst);
            orderHash.put("photo_attachments_attributes", arr);
        }
        // enviornment
        orderHash.put("square_feet", this.square_feet);
        orderHash.put("wind_direction", this.wind_direction);
        orderHash.put("wind_speed", this.wind_speed);
        orderHash.put("temperature", this.temperature);

        // Notes ,Conditionsids
        String rids = "";
        Utils.writeContent("Recomendation id : " + this.recommendation_ids
                + "condition ids : " + this.appointment_condition_ids);
        ArrayList<Integer> recarr = new ArrayList<Integer>();
        if (this.recommendation_ids != null
                && this.recommendation_ids.size() > 0) {
            rids = Utils.Instance().join(this.recommendation_ids, ",");
            for (int i = 0; i < this.recommendation_ids.size(); i++) {
                if (!this.recommendation_ids.get(i).contains("[")
                        && this.recommendation_ids.get(i).length() > 0) {
                    recarr.add((Integer) Integer
                            .parseInt(this.recommendation_ids.get(i)));
                }
            }
        }
        String cids = "";
        ArrayList<Integer> conarr = new ArrayList<Integer>();
        if (this.appointment_condition_ids != null
                && this.appointment_condition_ids.size() > 0) {
            cids = Utils.Instance().join(this.appointment_condition_ids, ",");
            for (int i = 0; i < this.appointment_condition_ids.size(); i++) {
                if (!this.appointment_condition_ids.get(i).contains("[")
                        && this.appointment_condition_ids.get(i).length() > 0) {
                    conarr.add((Integer) Integer
                            .parseInt(this.appointment_condition_ids.get(i)));
                }
            }
        }
        Utils.LogInfo("Recomendation ids ::: " + rids + " conditions ids ::: "
                + cids);
        orderHash.put("notes", this.notes);
        orderHash.put("private_notes", this.private_notes);
        orderHash.put("recommendation_ids", Utils.convertIntegers(recarr));
        orderHash.put("appointment_condition_ids",
                Utils.convertIntegers(conarr));

        JSONObject obj = JsonCreator.getJsonObject(orderHash);
        return obj.toString();
    }

    public void sync1(String json) {
        try {
            String url = String.format("work_orders/%d", this.id);
            ServiceCallerSync caller = new ServiceCallerSync(url,
                    ServiceCallerSync.RequestMethod.PUT, json);
            ServiceResponse res = caller.startRequest();
            if (!res.isError()) {
                storeWorkOrderResponse(res, false);
                Utils.LogInfo("in success after appt PUT method *****");
                // JSONObject obj = new JSONObject(res.RawResponse);
                // JSONObject order = obj.getJSONObject("work_order");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void storeWorkOrderResponse(ServiceResponse res, boolean loadCust) {
        if (!res.isError()) {
            try {
                JSONObject subjectList = new JSONObject(res.RawResponse);
                JSONObject data = subjectList.getJSONObject("work_order");
                if (data != null) {
                    ClearDataByAppoointmentId();
                    ModelMapHelper<AppointmentInfo> mapper = new ModelMapHelper<AppointmentInfo>();
                    AppointmentInfo info = mapper.getObject(
                            AppointmentInfo.class, data);
                    // JSONArray recarr =
                    // data.getJSONArray("recommendation_ids");
                    // ArrayList<String> rec
                    // for (int i = 0 ; i < recarr.length(); i++) {
                    //
                    // }
                    // info.recommendation_ids =
                    if (loadCust) {
                        CustomerInfo customer = CustomerList.Instance()
                                .getCustomerById(info.customer_id);
                        if (customer != null) {
                            customer.RetriveData(null);
                        } else {
                            CustomerInfo cust = FieldworkApplication
                                    .Connection().newEntity(CustomerInfo.class);
                            cust.id = info.customer_id;
                            cust.RetriveData(null);
                        }
                    }
                    try {
                        TrapList.Instance().load(
                                new ModelDelegate<TrapScanningInfo>() {

                                    @Override
                                    public void ModelLoaded(
                                            ArrayList<TrapScanningInfo> list) {
                                    }

                                    @Override
                                    public void ModelLoadFailedWithError(
                                            String error) {
                                    }
                                }, info.customer_id, info.service_location_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    InspectionList.Instance().parseInspectionRecords(data,
                            info.id);
                    InvoiceList.Instance().parseInvoice(data, info.id);
                    MaterialUsagesList.Instance().parseMatrialUsages(data,
                            info.id, false);
                    TargetPestList.Instance().parseTargetPests(data, info.id);
                    LineItemsList.Instance().parseLineItems(data, info.id);
                    AttachmentsList.Instance().parseAttachments(data, info.id);
                    PhotoAttachmentsList.Instance().parseAttachments(data,
                            info.id);
                    PdfFormsList.Instance().parsePdfForms(data, info.id);
                    if (info != null) {
                        AppointmentInfo.this.copyFrom(info);
                        AppointmentInfo.this.save();
                    }
                    Thread.sleep(3000);
                    if (m_delegate != null)
                        m_delegate.UpdateSuccessFully(res);
                    // if (delegate != null) {
                    // delegate.UpdateSuccessFully(AppointmentInfo.this);
                    // }
                }
            } catch (Exception e) {
                // if (delegate != null) {
                // delegate.UpdateFail(e.getMessage());
                // }
                e.printStackTrace();
            }
            // if (delegate != null) {
            // delegate.UpdateFail(res.ErrorMessage);
            // }
        }
    }

    public static class PrinterThread extends Thread {
        String path = "";
        Printer myPrinter;

        public PrinterThread(String p, Printer printer) {
            path = p;
            myPrinter = printer;
        }

        public void run() {
            int pagenum;
            try {
                pagenum = myPrinter.getPDFPages(path);
                PrinterStatus printResult = new PrinterStatus();
                printResult = myPrinter.printPDF(path, pagenum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
