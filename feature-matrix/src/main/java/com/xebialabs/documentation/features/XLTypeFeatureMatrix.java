package com.xebialabs.documentation.features;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.xebialabs.deployit.booter.local.LocalPropertyDescriptor;
import com.xebialabs.deployit.plugin.api.reflect.Descriptor;
import com.xebialabs.deployit.plugin.api.reflect.DescriptorRegistry;
import com.xebialabs.deployit.plugin.api.reflect.PropertyDescriptor;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.api.udm.Deployed;

/**
 * Scans the XL Type registry to populate a feature matrix.
 *
 * @author Hes Siemelink
 */
public class XLTypeFeatureMatrix extends FeatureMatrix {

    public XLTypeFeatureMatrix(String name) {
        super(name);
    }

    public XLTypeFeatureMatrix(JSONObject json) throws JSONException {
        super(json);
    }

    public void scanTypes(String version, String prefix) {
        for (Descriptor descriptor : DescriptorRegistry.getDescriptors()) {

            // No vritual types
            if (descriptor.isVirtual()) {
                continue;
            }

            // Only selected types
            if (!descriptor.getType().getPrefix().startsWith(prefix)) {
                continue;
            }

            // Only Deployeds and Containers
            if (!isDeployedOrContainer(descriptor.getType())) {
                continue;
            }

            String typeName = descriptor.getType().toString();
            typeName = typeName.replace("wasx.", "was.");
            FeatureMatrix typeFeatures = getFeature(typeName);
            typeFeatures.addVersion(version);

            scanProperties(typeFeatures, descriptor, version);
            add(typeFeatures);
        }
    }

    private static void scanProperties(FeatureMatrix typeFeatures, Descriptor descriptor, String version) {
        for (PropertyDescriptor property : descriptor.getPropertyDescriptors()) {

            if (property.isHidden()) {
                continue;
            }

            if (isInherited(property)) {
                continue;
            }

            typeFeatures.getFeature(property.getName()).addVersion(version);
        }
    }

    private static boolean isDeployedOrContainer(Type type) {
        return type.instanceOf(Type.valueOf(Deployed.class)) || type.instanceOf(Type.valueOf(Container.class));
    }

    private static boolean isInherited(PropertyDescriptor property) {
        return ((LocalPropertyDescriptor) property).isInherited();
    }

}
