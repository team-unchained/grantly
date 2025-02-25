# Grantly API

## 프로젝트 구조
grantly api 는 클린 아키텍처를 지향하며 그 중에서도 hexagonal architecture 를 따르는 것을 목표로 합니다.

```
.
├── Application.kt
├── common
│   ├── annotations
│   │   ├── PersistenceAdapter.kt
│   │   └── UseCase.kt
│   ├── config
│       └── JpaConfig.kt
└── user
    ├── adapter
    │   ├── in
    │   │   ├── UserController.kt
    │   │   └── dto
    │   │       └── SignUpRequest.kt
    │   └── out
    │       ├── SpringDataUserRepository.kt
    │       ├── UserJpaEntity.kt
    │       ├── UserJpaRepository.kt
    │       └── UserMapper.kt
    ├── application
    │   ├── port
    │   │   ├── in
    │   │   │   ├── FindUserQuery.kt
    │   │   │   └── SignUpUseCase.kt
    │   │   └── out
    │   │       └── UserRepository.kt
    │   └── service
    │       └── UserService.kt
    └── domain
        └── User.kt
```

- application
    - port: 포트 계층
        - in: 클라이언트 -> 애플리케이션
            - `~UseCase`, `~Query` 의 이름을 가진 **인터페이스**
            - ex) SignUpUseCase 
        - out: 애플리케이션 -> 외부 (DB 와 같은 외부 시스템)
            - `CreateUserPort`, `UpdateUserPort` 식으로 구현할 수 있으나 현재로서는 `~Repository` 이름의 **인터페이스** 하나로 구현
            - ex) `UserRepository`
    - service: 서비스 계층
      - UseCase 의 **구현체**이며 Repository 인터페이스를 사용하여 DB 와 통신
      - `@UseCase` 어노테이션을 사용함
      - ex) `UserService`
- domain: 도메인 계층
    - 비즈니스 로직을 담은 객체 
    - ex) User
- adapter: 구현체가 아닌 인터페이스에 의존하는 방식
    - in: 외부에서 안으로 들어오는 요청에 대응하는 **구현체**
        - ex) HTTP 요청 -> UseCase 호출: `UserController`
    - out: 내부에서 밖으로 나가는 요청에 대응하는 **구현체**
        - `@PersistenceAdapter` 어노테이션을 사용함
        - ex) UseCase -> DB 저장 (외부 호출): `UserPersistenceAdapter`