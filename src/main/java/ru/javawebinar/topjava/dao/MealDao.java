package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    public List<Meal> list();
    public Meal add(Meal m);
    public void remove(int id);
    public Meal update(Meal m);
    public Meal getById(int id);
}