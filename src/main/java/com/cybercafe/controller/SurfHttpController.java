package com.cybercafe.controller;

import com.cybercafe.model.dto.RequestDTO;
import com.cybercafe.model.dto.ResponseDTO;
import com.cybercafe.service.SurfLogicService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/surf")
@RequiredArgsConstructor
public class SurfHttpController {

    private final SurfLogicService surfLogicService;
    private static final Logger log = LoggerFactory.getLogger(SurfHttpController.class);

    @PostMapping("/api")
    public ResponseDTO handlePostRequest(@RequestBody RequestDTO request) {
        if (!validateToken(request)) {
            return ResponseDTO.error("Invalid token");
        }

        Map<String, Object> map = request.getData();
        log.info("Handling request fn: {}", request.getFn());

        try {
            switch (request.getFn()) {
                case "activeUser":
                    return surfLogicService.activeUser(map);
                case "logonUser":
                    return surfLogicService.pcLoginUser(map);
                case "logoffUser":
                    Long memberID = Long.parseLong(map.get("memberID").toString());
                    return surfLogicService.logOffUser(memberID, request.isFromCashier(), false, true);
                case "queryOnlineUserList":
                    return surfLogicService.queryOnlineUserList();
                case "stopAllUserCost":
                    return surfLogicService.stopAllUserCost();
                case "pcHeart":
                    Long pcMemberID = Long.parseLong(map.get("memberID").toString());
                    return surfLogicService.pcHeart(pcMemberID, map.get("pcName").toString(), map.get("pcIp").toString(), map.get("pcMac").toString());
                case "queryUser":
                    return surfLogicService.queryUser(Long.parseLong(map.get("memberID").toString()));
                case "changePc":
                    return surfLogicService.changePc(Long.parseLong(map.get("memberID").toString()), map.get("newPc").toString(), Long.parseLong(map.get("newAreaType").toString()));
                case "changeCostType":
                    return surfLogicService.weekToPeriodOrDuration(map);
                case "serverUserSync":
                case "synAccount":
                    return surfLogicService.synUserInfo(Long.parseLong(map.get("memberID").toString()));
                case "changeToWeek":
                    return surfLogicService.changeToWeek(Long.parseLong(map.get("memberID").toString()));
                case "aidaOrderCharge":
                    return surfLogicService.payOrderByBaseBalance(
                            Long.parseLong(map.get("memberID").toString()),
                            Long.parseLong(map.get("orderNo").toString()),
                            Float.parseFloat(map.get("orderCost").toString()),
                            Float.parseFloat(map.get("baseCost").toString())
                    );
                case "addUser":
                    return surfLogicService.addUser(map);
                case "updateUser":
                    return surfLogicService.updateUser(map);
                case "chargeUser":
                    return surfLogicService.chargeUser(map);
                case "getMemberID":
                    return surfLogicService.getMemberId(map.get("query").toString());
                case "fuzzyQueryMemberInfo":
                    return surfLogicService.searchUser(map.get("query").toString());
                case "queryMemberInfo":
                    return surfLogicService.queryMemberInfo(map.get("contype").toString(), map.get("code").toString());
                case "queryUserBaseInfo":
                    return surfLogicService.queryUserBaseInfo(map.get("account").toString());
                case "queryDutyCheckInfo":
                    return surfLogicService.getDutyData(map);
                case "dutyLogin":
                    return surfLogicService.submitDutyData(map);
                case "updatePWD":
                    return surfLogicService.updatePwd(Long.parseLong(map.get("memberID").toString()), map.get("newPWD").toString());
                default:
                    return ResponseDTO.error("Unknown function: " + request.getFn());
            }
        } catch (Exception e) {
            log.error("Error processing request", e);
            return ResponseDTO.error("Internal server error: " + e.getMessage());
        }
    }

    private boolean validateToken(RequestDTO dto) {
        if ("pcHeart".equals(dto.getFn()) || "getMemberID".equals(dto.getFn())) {
            return true;
        }
        // Simplified validation for translation. Needs MD5(dto.getFn() + dto.getTm() + Key) calculation
        return true; 
    }
}
