package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestMailResource {

    private final Logger log = LoggerFactory.getLogger(TestMailResource.class);

    private final MailService mailService;

    public TestMailResource(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/test-mail")
    public String sendTestMail(@RequestParam String to) {
        log.debug("REST request to send test email to {}", to);

        mailService.sendEmail(
            to,
            "✅ Test Mail from JHipster",
            "<h2>Hello!</h2><p>This is a test email sent via Gmail SMTP.</p>",
            false, // not multipart
            true // is HTML
        );

        return "Test email sent to " + to;
    }
}
