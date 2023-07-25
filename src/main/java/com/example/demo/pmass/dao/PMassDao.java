package com.example.demo.pmass.dao;

import com.example.demo.pmass.entity.pmPatchReg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PMassDao extends JpaRepository<pmPatchReg, Long> {
    List<pmPatchReg> queryAllByPatchCode(String patchCode);
}
