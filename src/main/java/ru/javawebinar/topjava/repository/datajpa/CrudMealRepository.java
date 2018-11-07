package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {

    @Override
    Optional<Meal> findById(Integer integer);

    @Override
    @Transactional
    Meal save(Meal meal);

    @Transactional
    @Modifying
    @Query("DELETE FROM Meal m WHERE m.id=:id and m.user.id=:user_id")
    int delete(@Param("id") int id, @Param("user_id") int userId);

    @Query("FROM Meal m WHERE m.user.id=:user_id ORDER BY m.dateTime desc")
    List<Meal> getAll(@Param("user_id") int userId);

    @Query("FROM Meal m WHERE m.user.id=:user_id AND m.dateTime BETWEEN :startDate AND :endDate  ORDER BY m.dateTime desc")
    List<Meal> getBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("user_id") int userId);
}
