-- 테스트용 사용자 데이터 (비밀번호는 'password'를 BCrypt로 암호화한 값)
INSERT INTO users (id, email, password, name, nickname, role, created_at, updated_at)
VALUES (1, 'user1@example.com', '$2a$10$y.CVG.xSnaRwv2gG.3eK/eY9Z9C.Y4G/2N2b2d.Y4G/2N2b2d.Y4', '김마실', '여행가 마실쿤', 'USER', NOW(), NOW());
INSERT INTO users (id, email, password, name, nickname, role, created_at, updated_at)
VALUES (2, 'user2@example.com', '$2a$10$y.CVG.xSnaRwv2gG.3eK/eY9Z9C.Y4G/2N2b2d.Y4G/2N2b2d.Y4', '박사진', '사진작가 제이', 'USER', NOW(), NOW());

-- 테스트용 게시글 데이터 (ID=1), 작성자는 1번 유저
INSERT INTO articles (title, content, user_id, region, scrap_count, like_count, view_count, created_at, updated_at)
VALUES ('서울의 숨겨진 맛집 탐방', '서울의 잘 알려지지 않은 맛집들을 찾아가 봤습니다. 정말 놀라운 경험이었어요!', 1, '서울', 10, 25, 150, NOW(), NOW());

-- 게시글 1의 태그 데이터
INSERT INTO article_tags (article_id, tag) VALUES (1, 'RESTAURANT');
INSERT INTO article_tags (article_id, tag) VALUES (1, 'CAFE');

-- 게시글 1의 사진 데이터
INSERT INTO article_photos (article_id, photo_url) VALUES (1, 'https://m.fritz.co.kr/web/product/big/202302/1f2cef38956f3cd3be32355907ac8f93.jpg');
INSERT INTO article_photos (article_id, photo_url) VALUES (1, 'https://m.fritz.co.kr/web/product/big/202302/d42cd7b0d298579692e80638fa6dd6ed.jpg');

-- 게시글 1의 장소 경로 데이터
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (1, 1, '마실 카페', '서울시 강남구 테헤란로 123', '아늑한 분위기의 시그니처 커피가 맛있는 곳');
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (1, 2, '쿤 레스토랑', '서울시 강남구 역삼동 456', '신선한 재료로 만든 이탈리안 요리가 일품입니다.');

-- =================================================================================================

-- 테스트용 게시글 데이터 (ID=2), 작성자는 2번 유저
INSERT INTO articles (title, content, user_id, region, scrap_count, like_count, view_count, created_at, updated_at)
VALUES ('부산 해변 기차 여행', '해운대에서 송정까지, 해변을 따라 달리는 기차는 정말 낭만적이었어요.', 2, '부산', 5, 15, 100, NOW(), NOW());

-- 게시글 2의 태그 데이터
INSERT INTO article_tags (article_id, tag) VALUES (2, 'TRAVEL_SPOT');

-- 게시글 2의 사진 데이터
INSERT INTO article_photos (article_id, photo_url) VALUES (2, 'https://www.visitbusan.net/uploadImgs/files/cntnts/20191227195613061_oen');
INSERT INTO article_photos (article_id, photo_url) VALUES (2, 'https://tourimage.interpark.com/BBS/Tour/FckUpload/201706/6363304827248806650.jpg');
INSERT INTO article_photos (article_id, photo_url) VALUES (2, 'https://cdn.kbthink.com/content/dam/tam-dcp-cms/kbcontent/%EC%9E%90%EC%82%B0%EA%B4%80%EB%A6%AC/%EB%94%94%EC%BD%98-%EC%82%AC%EC%97%85%EC%9E%90-%EC%BD%98%ED%85%90%EC%B8%A0/%EC%83%81%EA%B6%8C%EB%B6%84%EC%84%9D/jeonpodong/Jeonpodong-MO-03-1.jpg');

-- 게시글 2의 장소 경로 데이터
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (2, 1, '해운대 블루라인파크', '부산시 해운대구 중동 948-1', '해변을 따라 달리는 스카이캡슐과 해변열차가 있습니다.');
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (2, 2, '송정해수욕장', '부산시 해운대구 송정동', '서핑으로 유명한 비교적 한적한 해변입니다.');

-- 테스트용 게시글 데이터 (ID=3), 작성자는 1번 유저, 경남 지역
INSERT INTO articles (title, content, user_id, region, scrap_count, like_count, view_count, created_at, updated_at)
VALUES ('경남 창원의 아름다운 길', '창원의 숨겨진 길을 따라 걸으며 새로운 풍경을 발견했습니다.', 1, '창원', 7, 18, 90, NOW(), NOW());
INSERT INTO article_tags (article_id, tag) VALUES (3, 'TRAVEL_SPOT');
INSERT INTO article_photos (article_id, photo_url) VALUES (3, 'https://example.com/changwon1.jpg');
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (3, 1, '주남저수지', '경남 창원시 의창구 대산면 유등리', '철새들이 많아 아름다운 곳');

-- 테스트용 게시글 데이터 (ID=4), 작성자는 2번 유저, 태그가 2개 (AND 조건 테스트용)
INSERT INTO articles (title, content, user_id, region, scrap_count, like_count, view_count, created_at, updated_at)
VALUES ('제주의 맛집과 카페', '제주도에서 맛집과 분위기 좋은 카페를 동시에 즐겼어요.', 2, '제주', 15, 30, 200, NOW(), NOW());
INSERT INTO article_tags (article_id, tag) VALUES (4, 'RESTAURANT');
INSERT INTO article_tags (article_id, tag) VALUES (4, 'CAFE');
INSERT INTO article_photos (article_id, photo_url) VALUES (4, 'https://example.com/jeju1.jpg');
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (4, 1, '제주 맛집', '제주시 어딘가', '제주 전통의 맛');