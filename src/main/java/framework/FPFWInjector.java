package framework;

import java.lang.reflect.Field;

class FPFWInjector {

    FPFWContext context;

    FPFWInjector(FPFWContext context) {
        this.context = context;
    }

    protected void injectField(Object instance, Field field, String name) throws IllegalAccessException {
        Class<?> fieldType = field.getType();
        Object fieldInstance = this.context.getServiceBeanOfType(fieldType,name);
        setField(field, instance, fieldInstance);
    }

    protected void injectFieldWithoutQualifier(Object instance, Field field) throws IllegalAccessException {
        injectField(instance,field,"");
    }

    protected void setField(Field field,Object instance,Object fieldInstance) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(instance, fieldInstance);
    }
}
