package com.project.opticost.db.services.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public abstract class AbstractService<T, E> implements Service<T, E> {

    public abstract JpaRepository<T, E> getRepo();

    @Override
    public List<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public List<T> saveAll(Collection<T> entities) {
        return getRepo().saveAll(entities);
    }

    @Override
    public T saveAndFlush(T entity) {
        return getRepo().saveAndFlush(entity);
    }

    @Override
    public T getOne(E id) {
        return getRepo().getOne(id);
    }

    @Override
    public void deleteById(E id) {
        getRepo().deleteById(id);
    }
}
