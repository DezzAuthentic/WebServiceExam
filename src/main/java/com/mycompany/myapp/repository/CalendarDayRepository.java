package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.CalendarDay;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CalendarDay entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CalendarDayRepository extends JpaRepository<CalendarDay, Long> {}
