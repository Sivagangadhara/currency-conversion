package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.ConversionHistory;

public interface ConversionRepository extends JpaRepository<ConversionHistory, Long>{

}
