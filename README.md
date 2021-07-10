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

# Note