package com.whw.footstones.controller;import com.whw.footstones.config.redis.RedisService;import com.whw.footstones.core.message.OutputMessage;import io.swagger.annotations.Api;import io.swagger.annotations.ApiOperation;import lombok.extern.slf4j.Slf4j;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.web.bind.annotation.GetMapping;import org.springframework.web.bind.annotation.RestController;/** * @version 1.0 * @description: TODO * @date 2021/9/15 7:32 PM */@RestController@Slf4j@Api(tags = {"redis"})public class RedisTestController {    @Autowired    RedisService redisService;    @GetMapping(value = "/getString")    @ApiOperation(value = "测试redis", tags = "redis")    public OutputMessage<String> setRedis() {        redisService.set("test", "key");        return OutputMessage.success("suc");    }}