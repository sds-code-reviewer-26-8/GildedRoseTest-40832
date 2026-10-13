# 과제 진행 로그

브랜치: `feature/gilded-rose-unit-test` / PR은 마지막에 한 번만 `main` 대상으로 생성

각 단계가 끝날 때마다 상태와 메모를 갱신한다.
상태: ⬜ 대기 · 🔄 진행 중 · ✅ 완료

## 진행 현황

| # | 단계 | 상태 |
|---|---|---|
| 1 | 환경 준비 | ✅ |
| 2 | 스펙 정리 | ⬜ |
| 3 | 테스트 작성 | ⬜ |
| 4 | 검증 | ⬜ |
| 5 | 제출 자료 준비 | ⬜ |
| 6 | 커밋 및 push (PR 생성) | ⬜ |

---

## 1. 환경 준비 ✅

- [x] `build.gradle`에 Mockito 의존성 추가 (`mockito-core`, `mockito-junit-jupiter`)
- [x] JaCoCo 플러그인 추가 (명령줄 Coverage 확인용)
- [x] `./gradlew test`로 기존 `foo` 테스트 동작 확인
- 참고: `texttest` 태스크는 없는 `TexttestFixture`를 참조하는 깨진 태스크. 과제와 무관하므로 그대로 둔다.

**메모:**
- `build.gradle` 변경: `jacoco` 플러그인, Mockito 4.11.0 (`mockito-core`, `mockito-junit-jupiter`; Java 8 호환 버전), JaCoCo 리포트(xml/html, `test` 후 자동 생성), 컴파일 인코딩 UTF-8
- UTF-8 설정 이유: 기본 인코딩이 x-windows-949라 한글 주석에서 `unmappable character` 경고 발생
- `./gradlew clean test` 성공. 기존 `foo` 테스트 통과, 소요 4.08초 (Mock 교체 전 기준선)
- Coverage 리포트: `build/reports/jacoco/test/html/index.html`

## 2. 스펙 정리 ⬜

- [ ] `ASSIGNMENT.md`의 스펙 22개를 아이템별로 정리
- [ ] 코드와 `README.md` 명세를 보고 누락/경계값 비교
- 이 표는 PR 보고서의 스펙-테스트 매핑에 사용

**메모:**

## 3. 테스트 작성 ⬜

`GildedRoseTest.java`

- [ ] 기존 `foo` 테스트 제거, `InMemoryItemRepository` 대신 Mockito `@Mock ItemRepository` 사용
- [ ] 스펙 검증 (stub + `assertEquals`, `sellIn`/`quality` 모두 검증)
  - [ ] 일반 아이템 6개
  - [ ] Aged Brie 4개
  - [ ] Backstage 8개
  - [ ] Sulfuras 2개
  - [ ] 여러 아이템 혼합 1개
- [ ] 상호작용 검증 (stub + `verify(repository).saveAll(...)`)
  - `findAll()`이 생성자와 `updateQuality()`에서 총 2번 호출됨에 유의
- [ ] 테스트 하나당 스펙 하나, Given/When/Then 구조
- [ ] `src/main` 수정 없음

**메모:**

## 4. 검증 ⬜

- [ ] `./gradlew test` 전체 통과
- [ ] `GildedRose` Line Coverage ≥ 90%
- [ ] `GildedRose` Branch Coverage ≥ 80%
- [ ] `GildedRoseTest` 전체 실행 시간 3초 이내
- [ ] 미커버 분기가 있으면 테스트 보강

**결과:**

## 5. 제출 자료 준비 ⬜

- [ ] IntelliJ Coverage 캡처 (직접 수행)
- [ ] `GildedRoseTest` 실행 시간 캡처 (직접 수행)
- [ ] PR 보고서 작성: 스펙-테스트 매핑 표, Test Double 사용 설명(무엇을/어떤 종류로/왜)

**메모:**

## 6. 커밋 및 push ⬜

- [ ] 작업 단위로 커밋하며 브랜치에 push
- [ ] 마지막에 `main` 대상 PR 한 번만 생성

**메모:**
