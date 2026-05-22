package com.cybercafe.repository;

import com.cybercafe.model.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MachineRepository extends JpaRepository<Machine, Long> {
    
    // loadAllPc
    List<Machine> findByStateAndGid(Integer state, Long gid);
    
    // updatePc
    @Modifying
    @Query("UPDATE Machine m SET m.ip = :ip WHERE m.machineName = :pcName")
    int updateMachineIp(@Param("pcName") String pcName, @Param("ip") String ip);
}
