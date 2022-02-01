/*
 * Copyright (c) 25/4/18 11:57 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller.user;

import com.gradle.controller.base.AbstractBaseController;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.forms.AjaxCommonForm;
import com.gradle.entity.forms.user.SubUserForm;
import com.gradle.entity.user.ReportedUser;
import com.gradle.entity.user.Role;
import com.gradle.entity.user.User;
import com.gradle.events.event.OnSubUserCreateCompleteEvent;
import com.gradle.util.Paging;
import com.gradle.util.adminConfig.AdminConfigUtil;
import com.gradle.util.constants.ConstantProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.*;

@Controller
@RequestMapping(value = "/subuser")
public class SubUserController extends AbstractBaseController {

    @Autowired
    private Validator validator;

    @Override
    public AdminConfig getAdminConfig() {
        return null;
    }


    @GetMapping(value = {"/list", "/list/{pNo}", "/list/{page}/{maxCount}"})
    public String subUsersList(@PathVariable Optional<Integer> page, @PathVariable Optional<Integer> maxCount, ModelMap model, RedirectAttributes redirectAttributes) {
        List<User> subUsersList = null;
        User currentUser = serviceUtil.getCurrentUser();
        try {
            Integer pageNo = 1;
            if (page.isPresent()) {
                pageNo = page.get();
            }

            /*AdminConfigUtil<User> adminConfigUtil = new AdminConfigUtil<User>();
            AdminConfig adminConfig = adminConfigUtil.getAdminConfig(serviceUtil, new User());
            Integer recordPerPage = (adminConfig != null && adminConfig.getRecordPerPage() > 0) ? adminConfig.getRecordPerPage() : ConstantProperties.PAGING_MAX_PER_PAGE.intValue();*/
            Integer recordPerPage = (!maxCount.isPresent()) ? serviceUtil.getRecordPerPage(new ReportedUser()) : maxCount.get();
            String query = "from User where parent_id=? order by id DESC";
            Object[] params = new Object[1];
            params[0] = currentUser.getId();
            subUsersList = userService.findPaginatedByUserWithOrder(pageNo, recordPerPage, new User(), currentUser, "parent_id","id", "DESC");
            Long count = userService.countQuery("select count(*) from User where parent_id=?", params);
            model.addAttribute("pagging", new Paging(pageNo.longValue(), ((count == null) ? 0 : count), recordPerPage.longValue()));
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        model.addAttribute("subUsers", subUsersList);
        model.addAttribute("user", currentUser);
        return "user/subuser/list";
    }


    @GetMapping(value = {"/create"})
    public String subUsersForm(ModelMap model, RedirectAttributes redirectAttributes) {
        User currentUser = serviceUtil.getCurrentUser();
        try {

        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        model.addAttribute("user", new User());
        return "user/subuser/form";
    }

    @PostMapping(value = {"/create"})
    public String subUsersSubmit(@ModelAttribute User user, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            String password = UUID.randomUUID().toString();
            User currentUser = serviceUtil.getCurrentUser();
            String email = user.getEmail();
            String username = user.getUsername();
            String name = user.getFirstName();

            user.setId(null);
            user.setFirstName(name);
            user.setId(0);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setConfirmPassword(password);
            user.setCountry(currentUser.getCountry());
            user.setCurrency(currentUser.getCurrency());
            user.setZone(currentUser.getZone());
            user.setPhone(currentUser.getPhone());


            Set<ConstraintViolation<User>> violations = validator.validate(user);
            if (violations.size() > 0) {
                for (ConstraintViolation<User> violation : violations) {
                    result.rejectValue(violation.getPropertyPath().toString(), "", violation.getMessage());
                }
                alerts.setError("User.save.error");
                alerts.setAlertModelAttribute(model);
                alerts.clearAlert();
            } else {
                Object[] params = new Object[1];
                params[0] = ConstantProperties.ROLE_SUB_USER;
                Role role = roleService.first(" from Role where role_name=?", params);
                List<Role> roles = new ArrayList<Role>();
                roles.add(role);
                user.setRoles(roles);
                user.setParent(currentUser);
                user.setPassword(passwordEncoder.encode(password));
                userService.save(user);
                eventPublisher.publishEvent(new OnSubUserCreateCompleteEvent(user, localeHelper.getCurrentLocale(), request.getContextPath(), password));
                alerts.setSuccess("User.save.success");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
                return "redirect:/subuser/list";
            }

        } catch (Exception e) {
            alerts.setError("User.save.error");
            alerts.setAlertModelAttribute(model);
            alerts.clearAlert();
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return "user/subuser/form";
    }


    @PostMapping(value = {"/edit"})
    @ResponseBody
    public AjaxCommonForm subUsersEdit(@Valid @ModelAttribute("SubUserForm") SubUserForm form, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        AjaxCommonForm ajaxCommonForm = new AjaxCommonForm();
        ajaxCommonForm.setSuccess(false);
        try {
            ajaxCommonForm.setMsg(localeHelper.getApplicationPropertiesText("General.required.fields.missing",null,"Required fields missing"));
            if (result.hasErrors()) {
                StringBuilder str = new StringBuilder("<br/><br/>");
                List<FieldError> errs = result.getFieldErrors();
                for (FieldError err : errs){
                    str.append(err.getField() + " : " + err.getDefaultMessage() + "<br/>");
                }
                ajaxCommonForm.setMsg(localeHelper.getApplicationPropertiesText("General.required.fields.missing",null,"Required fields missing")+ str.toString());

            } else {
                User editUser = userService.find(Integer.parseInt(pathVariableEncrypt.decrypt(form.getId())));
                User currentUser = serviceUtil.getCurrentUser();
                if (editUser != null) {
                    if (currentUser.getId() != editUser.getParent().getId()) {
                        ajaxCommonForm.setMsg(localeHelper.getApplicationPropertiesText("Genera.unauthorised.access",null,"You are not authorized to perform this action"));
                    } else {
                        editUser.setEmail(form.getEmail());
                        editUser.setFirstName(form.getName());
                        editUser.setUsername(form.getUsername());
                        editUser.setEnabled(form.isEnabled());

                        if (form.getPassword() != null && !form.getPassword().trim().equalsIgnoreCase("")) {
                            editUser.setPassword(passwordEncoder.encode(form.getPassword()));
                        }
                        userService.saveOrUpdate(editUser);
                        ajaxCommonForm.setMsg(localeHelper.getApplicationPropertiesText("User.save.success",null,"User saved"));
                        ajaxCommonForm.setSuccess(true);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return ajaxCommonForm;
    }

}
