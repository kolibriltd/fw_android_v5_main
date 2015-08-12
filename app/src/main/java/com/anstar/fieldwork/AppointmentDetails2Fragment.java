package com.anstar.fieldwork;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.WeakHashMap;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class AppointmentDetails2Fragment extends Fragment {

    private ExpandableStickyListHeadersListView stickyList;

    WeakHashMap<View, Integer> mOriginalViewHeightPool = new WeakHashMap<>();
    private int mAppointmentId;
    private AppointmentInfo mAppointmentInfo;
    private CustomerInfo mCustomerInfo;
    private ServiceLocationsInfo mServiceLocationInfo;
    private UserInfo user;
    private View mHeader;

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

        stickyList = (ExpandableStickyListHeadersListView) v.findViewById(R.id.lstAppointments);
        mHeader = (View) inflater.inflate(R.layout.fragment_appointment_details_list_header, null);
        stickyList.addHeaderView(mHeader);
        AppointmentListAdapter adapter = new AppointmentListAdapter(getActivity());
        stickyList.setAdapter(adapter);
        //getViewHeight(adapter);
        stickyList.setAnimExecutor(new AnimationExecutor());
        stickyList.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition,
                                      long headerId, boolean currentlySticky) {
                for (int i = 0; i < getHeadersCount(); i++) {
                    if (headerId == i && stickyList.isHeaderCollapsed(i)) {
                        stickyList.expand(i);
                    } else {
                        stickyList.collapse(i);
                    }
                }
            }
        });
        stickyList.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (stickyList.isHeaderCollapsed(headerId)) {
                    stickyList.expand(headerId);
                } else {
                    stickyList.collapse(headerId);
                }
            }
        });

        TextView textView = (TextView) mHeader.findViewById(R.id.textViewName);
        textView.setText(mCustomerInfo.name);
        textView = (TextView) mHeader.findViewById(R.id.textViewAddress);
        textView.setText(mServiceLocationInfo.street + ", " +
                mServiceLocationInfo.city + ", " +
                mServiceLocationInfo.state + ", " +
                mServiceLocationInfo.zip);

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
        mListener.onAppointmentDetailsFragmentResumed();
    }

    private void getViewHeight(AppointmentListAdapter mAdapter) {

        for (int i = 0; i <  getHeadersCount(); i++) {
            View mView = mAdapter.getView(i, null, stickyList);

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
                stickyList.collapse(item);
                if (item < getHeadersCount() - 1) {
                    collapseItem(item + 1);
                }
            }
        }, 200);
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
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.fragment_appointment_details_list_item, parent, false);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.image.setImageResource(getResources().getIdentifier(mDetailsHeadersIcons[position],
                    "drawable", getActivity().getPackageName()));
            holder.text.setText(mDetailsHeadersTitles[position]);

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
