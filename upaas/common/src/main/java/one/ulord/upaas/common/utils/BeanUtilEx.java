package one.ulord.upaas.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Bind base Object type to null value instead 0 or exception
 * @author yinhaibo
 * @since 2018/4/24
 */
@Slf4j
public class BeanUtilEx extends BeanUtils {
    private BeanUtilEx() {
    }

    static {
        ConvertUtils.register(new org.apache.commons.beanutils.converters.SqlDateConverter(null), java.sql.Date.class);
        ConvertUtils.register(new org.apache.commons.beanutils.converters.SqlDateConverter(null), java.util.Date.class);
        ConvertUtils.register(new org.apache.commons.beanutils.converters.SqlTimestampConverter(null),
                java.sql.Timestamp.class);

        ConvertUtils.register(new org.apache.commons.beanutils.converters.IntegerConverter(null), Integer.class);
        ConvertUtils.register(new org.apache.commons.beanutils.converters.LongConverter(null), Long.class);
        ConvertUtils.register(new org.apache.commons.beanutils.converters.ByteConverter(null), Byte.class);
        ConvertUtils.register(new org.apache.commons.beanutils.converters.FloatConverter(null), Float.class);
        ConvertUtils.register(new org.apache.commons.beanutils.converters.DoubleConverter(null), Double.class);
        ConvertUtils.register(new org.apache.commons.beanutils.converters.CharacterConverter(null), Character.class);
        ConvertUtils.register(new org.apache.commons.beanutils.converters.ShortConverter(null), Short.class);
    }

    public static void copyProperties(Object target, Object source) throws InvocationTargetException,
            IllegalAccessException {
        // 支持对日期copy
        org.apache.commons.beanutils.BeanUtils.copyProperties(target, source);

    }

    public static void populate(Object bean, Map<String, ? extends Object> properties) throws IllegalAccessException, InvocationTargetException {
        org.apache.commons.beanutils.BeanUtils.populate(bean, properties);
    }
}
