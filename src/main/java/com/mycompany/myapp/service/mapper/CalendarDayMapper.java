package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.CalendarDay;
import com.mycompany.myapp.service.dto.CalendarDayDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CalendarDay} and its DTO {@link CalendarDayDTO}.
 */
@Mapper(componentModel = "spring")
public interface CalendarDayMapper extends EntityMapper<CalendarDayDTO, CalendarDay> {}
