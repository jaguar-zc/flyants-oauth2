package org.flyants.authorize.domain.service.impl;

import org.flyants.authorize.configuration.PageResult;
import org.flyants.authorize.domain.entity.oauth2.OAuthClient;
import org.flyants.authorize.domain.entity.oauth2.OAuthClientResource;
import org.flyants.authorize.domain.repository.JpaSpecification;
import org.flyants.authorize.domain.repository.OAuthClientRepository;
import org.flyants.authorize.domain.service.AppService;
import org.flyants.authorize.utils.ResourceUtils;
import org.flyants.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author zhangchao
 * @Date 2019/5/16 16:03
 * @Version v1.0
 */
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    OAuthClientRepository OAuthClientRepository;


    @Override
    public PageResult<OAuthClient> findList(Integer page,Integer size,String searchBy,String keyWord) {
        PageRequest of = PageRequest.of(page - 1, size);
        Specification specification = JpaSpecification.getSpecification(searchBy, keyWord);

        Page<OAuthClient> all = OAuthClientRepository.findAll(specification,of);

        return new PageResult<OAuthClient>(all.getTotalElements(), all.getContent());
    }

    @Override
    public void save(OAuthClient client) {
        client.setStatus(0);
        client.setPeople(ResourceUtils.getCurrentPeople());
        OAuthClientResource resource = new OAuthClientResource();
        resource.setClientId(client.getClientId());
        resource.setResource("昵称、头像、手机号");
        client.setOAuthClientResource(resource);
        OAuthClientRepository.saveAndFlush(client);
    }

    @Override
    public void update(String id, OAuthClient oAuthClientPa) {
        Optional<OAuthClient> optionalOAuthClient = OAuthClientRepository.findById(id);
        if(!optionalOAuthClient.isPresent()){
            throw  new BusinessException("AppId不存在");
        }
        oAuthClientPa.setClientId(id);
        OAuthClientRepository.saveAndFlush(oAuthClientPa);
    }

    @Override
    public OAuthClient find(String clientId) {
        return OAuthClientRepository.findById(clientId).orElseThrow(()->new BusinessException("ClientID不存在"));
    }
}