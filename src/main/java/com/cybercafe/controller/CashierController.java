package com.cybercafe.controller;

import com.cybercafe.model.Online;
import com.cybercafe.model.dto.request.*;
import com.cybercafe.service.MachineService;
import com.cybercafe.service.SurfLogicService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cashier")
@RequiredArgsConstructor
public class CashierController extends BaseController {

    private final SurfLogicService surfLogicService;
    private final MachineService machineService;
    private static final Logger logger = LoggerFactory.getLogger(CashierController.class);

    @PostMapping("/members/activate")
    public ResponseEntity<Online> activateMember(@RequestBody ActiveUserRequest request) throws Exception {
        Online online = surfLogicService.activeUser(
            request.gid(), request.memberId(), request.areaId(),
            request.machineName(), request.durationId(), request.periodId());
        return ResponseEntity.ok(online);
    }

    @PostMapping("/members/login")
    public ResponseEntity<Online> loginMember(@RequestBody PcLoginRequest request) throws Exception {
        Online online = surfLogicService.pcLoginUser(
            request.gid(), request.memberId(), request.pcName(), request.password());
        return ResponseEntity.ok(online);
    }

    @PostMapping("/members/{memberId}/logoff")
    public ResponseEntity<Void> logoffMember(
            @PathVariable Long memberId,
            @RequestBody LogoffRequest request) {
        surfLogicService.logOffUser(
            request.gid(), memberId, request.isFromCashier(), request.force(), request.isNoteClient());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/online-users")
    public ResponseEntity<Object> listOnlineUsers() {
        return ResponseEntity.ok(surfLogicService.queryOnlineUserList());
    }

    @PostMapping("/online-users/stop-cost")
    public ResponseEntity<Void> stopAllCost() {
        surfLogicService.stopAllUserCost();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<Object> getMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(surfLogicService.queryUser(memberId));
    }

    @PutMapping("/members/{memberId}/machine")
    public ResponseEntity<Void> changeMemberPc(
            @PathVariable Long memberId,
            @RequestBody ChangePcRequest request) {
        surfLogicService.changePc(memberId, request.pcName(), request.areaId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/members/{memberId}/pricing/week")
    public ResponseEntity<Void> changeToWeek(@PathVariable Long memberId) {
        surfLogicService.changeToWeek(memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/members/{memberId}/pricing/convert")
    public ResponseEntity<Void> convertPricing(
            @PathVariable Long memberId,
            @RequestBody Map<String, Object> data) {
        data.put("memberId", memberId);
        surfLogicService.weekToPeriodOrDuration(data);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/members/{memberId}/pay-order")
    public ResponseEntity<Void> payOrder(
            @PathVariable Long memberId,
            @RequestBody PayOrderRequest request) {
        surfLogicService.payOrderByBaseBalance(
            memberId, request.orderId(), request.orderCost(), request.baseCost());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/members")
    public ResponseEntity<Object> addMember(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(surfLogicService.addUser(data));
    }

    @PutMapping("/members/{memberId}")
    public ResponseEntity<Void> updateMember(
            @PathVariable Long memberId,
            @RequestBody Map<String, Object> data) {
        data.put("memberId", memberId);
        surfLogicService.updateUser(data);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/members/{memberId}/charge")
    public ResponseEntity<Void> chargeMember(
            @PathVariable Long memberId,
            @RequestBody Map<String, Object> data) {
        data.put("memberId", memberId);
        surfLogicService.chargeUser(data);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/members/{memberId}/sync")
    public ResponseEntity<Void> syncMember(@PathVariable Long memberId) {
        surfLogicService.synUserInfo(memberId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/members/{memberId}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long memberId,
            @RequestBody UpdatePwdRequest request) {
        surfLogicService.updatePwd(memberId, request.newPwd());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/members")
    public ResponseEntity<Object> searchMembers(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(surfLogicService.searchUser(query));
    }

    @GetMapping("/members/by-code")
    public ResponseEntity<Object> queryMemberByCode(
            @RequestParam String contype,
            @RequestParam String code) {
        return ResponseEntity.ok(surfLogicService.queryMemberInfo(contype, code));
    }

    @GetMapping("/members/by-account")
    public ResponseEntity<Object> queryMemberByAccount(@RequestParam String account) {
        return ResponseEntity.ok(surfLogicService.queryUserBaseInfo(account));
    }

    @GetMapping("/members/member-id")
    public ResponseEntity<Object> getMemberId(@RequestParam String query) {
        return ResponseEntity.ok(surfLogicService.getMemberId(query));
    }

    @GetMapping("/duty")
    public ResponseEntity<Object> getDutyData(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(surfLogicService.getDutyData(params));
    }

    @PostMapping("/duty")
    public ResponseEntity<Void> submitDutyData(@RequestBody Map<String, Object> data) {
        surfLogicService.submitDutyData(data);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/machines")
    public ResponseEntity<List<com.cybercafe.model.Machine>> listMachines(@RequestParam Long gid) {
        return ResponseEntity.ok(machineService.loadMachines(gid));
    }

    @GetMapping("/areas")
    public ResponseEntity<List<com.cybercafe.model.Area>> listAreas(@RequestParam Long gid) {
        return ResponseEntity.ok(machineService.loadAreas(gid));
    }
}
