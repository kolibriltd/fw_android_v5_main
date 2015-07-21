package com.anstar.common;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anstar.fieldwork.R;

public class LogoutProvider extends ActionProvider {

	private final Context mContext;

	public LogoutProvider(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public View onCreateActionView() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View view = layoutInflater.inflate(R.layout.settings_action_provider,
				null);
		Button button = (Button) view.findViewById(R.id.btnLogout);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "Logout", Toast.LENGTH_LONG).show();
			}
		});
		return view;
	}

}
