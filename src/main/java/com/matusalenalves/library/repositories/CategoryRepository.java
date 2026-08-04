package com.matusalenalves.library.repositories;

import com.matusalenalves.library.entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}