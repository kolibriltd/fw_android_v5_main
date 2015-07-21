package com.anstar.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.anstar.fieldwork.R;
import com.anstar.models.Account;
import com.anstar.models.ApplicationDeviceTypeInfo;
import com.anstar.models.ApplicationMethodInfo;
import com.anstar.models.AppointmentConditionsInfo;
import com.anstar.models.BaitConditionsInfo;
import com.anstar.models.BillingTermsInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.DeviceTypesInfo;
import com.anstar.models.DilutionInfo;
import com.anstar.models.LocationInfo;
import com.anstar.models.MaterialInfo;
import com.anstar.models.MeasurementInfo;
import com.anstar.models.ModelDelegates;
import com.anstar.models.PestsTypeInfo;
import com.anstar.models.RecomendationInfo;
import com.anstar.models.ServiceRoutesInfo;
import com.anstar.models.ServicesInfo;
import com.anstar.models.StatusInfo;
import com.anstar.models.TaxRates;
import com.anstar.models.TrapConditionsInfo;
import com.anstar.models.TrapTypesInfo;
import com.anstar.models.UserInfo;
import com.anstar.models.list.ApplicationDeviceTypeList;
import com.anstar.models.list.AppointmentConditionsList;
import com.anstar.models.list.BaitConditionsList;
import com.anstar.models.list.BillingTermsList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.DeviceTypesList;
import com.anstar.models.list.DilutionRatesList;
import com.anstar.models.list.LocationInfoList;
import com.anstar.models.list.MaterialList;
import com.anstar.models.list.PestTypeList;
import com.anstar.models.list.RecomendationsList;
import com.anstar.models.list.ServiceRoutesList;
import com.anstar.models.list.ServicesList;
import com.anstar.models.list.StatusList;
import com.anstar.models.list.TaxRateList;
import com.anstar.models.list.TrapConditionsList;
import com.anstar.models.list.TrapTypesList;

import java.util.ArrayList;

/**
 * Created by oleg on 19.07.15.
 */
public class BaseLoader {
    private final Context mContext;
    private static ProgressDialog mProgressDialog = null;

    private int mItemsProcessed = 0;

    private DataLoadedListener mDataLoadedListener;

    public void setDataLoadedListener(DataLoadedListener dataLoadedListener) {
        mDataLoadedListener = dataLoadedListener;
    }

    private synchronized void addItemsProcessed(int itemsProcessed) {
        mItemsProcessed += itemsProcessed;
    }

    private int getItemsProcessed() {
        return mItemsProcessed;
    }

    public BaseLoader(Activity activity) {

        mContext = activity;
    }

    private synchronized void createProgressDialog() {
        if (mContext != null) {
            mProgressDialog = new ProgressDialog(mContext, R.style.TransparentProgressDialog);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setContentView(R.layout.custom_progressdialog);
        }
    }

    public void showProgress() {
        showProgress("Please wait...");
    }

    public void showProgress(String msg) {
        if (mProgressDialog == null) {
            createProgressDialog();
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        if (getItemsProcessed() == 0) {
            mProgressDialog.setMessage(msg);
        }
    }

    public void hideProgress() {
        if (mProgressDialog != null && getItemsProcessed() == 0) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    private synchronized void showProgressInc(int itemsToLoad) {
        showProgress("Syncing customer database and settings...");
        addItemsProcessed(itemsToLoad);
    }

    private synchronized void hideProgressDec() {
        if (mItemsProcessed > 0) {
            mItemsProcessed--;
        }
        if (mItemsProcessed == 0) {

            hideProgress();

            if (mDataLoadedListener != null) {
                mDataLoadedListener.onDataLoaded();
            }
        }
    }

    public void loadAllData(boolean withCustomerList) {

        if (Account.getkey().length() <= 0) {
            return;
        }

        if (withCustomerList) {
            showProgressInc(20);
        } else {
            showProgressInc(19);
        }

        if (withCustomerList) {
            try {
                CustomerList.Instance().refreshCustomerList(
                        new ModelDelegates.ModelDelegate<CustomerInfo>() {

                            @Override
                            public void ModelLoaded(ArrayList<CustomerInfo> list) {
                                try {
                                    Account info = Account.getUser();
                                    if (info != null) {
                                        info.LastModifiedCustomerData = String
                                                .valueOf(System.currentTimeMillis());
                                        info.isCustomerLoded = true;
                                        info.save();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                hideProgressDec();
                            }

                            @Override
                            public void ModelLoadFailedWithError(String error) {
                                hideProgressDec();
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                hideProgressDec();
            }
        }

        try {
            UserInfo.Instance().load(new ModelDelegates.ModelDelegate<UserInfo>() {

                @Override
                public void ModelLoaded(ArrayList<UserInfo> list) {
                    hideProgressDec();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {
                    hideProgressDec();
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
            hideProgressDec();
        }

        try {
            DilutionRatesList.Instance().load(
                    new ModelDelegates.ModelDelegate<DilutionInfo>() {
                        @Override
                        public void ModelLoaded(ArrayList<DilutionInfo> list) {
                            hideProgressDec();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                            hideProgressDec();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            ApplicationDeviceTypeList.Instance().load(
                    new ModelDelegates.ModelDelegate<ApplicationDeviceTypeInfo>() {
                        @Override
                        public void ModelLoaded(
                                ArrayList<ApplicationDeviceTypeInfo> list) {
                            hideProgressDec();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                            hideProgressDec();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            PestTypeList.Instance().load(new ModelDelegates.ModelDelegate<PestsTypeInfo>() {

                @Override
                public void ModelLoaded(ArrayList<PestsTypeInfo> list) {
                    hideProgressDec();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {
                    hideProgressDec();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            LocationInfoList.Instance().load(new ModelDelegates.ModelDelegate<LocationInfo>() {

                @Override
                public void ModelLoaded(ArrayList<LocationInfo> list) {
                    hideProgressDec();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {
                    hideProgressDec();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            StatusList.Instance().load(new ModelDelegates.ModelDelegate<StatusInfo>() {

                @Override
                public void ModelLoaded(ArrayList<StatusInfo> list) {
                    hideProgressDec();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {
                    hideProgressDec();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            TaxRateList.Instance().load(new ModelDelegates.ModelDelegate<TaxRates>() {
                @Override
                public void ModelLoaded(ArrayList<TaxRates> list) {
                    hideProgressDec();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {
                    hideProgressDec();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            ServicesList.Instance().load(new ModelDelegates.ModelDelegate<ServicesInfo>() {
                @Override
                public void ModelLoaded(ArrayList<ServicesInfo> list) {
                    hideProgressDec();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {
                    hideProgressDec();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            DeviceTypesList.Instance().load(
                    new ModelDelegates.ModelDelegate<DeviceTypesInfo>() {

                        @Override
                        public void ModelLoaded(ArrayList<DeviceTypesInfo> list) {
                            hideProgressDec();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                            hideProgressDec();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            BaitConditionsList.Instance().load(
                    new ModelDelegates.ModelDelegate<BaitConditionsInfo>() {

                        @Override
                        public void ModelLoaded(
                                ArrayList<BaitConditionsInfo> list) {
                            hideProgressDec();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                            hideProgressDec();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            TrapConditionsList.Instance().load(
                    new ModelDelegates.ModelDelegate<TrapConditionsInfo>() {

                        @Override
                        public void ModelLoaded(
                                ArrayList<TrapConditionsInfo> list) {
                            hideProgressDec();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                            hideProgressDec();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            TrapTypesList.Instance().load(new ModelDelegates.ModelDelegate<TrapTypesInfo>() {

                @Override
                public void ModelLoaded(ArrayList<TrapTypesInfo> list) {
                    hideProgressDec();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {
                    hideProgressDec();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            ServiceRoutesList.Instance().load(
                    new ModelDelegates.ModelDelegate<ServiceRoutesInfo>() {

                        @Override
                        public void ModelLoaded(
                                ArrayList<ServiceRoutesInfo> list) {
                            hideProgressDec();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                            hideProgressDec();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            MaterialList.Instance().load(new ModelDelegates.ModelDelegate<MaterialInfo>() {

                @Override
                public void ModelLoaded(ArrayList<MaterialInfo> list) {
                    hideProgressDec();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {
                    hideProgressDec();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            MeasurementInfo
                    .getMeasurements(new ModelDelegates.ModelDelegate<MeasurementInfo>() {

                        @Override
                        public void ModelLoaded(ArrayList<MeasurementInfo> list) {
                            hideProgressDec();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                            hideProgressDec();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            BillingTermsList.Instance().load(
                    new ModelDelegates.ModelDelegate<BillingTermsInfo>() {

                        @Override
                        public void ModelLoaded(ArrayList<BillingTermsInfo> list) {
                            hideProgressDec();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                            hideProgressDec();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            RecomendationsList.Instance().load(
                    new ModelDelegates.ModelDelegate<RecomendationInfo>() {

                        @Override
                        public void ModelLoaded(
                                ArrayList<RecomendationInfo> list) {
                            hideProgressDec();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                            hideProgressDec();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            AppointmentConditionsList.Instance().load(
                    new ModelDelegates.ModelDelegate<AppointmentConditionsInfo>() {

                        @Override
                        public void ModelLoaded(
                                ArrayList<AppointmentConditionsInfo> list) {
                            hideProgressDec();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                            hideProgressDec();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

        try {
            ApplicationMethodInfo
                    .getMeasurements(new ModelDelegates.ModelDelegate<ApplicationMethodInfo>() {

                        @Override
                        public void ModelLoaded(
                                ArrayList<ApplicationMethodInfo> list) {
                            hideProgressDec();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                            hideProgressDec();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDec();
        }

    }

    public class ProgressDialog extends Dialog {

        public ProgressDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.custom_progressdialog);

            setCancelable(false);
            setCanceledOnTouchOutside(false);
        }

        void setMessage(String message) {
            TextView msg = (TextView) findViewById(R.id.textViewMessage);
            msg.setText(message);
        }
    }

    public interface DataLoadedListener {
        public void onDataLoaded();
    }
}
