package io.sufeng.context.domain.repository;

import io.sufeng.context.domain.entity.LoginMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @Author zhangchao
 * @Date 2019/5/22 9:32
 * @Version v1.0
 */
@Repository
public interface LoginMethodRepository extends JpaRepository<LoginMethod,String> {

    Optional<LoginMethod> findByTypeAndMark(LoginMethod.LoginType loginType,String mark);
    Optional<LoginMethod> findByTypeAndPeopleId(LoginMethod.LoginType loginType,String peopleId);

}
