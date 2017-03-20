/**
 *  Copyright 2017 incub8 Software Labs GmbH
 *  Copyright 2017 protel Hotelsoftware GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.mizool.mail;

import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.google.common.annotations.VisibleForTesting;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class MailSender
{
    @VisibleForTesting
    protected static final String FROM_ADDRESS_PROPERTY_NAME = "from.address";

    @VisibleForTesting
    protected static final String FROM_PERSONAL_PROPERTY_NAME = "from.personal";

    @Resource
    @Setter(value = AccessLevel.PROTECTED, onMethod = @__(@VisibleForTesting))
    private Session session;

    public void sendMail(Mail mail)
    {
        try
        {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(
                new InternetAddress(
                    session.getProperty(FROM_ADDRESS_PROPERTY_NAME), session.getProperty(FROM_PERSONAL_PROPERTY_NAME)));
            message.setRecipients(
                Message.RecipientType.TO, new Address[]{ new InternetAddress(mail.getAddress(), mail.getName()) });
            message.setSubject(mail.getSubject());
            message.setSentDate(Date.from(ZonedDateTime.now().toInstant()));
            message.setContent(mail.getContent(), "text/html");
            Transport.send(message);
        }
        catch (MessagingException | UnsupportedEncodingException e)
        {
            throw new MailException("Error sending mail", e);
        }
    }
}