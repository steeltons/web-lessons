package org.jenjetsu.com.todo.aspect;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Hibernate;
import org.hibernate.collection.spi.PersistentBag;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import lombok.SneakyThrows;

@Component
@Aspect
public class PersistentBagCorrectAspect {

    private final Map<String, ?> modelClassNameMap;
    private final String MODEL_CLASS_PATH = "org.jenjetsu.com.todo.model";
    
    public PersistentBagCorrectAspect() {
        InputStream steam = ClassLoader.getSystemClassLoader().getResourceAsStream(MODEL_CLASS_PATH.replace('.', '/'));
        BufferedReader reader = new BufferedReader(new InputStreamReader(steam));
        modelClassNameMap = reader.lines()
            .filter((line) -> line.endsWith(".class") && !line.endsWith("Builder.class"))
            .map((line) -> this.getClass(line, MODEL_CLASS_PATH))
            .collect(Collectors.toMap(Class::getName, Function.identity()));
    }

    @SneakyThrows
    private final Class<?> getClass(String className, String packageName) {
        return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
    } 

    @Pointcut("execution(public * org.jenjetsu.com.todo.service.*.*(*))")
    public void returnEntityPersistenceBagCorrector() {}

    /**
     * <h2>correctPersistentBag</h2>
     * <p>Replace all not loaded Persistent bags in return model into empty list or set</p>
     * @param pjp process join point
     * @return Object - return object or list of objects from org.jenjetsu.com.tоdo.model
     */
    @Around("returnEntityPersistenceBagCorrector()")
    @SneakyThrows
    public Object correctPersistentBag(ProceedingJoinPoint pjp) {
        Object retVal = pjp.proceed();
        if(retVal != null && this.modelClassNameMap.containsKey(retVal.getClass().getName())) {
            if(retVal instanceof Iterable) {
                Iterable<?> collectionIterator = (Iterable<?>) retVal;
                for (Object collectionElement : collectionIterator) {
                    this.returnValuePersistentBagCorrect(collectionElement);
                }
            } else {
                this.returnValuePersistentBagCorrect(retVal);
            }
        }
        return retVal;
    }

    /**
     * <h2>returnValuePersistentBagCorrect</h2>
     * <p>Replace all not initialized collections to empty collections</p>
     * @param object
     * @throws Throwable every exception with reflections
     */
    private void returnValuePersistentBagCorrect(Object object) throws Throwable{
        Class<?> objectClass = object.getClass();
        if(ClassUtils.isPrimitiveOrWrapper(objectClass)) {
            return;
        }
        for(Field field : objectClass.getDeclaredFields()) {
            field.setAccessible(true);
            Object fieldValue = field.get(object);
            if (fieldValue instanceof PersistentBag && !Hibernate.isInitialized(fieldValue)) {
                if (List.class.equals(field.getType())) {
                    field.set(object, Collections.emptyList());
                } else if (Set.class.equals(field.getType())) {
                    field.set(object, Collections.emptySet());
                } else {
                    throw new IllegalArgumentException("Field is not List or Set");
                }
            }
        }
    }

}
