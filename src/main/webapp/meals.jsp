<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://javaops.ru/timeUtil" prefix="f" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<h2>Meals</h2>
    <p><a href="meals?action=insert">Добавить еду</a></p>
    <table border=1>
        <tr>
            <th>Дата/Время</th>
            <th>Описание</th>
            <th>Калории</th>
            <th>ID</th>
            <th>Редактировать</th>
            <th>Удалить</th>
        </tr>
            <c:forEach items="${meals}" var="meal">
                <tr style = "${meal.exceed ? 'color:red' : 'color:green'}">
                    <%--
                    <td><fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${localDateTimeFormat.parse(meal.dateTime)}" /></td>
                    --%>
                    <td>${f:formatLocalDateTime(meal.dateTime, 'yyyy-MM-dd HH:mm')}</td>
                    <td>${meal.description}</td>
                    <td>${meal.calories}</td>
                    <td>${meal.id}</td>
                    <td><a href="meals?action=edit&id=<c:out value="${meal.id}"/>">Редактировать</a></td>
                    <td><a href="meals?action=remove&id=<c:out value="${meal.id}"/>">Удалить</a></td>
                </tr>
            </c:forEach>
    </table>
</body>
</html>