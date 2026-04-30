package com.coffeeshop.repository;

import com.coffeeshop.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, java.util.UUID> {
    List<JobApplication> findAllByOrderByCreatedAtDesc();

    java.util.Optional<JobApplication> findByTrackingCode(String trackingCode);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM JobApplication a WHERE LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR a.phone LIKE CONCAT('%', :keyword, '%')")
    org.springframework.data.domain.Page<JobApplication> searchApplicationsPaginated(
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM JobApplication a WHERE a.status = :status AND (" +
            "LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "a.phone LIKE CONCAT('%', :keyword, '%'))")
    org.springframework.data.domain.Page<JobApplication> searchApplicationsByStatusPaginated(
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            @org.springframework.data.repository.query.Param("status") JobApplication.ApplicationStatus status,
            org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<JobApplication> findByStatus(JobApplication.ApplicationStatus status,
            org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<JobApplication> findAll(org.springframework.data.domain.Pageable pageable);
}
