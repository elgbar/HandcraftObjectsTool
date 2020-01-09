package no.elg.valentineRealms.core.parts.extract;

import no.elg.valentineRealms.core.parts.extract.data.AttributeData;
import no.elg.valentineRealms.core.parts.extract.data.PartData;
import no.uib.inf219.api.serialization.AttributeDoc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Elg
 */
public class PartsExtractor {

    public static Logger logger = LoggerFactory.getLogger(PartsExtractor.class);


//    @NotNull
//    public static <T> FileConfiguration extractIIM(@NotNull InterfaceImplManager<T> manager) {
//        if (!manager.getImplementations().isEmpty()) {
//            return extractAll(manager.getImplementations().values().toArray(new Class<?>[0]));
//        }
//        return new YamlConfiguration();
//    }
//
//    @NotNull
//    public static FileConfiguration extractAll(@NotNull Class<?>... classes) {
//        YamlConfiguration root = new YamlConfiguration();
//        root.options().pathSeparator(':'); //prevent the dots in the class path to create new sections
//        for (Class<?> clazz : classes) {
//            root.set(clazz.getCanonicalName(), extractExpected(clazz));
//        }
//        return root;
//    }

    @NotNull
    public static <T> PartData extractExpected(@NotNull Class<T> context) {
        Set<Field> keys = AnnotationUtil.getAllAnnotatedFields(context, AttributeDoc.class);
        List<AttributeData> data = keys.stream().map(AttributeData::new).collect(Collectors.toList());
        return new PartData(context, data);
    }

    @Nullable
    public static Class<?> getFirstGenericType(@NotNull Class<?> clazz) {
        return getGenericFromType(clazz.getGenericInterfaces()[0]);
    }

    /**
     * @param field The field to get the generic type of
     * @return The generic class of the given field. {@code null} if it does not have a type or the type contains a
     * wild card (ie {@code <? extend Something>})
     */
    @Nullable
    public static Class<?> getGenericFieldType(@NotNull Field field) {
        return getGenericFromType(field.getGenericType());
    }

    @Nullable
    private static Class<?> getGenericFromType(@Nullable Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType fieldType = (ParameterizedType) type;
            Type[] a = fieldType.getActualTypeArguments();
            if (a.length == 0) {
                logger.trace("ActualTypeArguments has size 0");
                return null;
            }
            if (a[0] instanceof Class) {
                return (Class<?>) a[0];
            } else {
                logger.trace("First ActualTypeArguments not class");
                return null;
            }
        }
        logger.trace("Generic type is not ParameterizedType");
        return null;
    }
}
