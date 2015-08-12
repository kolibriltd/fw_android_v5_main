package com.anstar.model;

import com.anstar.models.Account;
import com.anstar.models.ApplicationDeviceTypeInfo;
import com.anstar.models.ApplicationMethodInfo;
import com.anstar.models.AppointmentConditionsInfo;
import com.anstar.models.AppointmentInfo;
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
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.BaitConditionsList;
import com.anstar.models.list.BillingTermsList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.DeviceTypesList;
import com.anstar.models.list.DilutionRatesList;
import com.anstar.models.list.InspectionList;
import com.anstar.models.list.LocationInfoList;
import com.anstar.models.list.MaterialList;
import com.anstar.models.list.PestTypeList;
import com.anstar.models.list.PhotoAttachmentsList;
import com.anstar.models.list.RecomendationsList;
import com.anstar.models.list.ServiceRoutesList;
import com.anstar.models.list.ServicesList;
import com.anstar.models.list.StatusList;
import com.anstar.models.list.TaxRateList;
import com.anstar.models.list.TrapConditionsList;
import com.anstar.models.list.TrapTypesList;
import com.anstar.models.list.WorkHistoryList;

import java.util.ArrayList;

/**
 * Created by oleg on 04.08.15.
 */
public class CommonLoader {

    public interface OnLoadListener {
        void onDataLoaded(CommonLoader cl);
    }

    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    private OnLoadListener mOnLoadListener;

    private int mLoaded = 0;

    public synchronized boolean isIsCustomerListDownloadError() {
        return mIsCustomerListDownloadError;
    }

    public synchronized void setIsCustomerListDownloadError(boolean isCustomerListDownloadError) {
        mIsCustomerListDownloadError = isCustomerListDownloadError;
    }

    public boolean isIsCustomerListItemDownloadError() {
        return mIsCustomerListItemDownloadError;
    }

    public void setIsCustomerListItemDownloadError(boolean isCustomerListItemDownloadError) {
        mIsCustomerListItemDownloadError = isCustomerListItemDownloadError;
    }

    private boolean mIsCustomerListItemDownloadError = false;
    private boolean mIsCustomerListDownloadError = false;

    private synchronized void checkLoaded() {
        mLoaded--;
        if (mLoaded == 0 && mOnLoadListener != null) {
            mOnLoadListener.onDataLoaded(this);
        }
    }

    private synchronized void addLoadingItem() {
        mLoaded++;
    }

    public void clear() {
        CustomerList.Instance().ClearDB();
        DilutionRatesList.Instance().ClearDB();
        LocationInfoList.Instance().ClearDB();
        StatusList.Instance().ClearDB();
        MeasurementInfo.ClearDB();
        ApplicationMethodInfo.ClearDB();
        MaterialList.Instance().ClearDB();
        PestTypeList.Instance().ClearDB();
        DeviceTypesList.Instance().ClearDB();
        BaitConditionsList.Instance().ClearDB();
        TrapConditionsList.Instance().ClearDB();
        TrapTypesList.Instance().ClearDB();
        TaxRateList.Instance().ClearDB();
        ServiceRoutesList.Instance().ClearDB();
        ServicesList.Instance().ClearDB();
        BillingTermsList.Instance().ClearDB();
        RecomendationsList.Instance().ClearDB();
        PhotoAttachmentsList.Instance().ClearDB();
        ApplicationDeviceTypeList.Instance().ClearDB();

        AppointmentModelList.Instance().ClearDB();
        InspectionList.Instance().ClearDB();
        WorkHistoryList.Instance().ClearDB();

        UserInfo.Instance().ClearDB();
    }

    public void loadMin() {

        if (Account.getkey().length() <= 0) {
            return;
        }

        try {
            addLoadingItem();
            UserInfo.Instance().load(new ModelDelegates.ModelDelegate<UserInfo>() {

                @Override
                public void ModelLoaded(ArrayList<UserInfo> list) {

                    loadDilutionRatesList();
                    loadApplicationDeviceTypeList();
                    loadPestTypeList();
                    loadLocationInfoList();
                    loadStatusList();
                    loadTaxRateList();
                    loadServicesList();
                    loadDeviceTypesList();
                    loadBaitConditionsList();
                    loadTrapConditionsList();
                    loadTrapTypesList();
                    loadServiceRoutesList();
                    loadMaterialList();
                    loadMeasurementInfo();
                    loadBillingTermsList();
                    loadRecomendationsList();
                    loadAppointmentConditionsList();
                    loadApplicationMethodInfo();

                    checkLoaded();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {

                    checkLoaded();
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
            checkLoaded();
        }
    }

    public void loadMax() {

        if (Account.getkey().length() <= 0) {
            return;
        }

        try {
            addLoadingItem();
            UserInfo.Instance().load(new ModelDelegates.ModelDelegate<UserInfo>() {

                @Override
                public void ModelLoaded(ArrayList<UserInfo> list) {

                    loadAppointmentModelList();
                    loadCustomerList();

                    loadDilutionRatesList();
                    loadApplicationDeviceTypeList();
                    loadPestTypeList();
                    loadLocationInfoList();
                    loadStatusList();
                    loadTaxRateList();
                    loadServicesList();
                    loadDeviceTypesList();
                    loadBaitConditionsList();
                    loadTrapConditionsList();
                    loadTrapTypesList();
                    loadServiceRoutesList();
                    loadMaterialList();
                    loadMeasurementInfo();
                    loadBillingTermsList();
                    loadRecomendationsList();
                    loadAppointmentConditionsList();
                    loadApplicationMethodInfo();

                    checkLoaded();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {

                    checkLoaded();
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
            checkLoaded();
        }
    }

    public void loadWithCustomerList() {

        if (Account.getkey().length() <= 0) {
            return;
        }

        try {
            addLoadingItem();
            UserInfo.Instance().load(new ModelDelegates.ModelDelegate<UserInfo>() {

                @Override
                public void ModelLoaded(ArrayList<UserInfo> list) {

                    loadCustomerList();

                    loadDilutionRatesList();
                    loadApplicationDeviceTypeList();
                    loadPestTypeList();
                    loadLocationInfoList();
                    loadStatusList();
                    loadTaxRateList();
                    loadServicesList();
                    loadDeviceTypesList();
                    loadBaitConditionsList();
                    loadTrapConditionsList();
                    loadTrapTypesList();
                    loadServiceRoutesList();
                    loadMaterialList();
                    loadMeasurementInfo();
                    loadBillingTermsList();
                    loadRecomendationsList();
                    loadAppointmentConditionsList();
                    loadApplicationMethodInfo();

                    checkLoaded();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {

                    checkLoaded();
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
            checkLoaded();
        }
    }

    public void loadCustomerList() {

        try {
            addLoadingItem();
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
                                for (CustomerInfo item : list) {
                                    loadCustomer(item);
                                }
                            } catch (Exception e) {
                                setIsCustomerListDownloadError(true);
                                e.printStackTrace();
                            }
                            checkLoaded();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {
                            checkLoaded();
                        }
                    });
        } catch (Exception e) {
            setIsCustomerListDownloadError(true);
            checkLoaded();
            e.printStackTrace();
        }
    }

    private void loadCustomer(CustomerInfo item) {
        try {
            addLoadingItem();
            item.RetriveData(new ModelDelegates.UpdateCustomerDelegate() {

                @Override
                public void UpdateSuccessFully(CustomerInfo info) {
                    checkLoaded();
                }

                @Override
                public void UpdateFail(String ErrorMessage) {
                    setIsCustomerListItemDownloadError(true);
                    checkLoaded();
                }
            });
        } catch (Exception e) {
            setIsCustomerListItemDownloadError(true);
            checkLoaded();
            e.printStackTrace();
        }
    }


    public void loadAppointmentModelList() {
        try {
            addLoadingItem();
            AppointmentModelList.Instance().load(new ModelDelegates.ModelDelegate<AppointmentInfo>() {

                @Override
                public void ModelLoaded(ArrayList<AppointmentInfo> list) {
                    checkLoaded();
                }

                @Override
                public void ModelLoadFailedWithError(
                        String error) {
                    checkLoaded();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadDilutionRatesList() {

        try {
            addLoadingItem();
            DilutionRatesList.Instance().load(
                    new ModelDelegates.ModelDelegate<DilutionInfo>() {
                        @Override
                        public void ModelLoaded(ArrayList<DilutionInfo> list) {

                            checkLoaded();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {

                            checkLoaded();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadApplicationDeviceTypeList() {

        try {
            addLoadingItem();
            ApplicationDeviceTypeList.Instance().load(
                    new ModelDelegates.ModelDelegate<ApplicationDeviceTypeInfo>() {
                        @Override
                        public void ModelLoaded(
                                ArrayList<ApplicationDeviceTypeInfo> list) {

                            checkLoaded();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {

                            checkLoaded();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadPestTypeList() {

        try {
            addLoadingItem();
            PestTypeList.Instance().load(new ModelDelegates.ModelDelegate<PestsTypeInfo>() {

                @Override
                public void ModelLoaded(ArrayList<PestsTypeInfo> list) {

                    checkLoaded();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {

                    checkLoaded();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadLocationInfoList() {

        try {
            addLoadingItem();
            LocationInfoList.Instance().load(new ModelDelegates.ModelDelegate<LocationInfo>() {

                @Override
                public void ModelLoaded(ArrayList<LocationInfo> list) {

                    checkLoaded();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {

                    checkLoaded();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadStatusList() {

        try {
            addLoadingItem();
            StatusList.Instance().load(new ModelDelegates.ModelDelegate<StatusInfo>() {

                @Override
                public void ModelLoaded(ArrayList<StatusInfo> list) {

                    checkLoaded();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {

                    checkLoaded();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadTaxRateList() {

        try {
            addLoadingItem();
            TaxRateList.Instance().load(new ModelDelegates.ModelDelegate<TaxRates>() {
                @Override
                public void ModelLoaded(ArrayList<TaxRates> list) {

                    checkLoaded();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {

                    checkLoaded();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadServicesList() {

        try {
            addLoadingItem();
            ServicesList.Instance().load(new ModelDelegates.ModelDelegate<ServicesInfo>() {
                @Override
                public void ModelLoaded(ArrayList<ServicesInfo> list) {

                    checkLoaded();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {

                    checkLoaded();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadDeviceTypesList() {

        try {
            addLoadingItem();
            DeviceTypesList.Instance().load(
                    new ModelDelegates.ModelDelegate<DeviceTypesInfo>() {

                        @Override
                        public void ModelLoaded(ArrayList<DeviceTypesInfo> list) {

                            checkLoaded();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {

                            checkLoaded();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadBaitConditionsList() {

        try {
            addLoadingItem();
            BaitConditionsList.Instance().load(
                    new ModelDelegates.ModelDelegate<BaitConditionsInfo>() {

                        @Override
                        public void ModelLoaded(
                                ArrayList<BaitConditionsInfo> list) {

                            checkLoaded();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {

                            checkLoaded();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadTrapConditionsList() {

        try {
            addLoadingItem();
            TrapConditionsList.Instance().load(
                    new ModelDelegates.ModelDelegate<TrapConditionsInfo>() {

                        @Override
                        public void ModelLoaded(
                                ArrayList<TrapConditionsInfo> list) {

                            checkLoaded();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {

                            checkLoaded();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadTrapTypesList() {

        try {
            addLoadingItem();
            TrapTypesList.Instance().load(new ModelDelegates.ModelDelegate<TrapTypesInfo>() {

                @Override
                public void ModelLoaded(ArrayList<TrapTypesInfo> list) {

                    checkLoaded();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {

                    checkLoaded();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadServiceRoutesList() {

        try {
            addLoadingItem();
            ServiceRoutesList.Instance().load(
                    new ModelDelegates.ModelDelegate<ServiceRoutesInfo>() {

                        @Override
                        public void ModelLoaded(
                                ArrayList<ServiceRoutesInfo> list) {

                            checkLoaded();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {

                            checkLoaded();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadMaterialList() {

        try {
            addLoadingItem();
            MaterialList.Instance().load(new ModelDelegates.ModelDelegate<MaterialInfo>() {

                @Override
                public void ModelLoaded(ArrayList<MaterialInfo> list) {

                    checkLoaded();
                }

                @Override
                public void ModelLoadFailedWithError(String error) {

                    checkLoaded();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadMeasurementInfo() {

        try {
            addLoadingItem();
            MeasurementInfo
                    .getMeasurements(new ModelDelegates.ModelDelegate<MeasurementInfo>() {

                        @Override
                        public void ModelLoaded(ArrayList<MeasurementInfo> list) {

                            checkLoaded();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {

                            checkLoaded();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadBillingTermsList() {

        try {
            addLoadingItem();
            BillingTermsList.Instance().load(
                    new ModelDelegates.ModelDelegate<BillingTermsInfo>() {

                        @Override
                        public void ModelLoaded(ArrayList<BillingTermsInfo> list) {

                            checkLoaded();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {

                            checkLoaded();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadRecomendationsList() {

        try {
            addLoadingItem();
            RecomendationsList.Instance().load(
                    new ModelDelegates.ModelDelegate<RecomendationInfo>() {

                        @Override
                        public void ModelLoaded(
                                ArrayList<RecomendationInfo> list) {

                            checkLoaded();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {

                            checkLoaded();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadAppointmentConditionsList() {

        try {
            addLoadingItem();
            AppointmentConditionsList.Instance().load(
                    new ModelDelegates.ModelDelegate<AppointmentConditionsInfo>() {

                        @Override
                        public void ModelLoaded(
                                ArrayList<AppointmentConditionsInfo> list) {

                            checkLoaded();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {

                            checkLoaded();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }

    private void loadApplicationMethodInfo() {

        try {
            addLoadingItem();
            ApplicationMethodInfo
                    .getMeasurements(new ModelDelegates.ModelDelegate<ApplicationMethodInfo>() {

                        @Override
                        public void ModelLoaded(
                                ArrayList<ApplicationMethodInfo> list) {

                            checkLoaded();
                        }

                        @Override
                        public void ModelLoadFailedWithError(String error) {

                            checkLoaded();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            checkLoaded();
        }
    }
}
