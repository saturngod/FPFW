package framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;

class FPFWAnnotationScanner {

    Annotation findAnnotation(Executable object, Class<? extends Annotation> clazz) {
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
