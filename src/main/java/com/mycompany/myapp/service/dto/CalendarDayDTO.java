package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.CalendarDay} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CalendarDayDTO implements Serializable {

    private Long id;

    private LocalDate date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CalendarDayDTO)) {
            return false;
        }

        CalendarDayDTO calendarDayDTO = (CalendarDayDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, calendarDayDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CalendarDayDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            "}";
    }
}
