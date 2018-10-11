package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MealDaoMemory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(UserServlet.class);
    private static final String INSERT_OR_EDIT = "/meal.jsp";
    private static final String LIST_MEAL = "/meals.jsp";
    private MealDao dao;

    @Override
    public void init() {
        dao = new MealDaoMemory();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String forward = "";
        String action = request.getParameter("action");
        if (action == null) action = "";
        int mealID;

        switch (action) {
            case "remove":
                mealID = Integer.parseInt(request.getParameter("id"));
                dao.remove(mealID);
                break;
            case "edit":
                forward = INSERT_OR_EDIT;
                mealID = Integer.parseInt(request.getParameter("id"));
                Meal meal = dao.getById(mealID);
                request.setAttribute("meal", meal);
                break;
            case "insert":
                forward = INSERT_OR_EDIT;
                break;
            default:
                forward = LIST_MEAL;
                request.setAttribute("meals", MealsUtil.getFilteredWithExceeded(dao.list(), LocalTime.MIN, LocalTime.MAX, 2000));
        }
        if (forward.length() == 0) {
            log.debug("redirect to meals");
            response.sendRedirect("meals");
        } else {
            log.debug(String.format("forvard to %s", forward));
            RequestDispatcher view = request.getRequestDispatcher(forward);
            view.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        String mealID = request.getParameter("id");
        if (mealID == null || mealID.isEmpty()) {
            Meal meal = new Meal(dateTime, description, calories);
            dao.add(meal);
        } else {
            Meal meal = new Meal(Integer.parseInt(mealID), dateTime, description, calories);
            dao.update(meal);
        }
        log.debug("redirect to meals");
        response.sendRedirect("meals");
    }
}