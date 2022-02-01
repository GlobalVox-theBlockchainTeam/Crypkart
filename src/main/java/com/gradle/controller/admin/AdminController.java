/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller.admin;

import com.fasterxml.classmate.Annotations;
import com.gradle.controller.base.AbstractBaseController;
import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.advertisement.PaymentType;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.configurations.AdminConfigValues;
import com.gradle.entity.forms.util.PagingAjaxForm;
import com.gradle.entity.frontend.CMS;
import com.gradle.entity.user.Role;
import com.gradle.entity.user.User;
import com.gradle.services.iface.CMSService;
import com.gradle.services.iface.bitcoin.CurrencyService;
import com.gradle.util.Paging;
import com.gradle.util.adminConfig.AdminConfigUtil;
import com.gradle.util.constants.ConstantProperties;
import com.gradle.util.export.WriteCsvToResponse;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static jdk.nashorn.internal.runtime.regexp.joni.Syntax.Java;


/**
 * Controller for admin panel
 */

@Controller
@RequestMapping(value = "/admin")
public class AdminController extends AbstractBaseController {

    @Autowired
    private CMSService cmsService;

    @Autowired
    private Validator validator;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private SimpMessagingTemplate webSocket;

    private static final String templateBase = "admin/";

    final NetworkParameters netParams = TestNet3Params.get();


    /*@InitBinder("user")
    protected void initBinder(HttpServletRequest request, WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "roles", new SampleEditor(roleService, List.class));
    }*/


    /**
     * @param model ModelMap
     * @return
     */
    @RequestMapping(value = "/user/form", method = RequestMethod.GET)
    public String adminUserForm(ModelMap model) {
        model.addAttribute("user", new User());
        List<Role> rolesList = roleService.findAll();
        model.addAttribute("rolesList", rolesList);
        return templateBase + "user/create";
    }


    /**
     * @param user               : User object
     * @param result             : Result binded with User object after validation
     * @param model              : ModelMap
     * @param redirectAttributes : Redirect Attributes
     * @param request            : HttpServletRequest
     * @return
     */
    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    public String adminCreateUser(@ModelAttribute("user") User user, BindingResult result,
                                  ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        User dbUser = userService.find(user.getId());
        user.setCountry(dbUser.getCountry());
        user.setZone(dbUser.getZone());
        String ret = "redirect:/" + templateBase + "user/list";
        //validator = Validation.buildDefaultValidatorFactory().getValidator();
        {

            if (dbUser != null) {
                dbUser.setFirstName(user.getFirstName());
                dbUser.setLastName(user.getLastName());
                dbUser.setUsername(user.getUsername());
                dbUser.setEmail(user.getEmail());
                dbUser.setRoles(user.getRoles());
                dbUser.setPhone(user.getPhone());
                dbUser.setEnabled(user.isEnabled());
                dbUser.setPhoneVerified(user.isPhoneVerified());
                dbUser.setTrusted(user.isTrusted());
                dbUser.setCurrency(user.getCurrency());
                dbUser.setSellingVacation(user.isSellingVacation());
                dbUser.setBuyingVacation(user.isBuyingVacation());
                dbUser.setAccountDeleted(user.isAccountDeleted());
                dbUser.setEscrowSms(user.isEscrowSms());
                dbUser.setSendNewTradeSms(user.isSendNewTradeSms());
                dbUser.setEnableWebNotification(user.isEnableWebNotification());
                dbUser.setDisableSensitiveInformationFromEmail(user.isDisableSensitiveInformationFromEmail());
                Set<ConstraintViolation<User>> violations = validator.validate(dbUser);
                if (violations.size() <= 0) {
                    userService.saveOrUpdate(dbUser);
                    alerts.setSuccess("User.save.success");
                    alerts.setAlertModelAttribute(model);
                    alerts.clearAlert();
                } else {

                    for (ConstraintViolation violation : violations) {
                        result.rejectValue(violation.getPropertyPath().toString(), "", violation.getMessage());
                    }
                    alerts.setError("General.error.msg");
                    alerts.setAlertRedirectAttribute(redirectAttributes);
                    alerts.setAlertModelAttribute(model);
                    alerts.clearAlert();
                    //return "redirect:/user/settings";
                }
                userService.saveOrUpdate(dbUser);
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userService.saveOrUpdate(user);
            }

            alerts.setSuccess("User saved");
        }
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<Role>());
        }
        alerts.setAlertModelAttribute(model);
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        List<Role> rolesList = roleService.findAll();
        model.addAttribute("rolesList", rolesList);
        List<User> userList = userService.findAll();
        model.addAttribute("users", userList);
        model.addAttribute("currencies", currencyService.findAll());
        return ret;
    }

    /**
     * @param id    : User id to be edited
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = "/user/edit/form/{type}", method = RequestMethod.GET)
    public String adminUserEditForm(@PathVariable("type") int id, ModelMap model) {
        User user = userService.find(id);
        model.addAttribute("user", user);
        List<Role> rolesList = roleService.findAll();
        model.addAttribute("rolesList", rolesList);
        model.addAttribute("currencies", currencyService.findAll());
        return templateBase + "user/create";
    }


    /**
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/user/edit/password/{type}", method = RequestMethod.GET)
    public String adminUserPasswordEditForm(@PathVariable("type") int id, ModelMap model) {
        User user = userService.find(id);
        model.addAttribute("user", user);
        List<Role> rolesList = roleService.findAll();
        model.addAttribute("rolesList", rolesList);
        return templateBase + "user/password";
    }

    /**
     * @param id                 : user id to be deleted
     * @param model              : ModelMap
     * @param redirectAttributes :
     * @return
     */
    @RequestMapping(value = "/user/delete/{type}", method = RequestMethod.GET)
    public String adminUserList(@PathVariable("type") int id, ModelMap model, RedirectAttributes redirectAttributes) {
        String ret = "redirect:/" + templateBase + "user/list";
        User user = userService.find(id);
        try {

            userService.delete(user);
            alerts.setSuccess("User deleted");
        } catch (Exception e) {
            alerts.setError("Error saving. Try again later");
        }
        List<User> userList = userService.findAll();
        model.addAttribute("users", userList);
        alerts.setAlertModelAttribute(model);
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        return ret;
    }


    /**
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = {"/user/status/{type}"},
            method = RequestMethod.GET)
    public String adminUserStatusChange(
            @PathVariable Optional<String> type,
            ModelMap model, RedirectAttributes redirectAttributes, Pageable pageable,
            HttpServletResponse response
    ) {
        try {
            Integer id = 0;
            Integer pageNo = 1;
            if (type.isPresent()) {
                id = Integer.parseInt(pathVariableEncrypt.decrypt(type.get()));
                User user = userService.find(id);
                user.setEnabled(!user.isEnabled());
                userService.saveOrUpdate(user);
                String[] args = new String[2];
                args[1] = (user.isEnabled()) ? "Enabled" : "Disabled";
                args[0] = user.getUsername();
                alerts.setSuccess("User.status.changed", args);

            } else {
                alerts.setError("General.error.msg");
                return "redirect:/admin/user/list";
            }
        }catch (Exception e){
            alerts.setError("General.error.msg");
        }
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.setAlertModelAttribute(model);
        alerts.clearAlert();
        /*List<User> userList = userService.findPaginated(pageNo, ConstantProperties.PAGING_MAX_PER_PAGE.intValue(), new User(), null);
        //userList = userService.findAll();
        Long count = userService.count(new User());
        model.addAttribute("pagging", new Paging(pageNo.longValue(), count));
        model.addAttribute("users", userList);*/
        return "redirect:/admin/user/list";
    }


    /**
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = {"/home","/user/list", "/user/list/{page}", "/user/list/{page}/{maxCount}"},
            method = RequestMethod.GET)
    public String adminUserList(
            @PathVariable Optional<Integer> page, @PathVariable Optional<Integer> maxCount,
            @RequestParam Optional<String> search,
            ModelMap model, RedirectAttributes redirectAttributes, Pageable pageable,
            HttpServletResponse response
    ) {
        Integer pageNo = 1;
        if (page.isPresent()) {
            pageNo = page.get();
        }

        List<User> userList = userService.findPaginated(pageNo, ConstantProperties.PAGING_MAX_PER_PAGE.intValue(), new User(), null);
        //userList = userService.findAll();
        Long count = userService.count(new User());
        model.addAttribute("pagging", new Paging(pageNo.longValue(), count));
        model.addAttribute("users", userList);
        return templateBase + "user/list";
    }


    /**
     * **********************************************************************************
     * Payment Type management
     * **********************************************************************************
     */


    /**
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = "/pay/form", method = RequestMethod.GET)
    public String adminPayForm(ModelMap model) {
        model.addAttribute("paymentType", new PaymentType());
        return templateBase + "pay/create";
    }


    /**
     * @param paymentType         : Payment Method
     * @param result              : Validation result
     * @param model               : ModelMap
     * @param redirectAttributes:
     * @param request             : HttpServletRequest
     * @return
     */
    @RequestMapping(value = "/pay/create", method = RequestMethod.POST)
    public String adminCreatePay(@ModelAttribute("paymentType") @Valid PaymentType paymentType, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String ret = templateBase + "pay/list";

        // if validation failed redirect back with flash message
        if (result.hasErrors()) {
            alerts.setError("Error.saving.pay");
            ret = templateBase + "pay/create";
        } else { // if no validation error save payment method
            alerts.setSuccess("Success.saving.pay");
            paymentTypeService.saveOrUpdate(paymentType);
            ret = "redirect:/admin/pay/list";
        }
        alerts.setAlertModelAttribute(model);
        alerts.clearAlert();
        List<PaymentType> paymentTypeList = paymentTypeService.findAll();
        model.addAttribute("paymentTypes", paymentTypeList);

        return ret;
    }

    /**
     * @param model : ModelMAp
     * @return
     */
    @RequestMapping(value = {"/pay/list", "/pay/list/{page}", "/pay/list/{page}/{maxCount}"}, method = RequestMethod.GET)
    public String adminPayList(@PathVariable Optional<Integer> page, @PathVariable Optional<Integer> maxCount,
                               @RequestParam Optional<String> search,
                               ModelMap model) {

        Integer pageNo = 1;
        AdminConfigUtil<PaymentType> adminConfigUtil = new AdminConfigUtil<PaymentType>();
        AdminConfig adminConfig = adminConfigUtil.getAdminConfig(serviceUtil, new PaymentType());
        Integer recordPerPage = (adminConfig != null && adminConfig.getRecordPerPage() > 0) ? adminConfig.getRecordPerPage() : ConstantProperties.PAGING_MAX_PER_PAGE.intValue();

        if (page.isPresent()) {
            pageNo = page.get();
        }
        List<PaymentType> paymentTypeList = paymentTypeService.findPaginated(pageNo, recordPerPage, new PaymentType(), (search.isPresent() ? search.get() : null));
        Long count = paymentTypeService.count(new PaymentType());
        model.addAttribute("paymentTypes", paymentTypeList);
        model.addAttribute("pagging", new Paging(pageNo.longValue(), count, recordPerPage.longValue()));
        return templateBase + "pay/list";
    }


    /**
     * @param id    : Payment id to be edited
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = "/pay/edit/form/{type}", method = RequestMethod.GET)
    public String adminPayEditForm(@PathVariable("type") int id, ModelMap model) {
        PaymentType paymentType = paymentTypeService.find(id);
        model.addAttribute("paymentType", paymentType);
        return templateBase + "pay/create";
    }

    /**
     * @param id    : Payment id to be deleted
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = "/pay/delete/{type}", method = RequestMethod.GET)
    public String adminPayList(@PathVariable("type") int id, ModelMap model) {
        PaymentType paymentType = paymentTypeService.find(id);
        String ret = templateBase + "pay/list";
        try {
            paymentTypeService.delete(paymentType);
            alerts.setSuccess("Payment Deleted");
            ret = "redirect:/admin/pay/list";
        } catch (Exception e) {
            alerts.setError("Error saving. Try again later");
        }
        List<PaymentType> paymentTypes = paymentTypeService.findAll();
        model.addAttribute("paymentTypes", paymentTypes);
        alerts.setAlertModelAttribute(model);
        alerts.clearAlert();
        return ret;
    }


    /**
     * **********************************************************************************
     * CMS Page management
     * **********************************************************************************
     */

    /**
     * @param page               Pagging page number
     * @param maxCount           Pagging max count per page
     * @param search             Search string
     * @param model
     * @param redirectAttributes
     * @param pageable
     * @param response
     * @return
     */
    @RequestMapping(value = {"/cms/list/{page}/{maxCount}", "/cms/list", "/cms/list/{page}"})
    public String adminCmsList(@PathVariable Optional<Integer> page, @PathVariable Optional<Integer> maxCount,
                               @RequestParam Optional<String> search,
                               ModelMap model, RedirectAttributes redirectAttributes, Pageable pageable,
                               HttpServletResponse response
    ) {

        User user = serviceUtil.getCurrentUser();
        Integer pageNo = 1;

        if (page.isPresent()) {
            pageNo = page.get();
        }


        List<CMS> cmsList = cmsService.findPaginated(pageNo, ConstantProperties.PAGING_MAX_PER_PAGE.intValue(), new CMS(), (search.isPresent() ? search.get() : null));
        Long count = cmsService.count(new CMS());
        model.addAttribute("cmsList", cmsList);
        model.addAttribute("user", user);
        model.addAttribute("pagging", new Paging(pageNo.longValue(), count));


        return templateBase + "cms/list";

    }


    /**
     * @param model              : ModelMap
     * @param redirectAttributes : -
     * @param response           - Will write csv file as response
     */
    @GetMapping(value = "/cms/download/csv", produces = "text/csv")
    @ResponseBody
    public void cmsDownload(ModelMap model, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        List<CMS> cmsList = cmsService.findAll();
        try {
            WriteCsvToResponse<CMS> csvWriter = new WriteCsvToResponse<CMS>();
            String[] columns = new String[2];
            columns[0] = "id";
            columns[1] = "content";
            csvWriter.writeCsv(response, cmsList, columns);
        } catch (IOException e) {

        }
    }

    /**
     * @param model              : ModelMap
     * @param redirectAttributes : -
     * @param response           - Will write csv file as response
     */
    @GetMapping(value = "/user/download/csv", produces = "text/csv")
    @ResponseBody
    public void userDownload(ModelMap model, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        List<User> cmsList = userService.findAll();
        try {
            WriteCsvToResponse<User> csvWriter = new WriteCsvToResponse<User>();
            String[] columns = new String[2];
            columns[0] = "username";
            columns[1] = "email";


            csvWriter.writeCsv(response, cmsList, columns);
        } catch (IOException e) {

        }
    }


    /**
     * @param id    : Cms id to be edited
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = "/cms/edit/form/{type}", method = RequestMethod.GET)
    public String adminCMSEditForm(@PathVariable("type") int id, ModelMap model) {
        CMS cms = cmsService.find(id);
        model.addAttribute("cms", cms);
        return templateBase + "cms/create";
    }


    /**
     * @param id    : Cms id to be deleted
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = "/cms/delete/{type}", method = RequestMethod.GET)
    public String adminCMSDelete(@PathVariable("type") int id, ModelMap model, RedirectAttributes redirectAttributes) {
        CMS cms = cmsService.find(id);
        try {
            cmsService.delete(cms);
            alerts.setSuccess("Success.delete.cms");
        } catch (Exception e) {
            logger.error(e.getMessage());
            alerts.setError("Error.delete.cms");
        }
        List<CMS> cmsList = cmsService.findAll();
        model.addAttribute("cmsList", cmsList);
        alerts.setAlertModelAttribute(model);
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        //return templateBase + "cms/list";
        return "redirect:/admin/cms/list/1";
    }

    /**
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = "/cms/form", method = RequestMethod.GET)
    public String adminCMSForm(ModelMap model) {
        model.addAttribute("cms", new CMS());
        return templateBase + "cms/create";
    }


    /**
     * @param cms                 : CMS Page
     * @param result              : Validation result
     * @param model               : ModelMap
     * @param redirectAttributes:
     * @param request             : HttpServletRequest
     * @return
     */
    @RequestMapping(value = "/cms/create", method = RequestMethod.POST)
    public String adminCreateCMS(@ModelAttribute("cms") @Valid CMS cms, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String ret = "redirect:/admin/cms/list/1";

        // if validation failed redirect back with flash message
        if (result.hasErrors()) {
            alerts.setError("Error.saving.cms");
            ret = templateBase + "cms/create";
        } else { // if no validation error save payment method
            cms.setUser(serviceUtil.getCurrentUser());
            alerts.setSuccess("Success.saving.cms");
            cmsService.saveOrUpdate(cms);
        }
        alerts.setAlertModelAttribute(model);
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();

        List<CMS> cmsList = cmsService.findAll();
        model.addAttribute("cmsList", cmsList);
        return ret;
    }







    @RequestMapping(value = "/config/ajax/paging", method = {RequestMethod.POST, RequestMethod.GET})
    public String  getAdminConfig(PagingAjaxForm paging, ModelMap model){
        try {
            model.addAttribute("adminConfigValuesList", adminConfigValuesService.findPaginated(paging.getPageNo(), paging.getMaxCount(), new AdminConfigValues(), ""));
            Long count = adminConfigValuesService.count(new AdminConfigValues());
            model.addAttribute("pagging", new Paging(paging.getPageNo().longValue(), count, paging.getMaxCount().longValue(), "/admin/config/ajax/paging", "adminConfigValues", new String[]{}));
            return templateBase + "config/configlist";
        }catch (Exception e){
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return "no data";
    }


    @RequestMapping(value = "/configa/ajax/paging", method = {RequestMethod.POST, RequestMethod.GET})
    public String  getAdminConfiga(PagingAjaxForm paging, ModelMap model){
        try {
            model.addAttribute("adminConfigList", adminConfigService.findPaginated(paging.getPageNo(), paging.getMaxCount(), new AdminConfig(), ""));
            Long count = adminConfigService.count(new AdminConfig());
            model.addAttribute("pagging", new Paging(paging.getPageNo().longValue(), count, paging.getMaxCount().longValue(), "/admin/configa/ajax/paging", "adminConfig", new String[]{}));
            return templateBase + "config/aconfiglist";
        }catch (Exception e){
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return "no data";
    }

    /**
     * **********************************************************************************
     * Config management
     * **********************************************************************************
     */


    /**
     * @param model              Model map opject
     * @param redirectAttributes RedirectAttributes Object
     * @return
     */
    @RequestMapping(value = {"/config/list", "/config/list/{page}", "/config/list/{page}/{maxCount}"})
    public String adminConfigList(
            @PathVariable Optional<Integer> type,
            @PathVariable Optional<Integer> page,
            @PathVariable Optional<Integer> maxCount,
            ModelMap model, RedirectAttributes redirectAttributes) {

        Integer pageNo = 1;
        if (page.isPresent()){
            pageNo = page.get();
        }
        Integer recordPerPage = (!maxCount.isPresent()) ? serviceUtil.getRecordPerPage(new AdminConfigValues()) : maxCount.get();
        List<AdminConfigValues> adminConfigValues = new ArrayList<AdminConfigValues>();

        List<AdminConfig> adminConfigs = userService.getConfig(User.class.getAnnotationsByType(Table.class)[0].name());
        if (adminConfigs != null && adminConfigs.size() > 0) {
            adminConfigValues = adminConfigService.getConfigValues(User.class.getAnnotationsByType(Table.class)[0].name());
        }

        User user = serviceUtil.getCurrentUser();
        List<AdminConfig> adminConfigList = adminConfigService.findPaginated(pageNo,recordPerPage, new AdminConfig(),"");
        Long adminConfigCount = adminConfigService.count(new AdminConfig());
        Integer recordPerPageAdminConfig = (!maxCount.isPresent()) ? serviceUtil.getRecordPerPage(new AdminConfig()) : maxCount.get();
        Long count = adminConfigValuesService.count(new AdminConfigValues());

        model.addAttribute("adminConfigList", adminConfigList);
        List<AdminConfigValues> adminConfigValuesList = adminConfigValuesService.findPaginated(pageNo,recordPerPage,new AdminConfigValues(),"");
        model.addAttribute("adminConfigValuesList", adminConfigValuesList);
        model.addAttribute("pagging", new Paging(pageNo.longValue(), count, recordPerPage.longValue(), "/admin/config/ajax/paging", "adminConfigValues", new String[]{}));
        model.addAttribute("pagging1", new Paging(pageNo.longValue(), adminConfigCount, recordPerPageAdminConfig.longValue(), "/admin/configa/ajax/paging", "adminConfig", new String[]{}));

        model.addAttribute("user", user);
        return templateBase + "config/list";
    }


    /**
     * @param id    : Config id to be edited
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = "/config/edit/form/{type}", method = RequestMethod.GET)
    public String adminConfigEditForm(@PathVariable("type") int id, ModelMap model) {
        AdminConfig adminConfig = adminConfigService.find(id);
        model.addAttribute("adminConfig", adminConfig);
        model.addAttribute("tables", serviceUtil.getAllTables());
        List<AdminConfigValues> adminConfigValuesList = adminConfigValuesService.findAll();
        model.addAttribute("adminConfigValuesList", adminConfigValuesList);
        return templateBase + "config/create";
    }


    /**
     * @param id    : Config id to be deleted
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = "/config/delete/{type}", method = RequestMethod.GET)
    public String adminConfigDelete(@PathVariable("type") int id, ModelMap model) {
        AdminConfig adminConfig = adminConfigService.find(id);
        try {
            adminConfigService.delete(adminConfig);
            alerts.setSuccess("Success.delete.config");
        } catch (Exception e) {
            logger.error(e.getMessage());
            alerts.setError("Error.delete.config");
        }
        List<AdminConfig> adminConfigList = adminConfigService.findAll();
        model.addAttribute("adminConfigList", adminConfigList);
        List<AdminConfigValues> adminConfigValuesList = adminConfigValuesService.findAll();
        model.addAttribute("adminConfigValuesList", adminConfigValuesList);
        alerts.setAlertModelAttribute(model);
        alerts.clearAlert();
        return templateBase + "config/list";
    }

    /**
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = "/config/form", method = RequestMethod.GET)
    public String adminConfigForm(ModelMap model) {
        List<Object> tables = serviceUtil.getAllTables();
        model.addAttribute("adminConfig", new AdminConfig());
        model.addAttribute("tables", tables);
        List<AdminConfigValues> adminConfigValuesList = adminConfigValuesService.findAll();
        model.addAttribute("adminConfigValuesList", adminConfigValuesList);
        return templateBase + "config/create";
    }


    /**
     * @param adminConfig         : Config
     * @param result              : Validation result
     * @param model               : ModelMap
     * @param redirectAttributes:
     * @param request             : HttpServletRequest
     * @return
     */
    @RequestMapping(value = "/config/create", method = RequestMethod.POST)
    public String adminCreateConfig(@ModelAttribute("adminConfig") @Valid AdminConfig adminConfig, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String ret = templateBase + "config/list";

        // if validation failed redirect back with flash message
        if (result.hasErrors()) {
            alerts.setError("Error.saving.config");
            ret = templateBase + "config/create";
        } else { // if no validation error save payment method
            adminConfig.setUser(serviceUtil.getCurrentUser());
            alerts.setSuccess("Success.saving.config");
            adminConfigService.saveOrUpdate(adminConfig);
        }
        alerts.setAlertModelAttribute(model);
        alerts.clearAlert();
        model.addAttribute("tables", serviceUtil.getAllTables());
        List<AdminConfigValues> adminConfigValuesList = adminConfigValuesService.findAll();
        model.addAttribute("adminConfigValuesList", adminConfigValuesList);
        List<AdminConfig> adminConfigList = adminConfigService.findAll();
        model.addAttribute("adminConfigList", adminConfigList);
        return ret;
    }

    /**
     * **********************************************************************************
     * Config Values management
     * **********************************************************************************
     */


    /**
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/config/value/list")
    public String adminConfigValuesList(ModelMap model, RedirectAttributes redirectAttributes) {

        User user = serviceUtil.getCurrentUser();
        List<AdminConfig> adminConfigList = adminConfigService.findAll();
        model.addAttribute("adminConfigList", adminConfigList);
        List<AdminConfigValues> adminConfigValuesList = adminConfigValuesService.findAll();
        model.addAttribute("adminConfigValuesList", adminConfigValuesList);
        model.addAttribute("user", user);
        return templateBase + "config/list";
    }


    /**
     * @param id    : Config id to be edited
     * @param model : ModelMap
     * @return
     *//*
    @RequestMapping(value = "/config/value/edit/form/{type}", method = RequestMethod.GET)
    public String adminConfigValueEditForm(@PathVariable("type") int id, AdminConfigValues adminConfigValues, ModelMap model) {
        AdminConfig adminConfig = adminConfigService.find(id);
        model.addAttribute("adminConfig", adminConfig);
        List<AdminConfigValues> adminConfigValuesList = adminConfigValuesService.findAll();
        model.addAttribute("adminConfigValuesList", adminConfigValuesList);
        model.addAttribute("adminConfigValues", adminConfigValues);

        return templateBase + "config/create_value";
    }*/


    /**
     * @param id    : Config id to be edited
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = "/config/value/edit/form/{type}", method = RequestMethod.GET)
    public String adminConfigValuesEditForm(@PathVariable("type") int id, ModelMap model) {
        AdminConfigValues adminConfigValues = adminConfigValuesService.find(id);
        List<AdminConfigValues> adminConfigValuesList = adminConfigValuesService.findAll();
        List<AdminConfig> adminConfigList = adminConfigService.findAll();
        model.addAttribute("adminConfigValuesList", adminConfigValuesList);
        model.addAttribute("adminConfigValues", adminConfigValues);
        model.addAttribute("adminConfigList", adminConfigList);

        return templateBase + "config/create_value";
    }


    /**
     * @param id    : Config id to be deleted
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = "/config/value/delete/{type}", method = RequestMethod.GET)
    public String adminConfigValueDelete(@PathVariable("type") int id, ModelMap model) {
        AdminConfigValues adminConfigValues = adminConfigValuesService.find(id);
        try {
            adminConfigValuesService.delete(adminConfigValues);
            alerts.setSuccess("Success.delete.config");
        } catch (Exception e) {
            logger.error(e.getMessage());
            alerts.setError("Error.delete.config");
        }
        List<AdminConfig> adminConfigList = adminConfigService.findAll();
        model.addAttribute("adminConfigList", adminConfigList);
        List<AdminConfigValues> adminConfigValuesList = adminConfigValuesService.findAll();
        model.addAttribute("adminConfigValuesList", adminConfigValuesList);
        alerts.setAlertModelAttribute(model);
        alerts.clearAlert();
        return templateBase + "config/list";
    }

    /**
     * @param model : ModelMap
     * @return
     */
    @RequestMapping(value = "/config/value/form", method = RequestMethod.GET)
    public String adminConfigValueForm(ModelMap model) {
        List<AdminConfig> adminConfigList = adminConfigService.findAll();
        model.addAttribute("adminConfig", new AdminConfig());
        model.addAttribute("adminConfigValues", new AdminConfigValues());
        model.addAttribute("adminConfigList", adminConfigList);
        return templateBase + "config/create_value";
    }


    /**
     * @param adminConfigValue    : Config
     * @param result              : Validation result
     * @param model               : ModelMap
     * @param redirectAttributes:
     * @param request             : HttpServletRequest
     * @return
     */
    @RequestMapping(value = "/config/value/create", method = RequestMethod.POST)
    public String adminCreateValueConfig(@ModelAttribute("adminConfigValues") @Valid AdminConfigValues adminConfigValue, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String ret = "redirect:/" + templateBase + "config/list";

        // if validation failed redirect back with flash message
        if (result.hasErrors()) {
            alerts.setError("Error.saving.config");
            ret = templateBase + "config/create_value";
        } else { // if no validation error save payment method

            alerts.setSuccess("Success.saving.config");
            adminConfigValuesService.saveOrUpdate(adminConfigValue);
        }
        alerts.setAlertModelAttribute(model);
        alerts.clearAlert();
        List<AdminConfigValues> adminConfigValuesList = adminConfigValuesService.findAll();
        model.addAttribute("adminConfigValuesList", adminConfigValuesList);
        List<AdminConfig> adminConfigList = adminConfigService.findAll();
        model.addAttribute("adminConfigList", adminConfigList);
        return ret;
    }

    /**
     * @return : All available roles
     */
    @ModelAttribute("roles")
    public List<Role> populateVarieties() {
        return roleService.findAll();
    }


    @Override
    public AdminConfig getAdminConfig() {
        return null;
    }
}
