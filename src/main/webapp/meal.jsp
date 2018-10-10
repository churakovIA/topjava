<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<ul>
    <li><a href="meals">Meals</a></li>
</ul>
<h2>Meal</h2>
<form method="post" action="meals" name = "mealEdit">
    <c:if test="${!empty meal.id}">
    Id : <input type="hidden" readonly="readonly" name="id" value="<c:out value="${meal.id}" />" /> <br />
    </c:if>
    Дата/Время : <input type="datetime-local" name="dateTime" value="<c:out value="${meal.dateTime}" />" /> <br />
    Описание : <input type="text" name="description" value="<c:out value="${meal.description}" />" /> <br />
    Калории : <input type="text" name="calories" datatype="number" value="<c:out value="${meal.calories}" />" /> <br />
    <input type="submit" value="Сохранить">
</form>
</body>
</html>