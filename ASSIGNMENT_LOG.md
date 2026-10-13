# 과제 진행 로그

브랜치: `feature/gilded-rose-unit-test` / PR은 마지막에 한 번만 `main` 대상으로 생성

각 단계가 끝날 때마다 상태와 메모를 갱신한다.
상태: ⬜ 대기 · 🔄 진행 중 · ✅ 완료

## 진행 현황

| # | 단계 | 상태 |
|---|---|---|
| 1 | 환경 준비 | ✅ |
| 2 | 스펙 정리 | ✅ |
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

## 2. 스펙 정리 ✅

- [x] 과제 원본 PDF(`Day2_과제_GildedRoseTest.pdf`, 23p)를 읽고 스펙 파악
- [x] 코드(`GildedRose.updateItems`)와 `README.md`/`ASSIGNMENT.md` 대조, 누락/경계값 확인
- 이 표는 PR 보고서의 스펙-테스트 매핑에 사용 (테스트 메서드 열은 3단계에서 채움)

### 스펙 목록

판정 기준은 `sellIn`을 1 감소시킨 **후** `sellIn < 0`이면 만료. 표기는 (sellIn, quality) 전/후.

**일반 아이템** (`+5 Dexterity Vest`)

| # | 스펙 | 예시 | 테스트 메서드 |
|---|---|---|---|
| 1 | 만료 전 품질 -1 | (10,20)→(9,19) | |
| 2 | sellIn=1이면 아직 -1 (경계) | (1,20)→(0,19) | |
| 3 | sellIn=0이면 만료되어 -2 (경계) | (0,20)→(-1,18) | |
| 4 | 만료 후에도 -2 | (-1,20)→(-2,18) | |
| 5 | 품질은 0 미만 불가 | (5,0)→(4,0) | |
| 6 | 품질 1에서 -2 해도 0 | (0,1)→(-1,0) | |

**Aged Brie**

| # | 스펙 | 예시 | 테스트 메서드 |
|---|---|---|---|
| 7 | 만료 전 품질 +1 | (5,10)→(4,11) | |
| 8 | 만료 후 품질 +2 | (0,10)→(-1,12) | |
| 9 | 품질 50 초과 불가 | (5,50)→(4,50) | |
| 10 | 품질 49에서 +2 해도 50까지 | (0,49)→(-1,50) | |

**Backstage passes to a TAFKAL80ETC concert**

| # | 스펙 | 예시 | 테스트 메서드 |
|---|---|---|---|
| 11 | sellIn 11 이상이면 +1 (경계) | (11,20)→(10,21) | |
| 12 | sellIn 10이면 +2 (경계) | (10,20)→(9,22) | |
| 13 | sellIn 6이면 +2 (경계) | (6,20)→(5,22) | |
| 14 | sellIn 5이면 +3 (경계) | (5,20)→(4,23) | |
| 15 | sellIn 1이면 +3 (경계) | (1,20)→(0,23) | |
| 16 | 공연 종료(sellIn 0 → 만료) 시 품질 0 | (0,20)→(-1,0) | |
| 17 | +2/+3 해도 50 초과 불가 | (10,49)→(9,50), (5,48)→(4,50) | |
| 18 | 품질 50이면 증가 없음 | (5,50)→(4,50) | |

**Sulfuras, Hand of Ragnaros**

| # | 스펙 | 예시 | 테스트 메서드 |
|---|---|---|---|
| 19 | sellIn, 품질 불변 | (10,80)→(10,80) | |
| 20 | 만료 상태에서도 불변 | (-1,80)→(-1,80) | |

**공통 / Repository**

| # | 스펙 | 방법 | 테스트 메서드 |
|---|---|---|---|
| 21 | 여러 아이템을 각자 규칙대로 갱신 | 일반 + Aged Brie 혼합 | |
| 22 | 갱신 결과를 `saveAll`로 저장 요청 | stub + verify | |

### 원본 PDF와 ASSIGNMENT.md 대조 결과

- 규칙 내용(일반/치즈/콘서트/전설)은 ASSIGNMENT.md 힌트와 일치. 코드의 Backstage 경계(`sellIn < 11`, `< 6`)도 PDF의 "10일 이하 +2, 5일 이하 +3"와 일치.
- 원본 PDF 요구: 테스트 하나 = 명세 하나, 테스트 이름에 명세 드러내기, 경계값 테스트, Given/When/Then, `GildedRoseTest`에서 `InMemoryItemRepository` 사용 금지.
- 원본 PDF 제출물: PR 본문에 Coverage(Line/Branch) 캡처, 실행 시간 캡처, 보고서(스펙↔테스트 매핑, Test Double 선택 이유). PR 변경 파일은 `GildedRoseTest.java`로 안내됨 (Mockito 의존성 변경을 위한 `build.gradle`은 ASSIGNMENT.md상 허용).
- 원본 PDF Mockito 버전은 4.11.0으로, 1단계에서 추가한 버전과 동일.
- 힌트 22개로 코드의 분기는 모두 커버될 것으로 보이나, 아래 보강 후보를 추가해 경계를 더 확인할 수 있음 (선택).

### 보강 후보 (힌트에 없는 경계)

| # | 스펙 | 예시 |
|---|---|---|
| 23 | Aged Brie 만료 후 품질 50이면 유지 | (0,50)→(-1,50) |
| 24 | Backstage 이미 만료된 상태에서도 품질 0 | (-1,20)→(-2,0) |
| 25 | Backstage 품질 50이어도 만료 시 0 | (0,50)→(-1,0) |

### 주의할 점

- Sulfuras의 품질 80 고정은 코드가 강제하지 않고 **변경하지 않을 뿐**이다. 테스트는 "불변"만 검증한다.
- `findAll()`이 생성자와 `updateQuality()`에서 2번 호출되므로 stub 반환값이 두 번 다 같은 리스트여야 하고, `Item`은 같은 객체라 `assertEquals`는 stub에 넘긴 Item 필드로 확인한다.
- 이번 PR에는 `CLAUDE.md`, `ASSIGNMENT_LOG.md`, `build.gradle`(JaCoCo, UTF-8)도 포함되어 있어 원본의 "GildedRoseTest.java 변경" 안내보다 변경 파일이 많다. PR 전에 포함 여부를 결정해야 한다.

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
