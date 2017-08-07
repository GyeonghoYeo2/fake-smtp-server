package de.gessnerfl.fakesmtp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gessnerfl.fakesmtp.model.Email;
import de.gessnerfl.fakesmtp.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
public class EmailRestController {
    private final EmailRepository emailRepository;

    @Autowired
    public EmailRestController(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public Map<String, String> parseIntoMap(Email email) {
        Map<String,String> emailMap = new HashMap<String,String>();

        emailMap.put("messageId", email.getMessageId());
        emailMap.put("subject", email.getSubject());
        emailMap.put("content", email.getContent());
        emailMap.put("fromAddress", email.getFromAddress());
        emailMap.put("toAddress", email.getToAddress());
        emailMap.put("receivedOn", email.getReceivedOn().toString());
        emailMap.put("id", email.getId().toString());

        return emailMap;
    }

    @RequestMapping(path = "/rest/email/{messageId}", method = org.springframework.web.bind.annotation.RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map<String, String> getEmailById(@PathVariable String messageId, HttpServletResponse response) throws JsonProcessingException {
        Email email = emailRepository.findByMessageId(messageId);

        Map<String,String> emailJson = new HashMap<String,String>();

        if ( email != null ) {
            emailJson = parseIntoMap(email);
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            emailJson.put("error", "Not Found Message Id::" + messageId);
        }

        return emailJson;
    }

    @RequestMapping(path = "/rest/email/}", method = org.springframework.web.bind.annotation.RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Map<String, String>> getEmailById(HttpServletResponse response) throws JsonProcessingException {
        List<Email> emails = emailRepository.findEmails();

        return emails.stream().map(email -> parseIntoMap(email)).collect(Collectors.toList());
    }

}
