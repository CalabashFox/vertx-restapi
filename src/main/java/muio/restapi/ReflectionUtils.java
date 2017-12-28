package muio.restapi;


import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

public class ReflectionUtils {

	private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

	public static Set<Class> getClassInPackage(String pkgName) {
		Set<Class> classes = new HashSet<>();
		URL root = Thread.currentThread().getContextClassLoader().getResource(pkgName.replace(".", "/"));
		if (root != null) {
			readFiles(classes, root.getFile(), pkgName);
		}
		return classes;
	}

	private static void readFiles(Set<Class> classes, String url, String pkgName) {
		try {
			File[] files = new File(URLDecoder.decode(url, "UTF-8")).listFiles((dir, name) -> name.endsWith(".class"));
			if (files != null) {
				for (File file : files) {
					String className = file.getName().replaceAll(".class$", "");
					classes.add(Class.forName(pkgName + "." + className));
				}
			}
			File[] dirs = new File(URLDecoder.decode(url, "UTF-8")).listFiles(File::isDirectory);
			if (dirs != null) {
				for (File dir : dirs) {
					readFiles(classes, dir.getAbsolutePath(), pkgName + "." + dir.getName());
				}
			}
		} catch (UnsupportedEncodingException e) {
			log.error("Exception occurred in ReflectionUtil.getClassInPackage: " + e);
		} catch (ClassNotFoundException e) {
			log.error("ClassNotFound in ReflectionUtil.getClassInPackage: " + e);
		}
	}

	public static <T> MethodHandle getMethodHandleReference(Class<T> type, Method method) {
		Parameter[] parameters = method.getParameters();
		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		try {
			return lookup.findVirtual(type, method.getName(), MethodType.methodType(method.getReturnType(), parameterTypes));
		} catch (NoSuchMethodException | IllegalAccessException e) {
			log.warn("Could not parse method correctly for type {0} method {1}. Error: {2} ",
					type, method, e);
			return null;
		}
	}

}
