package org.flyants.chat.web.v1.openapi;

import lombok.extern.slf4j.Slf4j;
import org.flyants.chat.domain.service.AuthorizeService;
import org.flyants.chat.domain.service.PeopleService;
import org.flyants.chat.domain.entity.platform.People;
import org.flyants.chat.utils.ResponseDataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @Author zhangchao
 * @Date 2019/4/26 13:46
 * @Version v1.0
 */
@RestController
@RequestMapping("/api/v1.0/people/info")
@Slf4j
public class PeopleController {

    @Autowired
    AuthorizeService authorizeService;

    @Autowired
    PeopleService peopleService;

    @GetMapping
    public Object info(@RequestHeader("access_token") @RequestParam("access_token") String accessToken, @RequestParam("openId")String openId) {
        if (!authorizeService.checkAccessToken(accessToken)) {
            return ResponseDataUtils.buildError("无效的 access_token");
        }
        Optional<String> peopleId = authorizeService.findPeopleIdByOpenId(accessToken, openId);
        if (!peopleId.isPresent()) {
            return ResponseDataUtils.buildError("openId 错误");
        }
        Optional<People> optional = peopleService.findPeopleById(peopleId.get());
        return ResponseDataUtils.buildSuccess(optional.get());
    }
}