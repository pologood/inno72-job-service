package com.inno72.job.admin.utils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MailUtil {
	
	private static Logger logger = LoggerFactory.getLogger(MailUtil.class);

	private static final String MAIL_SMTP_HOST = "smtpdm.aliyun.com";

	private static final String MAIL_SMTP_PORT = "80";

	private static final String MAIL_USER = "job-service@mail.72solo.com";

	private static final String MAIL_PASSWD = "yhxaG7Fn6WCBjGq";

	/**
	 *
	 * @param toAddress		收件人邮箱
	 * @param mailSubject	邮件主题
	 * @param mailBody		邮件正文
	 * @return
	 */
	public static boolean sendMail(String toAddress, String mailSubject, String mailBody) {


		final Properties props = new Properties();
		// 表示SMTP发送邮件，需要进行身份验证
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", MAIL_SMTP_HOST);
		props.put("mail.smtp.port", MAIL_SMTP_PORT);

		props.put("mail.user", MAIL_USER);
		props.put("mail.password", MAIL_PASSWD);

		Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// 用户名、密码
				String userName = props.getProperty("mail.user");
				String password = props.getProperty("mail.password");
				return new PasswordAuthentication(userName, password);
			}
		};

		Session mailSession = Session.getInstance(props, authenticator);

		MimeMessage message = new MimeMessage(mailSession) {
		};

		try {
			InternetAddress from = new InternetAddress(MAIL_USER, "job任务");
			message.setFrom(from);

			InternetAddress to = new InternetAddress(toAddress);
			message.setRecipient(MimeMessage.RecipientType.TO, to);
			message.setSubject(mailSubject);
			message.setContent(mailBody, "text/html;charset=UTF-8");
			Transport.send(message);
			return true;
		} catch (UnsupportedEncodingException | MessagingException e) {
			logger.error(e.getMessage(), e);
		}

		return false;
	}

}
