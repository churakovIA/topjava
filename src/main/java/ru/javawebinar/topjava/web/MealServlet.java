package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDAO;
import ru.javawebinar.topjava.dao.MealDAOMemory;
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
    private static String INSERT_OR_EDIT = "/meal.jsp";
    private static String LIST_MEAL = "/meals.jsp";
    private final MealDAO dao = new MealDAOMemory();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String forward="";
        String action = request.getParameter("action");

        if (action.equalsIgnoreCase("remove")){
            int mealID = Integer.parseInt(request.getParameter("id"));
            dao.remove(mealID);
            forward = LIST_MEAL;
            request.setAttribute("meals", MealsUtil.getFilteredWithExceededInOnePass2(dao.list(), LocalTime.MIN, LocalTime.MAX, 2000));
        } else if (action.equalsIgnoreCase("edit")){
            forward = INSERT_OR_EDIT;
            int mealID = Integer.parseInt(request.getParameter("id"));
            Meal meal = dao.getById(mealID);
            request.setAttribute("meal", meal);
            request.setAttribute("dateTime", meal.getDateTime());
        } else if (action.equalsIgnoreCase("insert")){
            forward = INSERT_OR_EDIT;
        } else{
            forward = LIST_MEAL;
            request.setAttribute("meals", MealsUtil.getFilteredWithExceededInOnePass2(dao.list(), LocalTime.MIN, LocalTime.MAX, 2000));
        }
        RequestDispatcher view = request.getRequestDispatcher(forward);
        view.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        String mealID = request.getParameter("id");
        if(mealID == null || mealID.isEmpty()){
            Meal meal = new Meal(dateTime, description, calories);
            dao.add(meal);
        }
        else{
            Meal meal = new Meal(Integer.parseInt(mealID), dateTime, description, calories);
            dao.update(meal);
        }
        response.sendRedirect("meals?action=list");
    }
}