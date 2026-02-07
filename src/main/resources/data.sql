-- 1. 카테고리 데이터 (LearningObjectCategory)
INSERT INTO learning_object_categories (id, main_image_url) VALUES 
('v4_engine', '/asset/v4_engine/main_image.png'),
('suspension', '/asset/suspension/main_image.png'),
('robot_gripper', '/asset/robot_gripper/main_image.png'),
('robot_arm', '/asset/robot_arm/main_image.png'),
('machine_vice', '/asset/machine_vice/main_image.png'),
('leaf_spring', '/asset/leaf_spring/main_image.png'),
('drone', '/asset/drone/main_image.png');

-- 2. 부품 데이터 (LearningObject)

-- V4 Engine
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, category_id) VALUES
(1, 'Piston', '왕복 운동 부품', '/asset/v4_engine/piston.glb', '/asset/v4_engine/piston.glb', 'v4_engine'),
(2, 'Crankshaft', '회전 운동 변환 축', '/asset/v4_engine/crankshaft.glb', '/asset/v4_engine/crankshaft.glb', 'v4_engine'),
(3, 'Connecting Rod', '동력 전달 막대', '/asset/v4_engine/connecting_rod.glb', '/asset/v4_engine/connecting_rod.glb', 'v4_engine'),
(4, 'Piston Pin', '연결 핀', '/asset/v4_engine/piston_pin.glb', '/asset/v4_engine/piston_pin.glb', 'v4_engine'),
(5, 'Piston Ring', '밀폐 링', '/asset/v4_engine/piston_ring.glb', '/asset/v4_engine/piston_ring.glb', 'v4_engine'),
(6, 'Rod Cap', '로드 캡', '/asset/v4_engine/rod_cap.glb', '/asset/v4_engine/rod_cap.glb', 'v4_engine'),
(7, 'Conrod Bolt', '체결 볼트', '/asset/v4_engine/bolt.glb', '/asset/v4_engine/bolt.glb', 'v4_engine');

-- Suspension
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, category_id) VALUES
(10, 'Suspension Base', '서스펜션의 하단 지지대 프레임', '/asset/suspension/base.glb', '/asset/suspension/base.glb', 'suspension'),
(11, 'Coil Spring', '노면의 충격을 흡수하는 코일 스프링', '/asset/suspension/spring.glb', '/asset/suspension/spring.glb', 'suspension'),
(12, 'Shock Rod', '충격 흡수기(쇼바)의 중심 축', '/asset/suspension/rod.glb', '/asset/suspension/rod.glb', 'suspension'),
(13, 'Lock Nut', '부품을 단단히 고정하는 너트', '/asset/suspension/nut.glb', '/asset/suspension/nut.glb', 'suspension'),
(14, 'Fixing Pin', '부품 간 연결을 위한 고정 핀', '/asset/suspension/nit.glb', '/asset/suspension/nit.glb', 'suspension');

-- Robot Gripper
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, category_id) VALUES
(20, 'Base Plate', '로봇 그리퍼를 바닥에 고정하는 메인 판', '/asset/robot_gripper/base_plate.glb', '/asset/robot_gripper/base_plate.glb', 'robot_gripper'),
(21, 'Base Mounting Bracket', '베이스와 그리퍼 본체를 연결하는 브라켓', '/asset/robot_gripper/base_bracket.glb', '/asset/robot_gripper/base_bracket.glb', 'robot_gripper'),
(22, 'Gear Link A', '동력을 전달하는 첫 번째 기어 링크', '/asset/robot_gripper/gear_link_1.glb', '/asset/robot_gripper/gear_link_1.glb', 'robot_gripper'),
(23, 'Gear Link B', '맞물려 돌아가는 두 번째 기어 링크', '/asset/robot_gripper/gear_link_2.glb', '/asset/robot_gripper/gear_link_2.glb', 'robot_gripper'),
(24, 'Connecting Link', '관절을 이어주는 연결 링크', '/asset/robot_gripper/link.glb', '/asset/robot_gripper/link.glb', 'robot_gripper'),
(25, 'Gripper Jaw', '물체를 실제로 집는 집게 부분', '/asset/robot_gripper/gripper.glb', '/asset/robot_gripper/gripper.glb', 'robot_gripper'),
(26, 'Fixing Pin', '각 부품을 고정하는 회전 핀', '/asset/robot_gripper/pin.glb', '/asset/robot_gripper/pin.glb', 'robot_gripper'),
(27, 'Base Gear', '베이스 부분에 위치한 기어 부품', '/asset/robot_gripper/base_gear.glb', '/asset/robot_gripper/base_gear.glb', 'robot_gripper');

-- Robot Arm
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, category_id) VALUES
(30, 'Robot Base', '로봇 팔의 회전 바닥 (Base)', '/asset/robot_arm/base.glb', '/asset/robot_arm/base.glb', 'robot_arm'),
(31, 'Arm Part 2', '로봇 팔 연결 부품 2', '/asset/robot_arm/part_2.glb', '/asset/robot_arm/part_2.glb', 'robot_arm'),
(32, 'Arm Part 3', '로봇 팔 연결 부품 3', '/asset/robot_arm/part_3.glb', '/asset/robot_arm/part_3.glb', 'robot_arm'),
(33, 'Arm Part 4', '로봇 팔 연결 부품 4', '/asset/robot_arm/part_4.glb', '/asset/robot_arm/part_4.glb', 'robot_arm'),
(34, 'Arm Part 5', '로봇 팔 연결 부품 5', '/asset/robot_arm/part_5.glb', '/asset/robot_arm/part_5.glb', 'robot_arm'),
(35, 'Arm Part 6', '로봇 팔 연결 부품 6', '/asset/robot_arm/part_6.glb', '/asset/robot_arm/part_6.glb', 'robot_arm'),
(36, 'Arm Part 7', '로봇 팔 연결 부품 7', '/asset/robot_arm/part_7.glb', '/asset/robot_arm/part_7.glb', 'robot_arm'),
(37, 'Arm Part 8', '로봇 팔 연결 부품 8', '/asset/robot_arm/part_8.glb', '/asset/robot_arm/part_8.glb', 'robot_arm');

-- Machine Vice
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, category_id) VALUES
(40, 'Guide Part', '바이스의 이동을 돕는 가이드 부품', '/asset/machine_vice/part_1_guide.glb', '/asset/machine_vice/part_1_guide.glb', 'machine_vice'),
(41, 'Vice Handle', '바이스를 조이고 푸는 회전 핸들', '/asset/machine_vice/part_1.glb', '/asset/machine_vice/part_1.glb', 'machine_vice'),
(42, 'Fixed Jaw', '움직이지 않고 고정된 턱', '/asset/machine_vice/part_2.glb', '/asset/machine_vice/part_2.glb', 'machine_vice'),
(43, 'Movable Jaw', '스핀들에 의해 앞뒤로 움직이는 턱', '/asset/machine_vice/part_3.glb', '/asset/machine_vice/part_3.glb', 'machine_vice'),
(44, 'Spindle Base', '스핀들을 지지하는 소켓 베이스', '/asset/machine_vice/part_4.glb', '/asset/machine_vice/part_4.glb', 'machine_vice'),
(45, 'Clamping Jaw', '물체를 직접 물어 고정하는 조임 턱', '/asset/machine_vice/part_5.glb', '/asset/machine_vice/part_5.glb', 'machine_vice'),
(46, 'Guide Rail', '이동 죠가 미끄러지듯 움직이는 레일', '/asset/machine_vice/part_6.glb', '/asset/machine_vice/part_6.glb', 'machine_vice'),
(47, 'Spindle Shaft', '회전력을 직선 운동으로 바꾸는 나사 축', '/asset/machine_vice/part_7.glb', '/asset/machine_vice/part_7.glb', 'machine_vice'),
(48, 'Base Plate', '바이스 전체를 지지하는 바닥판', '/asset/machine_vice/part_8.glb', '/asset/machine_vice/part_8.glb', 'machine_vice'),
(49, 'Pressure Sleeve', '축의 압력을 전달하는 슬리브', '/asset/machine_vice/part_9.glb', '/asset/machine_vice/part_9.glb', 'machine_vice');

-- Leaf Spring
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, category_id) VALUES
(50, 'Center Clamp', '판스프링 중앙을 고정하는 클램프', '/asset/leaf_spring/clamp_center.glb', '/asset/leaf_spring/clamp_center.glb', 'leaf_spring'),
(51, 'Primary Clamp', '메인 고정 클램프', '/asset/leaf_spring/clamp_primary.glb', '/asset/leaf_spring/clamp_primary.glb', 'leaf_spring'),
(52, 'Secondary Clamp', '보조 고정 클램프', '/asset/leaf_spring/clamp_secondary.glb', '/asset/leaf_spring/clamp_secondary.glb', 'leaf_spring'),
(53, 'Leaf Layer', '충격을 흡수하는 판스프링 레이어', '/asset/leaf_spring/leaf_layer.glb', '/asset/leaf_spring/leaf_layer.glb', 'leaf_spring'),
(54, 'Rigid Chassis Support', '단단하게 고정되는 섀시 지지대', '/asset/leaf_spring/support_chassis_rigid.glb', '/asset/leaf_spring/support_chassis_rigid.glb', 'leaf_spring'),
(55, 'Chassis Support', '차체와 연결되는 지지대', '/asset/leaf_spring/support_chassis.glb', '/asset/leaf_spring/support_chassis.glb', 'leaf_spring'),
(56, 'Rubber Bushing (60mm)', '진동을 흡수하는 60mm 고무 부싱', '/asset/leaf_spring/support_rubber_60mm.glb', '/asset/leaf_spring/support_rubber_60mm.glb', 'leaf_spring'),
(57, 'Rubber Bushing', '진동 흡수 고무 부품', '/asset/leaf_spring/support_rubber.glb', '/asset/leaf_spring/support_rubber.glb', 'leaf_spring'),
(58, 'Main Support', '스프링 전체를 받쳐주는 지지대', '/asset/leaf_spring/support.glb', '/asset/leaf_spring/support.glb', 'leaf_spring');

-- Drone
INSERT INTO learning_objects (id, name, description, model_url, assembly_url, category_id) VALUES
(60, 'Main Frame', '드론의 중심이 되는 메인 바디', '/asset/drone/main_frame.glb', '/asset/drone/main_frame.glb', 'drone'),
(61, 'Main Frame (Mirror)', '대칭형 메인 프레임 파트', '/asset/drone/main_frame_mir.glb', '/asset/drone/main_frame_mir.glb', 'drone'),
(62, 'Landing Leg', '착륙 시 충격을 흡수하는 다리', '/asset/drone/leg.glb', '/asset/drone/leg.glb', 'drone'),
(63, 'Impeller Blade', '양력을 발생시키는 프로펠러', '/asset/drone/impeller_blade.glb', '/asset/drone/impeller_blade.glb', 'drone'),
(64, 'Arm Gear', '암(Arm) 각도를 조절하는 기어', '/asset/drone/arm_gear.glb', '/asset/drone/arm_gear.glb', 'drone'),
(65, 'Internal Gearing', '동력을 전달하는 내부 기어 뭉치', '/asset/drone/gearing.glb', '/asset/drone/gearing.glb', 'drone'),
(66, 'Beater Disc', '회전 동작을 제어하는 디스크', '/asset/drone/beater_disc.glb', '/asset/drone/beater_disc.glb', 'drone'),
(67, 'Fixing Nut', '부품 고정용 너트', '/asset/drone/nut.glb', '/asset/drone/nut.glb', 'drone'),
(68, 'Assembly Screw', '부품 체결용 스크류', '/asset/drone/screw.glb', '/asset/drone/screw.glb', 'drone'),
(69, 'XYZ Sensor Module', '위치/자세 제어 센서 모듈', '/asset/drone/xyz.glb', '/asset/drone/xyz.glb', 'drone');