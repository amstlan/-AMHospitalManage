package com.amstlan.yygh.user.service;

import com.amstlan.yygh.model.user.Patient;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PatientService extends IService<Patient> {

    List<Patient> findAllUserId(Long userId);

    Patient getPatientId(Long id);
}
