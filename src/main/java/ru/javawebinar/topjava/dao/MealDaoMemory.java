package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealDaoMemory implements MealDao {

    private static AtomicInteger mealCounter = new AtomicInteger(0);
    private final static Map<Integer, Meal> mealsMap = new ConcurrentHashMap<Integer, Meal>();

    public MealDaoMemory() {
        add(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500));
        add(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000));
        add(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500));
        add(new Meal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000));
        add(new Meal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500));
        add(new Meal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510));
    }

    @Override
    public List<Meal> list() {
        return new ArrayList<>(mealsMap.values());
    }

    @Override
    public Meal add(Meal m) {
        int id = mealCounter.incrementAndGet();
        Meal meal = new Meal(id, m.getDateTime(), m.getDescription(), m.getCalories());
        mealsMap.put(id, meal);
        return meal;
    }

    @Override
    public void remove(int id) {
        mealsMap.remove(id);
    }

    @Override
    public Meal update(Meal m) {
        mealsMap.computeIfPresent(m.getId(), (k,v)->m);
        return m;
    }

    @Override
    public Meal getById(int id) {
        return mealsMap.get(id);
    }
}