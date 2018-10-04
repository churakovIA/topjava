package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );
        List<UserMealWithExceed> filteredWithExceeded = getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        filteredWithExceeded.forEach(System.out::println);
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        //return getFilteredWithExceededByStream(mealList, startTime, endTime, caloriesPerDay);
        //return getFilteredWithExceededByCycle(mealList, startTime, endTime, caloriesPerDay);
        //return getFilteredWithExceededByCycleAtomicBoolean(mealList, startTime, endTime, caloriesPerDay);
        return getFilteredWithExceededByStreamInOneReturn(mealList, startTime, endTime, caloriesPerDay);


    }

    private static List<UserMealWithExceed> getFilteredWithExceededByStream(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> caloriesByDay = mealList.stream().collect(Collectors.toMap(UserMeal::getDate, UserMeal::getCalories, Integer::sum));
        return mealList.stream()
                .filter(userMeal -> TimeUtil.isBetween(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .map(userMeal -> createMealWithExceed(userMeal, new AtomicBoolean(caloriesByDay.getOrDefault(userMeal.getDate(),0) > caloriesPerDay)))
                .collect(Collectors.toList());

    }

    private static List<UserMealWithExceed> getFilteredWithExceededByStreamInOneReturn(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return mealList.stream().collect(Collectors.groupingBy(UserMeal::getDate)).values()
                .stream()
                    .flatMap(dayMeals->{
                        boolean exceed = dayMeals.stream().mapToInt(UserMeal::getCalories).sum() > caloriesPerDay;
                        return dayMeals.stream().filter(m->TimeUtil.isBetween(m.getTime(), startTime, endTime)).map(m->createMealWithExceed(m, new AtomicBoolean(exceed)));
                    })
                    .collect(Collectors.toList());
    }

    private static List<UserMealWithExceed> getFilteredWithExceededByCycleAtomicBoolean(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDay = new HashMap<>();
        Map<LocalDate, AtomicBoolean> exceedByDay = new HashMap<>();
        List<UserMealWithExceed> userMealWithExceeds = new ArrayList<>();
        mealList.forEach(m->{
            caloriesByDay.merge(m.getDate(), m.getCalories(), Integer::sum);
            AtomicBoolean wrapBoolean = exceedByDay.computeIfAbsent(m.getDate(), d->new AtomicBoolean());
            if(caloriesByDay.get(m.getDate()) > caloriesPerDay){
                wrapBoolean.set(true);
            }
            if (TimeUtil.isBetween(m.getTime(), startTime, endTime)){
                userMealWithExceeds.add(createMealWithExceed(m, wrapBoolean));
            }
        });
        return userMealWithExceeds;
    }

    private static List<UserMealWithExceed> getFilteredWithExceededByCycle(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> caloriesByDay = new HashMap<>();
        mealList.forEach(userMeal -> caloriesByDay.merge(userMeal.getDate(), userMeal.getCalories(), Integer::sum));

        List<UserMealWithExceed> userMealWithExceeds = new ArrayList<>();
        mealList.forEach(userMeal -> {
            if (TimeUtil.isBetween(userMeal.getDateTime().toLocalTime(), startTime, endTime)){
                userMealWithExceeds.add(createMealWithExceed(userMeal, new AtomicBoolean(caloriesByDay.get(userMeal.getDate()) > caloriesPerDay)));
            }
        });

        return userMealWithExceeds;
    }

    private static UserMealWithExceed createMealWithExceed(UserMeal meal, AtomicBoolean exceed){
        return new UserMealWithExceed(
                meal.getDateTime(),
                meal.getDescription(),
                meal.getCalories(),
                exceed);
    }

}