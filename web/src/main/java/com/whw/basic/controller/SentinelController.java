package com.whw.basic.controller;import com.alibaba.csp.sentinel.annotation.SentinelResource;import com.alibaba.csp.sentinel.slots.block.BlockException;import com.whw.core.message.OutputMessage;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.RestController;/** * @version 1.0 * @description: TODO * @date 2021/7/13 4:51 PM */@RestControllerpublic class SentinelController {    @SentinelResource(value = "sentinel", blockHandler = "sentinelBlock")    @RequestMapping(value = "test")    public void sentinel(String name) {        System.out.println(name);    }    public String sentinelBlock(String name, BlockException e) {        System.out.println(e.getMessage());        System.out.println("限流==" + name);        return "";    }}