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
import java.util.Map;

@Controller
public class EmailRestController {
    private final EmailRepository emailRepository;

    @Autowired
    public EmailRestController(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @RequestMapping(path = "/rest/email/{messageId}", method = org.springframework.web.bind.annotation.RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map<String, String> getEmailById(@PathVariable String messageId, HttpServletResponse response) throws JsonProcessingException {
        Email email = emailRepository.findByMessageId(messageId);

        Map<String,String> emailJson = new HashMap<String,String>();

        if ( email != null ) {
            emailJson.put("messageId", email.getMessageId());
            emailJson.put("subject", email.getSubject());
            emailJson.put("content", email.getContent());
            emailJson.put("fromAddress", email.getFromAddress());
            emailJson.put("toAddress", email.getToAddress());
            emailJson.put("receivedOn", email.getReceivedOn().toString());
            emailJson.put("id", email.getId().toString());
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            emailJson.put("error", "Not Found Message Id::" + messageId);
        }

        return emailJson;
    }

}
