/*
 * Copyright (c) 8/3/18 10:59 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller.forum;

import com.gradle.controller.base.AbstractBaseController;
import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.forms.forum.NewSectionForm;
import com.gradle.entity.forum.Post;
import com.gradle.entity.forum.Section;
import com.gradle.entity.forum.Topic;
import com.gradle.entity.user.User;
import com.gradle.services.iface.forum.PostService;
import com.gradle.services.iface.forum.SectionService;
import com.gradle.services.iface.forum.TopicService;
import com.gradle.util.Paging;
import com.gradle.util.adminConfig.AdminConfigUtil;
import com.gradle.util.constants.ConstantProperties;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/forum")
@Controller
public class ForumController extends AbstractBaseController {


    @Autowired
    private SectionService sectionService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private PostService postService;


    /**
     *
     * @param topicPage
     * @param model
     * @return
     */
    @GetMapping(value = {"/", "/home", "/home/{topicPage}"})
    public String home(
            @PathVariable Optional<String> topicPage,
            Model model) {

        Integer recordPerPage = serviceUtil.getRecordPerPage(new Topic());
        Integer topicPageNo = 1, postPageNo=1;
        if (topicPage.isPresent()){
            topicPageNo = Integer.parseInt(topicPage.get());
        }
        model.addAttribute("sections", sectionService.findAll());
        model.addAttribute("topics", topicService.findPaginated(topicPageNo, recordPerPage,new Topic(),""));
        model.addAttribute("posts", postService.findPaginated(postPageNo, recordPerPage, new Post(),""));
        model.addAttribute("pagging", new Paging(topicPageNo.longValue(), topicService.count(new Topic()), recordPerPage.longValue()));
        return "forum/home";
    }


    /**
     *
     * @param type Id of post to be deleted in encrypted mode
     * @param authentication
     * @param model
     * @return
     */
    @RequestMapping(value = "/admin/post/delete/{type}", method = RequestMethod.GET)
    public String delete(@PathVariable String type,
                         Authentication authentication,
                         RedirectAttributes model) {
        Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
        Post post = postService.find(id);
        if (post == null || authentication == null || authentication.getName() == null
                || serviceUtil.getCurrentUser().getId() != post.getUser().getId()) {
            alerts.setError("post.not.authorize.delete");
            alerts.setAlertRedirectAttribute(model);
            alerts.clearAlert();
            return "redirect:/forum/home";
        }
        int topicId = post.getTopic().getId();
        try {
            postService.delete(post);
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        alerts.setSuccess("post.successfully.deleted");
        alerts.setAlertRedirectAttribute(model);
        alerts.clearAlert();
        return "redirect:/forum/topic/" + pathVariableEncrypt.encrypt(Integer.toString(topicId));
    }


    /**
     *
     * @param type
     * @param model
     * @return
     */
    @GetMapping("/section/{type}")
    public String getTopicsFromSection(@PathVariable String type,
                                       Model model) {
        Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
        model.addAttribute("section", sectionService.find(id));
        String query = "from Topic where section_id = ?";
        Object[] params = new Object[1];
        params[0] = id;
        model.addAttribute("topics", topicService.queryWithParameter(query, params));
        return "forum/section";
    }

    /**
     *
     * @param model
     * @return
     * @implNote
     */
    @PreAuthorize("hasRole('ADMIN')") // this code of line will check if user has Admin role access else will redirect back
    @RequestMapping(value = "/section/new", method = RequestMethod.GET)
    public String getNewSectionForm(Model model) {
        model.addAttribute("newSection", new NewSectionForm());
        return "forum/new_section_form";
    }

    /**
     *
     * @param newSection
     * @param result
     * @return
     */
    @RequestMapping(value = "/section/new", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")// this code of line will check if user has Admin role access else will redirect back
    public String processAndAddNewSection(
            @Valid @ModelAttribute("newSection") NewSectionForm newSection,
            BindingResult result) {

        if (result.hasErrors()) {
            return "forum/new_section_form";
        }

        Section section = new Section();
        section.setName(newSection.getName());
        section.setDescription(newSection.getDescription());
        sectionService.saveOrUpdate(section);
        return "redirect:/forum/section/" + pathVariableEncrypt.encrypt(Integer.toString(section.getId()));
    }

    /**
     *
     * @param type Section id in encrypted mode
     * @param authentication
     * @param model
     * @return
     */
    @RequestMapping(value = "/admin/section/delete/{type}", method = RequestMethod.GET)
    public String deleteSection(@PathVariable String type,
                                Authentication authentication,
                                RedirectAttributes model) {
        Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
        User user = serviceUtil.getUserFromUsernameOrEmail(authentication.getName());
        Section section = sectionService.find(id);
        sectionService.delete(section);
        model.addFlashAttribute("message", "section.successfully.deleted");
        return "redirect:/forum/home";
    }

    @Override
    public AdminConfig getAdminConfig() {
        return null;
    }
}
