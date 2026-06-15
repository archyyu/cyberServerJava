package com.cybercafe.controller;

import com.cybercafe.model.Online;
import com.cybercafe.model.dto.request.*;
import com.cybercafe.service.SurfLogicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
public class ClientController {

    private final SurfLogicService surfLogicService;

    @PostMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat(@RequestBody PcHeartbeatRequest request) {
        surfLogicService.pcHeart(
            request.memberID(), request.pcName(), request.pcIp(), request.pcMac());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Online> pcLogin(@RequestBody PcLoginRequest request) throws Exception {
        Online online = surfLogicService.memberLoginMachine(
            request.gid(), request.memberId(), request.pcName(), request.password());
        return ResponseEntity.ok(online);
    }

    @GetMapping("/users/{memberId}")
    public ResponseEntity<Object> getUser(@PathVariable Long memberId) {
        return ResponseEntity.ok(surfLogicService.queryUser(memberId));
    }

    @PutMapping("/users/{memberId}/machine")
    public ResponseEntity<Void> changePc(
            @PathVariable Long memberId,
            @RequestBody ChangePcRequest request) {
        surfLogicService.changePc(memberId, request.pcName(), request.areaId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/members/activate")
    public ResponseEntity<Online> activate(@RequestBody ActiveUserRequest request) throws Exception {
        Online online = surfLogicService.activateUser(
            request.gid(), request.memberId(), request.areaId(),
            request.machineName(), request.durationId(), request.periodId());
        return ResponseEntity.ok(online);
    }

    @PostMapping("/users/sync")
    public ResponseEntity<Void> syncUser(@RequestParam Long memberId) {
        surfLogicService.synUserInfo(memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logoff")
    public ResponseEntity<Void> logoff(@RequestBody LogoffRequest request) {
        surfLogicService.logOffUser(
            request.gid(), request.memberId(),
            request.isFromCashier(), request.force(), request.isNoteClient());
        return ResponseEntity.ok().build();
    }
}
