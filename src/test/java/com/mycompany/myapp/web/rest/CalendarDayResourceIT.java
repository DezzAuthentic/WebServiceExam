package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.CalendarDay;
import com.mycompany.myapp.repository.CalendarDayRepository;
import com.mycompany.myapp.service.dto.CalendarDayDTO;
import com.mycompany.myapp.service.mapper.CalendarDayMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CalendarDayResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CalendarDayResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/calendar-days";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CalendarDayRepository calendarDayRepository;

    @Autowired
    private CalendarDayMapper calendarDayMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCalendarDayMockMvc;

    private CalendarDay calendarDay;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CalendarDay createEntity(EntityManager em) {
        CalendarDay calendarDay = new CalendarDay().date(DEFAULT_DATE);
        return calendarDay;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CalendarDay createUpdatedEntity(EntityManager em) {
        CalendarDay calendarDay = new CalendarDay().date(UPDATED_DATE);
        return calendarDay;
    }

    @BeforeEach
    public void initTest() {
        calendarDay = createEntity(em);
    }

    @Test
    @Transactional
    void createCalendarDay() throws Exception {
        int databaseSizeBeforeCreate = calendarDayRepository.findAll().size();
        // Create the CalendarDay
        CalendarDayDTO calendarDayDTO = calendarDayMapper.toDto(calendarDay);
        restCalendarDayMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(calendarDayDTO))
            )
            .andExpect(status().isCreated());

        // Validate the CalendarDay in the database
        List<CalendarDay> calendarDayList = calendarDayRepository.findAll();
        assertThat(calendarDayList).hasSize(databaseSizeBeforeCreate + 1);
        CalendarDay testCalendarDay = calendarDayList.get(calendarDayList.size() - 1);
        assertThat(testCalendarDay.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void createCalendarDayWithExistingId() throws Exception {
        // Create the CalendarDay with an existing ID
        calendarDay.setId(1L);
        CalendarDayDTO calendarDayDTO = calendarDayMapper.toDto(calendarDay);

        int databaseSizeBeforeCreate = calendarDayRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCalendarDayMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(calendarDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CalendarDay in the database
        List<CalendarDay> calendarDayList = calendarDayRepository.findAll();
        assertThat(calendarDayList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCalendarDays() throws Exception {
        // Initialize the database
        calendarDayRepository.saveAndFlush(calendarDay);

        // Get all the calendarDayList
        restCalendarDayMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(calendarDay.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getCalendarDay() throws Exception {
        // Initialize the database
        calendarDayRepository.saveAndFlush(calendarDay);

        // Get the calendarDay
        restCalendarDayMockMvc
            .perform(get(ENTITY_API_URL_ID, calendarDay.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(calendarDay.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCalendarDay() throws Exception {
        // Get the calendarDay
        restCalendarDayMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCalendarDay() throws Exception {
        // Initialize the database
        calendarDayRepository.saveAndFlush(calendarDay);

        int databaseSizeBeforeUpdate = calendarDayRepository.findAll().size();

        // Update the calendarDay
        CalendarDay updatedCalendarDay = calendarDayRepository.findById(calendarDay.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCalendarDay are not directly saved in db
        em.detach(updatedCalendarDay);
        updatedCalendarDay.date(UPDATED_DATE);
        CalendarDayDTO calendarDayDTO = calendarDayMapper.toDto(updatedCalendarDay);

        restCalendarDayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, calendarDayDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(calendarDayDTO))
            )
            .andExpect(status().isOk());

        // Validate the CalendarDay in the database
        List<CalendarDay> calendarDayList = calendarDayRepository.findAll();
        assertThat(calendarDayList).hasSize(databaseSizeBeforeUpdate);
        CalendarDay testCalendarDay = calendarDayList.get(calendarDayList.size() - 1);
        assertThat(testCalendarDay.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingCalendarDay() throws Exception {
        int databaseSizeBeforeUpdate = calendarDayRepository.findAll().size();
        calendarDay.setId(longCount.incrementAndGet());

        // Create the CalendarDay
        CalendarDayDTO calendarDayDTO = calendarDayMapper.toDto(calendarDay);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCalendarDayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, calendarDayDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(calendarDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CalendarDay in the database
        List<CalendarDay> calendarDayList = calendarDayRepository.findAll();
        assertThat(calendarDayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCalendarDay() throws Exception {
        int databaseSizeBeforeUpdate = calendarDayRepository.findAll().size();
        calendarDay.setId(longCount.incrementAndGet());

        // Create the CalendarDay
        CalendarDayDTO calendarDayDTO = calendarDayMapper.toDto(calendarDay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalendarDayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(calendarDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CalendarDay in the database
        List<CalendarDay> calendarDayList = calendarDayRepository.findAll();
        assertThat(calendarDayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCalendarDay() throws Exception {
        int databaseSizeBeforeUpdate = calendarDayRepository.findAll().size();
        calendarDay.setId(longCount.incrementAndGet());

        // Create the CalendarDay
        CalendarDayDTO calendarDayDTO = calendarDayMapper.toDto(calendarDay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalendarDayMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(calendarDayDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CalendarDay in the database
        List<CalendarDay> calendarDayList = calendarDayRepository.findAll();
        assertThat(calendarDayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCalendarDayWithPatch() throws Exception {
        // Initialize the database
        calendarDayRepository.saveAndFlush(calendarDay);

        int databaseSizeBeforeUpdate = calendarDayRepository.findAll().size();

        // Update the calendarDay using partial update
        CalendarDay partialUpdatedCalendarDay = new CalendarDay();
        partialUpdatedCalendarDay.setId(calendarDay.getId());

        partialUpdatedCalendarDay.date(UPDATED_DATE);

        restCalendarDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCalendarDay.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCalendarDay))
            )
            .andExpect(status().isOk());

        // Validate the CalendarDay in the database
        List<CalendarDay> calendarDayList = calendarDayRepository.findAll();
        assertThat(calendarDayList).hasSize(databaseSizeBeforeUpdate);
        CalendarDay testCalendarDay = calendarDayList.get(calendarDayList.size() - 1);
        assertThat(testCalendarDay.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateCalendarDayWithPatch() throws Exception {
        // Initialize the database
        calendarDayRepository.saveAndFlush(calendarDay);

        int databaseSizeBeforeUpdate = calendarDayRepository.findAll().size();

        // Update the calendarDay using partial update
        CalendarDay partialUpdatedCalendarDay = new CalendarDay();
        partialUpdatedCalendarDay.setId(calendarDay.getId());

        partialUpdatedCalendarDay.date(UPDATED_DATE);

        restCalendarDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCalendarDay.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCalendarDay))
            )
            .andExpect(status().isOk());

        // Validate the CalendarDay in the database
        List<CalendarDay> calendarDayList = calendarDayRepository.findAll();
        assertThat(calendarDayList).hasSize(databaseSizeBeforeUpdate);
        CalendarDay testCalendarDay = calendarDayList.get(calendarDayList.size() - 1);
        assertThat(testCalendarDay.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingCalendarDay() throws Exception {
        int databaseSizeBeforeUpdate = calendarDayRepository.findAll().size();
        calendarDay.setId(longCount.incrementAndGet());

        // Create the CalendarDay
        CalendarDayDTO calendarDayDTO = calendarDayMapper.toDto(calendarDay);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCalendarDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, calendarDayDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(calendarDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CalendarDay in the database
        List<CalendarDay> calendarDayList = calendarDayRepository.findAll();
        assertThat(calendarDayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCalendarDay() throws Exception {
        int databaseSizeBeforeUpdate = calendarDayRepository.findAll().size();
        calendarDay.setId(longCount.incrementAndGet());

        // Create the CalendarDay
        CalendarDayDTO calendarDayDTO = calendarDayMapper.toDto(calendarDay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalendarDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(calendarDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CalendarDay in the database
        List<CalendarDay> calendarDayList = calendarDayRepository.findAll();
        assertThat(calendarDayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCalendarDay() throws Exception {
        int databaseSizeBeforeUpdate = calendarDayRepository.findAll().size();
        calendarDay.setId(longCount.incrementAndGet());

        // Create the CalendarDay
        CalendarDayDTO calendarDayDTO = calendarDayMapper.toDto(calendarDay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalendarDayMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(calendarDayDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CalendarDay in the database
        List<CalendarDay> calendarDayList = calendarDayRepository.findAll();
        assertThat(calendarDayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCalendarDay() throws Exception {
        // Initialize the database
        calendarDayRepository.saveAndFlush(calendarDay);

        int databaseSizeBeforeDelete = calendarDayRepository.findAll().size();

        // Delete the calendarDay
        restCalendarDayMockMvc
            .perform(delete(ENTITY_API_URL_ID, calendarDay.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CalendarDay> calendarDayList = calendarDayRepository.findAll();
        assertThat(calendarDayList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
