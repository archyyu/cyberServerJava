package com.cybercafe.service;

import com.cybercafe.model.Billing;
import com.cybercafe.model.DurationPrice;
import com.cybercafe.model.Member;
import com.cybercafe.model.Netbar;
import com.cybercafe.model.Online;
import com.cybercafe.model.PeriodPrice;
import com.cybercafe.model.WeekPrice;
import com.cybercafe.model.dto.ResponseDTO;
import com.cybercafe.model.exception.SurfException;
import com.cybercafe.model.logic.CostPair;
import com.cybercafe.model.types.OnlineType;
import com.cybercafe.repository.BillingRepository;
import com.cybercafe.repository.MemberRepository;
import com.cybercafe.repository.OnlineRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurfLogicService {
    
    private final MemberRepository memberRepository;
    private final OnlineRepository onlineRepository;
    private final BillingRepository billingRepository;
    private final RateService rateService;


    public ResponseDTO activeUser(Long memberId, Long areaId, String machineName, Long durationId, Long periodId) {
        
        

        return null;
    }

    public ResponseDTO pcLoginUser(Long memberId, String pcName, String password) {


        return null;
    }

    public ResponseDTO logOffUser(Long memberID, boolean isFromCashier, boolean force, boolean isNoteClient) {
        
        return null;
    }

    @Transactional
    public void userLogOff(Long memberId) {



    }

    public Online getOnlineByMember(Member member) {

        Online online = Online.builder().memberID(member.getMemberId()).build();
        return this.onlineRepository.findOne(Example.of(online)).orElse(null);

    }

    public Online generateOnline(Member member, WeekPrice weekPrice) {
        Online online = this.getOnlineByMember(member);
        if (online != null) {
            return online;
        }
        
        online = Online.builder().memberID(member.getMemberId())
                                        .ruleID(weekPrice.getRuleId())
                                        .onlineActiveTime(this.timestampToLocalDateTime(System.currentTimeMillis() / 1000))
                                        .ignoreTime(null)
                                        .build();

        this.onlineRepository.save(online);
        return online;

    }

    public void generateOnline(Member member, DurationPrice durationPrice) {

    }

    public void generateOnline(Member member, PeriodPrice periodPrice) {

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


    private int getHourPrice(WeekPrice weekPrice, long timestamp) {
        if (weekPrice == null) {
            return 0;
        }



        return 0;
    }

    public int calculateMaxOnlineTime() {
        return 0;
    }

    

    public int calculateOnlineTimeByCost(float hourRate, int cost) {

        int secondsByHour = 60 * 60;
        return (int)((cost / hourRate) * secondsByHour);

    }

    private SimpleEntry<Integer, Integer> calculateTheCostAndOnlineTime(Online online, Member member, WeekPrice weekPrice, Long timestamp) {
        int cost = 0;
        int lastSeconds = 0;
        if (online.getIgnoreTime().intValue() < weekPrice.getIgnoreTime()) {
            // firstly should check the ignore time, in that case, cost 0 and online time lasts the ignore time
            online.setIgnoreTime(weekPrice.getIgnoreTime().intValue());
            cost = 0;
            lastSeconds = online.getIgnoreTime();
        } else {

            // if has processed the ignore time or the ignore time is 0, calculate how much should charge the user
            if (online.getStartCost().intValue() < weekPrice.getStartPrice()) {
                online.setStartCost(weekPrice.getStartPrice());
                cost = online.getStartPrice();
            } else {
                cost = Math.min(member.balance(), weekPrice.getMinCostPrice());
            }

            // Hourly price based on current timestamp
            int hourPrice = this.getHourPrice(weekPrice, timestamp);
            if (hourPrice <= 0) {
                log.warn("HourPrice resolved to 0 for user {} – skipping", member.getMemberId());
                
            }
            lastSeconds = this.calculateOnlineTimeByCost(hourPrice, cost);
        }
        return new SimpleEntry<Integer, Integer>(cost, lastSeconds);
    }


    public void updateOnlineCost(Online online, CostPair costPair, long timestamp, int lastSeconds) {

        online.setAllHadCost(online.getAllHadCost() + costPair.all());
        online.setAllCostBase(online.getAllCostBase() + costPair.costBase());
        online.setAllCostAward(online.getAllCostAward() + costPair.costAward());
        online.setAllCostCash(online.getAllCostCash() + costPair.costCash());
        online.setLastCostTimestamp(Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime());
        online.setNextCostTimestamp(Instant.ofEpochSecond(timestamp + lastSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime());
        this.onlineRepository.save(online);

    }

    private LocalDateTime timestampToLocalDateTime(long timestamp) {
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public void generateTheBilling(Online online, CostPair costPair, long timestamp, int lastSeconds) {

        Billing billing = Billing.builder().gid(online.getGid())
                                    .memberID(online.getMemberID())
                                    .onlineID(online.getOnlineID())
                                    .ruleType(online.getRuleType())
                                    .ruleId(online.getRuleID())
                                    .currentCostBase(costPair.costBase())
                                    .currentCostAward(costPair.costAward())
                                    .currentCostTemp(costPair.costCash())
                                    .currentCostTimestamp(this.timestampToLocalDateTime(timestamp))
                                    .build();
        
        this.billingRepository.save(billing);

    }



    public CostPair deductMemberBalance(Netbar netbar, Member member, int cost) {
        // Apply billing ratios (base and award)
        int baseCost = cost * (netbar.getRatioBase() / (netbar.getRatioAward() + netbar.getRatioBase()));
        int awardCost = cost - baseCost;
        // Deduct from balances, allowing award to cover deficits
        int remainBase = member.getBaseBalance() - baseCost;
        int remainAward = member.getAwardBalance() - awardCost;
        
        //TODO if base or award is not enough
        
        member.setBaseBalance(remainBase);
        member.setAwardBalance(remainAward);

        this.memberRepository.save(member);

        return new CostPair(baseCost, awardCost, 0);

    }

    //calculate how much should charge the member,
    @Transactional
    public void cost(Netbar netbar, SimpleEntry<Online, Member> entry, long timestamp) { 
        
    }

    public void costByPeriod(Netbar netbar, Online online, Member member, PeriodPrice periodPrice, long timestamp) throws SurfException {
        if (member.balance() < periodPrice.getPrice()) {
            throw new SurfException("low balance");
        }

        if (periodPrice.isIn(timestamp) == false) {
            throw new SurfException("not in the period time");
        }

        int cost = periodPrice.getPrice();
        int lastSeconds = periodPrice.secondsToGo(timestamp);

        online.setRuleType(OnlineType.DURATION.typeId());
        online.setRuleID(periodPrice.getRuleId());
        this.deductMember(netbar, online, member, cost, lastSeconds, timestamp);
    }

    public void costByDuration(Netbar netbar, Online online, Member member, DurationPrice durationPrice, long timestamp) throws SurfException {

        if (member.balance() < durationPrice.getPrice()) {
            throw new SurfException("low balance");
        }

        int cost = durationPrice.getPrice();
        int lastSeconds = durationPrice.getDurationTime().intValue();

        online.setRuleType(OnlineType.DURATION.typeId());
        online.setRuleID(durationPrice.getRuleId());
        this.deductMember(netbar, online, member, cost, lastSeconds, timestamp);

    }

    public void costByWeek(Netbar netbar, Online online, Member member, WeekPrice weekPrice, long timestamp) {

        if (weekPrice == null) {
            log.warn("WeekPrice not found for user {} – skipping cost", member.getMemberId());
            return;
        }

        int lastSeconds = 0;
        int cost = 0;

        SimpleEntry<Integer, Integer> costAndLastSeconds = this.calculateTheCostAndOnlineTime(online, member, weekPrice, timestamp);
    
        cost = costAndLastSeconds.getKey();
        lastSeconds = costAndLastSeconds.getValue();
        
        online.setRuleType(OnlineType.WEEK.typeId());
        online.setRuleID(weekPrice.getRuleId());
        this.deductMember(netbar, online, member, cost, lastSeconds, timestamp);

    }

    private void deductMember(Netbar netbar, Online online, Member member, int cost, int lastSeconds, long timestamp) {

        log.info("Handling User Cost Deduction for user {}", member.getMemberId());
        CostPair costPair = this.deductMemberBalance(netbar, member, cost);

        this.updateOnlineCost(online, costPair, timestamp, lastSeconds);
        this.generateTheBilling(online, costPair, timestamp, lastSeconds);

        log.info("Deducted weekly cost for user {}: base {} award {}", member.getMemberId(), costPair.costBase(), costPair.costAward());

    }



}
