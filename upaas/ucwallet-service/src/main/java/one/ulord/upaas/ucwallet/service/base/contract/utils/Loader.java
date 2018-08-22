/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.contract.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ReflectPermission;
import java.net.URL;

/**
 * @author haibo
 * @since 7/30/18
 */
public class Loader {
    private static final Logger logger = LoggerFactory.getLogger(Loader.class);

    private static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";

    static {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getStackTrace"));
            sm.checkPermission(new ReflectPermission("suppressAccessChecks"));
        }
    }

    /**
     * Returns the ClassLoader to use.
     * @return the ClassLoader.
     */
    public static ClassLoader getClassLoader() {
        return getClassLoader(Loader.class, null);
    }

    /**
     * Returns the ClassLoader of current thread if possible, or falls back to the system ClassLoader if none is
     * available.
     *
     * @return the TCCL.
     * @see org.apache.logging.log4j.util.LoaderUtil#getThreadContextClassLoader()
     */
    public static ClassLoader getThreadContextClassLoader() {
        return LoaderUtil.getThreadContextClassLoader();
    }

    // TODO: this method could use some explanation
    public static ClassLoader getClassLoader(final Class<?> class1, final Class<?> class2) {
        final ClassLoader threadContextClassLoader = getTcl();
        final ClassLoader loader1 = class1 == null ? null : class1.getClassLoader();
        final ClassLoader loader2 = class2 == null ? null : class2.getClassLoader();

        if (isChild(threadContextClassLoader, loader1)) {
            return isChild(threadContextClassLoader, loader2) ? threadContextClassLoader : loader2;
        }
        return isChild(loader1, loader2) ? loader1 : loader2;
    }

    /**
     * This method will search for {@code resource} in different
     * places. The search order is as follows:
     *
     * <ol>
     *
     * <li>Search for {@code resource} using the thread context
     * class loader under Java2. If that fails, search for
     * {@code resource} using the class loader that loaded this
     * class ({@code Loader}). Under JDK 1.1, only the the class
     * loader that loaded this class ({@code Loader}) is used.</li>
     * <li>Try one last time with
     * {@code ClassLoader.getSystemResource(resource)}, that is is
     * using the system class loader in JDK 1.2 and virtual machine's
     * built-in class loader in JDK 1.1.</li>
     * </ol>
     * @param resource The resource to load.
     * @param defaultLoader The default ClassLoader.
     * @return A URL to the resource.
     */
    public static URL getResource(final String resource, final ClassLoader defaultLoader) {
        try {
            ClassLoader classLoader = getTcl();
            if (classLoader != null) {
                logger.trace("Trying to find [{}] using context class loader {}.", resource, classLoader);
                final URL url = classLoader.getResource(resource);
                if (url != null) {
                    return url;
                }
            }

            // We could not find resource. Let us now try with the classloader that loaded this class.
            classLoader = Loader.class.getClassLoader();
            if (classLoader != null) {
                logger.trace("Trying to find [{}] using {} class loader.", resource, classLoader);
                final URL url = classLoader.getResource(resource);
                if (url != null) {
                    return url;
                }
            }
            // We could not find resource. Finally try with the default ClassLoader.
            if (defaultLoader != null) {
                logger.trace("Trying to find [{}] using {} class loader.", resource, defaultLoader);
                final URL url = defaultLoader.getResource(resource);
                if (url != null) {
                    return url;
                }
            }
        } catch (final Throwable t) {
            //
            //  can't be InterruptedException or InterruptedIOException
            //    since not declared, must be error or RuntimeError.
            logger.warn(TSTR, t);
        }

        // Last ditch attempt: get the resource from the class path. It
        // may be the case that clazz was loaded by the Extension class
        // loader which the parent of the system class loader. Hence the
        // code below.
        logger.trace("Trying to find [{}] using ClassLoader.getSystemResource().", resource);
        return ClassLoader.getSystemResource(resource);
    }

    public static URL getResource(final String resource){
        return getResource(resource, getThreadContextClassLoader());
    }

    /**
     * This method will search for {@code resource} in different
     * places. The search order is as follows:
     *
     * <ol>
     * <li>Search for {@code resource} using the thread context
     * class loader under Java2. If that fails, search for
     * {@code resource} using the class loader that loaded this
     * class ({@code Loader}). Under JDK 1.1, only the the class
     * loader that loaded this class ({@code Loader}) is used.</li>
     * <li>Try one last time with
     * {@code ClassLoader.getSystemResource(resource)}, that is is
     * using the system class loader in JDK 1.2 and virtual machine's
     * built-in class loader in JDK 1.1.</li>
     * </ol>
     * @param resource The resource to load.
     * @param defaultLoader The default ClassLoader.
     * @return An InputStream to read the resouce.
     */
    public static InputStream getResourceAsStream(final String resource, final ClassLoader defaultLoader) {
        try {
            ClassLoader classLoader = getTcl();
            InputStream is;
            if (classLoader != null) {
                logger.trace("Trying to find [{}] using context class loader {}.", resource, classLoader);
                is = classLoader.getResourceAsStream(resource);
                if (is != null) {
                    return is;
                }
            }

            // We could not find resource. Let us now try with the classloader that loaded this class.
            classLoader = Loader.class.getClassLoader();
            if (classLoader != null) {
                logger.trace("Trying to find [{}] using {} class loader.", resource, classLoader);
                is = classLoader.getResourceAsStream(resource);
                if (is != null) {
                    return is;
                }
            }

            // We could not find resource. Finally try with the default ClassLoader.
            if (defaultLoader != null) {
                logger.trace("Trying to find [{}] using {} class loader.", resource, defaultLoader);
                is = defaultLoader.getResourceAsStream(resource);
                if (is != null) {
                    return is;
                }
            }
        } catch (final Throwable t) {
            //
            //  can't be InterruptedException or InterruptedIOException
            //    since not declared, must be error or RuntimeError.
            logger.warn(TSTR, t);
        }

        // Last ditch attempt: get the resource from the class path. It
        // may be the case that clazz was loaded by the Extension class
        // loader which the parent of the system class loader. Hence the
        // code below.
        logger.trace("Trying to find [{}] using ClassLoader.getSystemResource().", resource);
        return ClassLoader.getSystemResourceAsStream(resource);
    }

    public static InputStream getResourceAsStream(final String resource) {
        return getResourceAsStream(resource, getThreadContextClassLoader());
    }

    private static ClassLoader getTcl() {
        return LoaderUtil.getThreadContextClassLoader();
    }

    /**
     * Determines if one ClassLoader is a child of another ClassLoader. Note that a {@code null} ClassLoader is
     * interpreted as the system ClassLoader as per convention.
     *
     * @param loader1 the ClassLoader to check for childhood.
     * @param loader2 the ClassLoader to check for parenthood.
     * @return {@code true} if the first ClassLoader is a strict descendant of the second ClassLoader.
     */
    private static boolean isChild(final ClassLoader loader1, final ClassLoader loader2) {
        if (loader1 != null && loader2 != null) {
            ClassLoader parent = loader1.getParent();
            while (parent != null && parent != loader2) {
                parent = parent.getParent();
            }
            // once parent is null, we're at the system CL, which would indicate they have separate ancestry
            return parent != null;
        }
        return loader1 != null;
    }

    /**
     * Load a Class by name. Note that unlike {@link ClassLoader#loadClass(String) ClassLoader.loadClass}, this method
     * will initialize the class as well if it hasn't been already. This is equivalent to the calling the
     * {@link ClassLoader#loadClass(String, boolean) protected version} with the second parameter equal to {@code true}.
     *
     * @param className The class name.
     * @return The Class.
     * @throws ClassNotFoundException if the Class could not be found.
     */
    public static Class<?> loadClass(final String className) throws ClassNotFoundException {
        return LoaderUtil.loadClass(className);
    }

    /**
     * Loads and initializes a named Class using a given ClassLoader.
     *
     * @param className The class name.
     * @param loader The class loader.
     * @return The class.
     * @throws ClassNotFoundException if the class could not be found.
     */
    public static Class<?> initializeClass(final String className, final ClassLoader loader)
            throws ClassNotFoundException {
        return Class.forName(className, true, loader);
    }

    /**
     * Load a Class in the {@code java.*} namespace by name. Useful for peculiar scenarios typically involving
     * Google App Engine.
     *
     * @param className The class name.
     * @return The Class.
     * @throws ClassNotFoundException if the Class could not be found.
     */
    public static Class<?> loadSystemClass(final String className) throws ClassNotFoundException {
        try {
            return Class.forName(className, true, ClassLoader.getSystemClassLoader());
        } catch (final Throwable t) {
            logger.trace("Couldn't use SystemClassLoader. Trying Class.forName({}).", className, t);
            return Class.forName(className);
        }
    }

    /**
     * Loads and instantiates a Class using the default constructor.
     *
     * @param className The class name.
     * @return new instance of the class.
     * @throws ClassNotFoundException if the class isn't available to the usual ClassLoaders
     * @throws IllegalAccessException if the class can't be instantiated through a public constructor
     * @throws InstantiationException if there was an exception whilst instantiating the class
     * @throws NoSuchMethodException if there isn't a no-args constructor on the class
     * @throws InvocationTargetException if there was an exception whilst constructing the class
     */
    public static Object newInstanceOf(final String className)
            throws ClassNotFoundException,
            IllegalAccessException,
            InstantiationException,
            NoSuchMethodException,
            InvocationTargetException {
        return LoaderUtil.newInstanceOf(className);
    }

    /**
     * Loads, instantiates, and casts a Class using the default constructor.
     *
     * @param className The class name.
     * @param clazz The class to cast it to.
     * @param <T> The type to cast it to.
     * @return new instance of the class cast to {@code T}
     * @throws ClassNotFoundException if the class isn't available to the usual ClassLoaders
     * @throws IllegalAccessException if the class can't be instantiated through a public constructor
     * @throws InstantiationException if there was an exception whilst instantiating the class
     * @throws NoSuchMethodException if there isn't a no-args constructor on the class
     * @throws InvocationTargetException if there was an exception whilst constructing the class
     * @throws ClassCastException if the constructed object isn't type compatible with {@code T}
     */
    public static <T> T newCheckedInstanceOf(final String className, final Class<T> clazz)
            throws ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        return LoaderUtil.newCheckedInstanceOf(className, clazz);
    }

    /**
     * Determines if a named Class can be loaded or not.
     *
     * @param className The class name.
     * @return {@code true} if the class could be found or {@code false} otherwise.
     */
    public static boolean isClassAvailable(final String className) {
        try {
            final Class<?> clazz = loadClass(className);
            return clazz != null;
        } catch (final ClassNotFoundException e) {
            return false;
        } catch (final Throwable e) {
            logger.trace("Unknown error checking for existence of class [{}].", className, e);
            return false;
        }
    }

    private Loader() {
    }
}
