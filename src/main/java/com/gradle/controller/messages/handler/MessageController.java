/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller.messages.handler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/message")
public class MessageController {

    /**
     *
     * @return
     */
    @RequestMapping(value = "/showForm", method = RequestMethod.GET)
    public String showForm() {
        return "message";
    }

    /**
     *
     * @param message
     * @param model
     * @return
     */
    @RequestMapping(value = "/processForm", method = RequestMethod.POST)
    public String processForm(@RequestParam("message") final String message, final Model model) {
        model.addAttribute("message", message);
        return "message";
    }

}
