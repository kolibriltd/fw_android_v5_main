package com.anstar.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NotificationCenter {

	private NotificationCenter() {
		m_observerCollection = new ConcurrentLinkedQueue<NotificationCenter.Observer>();
	}

	private volatile static NotificationCenter _instance = null;

	public static NotificationCenter Instance() {
		synchronized (NotificationCenter.class) {
			if (_instance == null) {
				synchronized (NotificationCenter.class) {
					if (_instance == null) {
						_instance = new NotificationCenter();
					}
				}
			}
		}
		return _instance;
	}

	ConcurrentLinkedQueue<Observer> m_observerCollection = null;

	public void addObserver(Object cls, String notificationType,
			String methodName, Object[] params) {
		Observer newObserver = new Observer(cls, notificationType, methodName,
				params);
		for (Observer observer : m_observerCollection) { 
			if (observer.equals(newObserver)) {
				return;
			}
		}
		m_observerCollection.add(newObserver);
	}

	public void removeObserver(Object cls) {
		for (Observer obs : m_observerCollection) {
			if (obs.Clazz.getClass() == cls.getClass()) {
				m_observerCollection.remove(obs);
			}
		}
	}

	public void postNotification(String notificationType) {
		for (Observer observer : m_observerCollection) {
			if (observer.NotificationType.equalsIgnoreCase(notificationType)) {
				callMethod(observer);
			}
		}
	}
	
	public void postNotification(String notificationType, Object[] params) {
		for (Observer observer : m_observerCollection) {
			if (observer.NotificationType.equalsIgnoreCase(notificationType)) {
				observer.Params = params;
				callMethod(observer);
			}
		}
	}

	private void callMethod(Observer observer) {
		if(observer.Clazz == null){
			this.removeObserver(observer.Clazz);
			return;
		}
		ArrayList<Class<?>> argumentTypes = new ArrayList<Class<?>>();
		if (observer.Params != null) {
			for (Object obj : observer.Params) {
				argumentTypes.add(obj.getClass());
			}
		}
		try {

			Method meth = null;
			if (argumentTypes.size() <= 0) {
				meth = observer.Clazz.getClass().getMethod(
						observer.MethodName);
			} else {
				meth = observer.Clazz.getClass().getMethod(
						observer.MethodName,
						(Class<?>[]) argumentTypes.toArray(new Class<?>[0]));
			}
			if(meth != null){
				meth.invoke(observer.Clazz, observer.Params);
			}

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	class Observer {

		public Observer(Object cls, String notificationType, String methodName,
				Object[] objs) {
			this.Clazz = cls;
			this.MethodName = methodName;
			this.NotificationType = notificationType;
			this.Params = objs;
		}

		public Object Clazz;
		public String NotificationType;
		public String MethodName;
		public Object[] Params;

		@Override
		public boolean equals(Object o) {
			if (o.getClass() == Observer.class) {
				Observer newObserver = (Observer) o;
				if (!newObserver.NotificationType
						.equalsIgnoreCase(this.NotificationType)) {
					return false;
				}
				if (newObserver.Clazz.getClass() == this.Clazz.getClass()
						&& newObserver.MethodName
								.equalsIgnoreCase(this.MethodName)) {
					if (this.Params == newObserver.Params) {
						return true;
					}
				}
			}
			return false;
		}
	}
}
