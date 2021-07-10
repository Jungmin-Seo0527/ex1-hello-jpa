# 자바 ORM 표준 JPA 프로그래밍 - 기본편

## 3. 엔티티 매핑

### 3-1. 객체와 테이블 매핑

#### @Entity

* `@Entity`가 붙은 클래스는 JPA가 관리, 엔티티라 한다.
* JPA를 사용해서 테이블과 매핑할 클래스는 `@Entity`필수
* **주의**
    * **기본 생성자 필수** (파라미터가 없는 public protected 생성자)
    * final 클래스, enum, interface, inner 클래스 사용x
    * 저장할 필드에 final 사용 x

##### @Entity 속성

* `name`
    * JPA에서 사용할 엔티티 이름을 지정한다.
    * 기본값: 클래스 이름을 그대로 사용
    * 같은 클래스 이름이 없으면 가급적 기본값을 사용한다.

#### @Table

* `@Table`은 엔티티와 매핑할 테이블 지정
* `name`: 매핑할 테이블 이름
* `catalog`: 데이터베이스 catalog 매핑
* `schema`: 데이터베이스 schema 매핑
* `uniqueConstraints(DDL)` DDL 생성 시에 유니크 제약 조건 생성

#### MN

기본 생성자가 필요한 이유에 대한 의문이 풀렸다. 기본적으로 JPA 뿐만 아니라 라이브러리, 프레임워크가 특정 객체에 접근하기 위해서 해당 객체의 기본 생성자가 필요하다고 한다.

### 3-2. 데이터베이스 스키마 자동 생성

* DDL을 애플리케이션 실행 시점에 자동 생성
* 테이블 중심 -> 객체 중심
* 데이터베이스 방언을 활용해서 데이터베이스에 맞는 적절한 DDL 생성
* 이렇게 **생성된 DDL은 개발 장비에서만 사용**
* 생성된 DDL은 운영서버에서는 사용하지 않거나, 적절히 다듬은 후 사용
* 속성
    * `hibernate.hbm2ddl.auto`
    * `create`: 기존 테이블 삭제 후 다시 생성(DROP + CREATE)
    * `create-drop`: create와 같으나 종료시점에 테이블 DROP
    * `update`: 변경분만 반영(운영 DB에서는 사용하면 안됨)
    * `validate`: 엔티티와 테이블이 정상 매핑되었는지만 확인
    * `none`: 사용하지 않음

#### 데이터베이스 스키마 자동 생성 - 주의

* **운영 장비에는 절대 create, create-drop, update 사용하면 안된다.**
* 개발 초기 단계는 create 또는 update
* 테스트 서버는 update 또는 validate
* 스테이징과 운영 서버는 validate 또는 none
* 개발 단계를 제외하고는 validate 정도만 허용해주고 왠만하면 none으로 설정하는 것이 좋다.

#### DDL 생성 기능

* 제약 조건 추가: 회원 이름은 **필수**, 10자 초과
    * `@Column(nullable = false, length = 10)`
* 유니크 제약 조건 추가

    ```
    @Table(uniqueCostraints = {@UniqueConstraint(name = "NAME_AGE_UNIQUE",
                                                 colmnNames={"NAME", "AGE"})})
    ```

* **DDL 생성 기능은 DDL을 자동 생성할 때만 사용되고 JPA의 실행 로직에는 영향을 주지 않는다.**

#### MN

이전 프로젝트의 `application.yml`을 보면 `yml`방식으로 데이터 스키마 자동 생성 설정하는 방법을 알수 있다.

```yaml
spring:
  datasource:
    url: jdbc:h2:tcp://localhost/~/jpashop;
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
        #        show_sql: true
        format_sql: true
        default_batch_fetch_size: 100


logging:
  level:
    org.hibernate.SQL: debug
```

여기서 `ddl-auto:`가 직관적으로 ddl을 자동 생성하는 설정이라는 것을 알 수 있다.

현재 프로젝트인 `persistence.xml`에서는 아래와 같이 작성한다.

```xml

<property name="hibernate.hbm2ddl.auto" value="create"/>

```

> YML? XML?

# Note