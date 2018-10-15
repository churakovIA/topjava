package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealWithExceed;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MealService service;

    public List<MealWithExceed> getByDateTime(String startTime, String endTime, String startDate, String endDate) {
        log.info("getByDateTime with " +
                "startTime {}" +
                "endTime {}" +
                "startDate {}" +
                "endDate {}", startTime, endTime, startDate, endDate);

        return service.getByDateTime(startTime.length() == 0 ? LocalTime.MIN : LocalTime.parse(startTime),
                endTime.length() == 0 ? LocalTime.MAX : LocalTime.parse(endTime),
                startDate.length() == 0 ? LocalDate.MIN : LocalDate.parse(startDate),
                endDate.length() == 0 ? LocalDate.MAX : LocalDate.parse(endDate),
                authUserId(), authUserCaloriesPerDay());
    }

    public List<MealWithExceed> getAll() {
        log.info("getAll");
        return service.getAll(authUserId(), authUserCaloriesPerDay());
    }

    public void create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        service.update(meal);
    }

    public void update(Meal meal, int id) {
        log.info("save {}", meal);
        assureIdConsistent(meal, id);
        service.update(meal);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(authUserId(), id);
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(authUserId(), id);
    }
}