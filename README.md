# 자바 ORM 표준 JPA 프로그래밍 - 기본편

## 1. JPA 시작하기

### 1-1. Hello JPA - 프로젝트 생성

#### H2 데이터베이스 설치와 실행

* http://www.h2database.com/html/main.html
* 최고의 실습용 DB
* 가볍다 (1.5M)
* 웹용 쿼리툴 제공
* MySQL, Oracle 데이터베이스 시뷸레이션 기능
* 시퀸스, AUTO INCREMENT 기능 지원

#### 메이븐

##### 메이븐 소개

* 자바 라이브러리, 빌드 관리
* 라이브러리 자동 다운로드 및 의존성 관리
* 최근에는 그래들(Gradle)이 점점 유명

##### pom.xml

* `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>jpa-basic</groupId>
    <artifactId>ex1-hello-jpa</artifactId>
    <version>1.0.0</version>

    <dependencies>
        <!-- JPA 하이버네이트 -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-entitymanager</artifactId>
            <version>5.5.3.Final</version>
        </dependency>

        <!-- H2 데이터베이스 -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>1.4.200</version>
        </dependency>
    </dependencies>

    <properties>
        <maven.compiler.source>14</maven.compiler.source>
        <maven.compiler.target>14</maven.compiler.target>
    </properties>

</project>
```

#### JPA 설정하기 - persistence.xml

* JPA 설정 파일
* /META-INF/peresistence.xml 위치
* persistence-unit name으로 이름 지정
* javax.persistence로 시작: JPA 표준 속성
* bibernate로 시작: 하이버네이트 전용 속성

##### persistence.xml

* `src/main/resources/META-INF/persistence.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">
    <persistence-unit name="hello">
        <properties>
            <!-- 필수 속성 -->
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
            <property name="javax.persistence.jdbc.user" value="sa"/>
            <property name="javax.persistence.jdbc.password" value=""/>
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>

            <!-- 옵션 -->
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
            <property name="hibernate.use_sql_comments" value="true"/>
            <!--<property name="hibernate.hbm2ddl.auto" value="create" />-->
        </properties>
    </persistence-unit>
</persistence>
```

##### 데이터베이스 방언

* JPA는 특정 데이터베이스에 종속 X
* 각각의 데이터베이스가 제공하는 SQL 문법과 함수는 조금씩 다름
    * 가변 문자: MySQL은 VARCHAR, Oracle은 VARCHAR2
    * 문자열을 자르는 함수: SQL 표준은 SUBSTRING(), Oracle은 SUBSTR()
    * 페이징: MySQL은 LIMIT, Oracle은 ROWNUM
* 방언: SQL 표준을 지키지 않는 특정 데이터베이스만의 고유한 기능
* `hibernate.dialect`속성에 지정
    * H2: `org.hibernate.dialect.H2Dialect`
    * Oracle 10g: `org.hibernate.dialect.Oracle10gDialect`
    * MySQL: `org.hibernate.dialect.MySQL5InnoDBDialect`
* 하이버네이트는 40가지 이상의 데이터베이스 방언 지원

#### MTH

`Gradle`방식이 아닌 `Maven`으로 빌드 관리를 하는것은 처음이다. (tag 알러지가 있다...)

우선 `pom.xml`을 살펴보면 스프링 부트 없이 순수하게 하이버네이트만 가져왔다. 그렇기에 버전을 따로 표기를 해주었다. (기억이 맞다면 `starter-data-jpa`에서 의존하는 라이브러리들을 모두 포함하는
것으로 안다.)

> 참고로 현재 프로젝트는 스프링을 전혀 쓰지 않는다.

* pom.xml

  ![](https://i.ibb.co/dWwhw5s/bandicam-2021-07-09-18-06-20-114.jpg)

* build.gradle

  ![](https://i.ibb.co/YL8nPwF/bandicam-2021-07-09-18-07-03-060.jpg)

* 당연한 결과이지만 core, persistence 등등 핵심적인 라이브러리들을 확인할 수 있다.

##### dialect 설정

`persistence.xml`설정을 하는 과정에서 데이터베이스 방언의 개념과 함께 `dialect`을 설정하는 부분이 나온다.   
하지만 이전까지는 DB 방언에 대해서 들은 것이 없다. (설정을 고려조차 하지 않았다.) 알고보니 이전에는 JPA를 Springboot와 함께 사용을 해서 SpringBoot가 자동으로 설정을 해주었다. (그래서
개념 조차 모랐던것...)