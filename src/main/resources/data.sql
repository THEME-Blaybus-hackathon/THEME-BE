INSERT INTO learning_object_categories (id, main_image_url) VALUES
('suspension', '/asset/suspension/main_image.png'),
('robot_gripper', '/asset/robot_gripper/main_image.png'),
('robot_arm', '/asset/robot_arm/main_image.png'),
('drone', '/asset/drone/main_image.png')
ON CONFLICT (id) DO NOTHING;


INSERT INTO learning_objects
(id, name, description, model_url, assembly_url, mesh_name, category_id) VALUES
(10, 'Suspension Base', '서스펜션의 하단 지지대 프레임', '/asset/suspension/base.glb', '/asset/suspension/base.glb', 'base', 'suspension'),
(11, 'Coil Spring', '노면의 충격을 흡수하는 코일 스프링', '/asset/suspension/spring.glb', '/asset/suspension/spring.glb', 'spring', 'suspension'),
(12, 'Shock Rod', '충격 흡수기(쇼바)의 중심 축', '/asset/suspension/rod.glb', '/asset/suspension/rod.glb', 'rod', 'suspension'),
(13, 'Lock Nut', '부품을 단단히 고정하는 너트', '/asset/suspension/nut.glb', '/asset/suspension/nut.glb', 'nut', 'suspension'),
(14, 'Fixing Pin', '부품 간 연결을 위한 고정 핀', '/asset/suspension/nit.glb', '/asset/suspension/nit.glb', 'nit', 'suspension')
ON CONFLICT (id) DO NOTHING;


INSERT INTO learning_objects
(id, name, description, model_url, assembly_url, mesh_name, category_id) VALUES
(20, 'Base Plate', '로봇 그리퍼를 바닥에 고정하는 메인 판', '/asset/robot_gripper/base_plate.glb', '/asset/robot_gripper/base_plate.glb', 'base_plate', 'robot_gripper'),
(21, 'Base Mounting Bracket', '베이스와 그리퍼 본체를 연결하는 브라켓', '/asset/robot_gripper/base_bracket.glb', '/asset/robot_gripper/base_bracket.glb', 'bracket', 'robot_gripper'),
(22, 'Gear Link A', '동력을 전달하는 첫 번째 기어 링크', '/asset/robot_gripper/gear_link_1.glb', '/asset/robot_gripper/gear_link_1.glb', 'gear_link_1', 'robot_gripper'),
(23, 'Gear Link B', '맞물려 돌아가는 두 번째 기어 링크', '/asset/robot_gripper/gear_link_2.glb', '/asset/robot_gripper/gear_link_2.glb', 'gear_link_2', 'robot_gripper'),
(24, 'Connecting Link', '관절을 이어주는 연결 링크', '/asset/robot_gripper/link.glb', '/asset/robot_gripper/link.glb', 'link', 'robot_gripper'),
(25, 'Gripper Jaw', '물체를 실제로 집는 집게 부분', '/asset/robot_gripper/gripper.glb', '/asset/robot_gripper/gripper.glb', 'Gripper', 'robot_gripper'),
(26, 'Fixing Pin', '각 부품을 고정하는 회전 핀', '/asset/robot_gripper/pin.glb', '/asset/robot_gripper/pin.glb', 'pin', 'robot_gripper'),
(27, 'Base Gear', '베이스 부분에 위치한 기어 부품', '/asset/robot_gripper/base_gear.glb', '/asset/robot_gripper/base_gear.glb', 'base_gear', 'robot_gripper')
ON CONFLICT (id) DO NOTHING;


INSERT INTO learning_objects
(id, name, description, model_url, assembly_url, mesh_name, category_id) VALUES
(30, 'Robot Base', '로봇 팔의 회전 바닥 (Base)', '/asset/robot_arm/base.glb', '/asset/robot_arm/base.glb', 'base', 'robot_arm'),
(31, 'Arm Part 2', '로봇 팔 연결 부품 2', '/asset/robot_arm/part_2.glb', '/asset/robot_arm/part_2.glb', 'part_2', 'robot_arm'),
(32, 'Arm Part 3', '로봇 팔 연결 부품 3', '/asset/robot_arm/part_3.glb', '/asset/robot_arm/part_3.glb', 'part_3', 'robot_arm'),
(33, 'Arm Part 4', '로봇 팔 연결 부품 4', '/asset/robot_arm/part_4.glb', '/asset/robot_arm/part_4.glb', 'part_4', 'robot_arm'),
(34, 'Arm Part 5', '로봇 팔 연결 부품 5', '/asset/robot_arm/part_5.glb', '/asset/robot_arm/part_5.glb', 'part_5', 'robot_arm'),
(35, 'Arm Part 6', '로봇 팔 연결 부품 6', '/asset/robot_arm/part_6.glb', '/asset/robot_arm/part_6.glb', 'part_6', 'robot_arm'),
(36, 'Arm Part 7', '로봇 팔 연결 부품 7', '/asset/robot_arm/part_7.glb', '/asset/robot_arm/part_7.glb', 'part_7', 'robot_arm'),
(37, 'Arm Part 8', '로봇 팔 연결 부품 8', '/asset/robot_arm/part_8.glb', '/asset/robot_arm/part_8.glb', 'part_8', 'robot_arm')
ON CONFLICT (id) DO NOTHING;


INSERT INTO learning_objects (id, name, description, model_url, assembly_url, mesh_name, category_id) VALUES
(61, 'MainFrame', '대칭형 메인 프레임 파트', '/asset/drone/main_frame_mir.glb', '/asset/drone/main_frame_mir.glb', 'main_frame_mir', 'drone'),
(62, 'Leg', '착륙 시 충격을 흡수하는 다리', '/asset/drone/leg.glb', '/asset/drone/leg.glb', 'leg', 'drone'),
(63, 'Impeller', '양력을 발생시키는 프로펠러', '/asset/drone/impeller_blade.glb', '/asset/drone/impeller_blade.glb', 'impeller_blade', 'drone'),
(64, 'Arm', '암(Arm) 각도를 조절하는 기어', '/asset/drone/arm_gear.glb', '/asset/drone/arm_gear.glb', 'arm_gear', 'drone'),
(65, 'Gearing', '동력을 전달하는 내부 기어 뭉치', '/asset/drone/gearing.glb', '/asset/drone/gearing.glb', 'gearing', 'drone'),
(66, 'Beater Disc', '회전 동작을 제어하는 디스크', '/asset/drone/beater_disc.glb', '/asset/drone/beater_disc.glb', 'beater_disc', 'drone'),
(67, 'Nut', '부품 고정용 너트', '/asset/drone/nut.glb', '/asset/drone/nut.glb', 'nut', 'drone'),
(69, 'xyz', '위치/자세 제어 센서 모듈', '/asset/drone/xyz.glb', '/asset/drone/xyz.glb', 'xyz', 'drone')
ON CONFLICT (id) DO NOTHING;
