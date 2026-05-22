package com.cybercafe.service;

import org.springframework.stereotype.Service;

import com.cybercafe.model.Machine;
import com.cybercafe.repository.MachineRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MachineService {
    
    private MachineRepository machineRepository;

    public List<Machine> loadMachines(int gid) {
        return this.machineRepository.findByStateAndGid(1, (long)gid);
    }

}
