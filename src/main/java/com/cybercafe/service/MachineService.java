package com.cybercafe.service;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.cybercafe.model.Area;
import com.cybercafe.model.Machine;
import com.cybercafe.repository.AreaRepository;
import com.cybercafe.repository.MachineRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MachineService {
    
    private final MachineRepository machineRepository;

    private final AreaRepository areaRepository;

    public List<Machine> loadMachines(long gid) {
        return this.machineRepository.findByStateAndGid(1, gid);
    }

    public List<Area> loadAreas(long gid) {
        Area area = new Area();
        area.setGid(gid);
        return this.areaRepository.findAll(Example.of(area));
    }

    public Optional<Machine> getMachine(long gid, String machine) {

        Machine machineItem = new Machine();
        machineItem.setGid(gid);
        machineItem.setMachineName(machine);
        return this.machineRepository.findOne(Example.of(machineItem));
        
    }

    public Area getArea(Machine machine) {
        if (machine == null) {
            return null;
        }
        return this.areaRepository.findById(machine.getGid()).orElse(null);
    }

    public Area getArea(long gid, String machine) {
        Machine machineItem = this.getMachine(gid, machine).orElse(null);
        if (machineItem == null) {
            return null;
        }
        return this.getArea(machineItem);
    }

}
