package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CalendarDayTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CalendarDayTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CalendarDay.class);
        CalendarDay calendarDay1 = getCalendarDaySample1();
        CalendarDay calendarDay2 = new CalendarDay();
        assertThat(calendarDay1).isNotEqualTo(calendarDay2);

        calendarDay2.setId(calendarDay1.getId());
        assertThat(calendarDay1).isEqualTo(calendarDay2);

        calendarDay2 = getCalendarDaySample2();
        assertThat(calendarDay1).isNotEqualTo(calendarDay2);
    }
}
