-- 대한민국 최상위 행정구역 (17개)
INSERT INTO regions (id, name, parent_id) VALUES (1, '서울특별시', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (2, '부산광역시', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (3, '대구광역시', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (4, '인천광역시', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (5, '광주광역시', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (6, '대전광역시', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (7, '울산광역시', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (8, '세종특별자치시', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (9, '경기도', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (10, '강원특별자치도', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (11, '충청북도', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (12, '충청남도', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (13, '전북특별자치도', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (14, '전라남도', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (15, '경상북도', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (16, '경상남도', NULL);
INSERT INTO regions (id, name, parent_id) VALUES (17, '제주특별자치도', NULL);

-- 서울특별시 (25개 구)
INSERT INTO regions (id, name, parent_id) SELECT 101, '종로구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 101);
INSERT INTO regions (id, name, parent_id) SELECT 102, '중구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 102);
INSERT INTO regions (id, name, parent_id) SELECT 103, '용산구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 103);
INSERT INTO regions (id, name, parent_id) SELECT 104, '성동구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 104);
INSERT INTO regions (id, name, parent_id) SELECT 105, '광진구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 105);
INSERT INTO regions (id, name, parent_id) SELECT 106, '동대문구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 106);
INSERT INTO regions (id, name, parent_id) SELECT 107, '중랑구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 107);
INSERT INTO regions (id, name, parent_id) SELECT 108, '성북구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 108);
INSERT INTO regions (id, name, parent_id) SELECT 109, '강북구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 109);
INSERT INTO regions (id, name, parent_id) SELECT 110, '도봉구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 110);
INSERT INTO regions (id, name, parent_id) SELECT 111, '노원구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 111);
INSERT INTO regions (id, name, parent_id) SELECT 112, '은평구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 112);
INSERT INTO regions (id, name, parent_id) SELECT 113, '서대문구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 113);
INSERT INTO regions (id, name, parent_id) SELECT 114, '마포구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 114);
INSERT INTO regions (id, name, parent_id) SELECT 115, '양천구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 115);
INSERT INTO regions (id, name, parent_id) SELECT 116, '강서구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 116);
INSERT INTO regions (id, name, parent_id) SELECT 117, '구로구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 117);
INSERT INTO regions (id, name, parent_id) SELECT 118, '금천구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 118);
INSERT INTO regions (id, name, parent_id) SELECT 119, '영등포구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 119);
INSERT INTO regions (id, name, parent_id) SELECT 120, '동작구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 120);
INSERT INTO regions (id, name, parent_id) SELECT 121, '관악구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 121);
INSERT INTO regions (id, name, parent_id) SELECT 122, '서초구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 122);
INSERT INTO regions (id, name, parent_id) SELECT 123, '강남구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 123);
INSERT INTO regions (id, name, parent_id) SELECT 124, '송파구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 124);
INSERT INTO regions (id, name, parent_id) SELECT 125, '강동구', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 125);

-- 부산광역시 (15개 구, 1개 군)
INSERT INTO regions (id, name, parent_id) SELECT 201, '중구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 201);
INSERT INTO regions (id, name, parent_id) SELECT 202, '서구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 202);
INSERT INTO regions (id, name, parent_id) SELECT 203, '동구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 203);
INSERT INTO regions (id, name, parent_id) SELECT 204, '영도구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 204);
INSERT INTO regions (id, name, parent_id) SELECT 205, '부산진구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 205);
INSERT INTO regions (id, name, parent_id) SELECT 206, '동래구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 206);
INSERT INTO regions (id, name, parent_id) SELECT 207, '남구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 207);
INSERT INTO regions (id, name, parent_id) SELECT 208, '북구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 208);
INSERT INTO regions (id, name, parent_id) SELECT 209, '해운대구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 209);
INSERT INTO regions (id, name, parent_id) SELECT 210, '사하구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 210);
INSERT INTO regions (id, name, parent_id) SELECT 211, '금정구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 211);
INSERT INTO regions (id, name, parent_id) SELECT 212, '강서구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 212);
INSERT INTO regions (id, name, parent_id) SELECT 213, '연제구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 213);
INSERT INTO regions (id, name, parent_id) SELECT 214, '수영구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 214);
INSERT INTO regions (id, name, parent_id) SELECT 215, '사상구', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 215);
INSERT INTO regions (id, name, parent_id) SELECT 216, '기장군', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 216);

-- 대구광역시 (7개 구, 2개 군)
INSERT INTO regions (id, name, parent_id) SELECT 301, '중구', 3 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 301);
INSERT INTO regions (id, name, parent_id) SELECT 302, '동구', 3 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 302);
INSERT INTO regions (id, name, parent_id) SELECT 303, '서구', 3 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 303);
INSERT INTO regions (id, name, parent_id) SELECT 304, '남구', 3 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 304);
INSERT INTO regions (id, name, parent_id) SELECT 305, '북구', 3 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 305);
INSERT INTO regions (id, name, parent_id) SELECT 306, '수성구', 3 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 306);
INSERT INTO regions (id, name, parent_id) SELECT 307, '달서구', 3 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 307);
INSERT INTO regions (id, name, parent_id) SELECT 308, '달성군', 3 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 308);
INSERT INTO regions (id, name, parent_id) SELECT 309, '군위군', 3 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 309);

-- 인천광역시 (8개 구, 2개 군)
INSERT INTO regions (id, name, parent_id) SELECT 401, '중구', 4 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 401);
INSERT INTO regions (id, name, parent_id) SELECT 402, '동구', 4 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 402);
INSERT INTO regions (id, name, parent_id) SELECT 403, '미추홀구', 4 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 403);
INSERT INTO regions (id, name, parent_id) SELECT 404, '연수구', 4 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 404);
INSERT INTO regions (id, name, parent_id) SELECT 405, '남동구', 4 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 405);
INSERT INTO regions (id, name, parent_id) SELECT 406, '부평구', 4 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 406);
INSERT INTO regions (id, name, parent_id) SELECT 407, '계양구', 4 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 407);
INSERT INTO regions (id, name, parent_id) SELECT 408, '서구', 4 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 408);
INSERT INTO regions (id, name, parent_id) SELECT 409, '강화군', 4 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 409);
INSERT INTO regions (id, name, parent_id) SELECT 410, '옹진군', 4 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 410);

-- 광주광역시 (5개 구)
INSERT INTO regions (id, name, parent_id) SELECT 501, '동구', 5 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 501);
INSERT INTO regions (id, name, parent_id) SELECT 502, '서구', 5 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 502);
INSERT INTO regions (id, name, parent_id) SELECT 503, '남구', 5 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 503);
INSERT INTO regions (id, name, parent_id) SELECT 504, '북구', 5 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 504);
INSERT INTO regions (id, name, parent_id) SELECT 505, '광산구', 5 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 505);

-- 대전광역시 (5개 구)
INSERT INTO regions (id, name, parent_id) SELECT 601, '동구', 6 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 601);
INSERT INTO regions (id, name, parent_id) SELECT 602, '중구', 6 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 602);
INSERT INTO regions (id, name, parent_id) SELECT 603, '서구', 6 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 603);
INSERT INTO regions (id, name, parent_id) SELECT 604, '유성구', 6 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 604);
INSERT INTO regions (id, name, parent_id) SELECT 605, '대덕구', 6 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 605);

-- 울산광역시 (4개 구, 1개 군)
INSERT INTO regions (id, name, parent_id) SELECT 701, '중구', 7 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 701);
INSERT INTO regions (id, name, parent_id) SELECT 702, '남구', 7 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 702);
INSERT INTO regions (id, name, parent_id) SELECT 703, '동구', 7 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 703);
INSERT INTO regions (id, name, parent_id) SELECT 704, '북구', 7 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 704);
INSERT INTO regions (id, name, parent_id) SELECT 705, '울주군', 7 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 705);

-- 경기도 (28개 시, 3개 군)
INSERT INTO regions (id, name, parent_id) SELECT 901, '수원시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 901);
INSERT INTO regions (id, name, parent_id) SELECT 902, '성남시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 902);
INSERT INTO regions (id, name, parent_id) SELECT 903, '의정부시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 903);
INSERT INTO regions (id, name, parent_id) SELECT 904, '안양시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 904);
INSERT INTO regions (id, name, parent_id) SELECT 905, '부천시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 905);
INSERT INTO regions (id, name, parent_id) SELECT 906, '광명시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 906);
INSERT INTO regions (id, name, parent_id) SELECT 907, '평택시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 907);
INSERT INTO regions (id, name, parent_id) SELECT 908, '동두천시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 908);
INSERT INTO regions (id, name, parent_id) SELECT 909, '안산시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 909);
INSERT INTO regions (id, name, parent_id) SELECT 910, '고양시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 910);
INSERT INTO regions (id, name, parent_id) SELECT 911, '과천시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 911);
INSERT INTO regions (id, name, parent_id) SELECT 912, '구리시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 912);
INSERT INTO regions (id, name, parent_id) SELECT 913, '남양주시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 913);
INSERT INTO regions (id, name, parent_id) SELECT 914, '오산시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 914);
INSERT INTO regions (id, name, parent_id) SELECT 915, '시흥시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 915);
INSERT INTO regions (id, name, parent_id) SELECT 916, '군포시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 916);
INSERT INTO regions (id, name, parent_id) SELECT 917, '의왕시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 917);
INSERT INTO regions (id, name, parent_id) SELECT 918, '하남시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 918);
INSERT INTO regions (id, name, parent_id) SELECT 919, '용인시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 919);
INSERT INTO regions (id, name, parent_id) SELECT 920, '파주시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 920);
INSERT INTO regions (id, name, parent_id) SELECT 921, '이천시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 921);
INSERT INTO regions (id, name, parent_id) SELECT 922, '안성시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 922);
INSERT INTO regions (id, name, parent_id) SELECT 923, '김포시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 923);
INSERT INTO regions (id, name, parent_id) SELECT 924, '화성시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 924);
INSERT INTO regions (id, name, parent_id) SELECT 925, '광주시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 925);
INSERT INTO regions (id, name, parent_id) SELECT 926, '양주시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 926);
INSERT INTO regions (id, name, parent_id) SELECT 927, '포천시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 927);
INSERT INTO regions (id, name, parent_id) SELECT 928, '여주시', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 928);
INSERT INTO regions (id, name, parent_id) SELECT 929, '연천군', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 929);
INSERT INTO regions (id, name, parent_id) SELECT 930, '가평군', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 930);
INSERT INTO regions (id, name, parent_id) SELECT 931, '양평군', 9 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 931);

-- 강원특별자치도 (7개 시, 11개 군)
INSERT INTO regions (id, name, parent_id) SELECT 1001, '춘천시', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1001);
INSERT INTO regions (id, name, parent_id) SELECT 1002, '원주시', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1002);
INSERT INTO regions (id, name, parent_id) SELECT 1003, '강릉시', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1003);
INSERT INTO regions (id, name, parent_id) SELECT 1004, '동해시', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1004);
INSERT INTO regions (id, name, parent_id) SELECT 1005, '태백시', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1005);
INSERT INTO regions (id, name, parent_id) SELECT 1006, '속초시', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1006);
INSERT INTO regions (id, name, parent_id) SELECT 1007, '삼척시', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1007);
INSERT INTO regions (id, name, parent_id) SELECT 1008, '홍천군', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1008);
INSERT INTO regions (id, name, parent_id) SELECT 1009, '횡성군', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1009);
INSERT INTO regions (id, name, parent_id) SELECT 1010, '영월군', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1010);
INSERT INTO regions (id, name, parent_id) SELECT 1011, '평창군', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1011);
INSERT INTO regions (id, name, parent_id) SELECT 1012, '정선군', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1012);
INSERT INTO regions (id, name, parent_id) SELECT 1013, '철원군', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1013);
INSERT INTO regions (id, name, parent_id) SELECT 1014, '화천군', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1014);
INSERT INTO regions (id, name, parent_id) SELECT 1015, '양구군', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1015);
INSERT INTO regions (id, name, parent_id) SELECT 1016, '인제군', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1016);
INSERT INTO regions (id, name, parent_id) SELECT 1017, '고성군', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1017);
INSERT INTO regions (id, name, parent_id) SELECT 1018, '양양군', 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1018);

-- 충청북도 (3개 시, 8개 군)
INSERT INTO regions (id, name, parent_id) SELECT 1101, '청주시', 11 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1101);
INSERT INTO regions (id, name, parent_id) SELECT 1102, '충주시', 11 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1102);
INSERT INTO regions (id, name, parent_id) SELECT 1103, '제천시', 11 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1103);
INSERT INTO regions (id, name, parent_id) SELECT 1104, '보은군', 11 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1104);
INSERT INTO regions (id, name, parent_id) SELECT 1105, '옥천군', 11 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1105);
INSERT INTO regions (id, name, parent_id) SELECT 1106, '영동군', 11 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1106);
INSERT INTO regions (id, name, parent_id) SELECT 1107, '증평군', 11 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1107);
INSERT INTO regions (id, name, parent_id) SELECT 1108, '진천군', 11 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1108);
INSERT INTO regions (id, name, parent_id) SELECT 1109, '괴산군', 11 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1109);
INSERT INTO regions (id, name, parent_id) SELECT 1110, '음성군', 11 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1110);
INSERT INTO regions (id, name, parent_id) SELECT 1111, '단양군', 11 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1111);

-- 충청남도 (8개 시, 7개 군)
INSERT INTO regions (id, name, parent_id) SELECT 1201, '천안시', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1201);
INSERT INTO regions (id, name, parent_id) SELECT 1202, '공주시', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1202);
INSERT INTO regions (id, name, parent_id) SELECT 1203, '보령시', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1203);
INSERT INTO regions (id, name, parent_id) SELECT 1204, '아산시', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1204);
INSERT INTO regions (id, name, parent_id) SELECT 1205, '서산시', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1205);
INSERT INTO regions (id, name, parent_id) SELECT 1206, '논산시', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1206);
INSERT INTO regions (id, name, parent_id) SELECT 1207, '계룡시', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1207);
INSERT INTO regions (id, name, parent_id) SELECT 1208, '당진시', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1208);
INSERT INTO regions (id, name, parent_id) SELECT 1209, '금산군', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1209);
INSERT INTO regions (id, name, parent_id) SELECT 1210, '부여군', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1210);
INSERT INTO regions (id, name, parent_id) SELECT 1211, '서천군', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1211);
INSERT INTO regions (id, name, parent_id) SELECT 1212, '청양군', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1212);
INSERT INTO regions (id, name, parent_id) SELECT 1213, '홍성군', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1213);
INSERT INTO regions (id, name, parent_id) SELECT 1214, '예산군', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1214);
INSERT INTO regions (id, name, parent_id) SELECT 1215, '태안군', 12 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1215);

-- 전북특별자치도 (6개 시, 8개 군)
INSERT INTO regions (id, name, parent_id) SELECT 1301, '전주시', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1301);
INSERT INTO regions (id, name, parent_id) SELECT 1302, '군산시', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1302);
INSERT INTO regions (id, name, parent_id) SELECT 1303, '익산시', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1303);
INSERT INTO regions (id, name, parent_id) SELECT 1304, '정읍시', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1304);
INSERT INTO regions (id, name, parent_id) SELECT 1305, '남원시', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1305);
INSERT INTO regions (id, name, parent_id) SELECT 1306, '김제시', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1306);
INSERT INTO regions (id, name, parent_id) SELECT 1307, '완주군', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1307);
INSERT INTO regions (id, name, parent_id) SELECT 1308, '진안군', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1308);
INSERT INTO regions (id, name, parent_id) SELECT 1309, '무주군', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1309);
INSERT INTO regions (id, name, parent_id) SELECT 1310, '장수군', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1310);
INSERT INTO regions (id, name, parent_id) SELECT 1311, '임실군', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1311);
INSERT INTO regions (id, name, parent_id) SELECT 1312, '순창군', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1312);
INSERT INTO regions (id, name, parent_id) SELECT 1313, '고창군', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1313);
INSERT INTO regions (id, name, parent_id) SELECT 1314, '부안군', 13 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1314);

-- 전라남도 (5개 시, 17개 군)
INSERT INTO regions (id, name, parent_id) SELECT 1401, '목포시', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1401);
INSERT INTO regions (id, name, parent_id) SELECT 1402, '여수시', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1402);
INSERT INTO regions (id, name, parent_id) SELECT 1403, '순천시', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1403);
INSERT INTO regions (id, name, parent_id) SELECT 1404, '나주시', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1404);
INSERT INTO regions (id, name, parent_id) SELECT 1405, '광양시', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1405);
INSERT INTO regions (id, name, parent_id) SELECT 1406, '담양군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1406);
INSERT INTO regions (id, name, parent_id) SELECT 1407, '곡성군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1407);
INSERT INTO regions (id, name, parent_id) SELECT 1408, '구례군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1408);
INSERT INTO regions (id, name, parent_id) SELECT 1409, '고흥군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1409);
INSERT INTO regions (id, name, parent_id) SELECT 1410, '보성군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1410);
INSERT INTO regions (id, name, parent_id) SELECT 1411, '화순군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1411);
INSERT INTO regions (id, name, parent_id) SELECT 1412, '장흥군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1412);
INSERT INTO regions (id, name, parent_id) SELECT 1413, '강진군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1413);
INSERT INTO regions (id, name, parent_id) SELECT 1414, '해남군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1414);
INSERT INTO regions (id, name, parent_id) SELECT 1415, '영암군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1415);
INSERT INTO regions (id, name, parent_id) SELECT 1416, '무안군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1416);
INSERT INTO regions (id, name, parent_id) SELECT 1417, '함평군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1417);
INSERT INTO regions (id, name, parent_id) SELECT 1418, '영광군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1418);
INSERT INTO regions (id, name, parent_id) SELECT 1419, '장성군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1419);
INSERT INTO regions (id, name, parent_id) SELECT 1420, '완도군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1420);
INSERT INTO regions (id, name, parent_id) SELECT 1421, '진도군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1421);
INSERT INTO regions (id, name, parent_id) SELECT 1422, '신안군', 14 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1422);

-- 경상북도 (10개 시, 12개 군)
INSERT INTO regions (id, name, parent_id) SELECT 1501, '포항시', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1501);
INSERT INTO regions (id, name, parent_id) SELECT 1502, '경주시', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1502);
INSERT INTO regions (id, name, parent_id) SELECT 1503, '김천시', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1503);
INSERT INTO regions (id, name, parent_id) SELECT 1504, '안동시', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1504);
INSERT INTO regions (id, name, parent_id) SELECT 1505, '구미시', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1505);
INSERT INTO regions (id, name, parent_id) SELECT 1506, '영주시', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1506);
INSERT INTO regions (id, name, parent_id) SELECT 1507, '영천시', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1507);
INSERT INTO regions (id, name, parent_id) SELECT 1508, '상주시', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1508);
INSERT INTO regions (id, name, parent_id) SELECT 1509, '문경시', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1509);
INSERT INTO regions (id, name, parent_id) SELECT 1510, '경산시', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1510);
INSERT INTO regions (id, name, parent_id) SELECT 1511, '의성군', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1511);
INSERT INTO regions (id, name, parent_id) SELECT 1512, '청송군', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1512);
INSERT INTO regions (id, name, parent_id) SELECT 1513, '영양군', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1513);
INSERT INTO regions (id, name, parent_id) SELECT 1514, '영덕군', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1514);
INSERT INTO regions (id, name, parent_id) SELECT 1515, '청도군', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1515);
INSERT INTO regions (id, name, parent_id) SELECT 1516, '고령군', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1516);
INSERT INTO regions (id, name, parent_id) SELECT 1517, '성주군', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1517);
INSERT INTO regions (id, name, parent_id) SELECT 1518, '칠곡군', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1518);
INSERT INTO regions (id, name, parent_id) SELECT 1519, '예천군', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1519);
INSERT INTO regions (id, name, parent_id) SELECT 1520, '봉화군', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1520);
INSERT INTO regions (id, name, parent_id) SELECT 1521, '울진군', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1521);
INSERT INTO regions (id, name, parent_id) SELECT 1522, '울릉군', 15 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1522);

-- 경상남도 (8개 시, 10개 군)
INSERT INTO regions (id, name, parent_id) SELECT 1601, '창원시', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1601);
INSERT INTO regions (id, name, parent_id) SELECT 1602, '진주시', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1602);
INSERT INTO regions (id, name, parent_id) SELECT 1603, '통영시', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1603);
INSERT INTO regions (id, name, parent_id) SELECT 1604, '사천시', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1604);
INSERT INTO regions (id, name, parent_id) SELECT 1605, '김해시', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1605);
INSERT INTO regions (id, name, parent_id) SELECT 1606, '밀양시', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1606);
INSERT INTO regions (id, name, parent_id) SELECT 1607, '거제시', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1607);
INSERT INTO regions (id, name, parent_id) SELECT 1608, '양산시', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1608);
INSERT INTO regions (id, name, parent_id) SELECT 1609, '의령군', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1609);
INSERT INTO regions (id, name, parent_id) SELECT 1610, '함안군', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1610);
INSERT INTO regions (id, name, parent_id) SELECT 1611, '창녕군', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1611);
INSERT INTO regions (id, name, parent_id) SELECT 1612, '고성군', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1612);
INSERT INTO regions (id, name, parent_id) SELECT 1613, '남해군', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1613);
INSERT INTO regions (id, name, parent_id) SELECT 1614, '하동군', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1614);
INSERT INTO regions (id, name, parent_id) SELECT 1615, '산청군', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1615);
INSERT INTO regions (id, name, parent_id) SELECT 1616, '함양군', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1616);
INSERT INTO regions (id, name, parent_id) SELECT 1617, '거창군', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1617);
INSERT INTO regions (id, name, parent_id) SELECT 1618, '합천군', 16 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1618);

-- 제주특별자치도 (2개 시)
INSERT INTO regions (id, name, parent_id) SELECT 1701, '제주시', 17 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1701);
INSERT INTO regions (id, name, parent_id) SELECT 1702, '서귀포시', 17 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM regions WHERE id = 1702);