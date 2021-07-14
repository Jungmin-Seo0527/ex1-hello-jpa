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

### 3-3. 필드와 컬럼 매핑

#### Member.java (수정) - 요구사항 추가

* 요구사항
    * 회원은 일반 회원과 관리자로 구분해야 한다.
    * 회원 가입일과 수정일이 있어야 한다.
    * 회원을 설명할 수 있는 필드가 있으야 한다 이 필드는 길이 제한이 없다.

```java
package hellojpa;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Member {

    @Id
    private Long id;

    @Column(name = "name")
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    private LocalDate testLocalDate;
    private LocalDateTime testLocalDateTime;

    @Lob
    private String description;

    public Member() {
    }
}

```

#### 매핑 어노테이션 정리

* `hibernate.hbm2ddl.auto`
* `@Column`: 컬럼 매핑
* `@Temporal`: 날짜 타입 매핑
* `@Enumerated`: enum 타입 매핑
* `@Lob`: BLOB, CLOB 매핑
* `@Transient`: 특정 필드를 컬럼에 매핑하지 않음(매핑 무시)

##### @Column

* `name`: 필드과 매핑할 테이블의 컬럼 이름
    * default: 객체의 필드 이름
* `insetable`, `updatable`: 등록, 변경 가능 여부
    * default: TRUE
* `nullable(DDL)`: null 값의 허용 여부를 설정
    * false: DDL 생성 시에 not null 제약 조건이 붙는다.
* `unique(DDL)`: `@Table`의 `uniqueConstraints`와 같지만 한 컬럼에 간단히 유니크 제약조건을 걸 때 사용한다.
    * 제약명이 알아보기 힘들다.
    * `@Table(uniqueConstraints = {@UniqueConstraint(name = "NAME_AGE_UNIQUE", columnNames = {"NAME", "AGE"})})
      https://gmlwjd9405.github.io/2019/08/11/entity-mapping.html` 와 같은 방법을 많이 사용한다.
* `colmnDefinition`: 데이터베이스 컬럼 정보를 직접 줄 수 있다.
    * ex) varchar(100), default: `EMPTY`
* `length(DDL)`: 문자 길이 제약 조건, String 타입에만 사용한다.
    * default: 255
* `precision, scale(DDL)`: BigDecimal 타입에서 사용한다. (BigInteger도 사용할 수 있다.)
    * `precision`은 소수점을 포함한 존체 자릿수를, `scale`은 소수의 자릿수다.
    * `double`, `float`타입에는 적용되지 않는다.
    * 아주 큰 숫자나 정밀한 소수를 다루어야 할 때만 사용한다.
    * default: `precision=19, scale=2`

#### @Enumerated

* 자바 enum 타입을 매핑할 때 사용
* **ORDINAL 사용 X**
* value
    * `EnumType.ORDINAL`: enum 순서를 데이터베이스에 저장
    * `EnumType.STRING`: enum 이름을 데이터베이스에 저장
    * default: `EnymType.ORDINAL` - 항상 `EnumType.STRING`으로 설정을 바꿔 주는 것이 좋다.

#### @Temporal

* 날짜 타입(java.util.Date, java.util.Calendar)을 매핑할 때 사용
* `LocalDate`, `LocalDateTime`을 사용할 때는 생략 가능(최신 하이버네이트 지원)
* value
    * `TemporalType.DATE`: 날짜, 데이터베이스 data 타입과 매핑
        * 예) 2013-10-11
    * `TemporalType.TEIM`: 시간, 데이터베이스 time 타입과 매핑
        * 예) 11:11:11
    * `TemporalType.TIMESTAMP`: 날짜와 시간, 데이터베이스 timestamp 타입과 매핑
        * 예) 2013-10-11 11:11:11

#### @Lob

* DB에서 varchar를 넘어서는 큰 내용을 넣고 싶은 경우에 사용한다.
* 데이터베이스 BLOB, CLOB 타입과 매핑
* `@Lob`에는 지정할 수 있는 속성이 없다.
* 매핑하는 필드 타입이 문자면 CLOB, 나머지는 BLOB 매핑
    * CLOB: String, char[], java.sql.CLOB
    * BLOB: byte[], java.sql.BLOB

#### @Transient

* 필드 매핑 X
* 데이터베이스에 저장X, 조회X
* 주로 메모리상에서만 임시로 어떤 값을 보관하고 싶을 때 사용

### 3-4. 기본 키 매핑

#### 기본 키 매핑 방법

* 직접 할당: `@Id`만 사용
* 자동 생성: `@GeneratedValue`
    * `IDENTITY`: 데이터베이스에 위임, MYSQL
    * `SEQUENCE`: 데이터베이스 시퀀스 오브젝트 사용, ORACLE
        * `@SequenceGenerator`필요
    * `TABLE`: 키 생성용 테이블 사용, 모든 DB에서 사용
        * `@TableGenerator`필요
    * `Auto`: 방언에 따라 자동 지정, 기본값

#### IDENTITY 전략

* 기본 키 생성을 데이터베이스에 위임
* 주로 MySQL, PostgreSQL, SQL Server, DB2에서 사용(예: MySQL의 AUTO_INCREMENT)
* JPA는 보통 트랜잭션 커밋 시점에 INSERT SQL 실행
* AUTO_INCREMENT는 데이터베이스에 INSERT SQL을 샐행한 이후에 ID 값을 알 수 있음
* IDENTITY 전략은 `em.persist()`시점에 즉시 INSERT SQL 실행하고 DB에서 식별자를 조회

```
@Entity
public class Member {
    @Id
    @GenertabeValue(Strategy = Generation.Type.IDENTITY)
    private Long id;
```

#### SEQUENCE 전략

* 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트(예: 오라클 시퀀스)
* 오라클, PostgreSQL, DB2, H2 데이터베이스에서 사용

```java
import javax.persistence.*;

@Entity
@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ", // 매핑할 데이터베이스 시퀀스 이름
        initialValue = 1, allocationSize = 1
)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "MEMBER_SEQ_GENERATOR")
    private Long id;
}
```

##### SEQUENCE - `@SequenceGenerator` 속성

* `allocationSize`: default = 50
* `name`: 식별자 생성기 이름
    * default: 필수
* `sequenceName`: 데이터베이스에 등록되어 있는 시퀀스 이름
    * default: hibernate_sequence
* `initialValue`: DDL 생성 시에만 사용됨, 시퀀스 DDL을 생성할 때 처음 1 시작하는 수를 지정한다.
    * default: 1
* `allocationSize`: 시퀀스 한 번 호출에 증가하는 수
    * 성증 최적화에 사용됨
    * **데이터베이스 시퀀스 값이 하나씩 증가하도록 설정되어 있으면 이 값을 반드시 1로 설쟁해야 한다.**
    * default: **50**
* `catalog`, `schema`: 데이터베이스 catalog, schema 이름

#### TABLE 전략

* 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략
* 장점: 모든 데이터베이스에 적용 가능
* 단점: 성능

```
create table MY_SEQUENCES (
    sequence_name varchar(255) not null,
    next_val bigint,
    primary key (sequence_name)
)
```

```java
import javax.persistence.*;

@Entity
@TableGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "MEMBER_SEQ", allocationSize = 1
)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "MEMBER_SEQ_GENERAGTOR")
    private Long id;
}
```

##### TABLE - `@TableGenerator` 속성

* `name`: 식별자 생성기 이름
    * default: 필수
* `table`: 키생성 테이블명
    * default: hibernate_sequences
* `pkColumnName`: 시퀀스 컬럼명
    * default: sequence_name
* `valueColumnNa`: 시퀀스 값 컬럼명
    * default: next_val
* `pkColumnValue`: 키로 사용할 값 이름
    * default: 엔티티 이름
* `initialValue`: 초기 값, 마지막으로 생성된 값이 기준이다.
    * default: 0
* `allocationSize`: 시퀀스 한 번 호출에 증가하는 수 (성능 최적화에 사용됨)
    * default: **50**
* `catalog`, `schema`: 데이터베이스 catalog, schema 이름
* `uniqueConstraints`(DDL): 유니크 제약 조건을 지정할 수 있다.

#### 권장하는 식별자 전략

* **기본 키 제약 조건**: null 아님, 유일, **변하면 안된다.**
* 미래까지 이 조건을 만족하는 자연키는 찾기 어렵다. 대리키(대체키)를 사용하자.
* 예를 들어 주민등록번호도 기본 키로 적절하지 않다.
* **권장: Long형 + 대체키 + 키 생성전략 사용**

# Note