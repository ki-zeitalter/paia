package de.kizeitalter.paiatools.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Period;

@Converter(autoApply = true)
public class PeriodConverter implements AttributeConverter<Period, String> {

    @Override
    public String convertToDatabaseColumn(Period period) {
        return period != null ? period.toString() : null;
    }

    @Override
    public Period convertToEntityAttribute(String dbData) {
        return dbData != null ? Period.parse(dbData) : null;
    }
} 