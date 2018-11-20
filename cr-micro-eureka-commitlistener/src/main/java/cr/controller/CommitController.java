package cr.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import cr.CommitListenerApplication;
import cr.generated.config.ApplicationConfigReader;
import cr.generated.config.ApplicationConstant;
import cr.generated.ops.MessageSender;
import cr.shared.Commit;

@Controller
public class CommitController {
	private static final Logger log = LoggerFactory.getLogger(CommitController.class);
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private ApplicationConfigReader applicationConfig;
	@Autowired
	private MessageSender messageSender;
	@Autowired
	private RabbitTemplate rabbitTemplate;
	protected static String encryption=CommitListenerApplication.class.getAnnotation(cr.annotation.QueueDefinition.class).encryption();

	@RequestMapping(path = "/stash", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_MARKDOWN_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public void commit(@RequestBody String requestBody) {
		log.info("Received request: " + requestBody);
		enqueue(requestBody);
	}
	
	private void enqueue(String requestBody) {
		Commit commit=null;
		try {
		    ObjectMapper mapper = new ObjectMapper();
		    commit = mapper.readValue(requestBody, Commit.class);

		} catch (IOException e) {
		    e.printStackTrace();
		}
		String exchange = applicationConfig.getDbExchange();
		String routingKey = applicationConfig.getDbRoutingKey();
		messageSender.sendMessage(rabbitTemplate, exchange, routingKey, commit.toEncryptedMessage(encryption).encodeBase64());
		log.info(ApplicationConstant.IN_QUEUE+" "+commit.toString());
	}
	private Map<String, String> getHeadersInfo() {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}
		return map;
	}
	private void printHeader() {
		final StringBuilder sb = new StringBuilder();
		Map<String, String> header = getHeadersInfo();
		header.entrySet().stream().forEach(e -> sb.append("\n\t" + e.getKey() + ": " + e.getValue()));
		sb.append("\n");
		log.info(sb.toString());
	}
}
