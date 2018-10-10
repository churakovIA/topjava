package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDAO {
    public List<Meal> list();
    public void add(Meal m);
    public void remove(int id);
    public void update(Meal m);
    public Meal getById(int id);
}