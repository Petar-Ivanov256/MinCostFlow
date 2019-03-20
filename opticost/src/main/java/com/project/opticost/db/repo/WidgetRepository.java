package com.project.opticost.db.repo;

import com.project.opticost.db.model.Widget;
import org.springframework.data.repository.CrudRepository;

public interface WidgetRepository extends CrudRepository<Widget, Long> {
}