package framework.classes;

import framework.annotations.Async;
import framework.annotations.EnableAsync;
import framework.annotations.EventListner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FPFWEventPublisher {

    private List<Object> serviceObjectList;


    public void setServiceObjectList(List<Object> serviceObjectList) {
        this.serviceObjectList = serviceObjectList;
    }

    public void publishEvent(Object object) throws InvocationTargetException, IllegalAccessException {
        for(Object serviceObject: serviceObjectList) {

            Method[] methods = serviceObject.getClass().getMethods();
            for (Method method : methods) {
                findAndInvokeMethod(object, serviceObject, method);
            }
        }
    }

    private static void findAndInvokeMethod(Object object, Object serviceObject, Method method) throws IllegalAccessException, InvocationTargetException {
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (parameterTypes.length != 1) {
            return;
        }

        Class contextClass = parameterTypes[0];


        if (contextClass.getName().equals(object.getClass().getName()) &&
                method.isAnnotationPresent(EventListner.class)) {
            if(method.isAnnotationPresent(Async.class)) {
                if(serviceObject.getClass().isAnnotationPresent(EnableAsync.class)) {
                    runAsAsync(object, serviceObject, method);
                }
            }
            else {
                method.invoke(serviceObject, object);
            }
        }

    }

    private static void runAsAsync(Object object, Object serviceObject, Method method) {
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(serviceObject, object);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
