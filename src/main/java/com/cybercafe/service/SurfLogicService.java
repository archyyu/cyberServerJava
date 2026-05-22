package com.cybercafe.service;

import com.cybercafe.model.DurationPrice;
import com.cybercafe.model.ExtraPrice;
import com.cybercafe.model.Machine;
import com.cybercafe.model.Member;
import com.cybercafe.model.Netbar;
import com.cybercafe.model.Online;
import com.cybercafe.model.PeriodPrice;
import com.cybercafe.model.WeekPrice;
import com.cybercafe.model.dto.ResponseDTO;
import com.cybercafe.model.logic.ActiveData;
import com.cybercafe.model.logic.SurfUser;
import com.cybercafe.repository.MemberRepository;
import com.cybercafe.repository.OnlineRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurfLogicService {
    
    private final MemberRepository memberRepository;
    private final OnlineRepository onlineRepository;
    private final RateService rateService;


    public ResponseDTO activeUser(ActiveData activeData) {
        SurfUser surfUser = surfUserMap.get(activeData.getMemberId());
        
        if (surfUser == null) {
            Member member = memberRepository.findById(activeData.getMemberId()).orElse(null);
            if (member == null) {
                return ResponseDTO.error(1001, "User not found in DB");
            }

            surfUser = new SurfUser();
            surfUser.setMemberId(activeData.getMemberId());
            surfUser.setPcName(activeData.getPcName());
            surfUser.setCostType(activeData.getCostType());
            surfUser.setBaseBalance(member.getBaseBalance() != null ? member.getBaseBalance().floatValue() : 0f);
            surfUser.setAwardBalance(member.getAwardBalance() != null ? member.getAwardBalance().floatValue() : 0f);
            
            // Core Pricing Logic Evaluation
            long now = System.currentTimeMillis() / 1000;
            if (activeData.getCostType() == 2) { // 2 = PERIOD
                PeriodPrice period = this.rateService.findPeriodItem(surfUser.getRuleId());
                if (period == null || !period.isIn(now, false)) {
                    return ResponseDTO.error(1002, "Invalid period or not in time");
                }
                if (surfUser.remain() < period.getPrice()) {
                    return ResponseDTO.error(1003, "Insufficient balance for period");
                }
                if (!surfUser.getAreaTypeId().equals(period.getAreaId())) {
                    return ResponseDTO.error(1004, "Invalid area for period");
                }
                ExtraPrice extraPrice = this.rateService.findExtraPrice(surfUser.getMemberTypeId(), period.getAreaId());
                if (extraPrice != null) surfUser.setExtraCharge(extraPrice.getAdditionalPrice());

                surfUser.setRuleValue(period.getPrice());
                surfUser.setPeriodStartTime(period.getStartTime());
                surfUser.setPeriodEndTime(period.getEndTime());
                surfUser.setDurationTime(period.getPeriodTime());

            } else if (activeData.getCostType() == 3) { // 3 = DURATION
                DurationPrice duration = this.rateService.findDurationItem(surfUser.getRuleId());
                if (duration == null) {
                    return ResponseDTO.error(1002, "Invalid duration");
                }
                if (surfUser.remain() < duration.getPrice()) {
                    return ResponseDTO.error(1003, "Insufficient balance for duration");
                }
                if (!surfUser.getAreaTypeId().equals(duration.getAreaId())) {
                    return ResponseDTO.error(1004, "Invalid area for duration");
                }
                ExtraPrice extraPrice = this.rateService.findExtraPrice(surfUser.getMemberTypeId(), duration.getAreaId());
                if (extraPrice != null) surfUser.setExtraCharge(extraPrice.getAdditionalPrice());

                surfUser.setRuleValue(duration.getPrice());
                surfUser.setDurationTime(duration.getDurationTime());
            }

            // Register Online Database Session
            Online onlineRecord = new Online();
            onlineRecord.setMemberID(surfUser.getMemberId());
            onlineRecord.setMachineName(surfUser.getPcName());
            onlineRepository.save(onlineRecord);
            
            surfUserMap.put(surfUser.getMemberId(), surfUser);

            // TODO: Publisher component ZeroMQ notifications
        } else {
             // standard to period transfer logic
        }

        return ResponseDTO.success(surfUser);
    }

    

    public ResponseDTO pcLoginUser(Map<String, Object> map) {
        Long memberId = Long.parseLong(map.get("memberID").toString());
        String pcName = map.get("pcName").toString();
        String password = map.get("pwd").toString();
        Long loginType = Long.parseLong(map.get("loginType").toString());

        // 1. Verify PC exists
        Object surfPc = surfPcMap.get(pcName);
        if (surfPc == null) {
            return ResponseDTO.error(1005, "PC Not Found");
        }

        // 2. Lookup User
        SurfUser surfUser = surfUserMap.get(memberId);
        if (surfUser == null) {
            if (billingRate.getNeedActive() != null && billingRate.getNeedActive() == 0) {
                // Auto login user logic
                ActiveData dummyActive = new ActiveData();
                dummyActive.setMemberId(memberId);
                dummyActive.setPcName(pcName);
                ResponseDTO activeResp = activeUser(dummyActive);
                if (activeResp.getStatus() == 0) {
                    surfUser = (SurfUser) activeResp.getData();
                }
            }
            
            if (surfUser == null) {
                return ResponseDTO.error(1006, "User not active or does not exist");
            }
        }

        // 3. Password Verification
        if (loginType == 1) { // 1 = WX login
             // WeChat MD5 logic verification here
        } else {
            if (!password.equals(surfUser.getPassword())) {
                return ResponseDTO.error(1007, "Invalid Password");
            }
        }

        // 4. Check repeat login
        if (pcName.equals(surfUser.getPcName())) {
            return ResponseDTO.success(surfUser);
        }

        // 5. Existing user on PC kick-off
        // If surfPc has a user mapped, log them off first
        // TODO: map the PC User logic closely once SurfPc POJO is mapped

        // 6. Cost Type Logic (Week Pricing override upon logon)
        long now = System.currentTimeMillis() / 1000;
        if (surfUser.getCostType() == 1) { // 1 = WEEK
            WeekPrice weekPrice = this.rateService.findWeekItem(surfUser.getMemberTypeId(), surfUser.getAreaTypeId());
            if (weekPrice != null) {
                weekPrice.resetSurfUser(surfUser, surfUser.getAreaTypeId(), now);
            }
            
            // 7. DB Save
            Online onlineRecord = onlineRepository.findByMemberIDAndOffLineTimeIsNull(surfUser.getMemberId());
            if (onlineRecord != null) {
                onlineRecord.setMachineName(pcName);
                onlineRepository.save(onlineRecord);
            }
            surfUserMap.put(surfUser.getMemberId(), surfUser);
        }

        surfUser.setPcName(pcName);

        // TODO: Publish ZeroMQ LOGON event
        return ResponseDTO.success(surfUser);
    }

    public ResponseDTO logOffUser(Long memberID, boolean isFromCashier, boolean force, boolean isNoteClient) {
        SurfUser surfUser = surfUserMap.get(memberID);
        if (surfUser == null) {
            return ResponseDTO.error(1006, "User not active");
        }

        if (!isFromCashier && surfUser.getMemberTypeId() == 0 && surfUser.getTempBalance() > 0) {
            return ResponseDTO.error(1008, "Temporary user cannot log off with balance");
        }

        // Clear PC mapped data
        Object surfPc = surfPcMap.get(surfUser.getPcName());
        if (surfPc != null) {
            // surfPc.setLpSurfUser(null);
            // surfPc.setPcHeartTime(0);
        }

        surfUserMap.remove(memberID);
        surfUser.setLogonTimestamp(System.currentTimeMillis() / 1000);

        // Update DB
        Online onlineRecord = onlineRepository.findByMemberIDAndOffLineTimeIsNull(memberID);
        if (onlineRecord != null) {
            onlineRecord.setOffLineTime(java.time.LocalDateTime.now());
            onlineRepository.save(onlineRecord);
        }

        surfUser.setPcName("");

        // TODO: Publish ZeroMQ LOGOFF event
        return ResponseDTO.success(surfUser);
    }

    public ResponseDTO queryOnlineUserList() {
        return ResponseDTO.success("Online users list");
    }

    public ResponseDTO stopAllUserCost() {
        return ResponseDTO.success("All users cost stopped");
    }

    public ResponseDTO pcHeart(Long memberID, String pcName, String pcIp, String pcMac) {
        return ResponseDTO.success("PC heartbeat received");
    }

    public ResponseDTO queryUser(Long memberID) {
        return ResponseDTO.success("User info");
    }

    public ResponseDTO changePc(Long memberID, String pcName, Long areaId) {
        return ResponseDTO.success("PC changed");
    }

    public ResponseDTO weekToPeriodOrDuration(Map<String, Object> map) {
         return ResponseDTO.success("Cost type changed");
    }

    public ResponseDTO synUserInfo(Long memberId) {
        return ResponseDTO.success("User synced");
    }

    public ResponseDTO changeToWeek(Long memberId) {
        return ResponseDTO.success("Changed to week");
    }

    public ResponseDTO payOrderByBaseBalance(Long memberId, Long orderId, Float orderCost, Float baseCost) {
        return ResponseDTO.success("Order paid");
    }

    public ResponseDTO addUser(Map<String, Object> map) {
        return ResponseDTO.success("User added");
    }

    public ResponseDTO updateUser(Map<String, Object> map) {
        return ResponseDTO.success("User updated");
    }

    public ResponseDTO chargeUser(Map<String, Object> map) {
        return ResponseDTO.success("User charged");
    }

    public ResponseDTO getMemberId(String query) {
        return ResponseDTO.success("Member ID");
    }

    public ResponseDTO searchUser(String query) {
        return ResponseDTO.success("Search user exact");
    }

    public ResponseDTO queryMemberInfo(String contype, String code) {
        return ResponseDTO.success("Member info code");
    }

    public ResponseDTO queryUserBaseInfo(String account) {
        return ResponseDTO.success("User base info");
    }

    public ResponseDTO getDutyData(Map<String, Object> map) {
        return ResponseDTO.success("Duty data");
    }

    public ResponseDTO submitDutyData(Map<String, Object> map) {
        return ResponseDTO.success("Duty data submitted");
    }

    public ResponseDTO updatePwd(Long memberID, String newPwd) {
        return ResponseDTO.success("Password updated");
    }


    private float getHourPrice(WeekPrice weekPrice, long timestamp) {

        return 0;
    }

    @Transactional
    public void cost(Netbar netbar, SimpleEntry<Online, Member> entry, long timestamp) {

        Online online = entry.getKey();
        Member member = entry.getValue();

        log.info("Handling User Cost Deduction for user {}", member.getMemberId());
        
        // Resolve weekly pricing rule
        WeekPrice weekPrice = this.rateService.findWeekItem(member.getMemberType(), online.getAreaID());
        if (weekPrice == null) {
            log.warn("WeekPrice not found for user {} – skipping cost", member.getMemberId());
            return;
        }
        // Hourly price based on current timestamp
        float hourPrice = this.getHourPrice(weekPrice, timestamp);
        if (hourPrice <= 0) {
            log.warn("HourPrice resolved to 0 for user {} – skipping", member.getMemberId());
            return;
        }
        // Apply billing ratios (base and award)
        double baseCost = hourPrice * netbar.getRatioBase();
        double awardCost = hourPrice * netbar.getRatioAward();
        // Deduct from balances, allowing award to cover deficits
        double remainBase = member.getBaseBalance() - baseCost;
        double remainAward = member.getAwardBalance() - awardCost;
        if (remainBase < 0) {
            double deficit = -remainBase;
            if (remainAward >= deficit) {
                remainAward -= deficit;
                remainBase = 0;
            } else {
                // Not enough funds – log off user
                log.info("User {} ran out of funds during weekly charge", member.getMemberId());
                logOffUser(member.getMemberId(), false, true, true);
                return;
            }
        }
        
        member.setBaseBalance(remainBase);
        member.setAwardBalance(remainAward);
        
        // Update next cost timestamp (next hour)
        online.setNextCostTimestamp(Instant.ofEpochSecond(timestamp + 3600).atZone(ZoneId.systemDefault()).toLocalDateTime());
        
        // Persist changes to DB (online record timestamp optional)         
        onlineRepository.save(online); 

        log.info("Deducted weekly cost for user {}: base {} award {}", member.getMemberId(), baseCost, awardCost);

    }

}
