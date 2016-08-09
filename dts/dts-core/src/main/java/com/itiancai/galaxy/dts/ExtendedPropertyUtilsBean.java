package com.itiancai.galaxy.dts;

import org.apache.commons.beanutils.*;
import org.apache.commons.beanutils.expression.DefaultResolver;
import org.apache.commons.beanutils.expression.Resolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import scala.collection.Seq;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ExtendedPropertyUtilsBean extends PropertyUtilsBean {

    private final Log log = LogFactory.getLog(ExtendedPropertyUtilsBean.class);
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private Resolver resolver = new DefaultResolver();

    @Override
    public Object getMappedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Identify the key of the requested individual property
        String key  = null;
        try {
            key = resolver.getKey(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException
                    ("Invalid mapped property '" + name +
                            "' on bean class '" + bean.getClass() + "' " + e.getMessage());
        }
        if (key == null) {
            throw new IllegalArgumentException("Invalid mapped property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        }

        // Isolate the name
        name = resolver.getProperty(name);

        // Request the specified indexed property value
        return (getMappedProperty(bean, name, key));

    }

    @Override
    public Object getMappedProperty(Object bean,
                                    String name, String key)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }
        if (key == null) {
            throw new IllegalArgumentException("No key specified for property '" +
                    name + "' on bean class " + bean.getClass() + "'");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                        name + "'+ on bean class '" + bean.getClass() + "'");
            }
            return (((DynaBean) bean).get(name, key));
        }

        Object result = null;

        // Retrieve the property descriptor for the specified property
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "'+ on bean class '" + bean.getClass() + "'");
        }

        if (descriptor instanceof MappedPropertyDescriptor) {
            // Call the keyed getter method if there is one
            Method readMethod = ((MappedPropertyDescriptor) descriptor).
                    getMappedReadMethod();
            readMethod = MethodUtils.getAccessibleMethod(bean.getClass(), readMethod);
            if (readMethod != null) {
                Object[] keyArray = new Object[1];
                keyArray[0] = key;
                result = invokeMethod(readMethod, bean, keyArray);
            } else {
                throw new NoSuchMethodException("Property '" + name +
                        "' has no mapped getter method on bean class '" +
                        bean.getClass() + "'");
            }
        } else {
          /* means that the result has to be retrieved from a map */
            Method readMethod = getReadMethod(bean.getClass(), descriptor);
            if (readMethod != null) {
                Object invokeResult = invokeMethod(readMethod, bean, EMPTY_OBJECT_ARRAY);
            /* test and fetch from the map */
                if (invokeResult instanceof java.util.Map) {
                    result = ((java.util.Map<?, ?>)invokeResult).get(key);
                }
                if(invokeResult instanceof scala.collection.Map){
                    result = ((scala.collection.Map<String,?>)invokeResult).get(key).get();
                }
            } else {
                throw new NoSuchMethodException("Property '" + name +
                        "' has no mapped getter method on bean class '" +
                        bean.getClass() + "'");
            }
        }
        return result;
    }

    Method getReadMethod(Class<?> clazz, PropertyDescriptor descriptor) {
        return (MethodUtils.getAccessibleMethod(clazz, descriptor.getReadMethod()));
    }

    private Object invokeMethod(
            Method method,
            Object bean,
            Object[] values)
            throws
            IllegalAccessException,
            InvocationTargetException {
        if(bean == null) {
            throw new IllegalArgumentException("No bean specified " +
                    "- this should have been checked before reaching this method");
        }

        try {

            return method.invoke(bean, values);

        } catch (NullPointerException cause) {
            // JDK 1.3 and JDK 1.4 throw NullPointerException if an argument is
            // null for a primitive value (JDK 1.5+ throw IllegalArgumentException)
            String valueString = "";
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    if (i>0) {
                        valueString += ", " ;
                    }
                    if (values[i] == null) {
                        valueString += "<null>";
                    } else {
                        valueString += (values[i]).getClass().getName();
                    }
                }
            }
            String expectedString = "";
            Class<?>[] parTypes = method.getParameterTypes();
            if (parTypes != null) {
                for (int i = 0; i < parTypes.length; i++) {
                    if (i > 0) {
                        expectedString += ", ";
                    }
                    expectedString += parTypes[i].getName();
                }
            }
            IllegalArgumentException e = new IllegalArgumentException(
                    "Cannot invoke " + method.getDeclaringClass().getName() + "."
                            + method.getName() + " on bean class '" + bean.getClass() +
                            "' - " + cause.getMessage()
                            // as per https://issues.apache.org/jira/browse/BEANUTILS-224
                            + " - had objects of type \"" + valueString
                            + "\" but expected signature \""
                            +   expectedString + "\""
            );
            if (!BeanUtils.initCause(e, cause)) {
                log.error("Method invocation failed", cause);
            }
            throw e;
        } catch (IllegalArgumentException cause) {
            String valueString = "";
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    if (i>0) {
                        valueString += ", " ;
                    }
                    if (values[i] == null) {
                        valueString += "<null>";
                    } else {
                        valueString += (values[i]).getClass().getName();
                    }
                }
            }
            String expectedString = "";
            Class<?>[] parTypes = method.getParameterTypes();
            if (parTypes != null) {
                for (int i = 0; i < parTypes.length; i++) {
                    if (i > 0) {
                        expectedString += ", ";
                    }
                    expectedString += parTypes[i].getName();
                }
            }
            IllegalArgumentException e = new IllegalArgumentException(
                    "Cannot invoke " + method.getDeclaringClass().getName() + "."
                            + method.getName() + " on bean class '" + bean.getClass() +
                            "' - " + cause.getMessage()
                            // as per https://issues.apache.org/jira/browse/BEANUTILS-224
                            + " - had objects of type \"" + valueString
                            + "\" but expected signature \""
                            +   expectedString + "\""
            );
            if (!BeanUtils.initCause(e, cause)) {
                log.error("Method invocation failed", cause);
            }
            throw e;

        }
    }

    public Object getNestedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Resolve nested references
        while (resolver.hasNested(name)) {
            String next = resolver.next(name);
            Object nestedBean = null;
            if (bean instanceof Map) {
                nestedBean = getPropertyOfMapBean((Map<?, ?>) bean, next);
            } else if (resolver.isMapped(next)) {
                nestedBean = getMappedProperty(bean, next);
            } else if (resolver.isIndexed(next)) {
                nestedBean = getIndexedProperty(bean, next);
            } else {
                nestedBean = getSimpleProperty(bean, next);
            }
            if (nestedBean == null) {
                throw new NestedNullException
                        ("Null property value for '" + name +
                                "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = resolver.remove(name);
        }

        if (bean instanceof Map) {
            bean = getPropertyOfMapBean((Map<?, ?>) bean, name);
        } else if (resolver.isMapped(name)) {
            bean = getMappedProperty(bean, name);
        } else if (resolver.isIndexed(name)) {
            bean = getIndexedProperty(bean, name);
        } else {
            bean = getSimpleProperty(bean, name);
        }
        return bean;
    }
    /**
     * Return the value of the specified indexed property of the specified
     * bean, with no type conversions.  The zero-relative index of the
     * required value must be included (in square brackets) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.  In addition to supporting the JavaBeans specification, this
     * method has been extended to support <code>List</code> objects as well.
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname[index]</code> of the property value
     *  to be extracted
     * @return the indexed property value
     *
     * @exception IndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying array or List
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public Object getIndexedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Identify the index of the requested individual property
        int index = -1;
        try {
            index = resolver.getIndex(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "' on bean class '" + bean.getClass() + "' " +
                    e.getMessage());
        }
        if (index < 0) {
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        }

        // Isolate the name
        name = resolver.getProperty(name);

        // Request the specified indexed property value
        return (getIndexedProperty(bean, name, index));

    }

    /**
     * Return the value of the specified indexed property of the specified
     * bean, with no type conversions.  In addition to supporting the JavaBeans
     * specification, this method has been extended to support
     * <code>List</code> objects as well.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param index Index of the property value to be extracted
     * @return the indexed property value
     *
     * @exception IndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying property
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public Object getIndexedProperty(Object bean,
                                     String name, int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null || name.length() == 0) {
            if (bean.getClass().isArray()) {
                return Array.get(bean, index);
            } else if (bean instanceof List) {
                return ((List<?>)bean).get(index);
            }
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                        name + "' on bean class '" + bean.getClass() + "'");
            }
            return (((DynaBean) bean).get(name, index));
        }

        // Retrieve the property descriptor for the specified property
        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        }

        // Call the indexed getter method if there is one
        if (descriptor instanceof IndexedPropertyDescriptor) {
            Method readMethod = ((IndexedPropertyDescriptor) descriptor).
                    getIndexedReadMethod();
            readMethod = MethodUtils.getAccessibleMethod(bean.getClass(), readMethod);
            if (readMethod != null) {
                Object[] subscript = new Object[1];
                subscript[0] = new Integer(index);
                try {
                    return (invokeMethod(readMethod,bean, subscript));
                } catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof
                            IndexOutOfBoundsException) {
                        throw (IndexOutOfBoundsException)
                                e.getTargetException();
                    } else {
                        throw e;
                    }
                }
            }
        }

        // Otherwise, the underlying property must be an array
        Method readMethod = getReadMethod(bean.getClass(), descriptor);
        if (readMethod == null) {
            throw new NoSuchMethodException("Property '" + name + "' has no " +
                    "getter method on bean class '" + bean.getClass() + "'");
        }

        // Call the property getter and return the value
        Object value = invokeMethod(readMethod, bean, EMPTY_OBJECT_ARRAY);
        if (!value.getClass().isArray() && !(value instanceof Seq)) {
            if (!(value instanceof java.util.List)) {
                throw new IllegalArgumentException("Property '" + name +
                        "' is not indexed on bean class '" + bean.getClass() + "'");
            } else {
                //get the List's value
                return ((java.util.List<?>) value).get(index);
            }
        }else if(value.getClass().isArray() && !(value instanceof Seq)) {
            //get the array's value
            try {
                return (Array.get(value, index));
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ArrayIndexOutOfBoundsException("Index: " +
                        index + ", Size: " + Array.getLength(value) +
                        " for property '" + name + "'");
            }
        }else{
           return scala.collection.JavaConversions.seqAsJavaList(((Seq)value)).get(index);
        }

    }
}
