<%--
  Created by IntelliJ IDEA.
  User: TTong
  Date: 16-1-8
  Time: 下午5:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>

<html>
<head>
    <title>用户注册</title>
</head>
<body>

<c:if  test="${isPost == true}">
    <form:errors path="user1.*" />
</c:if>

<form:form method="post">
    <table>
        <tbody>
        <tr>
            <td>用户名：</td>
            <td><input type="text" name="name" value="${user.name}" placeholder="请在此处输入用户名" /> </td>
        </tr>
        <tr>
            <td>密码：</td>
            <td><input type="text" name="passwd" value="${user.passwd}" placeholder="请在此处输入密码" /> </td>
        </tr>
        <tr>
            <td>生日：</td>
            <td><input type="text" name="birthday"
                    <fmt:formatDate pattern="yyyy-MM-dd" var="myBirthday" type="date" value="${user.birthday}" />
                       value="${myBirthday}" placeholder="请在此处输入生日" /> </td>
        </tr>
        <tr>
            <td>Email：</td>
            <td><input type="text" name="email" value="${user.email}" placeholder="请在此处输入Email" /> </td>
        </tr>
        <tr>
            <td>性别：</td>
            <td>
                <input type="radio" name="gender" <c:if test='${user.gender == "男"}'>checked</c:if> id="g1" value="男" />
                <label for="g1">男</label>
                <input type="radio" name="gender" <c:if test='${user.gender == "女"}'>checked</c:if> id="g2" value="女" />
                <label for="g2">女</label>
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td>
                <input type="submit" value="注册" />
                <input type="reset" value="重填" />
            </td>
        </tr>
        </tbody>
    </table>
</form:form>
</body>
</html>