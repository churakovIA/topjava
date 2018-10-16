package ru.javawebinar.topjava.repository.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private final Logger log = LoggerFactory.getLogger(InMemoryMealRepositoryImpl.class);
    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Meal save(int userId, Meal meal) {
        log.info("save userId {}, meal {}", userId, meal);
        meal.setUserId(userId);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.computeIfAbsent(userId, map -> new ConcurrentHashMap<>()).put(meal.getId(), meal);
            return meal;
        }
        // treat case: update, but absent in storage
        Map<Integer, Meal> mealMap = repository.get(userId);
        return mealMap == null ? null : mealMap.computeIfPresent(meal.getId(), (id, oldMeal) -> oldMeal.getUserId() == userId ? meal : oldMeal);
    }

    @Override
    public boolean delete(int userId, int id) {
        log.info("delete userId {}, id {}", userId, id);
        Map<Integer, Meal> mealMap = repository.get(userId);
        return mealMap != null && mealMap.containsKey(id) ? mealMap.remove(id) != null : false;
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("get userId {}, id {}", userId, id);
        Map<Integer, Meal> mealMap = repository.get(userId);
        return mealMap != null ? mealMap.get(id) : null;
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        log.info("getAll userId {}", userId);
        return getList(userId);
    }

    @Override
    public Collection<Meal> getByDate(int userId, LocalDate startDate, LocalDate endDate) {
        log.info("getByDate userId {}, startDate {}, endDate {},", userId, startDate, endDate);
        return getList(userId, meal -> DateTimeUtil.isBetween(meal.getDate(), startDate, endDate));
    }

    private Collection<Meal> getList(int userId) {
        return getList(userId, x -> true);
    }

    private Collection<Meal> getList(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> mealMap = repository.get(userId);
        return mealMap == null ? new ArrayList<>() : mealMap.values().stream()
                .filter(filter.and(meal -> meal.getUserId() == userId))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

}

