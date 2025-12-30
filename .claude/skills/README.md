# Claude Code Skills

이 디렉토리에는 프로젝트의 Claude Code skills가 포함되어 있습니다.

## Available Skills

### auto-update-docs
프로젝트 문서를 자동으로 업데이트하는 skill입니다.

**사용 시점:**
- 기능 개발이나 리팩토링 완료 후
- 모듈 의존성 변경 후
- 리소스 이동 작업 후
- 아키텍처 변경 후

**관리하는 문서:**
- `CLAUDE.md` - Claude Code 작업 가이드
- `DEPENDENCY_MAP.md` - 모듈 의존성 구조
- `MIGRATION_RULES.md` - Clean Architecture 마이그레이션 가이드

**사용 방법:**

Claude Code에서 다음과 같이 입력:
```
/skill auto-update-docs
```

또는 자연어로 요청:
```
작업 완료했으니 문서 업데이트해줘
문서들을 최신 상태로 업데이트해줘
```

**동작 과정:**
1. 최근 Git 변경사항 분석
2. 영향받는 문서 식별
3. 각 문서 자동 업데이트
4. 일관성 검증
5. 업데이트 요약 리포트 제공

## Skills 추가 방법

새로운 skill을 추가하려면:

1. `.claude/skills/` 디렉토리에 `.md` 파일 생성
2. Skill 설명과 프로세스 작성
3. 이 README에 skill 설명 추가

## 주의사항

- Skills는 Claude Code가 자동으로 인식합니다
- Skill 파일은 마크다운 형식으로 작성해야 합니다
- 상세한 지침을 제공할수록 더 정확하게 동작합니다
