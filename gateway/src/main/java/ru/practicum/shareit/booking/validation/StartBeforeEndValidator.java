package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.time.LocalDateTime;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookItemRequestDto> {

    @Override
    public boolean isValid(BookItemRequestDto booking, ConstraintValidatorContext context) {
        if (booking.getStart() == null || booking.getEnd() == null || booking.getItemId() == null) {
            return false;
        }
        if (!(booking.getStart() instanceof LocalDateTime)
                || !(booking.getEnd() instanceof LocalDateTime)) {
            throw new IllegalArgumentException(
                    "Illegal method signature, expected two parameters of type LocalDateTime.");
        }

        return (booking.getStart().isAfter(LocalDateTime.now())
                && booking.getStart().isBefore(booking.getEnd()));
    }
}
