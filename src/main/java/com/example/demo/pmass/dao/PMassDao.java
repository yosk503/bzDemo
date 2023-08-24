package com.example.demo.pmass.dao;

import com.example.demo.pmass.entity.PmPatchReg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface PMassDao extends JpaRepository<PmPatchReg, Long> {
    @Query(value = "SELECT PR.PATCH_CODE,\n" +
            "       PR.PATCH_DISC,\n" +
            "       PM.FILEBODY FILE_BODY,\n" +
            "       PM.FILE_NAME FILE_NAME,\n" +
            "       PR.PATCH_DEVER,PR.PATCH_FILES\n" +
            "  FROM PM_ATTACH_FILES PM\n" +
            " INNER JOIN PM_PATCH_REG PR\n" +
            "    ON PM.BUS_ID = PR.PATCH_ID\n" +
            " WHERE PR.PATCH_CODE IN (:patchCode)",nativeQuery = true)
    List<Map<String,Object>> queryAllByPatchCode(@Param("patchCode") List<String> patchCode);


    @Query(value = "UPDATE PM_PATCH_REG PM\n" +
            "   SET PM.STAT = :stat\n" +
            " WHERE 1 = 1\n" +
            "   AND PM.PATCH_CODE IN (:patchCode)",nativeQuery = true)
    int updateStat(@Param("stat") String stat,@Param("patchCode") List<String> patchCode);


    @Query(value = "SELECT PR.PATCH_CODE,\n" +
            "       PR.PATCH_DISC,\n" +
            "       PM.FILEBODY FILE_BODY,\n" +
            "       PM.FILE_NAME FILE_NAME,\n" +
            "       PR.PATCH_DEVER\n" +
            "  FROM PM_ATTACH_FILES PM,PR.PATCH_FILES\n" +
            " INNER JOIN PM_PATCH_REG PR\n" +
            "    ON PM.BUS_ID = PR.PATCH_ID\n" +
            " WHERE PR.PATCH_CODE IN (:patchCode)",nativeQuery = true)
    List<PmPatchReg> queryPatchCode(@Param("patchCode") List<String> patchCode);
}
