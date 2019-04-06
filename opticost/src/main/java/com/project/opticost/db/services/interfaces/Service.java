package com.project.opticost.db.services.interfaces;

import java.util.Collection;
import java.util.List;

public interface Service<T> {
    List<T> findAll();
    List<T> saveAll(Collection<T> entities);
    T saveAndFlush(T entity);
}
