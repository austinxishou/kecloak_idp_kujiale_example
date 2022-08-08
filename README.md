# Keycloak自定义实现第三方登录

## 项目说明

由于对接的第三方IDP不一定都是标准的openid connect实现,所以都需要根据第三方的Oauth文档进行定制;
Keycloak对于新增Social IDP的实现,都是标准,以及灵活的;
我们完全可以参照 Keycloak 本身已实现的Github LinkedIn等,快速实现我们的需求;
我们这里以酷家乐的Oauth2 接口进行说明

参考文章: https://blog.csdn.net/austindev/article/details/119113923