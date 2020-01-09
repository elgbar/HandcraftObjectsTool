package no.elg.valentineRealms.core.parts.extract.data;

import no.uib.inf219.api.serialization.SerializationManager;
import no.uib.inf219.api.serialization.Serializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Elg
 */
//@SerializableAs("part_metadata")
public class PartData implements Serializer {

    public static final String CLASS_KEY = "class";
    public static final String ATTRIBUTES_KEY = "attributes";


    /**
     * Canonical class name of the relevant class
     */
    @NotNull
    public final String className;
    /**
     * List of all attributes this part has
     *
     * @see AttributeData
     */
    @NotNull
    public final List<AttributeData> attributes;

    static {
        SerializationManager.registerClass(PartData.class);
    }

    @Contract(pure = true)
    public PartData(@NotNull Class<?> clazz, @NotNull List<AttributeData> attributes) {
        className = clazz.getCanonicalName();
        this.attributes = attributes;
    }

    @Contract(pure = true)
    public PartData(@NotNull String className, @NotNull List<AttributeData> attributes) {
        this.className = className;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put(CLASS_KEY, className);
        map.put(ATTRIBUTES_KEY, attributes);
        return map;
    }

    @SuppressWarnings("unused")
    @NotNull
    public static PartData deserialize(@NotNull Map<String, Object> args) {
        String className = (String) args.get(CLASS_KEY);
        //noinspection unchecked
        List<AttributeData> data = (List<AttributeData>) args.get(ATTRIBUTES_KEY);
        return new PartData(className, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartData)) {
            return false;
        }

        PartData data1 = (PartData) o;

        if (!className.equals(data1.className)) {
            return false;
        }
        return attributes.equals(data1.attributes);
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + attributes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PartData{");
        sb.append("className='").append(className).append('\'');
        sb.append(", data=").append(attributes);
        sb.append('}');
        return sb.toString();
    }
}
