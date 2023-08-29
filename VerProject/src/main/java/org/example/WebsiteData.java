package org.example;

import java.util.ArrayList;
import java.util.List;

public class WebsiteData {
    private String domain;
    private List<String> phoneNumbers;
    private List<String> socialMediaLinks;

    public WebsiteData() {
        phoneNumbers = new ArrayList<>();
        socialMediaLinks = new ArrayList<>();
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public List<String> getSocialMediaLinks() {
        return socialMediaLinks;
    }

    public void addPhoneNumber(String phoneNumber) {
        phoneNumbers.add(phoneNumber);
    }

    public void addSocialMediaLink(String link) {
        socialMediaLinks.add(link);
    }
}
