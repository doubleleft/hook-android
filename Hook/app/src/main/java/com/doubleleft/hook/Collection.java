package com.doubleleft.hook;

import java.util.Vector;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by glaet on 2/28/14.
 */

@SuppressLint("DefaultLocale")
public class Collection {
	protected Client client;
	protected String name;
	protected String segments;

	protected CollectionOptions _options;
	protected Vector<CollectionWhere> _wheres;
	protected Vector<CollectionOrdering> _ordering;
	protected Vector<String> _group;
	protected Integer _limit;
	protected Integer _offset;

	public Collection(Client client, String name) {
		if (!name.matches("^[a-z_//0-9]+$")) {
			throw new Error("Invalid name" + name);
		}

		this.client = client;
		this.name = name;
		this.segments = "collection/" + this.name;
		this.reset();
	}

	public RequestHandle create(JSONObject data, AsyncHttpResponseHandler responseHandler) {
		return client.post(this.segments, data, responseHandler);
	}

	public RequestHandle get(AsyncHttpResponseHandler responseHandler) {
		return client.get(this.segments, this.buildQuery(), responseHandler);
	}

	public RequestHandle first(AsyncHttpResponseHandler responseHandler) {
		_options.first = true;
		return this.get(responseHandler);
	}

	public RequestHandle firstOrCreate(JSONObject data, AsyncHttpResponseHandler responseHandler) {
		_options.first = true;
        _options.data = data;
        return this.client.post(this.segments, this.buildQuery(), responseHandler);
	}

	public RequestHandle count(AsyncHttpResponseHandler responseHandler) {
		_options.aggregation = new CollectionOptionItem("count", null, null);
		return this.get(responseHandler);
	}

	public RequestHandle max(String field, AsyncHttpResponseHandler responseHandler) {
		_options.aggregation = new CollectionOptionItem("max", field, null);
		return this.get(responseHandler);
	}

	public RequestHandle min(String field, AsyncHttpResponseHandler responseHandler) {
		_options.aggregation = new CollectionOptionItem("min", field, null);
		return this.get(responseHandler);
	}

	public RequestHandle avg(String field, AsyncHttpResponseHandler responseHandler) {
		_options.aggregation = new CollectionOptionItem("avg", field, null);
		return this.get(responseHandler);
	}

	public RequestHandle sum(String field, AsyncHttpResponseHandler responseHandler) {
		_options.aggregation = new CollectionOptionItem("sum", field, null);
		return this.get(responseHandler);
	}

	public RequestHandle update(int id, JSONObject data, AsyncHttpResponseHandler responseHandler) {
		return client.post(this.segments + "/" + id, data, responseHandler);
	}

	public RequestHandle updateAll(JSONObject data, AsyncHttpResponseHandler responseHandler) {
		_options.data = data;
		return client.put(this.segments, this.buildQuery(), responseHandler);
	}

	public RequestHandle increment(String field, Object value, AsyncHttpResponseHandler responseHandler) {
		_options.operation = new CollectionOptionItem("increment", field, value);
		return client.put(this.segments, this.buildQuery(), responseHandler);
	}

	public RequestHandle decrement(String field, Object value, AsyncHttpResponseHandler responseHandler) {
		_options.operation = new CollectionOptionItem("decrement", field, value);
		return client.put(this.segments, this.buildQuery(), responseHandler);
	}

	public RequestHandle remove(int id, AsyncHttpResponseHandler responseHandler) {
		return client.remove(this.segments + "/" + id, responseHandler);
	}

	public RequestHandle drop(AsyncHttpResponseHandler responseHandler) {
		return client.remove(this.segments, responseHandler);
	}

	public Collection where(String field, Object value) {
		return this.where(field, "=", value);
	}

	public Collection where(String field, String operation, Object value) {
		_wheres.add(new CollectionWhere(field, operation, value, "and"));
		return this;
	}

    public Collection orWhere(String field, String operation, Object value) {
        _wheres.add(new CollectionWhere(field, operation, value, "or"));
        return this;
    }

	public Collection group(String field) {
		_group.add(field);
		return this;
	}

	public Collection group(String[] fields) {
		for (int i = 0; i < fields.length; i++) {
			_group.add(fields[i]);
		}
		return this;
	}

	public Collection sort(String field, Integer direction) {
		return this.sort(field, direction == -1 ? "desc" : "asc");
	}

	public Collection sort(String field, String direction) {
		_ordering.add(new CollectionOrdering(field, direction));
		return this;
	}

	public Collection limit(Integer num) {
		_limit = num;
		return this;
	}

	public Collection offset(Integer num) {
		_offset = num;
		return this;
	}

	protected JSONObject buildQuery() {
        JSONObject query = new JSONObject();

        try {
            if (_limit != null) {
                query.put("limit", _limit);
            }

            if (_offset != null) {
                query.put("offset", _offset);
            }

            if (_wheres != null && _wheres.size() > 0) {
                JSONArray whereArray = new JSONArray();
                for (int i = 0; i < _wheres.size(); i++) {
                    whereArray.put(_wheres.get(i).toJSON());
                }
                query.put("q", whereArray);
            }

            if (_ordering != null && _ordering.size() > 0) {
                JSONArray orderingArray = new JSONArray();
                for (int i = 0; i < _ordering.size(); i++) {
                    orderingArray.put(_ordering.get(i).toJSON());
                }
                query.put("s", orderingArray);
            }

            if (_group != null && _group.size() > 0) {
                JSONArray groupArray = new JSONArray();
                for (int i = 0; i < _group.size(); i++) {
                    groupArray.put(_group.get(i));
                }
                query.put("g", groupArray);
            }

            if (_options != null) {
                if (_options.data != null) {
                    query.put("data", _options.data);
                }

                if (_options.first) {
                    query.put("f", 1);
                }

                if (_options.aggregation != null) {
                    query.put("aggr", _options.aggregation.toJSON());
                }

                if (_options.operation != null) {
                    query.put("op", _options.operation.toJSON());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

		this.reset(); // clear for future calls
		return query;
	}

	protected void reset() {
		this._options = new CollectionOptions();
		this._wheres = new Vector<CollectionWhere>();
		this._ordering = new Vector<CollectionOrdering>();
		this._group = new Vector<String>();
		this._limit = null;
		this._offset = null;
	}

	class CollectionOptions {
		public boolean first;
		public JSONObject data;
		public CollectionOptionItem aggregation;
		public CollectionOptionItem operation;

		public CollectionOptions() {
			first = false;
			data = null;
			aggregation = null;
			operation = null;
		}
	}

	class CollectionOptionItem {
		public String method;
		public String field;
		public Object value;

		public CollectionOptionItem(String method, String field, Object value) {
			this.method = method;
			this.field = field;
			this.value = value;
		}

		public JSONObject toJSON() {
            JSONObject json = new JSONObject();

            try {
                json.put("method", method);
                json.put("field", field);
                json.put("value", value);
            } catch (JSONException e) {
                e.printStackTrace();
            }

			return json;
		}

	}

	class CollectionWhere {
		public String field;
		public String operation;
		public Object value;
        public String bool;

        public CollectionWhere(String field, String operation, Object value, String bool) {
            this.field = field;
            this.operation = operation.toLowerCase();
            this.value = value;
            this.bool = bool;
        }

		public JSONArray toJSON() {
			JSONArray json = new JSONArray();
			json.put(field);
			json.put(operation);
			json.put(value);
            json.put(bool);
			return json;
		}

	}

	class CollectionOrdering {
		public String field;
		public String direction;

		public CollectionOrdering(String field, String direction) {
			this.field = field;
			this.direction = direction;
		}

		public JSONArray toJSON() {
            JSONArray json = new JSONArray();
            json.put(field);
            json.put(direction);
			return json;
		}

	}
}
