package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealWithExceed;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MealService {

    Meal create(int userId, Meal meal);

    void delete(int userId, int id) throws NotFoundException;

    Meal get(int userId, int id) throws NotFoundException;

    void update(Meal meal);

    List<MealWithExceed> getAll(int userId, int caloriesPerDay);

    List<MealWithExceed> getByDateTime(LocalTime startTime, LocalTime endTime, LocalDate startDate, LocalDate endDate, int userId, int caloriesPerDay);

}