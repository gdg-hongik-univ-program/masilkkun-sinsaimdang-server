-- 테스트용 사용자 데이터 (비밀번호는 'password'를 BCrypt로 암호화한 값)
INSERT INTO users (id, email, password, name, nickname, role, created_at, updated_at)
VALUES (1, 'user1@example.com', '$2a$10$y.CVG.xSnaRwv2gG.3eK/eY9Z9C.Y4G/2N2b2d.Y4G/2N2b2d.Y4', '김마실', '여행가 마실쿤', 'USER', NOW(), NOW());
INSERT INTO users (id, email, password, name, nickname, role, created_at, updated_at)
VALUES (2, 'user2@example.com', '$2a$10$y.CVG.xSnaRwv2gG.3eK/eY9Z9C.Y4G/2N2b2d.Y4G/2N2b2d.Y4', '박사진', '사진작가 제이', 'USER', NOW(), NOW());

-- 테스트용 게시글 데이터 (ID=1), 작성자는 1번 유저
INSERT INTO articles (title, content, user_id, region, scrap_count, like_count, view_count, created_at, updated_at)
VALUES ('수원의 숨겨진 맛집 탐방', '수원의 잘 알려지지 않은 맛집들을 찾아가 봤습니다. 정말 놀라운 경험이었어요!', 1, '수원', 10, 25, 150, NOW(), NOW());

-- 게시글 1의 태그 데이터
INSERT INTO article_tags (article_id, tag) VALUES (1, 'RESTAURANT');
INSERT INTO article_tags (article_id, tag) VALUES (1, 'CAFE');
INSERT INTO article_tags (article_id, tag) VALUES (1, 'TRAVEL_SPOT');

-- 게시글 1의 사진 데이터
INSERT INTO article_photos (article_id, photo_url) VALUES (1, 'https://www.esquirekorea.co.kr/resources_old/online/org_online_image/eq/afcd68be-5e61-4ec0-832f-b572c13634c0.jpg');
INSERT INTO article_photos (article_id, photo_url) VALUES (1, 'https://d12zq4w4guyljn.cloudfront.net/750_750_20250603084601_photo1_66362ea9f767.webp');
INSERT INTO article_photos (article_id, photo_url) VALUES (1, 'https://ggc.ggcf.kr/uploadimg/resize/ce9fb7f81544103150251.jpeg');

-- 게시글 1의 장소 경로 데이터
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (1, 1, '신라갈비', '경기도 수원시 영통구 동수원로 538', '수원 3대 갈비 맛집');
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (1, 2, '누크녹', '경기 수원시 팔달구 화서문로31번길 14-32 1 2층', '누크녹은 골목에 위치해 있지만, 멀리서도 존재감 퐁퐁');
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (1, 3, '만석공원', '경기 수원시 장안구 송죽동 248', '밥 먹고 느즈막히 산책하기');

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
VALUES ('경남 창원 아름다운 길', '창원의 숨겨진 길을 따라 걸으며 새로운 풍경을 발견했습니다.', 1, '창원', 7, 18, 90, NOW(), NOW());
INSERT INTO article_tags (article_id, tag) VALUES (3, 'TRAVEL_SPOT');
INSERT INTO article_photos (article_id, photo_url) VALUES (3, 'https://culture.changwon.go.kr/cmm/fms/getImage.do?atchFileId=FILE_000000000008746');
INSERT INTO article_photos (article_id, photo_url) VALUES (3, 'https://eiec.kdi.re.kr/userdata/nara/202108/edit/aaauOBDTNDE3Kaf343HRx_1627448711563.jpg');
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (3, 1, '주남저수지', '경남 창원시 의창구 대산면 유등리', '철새들이 많아 아름다운 곳');
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (3, 2, '저도', '경남 창원시 의창구 대산면 유등리', '한적하지만 소박하고 아름다운 곳');

-- 테스트용 게시글 데이터 (ID=4), 작성자는 2번 유저, 태그가 2개 (AND 조건 테스트용)
INSERT INTO articles (title, content, user_id, region, scrap_count, like_count, view_count, created_at, updated_at)
VALUES ('제주의 맛집과 카페', '제주도에서 맛집과 분위기 좋은 카페를 동시에 즐겼어요.', 2, '제주', 15, 30, 200, NOW(), NOW());
INSERT INTO article_tags (article_id, tag) VALUES (4, 'RESTAURANT');
INSERT INTO article_tags (article_id, tag) VALUES (4, 'CAFE');
INSERT INTO article_photos (article_id, photo_url) VALUES (4, 'https://digitalchosun.dizzo.com/site/data/img_dir/2021/03/05/2021030580086_0.jpg');
INSERT INTO article_photos (article_id, photo_url) VALUES (4, 'https://i.ytimg.com/vi/AdnXgPYZgR8/maxresdefault.jpg');
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (4, 1, '제주 맛집', '제주시 어딘가', '제주 전통의 맛');
INSERT INTO article_places (article_id, place_order, place_name, address, description)
VALUES (4, 2, '제주 맛집', '제주시 어딘가', '제주 특산물의 맛');