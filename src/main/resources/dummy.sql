-- 1. 사용자(USERS) 데이터
INSERT INTO users (id, email, password, name, nickname, role, created_at, updated_at)
VALUES (1, 'user1@example.com', '$2a$10$y.CVG.xSnaRwv2gG.3eK/eY9Z9C.Y4G/2N2b2d.Y4G/2N2b2d.Y4', '김마실', '여행가 마실쿤', 'USER', NOW(), NOW())
    ON DUPLICATE KEY UPDATE name='김마실', nickname='여행가 마실쿤';

INSERT INTO users (id, email, password, name, nickname, role, created_at, updated_at)
VALUES (2, 'user2@example.com', '$2a$10$y.CVG.xSnaRwv2gG.3eK/eY9Z9C.Y4G/2N2b2d.Y4G/2N2b2d.Y4', '박사진', '사진작가 제이', 'USER', NOW(), NOW())
    ON DUPLICATE KEY UPDATE name='박사진', nickname='사진작가 제이';

-- 3번 사용자 추가
INSERT INTO users (id, email, password, name, nickname, role, created_at, updated_at)
VALUES (3, 'user3@example.com', '$2a$10$y.CVG.xSnaRwv2gG.3eK/eY9Z9C.Y4G/2N2b2d.Y4G/2N2b2d.Y4', '이팔십육', '팔이십육', 'USER', NOW(), NOW())
    ON DUPLICATE KEY UPDATE name='이팔십육', nickname='팔이십육';


-- 2. 게시글(ARTICLES) 및 관련 데이터 초기화
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE article_places;
TRUNCATE TABLE article_photos;
TRUNCATE TABLE article_tags;
TRUNCATE TABLE articles;
TRUNCATE TABLE follow;
SET FOREIGN_KEY_CHECKS = 1;


-- =================================================================================================
-- 게시글 1: 수원시 (region_id = 901)
-- =================================================================================================
INSERT INTO articles (article_id, title, content, user_id, region_id, scrap_count, like_count, view_count, created_at, updated_at)
VALUES (1, '수원의 숨겨진 맛집 탐방', '수원의 잘 알려지지 않은 맛집들을 찾아가 봤습니다. 정말 놀라운 경험이었어요!', 1, 901, 10, 25, 150, NOW(), NOW());

INSERT INTO article_tags (article_id, tag) VALUES (1, 'RESTAURANT'), (1, 'CAFE'), (1, 'TRAVEL_SPOT');

INSERT INTO article_photos (article_id, photo_url, photo_order) VALUES
                                                                    (1, 'https://www.esquirekorea.co.kr/resources_old/online/org_online_image/eq/afcd68be-5e61-4ec0-832f-b572c13634c0.jpg', 0),
                                                                    (1, 'https://d12zq4w4guyljn.cloudfront.net/750_750_20250603084601_photo1_66362ea9f767.webp', 1),
                                                                    (1, 'https://ggc.ggcf.kr/uploadimg/resize/ce9fb7f81544103150251.jpeg', 2);

INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (1, 1, '신라갈비', '경기도 수원시 영통구 동수원로 538', '수원 3대 갈비 맛집'),
       (1, 2, '누크녹', '경기도 수원시 팔달구 화서문로31번길 14-32 1 2층', '누크녹은 골목에 위치해 있지만, 멀리서도 존재감 퐁퐁'),
       (1, 3, '만석공원', '경기도 수원시 장안구 송죽동 248', '밥 먹고 느즈막히 산책하기');


-- =================================================================================================
-- 게시글 2: 부산광역시 해운대구 (region_id = 209)
-- =================================================================================================
INSERT INTO articles (article_id, title, content, user_id, region_id, scrap_count, like_count, view_count, created_at, updated_at)
VALUES (2, '부산 해변 기차 여행', '해운대에서 송정까지, 해변을 따라 달리는 기차는 정말 낭만적이었어요!', 2, 209, 5, 15, 100, NOW(), NOW());

INSERT INTO article_tags (article_id, tag) VALUES (2, 'TRAVEL_SPOT');

INSERT INTO article_photos (article_id, photo_url, photo_order) VALUES
                                                                    (2, 'https://www.visitbusan.net/uploadImgs/files/cntnts/20191227195613061_oen', 0),
                                                                    (2, 'https://tourimage.interpark.com/BBS/Tour/FckUpload/201706/6363304827248806650.jpg', 1),
                                                                    (2, 'https://cdn.kbthink.com/content/dam/tam-dcp-cms/kbcontent/%EC%9E%90%EC%82%B0%EA%B4%80%EB%A6%AC/%EB%94%94%EC%BD%98-%EC%82%AC%EC%97%85%EC%9E%90-%EC%BD%98%ED%85%90%EC%B8%A0/%EC%83%81%EA%B6%8C%EB%B6%84%EC%84%9D/jeonpodong/Jeonpodong-MO-03-1.jpg', 2);

INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (2, 1, '해운대 블루라인파크', '부산광역시 해운대구 중동 948-1', '해변을 따라 달리는 스카이캡슐과 해변열차가 있습니다.'),
       (2, 2, '송정해수욕장', '부산광역시 해운대구 송정동', '서핑으로 유명한 비교적 한적한 해변입니다.');


-- =================================================================================================
-- 게시글 3: 경상남도 창원시 (region_id = 1601)
-- =================================================================================================
INSERT INTO articles (article_id, title, content, user_id, region_id, scrap_count, like_count, view_count, created_at, updated_at)
VALUES (3, '경남 창원 아름다운 길', '창원의 숨겨진 길을 따라 걸으며 새로운 풍경을 발견했습니다.', 1, 1601, 7, 18, 90, NOW(), NOW());

INSERT INTO article_tags (article_id, tag) VALUES (3, 'TRAVEL_SPOT');

INSERT INTO article_photos (article_id, photo_url, photo_order) VALUES
                                                                    (3, 'https://culture.changwon.go.kr/cmm/fms/getImage.do?atchFileId=FILE_000000000008746', 0),
                                                                    (3, 'https://eiec.kdi.re.kr/userdata/nara/202108/edit/aaauOBDTNDE3Kaf343HRx_1627448711563.jpg', 1);

INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (3, 1, '주남저수지', '경상남도 창원시 의창구 대산면 유등리', '철새들이 많아 아름다운 곳'),
       (3, 2, '저도', '경상남도 창원시 의창구 대산면 유등리', '한적하지만 소박하고 아름다운 곳');


-- =================================================================================================
-- 게시글 4: 제주특별자치도 제주시 (region_id = 1701)
-- =================================================================================================
INSERT INTO articles (article_id, title, content, user_id, region_id, scrap_count, like_count, view_count, created_at, updated_at)
VALUES (4, '제주의 맛집과 카페', '제주도에서 맛집과 분위기 좋은 카페를 동시에 즐겼어요.', 2, 1701, 15, 30, 200, NOW(), NOW());

INSERT INTO article_tags (article_id, tag) VALUES (4, 'RESTAURANT'), (4, 'CAFE');

INSERT INTO article_photos (article_id, photo_url, photo_order) VALUES
                                                                    (4, 'https://digitalchosun.dizzo.com/site/data/img_dir/2021/03/05/2021030580086_0.jpg', 0),
                                                                    (4, 'https://i.ytimg.com/vi/AdnXgPYZgR8/maxresdefault.jpg', 1);

INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (4, 1, '제주 맛집', '제주특별자치도 제주시 어딘가', '제주 전통의 맛'),
       (4, 2, '제주 맛집', '제주특별자치도 제주시 어딘가', '제주 특산물의 맛');


-- 3. 팔로우(FOLLOW) 테스트 데이터
-- =================================================================================================
-- 1번 유저가 2번 유저를 팔로우
INSERT INTO follow (follower_id, following_id, created_at) VALUES (1, 2, NOW());
-- 2번 유저가 1번 유저를 팔로우 (맞팔)
INSERT INTO follow (follower_id, following_id, created_at) VALUES (2, 1, NOW());
-- 3번 유저가 1번 유저를 팔로우
INSERT INTO follow (follower_id, following_id, created_at) VALUES (3, 1, NOW());

-- users 테이블의 팔로우/팔로잉 카운트 업데이트
-- 1번 유저: 팔로잉 1명, 팔로워 2명
UPDATE users SET following_count = 1, follower_count = 2 WHERE id = 1;
-- 2번 유저: 팔로잉 1명, 팔로워 1명
UPDATE users SET following_count = 1, follower_count = 1 WHERE id = 2;
-- 3번 유저: 팔로잉 1명, 팔로워 0명
UPDATE users SET following_count = 1, follower_count = 0 WHERE id = 3;