package com.cybercafe.service;

import com.cybercafe.component.TimeService;
import com.cybercafe.model.Billing;
import com.cybercafe.model.DurationPrice;
import com.cybercafe.model.Machine;
import com.cybercafe.model.Member;
import com.cybercafe.model.Netbar;
import com.cybercafe.model.Online;
import com.cybercafe.model.PeriodPrice;
import com.cybercafe.model.WeekPrice;
import com.cybercafe.model.exception.SurfException;
import com.cybercafe.model.logic.CostPair;
import com.cybercafe.model.types.OnlineType;
import com.cybercafe.repository.BillingRepository;
import com.cybercafe.repository.MemberRepository;
import com.cybercafe.repository.NetbarRepository;
import com.cybercafe.repository.OnlineRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
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
    private final NetbarRepository netbarRepository;

    private final MemberService memberService;
    private final MachineService machineService;
    private final RateService rateService;
    private final TimeService timeService;


    public Online activateUser(Long gid, Long memberId, Long areaId, String machineName, Long durationId, Long periodId)
        throws SurfException {
        
        Member member = this.memberService.findMember(gid, memberId).orElseThrow(() -> new SurfException("member is not exists"));
        
        Online online = this.getOnlineByMember(member);
        if (online != null) {
            throw new SurfException("only active once");
        }

        online = Online.builder().gid(gid).memberId(memberId).onlineActiveTime(this.timeService.nowLocalDateTime())    
                .build();
        
        if (durationId != null) {
            DurationPrice durationPrice = this.rateService.findDurationItem(durationId).orElseThrow(() -> new SurfException("not"));
            return this.activateMember(member, online, durationPrice);
        } else if (periodId != null) {
            PeriodPrice periodPrice = this.rateService.findPeriodItem(periodId).orElseThrow(() -> new SurfException("not"));
            return this.activateMember(member, online, periodPrice);
        } else {
            return this.activateMember(member, online);
        }

    }

    public Online memberLoginMachine(Long gid, Long memberId, String pcName, String password) throws SurfException {

        Netbar netbar = this.netbarRepository.findById(gid).orElseThrow(() -> new SurfException("gid is incorrect"));
        Online online = this.findOnlineByMember(gid, memberId).orElseThrow(() -> new SurfException("the member is not actived yet"));
        Member member = this.memberService.findMember(gid, memberId).orElseThrow(() -> new SurfException("member is not existed"));
        Machine machine = this.getMachine(gid, pcName);

        if (this.memberHasLogin(online)) {
            this.updateOnlineMachine(online, machine);
            return online;
        }

        if (online.getRuleType().intValue() == OnlineType.WEEK.typeId()) {
            return this.memberLoginByWeek(netbar, member, online, machine);
        } else {
            if (online.getRuleType().intValue() == OnlineType.PERIOD.typeId()) {
                return this.memberLoginByPeriod(netbar, member, online, machine);
            } else if (online.getRuleType().intValue() == OnlineType.DURATION.typeId()) {
                return this.memberLoginByDuration(netbar, member, online, machine);
            } else {
                throw new SurfException("");
            }
        }

    }

    private boolean memberHasLogin(Online online) {
        return online.getMachineId() != null;
    }

    private Online activateMember(Member member, Online online) {
        online.setRuleType(OnlineType.WEEK.typeId());
        this.onlineRepository.save(online);
        return online;
    }

    private Online activateMember(Member member, Online online, PeriodPrice periodPrice) {
        online.setRuleType(OnlineType.PERIOD.typeId());
        online.setRuleId(periodPrice.getRuleId());
        this.onlineRepository.save(online);
        return online;
    }

    private Online activateMember(Member member, Online online, DurationPrice durationPrice) {
        online.setRuleType(OnlineType.DURATION.typeId());
        online.setRuleId(durationPrice.getRuleId());
        this.onlineRepository.save(online);
        return online;
    }

    private Online memberLoginByWeek(Netbar netbar, Member member, Online online, Machine machine) throws SurfException {

        WeekPrice weekPrice = this.rateService.findWeekItem(member.getMemberType(), machine.getAreaId().longValue()).orElseThrow(
            () -> new SurfException("could not find the rate")
        );

        if (weekPrice.getStartPrice() > member.balance()) {
            throw new SurfException("not enough money");
        }

        this.updateOnlineMachine(online, machine);
        this.costByWeek(netbar, online, member, weekPrice, this.timeService.now());

        return online;
    }

    private Online memberLoginByPeriod(Netbar netbar, Member member, Online online, Machine machine) throws SurfException{
        PeriodPrice periodPrice = this.rateService.findPeriodItem(online.getRuleId()).orElseThrow(
            () -> new SurfException("could not find the period")
        );

        if (periodPrice.getPrice() > member.balance()) {
            throw new SurfException("not enough money");
        }

        if ((machine.getAreaId().intValue() != periodPrice.getAreaId().intValue()) || 
                (member.getMemberType().intValue() != periodPrice.getMemberType().intValue())) {
            throw new SurfException("machine does not match the period");
        }

        this.updateOnlineMachine(online, machine);
        this.costByPeriod(netbar, online, member, periodPrice, this.timeService.now());

        return online;
    }

    private Online memberLoginByDuration(Netbar netbar, Member member, Online online, Machine machine) throws SurfException{
        DurationPrice durationPrice = this.rateService.findDurationItem(online.getRuleId()).orElseThrow(
            () -> new SurfException("could not find the duration")
        );

        if (durationPrice.getPrice() > member.balance()) {
            throw new SurfException("not enough money");
        }

        if ((machine.getAreaId().intValue() != durationPrice.getAreaId().intValue()) || 
                (member.getMemberType().intValue() != durationPrice.getMemberType().intValue())) {
            throw new SurfException("machine does not match the duration");
        }

        this.updateOnlineMachine(online, machine);
        this.costByDuration(netbar, online, member, durationPrice, this.timeService.now());

        return online;
    }

    private Machine getMachine(long gid, String pcName) throws SurfException {
        return this.machineService.getMachine(gid, pcName).orElseThrow(() -> new SurfException("pcName is not existed"));
    }

    private void changeOnlineMachine(Online online, Machine machine) {

        online.setMachineId(machine.getMachineID());
        online.setMachineName(machine.getMachineName());
        this.onlineRepository.save(online);

    }

    
    private void updateOnlineMachine(Online online, Machine machine) {
        
        online.setOnlineStartTime(this.timeService.nowLocalDateTime());
        online.setMachineId(machine.getMachineID());
        online.setMachineName(machine.getMachineName());
        this.onlineRepository.save(online);

    }

    private Optional<Online> findOnlineByMember(long gid, long memberId) {
        return this.onlineRepository.findOne(Example.of(Online.builder().gid(gid).memberId(memberId).build()));
    }

    private void checkPcOccupiedOrNot(long gid, String pcName) throws SurfException {
        Online online = this.onlineRepository.findOne(Example.of(Online.builder().gid(gid).machineName(pcName).build())).get();
        if (online != null) {
            throw new SurfException("pc occupied");
        }
    }

    public void logOffUser(Long gid, Long memberId, boolean isFromCashier, boolean force, boolean isNoteClient) {
        
    }

    @Transactional
    public void userLogOff(Long memberId) {



    }

    public Online getOnlineByMember(Member member) {

        Online online = Online.builder().memberId(member.getMemberId()).build();
        return this.onlineRepository.findOne(Example.of(online)).orElse(null);

    }
 

    public Object queryOnlineUserList() {
        return "Online users list";
    }

    public void stopAllUserCost() {
    }

    public void pcHeart(Long memberId, String pcName, String pcIp, String pcMac) {
    }

    public Object queryUser(Long memberId) {
        return "User info";
    }

    public void changePc(Long memberId, String pcName, Long areaId) {
    }

    public void weekToPeriodOrDuration(Map<String, Object> map) {
    }

    public void synUserInfo(Long memberId) {
    }

    public void changeToWeek(Long memberId) {
    }

    public void payOrderByBaseBalance(Long memberId, Long orderId, Float orderCost, Float baseCost) {
    }

    public Object addUser(Map<String, Object> map) {
        return "User added";
    }

    public void updateUser(Map<String, Object> map) {
    }

    public void chargeUser(Map<String, Object> map) {
    }

    public Object getMemberId(String query) {
        return "Member ID";
    }

    public Object searchUser(String query) {
        return "Search user exact";
    }

    public Object queryMemberInfo(String contype, String code) {
        return "Member info code";
    }

    public Object queryUserBaseInfo(String account) {
        return "User base info";
    }

    public Object getDutyData(Map<String, Object> map) {
        return "Duty data";
    }

    public void submitDutyData(Map<String, Object> map) {
    }

    public void updatePwd(Long memberId, String newPwd) {
    }


    private int getHourPrice(WeekPrice weekPrice, long timestamp) {
        if (weekPrice == null) {
            return 0;
        }

        //

        return 0;
    }

    public int calculateMaxOnlineTime() {
        return 0;
    }

    

    public int calculateOnlineTimeByCost(float hourRate, int cost) {

        int secondsByHour = 60 * 60;
        return (int)((cost / hourRate) * secondsByHour);

    }

    private SimpleEntry<Integer, Integer> calculateCostAndTime(Online online, Member member, WeekPrice weekPrice, Long timestamp) {
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

    public void generateBilling(Online online, CostPair costPair, long timestamp, int lastSeconds) {

        Billing billing = Billing.builder().gid(online.getGid())
                                    .memberID(online.getMemberId())
                                    .onlineID(online.getOnlineId())
                                    .ruleType(online.getRuleType())
                                    .ruleId(online.getRuleId())
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

    private void handlePeriodEnd() {
        //TODO, if the period or duration reaches the end, then logoff the members 
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
        online.setRuleId(periodPrice.getRuleId());
        this.deductMember(netbar, online, member, cost, lastSeconds, timestamp);
    }

    public void costByDuration(Netbar netbar, Online online, Member member, DurationPrice durationPrice, long timestamp) throws SurfException {

        if (member.balance() < durationPrice.getPrice()) {
            throw new SurfException("low balance");
        }

        int cost = durationPrice.getPrice();
        int lastSeconds = durationPrice.getDurationTime().intValue();

        online.setRuleType(OnlineType.DURATION.typeId());
        online.setRuleId(durationPrice.getRuleId());
        this.deductMember(netbar, online, member, cost, lastSeconds, timestamp);

    }

    public void costByWeek(Netbar netbar, Online online, Member member, WeekPrice weekPrice, long timestamp) {

        if (weekPrice == null) {
            log.warn("WeekPrice not found for user {} – skipping cost", member.getMemberId());
            return;
        }

        int lastSeconds = 0;
        int cost = 0;

        SimpleEntry<Integer, Integer> costAndLastSeconds = this.calculateCostAndTime(online, member, weekPrice, timestamp);
    
        cost = costAndLastSeconds.getKey();
        lastSeconds = costAndLastSeconds.getValue();
        
        online.setRuleType(OnlineType.WEEK.typeId());
        online.setRuleId(weekPrice.getRuleId());
        this.deductMember(netbar, online, member, cost, lastSeconds, timestamp);

    }

    private void deductMember(Netbar netbar, Online online, Member member, int cost, int lastSeconds, long timestamp) {

        log.info("Handling User Cost Deduction for user {}", member.getMemberId());
        CostPair costPair = this.deductMemberBalance(netbar, member, cost);

        this.updateOnlineCost(online, costPair, timestamp, lastSeconds);
        this.generateBilling(online, costPair, timestamp, lastSeconds);

        log.info("Deducted weekly cost for user {}: base {} award {}", member.getMemberId(), costPair.costBase(), costPair.costAward());

    }



}
