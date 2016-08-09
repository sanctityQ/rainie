package com.itiancai.galaxy.dts;

import org.apache.commons.beanutils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;

public class ExtendedBeanUtilsBean extends BeanUtilsBean {


    /**
     * Contains <code>BeanUtilsBean</code> instances indexed by context classloader.
     */
    private static final ContextClassLoaderLocal<ExtendedBeanUtilsBean>
            BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal<ExtendedBeanUtilsBean>() {
        // Creates the default instance used when the context classloader is unavailable
        @Override
        protected ExtendedBeanUtilsBean initialValue() {
            return new ExtendedBeanUtilsBean();
        }
    };

    /**
     * Gets the instance which provides the functionality for {@link BeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container.
     *
     * @return The (pseudo-singleton) BeanUtils bean instance
     */
    public static ExtendedBeanUtilsBean getInstance() {
        return BEANS_BY_CLASSLOADER.get();
    }

    /**
     * Sets the instance which provides the functionality for {@link BeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container.
     *
     * @param newInstance The (pseudo-singleton) BeanUtils bean instance
     */
    public static void setInstance(ExtendedBeanUtilsBean newInstance) {
        BEANS_BY_CLASSLOADER.set(newInstance);
    }

    // --------------------------------------------------------- Attributes

    /**
     * Logging for this instance
     */
    private final Log log = LogFactory.getLog(BeanUtils.class);

    /** Used to perform conversions between object types when setting properties */
    private final ConvertUtilsBean convertUtilsBean;

    /** Used to access properties*/
    private final ExtendedPropertyUtilsBean propertyUtilsBean;

    /** A reference to Throwable's initCause method, or null if it's not there in this JVM */

    // --------------------------------------------------------- Constuctors

    /**
     * <p>Constructs an instance using new property
     * and conversion instances.</p>
     */
    public ExtendedBeanUtilsBean() {
        this(new ConvertUtilsBean(), new ExtendedPropertyUtilsBean());
    }

    /**
     * <p>Constructs an instance using given conversion instances
     * and new {@link PropertyUtilsBean} instance.</p>
     *
     * @param convertUtilsBean use this <code>ConvertUtilsBean</code>
     * to perform conversions from one object to another
     *
     * @since 1.8.0
     */
    public ExtendedBeanUtilsBean(ConvertUtilsBean convertUtilsBean) {
        this(convertUtilsBean, new ExtendedPropertyUtilsBean());
    }

    /**
     * <p>Constructs an instance using given property and conversion instances.</p>
     *
     * @param convertUtilsBean use this <code>ConvertUtilsBean</code>
     * to perform conversions from one object to another
     * @param propertyUtilsBean use this <code>PropertyUtilsBean</code>
     * to access properties
     */
    public ExtendedBeanUtilsBean(
            ConvertUtilsBean convertUtilsBean,
            ExtendedPropertyUtilsBean propertyUtilsBean) {

        this.convertUtilsBean = convertUtilsBean;
        this.propertyUtilsBean = propertyUtilsBean;
    }

    /**
     * Return the value of the (possibly nested) property of the specified
     * name, for the specified bean, as a String.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     * @return The nested property's value, converted to a String
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public String getNestedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getNestedProperty(bean, name);
        return (getConvertUtils().convert(value));

    }


    /**
     * Return the value of the specified property of the specified bean,
     * no matter which property reference format is used, as a String.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *  to be extracted
     * @return The property's value, converted to a String
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public String getProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return (getNestedProperty(bean, name));

    }

    /**
     * Gets the <code>ConvertUtilsBean</code> instance used to perform the conversions.
     *
     * @return The ConvertUtils bean instance
     */
    public ConvertUtilsBean getConvertUtils() {
        return convertUtilsBean;
    }

    /**
     * Gets the <code>PropertyUtilsBean</code> instance used to access properties.
     *
     * @return The ConvertUtils bean instance
     */
    public ExtendedPropertyUtilsBean getPropertyUtils() {
        return propertyUtilsBean;
    }
}
