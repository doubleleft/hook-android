package com.doubleleft.hook;

import java.util.Vector;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import android.annotation.SuppressLint;
import android.util.Log;

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

	public RequestHandle create(RequestParams data, ResponseHandlerInterface responseHandler) {
		return client.post(this.segments, data, responseHandler);
	}

	public RequestHandle get(ResponseHandlerInterface responseHandler) {
		return client.get(this.segments, this.buildQuery(), responseHandler);
	}

	public RequestHandle first(ResponseHandlerInterface responseHandler) {
		_options.first = true;
		return this.get(responseHandler);
	}

	public RequestHandle firstOrCreate(RequestParams data, ResponseHandlerInterface responseHandler) {
		_options.first = true;
		// TODO: implement firstOrCreate method
		throw new Error("Not implemented");
	}

	public RequestHandle count(ResponseHandlerInterface responseHandler) {
		_options.aggregation = new CollectionOptionItem("count", null, null);
		return this.get(responseHandler);
	}

	public RequestHandle max(String field, ResponseHandlerInterface responseHandler) {
		_options.aggregation = new CollectionOptionItem("max", field, null);
		return this.get(responseHandler);
	}

	public RequestHandle min(String field, ResponseHandlerInterface responseHandler) {
		_options.aggregation = new CollectionOptionItem("min", field, null);
		return this.get(responseHandler);
	}

	public RequestHandle avg(String field, ResponseHandlerInterface responseHandler) {
		_options.aggregation = new CollectionOptionItem("avg", field, null);
		return this.get(responseHandler);
	}

	public RequestHandle sum(String field, ResponseHandlerInterface responseHandler) {
		_options.aggregation = new CollectionOptionItem("sum", field, null);
		return this.get(responseHandler);
	}

	public RequestHandle update(int id, RequestParams data, ResponseHandlerInterface responseHandler) {
		return client.post(this.segments + "/" + id, data, responseHandler);
	}

	public RequestHandle updateAll(RequestParams data, ResponseHandlerInterface responseHandler) {
		_options.data = data;
		return client.put(this.segments, this.buildQuery(), responseHandler);
	}

	public RequestHandle increment(String field, Object value, ResponseHandlerInterface responseHandler) {
		_options.operation = new CollectionOptionItem("increment", field, value);
		return client.put(this.segments, this.buildQuery(), responseHandler);
	}

	public RequestHandle decrement(String field, Object value, ResponseHandlerInterface responseHandler) {
		_options.operation = new CollectionOptionItem("decrement", field, value);
		return client.put(this.segments, this.buildQuery(), responseHandler);
	}

	public RequestHandle remove(int id, ResponseHandlerInterface responseHandler) {
		return client.remove(this.segments + "/" + id, responseHandler);
	}

	public RequestHandle drop(ResponseHandlerInterface responseHandler) {
		return client.remove(this.segments, responseHandler);
	}

	public Collection where(String field, Object value) {
		return this.where(field, "=", value);
	}

	public Collection where(String field, String operation, Object value) {
		CollectionWhere obj = new CollectionWhere(field, operation, value);
		_wheres.add(obj);
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
		CollectionOrdering obj = new CollectionOrdering(field, direction);
		_ordering.add(obj);
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

	protected RequestParams buildQuery() {
		RequestParams query = new RequestParams();

		try {
			if (_limit != null) {
				query.putOpt("limit", _limit);
			}

			if (_offset != null) {
				query.putOpt("offset", _offset);
			}

			if (_wheres != null && _wheres.size() > 0) {
				JSONArray whereArray = new JSONArray();
				for (int i = 0; i < _wheres.size(); i++) {
					whereArray.put(_wheres.get(i).toJSON());
				}
				query.putOpt("q", whereArray);
			}

			if (_ordering != null && _ordering.size() > 0) {
				JSONArray orderingArray = new JSONArray();
				for (int i = 0; i < _ordering.size(); i++) {
					orderingArray.put(_ordering.get(i).toJSON());
				}
				query.putOpt("s", orderingArray);
			}

			if (_group != null && _group.size() > 0) {
				JSONArray groupArray = new JSONArray();
				for (int i = 0; i < _group.size(); i++) {
					groupArray.put(_group.get(i));
				}
				query.putOpt("g", groupArray);
			}

			if (_options != null) {
				if (_options.data != null) {
					query.putOpt("d", _options.data);
				}

				if (_options.first) {
					query.putOpt("first", 1);
				}

				if (_options.aggregation != null) {
					query.putOpt("aggr", _options.aggregation.toJSON());
				}

				if (_options.operation != null) {
					query.putOpt("op", _options.operation.toJSON());
				}
			}

		} catch (JSONException e) {
			Log.d("hook", "error building query " + e.toString());
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
		public RequestParams data;
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

		public RequestParams toJSON() {
			RequestParams json = new RequestParams();
			try {
				json.putOpt("method", method == null ? RequestParams.NULL : method);
				json.putOpt("field", field == null ? RequestParams.NULL : field);
				json.putOpt("value", value == null ? RequestParams.NULL : value);

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

		public CollectionWhere(String field, String operation, Object value) {
			this.field = field;
			this.operation = operation;
			this.value = value;
		}

		public Object toJSON() {
			JSONArray json = new JSONArray();
			json.put(field == null ? RequestParams.NULL : field);
			json.put(operation == null ? RequestParams.NULL : operation.toLowerCase());
			json.put(value == null ? RequestParams.NULL : value);
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

		public RequestParams toJSON() {
			RequestParams json = new RequestParams();
			try {
				json.putOpt("field", field == null ? RequestParams.NULL : field);
				json.putOpt("direction", direction == null ? RequestParams.NULL : direction);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json;
		}

	}
}