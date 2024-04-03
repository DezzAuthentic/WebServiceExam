package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.CalendarDayRepository;
import com.mycompany.myapp.service.CalendarDayService;
import com.mycompany.myapp.service.dto.CalendarDayDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.CalendarDay}.
 */
@RestController
@RequestMapping("/api/calendar-days")
public class CalendarDayResource {

    private final Logger log = LoggerFactory.getLogger(CalendarDayResource.class);

    private static final String ENTITY_NAME = "webservicedayfinderCalendarDay";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CalendarDayService calendarDayService;

    private final CalendarDayRepository calendarDayRepository;

    public CalendarDayResource(CalendarDayService calendarDayService, CalendarDayRepository calendarDayRepository) {
        this.calendarDayService = calendarDayService;
        this.calendarDayRepository = calendarDayRepository;
    }

    /**
     * {@code POST  /calendar-days} : Create a new calendarDay.
     *
     * @param calendarDayDTO the calendarDayDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new calendarDayDTO, or with status {@code 400 (Bad Request)} if the calendarDay has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CalendarDayDTO> createCalendarDay(@RequestBody CalendarDayDTO calendarDayDTO) throws URISyntaxException {
        log.debug("REST request to save CalendarDay : {}", calendarDayDTO);
        if (calendarDayDTO.getId() != null) {
            throw new BadRequestAlertException("A new calendarDay cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CalendarDayDTO result = calendarDayService.save(calendarDayDTO);
        return ResponseEntity
            .created(new URI("/api/calendar-days/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /calendar-days/:id} : Updates an existing calendarDay.
     *
     * @param id the id of the calendarDayDTO to save.
     * @param calendarDayDTO the calendarDayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated calendarDayDTO,
     * or with status {@code 400 (Bad Request)} if the calendarDayDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the calendarDayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CalendarDayDTO> updateCalendarDay(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CalendarDayDTO calendarDayDTO
    ) throws URISyntaxException {
        log.debug("REST request to update CalendarDay : {}, {}", id, calendarDayDTO);
        if (calendarDayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, calendarDayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!calendarDayRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CalendarDayDTO result = calendarDayService.update(calendarDayDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, calendarDayDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /calendar-days/:id} : Partial updates given fields of an existing calendarDay, field will ignore if it is null
     *
     * @param id the id of the calendarDayDTO to save.
     * @param calendarDayDTO the calendarDayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated calendarDayDTO,
     * or with status {@code 400 (Bad Request)} if the calendarDayDTO is not valid,
     * or with status {@code 404 (Not Found)} if the calendarDayDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the calendarDayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CalendarDayDTO> partialUpdateCalendarDay(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CalendarDayDTO calendarDayDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CalendarDay partially : {}, {}", id, calendarDayDTO);
        if (calendarDayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, calendarDayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!calendarDayRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CalendarDayDTO> result = calendarDayService.partialUpdate(calendarDayDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, calendarDayDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /calendar-days} : get all the calendarDays.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of calendarDays in body.
     */
    @GetMapping("")
    public List<CalendarDayDTO> getAllCalendarDays() {
        log.debug("REST request to get all CalendarDays");
        return calendarDayService.findAll();
    }

    /**
     * {@code GET  /calendar-days/:id} : get the "id" calendarDay.
     *
     * @param id the id of the calendarDayDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the calendarDayDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CalendarDayDTO> getCalendarDay(@PathVariable("id") Long id) {
        log.debug("REST request to get CalendarDay : {}", id);
        Optional<CalendarDayDTO> calendarDayDTO = calendarDayService.findOne(id);
        return ResponseUtil.wrapOrNotFound(calendarDayDTO);
    }

    /**
     * {@code DELETE  /calendar-days/:id} : delete the "id" calendarDay.
     *
     * @param id the id of the calendarDayDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendarDay(@PathVariable("id") Long id) {
        log.debug("REST request to delete CalendarDay : {}", id);
        calendarDayService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
