# 모듈 의존성 매핑

## 현재 의존성 구조

### Core 모듈 (기반)
```
core/common (최하위 - 의존성 없음)
├── core/network → core/common, common
├── core/ui → core/common
└── core/designsystem (의존성 없음)
```

### Feature 모듈

#### Home Feature
```
home/domain → core/common
home/data → home/domain, core/network, core/common
home/presentation → home/domain
```

#### My Feature  
```
my/domain → home/domain, core/common, core/network
my/data → core/network, core/common, home/domain, my/domain
my/presentation → common, core/common, core/network, my/data, my/domain
```

#### Community Feature
```
community/domain → home/domain, core/common, core/network  
community/data → core/network, core/common, home/domain, community/domain
community/presentation → common, core/common, core/network, community/data, community/domain, home:domain
```

#### Login Feature
```
login/domain → core/common, core/network
login/data → login/domain, core/network, core/common
```

### Legacy 모듈
```
common → core/common
app → common, core/network, core/common, home:*, my:*, community:*, login:*
```

## 🔴 발견된 문제점

### 1. 순환 의존성 위험
- `my/domain` → `home/domain` (잘못된 의존성)
- `community/domain` → `home/domain` (잘못된 의존성)

### 2. 레이어 위반
- Domain 모듈이 Network에 직접 의존 (my, community, login)
- Presentation이 Network에 직접 의존

### 3. 불필요한 의존성
- Feature 간 직접 의존성 (my ↔ home, community ↔ home)

## ✅ 수정 계획

### 1. Domain Layer 정리
- Domain은 오직 core/common에만 의존
- Network 의존성 제거 (Repository 패턴으로 Data Layer에서 처리)

### 2. Feature 간 의존성 제거
- 공통 모델은 core/common으로 이동
- Feature 간 직접 참조 제거

### 3. 올바른 의존성 구조
```
Presentation → Data → Domain → Core/Common
            ↘ Core/UI ← Core/DesignSystem
```