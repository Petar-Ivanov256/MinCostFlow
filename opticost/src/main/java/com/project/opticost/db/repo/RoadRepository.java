package com.project.opticost.db.repo;

import com.project.opticost.db.model.Road;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadRepository extends JpaRepository<Road, Long> {
}
