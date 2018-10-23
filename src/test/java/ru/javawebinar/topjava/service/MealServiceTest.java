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
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

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
        Meal actual = service.get(USER_MEAL_100002_ID, USER_ID);
        assertMatch(actual, USER_MEAL_100002);
    }

    @Test
    public void delete() {
        service.delete(ADMIN_MEAL_100008_ID, ADMIN_ID);
        assertMatch(service.getAll(ADMIN_ID), ADMIN_MEAL_100009);
    }

    @Test(expected = NotFoundException.class)
    public void deletedNotFound() {
        service.delete(USER_MEAL_100002_ID, ADMIN_ID);
    }

    @Test
    public void getBetweenDates() {
        final LocalDate start = LocalDate.of(2015, Month.MAY, 30);
        final LocalDate end = LocalDate.of(2015, Month.MAY, 30);
        assertMatch(service.getBetweenDates(start, end, USER_ID),
                USER_MEAL_100004,
                USER_MEAL_100003,
                USER_MEAL_100002
        );
    }

    @Test
    public void getBetweenDateTimes() {
        final LocalDateTime start = LocalDateTime.of(2015, Month.MAY, 30, 20, 0);
        final LocalDateTime end = LocalDateTime.of(2015, Month.MAY, 31, 10, 0);
        assertMatch(service.getBetweenDateTimes(start, end, USER_ID),
                USER_MEAL_100005,
                USER_MEAL_100004
        );
    }

    @Test
    public void getAll() {
        assertMatch(service.getAll(ADMIN_ID),
                ADMIN_MEAL_100009,
                ADMIN_MEAL_100008
        );
    }

    @Test
    public void update() {
        Meal updateMeal = new Meal(USER_MEAL_100002);
        updateMeal.setDescription("asxasxsx");
        updateMeal.setCalories(777);
        service.update(updateMeal, USER_ID);
        assertMatch(service.get(updateMeal.getId(), USER_ID), updateMeal);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotFound() {
        Meal updateMeal = new Meal(USER_MEAL_100002);
        updateMeal.setDescription("asxasxsx");
        updateMeal.setCalories(777);
        service.update(updateMeal, ADMIN_ID);
        assertMatch(service.get(updateMeal.getId(), USER_ID), updateMeal);
    }

    @Test
    public void create() {
        Meal newMeal = new Meal(LocalDateTime.of(2018, Month.MAY, 30, 10, 0), "Завтрак", 500);
        Meal meal = service.create(newMeal, ADMIN_ID);
        newMeal.setId(meal.getId());
        assertMatch(service.getAll(ADMIN_ID),
                newMeal,
                ADMIN_MEAL_100009,
                ADMIN_MEAL_100008
        );
        assertMatch(meal, newMeal);
    }

    @Test(expected = DataAccessException.class)
    public void duplicateDateTimeCreate() {
        service.create(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500), USER_ID);
    }

}