package com.cybercafe.controller;

import com.cybercafe.model.dto.RequestDTO;
import com.cybercafe.model.dto.ResponseDTO;
import com.cybercafe.service.SurfLogicService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cashier")
@RequiredArgsConstructor
public class CashierHttpController {

    private final SurfLogicService surfLogicService;
    private static final Logger log = LoggerFactory.getLogger(CashierHttpController.class);

    @PostMapping("/extrachannel")
    public ResponseDTO handlePostRequest(@RequestBody RequestDTO request) {
        if (!validateToken(request)) {
            return ResponseDTO.error("Invalid token");
        }

        try {
            if ("UpdateMemberByYun".equals(request.getFn())) {
                return surfLogicService.updateUser(request.getData());
            } else if ("AddChainMember".equals(request.getFn())) {
                return surfLogicService.addUser(request.getData());
            }
            return ResponseDTO.error("Unknown function");
        } catch (Exception e) {
            log.error("post request process error", e);
            return ResponseDTO.error("error");
        }
    }

    private boolean validateToken(RequestDTO dto) {
        // Validation logic
        return true; 
    }
}
