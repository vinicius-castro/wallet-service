package com.vicastro.walletservice.shared.utils;

import com.vicastro.walletservice.shared.exception.InvalidDateException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

public class OffsetDateTimeUtil {

    private OffsetDateTimeUtil() {
        // Utility class
    }

    public static OffsetDateTime parseToOffsetDateTime(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        var normalizedString = dateString.trim();
        try {
            return OffsetDateTime.parse(normalizedString);
        } catch (DateTimeParseException e) {
            return convertStringToOffsetDateTimeDefault(normalizedString);
        }
    }

    private static OffsetDateTime convertStringToOffsetDateTimeDefault(String normalizedString) {
        try {
            var localDate = LocalDate.parse(normalizedString);
            return localDate
                    .atTime(LocalTime.MAX)
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime();
        } catch (DateTimeParseException e) {
            throw new InvalidDateException();
        }
    }
}
