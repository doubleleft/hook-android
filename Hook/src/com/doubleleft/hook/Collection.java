package com.doubleleft.hook;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

	public Request create(JSONObject data) {
		return client.post(this.segments, data);
	}

	public Request get() {
		return client.get(this.segments, this.buildQuery());
	}

	public Request first() {
		_options.first = true;
		return this.get();
	}

	public Request firstOrCreate(JSONObject data) {
		// TODO: implement firstOrCreate method
		throw new Error("Not implemented");
	}

	public Request count() {
		_options.aggregation = new CollectionOptionItem("count", null, null);
		return this.get();
	}

	public Request max(String field) {
		_options.aggregation = new CollectionOptionItem("max", field, null);
		return this.get();
	}

	public Request min(String field) {
		_options.aggregation = new CollectionOptionItem("min", field, null);
		return this.get();
	}

	public Request avg(String field) {
		_options.aggregation = new CollectionOptionItem("avg", field, null);
		return this.get();
	}

	public Request sum(String field) {
		_options.aggregation = new CollectionOptionItem("sum", field, null);
		return this.get();
	}

	public Request update(int id, JSONObject data) {
		return client.post(this.segments + "/" + id, data);
	}

	public Request updateAll(JSONObject data) {
		_options.data = data;
		return client.put(this.segments, this.buildQuery());
	}

	public Request increment(String field, Object value) {
		_options.operation = new CollectionOptionItem("increment", field, value);
		return client.put(this.segments, this.buildQuery());
	}

	public Request decrement(String field, Object value) {
		_options.operation = new CollectionOptionItem("decrement", field, value);
		return client.put(this.segments, this.buildQuery());
	}

	public Request remove(int id) {
		return client.remove(this.segments + "/" + id);
	}

	public Request drop() {
		return client.remove(this.segments);
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

	protected JSONObject buildQuery() {
		JSONObject query = new JSONObject();

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
			Log.d("dl-api", "error building query " + e.toString());
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
				json.putOpt("method", method == null ? JSONObject.NULL : method);
				json.putOpt("field", field == null ? JSONObject.NULL : field);
				json.putOpt("value", value == null ? JSONObject.NULL : value);

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
			json.put(field == null ? JSONObject.NULL : field);
			json.put(operation == null ? JSONObject.NULL : operation.toLowerCase());
			json.put(value == null ? JSONObject.NULL : value);
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

		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			try {
				json.putOpt("field", field == null ? JSONObject.NULL : field);
				json.putOpt("direction", direction == null ? JSONObject.NULL : direction);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json;
		}

	}
}
