package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.Util;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal actual = service.get(MEAL_ID, USER_ID);
        assertMatch(actual, MEAL);
    }

    @Test(expected = NotFoundException.class)
    public void getNotFound() {
        Meal actual = service.get(MEAL_ID, ADMIN_ID);
        assertMatch(actual, MEAL);
    }

    @Test
    public void delete() {
        service.delete(MEAL_ID, USER_ID);
        assertMatch(service.getAll(USER_ID), MEALS.subList(1, MEALS.size()));
    }

    @Test(expected = NotFoundException.class)
    public void deletedNotFound() {
        service.delete(MEAL_ID, ADMIN_ID);
    }

    @Test
    public void getBetweenDates() {
        final LocalDate start = LocalDate.of(2015, Month.MAY, 30);
        final LocalDate end = LocalDate.of(2015, Month.MAY, 30);
        List<Meal> expected = MEALS.stream().filter(m -> Util.isBetween(m.getDateTime(), LocalDateTime.of(start, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX))).collect(Collectors.toList());
        assertMatch(service.getBetweenDates(start, end, USER_ID), expected);
    }

    @Test
    public void getBetweenDateTimes() {
        final LocalDateTime start = LocalDateTime.of(2015, Month.MAY, 30, 20, 0);
        final LocalDateTime end = LocalDateTime.of(2015, Month.MAY, 31, 10, 0);
        List<Meal> expected = MEALS.stream().filter(m -> Util.isBetween(m.getDateTime(), start, end)).collect(Collectors.toList());
        assertMatch(service.getBetweenDateTimes(start, end, USER_ID), expected);
    }

    @Test
    public void getAll() {
        assertMatch(service.getAll(USER_ID), MEALS);
    }

    @Test
    public void update() {
        Meal updateMeal = new Meal(MEAL);
        updateMeal.setDescription("asxasxsx");
        updateMeal.setCalories(777);
        service.update(updateMeal, USER_ID);
        assertMatch(service.get(updateMeal.getId(), USER_ID), updateMeal);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotFound() {
        Meal updateMeal = new Meal(MEAL);
        updateMeal.setDescription("asxasxsx");
        updateMeal.setCalories(777);
        service.update(updateMeal, ADMIN_ID);
        assertMatch(service.get(updateMeal.getId(), USER_ID), updateMeal);
    }

    @Test
    public void create() {
        Meal newMeal = new Meal(LocalDateTime.of(2018, Month.MAY, 30, 10, 0), "Завтрак", 500);
        Meal meal = service.create(newMeal, USER_ID);
        newMeal.setId(meal.getId());
        ArrayList<Meal> meals = new ArrayList<Meal>(MEALS) {{
            add(0, newMeal);
        }};
        assertMatch(service.getAll(USER_ID), meals);
    }

    @Test(expected = DataAccessException.class)
    public void duplicateDateTimeCreate() {
        service.create(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500), USER_ID);
    }

}