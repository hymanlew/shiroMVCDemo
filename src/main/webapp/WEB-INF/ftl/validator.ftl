
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%
  String id = request.getParameter("id");
  String name = request.getParameter("name");
%>
<html>
<head>
    <title>Title</title>
</head>
<body>

<form:errors path="user.name"></form:errors>
<form:form method="post" modelAttribute="user" action="${pageContext.request.contextPath }/freemarker/addUser2">
    账号：<input type="text" name="name" value=${user.account}>
    <form:errors path="name"></form:errors>
    <br />
    密码：<input type="password" name="passwd">
    <form:errors path="passwd"></form:errors>
    <br />
    手机号：<input type="text" name="phone" value=${user.phone}>
    <form:errors path="phone"></form:errors>
    <br />
    邮箱：<input type="email" name="email" value="${user.email}">
    <form:errors path="email"></form:errors>
    <br />
    用户昵称：<input type="text" name="userName" value=${user.userName}>
    <form:errors path="userName"></form:errors>
    <br />
    <input type="submit" name="submit" value="注册">
</form:form>

<br>===========================================================================
<br>===========================================================================

<form action="${pageContext.request.contextPath }/freemarker/addUser3" method="post" >
    <div class="layui-form-item">
        <label class="layui-form-label">姓名</label>
        <div class="layui-input-block">
            <input type="text" name="name" autocomplete="off" placeholder="zhangsan" class="layui-input" th:value="${emp!=null}?${emp.name}" required lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">密码框</label>
        <div class="layui-input-block">
            <input type="password" name="password" placeholder="请输入密码" autocomplete="off" class="layui-input" th:value="${emp!=null}?${emp.password}" required lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">性别</label>
        <div class="layui-input-block">
            <input type="radio" name="sex" value="1" title="男" th:checked="${emp!=null && (emp.sex==null || emp.sex==1)?true:false}">
            <input type="radio" name="sex" value="0" title="女" th:checked="${emp!=null}?${emp.sex==0}">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">年龄</label>
        <div class="layui-input-block">
            <input type="text" name="age" placeholder="年龄" autocomplete="off" class="layui-input" th:value="${emp!=null}?${emp.age}" required lay-verify="number">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">出生日期</label>
        <div class="layui-input-block">
            <input type="text" id="birth2" name="birth" placeholder="2018/01/01" autocomplete="off" class="layui-input" th:value="${emp!=null}?${#dates.format(emp.birth, 'yyyy/MM/dd')}" required lay-verify="date">
        </div>
    </div>

    <div class="layui-form-item">
        <div class="layui-input-block">
            <input type="submit" value="提交"/>
        </div>
    </div>
</form>

</body>
</html>
