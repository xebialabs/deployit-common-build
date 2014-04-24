package com.xebialabs.documentation.features;

import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.xebialabs.xltype.serialization.json.JsonWriter;

import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.google.common.collect.Sets.newTreeSet;

/**
 * Nested data structure to contains feature name, supported version and child features. Support JSON serialization.
 *
 * @author Hes Siemelink
 */
public class FeatureMatrix implements Comparable<FeatureMatrix> {

    private final String name;
    private final Set<String> supportedVersions = newLinkedHashSet();
    private final Map<String, FeatureMatrix> children = newTreeMap();

    public FeatureMatrix(String name) {
        this.name = name;
    }

    public FeatureMatrix addVersion(String version) {
        supportedVersions.add(version);
        return this;
    }

    public FeatureMatrix(JSONObject json) throws JSONException {
        this.name = json.getString("name");

        // Versions
        JSONArray versions = json.getJSONArray("versions");
        for (int i = 0; i < versions.length(); i++) {
            supportedVersions.add(versions.getString(i));
        }

        // Children
        if (json.has("features")) {
            JSONArray children = json.getJSONArray("features");
            for (int i = 0; i < children.length(); i++) {
                add(new FeatureMatrix(children.getJSONObject(i)));
            }
        }

    }

    public FeatureMatrix getFeature(String name) {
        FeatureMatrix feature = children.get(name);
        if (feature == null) {
            feature = new FeatureMatrix(name);
            add(feature);
        }
        return feature;
    }

    public void add(FeatureMatrix feature) {
        children.put(feature.getName(), feature);
    }

    public void combine(FeatureMatrix other) {
        supportedVersions.addAll(other.getSupportedVersions());

        Set<String> names = newTreeSet(children.keySet());
        names.addAll(other.getChildren().keySet());
        for (String name : names) {
            getFeature(name).combine(other.getFeature(name));
        }
    }

    //
    // Serialization
    //


    public void writeJson(JsonWriter writer) {
        writer.object();
        writer.key("name").value(name);
        writer.key("versions").array();
        for (String version : supportedVersions) {
            writer.value(version);
        }
        writer.endArray();
        if (!children.isEmpty()) {
            writer.key("features");
            writer.array();
            for (FeatureMatrix feature : children.values()) {
                feature.writeJson(writer);
            }
            writer.endArray();
        }
        writer.endObject();

    }

    //
    // Getters and Setters
    //

    public String getName() {
        return name;
    }

    public Set<String> getSupportedVersions() {
        return supportedVersions;
    }

    public Map<String, FeatureMatrix> getChildren() {
        return children;
    }

    @Override
    public int compareTo(FeatureMatrix o) {
        return name.compareTo(o.name);
    }
}