# 1 系统介绍

本系统为基于 Websocket 的信息转发工具，解决客户端没有公网IP，但需要实时接收服务器发来信息的场景。

下载源码后，可以集成到其它项目中。

示例集成项目[Bark 转发处理服务](https://github.com/bark-processor)

## 1.1 系统组成

该系统包含以下两个部分：

- 服务端：管理客户端，接收客户端的连接，向各接收端分发消息。
- 客户端：连接服务端，接收来自服务端的信息。

## 1.2 系统示意图

# 2 技术说明

## 2.1 系统项目介绍

该系统由Maven进行管理，包含 3 个 model：

- websocket-forward-client：客户端
- websocket-forward-server：服务端
- websocket-forward-utils：项目工具包

其中，客户端和服务端依赖 websocket-forward-utils，因此如果运行项目时提示找不到相应类时，需要手动安装依赖到本地 Maven 仓库。

```sh
cd /path/to/project # 进入项目目录
cd websocket-forward-utils # 进入工具包目录

# 以下安装方式二选一
# 安装到本地 Maven 仓库（同时安装源码）
mvn source:jar install 
# 安装到本地 Maven 仓库（不安装源码）
mvn install
```

## 2.2 使用场景

- 客户端没有公网IP，但是需要被动接收信息。
- 客户端需要实时接收来自服务端发送的消息，且对消息传递时效性要求比较高。
- 不使用轮询的方式进行查询。

# 3 系统运行

## 3.1 客户端

客户端打包完成后会生成 .jar 包，该 .jar 包可直接运行。

```sh
java -jar websocket-forward-client.jar
```

## 3.2 服务端

服务端打包完成后会生成 .jar 包，该 .jar 包可直接运行。

```sh
java -jar websocket-forward-server.jar
```

# 4 系统配置

## 4.1 服务端配置

```yaml
# 服务端配置
websocket-server:
  response:
    time-out: 20000 # 服务端等待客户端回复报文的时间(单位：毫秒)

# 允许的客户端列表
# 只有在列表里的客户端才可以连接
websocket-client:
  list:
    - id: TEST_CLIENT_ID # 客户端 ID
      key: TEST_CLIENT_KEY # 客户端密钥
      description: "客户端描述" # 客户端描述
      # 是否由服务器定时发送 ping-pong 信息进行保活 (默认不开启)
      # keep-alive: true
```

## 4.2 客户端配置

```yaml
# 服务端配置
websocket-server:
  # 服务器 URL (http->ws; https->wss)
  url: ws://localhost:8080/exchange
  # 连接服务端失败后等待多少秒后重试 (默认 5 秒)
  reconnect-wait: 5

# 客户端配置
websocket-client:
  id: TEST_CLIENT_ID # 客户端ID
  security-key: TEST_CLIENT_KEY # 客户端密钥
```

## 4.3 反向代理配置

如有需要，可以为转发端或接收端配置反向代理。

以下以 Nginx 配置为例：

```
# Websocket 端点转发
location /exchange {
  proxy_pass  http://127.0.0.1:8080; # 转发端或接收端的 URL
  proxy_redirect off;
  proxy_set_header  Host $host:$server_port;
  proxy_set_header  X-Real-IP  $remote_addr;
  proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
  add_header X-Frame-Options "SAMEORIGIN";
}

# 发送消息服务
location /send {
  proxy_pass  http://127.0.0.1:8080; # 转发端或接收端的 URL
  proxy_redirect off;
  proxy_set_header  Host $host:$server_port;
  proxy_set_header  X-Real-IP  $remote_addr;
  proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
  add_header X-Frame-Options "SAMEORIGIN";
}
```

# 5 示例 API 说明

## 5.1 发送信息

```
GET /send/{客户端ID}
请求参数：
- text: 要发送给客户端的文本
- requireReply: 是否有要求客户端收到消息后回复。"true"-是 "false"-否
```

# 6 TODO

## 6.1 连接加密验证

`WebsocketClientUtil.verifyClient()`

目前，客户端加入验证只验证服务端和客户端配置的key字符串是否一致，后续可以引进一次性密码(HOTP)等验证方式

