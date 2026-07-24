# UFS Client — Java SDK

[![CI](https://github.com/yesingle10/ufs-client/actions/workflows/ci.yml/badge.svg)](https://github.com/yesingle10/ufs-client/actions/workflows/ci.yml)
[![CodeQL](https://github.com/yesingle10/ufs-client/actions/workflows/codeql.yml/badge.svg)](https://github.com/yesingle10/ufs-client/actions/workflows/codeql.yml)
[![Release](https://img.shields.io/github/v/release/yesingle10/ufs-client?color=blue)](https://github.com/yesingle10/ufs-client/releases)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

UFS 统一文件服务的 Java SDK。封装 HTTP + JWT 鉴权，一行依赖即可调用 UFS 所有 API。

## 安装

**Maven** (`pom.xml`):

```xml
<dependency>
  <groupId>com.example.ufs</groupId>
  <artifactId>ufs-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

GitHub Packages 认证: 在 `~/.m2/settings.xml` 中配置 GITHUB_TOKEN。

## 快速开始

```java
UfsClient client = new UfsClient(new UfsClientConfig()
    .setBaseUrl("http://localhost:8080")
    .setAppId("app_demo")
    .setAppSecret("secret_demo")
    .setTenantId("t_demo"));

// 存储桶操作
client.bucket().list().forEach(b -> System.out.println(b.getName()));
client.bucket().create("photos", "cos", "ap-guangzhou", "private-read");

// 预签名上传
PresignResult presign = client.object()
    .presignUpload("photos", "vacation.jpg", "vacation.jpg", "image/jpeg", 5 * 1024 * 1024);

// 策略管理
client.policy().listActive().forEach(p -> System.out.println(p.getBizType()));

// 健康检查
Map<String, Object> status = client.health().check();
System.out.println(status.get("status")); // "UP"
```

## 模块

| 类 | 说明 |
|----|------|
| `UfsClient` | SDK 入口，聚合所有 API |
| `BucketApi` | 存储桶 CRUD + 列表 |
| `ObjectApi` | 预签名上传/下载、分片、列表、删除 |
| `PolicyApi` | 存储策略管理（bizType 路由） |
| `HealthApi` | 健康检查 |

## 鉴权流程

1. 客户端通过 `appId` + `appSecret` → POST `/api/v1/auth/token` 获取 JWT
2. JWT 自动缓存，提前 60 秒刷新
3. 后续请求自动携带 `Authorization: Bearer <token>`

## 测试

```bash
mvn test  # 23 tests, MockWebServer
```

完整示例: [ufs-sdk-example](https://github.com/yesingle10/ufs-sdk-example)
