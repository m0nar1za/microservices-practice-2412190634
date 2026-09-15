# 作业01：开发环境与个人仓库

## 环境检查

- Java: 
openjdk 26.0.2 2026-07-21
OpenJDK Runtime Environment(build 26.0.2+10-2-26.04.2-Ubuntu)
OpenJDK 64-Bit Server VM (build 26.0.2+10-2-26.04.2-Ubuntu,mixed mode，sharing)
- Maven: 
Apache Maven3.9.12 (848fbb4bf2d427b72bdb2471c22fced7ebd9a7a1)
Mavenhome:/mnt/c/Maven/apache-maven-3.9.12
Javaversion:26.0.2,,vendor: Ubuntu,runtime:/usr/lib/jvm/java-26-openjdk-amd64
Default locale: en，platform encoding:UTF-8
0S name: "linux"，version:"6.18.33.2-microsoft-standard-wsl2", arch:"amd64", family: "unix"
- Git: 
git version 2.53.0
- Docker: 
Client:
 Version:           29.8.0
 API version:       1.56
 Go version:        gol.26.8
 Git commit:        88096ef
 Built:             Thu Sep 3 21:49:51 2026
 OS/Arch:           linux/amd64
 Context:           default

Server: Docker Desktop 4.91.0 (239619)
 Engine:
  Version:          29.8.0
  API version:      1.56 (minimum version 1.40)
  Go version:       go1.26.8
  Git commit:       3ce5872
  Built:            Thu Sep  3 21:51:20 2026
  OS/Arch:          linux/amd64
  Experimental:     false
 containerd:
  Version:          v2.3.4
  GitCommit:        db8809540e1a7a9da5d518876894933ff55692ab
 runc:
  Version:          1.4.3
  GitCommit:        v1.4.3-0-gbb14dabe
 docker-init:
  Version:          0.19.0
  GitCommit:        de40ad0
- Docker Compose: 
Docker Compose version v5.5.1

## 概念回答

**1. 什么是微服务架构？**
微服务架构是一种将单一应用程序划分为一组小型服务的架构风格。每个服务都运行在自己的进程中，围绕业务能力构建，通过轻量级机制（如 HTTP/REST API）进行通信，并且可以独立部署和扩展。

**2. 微服务和单体架构的主要区别是什么？**
- 单体架构：所有功能打包在一个应用里，统一部署，共享数据库，技术栈单一，初期开发快但后期扩展困难。
- 微服务架构：按业务拆分为多个独立服务，分布式部署，每个服务有独立数据库，技术栈灵活，扩展性强但运维和分布式复杂度高。

**3. 为什么本课程先实现单体系统，再逐步拆分为微服务？**
先做单体是为了快速理清业务逻辑和边界，降低早期开发的复杂度和沟通成本。如果一开始就上微服务，很容易因为业务边界划分不清而导致过度设计。先单体再拆分，能让我们在业务稳定后，平滑地演进架构。

**4. 为什么作业需要提供可重复运行的测试或验证脚本？**
微服务引入了分布式系统的复杂性（网络延迟、服务故障、数据一致性等）。通过可重复运行的自动化测试或验证脚本，可以确保每次修改代码后系统功能依然正常，降低集成风险，同时保证不同环境下的验证结果一致。

## 问题记录

- 当前系统：Windows 11 + WSL 2 + Ubuntu 26.04
- 遇到的问题：无
- 解决计划：无
