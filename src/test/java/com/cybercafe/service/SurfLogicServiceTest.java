package com.cybercafe.service;

import com.cybercafe.model.Billing;
import com.cybercafe.model.DurationPrice;
import com.cybercafe.model.Member;
import com.cybercafe.model.Netbar;
import com.cybercafe.model.Online;
import com.cybercafe.model.exception.SurfException;
import com.cybercafe.model.types.OnlineType;
import com.cybercafe.repository.BillingRepository;
import com.cybercafe.repository.MemberRepository;
import com.cybercafe.repository.OnlineRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurfLogicServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private OnlineRepository onlineRepository;

    @Mock
    private BillingRepository billingRepository;

    @Mock
    private RateService rateService;

    @InjectMocks
    private SurfLogicService surfLogicService;

    @Captor
    private ArgumentCaptor<Billing> billingCaptor;

    private static final long TIMESTAMP = 1_700_000_000L;

    private Netbar netbar;
    private Member member;

    @BeforeEach
    private void init() {

        this.netbar =Netbar.builder()
            .RatioBase(100).RatioAward(0).build();


        this.member = Member.builder().memberId(1L)
                .baseBalance(1000)
                .awardBalance(0)
                .cashBalance(0)
                .build();

    }

    @Test
    void costByDuration_shouldDeductAndUpdate_whenBalanceSufficient() throws Exception {
        

        DurationPrice durationPrice = new DurationPrice();
        durationPrice.setRuleId(10L);
        durationPrice.setPrice(500);
        durationPrice.setDurationTime(3600L);

        Online online = Online.builder()
                .onlineID(100L)
                .memberID(1L)
                .gid(1L)
                .build();
        online.setAllHadCost(0);
        online.setAllCostBase(0);
        online.setAllCostAward(0);
        online.setAllCostCash(0);

        surfLogicService.costByDuration(netbar, online, member, durationPrice, TIMESTAMP);

        assertEquals(OnlineType.DURATION.typeId(), online.getRuleType());
        assertEquals(10L, online.getRuleID());

        assertEquals(500, member.getBaseBalance().intValue());
        assertEquals(0, member.getAwardBalance().intValue());

        assertEquals(500, online.getAllHadCost().intValue());
        assertEquals(500, online.getAllCostBase().intValue());
        assertEquals(0, online.getAllCostAward().intValue());
        assertNotNull(online.getLastCostTimestamp());
        assertNotNull(online.getNextCostTimestamp());

        verify(memberRepository).save(member);
        verify(onlineRepository).save(online);
        verify(billingRepository).save(billingCaptor.capture());

        Billing savedBilling = billingCaptor.getValue();
        assertEquals(online.getOnlineID(), savedBilling.getOnlineID());
        assertEquals(member.getMemberId(), savedBilling.getMemberID());
        assertEquals(durationPrice.getRuleId(), savedBilling.getRuleId());
        assertEquals(OnlineType.DURATION.typeId(), savedBilling.getRuleType());
        assertEquals(500, savedBilling.getCurrentCostBase().intValue());
        assertEquals(0, savedBilling.getCurrentCostAward().intValue());
        assertEquals(0, savedBilling.getCurrentCostTemp().intValue());
        assertNotNull(savedBilling.getCurrentCostTimestamp());
    }

    @Test
    void costByDuration_shouldDeductAwardBalance_whenRatioHasAwardPortion() throws Exception {
        
        this.member.setAwardBalance(1000);

        DurationPrice durationPrice = new DurationPrice();
        durationPrice.setRuleId(10L);
        durationPrice.setPrice(300);
        durationPrice.setDurationTime(1800L);

        Online online = Online.builder()
                .onlineID(100L)
                .memberID(1L)
                .gid(1L)
                .build();
        online.setAllHadCost(0);
        online.setAllCostBase(0);
        online.setAllCostAward(0);
        online.setAllCostCash(0);

        surfLogicService.costByDuration(netbar, online, member, durationPrice, TIMESTAMP);

        assertEquals(1000, member.getBaseBalance().intValue());
        assertEquals(700, member.getAwardBalance().intValue());

        assertEquals(300, online.getAllHadCost().intValue());
        assertEquals(0, online.getAllCostBase().intValue());
        assertEquals(300, online.getAllCostAward().intValue());
    }

    @Test
    void costByDuration_shouldThrowException_whenBalanceInsufficient() {

        DurationPrice durationPrice = new DurationPrice();
        durationPrice.setPrice(5000);
        durationPrice.setDurationTime(3600L);

        Online online = Online.builder()
                .onlineID(100L)
                .memberID(1L)
                .gid(1L)
                .build();
        online.setAllHadCost(0);
        online.setAllCostBase(0);
        online.setAllCostAward(0);
        online.setAllCostCash(0);

        SurfException exception = assertThrows(SurfException.class,
                () -> surfLogicService.costByDuration(this.netbar, online, this.member, durationPrice, TIMESTAMP));
        assertEquals("low balance", exception.getMessage());

        verifyNoInteractions(memberRepository, onlineRepository, billingRepository);
    }
}
