package framework;

import framework.annotations.*;
import framework.annotations.Runnable;
import framework.classes.FPFWEventPublisher;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class FPFWContext {

    private List<Object> serviceObjectList = new ArrayList<>();

    private Properties properties;
    private FPFWEventPublisher publisher;

    protected void injectFields(Object instance) throws IllegalAccessException {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(AutoWire.class)) {
                if(field.isAnnotationPresent(Qualifier.class)) {
                    injectFieldWithQualifier(instance, field);
                }
                else {
                    injectFieldWithoutQualifier(instance, field);
                }
            }
            else if(field.isAnnotationPresent(Value.class)) {
                injectValueField(instance,field);
            }
        }
    }

    private void injectValueField(Object instance, Field field) throws IllegalAccessException {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation.annotationType().getName().equals(Value.class.getName())) {
                Value injectValue = (Value) annotation;
                String value = injectValue.value();
                if(isValueReadFromProperties(injectValue.value())) {
                    value = readFromProperties(injectValue.value());
                }

                setField(field,instance,value);

            }
        }
    }

    private boolean isValueReadFromProperties(String value) {
        return (value.startsWith("${") && value.endsWith("}"));
    }

    private String readFromProperties(String value) {
        if(!isValueReadFromProperties(value)) {
            return "";
        }


        String propertyName = value.substring(2, value.length() - 1);
        String propertyValue = properties.getProperty(propertyName);

        return propertyValue;
    }

    private void injectFieldWithQualifier(Object instance, Field field) throws IllegalAccessException {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations){

            if(annotation.annotationType().getName().equals(Qualifier.class.getName())) {
                Qualifier q = (Qualifier)annotation;

                injectField(instance, field,q.value());
            }
        }
    }


    private void injectField(Object instance, Field field, String name) throws IllegalAccessException {
        Class<?> fieldType = field.getType();
        Object fieldInstance = getServiceBeanOfType(fieldType,name);
        setField(field, instance, fieldInstance);
    }

    private void injectFieldWithoutQualifier(Object instance, Field field) throws IllegalAccessException {
        injectField(instance,field,"");
    }

    private void setField(Field field,Object instance,Object fieldInstance) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(instance, fieldInstance);
    }


    private Object getServiceFromContainer(Class fieldClass,String name) {
        Object service = null;
        for (Object serviceInstance : serviceObjectList) {
            if (matchesType(serviceInstance, fieldClass)) {
                if(name.isEmpty()) {
                    service = serviceInstance;
                    break;
                }
                else if(serviceInstance.getClass().getSimpleName().toLowerCase().equals(name.toLowerCase())) {
                    service = serviceInstance;
                    break;
                }

            }
        }
        return service;
    }

    protected Object getServiceBeanOfType(Class fieldClass) {
        return getServiceBeanOfType(fieldClass,"");
    }
    private Object getServiceBeanOfType(Class fieldClass,String name) {
        Object service = null;
        try {
            service = getServiceFromContainer(fieldClass, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return service;
    }

    private boolean matchesType(Object instance, Class fieldClass) {
        Class<?> instanceClass = instance.getClass();
        if (instanceClass.getName().equals(fieldClass.getName())) {
            return true;
        }

        for (Class<?> implementedInterface : instanceClass.getInterfaces()) {
            if (implementedInterface.getName().equals(fieldClass.getName())) {
                return true;
            }
        }
        return false;
    }

    void loadProperties(Class<?> clazz) {

        properties = new Properties();
        try (InputStream inputStream = clazz.getClassLoader().getResourceAsStream("application.properties")) {
            if(inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void run(Class<?> clazz) {
        FPFWContext fWContext = new FPFWContext();
        fWContext.start(clazz);
    }

    public void start(Class<?> clazz) {
        try {
            setupPublisher();

            loadProperties(clazz);
            Reflections reflections = new Reflections(clazz.getPackageName());

            FPFWScanner scanner = new FPFWScanner(this,serviceObjectList,properties);
            scanner.scanAndInstantiateServiceClasses(reflections);

            FPFWDI di = new FPFWDI(this,serviceObjectList);
            di.performDependencyInjection();


            publisher.setServiceObjectList(serviceObjectList);

            FPFWMethod fpfwMethod = new FPFWMethod(serviceObjectList);
            fpfwMethod.scanSchedule();



            performRunable(reflections);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupPublisher() {
        publisher = new FPFWEventPublisher();
        serviceObjectList.add(publisher);
    }

    void performRunable(Reflections reflections) throws InstantiationException, IllegalAccessException {
        Set<Class<?>> apps = reflections.getTypesAnnotatedWith(FPFWApplication.class);
        if(apps.size() > 1 || apps.size() == 0) {
            System.out.println("FPFWApplication must declare only one with Runnable");
            return;
        }
        Class<?> appClass =apps.iterator().next();
        Runnable runnable = (Runnable) appClass.newInstance();

        injectFields(runnable);


        runnable.run();
    }




}
