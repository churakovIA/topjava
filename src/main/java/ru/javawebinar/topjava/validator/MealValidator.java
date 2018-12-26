package ru.javawebinar.topjava.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MealValidator implements Validator {

    @Autowired
    MealService service;

    @Override
    public boolean supports(Class<?> clazz) {
        return Meal.class.isAssignableFrom(clazz) || MealTo.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        boolean isNew = target instanceof Meal ? ((Meal) target).isNew() : ((MealTo) target).isNew();
        if (!isNew) {
            return;
        }
        LocalDateTime dateTime = target instanceof Meal ? ((Meal) target).getDateTime() : ((MealTo) target).getDateTime();
        if (dateTime != null) {
            List<Meal> mealList = service.getBetweenDateTimes(dateTime, dateTime, SecurityUtil.authUserId());
            if (mealList.size() > 0) {
                errors.rejectValue("dateTime", "", "You already have meal with this date/time");
            }
        }
    }
}
