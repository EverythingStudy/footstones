package com.whw.footstones.controller;import com.whw.footstones.core.message.OutputMessage;import com.whw.footstones.service.SseEmitterServer;import org.springframework.web.bind.annotation.*;import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;/** * @author cly * @version 1.0 * @description: TODO * @date 10/24/23 1:48 PM */@RestController@CrossOrigin(maxAge = 3600)public class SSEController {    @RequestMapping(value = "/sse/connect/{id}", method = RequestMethod.GET)    public SseEmitter connect(@PathVariable Integer id) {        SseEmitter sseEmitter = SseEmitterServer.connect(id);        return sseEmitter;    }    /**     * 向指定用户发送消息     */    @RequestMapping(value = "/sse/send/{id}", method = RequestMethod.GET)    public OutputMessage sendMsg(@PathVariable Integer id, @RequestParam("message") String message) {        SseEmitterServer.sendMessage(id, message);        return OutputMessage.success("向" + id + "号用户发送信息，" + message + "，消息发送成功");    }    /**     * 向所有用户发送消息     */    @RequestMapping(value = "/sse/send/all", method = RequestMethod.GET)    public OutputMessage sendMsg2AllUser(@RequestParam("message") String message) {        SseEmitterServer.batchSendMessage(message);        return OutputMessage.success("向所有用户发送信息，" + message + "，消息发送成功");    }    /**     * 关闭用户连接     */    @RequestMapping(value = "/sse/close/{id}", method = RequestMethod.GET)    public OutputMessage closeSse(@PathVariable Integer id) {        SseEmitterServer.removeUser(id);        return OutputMessage.success("关闭" + id + "号连接。当前连接用户有：" + SseEmitterServer.getIds());    }}