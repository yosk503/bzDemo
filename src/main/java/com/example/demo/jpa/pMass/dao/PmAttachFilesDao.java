package com.example.demo.jpa.pMass.dao;

import com.example.demo.jpa.pMass.entity.PmAttachFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PmAttachFilesDao extends JpaRepository<PmAttachFiles, Long> {
    @Query(value = "SELECT pm.attach_id,pm.bus_id,pm.FILEBODY file_body,pm.file_name FROM PM_ATTACH_FILES pm WHERE pm.BUS_ID=:busId",nativeQuery = true)
    List<PmAttachFiles> queryAllByBusId(@Param("busId") String busId);
}
