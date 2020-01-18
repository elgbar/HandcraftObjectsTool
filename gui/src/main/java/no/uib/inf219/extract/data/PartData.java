package no.uib.inf219.extract.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Elg
 */
public class PartData {

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
