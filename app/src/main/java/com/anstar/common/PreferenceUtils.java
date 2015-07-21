/**
*	Copyright (c) @ Samcom Technobrains.
*   AvantarApr 27, 2011
**/
package com.anstar.common;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;



/**
 * The Class specifically developed for transaction of fav Favorites
 * @author samir
 *
 */
public class PreferenceUtils {
	
	private static final String PREFS_NAME = "QuotesPrefsFile";
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	private static final String FAVORITE_PREF = "FavoriteIds";
	private ArrayList<String> m_favIds = null;
	private Context m_context;
	private PreferenceUtils()
	{
		
	}
	
	private static PreferenceUtils _instance = null;
	public static PreferenceUtils Instance(Context c){
 		if(_instance == null)
 		{
 			synchronized (PreferenceUtils.class) {
				_instance = new PreferenceUtils();
			}
 		}
 		_instance.m_context = c;
 		_instance.settings = _instance.m_context.getSharedPreferences(PREFS_NAME, 0);
 		return _instance;
 	}
	
	public static PreferenceUtils Instance()
	{
		if(_instance.m_context == null){
			return null;
		}
		return Instance(_instance.m_context);
	}
	
	
	class SetFavFavorite extends AsyncTask<String, Void, Void>
	{

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			String FavoriteId = params[0];
			editor = settings.edit(); 
			String ids = settings.getString(FAVORITE_PREF, "");
			if(ids.length() > 0)
			{
				ids = ids + "##" + FavoriteId;
			}
			else
			{
				ids = FavoriteId;
			}
			editor.putString(FAVORITE_PREF, ids);
			editor.commit();
			return null;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			m_favIds = null;
			super.onPostExecute(result);
		}
		
	}
	
	class RemoveFavFavorite extends AsyncTask<String, Void, Void>
	{

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			String FavoriteId = params[0];
			ArrayList<String> ids = new ArrayList<String>();
			ids = getStartedFavorites();
			ids.remove(FavoriteId);
			String tmp = Utils.Instance().join(ids, "##");
			editor = settings.edit();
			editor.putString(FAVORITE_PREF, tmp);
			editor.commit();
			return null;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			m_favIds = null;
			super.onPostExecute(result);
		}
		
	}
	
	
	public void setFavoriteId(String FavoriteId)
	{
		if(isStartedFavorite(FavoriteId)){
			removeFavoriteId(FavoriteId);
		}else{
			SetFavFavorite setFav = new SetFavFavorite();
			setFav.execute(FavoriteId);
		}
		m_favIds = null;
	}
	
	public ArrayList<String> getStartedFavorites()
	{
		if(this.m_favIds  == null)
		{
			ArrayList<String> ids = new ArrayList<String>();
			String tmp = settings.getString(FAVORITE_PREF, "");
			if(tmp.length() <= 0)
			{
				return new ArrayList<String>();
			}
			ids.addAll( Arrays.asList(tmp.split("##")));
			this.m_favIds = ids;
		}
		return this.m_favIds;
	}
	
	public void removeFavoriteId(String FavoriteId)
	{
		RemoveFavFavorite removeFav = new RemoveFavFavorite();
		removeFav.execute(FavoriteId);
	}
	
	public boolean isStartedFavorite(String FavoriteId)
	{
		ArrayList<String> ids = this.getStartedFavorites();
		for (String thid : ids) {
			if(thid.equalsIgnoreCase(FavoriteId))
			{
				return true;
			}
		}
		return false;
	}
	
}
