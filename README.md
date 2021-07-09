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

### 1-2. Hello JPA - 애플리케이션 개발

#### JPA 구동 방식

![](https://i.ibb.co/4tcFNSb/bandicam-2021-07-09-21-03-21-547.jpg)

#### Member.java - 객체와 테이블을 생성하고 매핑

* `src/main/java/hellojpa/Member.java`

```java
package hellojpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Member {

    @Id
    private Long id;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

```

> 롬복 쓰고 싶다.

* `@Entity`: JPA가 관리할 객체
* `@Id`: 데이터베이스 PK와 매핑

#### 실습 - 회원 등록, 수정, 삭제, 단건 조회

```java
package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 회원 저장
            // Member member = new Member();
            // member.setId(2L);
            // member.setName("서정민");
            //
            // em.persist(member);

            // 회원 수정
            // Member findMember = em.find(Member.class, 1L);
            // findMember.setName("HelloJPA");

            // 회원 조회
            em.createQuery("select m from Member m", Member.class)
                    .getResultList().stream()
                    .map(Member::getName)
                    .forEach(System.out::println);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}

```

* 주의
    * **엔티티 매니저 팩토리**는 하나만 생성해서 애틀리케이션 전체에서 공유
    * **엔티티 매니저**는 쓰레드간에 공유X (사용하고 버려야 한다.)
    * **JPA의 모든 데이터 변경은 트랜잭션 안에서 실행**

#### JPQL

* 가장 단순한 조회 방법
    * EntityManager.find()
    * 객체 그래프 탐색(a.getB().getC())
* 나이가 18살 이상인 회원을 모두 검색하고 싶다면?
* JPQL
  ```
  em.createQuery("select m from Member m, Member.class)
  ```

* JPA를 사용하면 엔티티 객체를 중심으로 개발
* 문제는 검색 쿼리
* 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색
* 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
* 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요
* JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
* SQL과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
* **JPQL은 텐티티 객체**를 대상으로 쿼리
* **SQL은 데이터베이스 테이블**을 대상으로 쿼리
* 테이블이 아닌 **객체를 대상으로 검색하는 객체 지향 쿼리**
* SQL을 추상화해서 특정 데이터베이스 SQL에 의존X
* JPQL을 한마디로 정의하면 객체 지향 SQL

#### MN

이번 강의에서 배우는 내용들은 사실 다 아는 내용으므로 이상의 추가 내용은 없다.

단지 메이븐으로 빌드 설정을 하는 것에서 애를 많이 먹었다. 메이븐에 익숙치 않음과 버전의 차이로 구글링과 질문 게시판을 뒤지면서 해냈다.

추가로 강의에서 `EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");`코드만 작성하고 실행하면 강의상에서는 애플리케이션이 종료되지
않고 유지가 되었다. (영한님이 강제 종료를 시켰다.) 하지만 나는 계속 정상 종료가 되었다. (0번으로 끝남) 그래서 처음에 설정이 잘못 된줄 알았다.

버전이 바뀌면서 바뀐듯 한데 구글링과 질문 게시판을 뒤져봐도 확실한 정답을 못찾았다.   
    
```
Using Hibernate built-in connection pool
``` 



쨋든 이후 과정에서 `EntityManager`객체를 생성하고 `persist()`, `find()`, `createQeury`
과정은 익숙한 과정이므로 복습 차원으로 정리하는 시간을 가졌다.

### Note