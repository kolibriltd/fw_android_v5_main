package com.anstar.fieldwork;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.common.Const;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.ModelDelegates;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.UserInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.ServiceLocationsList;
import com.anstar.widget.AppointmentDetailsFragmentChemicalUseListItem;
import com.anstar.widget.AppointmentDetailsFragmentDevicesListItem;
import com.anstar.widget.AppointmentDetailsFragmentLineItemsListItem;
import com.anstar.widget.AppointmentDetailsFragmentListHeader;
import com.anstar.widget.AppointmentDetailsFragmentNotesListItem;
import com.anstar.widget.AppointmentDetailsFragmentPDFFormsListItem;
import com.anstar.widget.AppointmentDetailsFragmentPhotosListItem;
import com.anstar.widget.AppointmentDetailsFragmentServiceInstructionsListItem;
import com.anstar.widget.AppointmentDetailsFragmentSignaturesListItem;
import com.anstar.widget.AppointmentDetailsFragmentUnitsListItem;

import java.util.ArrayList;
import java.util.WeakHashMap;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class AppointmentDetails2Fragment extends Fragment {

    private static final int VIEW_TYPE_LINE_ITEMS = 0;
    private static final int VIEW_TYPE_SERVICE_INSTRUCTIONS = 1;
    private static final int VIEW_TYPE_PDF_FORMS = 2;
    private static final int VIEW_TYPE_NOTES = 3;
    private static final int VIEW_TYPE_CHEMICAL_USE = 4;
    private static final int VIEW_TYPE_PHOTOS = 5;
    private static final int VIEW_TYPE_DEVICES = 6;
    private static final int VIEW_TYPE_UNITS = 7;
    private static final int VIEW_TYPE_SIGNATURES = 8;

    private ExpandableStickyListHeadersListView mStickyList;

    WeakHashMap<View, Integer> mOriginalViewHeightPool;
    private int mAppointmentId;
    private AppointmentInfo mAppointmentInfo;
    private CustomerInfo mCustomerInfo;
    private ServiceLocationsInfo mServiceLocationInfo;
    private UserInfo user;
    private AppointmentDetailsFragmentListHeader mHeader;
    private AppointmentDetailsFragmentLineItemsListItem mLineItems;
    private AppointmentDetailsFragmentServiceInstructionsListItem mServiceInstructions;
    private AppointmentDetailsFragmentPDFFormsListItem mPdfForms;
    private AppointmentDetailsFragmentNotesListItem mNotes;
    private AppointmentDetailsFragmentChemicalUseListItem mChemicalUse;
    private AppointmentDetailsFragmentPhotosListItem mPhotos;
    private AppointmentDetailsFragmentDevicesListItem mDevices;
    private AppointmentDetailsFragmentUnitsListItem mUnits;
    private AppointmentDetailsFragmentSignaturesListItem mSignatures;
    private AppointmentListAdapter mListAdapter;

    private int getHeadersCount() {
        return mDetailsHeadersValues.length;
    }
    private int[] mDetailsHeadersValues;
    private String[] mDetailsHeadersTitles;
    private String[] mDetailsHeadersIcons;

    private OnFragmentInteractionListener mListener;


    public AppointmentDetails2Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailsHeadersTitles = getResources().getStringArray(R.array.appointment_details_header_titles);
        mDetailsHeadersIcons = getResources().getStringArray(R.array.appointment_details_header_icons);
        mDetailsHeadersValues = getResources().getIntArray(R.array.appointment_details_header_values);

        Bundle b = getArguments();
        if (b != null) {
            mAppointmentId = b.getInt(Const.Appointment_Id);
        }
        if (mAppointmentId == 0) {
            mAppointmentId = Const.app_id;
        }
        mAppointmentInfo = AppointmentModelList.Instance().getAppointmentById(
                mAppointmentId);
        if (mAppointmentInfo == null) {
            Toast.makeText(getActivity(),
                    "Please try again, something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        mCustomerInfo = CustomerList.Instance().getCustomerById(
                mAppointmentInfo.customer_id);
        mServiceLocationInfo = ServiceLocationsList.Instance()
                .getServiceLocationById(mAppointmentInfo.service_location_id);

        if (mServiceLocationInfo == null) {
            Toast.makeText(getActivity(),
                    "Please try again, something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
        Const.customer_id = mAppointmentInfo.customer_id;
/*
        btnTrapCount.setText(TrapList
                .Instance()
                .getAllTrapsByCustomerId(mAppointmentInfo.customer_id,
                        mServiceLocationInfo.id).size()
                + "");
*/
        try {
            UserInfo.Instance().load(new ModelDelegates.ModelDelegate<UserInfo>() {
                @Override
                public void ModelLoaded(ArrayList<UserInfo> list) {
                    if (list != null) {
                        user = list.get(0);
                        if (!user.show_environment_fields) {
//                            rlEnvironment.setVisibility(View.GONE);
//                            dividerEnvirnMent.setVisibility(View.GONE);
                        } else {
//                            rlEnvironment.setVisibility(View.VISIBLE);
//                            dividerEnvirnMent.setVisibility(View.VISIBLE);
                        }
                        if (user.show_photos) {
//                            rlPictures.setVisibility(View.VISIBLE);
//                            dividerPic.setVisibility(View.VISIBLE);
//                            rlCamera.setVisibility(View.VISIBLE);
                        } else {
//                            rlPictures.setVisibility(View.GONE);
//                            dividerPic.setVisibility(View.GONE);
//                            rlCamera.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void ModelLoadFailedWithError(String error) {

                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_appointment_details, container, false);

        mStickyList = (ExpandableStickyListHeadersListView) v.findViewById(R.id.lstAppointments);

        //mHeader = (View) inflater.inflate(R.layout.fragment_appointment_details_list_header, null);
        mHeader = new AppointmentDetailsFragmentListHeader(getActivity());
        mStickyList.addHeaderView(mHeader);
        mListAdapter = new AppointmentListAdapter(getActivity());
        mStickyList.setAdapter(mListAdapter);
        //getViewHeight(adapter);
        mStickyList.setAnimExecutor(new AnimationExecutor());
        mStickyList.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition,
                                      long headerId, boolean currentlySticky) {
                for (int i = 0; i < getHeadersCount(); i++) {
                    if (headerId == i && mStickyList.isHeaderCollapsed(i)) {
                        mStickyList.expand(i);
                    } else {
                        mStickyList.collapse(i);
                    }
                }
            }
        });
/*
        mStickyList.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (mStickyList.isHeaderCollapsed(headerId)) {
                    mStickyList.expand(headerId);
                } else {
                    mStickyList.collapse(headerId);
                }
            }
        });
*/

        mLineItems = new AppointmentDetailsFragmentLineItemsListItem(getActivity());
        mLineItems.setOnListItemInteractionListener(new AppointmentDetailsFragmentLineItemsListItem.OnListItemInteractionListener() {
            @Override
            public void onButtonEditClick() {
                mListener.onAppointmentDetailsFragmentListItemsButtonEditClick(mAppointmentId);
            }

            @Override
            public void onButtonPayNowClick() {
                mListener.onAppointmentDetailsFragmentListItemsButtonPayNowClick(mAppointmentId);
            }

            @Override
            public void onLineItemClick(int position) {
                mListener.onAppointmentDetailsFragmentListItemsLineItemClick(mAppointmentId, position);
            }

            @Override
            public void onLineItemDelete() {
                refresh();
            }
        });
        mServiceInstructions = new AppointmentDetailsFragmentServiceInstructionsListItem(getActivity());
        mPdfForms = new AppointmentDetailsFragmentPDFFormsListItem(getActivity());
        mPdfForms.setOnListItemInteractionListener(new AppointmentDetailsFragmentPDFFormsListItem.OnListItemInteractionListener() {
            @Override
            public void onButtonAddClick(int appointmentId) {
                mListener.onAppointmentDetailsFragmentPDFFormsListItemButtonAddClick(appointmentId);
            }
        });
        mNotes = new AppointmentDetailsFragmentNotesListItem(getActivity());
        mNotes.setOnListItemInteractionListener(new AppointmentDetailsFragmentNotesListItem.OnListItemInteractionListener() {
            @Override
            public void onPublicNotesClick(int appointmentId) {
                mListener.onAppointmentDetailsFragmentNotesListItemPublicNotesClick(appointmentId);
            }

            @Override
            public void onPrivateNotesClick(int appointmentId) {
                mListener.onAppointmentDetailsFragmentNotesListItemPrivateNotesClick(appointmentId);
            }
        });
        mChemicalUse = new AppointmentDetailsFragmentChemicalUseListItem(getActivity());
        mChemicalUse.setOnListItemInteractionListener(new AppointmentDetailsFragmentChemicalUseListItem.OnListItemInteractionListener() {
            @Override
            public void onButtonAddClick(int appointmentId) {
                mListener.onAppointmentDetailsFragmentChemicalUseListItemButtonAddClick(appointmentId);
            }
        });
        mPhotos = new AppointmentDetailsFragmentPhotosListItem(getActivity());
        mPhotos.setOnListItemInteractionListener(new AppointmentDetailsFragmentPhotosListItem.OnListItemInteractionListener() {
            @Override
            public void onEditPhoto(int appointmentId, int id) {
                mListener.onAppointmentDetailsFragmentPhotosListItemEditPhoto(appointmentId, id);
            }

            @Override
            public void onAddPhoto(int appointmentId) {
                mListener.onAppointmentDetailsFragmentPhotosListItemAddPhoto(appointmentId);
            }

            @Override
            public void onRefresh() {
                mPhotos.init(mAppointmentId);
            }
        });
        mDevices = new AppointmentDetailsFragmentDevicesListItem(getActivity());
        mDevices.setOnListItemInteractionListener(new AppointmentDetailsFragmentDevicesListItem.OnListItemInteractionListener() {
            @Override
            public void onButtonAddClick(int mAppointmentId) {
                mListener.AppointmentDetailsFragmentDevicesListItemButtonAddClick(mAppointmentId);
            }
        });
        mUnits = new AppointmentDetailsFragmentUnitsListItem(getActivity());
        mSignatures = new AppointmentDetailsFragmentSignaturesListItem(getActivity());
        mSignatures.setOnListItemInteractionListener(new AppointmentDetailsFragmentSignaturesListItem.OnListItemInteractionListener() {
            @Override
            public void onSignatureCustomerClick() {
                mListener.onAppointmentDetailsFragmentSignatureCustomerClick(mAppointmentId);
            }

            @Override
            public void onSignatureTechnicianClick() {
                mListener.onAppointmentDetailsFragmentSignatureTechnicianClick(mAppointmentId);
            }
        });

        init();

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                collapseAll();
            }
        }, 10);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mListener.onAppointmentDetailsFragmentPaused();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("#" + mAppointmentId);
        mListener.onAppointmentDetailsFragmentResumed();
    }

    private void getViewHeight(AppointmentListAdapter mAdapter) {

        for (int i = 0; i <  getHeadersCount(); i++) {
            View mView = mAdapter.getView(i, null, mStickyList);

            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            mOriginalViewHeightPool.put(mView, mView.getMeasuredHeight());
        }
    }

    private void collapseAll() {
        collapseItem(0);
    }
    private void collapseItem(final int item) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mStickyList.collapse(item);
                if (item < getHeadersCount() - 2) {
                    collapseItem(item + 1);
                }
            }
        }, 200);
    }

    private void init() {
        mOriginalViewHeightPool = new WeakHashMap<>();

        mHeader.init(mAppointmentId);

        mLineItems.init(mAppointmentId);
        measure(mLineItems);

        mServiceInstructions.init(mAppointmentId);
        measure(mServiceInstructions);

        mPdfForms.init(mAppointmentId);
        measure(mPdfForms);

        mNotes.init(mAppointmentId);
        measure(mNotes);

        mChemicalUse.init(mAppointmentId);
        measure(mChemicalUse);

        mPhotos.init(mAppointmentId);
        measure(mPhotos);

        mDevices.init(mAppointmentId);
        measure(mDevices);

        mUnits.init(mAppointmentInfo, mCustomerInfo, mServiceLocationInfo);
        measure(mUnits);

        mSignatures.init(mAppointmentId);
        measure(mSignatures);
    }

    private void measure(View view) {

        view.measure(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        int h = view.getMeasuredHeight();
        mOriginalViewHeightPool.put(view, h);
    }

    public void refresh() {
        init();
        mListAdapter.notifyDataSetChanged();
        //mListAdapter = new AppointmentListAdapter(getActivity());
        //mStickyList.setAdapter(mListAdapter);
    }

    public class AppointmentListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private LayoutInflater mInflater;

        public AppointmentListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getViewTypeCount() {
            return mDetailsHeadersValues.length;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return mDetailsHeadersValues.length;
        }

        @Override
        public Object getItem(int position) {
            return "";
        }

        @Override
        public long getItemId(int position) {
            return mDetailsHeadersValues[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int viewType = this.getItemViewType(position);
            ViewHolder holder;

            switch (viewType) {
                case VIEW_TYPE_LINE_ITEMS:
                    //if (convertView == null) {
                        convertView = mLineItems;
                    //}
                    break;
                case VIEW_TYPE_SERVICE_INSTRUCTIONS:
                    //if (convertView == null) {
                        convertView = mServiceInstructions;
                    //}
                    break;
                case VIEW_TYPE_PDF_FORMS:
                    //if (convertView == null) {
                        convertView = mPdfForms;
                    //}
                    break;
                case VIEW_TYPE_NOTES:
                    //if (convertView == null) {
                        convertView = mNotes;
                    //}
                    break;
                case VIEW_TYPE_CHEMICAL_USE:
                    //if (convertView == null) {
                        convertView = mChemicalUse;
                    //}
                    break;
                case VIEW_TYPE_PHOTOS:
                    //if (convertView == null) {
                        convertView = mPhotos;
                    //}
                    break;
                case VIEW_TYPE_DEVICES:
                    //if (convertView == null) {
                        convertView = mDevices;
                    //}
                    break;
                case VIEW_TYPE_UNITS:
                    //if (convertView == null) {
                        convertView = mUnits;
                   // }
                    break;
                case VIEW_TYPE_SIGNATURES:
                    //if (convertView == null) {
                        convertView = mSignatures;
                    //}
                    break;
            }

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = mInflater.inflate(R.layout.fragment_appointment_details_list_item_header, parent, false);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }

            holder.image.setImageResource(getResources().getIdentifier(mDetailsHeadersIcons[position],
                    "drawable", getActivity().getPackageName()));
            holder.text.setText(mDetailsHeadersTitles[position]);

            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            return position;
        }

        class HeaderViewHolder {
            ImageView image;
            TextView text;
        }

        class ViewHolder {
            ImageView image;
            TextView text;
        }

    }

    public interface OnFragmentInteractionListener {

        void onAppointmentDetailsFragmentPaused();
        void onAppointmentDetailsFragmentResumed();

        void onAppointmentDetailsFragmentSignatureTechnicianClick(int mAppointmentId);
        void onAppointmentDetailsFragmentSignatureCustomerClick(int mAppointmentId);

        void onAppointmentDetailsFragmentListItemsButtonEditClick(int appointmentId);
        void onAppointmentDetailsFragmentListItemsLineItemClick(int appointmentId, int position);
        void onAppointmentDetailsFragmentListItemsButtonPayNowClick(int appointmentId);

        void onAppointmentDetailsFragmentNotesListItemPrivateNotesClick(int appointmentId);
        void onAppointmentDetailsFragmentNotesListItemPublicNotesClick(int appointmentId);

        void onAppointmentDetailsFragmentChemicalUseListItemButtonAddClick(int appointmentId);

        void onAppointmentDetailsFragmentPhotosListItemAddPhoto(int appointmentId);
        void onAppointmentDetailsFragmentPhotosListItemEditPhoto(int appointmentId, int id);

        void onAppointmentDetailsFragmentPDFFormsListItemButtonAddClick(int appointmentId);

        void AppointmentDetailsFragmentDevicesListItemButtonAddClick(int appointmentId);
    }

    //animation executor
    class AnimationExecutor implements ExpandableStickyListHeadersListView.IAnimationExecutor {

        @Override
        public void executeAnim(final View target, final int animType) {
            if (ExpandableStickyListHeadersListView.ANIMATION_EXPAND == animType && target.getVisibility() == View.VISIBLE) {
                return;
            }
            if (ExpandableStickyListHeadersListView.ANIMATION_COLLAPSE == animType && target.getVisibility() != View.VISIBLE) {
                return;
            }
            if (mOriginalViewHeightPool.get(target) == null) {
                mOriginalViewHeightPool.put(target, target.getHeight());
            }
            final int viewHeight = mOriginalViewHeightPool.get(target);
            float animStartY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? 0f : viewHeight;
            float animEndY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? viewHeight : 0f;
            final ViewGroup.LayoutParams lp = target.getLayoutParams();
            ValueAnimator animator = ValueAnimator.ofFloat(animStartY, animEndY);
            animator.setDuration(200);
            target.setVisibility(View.VISIBLE);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND) {
                        target.setVisibility(View.VISIBLE);
                    } else {
                        target.setVisibility(View.GONE);
                    }
                    target.getLayoutParams().height = viewHeight;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    lp.height = ((Float) valueAnimator.getAnimatedValue()).intValue();
                    target.setLayoutParams(lp);
                    target.requestLayout();
                }
            });
            animator.start();

        }
    }
}
