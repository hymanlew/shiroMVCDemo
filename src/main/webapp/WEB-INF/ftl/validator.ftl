
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
            <input type="text" id="birth2" name="birth" placeholder="2018/01/01" autocomplete="off" class="layui-input" th:value="${emp!=null}?${dates.format(emp.birth, 'yyyy/MM/dd')}" required lay-verify="date">
        </div>
    </div>

    <div class="layui-form-item">
        <div class="layui-input-block">
            <input type="submit" value="提交"/>
        </div>
    </div>
</form>

    <#-- FreeMarker 中文官方参考手册，http://freemarker.foofun.cn/toc.html -->
    ${(obj.item)!}
    JSON.stringify(${cate!}

    <#if entity?? && (entity.tec)??> ${entity.tec} </#if>

    <#if info?? && (info.labelId??)>
    <#else>
    </#if>

    <#list categoryList as clist>
        <#-- （categoryId 是从控制器传递过来的，并且在 freemarker 的条件判断中不需要使用 ${} 接收）-->
        <#if (clist.categoryId) = categoryId!></#if>

        <#list clist.taglist as tlist></#list>

        <#-- 判断是否含有某些字符串。-->
        <#if cate?? && (cate?index_of(te)>-1)></#if>
    </#list>

    <#--
    freemarker img 标签自定义指令：

    <@user_def_dir_exp param1=val1 param2=val2 ... paramN=valN/>
    user_def_dir_exp：   表达式算作是自定义指令(比如宏)，将会被调用（在加载当前页面时，会调用自定义指令的组件，SingleImageDirective 实现了指令方法）。
    param1，param2等...： 参数的名称，它们不是 表达式。参数的数量可以是0(也就是没有参数)。
    val1，val2等...：     参数的值，它们 是 表达式。
    lv1，lv2等...：       循环变量 的名称， 它们 不是 表达式。

    参数的顺序并不重要(除非你使用了位置参数传递)。参数名称必须唯一。在参数名中小写和大写的字母被认为是不同的字母 (也就是 Color 和 color 是不同的)。
    -->
    <img id="upload" src="<@cust_freemark_image value='${(icon)!}' default='images/a.png'/>" onerror="javascript:this.src='${StaticPath}images/b.png';this.onerror=null;">
<#--


取得list的长度：${fields?size}。 判断其长度：<#if (fields?size>0) >。

assign 用于为该模板页面创建或替换一个顶层变量,或者创建或替换多个变量等.
用FreeMarker来解析json数据（eval）:
<#assign text="{'name':'opal','age':'30+','addr':'上海上海'}" />
<#assign data=text?eval />
<#assign ncnt=10000 />
data.name=${data.name} <br/>
<@timeuse var="tm">（timeuse 内部函数指令）
<#list 1..ncnt as t>（从 1 开始，如果遍历的对象是集合，则需要声明为下标 0）
<#assign data=text?eval />
</#list>
</@timeuse>
解析json数据${ncnt}次共耗时:${tm}秒

<#if stringshuzu?? && (stringshuzu?size>0)>
	<#list stringshuzu as item>
		<#assign data=item?eval />
			<#list data as dt>
${(dt.id)!}
  </#list>
	</#list>
</#if>
解释：
双问号是非空判断，true 执行代码，false 则不执行。

-->
</body>
</html>
