package com.example.Project.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Project.dto.CategoryResponse;
import com.example.Project.dto.LearningObjectResponse;
import com.example.Project.dto.PartSummaryResponse;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ObjectDataService {

    private final List<LearningObjectResponse> allObjects = new ArrayList<>();
    // 카테고리별 대표 이미지를 저장하는 맵
    private final Map<String, String> categoryImages = new HashMap<>();

    @PostConstruct
    public void initData() {
        log.info("3D 데이터 초기화 시작...");

        // ==========================================
        // 1. 대표 이미지 설정 (여기에만 이미지가 있음)
        // ==========================================
        categoryImages.put("v4_engine", "/asset/v4_engine/main_image.png");
        categoryImages.put("suspension", "/asset/suspension/main_image.png");
        categoryImages.put("robot_gripper", "/asset/robot_gripper/main_image.png");
        categoryImages.put("robot_arm", "/asset/robot_arm/main_image.png");
        categoryImages.put("machine_vice", "/asset/machine_vice/main_image.png");
        categoryImages.put("leaf_spring", "/asset/leaf_spring/main_image.png");
        categoryImages.put("drone", "/asset/drone/main_image.png");

        // ==========================================
        // 2. 부품 데이터 설정 (이미지 null 처리)
        // ==========================================
        
        // 1. V4 Engine
        String v4Path = "/asset/v4_engine/";
        addObj(1L, "Piston", "왕복 운동 부품", null, v4Path + "piston.glb", "v4_engine");
        addObj(2L, "Crankshaft", "회전 운동 변환 축", null, v4Path + "crankshaft.glb", "v4_engine");
        addObj(3L, "Connecting Rod", "동력 전달 막대", null, v4Path + "connecting_rod.glb", "v4_engine");
        addObj(4L, "Piston Pin", "연결 핀", null, v4Path + "piston_pin.glb", "v4_engine");
        addObj(5L, "Piston Ring", "밀폐 링", null, v4Path + "piston_ring.glb", "v4_engine");
        addObj(6L, "Rod Cap", "로드 캡", null, v4Path + "rod_cap.glb", "v4_engine");
        addObj(7L, "Conrod Bolt", "체결 볼트", null, v4Path + "bolt.glb", "v4_engine");

        // 2. Suspension
        String suspPath = "/asset/suspension/";
        addObj(10L, "Suspension Base", "서스펜션의 하단 지지대 프레임", null, suspPath + "base.glb", "suspension");
        addObj(11L, "Coil Spring", "노면의 충격을 흡수하는 코일 스프링", null, suspPath + "spring.glb", "suspension");
        addObj(12L, "Shock Rod", "충격 흡수기(쇼바)의 중심 축", null, suspPath + "rod.glb", "suspension");
        addObj(13L, "Lock Nut", "부품을 단단히 고정하는 너트", null, suspPath + "nut.glb", "suspension");
        addObj(14L, "Fixing Pin", "부품 간 연결을 위한 고정 핀", null, suspPath + "nit.glb", "suspension");

        // 3. Robot Gripper
        String gripPath = "/asset/robot_gripper/";
        addObj(20L, "Base Plate", "로봇 그리퍼를 바닥에 고정하는 메인 판", null, gripPath + "base_plate.glb", "robot_gripper");
        addObj(21L, "Base Mounting Bracket", "베이스와 그리퍼 본체를 연결하는 브라켓", null, gripPath + "base_bracket.glb", "robot_gripper");
        addObj(22L, "Gear Link A", "동력을 전달하는 첫 번째 기어 링크", null, gripPath + "gear_link_1.glb", "robot_gripper");
        addObj(23L, "Gear Link B", "맞물려 돌아가는 두 번째 기어 링크", null, gripPath + "gear_link_2.glb", "robot_gripper");
        addObj(24L, "Connecting Link", "관절을 이어주는 연결 링크", null, gripPath + "link.glb", "robot_gripper");
        addObj(25L, "Gripper Jaw", "물체를 실제로 집는 집게 부분", null, gripPath + "gripper.glb", "robot_gripper");
        addObj(26L, "Fixing Pin", "각 부품을 고정하는 회전 핀", null, gripPath + "pin.glb", "robot_gripper");
        addObj(27L, "Base Gear", "베이스 부분에 위치한 기어 부품", null, gripPath + "base_gear.glb", "robot_gripper");

        // 4. Robot Arm
        String armPath = "/asset/robot_arm/";
        addObj(30L, "Robot Base", "로봇 팔의 회전 바닥 (Base)", null, armPath + "base.glb", "robot_arm");
        addObj(31L, "Arm Part 2", "로봇 팔 연결 부품 2", null, armPath + "part_2.glb", "robot_arm");
        addObj(32L, "Arm Part 3", "로봇 팔 연결 부품 3", null, armPath + "part_3.glb", "robot_arm");
        addObj(33L, "Arm Part 4", "로봇 팔 연결 부품 4", null, armPath + "part_4.glb", "robot_arm");
        addObj(34L, "Arm Part 5", "로봇 팔 연결 부품 5", null, armPath + "part_5.glb", "robot_arm");
        addObj(35L, "Arm Part 6", "로봇 팔 연결 부품 6", null, armPath + "part_6.glb", "robot_arm");
        addObj(36L, "Arm Part 7", "로봇 팔 연결 부품 7", null, armPath + "part_7.glb", "robot_arm");
        addObj(37L, "Arm Part 8", "로봇 팔 연결 부품 8", null, armPath + "part_8.glb", "robot_arm");

        // 5. Machine Vice
        String vicePath = "/asset/machine_vice/";
        addObj(40L, "Guide Part", "바이스의 이동을 돕는 가이드 부품", null, vicePath + "part_1_guide.glb", "machine_vice");
        addObj(41L, "Vice Handle", "바이스를 조이고 푸는 회전 핸들", null, vicePath + "part_1.glb", "machine_vice");
        addObj(42L, "Fixed Jaw", "움직이지 않고 고정된 턱", null, vicePath + "part_2.glb", "machine_vice");
        addObj(43L, "Movable Jaw", "스핀들에 의해 앞뒤로 움직이는 턱", null, vicePath + "part_3.glb", "machine_vice");
        addObj(44L, "Spindle Base", "스핀들을 지지하는 소켓 베이스", null, vicePath + "part_4.glb", "machine_vice");
        addObj(45L, "Clamping Jaw", "물체를 직접 물어 고정하는 조임 턱", null, vicePath + "part_5.glb", "machine_vice");
        addObj(46L, "Guide Rail", "이동 죠가 미끄러지듯 움직이는 레일", null, vicePath + "part_6.glb", "machine_vice");
        addObj(47L, "Spindle Shaft", "회전력을 직선 운동으로 바꾸는 나사 축", null, vicePath + "part_7.glb", "machine_vice");
        addObj(48L, "Base Plate", "바이스 전체를 지지하는 바닥판", null, vicePath + "part_8.glb", "machine_vice");
        addObj(49L, "Pressure Sleeve", "축의 압력을 전달하는 슬리브", null, vicePath + "part_9.glb", "machine_vice");

        // 6. Leaf Spring
        String leafPath = "/asset/leaf_spring/";
        addObj(50L, "Center Clamp", "판스프링 중앙을 고정하는 클램프", null, leafPath + "clamp_center.glb", "leaf_spring");
        addObj(51L, "Primary Clamp", "메인 고정 클램프", null, leafPath + "clamp_primary.glb", "leaf_spring");
        addObj(52L, "Secondary Clamp", "보조 고정 클램프", null, leafPath + "clamp_secondary.glb", "leaf_spring");
        addObj(53L, "Leaf Layer", "충격을 흡수하는 판스프링 레이어", null, leafPath + "leaf_layer.glb", "leaf_spring");
        addObj(54L, "Rigid Chassis Support", "단단하게 고정되는 섀시 지지대", null, leafPath + "support_chassis_rigid.glb", "leaf_spring");
        addObj(55L, "Chassis Support", "차체와 연결되는 지지대", null, leafPath + "support_chassis.glb", "leaf_spring");
        addObj(56L, "Rubber Bushing (60mm)", "진동을 흡수하는 60mm 고무 부싱", null, leafPath + "support_rubber_60mm.glb", "leaf_spring");
        addObj(57L, "Rubber Bushing", "진동 흡수 고무 부품", null, leafPath + "support_rubber.glb", "leaf_spring");
        addObj(58L, "Main Support", "스프링 전체를 받쳐주는 지지대", null, leafPath + "support.glb", "leaf_spring");

        // 7. Drone
        String dronePath = "/asset/drone/";
        addObj(60L, "Main Frame", "드론의 중심이 되는 메인 바디", null, dronePath + "main_frame.glb", "drone");
        addObj(61L, "Main Frame (Mirror)", "대칭형 메인 프레임 파트", null, dronePath + "main_frame_mir.glb", "drone");
        addObj(62L, "Landing Leg", "착륙 시 충격을 흡수하는 다리", null, dronePath + "leg.glb", "drone");
        addObj(63L, "Impeller Blade", "양력을 발생시키는 프로펠러", null, dronePath + "impeller_blade.glb", "drone");
        addObj(64L, "Arm Gear", "암(Arm) 각도를 조절하는 기어", null, dronePath + "arm_gear.glb", "drone");
        addObj(65L, "Internal Gearing", "동력을 전달하는 내부 기어 뭉치", null, dronePath + "gearing.glb", "drone");
        addObj(66L, "Beater Disc", "회전 동작을 제어하는 디스크", null, dronePath + "beater_disc.glb", "drone");
        addObj(67L, "Fixing Nut", "부품 고정용 너트", null, dronePath + "nut.glb", "drone");
        addObj(68L, "Assembly Screw", "부품 체결용 스크류", null, dronePath + "screw.glb", "drone");
        addObj(69L, "XYZ Sensor Module", "위치/자세 제어 센서 모듈", null, dronePath + "xyz.glb", "drone");

        log.info("데이터 초기화 완료. 총 {}개 부품 로드됨.", allObjects.size());
    }

    private void addObj(Long id, String name, String desc, String img, String model, String cat) {
        allObjects.add(LearningObjectResponse.builder()
                .id(id)
                .name(name)
                .description(desc)
                .imageUrl(img) // 상세 조회 시 null
                .modelUrl(model)
                .category(cat)
                .build());
    }

    public List<String> getAllCategories() {
        return allObjects.stream()
                .map(LearningObjectResponse::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    // [수정됨] 카테고리 검색 시: 대표 이미지 + 부품 리스트 반환
    public CategoryResponse getPartsByCategory(String category) {
        if (category == null) return null;
        
        // 1. 해당 카테고리의 대표 이미지 가져오기
        String mainImage = categoryImages.getOrDefault(category, null);

        // 2. 부품 리스트 가져오기
        List<PartSummaryResponse> parts = allObjects.stream()
                .filter(obj -> obj.getCategory().equalsIgnoreCase(category))
                .map(obj -> new PartSummaryResponse(obj.getId(), obj.getName()))
                .collect(Collectors.toList());

        // 3. 합쳐서 반환
        return CategoryResponse.builder()
                .categoryName(category)
                .mainImageUrl(mainImage) // 여기에만 이미지가 들어감!
                .parts(parts)
                .build();
    }

    public LearningObjectResponse getPartDetail(Long id) {
        if (id == null) return null;

        return allObjects.stream()
                .filter(obj -> obj.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}