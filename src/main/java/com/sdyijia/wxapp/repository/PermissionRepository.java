package com.sdyijia.wxapp.repository;

import com.sdyijia.wxapp.bean.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionRepository extends JpaRepository<SysPermission, Long> {

    List findAllByParentIdOrderByIdAsc(Long id);

    void deleteAllByParentId(Long parentId);

}
