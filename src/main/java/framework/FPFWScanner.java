package framework;

import framework.annotations.AutoWire;
import framework.annotations.Profile;
import framework.annotations.Service;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

class FPFWScanner {
    List<Object> serviceObjectList;
    FPFWContext context;
    Properties properties;

    FPFWScanner(FPFWContext context, List<Object> serviceObjectList, Properties properties) {
        this.context = context;
        this.serviceObjectList = serviceObjectList;
        this.properties = properties;
    }
    protected void scanAndInstantiateServiceClasses(Reflections reflections) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Set<Class<?>> serviceTypes = reflections.getTypesAnnotatedWith(Service.class);
        for (Class<?> serviceClass : serviceTypes) {

            if(shouldCreateNewInstance(serviceClass)) {
                if(shouldCreateForCurrentProfile(serviceClass)) {
                    serviceObjectList.add((Object) serviceClass.newInstance());
                }

            }
        }

        for (Class<?> serviceClass : serviceTypes) {
            performDependencyInjectionForConstructor(serviceClass);
        }
    }

    private boolean shouldCreateForCurrentProfile(Class<?> serviceClass) {

        String activeProfile = properties.getProperty("profiles.active");


        if(serviceClass.isAnnotationPresent(Profile.class)) {
            Annotation[] annotations = serviceClass.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().getName().equals(Profile.class.getName())) {
                    Profile p = (Profile)annotation;
                    if(activeProfile.equals(p.value())) {
                        System.out.println("CURRENT PROFILE " + p.value() + " CREATED ") ;
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean shouldCreateNewInstance(Class<?> serviceClass) {
        Constructor<?>[] constructors = serviceClass.getDeclaredConstructors();

        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(AutoWire.class)) {
                return false;
            }
        }
        return true;
    }

    private void performDependencyInjectionForConstructor(Class<?> serviceClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?>[] constructors = serviceClass.getDeclaredConstructors();

        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(AutoWire.class)) {
                injectDependenciesAndInstantiate(constructor);
            }
        }

    }

    private void injectDependenciesAndInstantiate(Constructor<?> constructor) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] arguments = new Object[parameterTypes.length];
        boolean injection = true;

        for (int i = 0; i < parameterTypes.length; i++) {
            Object bean = context.getServiceBeanOfType(parameterTypes[i]);

            if (bean != null) {
                arguments[i] = bean;
            } else {
                injection = false;
                break;
            }
        }

        if (injection) {
            serviceObjectList.add(constructor.newInstance(arguments));
        }
    }

}
