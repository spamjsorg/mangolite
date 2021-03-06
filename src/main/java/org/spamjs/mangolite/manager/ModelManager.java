package org.spamjs.mangolite.manager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.reflections.Reflections;
import org.spamjs.mangolite.abstracts.AbstractUser;
import org.spamjs.mangolite.annotations.RxModel;
import org.spamjs.mangolite.annotations.RxModel.ModelType;
import org.spamjs.utils.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelManager.
 */
public class ModelManager {
	
	/** The Constant LOG. */
	private static final Log LOG = new Log();

	/** The user constructor. */
	private static Constructor<?> userConstructor;
	static {
		try {
			userConstructor = AbstractUser.class.getConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			LOG.error(e);
		}
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	public AbstractUser getUser() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		return (AbstractUser) userConstructor.newInstance();
	}
	
	/**
	 * Scan.
	 *
	 * @param modelScanPath the model scan path
	 */
	public void scan(String modelScanPath) {
		Reflections modelReflections = new Reflections(modelScanPath);
		Set<Class<?>> modelAnnotated = modelReflections
				.getTypesAnnotatedWith(RxModel.class);
		for (Class<?> annotatedOne : modelAnnotated) {
			ModelType cName = annotatedOne.getAnnotation(RxModel.class).value();
			try {
				switch (cName) {
				case USER: {
					userConstructor = annotatedOne.getConstructor();
				}
					break;
				default:
					break;
				}
			} catch (NoSuchMethodException e) {
				LOG.error(e);
			} catch (SecurityException e) {
				LOG.error(e);
			} catch (IllegalArgumentException e) {
				LOG.error(e);
			}
		}
	}
}
