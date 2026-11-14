# Google Play 릴리즈

워크플로는 `.github/workflows/play-release.yml`("Play Release"). **release/** 브랜치에서 수동 실행만** 한다. develop push 로는 아무것도 올라가지 않는다.

```bash
gh workflow run "Play Release" --ref release/v4.37.0 -f whats_new="$(cat whats_new.txt)"   # -f dry_run=true 로 검증만
```

한 번 실행하면 release AAB 를 빌드해 같은 versionCode 를 **내부 테스트·프로덕션**에 올리고, 새로운 기능 문구(ko-KR, 500자 이하)를 넣어 **프로덕션 심사 제출**까지 커밋한다(`.github/scripts/play_release.py`).
Play Console 의 관리형 게시가 꺼져 있으면 심사 통과 즉시 출시된다.

심사 거절 등으로 자동 제출이 막힌 상태면 변경사항이 저장만 되고 디스코드 알림에 "검토를 위해 보내기 필요"로 표시된다. 그때는 Play Console → 게시 개요에서 보낸다.

## 순서

1. `release/vX.Y.Z` 브랜치에서 `gradle.properties` 의 `version_name`·`version_code`(+1)를 올린다. 기능 PR 은 이 브랜치로 머지한다.
2. 위 명령으로 심사 제출. 보통 `/3dollars:release-playstore`(디스코드 `/upload-playstore`)가 1~2를 한다.
3. 스토어에 출시되면 `Release vX.Y.Z` PR 을 develop 으로 머지 → 머지 커밋에 태그·GitHub 릴리즈(디스코드 출시 공지) → 다음 `release/vX.Y+1.0` 을 만든다(디스코드 `/finish-playstore`). 순서 상세는 `docs/process/pr-process.md` "개발 배포와 릴리즈 마무리".

Play 는 심사·출시 상태를 API 나 웹훅으로 알려주지 않는다. 출시 여부는 스토어 페이지 버전으로 확인한다.

## 시크릿

`GOOGLE_PLAY_SERVICE_ACCOUNT_JSON`(출시 관리 권한 서비스 계정), `KEYSTORE_BASE64`, `GOOGLE_SERVICES_RELEASE_BASE64`, `local.properties` 값들(`firebase-distribution.yml` 과 공유), `DISCORD_WEBHOOK_URL`.
