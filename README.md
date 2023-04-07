# reggie
##拉取代码到本地
- 代码地址：https://github.com/Y-Sir2021/reggie_take_out.git
- 
  <img src="img/snipaste_20230407_113853.png" width = "800" height = "600" alt="1" align=center />
  <img src="img/snipaste_20230407_113947.png" width = "800" height = "600" alt="1" align=center />
- 创建自己的分支
  <img src="img/snipaste_20230407_113857.png"  alt="1" align=center />


## 快读运行
- 在mysql中执行sql/reggie.sql脚本
- 修改datasource各参数为自己的（端口，用户名，密码）
```yaml
  datasource:
    druid:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:13306/reggie?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true
      username: root
      password: abc123
```
- 运行ReggieApplication的main方法
我加的