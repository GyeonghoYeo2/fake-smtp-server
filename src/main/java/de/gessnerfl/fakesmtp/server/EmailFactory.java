package de.gessnerfl.fakesmtp.server;

import de.gessnerfl.fakesmtp.model.Email;
import de.gessnerfl.fakesmtp.util.TimestampProvider;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailFactory {
    private final static Pattern SUBJECT_PATTERN = Pattern.compile("^Subject: (.*)$");
    private final static Pattern MESSAGEID_PATTERN = Pattern.compile("^Message-Id: (.*)$");
    public static final String NO_SUBJECT = "<no subject>";
    public static final String NO_MESSAGEID = "<no message-id>";

    private final TimestampProvider timestampProvider;
    private final Logger logger;

    @Autowired
    public EmailFactory(TimestampProvider timestampProvider, Logger logger) {
        this.timestampProvider = timestampProvider;
        this.logger = logger;
    }

    public Email convert(String from, String to, InputStream data) throws IOException {
        String content = convertStreamToString(data);
        String subject = parseSubject(content).orElse(NO_SUBJECT);
        String messageId = parseMessageId(content).orElse(NO_MESSAGEID);
        return createEmail(from, to, subject, messageId, content);
    }

    private Email createEmail(String from, String to, String subject, String messageId, String content){
        Email email = new Email();
        email.setFromAddress(from);
        email.setToAddress(to);
        email.setReceivedOn(timestampProvider.now());
        email.setSubject(subject);
        email.setMessageId(messageId);
        email.setContent(content);
        return email;
    }

    private String convertStreamToString(InputStream data) throws IOException {
        return IOUtils.toString(data, StandardCharsets.UTF_8);
    }

    private Optional<String> parseSubject(String data) {
        try {
            BufferedReader reader = new BufferedReader(new StringReader(data));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = SUBJECT_PATTERN.matcher(line);
                if (matcher.matches()) {
                    return Optional.of(matcher.group(1));
                }
            }
        } catch (IOException e) {
            logger.error("Failed to parse subject from email", e);
        }
        return Optional.empty();
    }

    private Optional<String> parseMessageId(String data) {
        try {
            BufferedReader reader = new BufferedReader(new StringReader(data));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = MESSAGEID_PATTERN.matcher(line);
                if (matcher.matches()) {
                    return Optional.of(matcher.group(1));
                }
            }
        } catch (IOException e) {
            logger.error("Failed to parse messageId from email", e);
        }
        return Optional.empty();
    }
}
