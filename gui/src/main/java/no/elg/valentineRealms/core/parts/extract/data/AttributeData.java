package no.elg.valentineRealms.core.parts.extract.data;

import no.elg.valentineRealms.core.parts.extract.AnnotationUtil;
import no.elg.valentineRealms.core.parts.extract.PartsExtractor;
import no.uib.inf219.api.annontation.AttributeDoc;
import no.uib.inf219.api.annontation.EnumValueDesc;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Elg
 */
//@SerializableAs("part_attribute")
public class AttributeData implements ConfigurationSerializable {

    public static final String PATH_KEY = "path";
    public static final String CLASS_NAME_KEY = "className";
    public static final String DEFAULT_VALUE_KEY = "defaultValue";
    public static final String IS_LIST_KEY = "isList";
    public static final String NOTE_KEY = "note";
    public static final String REQUIRED_KEY = "required";

    /**
     * The path to access this attribute
     *
     * @see AttributeDoc#path()
     */
    @NotNull
    public final String path;

    /**
     * Canonical name of the class this attribute represents
     */
    @NotNull
    public final String className;
    /**
     * The default value this attribute has. If {@link String#isEmpty()} there is no default value
     *
     * @see AttributeDoc#defaultValue()
     */
    @NotNull
    public final String defaultValue;
    /**
     * Extra note on this attribute
     *
     * @see AttributeDoc#note()
     */
    @NotNull
    public final String note;

    /**
     * If this is a list of attributes. If so {@link #className} denotes the generic type of the class.
     */
    public final boolean isList;
    /**
     * If this attribute must be present
     *
     * @see AttributeDoc#required()
     */
    public final boolean required;

    @Contract(pure = true)
    public AttributeData(@NotNull String path, @NotNull String className, boolean isList, boolean required,
                         @NotNull String defaultValue, @NotNull String note) {
        this.path = path;
        this.className = className;
        this.isList = isList;

        this.required = required;
        this.defaultValue = defaultValue;
        this.note = note;
    }

    @Contract(pure = true)
    public AttributeData(@NotNull Field field) {
        AttributeDoc ad = field.getAnnotation(AttributeDoc.class);
        if (ad == null) {
            throw new IllegalArgumentException("Field must have @AttributeDoc as attribute");
        }

        path = AnnotationUtil.getAttributePath(field);
        isList = List.class.isAssignableFrom(field.getType());
        if (isList) {
            Class<?> fieldType = PartsExtractor.getGenericFieldType(field);
            if (fieldType == null) {
                throw new IllegalArgumentException("Can not determine the generic type of the given list");
            }
            className = fieldType.getCanonicalName();
        } else {
            className = field.getType().getCanonicalName();
        }
        required = ad.required();
        String tempDefaultValue = ad.defaultValue();

        if (ad.defaultValue().isEmpty() && field.getType().isEnum()) {
            //noinspection unchecked
            for (Map.Entry<String, Field> entry : AnnotationUtil.getEnumFields(
                    (Class<? extends Enum<?>>) field.getType()).entrySet()) {

                EnumValueDesc enumValAnn = entry.getValue().getAnnotation(EnumValueDesc.class);

                if (enumValAnn != null && enumValAnn.isDefault()) {
                    tempDefaultValue = entry.getKey();
                    break;
                }
            }
        }
        defaultValue = tempDefaultValue;
        note = ad.note();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put(PATH_KEY, path);
        serialized.put(CLASS_NAME_KEY, className);
        serialized.put(IS_LIST_KEY, isList);
        serialized.put(REQUIRED_KEY, required);
        serialized.put(DEFAULT_VALUE_KEY, defaultValue);
        serialized.put(NOTE_KEY, note);
        return serialized;
    }


    @SuppressWarnings("unused")
    @NotNull
    public static AttributeData deserialize(@NotNull Map<String, ?> args) {
        String path = (String) args.get(PATH_KEY);
        String className = (String) args.get(CLASS_NAME_KEY);
        Boolean isList = (Boolean) args.get(IS_LIST_KEY);
        Boolean required = (Boolean) args.get(REQUIRED_KEY);
        String defaultValue = (String) args.get(DEFAULT_VALUE_KEY);
        String note = (String) args.get(NOTE_KEY);
        return new AttributeData(path, className, isList, required, defaultValue, note);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttributeData)) {
            return false;
        }

        AttributeData data = (AttributeData) o;

        if (isList != data.isList) {
            return false;
        }
        if (required != data.required) {
            return false;
        }
        if (!path.equals(data.path)) {
            return false;
        }
        if (!className.equals(data.className)) {
            return false;
        }
        if (!Objects.equals(defaultValue, data.defaultValue)) {
            return false;
        }
        return note.equals(data.note);
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + className.hashCode();
        result = 31 * result + defaultValue.hashCode();
        result = 31 * result + note.hashCode();
        result = 31 * result + (isList ? 1 : 0);
        result = 31 * result + (required ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AttributeData{");
        sb.append("path='").append(path).append('\'');
        sb.append(", className='").append(className).append('\'');
        sb.append(", defaultValue='").append(defaultValue).append('\'');
        sb.append(", note='").append(note).append('\'');
        sb.append(", isList=").append(isList);
        sb.append(", required=").append(required);
        sb.append('}');
        return sb.toString();
    }
}
