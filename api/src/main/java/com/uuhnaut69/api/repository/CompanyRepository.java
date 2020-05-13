package com.uuhnaut69.api.repository;

import com.uuhnaut69.api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
