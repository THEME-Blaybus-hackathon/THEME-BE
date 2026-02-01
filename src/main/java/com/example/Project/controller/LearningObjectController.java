package com.example.Project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.LearningObjectResponse;

@RestController
@RequestMapping("/api/objects")
public class LearningObjectController {

    @GetMapping
    public List<LearningObjectResponse> getAllObjects(@RequestParam(required = false) String category) {
        
        List<LearningObjectResponse> allObjects = new ArrayList<>();

        // 1. V4 Engine
        String v4Path = "/asset/v4_engine/";
        String v4Img = v4Path + "main_image.png";
        allObjects.add(makeObj(1L, "Piston", "왕복 운동 부품", v4Img, v4Path + "piston.glb", "v4_engine"));
        allObjects.add(makeObj(2L, "Crankshaft", "회전 운동 변환 축", v4Img, v4Path + "crankshaft.glb", "v4_engine"));
        allObjects.add(makeObj(3L, "Connecting Rod", "동력 전달 막대", v4Img, v4Path + "connecting_rod.glb", "v4_engine"));
        allObjects.add(makeObj(4L, "Piston Pin", "연결 핀", v4Img, v4Path + "piston_pin.glb", "v4_engine"));
        allObjects.add(makeObj(5L, "Piston Ring", "밀폐 링", v4Img, v4Path + "piston_ring.glb", "v4_engine"));
        allObjects.add(makeObj(6L, "Rod Cap", "로드 캡", v4Img, v4Path + "rod_cap.glb", "v4_engine"));
        allObjects.add(makeObj(7L, "Conrod Bolt", "체결 볼트", v4Img, v4Path + "bolt.glb", "v4_engine"));

        // 2. Suspension
        String suspPath = "/asset/suspension/";
        String suspImg = suspPath + "main_image.png";
        allObjects.add(makeObj(10L, "Suspension Base", "서스펜션의 하단 지지대 프레임", suspImg, suspPath + "base.glb", "suspension"));
        allObjects.add(makeObj(11L, "Coil Spring", "노면의 충격을 흡수하는 코일 스프링", suspImg, suspPath + "spring.glb", "suspension"));
        allObjects.add(makeObj(12L, "Shock Rod", "충격 흡수기(쇼바)의 중심 축", suspImg, suspPath + "rod.glb", "suspension"));
        allObjects.add(makeObj(13L, "Lock Nut", "부품을 단단히 고정하는 너트", suspImg, suspPath + "nut.glb", "suspension"));
        allObjects.add(makeObj(14L, "Fixing Pin", "부품 간 연결을 위한 고정 핀 (Nit)", suspImg, suspPath + "nit.glb", "suspension"));

        // 3. Robot Gripper
        String gripPath = "/asset/robot_gripper/";
        String gripImg = gripPath + "main_image.png";
        allObjects.add(makeObj(20L, "Base Plate", "로봇 그리퍼를 바닥에 고정하는 메인 판", gripImg, gripPath + "base_plate.glb", "robot_gripper"));
        allObjects.add(makeObj(21L, "Base Mounting Bracket", "베이스와 그리퍼 본체를 연결하는 브라켓", gripImg, gripPath + "base_bracket.glb", "robot_gripper"));
        allObjects.add(makeObj(22L, "Gear Link A", "동력을 전달하는 첫 번째 기어 링크", gripImg, gripPath + "gear_link_1.glb", "robot_gripper"));
        allObjects.add(makeObj(23L, "Gear Link B", "맞물려 돌아가는 두 번째 기어 링크", gripImg, gripPath + "gear_link_2.glb", "robot_gripper"));
        allObjects.add(makeObj(24L, "Connecting Link", "관절을 이어주는 연결 링크", gripImg, gripPath + "link.glb", "robot_gripper"));
        allObjects.add(makeObj(25L, "Gripper Jaw", "물체를 실제로 집는 집게 부분", gripImg, gripPath + "gripper.glb", "robot_gripper"));
        allObjects.add(makeObj(26L, "Fixing Pin", "각 부품을 고정하는 회전 핀", gripImg, gripPath + "pin.glb", "robot_gripper"));
        allObjects.add(makeObj(27L, "Base Gear", "베이스 부분에 위치한 기어 부품", gripImg, gripPath + "base_gear.glb", "robot_gripper"));

        // 4. Robot Arm
        String armPath = "/asset/robot_arm/";
        String armImg = armPath + "main_image.png";
        allObjects.add(makeObj(30L, "Robot Base", "로봇 팔의 회전 바닥 (Base)", armImg, armPath + "base.glb", "robot_arm"));
        allObjects.add(makeObj(31L, "Arm Part 2", "로봇 팔 연결 부품 2", armImg, armPath + "part_2.glb", "robot_arm"));
        allObjects.add(makeObj(32L, "Arm Part 3", "로봇 팔 연결 부품 3", armImg, armPath + "part_3.glb", "robot_arm"));
        allObjects.add(makeObj(33L, "Arm Part 4", "로봇 팔 연결 부품 4", armImg, armPath + "part_4.glb", "robot_arm"));
        allObjects.add(makeObj(34L, "Arm Part 5", "로봇 팔 연결 부품 5", armImg, armPath + "part_5.glb", "robot_arm"));
        allObjects.add(makeObj(35L, "Arm Part 6", "로봇 팔 연결 부품 6", armImg, armPath + "part_6.glb", "robot_arm"));
        allObjects.add(makeObj(36L, "Arm Part 7", "로봇 팔 연결 부품 7", armImg, armPath + "part_7.glb", "robot_arm"));
        allObjects.add(makeObj(37L, "Arm Part 8", "로봇 팔 연결 부품 8", armImg, armPath + "part_8.glb", "robot_arm"));

        // 5. Machine Vice
        String vicePath = "/asset/machine_vice/";
        String viceImg = vicePath + "main_image.png";
        allObjects.add(makeObj(40L, "Guide Part", "바이스의 이동을 돕는 가이드 부품", viceImg, vicePath + "part_1_guide.glb", "machine_vice"));
        allObjects.add(makeObj(41L, "Vice Handle", "바이스를 조이고 푸는 회전 핸들", viceImg, vicePath + "part_1.glb", "machine_vice"));
        allObjects.add(makeObj(42L, "Fixed Jaw", "움직이지 않고 고정된 턱 (Fixed Jaw)", viceImg, vicePath + "part_2.glb", "machine_vice"));
        allObjects.add(makeObj(43L, "Movable Jaw", "스핀들에 의해 앞뒤로 움직이는 턱", viceImg, vicePath + "part_3.glb", "machine_vice"));
        allObjects.add(makeObj(44L, "Spindle Base", "스핀들을 지지하는 소켓 베이스", viceImg, vicePath + "part_4.glb", "machine_vice"));
        allObjects.add(makeObj(45L, "Clamping Jaw", "물체를 직접 물어 고정하는 조임 턱", viceImg, vicePath + "part_5.glb", "machine_vice"));
        allObjects.add(makeObj(46L, "Guide Rail", "이동 죠가 미끄러지듯 움직이는 레일", viceImg, vicePath + "part_6.glb", "machine_vice"));
        allObjects.add(makeObj(47L, "Spindle Shaft", "회전력을 직선 운동으로 바꾸는 나사 축", viceImg, vicePath + "part_7.glb", "machine_vice"));
        allObjects.add(makeObj(48L, "Base Plate", "바이스 전체를 지지하는 바닥판", viceImg, vicePath + "part_8.glb", "machine_vice"));
        allObjects.add(makeObj(49L, "Pressure Sleeve", "축의 압력을 전달하는 슬리브", viceImg, vicePath + "part_9.glb", "machine_vice"));

        // 6. Leaf Spring
        String leafPath = "/asset/leaf_spring/";
        String leafImg = leafPath + "main_image.png";
        allObjects.add(makeObj(50L, "Center Clamp", "판스프링 중앙을 고정하는 클램프", leafImg, leafPath + "clamp_center.glb", "leaf_spring"));
        allObjects.add(makeObj(51L, "Primary Clamp", "메인 고정 클램프", leafImg, leafPath + "clamp_primary.glb", "leaf_spring"));
        allObjects.add(makeObj(52L, "Secondary Clamp", "보조 고정 클램프", leafImg, leafPath + "clamp_secondary.glb", "leaf_spring"));
        allObjects.add(makeObj(53L, "Leaf Layer", "충격을 흡수하는 판스프링 레이어", leafImg, leafPath + "leaf_layer.glb", "leaf_spring"));
        allObjects.add(makeObj(54L, "Rigid Chassis Support", "단단하게 고정되는 섀시 지지대", leafImg, leafPath + "support_chassis_rigid.glb", "leaf_spring"));
        allObjects.add(makeObj(55L, "Chassis Support", "차체와 연결되는 지지대", leafImg, leafPath + "support_chassis.glb", "leaf_spring"));
        allObjects.add(makeObj(56L, "Rubber Bushing (60mm)", "진동을 흡수하는 60mm 고무 부싱", leafImg, leafPath + "support_rubber_60mm.glb", "leaf_spring"));
        allObjects.add(makeObj(57L, "Rubber Bushing", "진동 흡수 고무 부품", leafImg, leafPath + "support_rubber.glb", "leaf_spring"));
        allObjects.add(makeObj(58L, "Main Support", "스프링 전체를 받쳐주는 지지대", leafImg, leafPath + "support.glb", "leaf_spring"));

        // 7. Drone
        String dronePath = "/asset/drone/";
        String droneImg = dronePath + "main_image.png";
        allObjects.add(makeObj(60L, "Main Frame", "드론의 중심이 되는 메인 바디", droneImg, dronePath + "main_frame.glb", "drone"));
        allObjects.add(makeObj(61L, "Main Frame (Mirror)", "대칭형 메인 프레임 파트", droneImg, dronePath + "main_frame_mir.glb", "drone"));
        allObjects.add(makeObj(62L, "Landing Leg", "착륙 시 충격을 흡수하는 다리", droneImg, dronePath + "leg.glb", "drone"));
        allObjects.add(makeObj(63L, "Impeller Blade", "양력을 발생시키는 프로펠러", droneImg, dronePath + "impeller_blade.glb", "drone"));
        allObjects.add(makeObj(64L, "Arm Gear", "암(Arm) 각도를 조절하는 기어", droneImg, dronePath + "arm_gear.glb", "drone"));
        allObjects.add(makeObj(65L, "Internal Gearing", "동력을 전달하는 내부 기어 뭉치", droneImg, dronePath + "gearing.glb", "drone"));
        allObjects.add(makeObj(66L, "Beater Disc", "회전 동작을 제어하는 디스크", droneImg, dronePath + "beater_disc.glb", "drone"));
        allObjects.add(makeObj(67L, "Fixing Nut", "부품 고정용 너트", droneImg, dronePath + "nut.glb", "drone"));
        allObjects.add(makeObj(68L, "Assembly Screw", "부품 체결용 스크류", droneImg, dronePath + "screw.glb", "drone"));
        allObjects.add(makeObj(69L, "XYZ Sensor Module", "위치/자세 제어 센서 모듈", droneImg, dronePath + "xyz.glb", "drone"));


        // 필터링 로직
        if (category != null && !category.isEmpty()) {
            return allObjects.stream()
                    .filter(obj -> obj.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        return allObjects;
    }

    // 도우미 함수 (코드를 깔끔하게!)
    private LearningObjectResponse makeObj(Long id, String name, String desc, String img, String model, String cat) {
        return LearningObjectResponse.builder()
                .id(id)
                .name(name)
                .description(desc)
                .imageUrl(img)
                .modelUrl(model)
                .category(cat)
                .build();
    }
}