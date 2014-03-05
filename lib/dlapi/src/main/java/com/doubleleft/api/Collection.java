package com.doubleleft.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;


/**
 * Created by glaet on 2/28/14.
 */

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

    public Collection(Client client, String name)
    {
        if(!name.matches("^[a-z_//0-9]+$")){
            throw new Error("Invalid name"+ name);
        }

        this.client = client;
        this.name = name;
        this.segments = "collection/" + this.name;
        this.reset();
    }

    public void create(JSONObject data, Responder responder)
    {
        JSONObject dataToPost = new JSONObject();
        try{
            dataToPost.put("data", data);

        }catch (JSONException exception){

        }

        client.post(this.segments, dataToPost, responder);
    }

    public void get(Responder responder)
    {
        client.get(this.segments, this.buildQuery(), responder);
    }

    public void first(Responder responder)
    {
        _options.first = true;
        this.get(responder);
    }

    public void firstOrCreate(Responder responder)
    {
        //TODO: implement firstOrCreate method
        throw new Error("Not implemented");
    }

    public void count(Responder responder)
    {
        _options.aggregation = new CollectionOptionItem("count", null, null);
        this.get(responder);
    }

    public void max(String field, Responder responder)
    {
        _options.aggregation = new CollectionOptionItem("max", field, null);
        this.get(responder);
    }

    public void min(String field, Responder responder)
    {
        _options.aggregation = new CollectionOptionItem("min", field, null);
        this.get(responder);
    }

    public void avg(String field, Responder responder)
    {
        _options.aggregation = new CollectionOptionItem("avg", field, null);
        this.get(responder);
    }


    public void sum(String field, Responder responder)
    {
        _options.aggregation = new CollectionOptionItem("sum", field, null);
        this.get(responder);
    }

    public void update(int id, JSONObject data, Responder responder)
    {
        JSONObject dataToPost = new JSONObject();
        try{
            dataToPost.put("data", data);

        }catch (JSONException exception){

        }
        client.post(this.segments + "/" + id, dataToPost, responder);
    }

    public void updateAll(JSONObject data, Responder responder)
    {
        _options.data = data;
        client.put(this.segments, this.buildQuery(), responder);
    }

    public void increment(String field, Object value, Responder responder)
    {
        _options.operation = new CollectionOptionItem("increment", field, value);
        client.put(this.segments, this.buildQuery(), responder);
    }

    public void decrement(String field, Object value, Responder responder)
    {
        _options.operation = new CollectionOptionItem("decrement", field, value);
        client.put(this.segments, this.buildQuery(), responder);
    }

    public void remove(int id, Responder responder)
    {
        client.remove(this.segments + "/" + id, responder);
    }

    public void drop(Responder responder)
    {
        client.remove(this.segments, responder);
    }

    public Collection where(String field, Object value)
    {
        return this.where(field, "=", value);
    }

    public Collection where(String field, String operation, Object value)
    {
        CollectionWhere obj = new CollectionWhere(field, operation, value);
        _wheres.add(obj);
        return this;
    }

    public Collection group(String field)
    {
        _group.add(field);
        return this;
    }

    public Collection group(String[] fields)
    {
        for(int i = 0; i<fields.length; i++){
            _group.add(fields[i]);
        }
        return this;
    }

    public Collection sort(String field, Integer direction)
    {
        return this.sort(field, direction == -1 ? "desc" : "asc");
    }

    public Collection sort(String field, String direction)
    {
        CollectionOrdering obj = new CollectionOrdering(field, direction);
        _ordering.add(obj);
        return this;
    }

    public Collection limit(Integer num)
    {
        _limit = num;
        return this;
    }

    public Collection offset(Integer num)
    {
        _offset = num;
        return this;
    }

    protected JSONObject buildQuery()
    {
        JSONObject query = new JSONObject();
        this.reset(); //clear for future calls
        return query;
    }

    protected void reset()
    {
        this._options = new CollectionOptions();
        this._wheres = new Vector<CollectionWhere>();
        this._ordering = new Vector<CollectionOrdering>();
        this._group = new Vector<String>();
        this._limit = null;
        this._offset = null;
    }

    class CollectionOptions
    {
        public boolean first;
        public JSONObject data;
        public CollectionOptionItem aggregation;
        public CollectionOptionItem operation;

        public CollectionOptions()
        {
            first = false;
            data = null;
            aggregation = null;
            operation = null;
        }
    }

    class CollectionOptionItem
    {
        public String method;
        public String field;
        public Object value;

        public CollectionOptionItem(String method, String field, Object value)
        {
            this.method = method;
            this.field = field;
            this.value = value;
        }
    }

    class CollectionWhere
    {
        public String field;
        public String operation;
        public Object value;

        public CollectionWhere(String field, String operation, Object value)
        {
            this.field = field;
            this.operation = operation;
            this.value = value;
        }
    }

    class CollectionOrdering
    {
        public String field;
        public String direction;

        public CollectionOrdering(String field, String direction)
        {
            this.field = field;
            this.direction = direction;
        }
    }
}