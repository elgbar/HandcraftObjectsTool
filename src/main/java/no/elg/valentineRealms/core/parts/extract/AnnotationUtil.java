package no.elg.valentineRealms.core.parts.extract;

import no.uib.inf219.api.serialization.AttributeDoc;
import no.uib.inf219.api.serialization.EnumValueDesc;
import no.uib.inf219.api.serialization.NoDefaultValue;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Elg
 */
public class AnnotationUtil {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationUtil.class);

    /**
     * This method will also check if the default from {@link EnumValueDesc#isDefault()} with {@link NoDefaultValue} is
     * correct
     *
     * @param enumClazz The enum class to get the fields of
     * @return A map of the name and the field of each constant
     */
    @NotNull
    public static Map<String, Field> getEnumFields(@NotNull Class<? extends Enum<?>> enumClazz) {
        Enum<?>[] enumConstants = enumClazz.getEnumConstants();
        Map<String, Field> fields = new HashMap<>(enumConstants.length);
        int defaults = 0;
        int descs = 0;
        for (Enum<?> constant : enumConstants) {
            try {
                Field field = enumClazz.getField(constant.name());
                fields.put(constant.name(), field);

                EnumValueDesc enumValAnn = field.getAnnotation(EnumValueDesc.class);
                if (enumValAnn != null) {
                    descs++;
                    if (enumValAnn.isDefault()) {
                        defaults++;
                    }
                }

            } catch (NoSuchFieldException e) {
                //Should never happen
                e.printStackTrace();
            }
        }

        //warn when too many/few default values
        boolean noDefault = enumClazz.getAnnotation(NoDefaultValue.class) != null;
        if (descs > 0 && defaults == 0 && !noDefault) {
            logger.warn("No value for enum {} is marked as default", enumClazz.getSimpleName());
        }
        if (defaults > 1) {
            logger.error("Multiple enum values for {} are marked as default. There can only be one default value!",
                    enumClazz.getSimpleName());
        }
        if (noDefault && defaults > 0) {
            logger.warn("Enum {} has @NoDefaultValue but some default values are found non the less",
                    enumClazz.getSimpleName());
        }
        return fields;
    }

    @NotNull
    public static <T extends Annotation> List<T> getAllSuperAnnotations(@NotNull Class<?> baseClass,
                                                                        @NotNull Class<T> annClass) {
        return getAllSuperAnnotations(baseClass, annClass, new ArrayList<>());
    }

    /**
     * For internal use only
     */
    @NotNull
    private static <T extends Annotation> List<T> getAllSuperAnnotations(@Nullable Class<?> baseClass,
                                                                         @NotNull Class<T> annClass, List<T> anns) {
        if (baseClass == null) {
            return anns; //recursion done
        }

        T an = baseClass.getAnnotation(annClass);
        if (an != null) {
            anns.add(an);
        }

        getAllSuperAnnotations(baseClass.getSuperclass(), annClass, anns);
        return anns;
    }

    @Contract(pure = true)
    @NotNull
    public static Set<Field> getAllAnnotatedFields(@NotNull Class<?> baseClass,
                                                   Class<? extends Annotation> annotationClass) {
        //all classes whos doced fields will be shown
        Set<Class<?>> loadingClasses = new HashSet<>();
        loadingClasses.add(baseClass);
//        if (ConfigurationSectionLoadable.class.isAssignableFrom(baseClass)) {
//            for (Field field : FieldUtils.getFieldsListWithAnnotation(baseClass, UsedInLoading.class)) {
//                if (!ConfigurationSectionLoadable.class.isAssignableFrom(field.getType())) {
//                    logger.error("The field '{}' in class '{}' with @UsedInLoading annotation does not implement " +
//                                 "ConfigurationSectionLoadable", field.getName(), baseClass.getSimpleName());
//                    continue;
//                }
//                if (logger.isDebugEnabled()) {
//                    logger.debug(
//                        "The field '{}' in class '{}' has @UsedInLoading annotation, adding its declaring class " +
//                        "'{}' " + "to the loading classes", field.getName(), baseClass.getSimpleName(),
//                        field.getType().getSimpleName());
//                }
//                loadingClasses.add(field.getType());
//            }
//        }

        //find all the docs in all the given classes
        Set<Field> fields = new HashSet<>();
        for (Class<?> loadingClazz : loadingClasses) {
            List<Field> list = FieldUtils.getFieldsListWithAnnotation(loadingClazz, annotationClass);
            if (logger.isDebugEnabled()) {
                //convert the list of fields into their names
                String fieldNames = Arrays.toString(list.stream().map(Field::getName).toArray());
                logger.debug("{}: Found the fields {} from class '{}' ", baseClass.getSimpleName(), fieldNames,
                        loadingClazz.getSimpleName());
            }
            fields.addAll(list);
        }
        return fields;
    }

    /**
     * @param context The class to look for {@link AttributeDoc} on fields
     * @return All expected keys to be found in the given context
     */
    @Contract(pure = true)
    @NotNull
    public static <T> Set<String> getExpectedKeys(@NotNull Class<T> context) {
        return getExpectedKeys(context, false, true);
    }

    /**
     * @param context    The class to look for {@link AttributeDoc} on fields
     * @param isListElem If the context is a part of a list
     * @return All expected keys to be found in the given context
     */
    @Contract(pure = true)
    @NotNull
    public static <T> Set<String> getExpectedKeys(@NotNull Class<T> context, boolean isListElem,
                                                  boolean allowedToUseLoader) {
//        no.kh498.valentineRealms.core.parts.api.Logger.logger.debug(
//                "Finding expected keys from context {}. Flags: isListElem? {} | allowedToUseLoader? {}",
//                context.getSimpleName(), isListElem, allowedToUseLoader);
//        Loader<T> loader = ConfigLoader.getLoader(context);
//        if (allowedToUseLoader && loader != null) {
//            no.kh498.valentineRealms.core.parts.api.Logger.logger.trace(
//                    "Given context is registered to be using a loader");
//
//            LoaderDoc[] docs = loader.getClass().getAnnotationsByType(LoaderDoc.class);
//            if (docs.length == 0) {
//                no.kh498.valentineRealms.core.parts.api.Logger.logger.warn(
//                        "Failed to find any loader documentation for the loader {}", loader.getClass().getSimpleName());
//                return Collections.emptySet();
//            }
//            Set<String> keys = new HashSet<>();
//            for (LoaderDoc doc : docs) {
//                String path = doc.path();
//                if (path.isEmpty()) {
//                    Class<?> clazz = doc.pathClass() != Object.class ? doc.pathClass() : doc.clazz();
//                    path = ConfigLoader.classPath(clazz);
//                }
//                keys.add(path);
//            }
//            return keys;
//        }
//        no.kh498.valentineRealms.core.parts.api.Logger.logger.trace("Will not get keys from a loader. loader exist? {}",
//                loader != null);
//        Set<Field> keys = getAllAnnotatedFields(context, AttributeDoc.class);
//        Set<String> keySet = keys.stream().map(AnnotationUtil::getAttributePath).collect(Collectors.toSet());
//        if (isListElem) {
//            keySet.add(Core.CLASS_PATH);
//        }
//        return keySet;
        return new HashSet<>();
    }

    /**
     * @param field The field to get {@link AttributeDoc} from
     * @return The expected path to this attribute
     * @throws IllegalArgumentException If the given field does not have the annotation {@link AttributeDoc}
     */
    @NotNull
    public static String getAttributePath(@NotNull Field field) {
        AttributeDoc ad = field.getAnnotation(AttributeDoc.class);
        if (ad == null) {
            throw new IllegalArgumentException(
                    "Given field does not have " + AttributeDoc.class.getSimpleName() + " as an annotation");
        }
        String path = ad.path();
        if (path.isEmpty()) {
//            path = ConfigLoader.classPath(field.getType());
        }
        return path;
    }

    @NotNull
    public static Field findField(@NotNull Class<?> parentContext, @NotNull String path) {
        Set<Field> fields = getAllAnnotatedFields(parentContext, AttributeDoc.class);
        for (Field field : fields) {
            if (path.equals(getAttributePath(field))) {
                return field;
            }
        }
        throw new IllegalArgumentException(String.format("Failed to find a field with the given path %s in context %s.",
                path, parentContext.getSimpleName()));
    }
}
