# CLAUDE.md

Gilded Rose 카타 기반 실습 프로젝트. 목표는 **기존 코드를 수정하지 않고 Unit Test를 작성**하고, 느린 Repository를 **Test Double(Mockito)** 로 대체하는 것이다. 상세 과제 내용은 `ASSIGNMENT.md`, 요구사항 명세는 `README.md` 참고.

## 기술 스택

- Java 8 (`sourceCompatibility = 1.8`)
- 빌드: Gradle (`build.gradle`, wrapper `gradlew`) / Maven (`pom.xml`, wrapper `mvnw`) 둘 다 존재
- 테스트: JUnit 5 (Gradle 5.6.2, Maven 5.8.2)
- **Mockito 의존성은 아직 없음** — 과제 수행 시 `build.gradle`(필요하면 `pom.xml`)에 추가해야 함

## 디렉터리 구조

```
src/main/java/com/gildedrose/
  Item.java                  # 아이템 모델 (public name, sellIn, quality)
  GildedRose.java            # 품질 갱신 로직 (updateQuality)
  ItemRepository.java        # 저장소 인터페이스 (Test Double 대상)
  InMemoryItemRepository.java# 메모리 구현체, 호출마다 1초 지연
src/test/java/com/gildedrose/
  GildedRoseTest.java        # 현재는 placeholder 테스트(foo) 1개
ASSIGNMENT.md                # 실습 과제 설명, 테스트할 스펙 22개 목록
README.md                    # Gilded Rose 요구사항 명세
```

## 핵심 클래스

- `Item`: 단순 데이터 클래스. 필드가 public이며 `toString()`은 `"name, sellIn, quality"`.
- `ItemRepository`: `findAll()`, `saveAll(List<Item>)`, `add(Item)`, `remove(Item)`.
- `InMemoryItemRepository`: `ArrayList` 기반. 모든 메서드가 `Thread.sleep(1000)` 후 동작 → 실제 테스트에 쓰면 느리므로 단위 테스트에서는 Mock/Stub 사용.
- `GildedRose` (package-private, 생성자 `GildedRose(ItemRepository)`):
  - 생성자에서 `repository.findAll()` 호출 (1회)
  - `updateQuality()`: `findAll()` → `updateItems()`로 in-place 갱신 → `saveAll(Arrays.asList(items))`
  - 즉 `updateQuality()` 한 번에 `findAll()`이 총 2번(생성자 + 갱신) 호출됨
  - `items` 필드는 package-private (과제상 수정 금지)
  - `updateItems()`는 중첩 if로 된 레거시 로직. 이름 기준 특수 아이템: `Aged Brie`, `Backstage passes to a TAFKAL80ETC concert`, `Sulfuras, Hand of Ragnaros`

## 비즈니스 규칙 요약

| 아이템 | 유통기한 전 | 유통기한 후(`sellIn < 0`) |
|---|---|---|
| 일반 | quality -1 | -2 (최소 0) |
| Aged Brie | +1 | +2 (최대 50) |
| Backstage | sellIn ≥11: +1, 6~10: +2, ≤5: +3 (최대 50) | 0 |
| Sulfuras | 변화 없음 (sellIn, quality 모두) | 변화 없음 |

- `sellIn`은 매일 1 감소(Sulfuras 제외). 갱신 **후** `sellIn < 0`이면 만료 처리.
- quality 범위 0~50 (Sulfuras는 80 고정).

## 명령어

```bash
./gradlew test          # Gradle 테스트 (Windows: gradlew.bat test)
./mvnw test             # Maven 테스트
```

## 작업 규칙 (ASSIGNMENT.md 기준)

- `src/main`은 수정하지 않는다. `Item`, `GildedRose.items` 필드도 그대로 둔다.
- 변경 가능 범위: 테스트 코드와 테스트 의존성 설정(`build.gradle` 등)만.
- 테스트는 Given/When/Then 구조, 테스트 하나당 스펙 하나, 경계값(sellIn 0/1, quality 0/50) 포함.
- 스펙 검증은 `stub` + `assertEquals`, 상호작용 검증은 `stub` + `verify(repository).saveAll(...)`.
- 완료 기준: `GildedRose` Line Coverage ≥ 90%, Branch Coverage ≥ 80%, `GildedRoseTest` 전체 실행 3초 이내.
- 제출은 작업 브랜치 → `main` 대상 PR. (현재 `origin`은 `sds-code-reviewer-26-8/GildedRoseTest-40832`)

## 알려진 이슈

- `build.gradle`의 `texttest` 태스크는 존재하지 않는 `com.gildedrose.TexttestFixture`를 참조한다 (실행 시 실패, 테스트와 무관).
- 현재 `GildedRoseTest.foo`는 `InMemoryItemRepository`를 직접 사용해 생성자 1초 + `updateQuality` 2초 + `findAll` 1초 등 약 4초 이상 걸린다. Mock으로 교체 필요.
