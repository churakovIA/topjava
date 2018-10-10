package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealDAOMemory implements MealDAO {

    private static AtomicInteger mealCounter = new AtomicInteger(0);
    private final static Map<Integer, Meal> mealsMap;

    static {
        mealsMap = new ConcurrentHashMap<Integer, Meal>(){{
            List<Meal> meals = Arrays.asList(
                    new Meal(1, LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                    new Meal(2, LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                    new Meal(3, LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                    new Meal(4, LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                    new Meal(5, LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                    new Meal(6, LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
            );
            meals.forEach(meal -> put(meal.getId(), meal));
            mealCounter.set(meals.size());
        }};
    }

    @Override
    public List<Meal> list() {
        return new ArrayList(mealsMap.values());
    }

    @Override
    public void add(Meal m) {
        int id = mealCounter.incrementAndGet();
        Meal meal = new Meal(id, m.getDateTime(), m.getDescription(), m.getCalories());
        mealsMap.put(id, meal);
    }

    @Override
    public void remove(int id) {
        mealsMap.remove(id);
    }

    @Override
    public void update(Meal m) {
        mealsMap.replace(m.getId(), m);
    }

    @Override
    public Meal getById(int id) {
        return mealsMap.get(id);
    }
}