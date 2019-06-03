package org.flyants.authorize.domain.repository;

import org.flyants.authorize.domain.entity.platform.dynamic.DynamicAssist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author zhangchao
 * @Date 2019/4/25 15:33
 * @Version v1.0
 */
@Repository
public interface DynamicAssistRepository extends JpaRepository<DynamicAssist,String> {

    List<DynamicAssist>  findAllByDynamicId(String dynamicId);
}