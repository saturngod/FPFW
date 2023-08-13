package framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;

class FPFWAnnotationScanner {

    Annotation findAnnotation(AnnotatedElement object, Class<? extends Annotation> clazz) {
        if(object.isAnnotationPresent(clazz)) {
            Annotation[] annotations = object.getAnnotations();
            for (Annotation annotation: annotations) {

                if(annotation.annotationType().getName().equals(clazz.getName())) {
                    return annotation;
                }
            }
        }
        return null;
    }
}
