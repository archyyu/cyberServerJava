package com.cybercafe.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cybercafe.model.Member;
import com.cybercafe.model.Netbar;
import com.cybercafe.model.Online;
import com.cybercafe.repository.MemberRepository;
import com.cybercafe.repository.NetbarRepository;
import com.cybercafe.repository.OnlineRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurfSchedulerService {
    
    private final SurfLogicService surfLogicService;
    
    private final OnlineRepository onlineRepository;

    private final MemberRepository memberRepository;

    private final NetbarRepository netbarRepository;

    
    public List<Online> getOnlineList(Long gid) {

        Online online = new Online();
        online.setGid(gid);
        List<Online> onlineList = this.onlineRepository.findAll(Example.of(online));
        return onlineList;

    }

    public List<Member> getMemberList(List<Long> list) {
        return this.memberRepository.findAllById(list);
    }

    @Scheduled(cron = "*/5 * * * * *")
    @Async
    public void tick() {

        List<Netbar> netbarList = this.netbarRepository.findAll();
        netbarList.forEach( item -> {
            this.netbarTick(item);
        });

    }


    public void netbarTick(Netbar netbar) {

        List<Online> onlineList = this.getOnlineList(netbar.getGid());
        List<Member> memberList = this.getMemberList(onlineList.stream().map( item -> {return item.getMemberID();}).toList());

        long timestamp = System.currentTimeMillis();
        
        for(int i=0; i< onlineList.size(); i++) {
            SimpleEntry<Online, Member> entry = new SimpleEntry<>(onlineList.get(i), memberList.get(i));
            this.surfLogicService.cost(netbar, entry, timestamp);
        }

    }

}
