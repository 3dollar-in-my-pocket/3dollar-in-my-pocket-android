# TH-1226 Next Session Prompt

이 프롬프트는 사용자가 `00-design.md`를 승인한 뒤 새 Codex 세션에 그대로 전달한다.

권장 실행 설정:

- model: `gpt-5.6-sol`
- reasoning effort: `xhigh`
- 문서 작성일: 2026-08-26

공식 OpenAI 문서상 GPT-5.6 Sol은 `xhigh` reasoning effort를 지원한다. GPT-5.6 prompt는 반복을 줄이고 domain context, hard constraint, 승인 경계, 성공 기준을 명확히 두는 편이 권장된다.

- https://developers.openai.com/api/docs/models/gpt-5.6-sol
- https://developers.openai.com/api/docs/guides/latest-model

## Copy-paste prompt

```text
TH-1226 Android 구현을 진행해.

이 세션은 GPT-5.6 Sol, reasoning effort xhigh를 사용한다. 깊은 사고량은 계약 확인, 기존 코드 재사용 판단, edge case 검토와 최종 diff review에 사용해. 추가 abstraction, 문서, 테스트, 하위 작업을 만들어 범위를 넓히는 데 사용하지 마.

현재 작업 디렉터리:
/Users/jeongjin-yong/Desktop/work/3dollar-in-my-pocket-android

현재 브랜치:
feature/TH-1226-store-detail-bottomsheet

기준 커밋:
6729399293462178d71e58048787067c7186ef97

가장 먼저 아래를 확인해.
- git branch --show-current
- git rev-parse HEAD
- git status --short
- git diff --name-only 6729399293462178d71e58048787067c7186ef97..HEAD

현재 HEAD는 기준 커밋 위에 이번 설계/프롬프트 문서 commit만 있어야 한다. 기준 커밋 이후 변경 파일은 아래 두 개뿐이어야 한다.
- docs/features/TH-1226-store-detail-bottom-sheet/00-design.md
- docs/features/TH-1226-store-detail-bottom-sheet/01-next-session-prompt.md

working tree에는 아래 기존 사용자 변경 두 파일만 dirty여야 한다.
- AGENTS.md
- docs/features/TH-1128-store-detail-action-nudge/03-implementation.md

두 파일은 절대 되돌리거나 stage/commit하지 마.

현재 과거 구현은 로컬 backup/TH-1226-before-replan-20260826에만 있다. 이 branch를 cherry-pick하거나 wholesale 복사하지 마. 사용자가 과구현으로 판단해 원복한 작업이다. 특정 legacy 동작을 확인할 때 제한적인 git show만 허용한다.

문서를 다음 순서로 완전히 읽어.
1. AGENTS.md
2. docs/README.md
3. docs/context/project-current.md
4. docs/context/architecture-current.md
5. docs/context/resource-rules-current.md
6. docs/context/verification-matrix.md
7. docs/features/TH-1226-store-detail-bottom-sheet/00-design.md
8. docs/features/TH-1226-store-detail-bottom-sheet/01-next-session-prompt.md

00-design.md는 사용자 승인 설계이자 scope source of truth다. Product 결정을 다시 묻거나 과거 조사를 처음부터 반복하지 마. 현재 코드나 Swagger가 설계와 실제로 충돌할 때만 충돌 근거와 영향 범위를 한 번 질문해.

목표:
- 기존 Home 구현을 유지하면서 marker Preview를 같은 bottom sheet의 V2 Expanded 상세로 연결한다.
- Home list card, deep link, push, share, related store와 기존 legacy 상세을 열던 모든 caller를 V2 full-screen으로 연결한다.
- Home Expanded와 full-screen은 같은 StoreDetailV2Content를 사용한다.
- Figma 개선 화면 10582:31339의 제보된 가게/사장님 직영 TO-BE를 구현한다.
- 실제 GET /api/v2/screen/store/{storeId} 응답을 사용한다.
- 16개 section을 server order로 처리하고 모든 현재 action을 동작시킨다.
- 기존 action의 Dialog, 하위 Activity, Repository, 결과와 오류 동작을 최대한 재사용한다.

확정 정책:
- 한번에 전체 범위를 완료한다. phase 1/후속 범위로 나누지 않는다.
- rollout gate를 만들지 않는다.
- legacy StoreDetailActivity/BossStoreDetailActivity로 이동하거나 실패 fallback하지 않는다.
- legacy Activity 파일은 삭제하지 않는다.
- V2 load/action 오류는 현재 legacy 상세처럼 Toast/Snackbar를 표시하고 content를 유지한다.
- USER not_exists_store와 필수 위치 실패는 현재 container를 닫는다.
- 별도 오류 화면이나 자동 retry를 만들지 않는다.
- 모든 action을 구현한다. Map, Ad, coupon, post, review, photo 등을 미루지 않는다.

구현 방식:
1. 기존 HomeBottomSheetContent, HomeViewModel, HomeFragment와 repository/action flow를 먼저 읽고 살릴 코드를 명시한다.
2. 00-design.md를 반복한 하위 문서를 만들지 말고, 파일 그룹·TDD 순서·검증 checkpoint만 담은 간결한 02-plan.md 하나를 작성한다.
3. 코드 수정 전 예상 변경 파일을 모듈/기능 그룹으로 짧게 보고한다. 00-design.md의 파일 경계를 벗어나면 구현을 멈추고 이유와 대안을 제시한다.
4. 기존 구조 위의 얇은 V2 확장을 구현한다.
   - ServerApi + ScreenRepository 흐름
   - response -> display model mapper 한 경계
   - StoreDetailV2ViewModel
   - StoreDetailV2Content
   - Home Preview/Expanded
   - 간단한 StoreDetailV2Activity.getIntent
5. renderer는 LazyColumn의 direct when으로 작성한다. renderer registry, render plan, 범용 plugin framework를 만들지 않는다.
6. action은 feature-local when 한곳에서 기존 기능으로 연결한다. resolver -> dispatcher -> effect policy 같은 계층을 만들지 않는다.
7. 상태는 Loading/Content/Error와 Preview/Expanded 정도로 유지한다. 별도 reducer, generation policy, lifecycle controller class를 만들지 않는다.
8. Map/Ad lifecycle은 각각 Compose host 한곳에서 처리한다.
9. 새 abstraction은 현재 두 consumer가 실제로 필요할 때만 추가한다.
10. 의존성 추가/업그레이드, toolchain 변경, 전체 Home/상세 리팩터링, unrelated formatting을 하지 않는다.

TDD와 commit:
- 사용자 동작 계약별로 실패 테스트를 먼저 작성하고 최소 구현으로 통과시킨다.
- 거대한 fixture/test 한 파일을 만들지 않는다.
- mapper, state, action, route의 가치 있는 경계만 테스트한다.
- 커밋은 API/model, renderer/action, Home sheet, full-screen/caller, verification 정도의 4~6개 논리 단위로 묶는다.
- micro commit을 수십 개 만들지 않는다.
- shared model, mapper, renderer는 메인 에이전트 한 명이 소유한다.
- 하위 에이전트는 사용자가 별도로 요청하지 않는 한 코드 수정에 사용하지 마. 필요하면 read-only final review에만 사용한다.

검증 범위:
- 관련 자동 unit test
- 필요한 compile task
- :app:assembleDebug

사용자 지시에 따라 아래는 실행하지 마.
- emulator 또는 실기기 수동 UI 검증
- browser로 앱 화면 확인
- full-screen screenshot
- screenshot comparator/visual diff
- Figma/iOS screenshot parity
- authenticated live preflight

수동 확인 항목은 docs/features/TH-1226-store-detail-bottom-sheet/04-verification.md에 사용자가 실행할 checklist로만 남겨.

API source of truth:
- Swagger/OpenAPI가 field/type/nullability/action의 우선 기준이다.
- Figma 10582:31339는 UI 구성과 interaction 기준이다.
- iOS model을 API 근거로 복사하지 마.
- 위치 header는 optional이며 값이 없으면 0.0을 만들지 말고 생략한다.
- unknown section은 해당 section만 skip한다.

완료 전 확인:
- 모든 기존 direct legacy 상세 caller가 V2로 전환됐는지 rg로 검사한다.
- Home list sheet 기존 동작이 회귀하지 않는지 관련 unit test를 실행한다.
- 모든 V2 action success/failure와 child result refresh를 검증한다.
- git diff --check를 실행한다.
- staged 파일에 보호 대상 두 파일이 없는지 확인한다.
- 실제 실행한 test/assemble 결과만 문서와 최종 답변에 기록한다.

push, PR, 배포는 하지 마.

먼저 현재 상태와 재사용할 기존 구조, 예상 변경 파일 그룹, 간결한 실행 순서를 보고한 뒤 구현을 계속 진행해. 승인된 설계와 충돌이 없으면 불필요한 중간 질문 없이 끝까지 완료해.
```
