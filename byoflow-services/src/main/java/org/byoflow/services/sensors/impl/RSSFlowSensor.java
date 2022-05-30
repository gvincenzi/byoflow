package org.byoflow.services.sensors.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.byochain.commons.exceptions.BYOFlowException;
import org.byoflow.model.entity.FlowResource;
import org.byoflow.services.actuators.IFlowActuator;
import org.byoflow.services.sensors.IFlowSensor;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@Service
@Profile("default")
public class RSSFlowSensor implements IFlowSensor<FlowResource> {
	private static Logger LOGGER = LoggerFactory.getLogger(RSSFlowSensor.class);

	private IFlowActuator<FlowResource> rssFlowActuator;
	private Calendar lastChangeDate;

	@Value("${RSS_LINK}")
	private String rssLink;

	@Value("${NODE_ELEMENT_CATEGORY}")
	private String nodeElementCategory;

	@Value("${NODE_ELEMENT_ENTRY}")
	private String nodeElementEntry;

	@Value("${SPECIAL_CHARS_TO_REMOVE}")
	private String specialCharsToRemove;

	@Value("classpath:${XML_RSSFEED_FILENAME}")
	private Resource rssFile;

	@Value("${RSS_SENSOR_TIMEOUT}")
	private Integer rssSensorTimeout;

	@Autowired
	public RSSFlowSensor(IFlowActuator<FlowResource> rssFlowActuator) {
		LOGGER.info(String.format("RSSFlowSensor has been created with an Actuator of type [%s]",
				rssFlowActuator.getClass().getName()));
		this.rssFlowActuator = rssFlowActuator;
	}

	@Override
	public void onChange(Set<FlowResource> resources) throws BYOFlowException {
		LOGGER.info(String.format("RSSFlowSensor onChange has been called with [%d] resources", resources.size()));
		rssFlowActuator.doAction(resources);
		lastChangeDate = Calendar.getInstance();
		LOGGER.info(String.format("RSSFlowSensor onChange has been successfully terminated with [%d] resources",
				resources.size()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while (true) {
			LOGGER.info(String.format("RSSFlowSensor has been started with an Actuator of type [%s]",
					rssFlowActuator.getClass().getName()));
			try {
				Calendar now = Calendar.getInstance();
				LOGGER.info(String.format("RSSFlowSensor - Start check at [%s]", now.getTime()));
				Set<FlowResource> resources = new HashSet<FlowResource>();
				ArrayList<SyndFeed> feeds = (ArrayList<SyndFeed>) getRSSFeedByCategory();
				FlowResource resource;
				Calendar lastDate = getLastChangeDate();
				for (SyndFeed feed : feeds) {
					List<SyndEntry> entries = feed.getEntries();
					for (SyndEntry entry : entries) {
						try {
							if (entry.getPublishedDate() == null || entry.getDescription() == null) {
								continue;
							}
							resource = new FlowResource();
							resource.setName(cleanString(entry.getTitle()));
							if (entry.getPublishedDate().after(Calendar.getInstance().getTime())) {
								resource.setStartDateOfValidity(Calendar.getInstance());
							} else {
								resource.getStartDateOfValidity().setTime(entry.getPublishedDate());
							}
							StringBuffer content = new StringBuffer();
							SyndContent description = entry.getDescription();
							List<SyndEnclosure> enclosures = entry.getEnclosures();
							for (SyndEnclosure enclosure : enclosures) {
								if (enclosure.getType().contains("image/jpeg") && enclosure.getUrl().length() > 0) {
									content.append("<img src='" + enclosure.getUrl() + "'/><br>");
								}
							}
							content.append(description.getValue());
							String minimizedContent = entry.getLink().length() >= 30 ? entry.getLink().substring(0, 30)
									: entry.getLink();
							content.append("<br><a href='" + entry.getLink() + "'>(" + minimizedContent + "...)</a>");
							resource.setDescription(content.toString());
							if ((lastDate == null)
									|| (lastDate != null && resource.getStartDateOfValidity().compareTo(lastDate) > 0
											&& !resources.contains(resource)
											&& resource.getStartDateOfValidity().compareTo(now) <= 0)) {
								resources.add(resource);
							}
						} catch (Exception e) {
							LOGGER.error(e.getMessage());
						}
					}
				}
				LOGGER.info("RSSFlowSensor - Check completed");
				if (!resources.isEmpty()) {
					onChange(resources);
				} else {
					LOGGER.info("RSSFlowSensor - No changes founded");
				}
			} catch (BYOFlowException e) {
				LOGGER.error(e.getMessage());
			}

			try {
				long nextTimeUpdate = rssSensorTimeout * 1000 + System.currentTimeMillis();
				Calendar nextTimeUpdateCalendar = Calendar.getInstance();
				nextTimeUpdateCalendar.setTimeInMillis(nextTimeUpdate);
				LOGGER.info(String.format("RSSFlowSensor next update [%s]", nextTimeUpdateCalendar.getTime()));
				Thread.sleep(rssSensorTimeout * 1000);
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	/**
	 * Method to get a list of RSS Feed divided by Category
	 * 
	 * @return
	 * @throws BYException
	 */
	@SuppressWarnings("unchecked")
	protected Collection<SyndFeed> getRSSFeedByCategory() throws BYOFlowException {
		List<SyndFeed> output = new ArrayList<SyndFeed>();
		// get urls by category
		Map<String, List<String>> rssfeedByCategory;
		try {
			rssfeedByCategory = getRSSFeedURLByCategory();
		} catch (IOException e) {
			throw new BYOFlowException(e);
		} catch (JDOMException e) {
			throw new BYOFlowException(e);
		} catch (IllegalArgumentException e) {
			throw new BYOFlowException(e);
		}

		// source Feed
		URL url = null;
		XmlReader reader = null;
		SyndFeed feedRead = null;
		SyndFeed newFeed = null;
		for (String rssCategory : rssfeedByCategory.keySet()) {
			// create new feed
			newFeed = new SyndFeedImpl();
			newFeed.setTitle(rssCategory);
			newFeed.setDescription(rssCategory);
			newFeed.setPublishedDate(new Date());
			newFeed.setFeedType("rss_2.0"); // set the type of your feed
			newFeed.setLanguage("fr");

			for (String rssEntryUrl : rssfeedByCategory.get(rssCategory)) {
				try {
					// source Feed
					url = new URL(rssEntryUrl);
					reader = new XmlReader(url);
					// Create Parser RSS
					feedRead = new SyndFeedInput().build(reader);
				} catch (FeedException e) {
					continue;
				} catch (IOException e) {
					continue;
				} catch (java.lang.IllegalArgumentException e) {
					continue;
				}

				newFeed.setLink(rssLink);
				// add entries to the new feed
				newFeed.getEntries().addAll(feedRead.getEntries());
			}
			// add new feed to the list
			output.add(newFeed);
		}

		return output;
	}

	@Override
	public Calendar getLastChangeDate() {
		if (lastChangeDate == null) {
			lastChangeDate = Calendar.getInstance();
		}
		return lastChangeDate;
	}

	/**
	 * @param lastChangeDate
	 *            the lastChangeDate to set
	 */
	public void setLastChangeDate(Calendar lastChangeDate) {
		this.lastChangeDate = lastChangeDate;
	}

	/**
	 * @return the rssFlowActuator
	 */
	public IFlowActuator<FlowResource> getRssFlowActuator() {
		return rssFlowActuator;
	}

	/**
	 * Method to get the RSS feed URLs divided by category
	 * 
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, List<String>> getRSSFeedURLByCategory() throws IOException, JDOMException {
		Map<String, List<String>> result = new HashMap<String, List<String>>();

		SAXBuilder builder = new SAXBuilder();
		Document document = (Document) builder.build(rssFile.getInputStream());
		Element rootNode = document.getRootElement();
		List listCategories = rootNode.getChildren(nodeElementCategory);

		Element categoryNode = null;
		String categoryName = null;
		List listEntries = null;
		Element entryNode = null;
		String entryURL = null;
		List<String> current = null;
		// Loop on categories
		for (int i = 0; i < listCategories.size(); i++) {
			categoryNode = (Element) listCategories.get(i);
			categoryName = categoryNode.getAttributeValue("name");

			listEntries = categoryNode.getChildren(nodeElementEntry);
			// Loop on entries
			for (int j = 0; j < listEntries.size(); j++) {
				entryNode = (Element) listEntries.get(j);
				entryURL = entryNode.getAttributeValue("url");

				// add values to the map
				if (categoryName != null && entryURL != null) {
					current = result.get(categoryName);
					if (current == null) {
						current = new ArrayList<String>();
					}
					current.add(entryURL);
					result.put(categoryName, current);
				}

			}
		}
		return result;
	}

	/**
	 * Method to get a unique file name for the file containing the RSS Feed
	 * 
	 * @param category
	 * @return
	 */
	public static String getUniqueRSSFeedFileName(String category) {
		return category.replaceAll(" ", "_");
	}

	/**
	 * Method to replace all special chars
	 * 
	 * @param s
	 *            String
	 */
	public String cleanString(String s) {
		s = s.replaceAll(String.valueOf("\'"), "'");
		for (char specialChar : specialCharsToRemove.toCharArray()) {
			s = s.replaceAll(String.valueOf(specialChar), StringUtils.EMPTY);
		}

		return s.toUpperCase();
	}
}
