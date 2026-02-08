-- 1. 카테고리 데이터 (LearningObjectCategory)
INSERT INTO learning_object_categories (id, main_image_url) VALUES 
('v4_engine', '/asset/v4_engine/main_image.png'),
('suspension', '/asset/suspension/main_image.png'),
('robot_gripper', '/asset/robot_gripper/main_image.png'),
('robot_arm', '/asset/robot_arm/main_image.png'),
('machine_vice', '/asset/machine_vice/main_image.png'),
('leaf_spring', '/asset/leaf_spring/main_image.png'),
('drone', '/asset/drone/main_image.png');

-- 2. 부품 데이터 (LearningObject) - mesh_name 추가 (GLB 파일명 기반)

-- V4 Engine (7개)
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, mesh_name, category_id) VALUES
(1, 'Piston', '왕복 운동 부품', '/asset/v4_engine/piston.glb', '/asset/v4_engine/piston.glb', 'piston', 'v4_engine'),
(2, 'Crankshaft', '회전 운동 변환 축', '/asset/v4_engine/crankshaft.glb', '/asset/v4_engine/crankshaft.glb', 'crankshaft', 'v4_engine'),
(3, 'Connecting Rod', '동력 전달 막대', '/asset/v4_engine/connecting_rod.glb', '/asset/v4_engine/connecting_rod.glb', 'connecting_rod', 'v4_engine'),
(4, 'Piston Pin', '연결 핀', '/asset/v4_engine/piston_pin.glb', '/asset/v4_engine/piston_pin.glb', 'piston_pin', 'v4_engine'),
(5, 'Piston Ring', '밀폐 링', '/asset/v4_engine/piston_ring.glb', '/asset/v4_engine/piston_ring.glb', 'piston_ring', 'v4_engine'),
(6, 'Rod Cap', '로드 캡', '/asset/v4_engine/rod_cap.glb', '/asset/v4_engine/rod_cap.glb', 'rod_cap', 'v4_engine'),
(7, 'Conrod Bolt', '체결 볼트', '/asset/v4_engine/bolt.glb', '/asset/v4_engine/bolt.glb', 'bolt', 'v4_engine');

-- Suspension (5개)
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, mesh_name, category_id) VALUES
(10, 'Suspension Base', '서스펜션의 하단 지지대 프레임', '/asset/suspension/base.glb', '/asset/suspension/base.glb', 'base', 'suspension'),
(11, 'Coil Spring', '노면의 충격을 흡수하는 코일 스프링', '/asset/suspension/spring.glb', '/asset/suspension/spring.glb', 'spring', 'suspension'),
(12, 'Shock Rod', '충격 흡수기(쇼바)의 중심 축', '/asset/suspension/rod.glb', '/asset/suspension/rod.glb', 'rod', 'suspension'),
(13, 'Lock Nut', '부품을 단단히 고정하는 너트', '/asset/suspension/nut.glb', '/asset/suspension/nut.glb', 'nut', 'suspension'),
(14, 'Fixing Pin', '부품 간 연결을 위한 고정 핀', '/asset/suspension/nit.glb', '/asset/suspension/nit.glb', 'nit', 'suspension');

-- Robot Gripper (8개)
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, mesh_name, category_id) VALUES
(20, 'Base Plate', '로봇 그리퍼를 바닥에 고정하는 메인 판', '/asset/robot_gripper/base_plate.glb', '/asset/robot_gripper/base_plate.glb', 'base_plate', 'robot_gripper'),
(21, 'Base Mounting Bracket', '베이스와 그리퍼 본체를 연결하는 브라켓', '/asset/robot_gripper/base_bracket.glb', '/asset/robot_gripper/base_bracket.glb', 'bracket', 'robot_gripper'),
(22, 'Gear Link A', '동력을 전달하는 첫 번째 기어 링크', '/asset/robot_gripper/gear_link_1.glb', '/asset/robot_gripper/gear_link_1.glb', 'gear_link_1', 'robot_gripper'),
(23, 'Gear Link B', '맞물려 돌아가는 두 번째 기어 링크', '/asset/robot_gripper/gear_link_2.glb', '/asset/robot_gripper/gear_link_2.glb', 'gear_link_2', 'robot_gripper'),
(24, 'Connecting Link', '관절을 이어주는 연결 링크', '/asset/robot_gripper/link.glb', '/asset/robot_gripper/link.glb', 'link', 'robot_gripper'),
(25, 'Gripper Jaw', '물체를 실제로 집는 집게 부분', '/asset/robot_gripper/gripper.glb', '/asset/robot_gripper/gripper.glb', 'Gripper', 'robot_gripper'),
(26, 'Fixing Pin', '각 부품을 고정하는 회전 핀', '/asset/robot_gripper/pin.glb', '/asset/robot_gripper/pin.glb', 'pin', 'robot_gripper'),
(27, 'Base Gear', '베이스 부분에 위치한 기어 부품', '/asset/robot_gripper/base_gear.glb', '/asset/robot_gripper/base_gear.glb', 'base_gear', 'robot_gripper');

-- Robot Arm (8개)
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, mesh_name, category_id) VALUES
(30, 'Robot Base', '로봇 팔의 회전 바닥 (Base)', '/asset/robot_arm/base.glb', '/asset/robot_arm/base.glb', 'base', 'robot_arm'),
(31, 'Arm Part 2', '로봇 팔 연결 부품 2', '/asset/robot_arm/part_2.glb', '/asset/robot_arm/part_2.glb', 'part_2', 'robot_arm'),
(32, 'Arm Part 3', '로봇 팔 연결 부품 3', '/asset/robot_arm/part_3.glb', '/asset/robot_arm/part_3.glb', 'part_3', 'robot_arm'),
(33, 'Arm Part 4', '로봇 팔 연결 부품 4', '/asset/robot_arm/part_4.glb', '/asset/robot_arm/part_4.glb', 'part_4', 'robot_arm'),
(34, 'Arm Part 5', '로봇 팔 연결 부품 5', '/asset/robot_arm/part_5.glb', '/asset/robot_arm/part_5.glb', 'part_5', 'robot_arm'),
(35, 'Arm Part 6', '로봇 팔 연결 부품 6', '/asset/robot_arm/part_6.glb', '/asset/robot_arm/part_6.glb', 'part_6', 'robot_arm'),
(36, 'Arm Part 7', '로봇 팔 연결 부품 7', '/asset/robot_arm/part_7.glb', '/asset/robot_arm/part_7.glb', 'part_7', 'robot_arm'),
(37, 'Arm Part 8', '로봇 팔 연결 부품 8', '/asset/robot_arm/part_8.glb', '/asset/robot_arm/part_8.glb', 'part_8', 'robot_arm');

-- Machine Vice (10개)
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, mesh_name, category_id) VALUES
(40, 'Guide Part', '바이스의 이동을 돕는 가이드 부품', '/asset/machine_vice/part_1_guide.glb', '/asset/machine_vice/part_1_guide.glb', 'part_1_guide', 'machine_vice'),
(41, 'Vice Handle', '바이스를 조이고 푸는 회전 핸들', '/asset/machine_vice/part_1.glb', '/asset/machine_vice/part_1.glb', 'part_1', 'machine_vice'),
(42, 'Fixed Jaw', '움직이지 않고 고정된 턱', '/asset/machine_vice/part_2.glb', '/asset/machine_vice/part_2.glb', 'part_2', 'machine_vice'),
(43, 'Movable Jaw', '스핀들에 의해 앞뒤로 움직이는 턱', '/asset/machine_vice/part_3.glb', '/asset/machine_vice/part_3.glb', 'part_3', 'machine_vice'),
(44, 'Spindle Base', '스핀들을 지지하는 소켓 베이스', '/asset/machine_vice/part_4.glb', '/asset/machine_vice/part_4.glb', 'part_4', 'machine_vice'),
(45, 'Clamping Jaw', '물체를 직접 물어 고정하는 조임 턱', '/asset/machine_vice/part_5.glb', '/asset/machine_vice/part_5.glb', 'part_5', 'machine_vice'),
(46, 'Guide Rail', '이동 죠가 미끄러지듯 움직이는 레일', '/asset/machine_vice/part_6.glb', '/asset/machine_vice/part_6.glb', 'part_6', 'machine_vice'),
(47, 'Spindle Shaft', '회전력을 직선 운동으로 바꾸는 나사 축', '/asset/machine_vice/part_7.glb', '/asset/machine_vice/part_7.glb', 'part_7', 'machine_vice'),
(48, 'Base Plate', '바이스 전체를 지지하는 바닥판', '/asset/machine_vice/part_8.glb', '/asset/machine_vice/part_8.glb', 'part_8', 'machine_vice'),
(49, 'Pressure Sleeve', '축의 압력을 전달하는 슬리브', '/asset/machine_vice/part_9.glb', '/asset/machine_vice/part_9.glb', 'part_9', 'machine_vice');

-- Leaf Spring (9개)
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, mesh_name, category_id) VALUES
(50, 'Center Clamp', '판스프링 중앙을 고정하는 클램프', '/asset/leaf_spring/clamp_center.glb', '/asset/leaf_spring/clamp_center.glb', 'clamp_center', 'leaf_spring'),
(51, 'Primary Clamp', '메인 고정 클램프', '/asset/leaf_spring/clamp_primary.glb', '/asset/leaf_spring/clamp_primary.glb', 'clamp_primary', 'leaf_spring'),
(52, 'Secondary Clamp', '보조 고정 클램프', '/asset/leaf_spring/clamp_secondary.glb', '/asset/leaf_spring/clamp_secondary.glb', 'clamp_secondary', 'leaf_spring'),
(53, 'Leaf Layer', '충격을 흡수하는 판스프링 레이어', '/asset/leaf_spring/leaf_layer.glb', '/asset/leaf_spring/leaf_layer.glb', 'leaf_layer', 'leaf_spring'),
(54, 'Rigid Chassis Support', '단단하게 고정되는 섀시 지지대', '/asset/leaf_spring/support_chassis_rigid.glb', '/asset/leaf_spring/support_chassis_rigid.glb', 'support_chassis_rigid', 'leaf_spring'),
(55, 'Chassis Support', '차체와 연결되는 지지대', '/asset/leaf_spring/support_chassis.glb', '/asset/leaf_spring/support_chassis.glb', 'support_chassis', 'leaf_spring'),
(56, 'Rubber Bushing (60mm)', '진동을 흡수하는 60mm 고무 부싱', '/asset/leaf_spring/support_rubber_60mm.glb', '/asset/leaf_spring/support_rubber_60mm.glb', 'support_rubber_60mm', 'leaf_spring'),
(57, 'Rubber Bushing', '진동 흡수 고무 부품', '/asset/leaf_spring/support_rubber.glb', '/asset/leaf_spring/support_rubber.glb', 'support_rubber', 'leaf_spring'),
(58, 'Main Support', '스프링 전체를 받쳐주는 지지대', '/asset/leaf_spring/support.glb', '/asset/leaf_spring/support.glb', 'support', 'leaf_spring');

-- Drone (10개)
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, mesh_name, category_id) VALUES
(60, 'Main Frame', '드론의 중심이 되는 메인 바디', '/asset/drone/main_frame.glb', '/asset/drone/main_frame.glb', 'main_frame', 'drone'),
(61, 'Main Frame (Mirror)', '대칭형 메인 프레임 파트', '/asset/drone/main_frame_mir.glb', '/asset/drone/main_frame_mir.glb', 'main_frame_mir', 'drone'),
(62, 'Landing Leg', '착륙 시 충격을 흡수하는 다리', '/asset/drone/leg.glb', '/asset/drone/leg.glb', 'leg', 'drone'),
(63, 'Impeller Blade', '양력을 발생시키는 프로펠러', '/asset/drone/impeller_blade.glb', '/asset/drone/impeller_blade.glb', 'impeller_blade', 'drone'),
(64, 'Arm Gear', '암(Arm) 각도를 조절하는 기어', '/asset/drone/arm_gear.glb', '/asset/drone/arm_gear.glb', 'arm_gear', 'drone'),
(65, 'Internal Gearing', '동력을 전달하는 내부 기어 뭉치', '/asset/drone/gearing.glb', '/asset/drone/gearing.glb', 'gearing', 'drone'),
(66, 'Beater Disc', '회전 동작을 제어하는 디스크', '/asset/drone/beater_disc.glb', '/asset/drone/beater_disc.glb', 'beater_disc', 'drone'),
(67, 'Fixing Nut', '부품 고정용 너트', '/asset/drone/nut.glb', '/asset/drone/nut.glb', 'nut', 'drone'),
(68, 'Assembly Screw', '부품 체결용 스크류', '/asset/drone/screw.glb', '/asset/drone/screw.glb', 'screw', 'drone'),
(69, 'XYZ Sensor Module', '위치/자세 제어 센서 모듈', '/asset/drone/xyz.glb', '/asset/drone/xyz.glb', 'xyz', 'drone');
