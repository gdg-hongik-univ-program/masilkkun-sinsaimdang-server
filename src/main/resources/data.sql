-- `articles` 테이블에 데이터 삽입 (article_id를 명시하지 않아 MySQL이 AUTO_INCREMENT로 자동 부여)
INSERT INTO articles (title, content, author_name, region, scrap_count, like_count, view_count, created_at, updated_at) VALUES
    ('서울의 숨겨진 맛집 탐방', '서울의 잘 알려지지 않은 맛집들을 찾아가 봤습니다. 정말 놀라운 경험이었어요!', '여행가 마실쿤', '서울', 10, 25, 150, NOW(), NOW());

INSERT INTO articles (title, content, author_name, region, scrap_count, like_count, view_count, created_at, updated_at) VALUES
    ('부산 해변 기차 여행', '해운대에서 송정까지, 해변을 따라 달리는 기차는 정말 낭만적이었어요.', '사진작가 제이', '부산', 5, 15, 100, NOW(), NOW());

-- =================================================================================================
-- 주의: 아래 INSERT 문들은 `articles` 테이블에 자동으로 부여된 ID를 알아야 합니다.
-- MySQL에서 LAST_INSERT_ID() 함수를 사용하여 직전 INSERT된 AUTO_INCREMENT 값을 가져올 수 있습니다.
-- 또는, 애플리케이션에서 JPA를 통해 Article을 생성한 후 ID를 사용하는 것이 더 안전합니다.
-- 여기서는 일단 쿼리 단순화를 위해 '대략적인 ID'를 가정하고 작성합니다.
-- 실제 테스트 시에는 articles 테이블의 'id' 컬럼 값을 MySQL Workbench 등에서 확인하여 아래 쿼리의 '1', '2' 대신 사용해야 합니다.

-- 게시글 1의 태그 데이터 (실제 ID를 확인하고 사용해야 함)
-- 아래 쿼리의 article_id는 위 INSERT 문으로 자동 생성된 ID를 기반으로 해야 합니다.
-- 임시로 1과 2를 가정합니다.
INSERT INTO article_tags (article_id, tag) VALUES (1, 'RESTAURANT');
INSERT INTO article_tags (article_id, tag) VALUES (1, 'CAFE');

-- 게시글 1의 사진 데이터
INSERT INTO article_photos (article_id, photo_url) VALUES (1, 'https://example.com/photo1.jpg');
INSERT INTO article_photos (article_id, photo_url) VALUES (1, 'https://example.com/photo2.jpg');

-- 게시글 1의 장소 경로 데이터
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (1, 1, '마실 카페', '서울시 강남구 테헤란로 123', '아늑한 분위기의 시그니처 커피가 맛있는 곳');
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (1, 2, '쿤 레스토랑', '서울시 강남구 역삼동 456', '가성비 좋은 점심 특선 최고!');

-- 게시글 2의 태그 데이터
INSERT INTO article_tags (article_id, tag) VALUES (2, 'TRAVEL_SPOT');

-- 게시글 2의 사진 데이터
INSERT INTO article_photos (article_id, photo_url) VALUES (2, 'https://example.com/busan_photo1.jpg');
INSERT INTO article_photos (article_id, photo_url) VALUES (2, 'https://example.com/busan_photo2.jpg');
INSERT INTO article_photos (article_id, photo_url) VALUES (2, 'https://example.com/busan_photo3.jpg');

-- 게시글 2의 장소 경로 데이터
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (2, 1, '해운대 블루라인파크', '부산시 해운대구 중동 948-1', '해변을 따라 달리는 스카이캡슐과 해변열차가 있습니다.');
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (2, 2, '송정해수욕장', '부산시 해운대구 송정동', '서핑으로 유명한 비교적 한적한 해변입니다.');