package com.uuhnaut69.dbz.mysql.repository;

import com.uuhnaut69.dbz.mysql.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
}
