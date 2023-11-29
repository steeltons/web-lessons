package org.jenjetsu.com.todo.aspect;

import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Hibernate;
import org.hibernate.collection.spi.PersistentBag;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
@Aspect
public class PersistentBagCorrectAspect {
    @Pointcut("execution(public org.jenjetsu.com.todo.model.* " +
            "|| java.util.List<org.jenjetsu.com.todo.model.*>  " +
            "|| java.util.Set<org.jenjetsu.com.todo.model.*> org.jenjetsu.com.todo.service.*.*(..))")
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
        if(retVal instanceof Iterable) {
            Iterable<?> collectionIterator = (Iterable<?>) retVal;
            for (Object collectionElement : collectionIterator) {
                this.returnValuePersistentBagCorrect(collectionElement);
            }
        } else {
            this.returnValuePersistentBagCorrect(retVal);
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
