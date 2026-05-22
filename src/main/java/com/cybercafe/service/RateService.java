package com.cybercafe.service;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.cybercafe.model.DurationPrice;
import com.cybercafe.model.ExtraPrice;
import com.cybercafe.model.PeriodPrice;
import com.cybercafe.model.WeekPrice;
import com.cybercafe.repository.DurationPriceRepository;
import com.cybercafe.repository.ExtraPriceRepository;
import com.cybercafe.repository.PeriodPriceRepository;
import com.cybercafe.repository.WeekPriceRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;



@Service
@RequiredArgsConstructor
public class RateService {
    
    private final WeekPriceRepository weekPriceRepository;
    private final DurationPriceRepository durationPriceRepository;
    private final ExtraPriceRepository extraPriceRepository;
    private final PeriodPriceRepository periodPriceRepository;

    public PeriodPrice findPeriodItem(Long ruleId) {
        return this.periodPriceRepository.findById(ruleId).orElse(null);
    }

    public DurationPrice findDurationItem(Long ruleId) {
        return this.durationPriceRepository.findById(ruleId).orElse(null);
    }

    public WeekPrice findWeekItem(Long memberType, Long areaId) {
        WeekPrice weekPrice = new WeekPrice();
        weekPrice.setMemberType(memberType);
        weekPrice.setAreaId(areaId);
        Example<WeekPrice> example = Example.of(weekPrice); 
        List<WeekPrice> list = this.weekPriceRepository.findAll(example);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public ExtraPrice findExtraPrice(Long memberType, Long areaId) {
        ExtraPrice extraPrice = new ExtraPrice();
        extraPrice.setMemberTypeId(memberType);
        extraPrice.setAreaTypeId(areaId);
        List<ExtraPrice> list = this.extraPriceRepository.findAll(Example.of(extraPrice));
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

}
