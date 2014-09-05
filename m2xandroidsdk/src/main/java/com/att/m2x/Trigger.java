package com.att.m2x;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.att.m2x.helpers.JSONHelper;
import com.att.m2x.helpers.JSONSerializable;

public final class Trigger extends com.att.m2x.model.Trigger implements JSONSerializable {

	public interface TriggersListener {
		public void onSuccess(ArrayList<Trigger> triggers);
		public void onError(String errorMessage);
	}

	public interface TriggerListener {
		public void onSuccess(Trigger trigger);
		public void onError(String errorMessage);		
	}

	public interface BasicListener {
		public void onSuccess();
		public void onError(String errorMessage);		
	}	
	
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String STREAM = "stream";
	private static final String CONDITION = "condition";
	private static final String VALUE = "value";
	private static final String CALLBACK_URL = "callback_url";
	private static final String URL = "url";
	private static final String STATUS = "status";
	private static final String CREATED = "created";
	private static final String UPDATED = "updated";

	public Trigger() {
		
	}
	
	public Trigger(JSONObject obj) {
		
		this.setId(JSONHelper.stringValue(obj, ID, ""));
		this.setName(JSONHelper.stringValue(obj, NAME, ""));
		this.setStream(JSONHelper.stringValue(obj, STREAM, ""));
		this.setCondition(JSONHelper.stringValue(obj, CONDITION, ""));
		this.setValue(JSONHelper.doubleValue(obj, VALUE, 0));
		this.setCallbackUrl(JSONHelper.stringValue(obj, CALLBACK_URL, ""));
		this.setUrl(JSONHelper.stringValue(obj, URL, ""));
		this.setStatus(JSONHelper.stringValue(obj, STATUS, ""));
		this.setCreated(JSONHelper.dateValue(obj, CREATED, null));
		this.setUpdated(JSONHelper.dateValue(obj, UPDATED, null));
	}
	
	public Trigger(Parcel in) {
		super(in);
	}
	
	public static void getTriggers(Context context, String feedKey, String feedId, final TriggersListener callback) {

		M2XHttpClient client = M2X.getInstance().getClient();
		String path = "/feeds/" + feedId + "/triggers";
		
		client.get(context, feedKey, path, null, new M2XHttpClient.Handler() {

			@Override
			public void onSuccess(int statusCode, JSONObject object) {

				ArrayList<Trigger> array = new ArrayList<Trigger>();
				try {
					JSONArray triggers = object.getJSONArray("triggers");
					for (int i = 0; i < triggers.length(); i++) {
						Trigger trigger = new Trigger(triggers.getJSONObject(i));
						array.add(trigger);
					}
				} catch (JSONException e) {
					M2XLog.d("Failed to parse Trigger JSON objects");
				}
				callback.onSuccess(array);
				
			}

			@Override
			public void onFailure(int statusCode, String body) {
				callback.onError(body);
			}
			
		});
		
	}

	public static void getTrigger(Context context, String feedKey, String feedId, String triggerId, final TriggerListener callback) {

		M2XHttpClient client = M2X.getInstance().getClient();
		String path = "/feeds/" + feedId + "/triggers/" + triggerId;
		
		client.get(context, feedKey, path, null, new M2XHttpClient.Handler() {

			@Override
			public void onSuccess(int statusCode, JSONObject object) {
				Trigger trigger = new Trigger(object);
				callback.onSuccess(trigger);				
			}

			@Override
			public void onFailure(int statusCode, String message) {
				callback.onError(message);
			}
			
		});

	}
	
	public void create(Context context, String feedKey, String feedId, final TriggerListener callback) {
		
		M2XHttpClient client = M2X.getInstance().getClient();
		String path = "/feeds/" + feedId + "/triggers";

        if (this.getName() != null && this.getStream() != null && this.getCondition() != null && this.getCallbackUrl() != null) {
            JSONObject content = this.toJSONObject();
            client.post(context, feedKey, path, content, new M2XHttpClient.Handler() {

                @Override
                public void onSuccess(int statusCode, JSONObject object) {
                    Trigger trigger = new Trigger(object);
                    callback.onSuccess(trigger);
                }

                @Override
                public void onFailure(int statusCode, String message) {
                    callback.onError(message);
                }

            });
        } else {
            callback.onError("Trigger name, stream, condition and callback url are required attributes!");
        }

	}
	
	public void update(Context context, String feedKey, String feedId, final BasicListener callback) {
		
		M2XHttpClient client = M2X.getInstance().getClient();
		String path = "/feeds/" + feedId + "/triggers/" + this.getId();

        if (this.getName() != null && this.getStream() != null && this.getCondition() != null && this.getCallbackUrl() != null) {
            JSONObject content = this.toJSONObject();
            client.put(context, feedKey, path, content, new M2XHttpClient.Handler() {

                @Override
                public void onSuccess(int statusCode, JSONObject object) {
                    callback.onSuccess();
                }

                @Override
                public void onFailure(int statusCode, String message) {
                    callback.onError(message);
                }

            });
        } else {
            callback.onError("Trigger name, stream, condition and callback url are required attributes!");
        }

	}
	
	public void test(Context context, String feedKey, String feedId, final BasicListener callback) {
		
		M2XHttpClient client = M2X.getInstance().getClient();
		String path = "/feeds/" + feedId + "/triggers/" + this.getId() + "/test";
		client.post(context, feedKey, path, null, new M2XHttpClient.Handler() {

			@Override
			public void onSuccess(int statusCode, JSONObject object) {
				// drops result info brought back by testing function
				callback.onSuccess();				
			}

			@Override
			public void onFailure(int statusCode, String message) {
				callback.onError(message);
			}
			
		});

	}

	public void delete(Context context, String feedKey, String feedId, final BasicListener callback) {
		
		M2XHttpClient client = M2X.getInstance().getClient();
		String path = "/feeds/" + feedId + "/triggers/" + this.getId();
		client.delete(context, feedKey, path, new M2XHttpClient.Handler() {
			
			@Override
			public void onSuccess(int statusCode, JSONObject object) {
				callback.onSuccess();				
			}
			
			@Override
			public void onFailure(int statusCode, String message) {
				callback.onError(message);
			}
		});
		
	}
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();		
		JSONHelper.put(obj, NAME, this.getName());
		JSONHelper.put(obj, STREAM, this.getStream());
		JSONHelper.put(obj, CONDITION, this.getCondition());
		JSONHelper.put(obj, VALUE, this.getValue());
		JSONHelper.put(obj, CALLBACK_URL, this.getCallbackUrl());
		JSONHelper.put(obj, STATUS, this.getStatus());
		return obj;
	}

	public static final Parcelable.Creator<Trigger> CREATOR = new Parcelable.Creator<Trigger>() {
	    public Trigger createFromParcel(Parcel in) {
	     return new Trigger(in);
	    }

	    public Trigger[] newArray(int size) {
	     return new Trigger[size];
	    }
	};

}