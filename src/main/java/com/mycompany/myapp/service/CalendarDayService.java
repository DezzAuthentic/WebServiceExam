package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.CalendarDay;
import com.mycompany.myapp.repository.CalendarDayRepository;
import com.mycompany.myapp.service.dto.CalendarDayDTO;
import com.mycompany.myapp.service.mapper.CalendarDayMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.CalendarDay}.
 */
@Service
@Transactional
public class CalendarDayService {

    private final Logger log = LoggerFactory.getLogger(CalendarDayService.class);

    private final CalendarDayRepository calendarDayRepository;

    private final CalendarDayMapper calendarDayMapper;

    public CalendarDayService(CalendarDayRepository calendarDayRepository, CalendarDayMapper calendarDayMapper) {
        this.calendarDayRepository = calendarDayRepository;
        this.calendarDayMapper = calendarDayMapper;
    }

    /**
     * Save a calendarDay.
     *
     * @param calendarDayDTO the entity to save.
     * @return the persisted entity.
     */
    public CalendarDayDTO save(CalendarDayDTO calendarDayDTO) {
        log.debug("Request to save CalendarDay : {}", calendarDayDTO);
        CalendarDay calendarDay = calendarDayMapper.toEntity(calendarDayDTO);
        calendarDay = calendarDayRepository.save(calendarDay);
        return calendarDayMapper.toDto(calendarDay);
    }

    /**
     * Update a calendarDay.
     *
     * @param calendarDayDTO the entity to save.
     * @return the persisted entity.
     */
    public CalendarDayDTO update(CalendarDayDTO calendarDayDTO) {
        log.debug("Request to update CalendarDay : {}", calendarDayDTO);
        CalendarDay calendarDay = calendarDayMapper.toEntity(calendarDayDTO);
        calendarDay = calendarDayRepository.save(calendarDay);
        return calendarDayMapper.toDto(calendarDay);
    }

    /**
     * Partially update a calendarDay.
     *
     * @param calendarDayDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CalendarDayDTO> partialUpdate(CalendarDayDTO calendarDayDTO) {
        log.debug("Request to partially update CalendarDay : {}", calendarDayDTO);

        return calendarDayRepository
            .findById(calendarDayDTO.getId())
            .map(existingCalendarDay -> {
                calendarDayMapper.partialUpdate(existingCalendarDay, calendarDayDTO);

                return existingCalendarDay;
            })
            .map(calendarDayRepository::save)
            .map(calendarDayMapper::toDto);
    }

    /**
     * Get all the calendarDays.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CalendarDayDTO> findAll() {
        log.debug("Request to get all CalendarDays");
        return calendarDayRepository.findAll().stream().map(calendarDayMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one calendarDay by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CalendarDayDTO> findOne(Long id) {
        log.debug("Request to get CalendarDay : {}", id);
        return calendarDayRepository.findById(id).map(calendarDayMapper::toDto);
    }

    /**
     * Delete the calendarDay by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete CalendarDay : {}", id);
        calendarDayRepository.deleteById(id);
    }
}
