package io.sufeng.context.domain.service.impl;

import io.sufeng.context.configuration.Constents;
import io.sufeng.context.configuration.PageResult;
import io.sufeng.context.domain.entity.People;
import io.sufeng.context.domain.entity.comments.CommentsType;
import io.sufeng.context.domain.entity.dynamic.Dynamic;
import io.sufeng.context.domain.entity.message.MessageUser;
import io.sufeng.context.domain.repository.*;
import io.sufeng.context.domain.service.DynamicService;
import io.sufeng.context.dto.app.DynamicAddDto;
import io.sufeng.context.dto.app.DynamicDto;
import io.sufeng.context.dto.app.MessageUserSimpleInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jagua on 2019/5/27.
 */
@Service
public class DynamicServiceImpl implements DynamicService {



    @Autowired
    DynamicRepository dynamicRepository;

    @Autowired
    DynamicAssistRepository dynamicAssistRepository;

    @Autowired
    PeopleRepository peopleRepository;

    @Autowired
    MessageFirendsRepository messageFirendsRepository;

    @Autowired
    MessageUserRepository messageUserRepository;

    @Autowired
    CommentsRepository dynamicCommentsRepository;

    @Override
    public void publishDynamic(String peopleId, DynamicAddDto dynamic) {
        int count = 0;
        if (!StringUtils.isEmpty(dynamic.getText())) {
            count++;
        }
        if (!StringUtils.isEmpty(dynamic.getImages()) && dynamic.getImages().size() > 0) {
            count++;
        }

        if (count == 0) {
            return;
        }

        String join = String.join("@", dynamic.getImages());

        Dynamic dynamic1 = new Dynamic();
        dynamic1.setCreateTime(new Date());
        dynamic1.setPeopleId(peopleId);
        dynamic1.setText(dynamic.getText());
        dynamic1.setImages(join);
        dynamic1.setLocation(dynamic.getLocation());
        dynamic1.setVisibility(dynamic.getVisibility());
        dynamicRepository.saveAndFlush(dynamic1);
    }

    @Override
    public PageResult<DynamicDto> list(Integer page, Integer size, List<String> peopleId) {
        PageRequest of = PageRequest.of(page - 1, size,  Sort.by(Sort.Order.desc("createTime")));
        Specification spec = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                List<Predicate> pr = new ArrayList<>();
                if (!StringUtils.isEmpty(peopleId)) {
                    pr.add(root.get("peopleId").as(String.class).in(peopleId));
                }
                return cb.and(pr.toArray(new Predicate[pr.size()]));
            }
        };

        Page<Dynamic> all = dynamicRepository.findAll(spec, of);
        long totalElements = all.getTotalElements();
        List<Dynamic> content = all.getContent();

        List<DynamicDto> collect = content.stream().map(item -> {

            Optional<People> people = peopleRepository.findById(item.getPeopleId());
            DynamicDto dynamicDto = new DynamicDto();
            dynamicDto.setId(item.getId());
            dynamicDto.setCreateTime(item.getCreateTime());
            dynamicDto.setPeopleId(item.getPeopleId());
            dynamicDto.setNickName(people.get().getNickName());
            dynamicDto.setEncodedPrincipal(people.get().getEncodedPrincipal()+ Constents.IMAGE_STYLE_SMALL);
            dynamicDto.setText(item.getText());
            dynamicDto.setImages(Arrays.asList(item.getImages().split("@")));
            dynamicDto.setLocation(item.getLocation());
            dynamicDto.setVisibility(item.getVisibility());

            List<MessageUserSimpleInfoDto> collect1 = dynamicAssistRepository.findAllByDynamicId(item.getId())
                    .stream()
                    .map(i -> peopleRepository.findById(i.getPeopleId()))
                    .filter(i -> i.isPresent())
                    .map(i -> i.get())
                    .map(i ->
                            new MessageUserSimpleInfoDto(i.getId(), i.getNickName(), i.getEncodedPrincipal())
                    ).collect(Collectors.toList());

            dynamicDto.setAssistPeopleList(collect1);

            Integer commentsCount = dynamicCommentsRepository.countByResourceIdAndCommentsType(item.getId(), CommentsType.DYNAMIC);

            dynamicDto.setCommentsCount(commentsCount);
            return dynamicDto;
        }).collect(Collectors.toList());


        return new PageResult<DynamicDto>(totalElements, collect);
    }

    @Override
    public PageResult<DynamicDto> listSelf(Integer page, Integer size, String peopleId) {
        return list(page, size, Arrays.asList(peopleId));
    }

    @Override
    public PageResult<DynamicDto> listFriend(Integer page, Integer size, String peopleId) {
        List<String> peopleIds = new ArrayList<>();
        peopleIds.add(peopleId);
        MessageUser myMessageUser = messageUserRepository.findById(peopleId).get();
        //查询我的朋友 并且将其转换为 peopleId 数组
        List<String> collect = messageFirendsRepository.findAllByMyMessageUserId(myMessageUser.getId())
                .stream()
                .map(i -> i.getFirendsMessageUserId())
                .map(id -> messageUserRepository.findById(id))
                .filter(item -> item.isPresent())
                .map(item -> item.get())
                .map(item -> item.getId()).collect(Collectors.toList());

        peopleIds.addAll(collect);
        return list(page, size, peopleIds);
    }
}
