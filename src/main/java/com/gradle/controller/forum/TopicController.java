/*
 * Copyright (c) 8/3/18 11:06 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller.forum;

import com.gradle.controller.base.AbstractBaseController;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.forms.forum.NewPostForm;
import com.gradle.entity.forms.forum.NewTopicForm;
import com.gradle.entity.forum.Post;
import com.gradle.entity.forum.Topic;
import com.gradle.services.iface.forum.PostService;
import com.gradle.services.iface.forum.SectionService;
import com.gradle.services.iface.forum.TopicService;
import com.gradle.services.iface.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import javax.validation.Valid;

@RequestMapping(value = "/forum/topic")
@Controller
public class TopicController extends AbstractBaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;

    /**
     *
     * @param type
     * @param model
     * @return
     */
    @RequestMapping(value = "/{type}", method = RequestMethod.GET)
    public String getTopicById(@PathVariable String type,
                               Model model) {
        Integer idTopic= Integer.parseInt(pathVariableEncrypt.decrypt(type));
        Topic topic = topicService.find(idTopic);
        topic.setViews(topic.getViews() + 1);
        //topicService.saveOrUpdate(topic);

        model.addAttribute("topic", topic);
        String query = " from Post where topic_id = ?";
        Object[] params = new Object[1];
        params[0] = idTopic;
        model.addAttribute("posts", postService.queryWithParameter(query, params));
        model.addAttribute("newPost", new NewPostForm());
        return "forum/topic";
    }

    /**
     *
     * @param newPost New post form object
     * @param result              Binding results
     * @param authentication Authentication object (Spring)
     * @param idTopic Topic id under which post is being created
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/{idTopic}", method = RequestMethod.POST)
    public String addPost(@Valid  @ModelAttribute("newPost") NewPostForm newPost,
                          BindingResult result,
                          Authentication authentication,
                          @PathVariable int idTopic,
                          ModelMap model,
                          RedirectAttributes redirectAttributes
                          ) {

        if (result.hasErrors()) {
            model.addAttribute("topic", topicService.find(idTopic));
            alerts.setError("post.save.error");
            alerts.setAlertModelAttribute(model);
            alerts.clearAlert();
            //model.addAttribute("posts", postService.findByTopic(idTopic));
            return "forum/topic";
        }
        String query = " from Post where topic_id = ?";
        Object[] params = new Object[1];
        params[0] = idTopic;
        Post post = new Post();
        post.setContent(newPost.getContent());
        post.setTopic(topicService.find(idTopic));
        post.setUser(serviceUtil.getCurrentUser());
        //post.setUser(userService.findByUsername(authentication.getName()));
        model.addAttribute("posts", postService.queryWithParameter(query, params));
        alerts.setSuccess("post.save.success");
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        postService.saveOrUpdate(post);

        // model.asMap().clear();
        return "redirect:/forum/topic/" + pathVariableEncrypt.encrypt(Integer.toString(idTopic));
    }


    /**
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String getNewTopictForm(ModelMap model) {
        model.addAttribute("newTopic", new NewTopicForm());
        model.addAttribute("sections", sectionService.findAll());
        return "forum/new_topic_form";
    }


    /**
     *
     * @param newTopic New topic form
     * @param result              Binding results
     * @param authentication Authentication object (Spring)
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String processAndAddNewTopic(@Valid @ModelAttribute("newTopic") NewTopicForm newTopic,
                                        BindingResult result,
                                        Authentication authentication,
                                        ModelMap model,
                                        RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("sections", sectionService.findAll());
            alerts.setError("topic.save.error");
            alerts.setAlertModelAttribute(model);
            alerts.clearAlert();
            return "forum/new_topic_form";
        }

        Topic topic = new Topic();
        //topic.setUser(userService.findByUsername(authentication.getName()));
        topic.setTitle(newTopic.getTitle());
        topic.setContent(newTopic.getContent());
        topic.setSection(sectionService.find(newTopic.getSectionId()));
        topic.setUser(serviceUtil.getCurrentUser());
        alerts.setSuccess("topic.save.success");
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        topicService.saveOrUpdate(topic);

        return "redirect:/forum/home/";
    }

    /**
     *
     * @param type Encrypted topic id
     * @param authentication
     * @param redirectAttributes
     * @param model
     * @return
     */
    @RequestMapping(value = "/admin/topic/delete/{type}", method = RequestMethod.GET)
    public String delete(@PathVariable String type,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes,
                         ModelMap model
                         ) {
        Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
        Topic topic = topicService.find(id);

        if (topic == null) {
            return "redirect:/";
        }
        int sectionId = topic.getSection().getId();
        if (topic.getUser().getId() != serviceUtil.getCurrentUser().getId()) {
            alerts.setError("topic.authorize.invalid");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            return "redirect:/forum/topic/" + type;
        }
        topicService.delete(topic);
        alerts.setSuccess("topic.successfully.deleted");
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        return "redirect:/forum/home";
    }

    @Override
    public AdminConfig getAdminConfig() {
        return null;
    }
}
