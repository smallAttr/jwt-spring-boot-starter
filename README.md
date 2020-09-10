[TOC]

快速接入json web token认证, 无侵入接入


## 提供可选配置字段参考
- secretKey: 安全认证key
- antPattern: 需要安全认证的url（默认拦截所有请求 如：`/**`）
- ignoreAntPatterns: 不需要安全认证的url（默认放行的url有：`/resources/**`、`/static/**`）
- algorithm: 加密算法（默认为：`SignatureAlgorithm.HS256`）


## 快速生成JWT
[在线JWT生成网站](https://jwt.io/)


## 获取JWT中属性
```
ServicePrincipalProvider.getAttribute("operatorId", Integer.class)
```
