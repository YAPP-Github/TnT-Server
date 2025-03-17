# TnT - 트레이너와 회원의 PT 관리 BE

- - -

<p align="center">
  <a href="https://codesandbox.io">
    <img src="document/image/main_banner.webp" alt="Main Banner">
  </a>
</p>

## 앱 다운로드

<p align="center">
  <a href="https://apps.apple.com/kr/app/tnt-%ED%8A%B8%EB%A0%88%EC%9D%B4%EB%84%88%EC%99%80-%ED%9A%8C%EC%9B%90%EC%9D%98-pt-%EA%B4%80%EB%A6%AC/id6741855834">
    <img src="https://img.shields.io/badge/Download%20on%20App%20Store-0070c9?style=for-the-badge&logo=apple&logoColor=white" alt="App Store">
  </a>
<br>
  <a href="https://play.google.com/store/apps/details?id=co.kr.tnt">
    <img src="https://img.shields.io/badge/Download%20on%20Google%20Play-34b7f1?style=for-the-badge&logo=googleplay&logoColor=green" alt="Google Play">
  </a>
</p>

## 서비스 소개

<p align="center">
  <a href="https://codesandbox.io">
    <img src="document/image/contents_first.webp" alt="Contents First">
  </a>
</p>

### **🧨** Problem: 각자의 문제는 다르지만 목표는 같은 우리, 트레이너와 트레이니 모두 효과적으로 PT를 지속할 수는 없을까? 🤨

| 🏋🏻 **트레이너(Trainer)** | 🏃🏻‍♂️ **트레이니(Trainee)** |
|------------------------|---------------------------|
| 많은 수의 회원을 관리하기 어려움     | 수업 외에 PT를 지속하기 힘듦         |
| 일정 정리의 번거로움            | 트레이너와 피드백 교환 과정이 번거로움     |

트레이너는 회원 개개인의 운동 진행 상황을 체계적으로 관리하기 어렵고, 효과적인 피드백을 제공하는 과정이 비효율적입니다.

반면, 트레이니는 수업 외에도 지속적인 운동 및 식단 관리를 원하지만 트레이너와의 원활한 소통이 쉽지 않아 PT 효과가 반감되는 경우가 많습니다.

트레이너와 트레이니 간의 **소통과 공유가 원활하지 않을수록, PT의 지속성과 효과는 점점 떨어질** 수밖에 없습니다. 😨

### **🧨** Solution:

> 💥 **트레이너와 트레이니의 상생 효과로 PT의 효능을 올려주는 서비스**

**트레이너**를 위해선 **PT 수업의 스케줄 및 회원 관리**, **트레이니**를 위해선 **수업 및 기록 관리**가 결합된 서비스가 필요합니다.
각 역할의 니즈에 맞게 PT 효과를 극대화하고자 트레이너와 트레이니, 두 가지 플로우를 다르게 개발하여 맞춤형 PT 관리 앱 서비스를 제공합니다.

| 🏋🏻‍♂️ **트레이너(Trainer)** | 🏋🏻‍♀️ **트레이니(Trainee)** |
|---------------------------|---------------------------|
| 회원 관리 효율화 및 일정 관리         | 기록 관리로 지속 가능한 운동 습관 형성    |

**우리는 트레이니도 쓰기 쉬운 트레이너 중심의 앱을 제공합니다.**

## AWS 아키텍처

![aws_architecture.jpg](document/image/aws_architecture.jpg)

## 기술 스택

| Category       | Stack                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
|----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Language**   | ![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| **Build Tool** | ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| **Framework**  | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring%20Security-6.4.1-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white) ![Spring WebFlux](https://img.shields.io/badge/Spring%20WebFlux-%236DB33F?style=for-the-badge&logo=spring&logoColor=white)                                                                                                                                                  |
| **Library**    | ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-%236DB33F?style=for-the-badge&logo=spring&logoColor=white) ![Querydsl](https://img.shields.io/badge/Querydsl-%236DB33F?style=for-the-badge)                                                                                                                                                                                                                                                                                                                                 |
| **Database**   | ![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white) ![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)                                                                                                                                                                                                                                                                                                                                           |
| **CI/CD**      | ![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white) ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white) ![SonarCloud](https://img.shields.io/badge/SonarCloud-F3702A?style=for-the-badge&logo=sonarcloud&logoColor=white) ![JACOCO](https://img.shields.io/badge/JACoCo-E34F26?style=for-the-badge) ![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white) _(현재 구성 중)_ |
| **API Docs**   | ![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white) ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)                                                                                                                                                                                                                                                                                                                                  |
| **Cloud**      | ![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| **Push Alarm** | ![FCM](https://img.shields.io/badge/FCM-%23FFCA28?style=for-the-badge&logo=firebase&logoColor=white)                                                                                                                                                                                                                                                                                                                                                                                                                                            |                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| **Logger**     | ![Log4j2](https://img.shields.io/badge/Log4j2-FF0000?style=for-the-badge)                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| **APM**        | ![Scouter](https://img.shields.io/badge/Scouter-000000?style=for-the-badge)                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |

## Backend Team 소개

|                                                    백엔드 개발                                                     |                                                       백엔드 개발                                                        |
|:-------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------:|
| <a href="https://github.com/ymkim97"><img src="https://github.com/ymkim97.png" width="140px;" alt="ymkim97"/> | <a href="https://github.com/fakerdeft"><img src="https://github.com/fakerdeft.png" width="140px;" alt="fakerdeft"/> |
|                                                      김영명                                                      |                                                         조만제                                                         |
