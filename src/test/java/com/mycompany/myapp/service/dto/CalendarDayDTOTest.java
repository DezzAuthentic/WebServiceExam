package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CalendarDayDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CalendarDayDTO.class);
        CalendarDayDTO calendarDayDTO1 = new CalendarDayDTO();
        calendarDayDTO1.setId(1L);
        CalendarDayDTO calendarDayDTO2 = new CalendarDayDTO();
        assertThat(calendarDayDTO1).isNotEqualTo(calendarDayDTO2);
        calendarDayDTO2.setId(calendarDayDTO1.getId());
        assertThat(calendarDayDTO1).isEqualTo(calendarDayDTO2);
        calendarDayDTO2.setId(2L);
        assertThat(calendarDayDTO1).isNotEqualTo(calendarDayDTO2);
        calendarDayDTO1.setId(null);
        assertThat(calendarDayDTO1).isNotEqualTo(calendarDayDTO2);
    }
}
