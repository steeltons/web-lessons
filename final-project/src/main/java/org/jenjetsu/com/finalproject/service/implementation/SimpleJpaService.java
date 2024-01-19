package org.jenjetsu.com.finalproject.service.implementation;

import java.io.Serializable;
import static java.lang.String.format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jenjetsu.com.finalproject.exception.EntityAccessDeniedException;
import org.jenjetsu.com.finalproject.exception.EntityCreateException;
import org.jenjetsu.com.finalproject.exception.EntityDeleteException;
import org.jenjetsu.com.finalproject.exception.EntityModifyException;
import org.jenjetsu.com.finalproject.exception.EntityNotFoundException;
import org.jenjetsu.com.finalproject.exception.EntityValidateException;
import org.jenjetsu.com.finalproject.service.CRUDService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SimpleJpaService<E, ID extends Serializable> implements CRUDService<E, ID> {

    private final JpaRepository<E, ID> jpaRepository;
    private final Class<? extends E> clazz;

    public SimpleJpaService(Class<? extends E> mappedClass, 
                            JpaRepository<E, ID> jpaRepository) {
        this.clazz = mappedClass;
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public E create(E raw) {
        try {
            return createEntity(raw);
        } catch (Exception e) {
            if (e instanceof EntityValidateException ||
                e instanceof EntityNotFoundException ||
                e instanceof EntityAccessDeniedException) {
                    throw e;
            }
            throw new EntityCreateException(format("Impossible to save Entity %s", clazz.getSimpleName()), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<E> createAll(Collection<E> rawCollection) {
        List<E> persistedEntities = new ArrayList<>();
        int index = 0;
        for(E raw : rawCollection) {
            try {
                persistedEntities.add(createEntity(raw));
                index++;
            } catch (Exception e) {
                throw new EntityCreateException(format("Impossible to save Entity %s in list[%d]", clazz.getSimpleName(), index), e);
            }
        }
        return persistedEntities;
    }

    /**
     * <h2>createEntity</h2>
     * <p>Overridable method that save entities. Used by create and createAll methods that wrapped by @Transactional</p>
     * @param raw
     * @return
     */
    protected E createEntity(E raw) {
        return this.jpaRepository.saveAndFlush(raw);
    }

    @Override
    public E readById(ID id) {
        if(id == null) {
            throw new EntityValidateException(format("%s id is null", clazz.getSimpleName()));
        }
        return this.jpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        format("Impossible to find entity %s with id %s", clazz.getSimpleName(), id))
                );
    }

    @Override
    public List<E> readAll() {
        return this.jpaRepository.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public E update(E newEntity) {
        try {
            return updateEntity(newEntity);
        } catch (Exception e) {
            throw new EntityModifyException(format("Impossible to update Entity %s", clazz.getSimpleName()), e);
        }
    }

    /**
     * <h2>updateEntity</h2>
     * <p>Overridable method for updating entities. Used by update method that wrapped by @Transactional</p>
     * @param newEntity
     * @return
     */
    protected E updateEntity(E newEntity) {
        return this.jpaRepository.saveAndFlush(newEntity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(E removeEntity) {
        try {
            this.jpaRepository.delete(removeEntity);
        } catch (Exception e) {
            throw new EntityDeleteException(format("Impossible to delete Entity %s", clazz.getSimpleName()), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(ID id) {
        try {
            this.jpaRepository.deleteById(id);
        } catch (Exception e) {
            throw new EntityDeleteException(format("Impossible to delete Entity %s with id %s", clazz.getSimpleName(), id.toString()), e);
        }
    }

    @Override
    public boolean existsById(ID id) {
        return id != null && this.jpaRepository.existsById(id);
    }
}
