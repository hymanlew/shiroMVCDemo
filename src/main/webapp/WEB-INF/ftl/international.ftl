
<html>
<head>
    <title>国际化配置</title>

    <!-- 一定要导入 spring.ftl，spring 集成的国际化配置文件 -->
	<#import "spring.ftl" as spring>
</head>
<body>

<!-- 或者使用 <@spring.message code="username" /> -->
<@spring.message "username" />

<!-- arg是一个在 freemarker 中定义的数组，包含了占位符{0},{1}对应的参数 -->
<#assign arg = ["我的首页","张三"]>
<@spring.messageArgs "title" arg />

<script type="text/javascript">



</script>

</body>
</html>

<#--

1，============================================
freemarker 实现国际化信息显示，是使用自定义指令 <@spring> 实现，并且该自定义的指令文件是 springmvc 提供的文件，所以直接使用即可。

1.首先定义2个国际化配置文件
messages_zh_CN.properties：
username=用户名
title=欢迎来到{0},{1}!

messages_en_US.properties：
username=UserName
title=welcome to {0},{1}!

2. 编辑前端 ftl 模板，需要注意的点：
1)一定要引入 spring.ftl
2)<@spring.messageArgs "xxx" arg /> ，第二个参数是一个 freemarker 数组，需要先用<#assign>指令定义好，spring.ftl 还定义了其他
	的宏，大家照葫芦画瓢，也就会用了。


4，=======================================








-->