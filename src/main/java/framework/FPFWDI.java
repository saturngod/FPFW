package framework;

import framework.annotations.AutoWire;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

class FPFWDI {

    List<Object> serviceObjectList;
    FPFWContext context;

    FPFWDI(FPFWContext context, List<Object> serviceObjectList) {
        this.context = context;
        this.serviceObjectList = serviceObjectList;
    }
    protected void performDependencyInjection() {
        try {
            for (Object serviceInstance : serviceObjectList) {
                injectSetter(serviceInstance);
                context.injectFields(serviceInstance);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void injectSetter(Object serviceObject) {
        for (Method method : serviceObject.getClass().getDeclaredMethods()) {
            try {
                if (method.isAnnotationPresent(AutoWire.class)) {
                    setterInjection(serviceObject, method);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setterInjection(Object serviceObject, Method method) throws InvocationTargetException, IllegalAccessException {
        Class<?>[] parameterTypes = method.getParameterTypes();

        if(parameterTypes.length > 1) {
            return;
        }

        Class setterClass = parameterTypes[0];
        Object field = context.getServiceBeanOfType(setterClass);

        method.invoke(serviceObject,field);
    }
}
