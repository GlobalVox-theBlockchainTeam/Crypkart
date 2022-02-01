/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */
package com.gradle.services.mail;

import com.gradle.config.SpringMailConfig;
import com.gradle.entity.Mail;
import com.gradle.entity.user.User;
import com.gradle.services.iface.user.UserService;
import com.gradle.util.LocaleHelper;
import com.gradle.util.ServiceUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Email service is being used to send emails
 */
@Service
public class EmailService {

    private static final String EMAIL_TEXT_TEMPLATE_NAME = "text/email-text";
    private static final String EMAIL_SIMPLE_TEMPLATE_NAME = "html/email-simple";
    private static final String EMAIL_WITHATTACHMENT_TEMPLATE_NAME = "html/email-withattachment";
    private static final String EMAIL_INLINEIMAGE_TEMPLATE_NAME = "html/email-inlineimage";
    private static final String EMAIL_EDITABLE_TEMPLATE_CLASSPATH_RES = "classpath:mail/editablehtml/email-editable.html";

    private static final String BACKGROUND_IMAGE = "mail/editablehtml/images/background.png";
    private static final String LOGO_BACKGROUND_IMAGE = "mail/editablehtml/images/logo-background.png";
    private static final String THYMELEAF_BANNER_IMAGE = "mail/editablehtml/images/thymeleaf-banner.png";
    private static final String THYMELEAF_LOGO_IMAGE = "mail/editablehtml/images/thymeleaf-logo.png";

    private static final String PNG_MIME = "image/png";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    @Qualifier("emailTemplateEngine")
    private TemplateEngine templateEngine;

    /*@Autowired
    @Qualifier("emailTemplateEngine")
    private TemplateEngine textTemplateEngine;*/

    @Autowired
    @Qualifier("emailTemplateEngine")
    private TemplateEngine stringTemplateEngine;

    @Autowired
    private LocaleHelper localeHelper;


    @Autowired
    private UserService userService;

    @Autowired
    private ServiceUtil serviceUtil;


    /*
     * Send plain TEXT mail
     */
    public void sendTextMail(
            final String recipientName, final String recipientEmail, final Locale locale)
            throws MessagingException {

        // Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Example plain TEXT email");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipientEmail);

        // Create the plain TEXT body using Thymeleaf
        final String textContent = this.templateEngine.process(EMAIL_TEXT_TEMPLATE_NAME, ctx);
        message.setText(textContent);

        // Send email
        this.mailSender.send(mimeMessage);
    }


    /*
     * Send HTML mail (simple)
     */
    public void sendSimpleMail(
            final String recipientName, final String recipientEmail, final Locale locale)
            throws MessagingException {

        // Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Example HTML email (simple)");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipientEmail);

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.templateEngine.process(EMAIL_SIMPLE_TEMPLATE_NAME, ctx);
        message.setText(htmlContent, true /* isHtml */);

        // Send email
        this.mailSender.send(mimeMessage);
    }


    /*
     * Send HTML mail with attachment.
     */
    public void sendMailWithAttachment(
            final String recipientName, final String recipientEmail, final String attachmentFileName,
            final byte[] attachmentBytes, final String attachmentContentType, final Locale locale)
            throws MessagingException {

        // Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message
                = new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
        message.setSubject("Example HTML email with attachment");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipientEmail);

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.templateEngine.process(EMAIL_WITHATTACHMENT_TEMPLATE_NAME, ctx);
        message.setText(htmlContent, true /* isHtml */);

        // Add the attachment
        final InputStreamSource attachmentSource = new ByteArrayResource(attachmentBytes);
        message.addAttachment(
                attachmentFileName, attachmentSource, attachmentContentType);

        // Send mail
        this.mailSender.send(mimeMessage);
    }


    /*
     * Send HTML mail with inline image
     */
    public void sendMailWithInline(
            final String recipientName, final String recipientEmail, final String imageResourceName,
            final byte[] imageBytes, final String imageContentType, final Locale locale)
            throws MessagingException {

        // Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        ctx.setVariable("imageResourceName", imageResourceName); // so that we can reference it from HTML

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message
                = new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
        message.setSubject(localeHelper.getApplicationPropertiesText("Welcome.text", null, "Welcome to CrypKart"));
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipientEmail);

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.templateEngine.process(EMAIL_INLINEIMAGE_TEMPLATE_NAME, ctx);
        message.setText(htmlContent, true /* isHtml */);

        // Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
        final InputStreamSource imageSource = new ByteArrayResource(imageBytes);
        message.addInline(imageResourceName, imageSource, imageContentType);

        // Send mail
        this.mailSender.send(mimeMessage);
    }


    public void sendMailWithInline(Mail mail) throws MessagingException {

        try {
            // Prepare the evaluation context
            final Context ctx = new Context(localeHelper.getCurrentLocale());
        /*ctx.setVariable("name", "anand");
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));*/
            mail.getModel().put("baseUrl", serviceUtil.getBaseUrl());
            ctx.setVariables(mail.getModel());
            ctx.setVariable("imageResourceName", "mailicon.png"); // so that we can reference it from HTML

            // Prepare message using a Spring helper
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
            message.setSubject(localeHelper.getApplicationPropertiesText("Welcome.text", null, "Welcome to CrypKart"));
            message.setFrom(mail.getFrom());
            message.setTo(mail.getTo());

            // Create the HTML body using Thymeleaf
            final String htmlContent = this.templateEngine.process("html/registration", ctx);
            message.setText(htmlContent, true); // true = isHtml

            // Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
        /*try {
            InputStream in = servletContext.getResourceAsStream("/resources/assets/images/mailicon.png");
            final InputStreamSource imageSource = new ByteArrayResource(IOUtils.toByteArray(in));
            message.addInline("mailicon.png", imageSource, MediaType.IMAGE_PNG.getType());
        }catch (Exception e){

        }*/

            // Send mail
            this.mailSender.send(mimeMessage);
        } catch (Exception e) {
            String test = "a";
        }

    }

    public void sendMailAtLogin(Mail mail) {

        try {
            final Context ctx = new Context(localeHelper.getCurrentLocale());

            mail.getModel().put("baseUrl", serviceUtil.getBaseUrl());
            ctx.setVariables(mail.getModel());
            ctx.setVariable("imageResourceName", "mailicon.png"); // so that we can reference it from HTML

            // Prepare message using a Spring helper
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
            message.setSubject(localeHelper.getApplicationPropertiesText("Welcome.text", null, "Welcome to CrypKart"));
            message.setFrom(mail.getFrom());
            message.setTo(mail.getTo());

            // Create the HTML body using Thymeleaf
            final String htmlContent = this.templateEngine.process("html/login", ctx);
            message.setText(htmlContent, true); // true = isHtml

            // Send mail
            this.mailSender.send(mimeMessage);
        } catch (Exception e) {
            String test = "a";
        }


    }

    /*
     * Send HTML mail with inline image
     */
    public String getEditableMailTemplate() throws IOException {
        final Resource templateResource = this.applicationContext.getResource(EMAIL_EDITABLE_TEMPLATE_CLASSPATH_RES);
        final InputStream inputStream = templateResource.getInputStream();
        return IOUtils.toString(inputStream, SpringMailConfig.EMAIL_TEMPLATE_ENCODING);
    }


    /*
     * Send HTML mail with inline image
     */
    public void sendEditableMail(
            final String recipientName, final String recipientEmail, final String htmlContent,
            final Locale locale)
            throws MessagingException {

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message
                = new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
        message.setSubject("Example editable HTML email");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipientEmail);

        // Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        List<User> userList = userService.findAll();
        ctx.setVariable("users", userList);

        // Create the HTML body using Thymeleaf
        final String output = stringTemplateEngine.process(htmlContent, ctx);
        final String output1 = templateEngine.process(htmlContent, ctx);
        message.setText(output, true /* isHtml */);

        // Add the inline images, referenced from the HTML code as "cid:image-name"
        message.addInline("background", new ClassPathResource(BACKGROUND_IMAGE), PNG_MIME);
        message.addInline("logo-background", new ClassPathResource(LOGO_BACKGROUND_IMAGE), PNG_MIME);
        message.addInline("thymeleaf-banner", new ClassPathResource(THYMELEAF_BANNER_IMAGE), PNG_MIME);
        message.addInline("thymeleaf-logo", new ClassPathResource(THYMELEAF_LOGO_IMAGE), PNG_MIME);

        // Send mail
        this.mailSender.send(mimeMessage);
    }


}
