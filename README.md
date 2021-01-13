# jspBB

jspBB是一款基于Java/Kotlin的免费、开源论坛(问答)系统，仿stackoverflow、quora、知乎的模式，以GPL-2协议开源。使用SpringBoot 2、Mybatis、TypeScript、React、Ant Design 4、Ant Design Pro 5、Thymeleaf、Bootstrap 4等技术开发。

jspBB这个名字为了致敬phpBB，系统中并没有使用jsp。当初php、asp、jsp三大互联网技术，唯有jsp一直没有重量级的开源论坛，希望jspBB可以弥补这一缺憾。

系统使用SpringBoot2，可以用jar方式启动。后台前端使用React、Ant Design 4、Ant Design Pro 5，基于React Hooks模式。考虑到搜索引擎友好，前台前端使用Thymeleaf、Bootstrap 4，比Freemarker更好用。后续会提供前台前端的API接口，使前台前端也可以使用React、Vue等技术。

系统中应用类代码用Kotlin开发，工具类的代码用Java开发（考虑到要在其它项目复用）。Kotlin是高效、安全的开发语言，100%兼容Java，编译后依旧是class文件，依旧运行在JDK中，无需依赖任何其它环境。Kotlin和Java可以相互调用，开发时可以Java、Kotlin混搭，并不需要全部都用Kotlin。在Android中Kotlin已经作为首选开发语言，Spring也早早的加入了Kotlin的支持。

实际开发中，Kotlin的空值安全处理，可以大大的增强代码安全性，不用随时随地考虑null值的问题；类似动态语言的语法糖，可以做到想怎样就怎样，代码量少。没有java开发时那种为什么不能这样、为什么不能那样的苦恼，一个简单的想法，需要多行代码才能实现。

* 官网地址：[http://www.jspxcms.com](http://www.jspxcms.com)
* 官方论坛：[http://jspbb.jspxcms.com](http://jspbb.jspxcms.com)
* 程序包下载地址：[http://www.jspxcms.com/download/](http://www.jspxcms.com/download/)
* github源码托管地址：[https://github.com/ujcms/jspbb](https://github.com/ujcms/jspbb)
* gitee源码托管地址：[https://gitee.com/jspxcms/jspbb](https://gitee.com/jspxcms/jspbb)

QQ交流群：626599871（Jspxcms交流群(三)）

## 前台功能

注册(阿里短信,邮箱)、登录、找回密码、第三方登录(QQ,微信,微博)、第三方存储(腾讯云,七牛云,阿里云)、提问(修改,删除)、回答(修改,删除)、评论(修改,删除)、搜索、支持Markdown(贴图)、支持敏感词过滤。

![](https://res.jspxcms.com/uploads/1/image/public/202101/20210107214742_3mrkomemla.png)

## 后台功能

* 设置：基础设置、注册设置、邮箱设置、上传设置、水印设置、约束设置、敏感词
* 用户：角色管理、用户组管理、用户管理
* 内容：问题管理、回答管理、评论管理
* 系统：短信日志、访问日志

后台列表页面，支持任意字段搜索、排序，隐藏或显示。

![](https://res.jspxcms.com/uploads/1/image/public/202101/20210107215006_ka47msn85q.png)

后台修改页面，支持上一条、下一条显示和编辑，不用列表页、编辑页来回切换。

![](https://res.jspxcms.com/uploads/1/image/public/202101/20210107215149_lht4tlyj3s.png)

## 环境要求

* JDK8。
* Servlet3.1或更高版本（如Tomcat8.5或更高版本）。
* MySQL5.5或更高版本（如需使用MySQL5.0，可将mysql驱动版本替换为5.1.24）。[如何连接MySQL8数据库](https://www.jspxcms.com/documentation/487.html)
* 浏览器：IE11、Edge、Firefox、Chrome。

## 技术栈

* Kotlin/Java
* Mybatis
* SpringBoot 2
* Shiro
* Thymeleaf
* Bootstrap 4
* React
* TypeScript
* Ant Design 4
* Ant Design Pro 5

## 开发环境搭建步骤

* Maven3.3 或更高版本。并配置阿里云Maven仓库镜像。
* IntelliJ IDEA 2018.3 或更高版本。需Kotlin1.3支持。
* 通过pom.xml导入项目。等待jar包下载完成。
* 在MySQL中创建数据库，字符集选择`utf8mb4`。
* 在数据库中执行建库脚本`/database/mysql_schema.sql`和初始化数据`/database/mysql_init.sql`。
* 打开`src/main/resources/application.properties`，修改数据库连接、用户名、密码：`spring.datasource.url` `spring.datasource.username` `spring.datasource.password`。
* 点击 IDEA 右上角 Run 按钮，启动程序。
* 前台地址：http://localhost:8080
* 默认用户名：admin，默认密码：password。
* 后台前端基于react开发，要在前端开发工具中启动才能访问。请另外下载jspbb-cp项目。
* jspbb-cp项目github地址：[https://github.com/ujcms/jspbb-cp](https://github.com/ujcms/jspbb-cp)
* jspbb-cp项目gitee地址：[https://gitee.com/jspxcms/jspbb-cp](https://gitee.com/jspxcms/jspbb-cp)

## GPL-2 简介

### 权利

* 可以免费使用，包括个人和商用。
* 可以修改源代码。

### 限制

* 修改源代码后，如需分发，则必须以`GPL-2`协议开源。
* 分发是指提供程序给别人。包括修改源代码后，另外成立一个新的项目；或者作为公司的一个软件产品。

### 常见问题

-----------------------------------------

Q：我修改源代码后，自己用不给别人，要不要开源？

A：不需要。

-----------------------------------------

Q：我修改源代码后，给甲方做了一个网站，要不要开源？

A：程序给了谁，就要针对谁开源，但可以不用全网开源。得到程序的甲方可以要求你以`GPL-2`协议免费提供源代码，并可公开以`GPL-2`协议发布到网上。但没有得到程序的第三方，没有权利要求你提供源代码。

-----------------------------------------

Q：我给甲方做网站（无论是否修改代码），是否可收费？

A：可以收费，但收的是技术服务费，不是软件的版权费。

-----------------------------------------

Q：我修改源代码后，是否可以另外成立一个新项目。

A：可以。但必须以`GPL-2`协议开源。

-----------------------------------------

Q：有哪些开源软件使用`GPL-2`协议？

A：Linux、MySQL、OpenJDK、phpBB、WordPress等知名开源软件都使用`GPL-2`协议。

-----------------------------------------
