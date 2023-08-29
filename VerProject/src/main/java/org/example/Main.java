package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
        int batchStartIndex = Integer.parseInt(System.getenv("BATCH_START_INDEX"));
        int batchEndIndex = Integer.parseInt(System.getenv("BATCH_END_INDEX"));
        List<String> websiteUrls = extractWebsiteUrlsFromCsv("sample-websites.csv", batchStartIndex, batchEndIndex);
        List<WebsiteData> websiteDataList=new ArrayList<>();
        for (String websiteUrl : websiteUrls) {
            if (!websiteUrl.startsWith("http://") && !websiteUrl.startsWith("https://")) {
                websiteUrl = addHttpProtocol(websiteUrl);
            }

            System.out.println("Website URL: " + websiteUrl);
            WebsiteData webData = extractDataFromWebsite(websiteUrl);
            if (webData != null) {
                websiteDataList.add(webData);
            }

        }
        writeWebsiteDataToCsv(websiteDataList, batchStartIndex, batchEndIndex);

    }

    private static String addHttpProtocol(String url) {
        return "http://" + url;
    }

    private static List<String> extractWebsiteUrlsFromCsv(String csvFilePath, int startIndex, int endIndex) {
        List<String> websiteUrls = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] nextLine;
            int currentRow = 0;
            while ((nextLine = reader.readNext()) != null) {
                if (currentRow >= startIndex && currentRow <= endIndex) {
                    String websiteUrl = nextLine[0];
                    websiteUrls.add(websiteUrl);
                }
                currentRow++;
                if (currentRow > endIndex) {
                    break;
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return websiteUrls;
    }
private static WebsiteData extractDataFromWebsite(String originalUrl) throws MalformedURLException {
    try {
        Document doc = Jsoup.connect(originalUrl).get();
        WebsiteData websiteData = new WebsiteData();
        Elements relativeLinks = doc.select("a[href^=/]");
        int count = 0;
        for (Element relativeLink : relativeLinks) {
            if (count >= 9) {
                break;
            }

            String absoluteLink = originalUrl + relativeLink.attr("href");
            System.out.println("Relative Link: " + absoluteLink);

            try {
                Document relativeDoc = Jsoup.connect(absoluteLink).get();

                Elements phoneElements = relativeDoc.select("a[href^=tel:]");
                for (Element phoneElement : phoneElements) {
                    String phoneNumber = phoneElement.attr("href").replaceAll("tel:", "");
                    System.out.println("Phone number: " + phoneNumber);
                    websiteData.addPhoneNumber(phoneNumber);
                }

                Elements socialMediaElements = relativeDoc.select("a[href*=facebook.com], a[href*=twitter.com], a[href*=instagram.com]");
                for (Element socialMediaElement : socialMediaElements) {
                    String socialMediaLink = socialMediaElement.attr("href");
                    System.out.println("Social Media: " + socialMediaLink);
                    websiteData.addSocialMediaLink(socialMediaLink);
                }
            } catch (IOException e) {

            }

            count++;
        }
        websiteData.setDomain(originalUrl);
        return websiteData;

    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}
private static void writeWebsiteDataToCsv(List<WebsiteData> websiteDataList, int startIndex, int endIndex) {
    String csvFilePath = "merged-data3.csv";  // Specify the appropriate file path

    try {
        List<String[]> csvLines = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] nextLine;
            int currentRow = 0;
            while ((nextLine = reader.readNext()) != null) {
                csvLines.add(nextLine);
                currentRow++;
                if (currentRow > endIndex) {
                    break;
                }
            }
        } catch (CsvValidationException e) {

        }

        int lineIndex = startIndex;
        for (WebsiteData websiteData : websiteDataList) {
            String domain = websiteData.getDomain();
            List<String> phoneNumbers = websiteData.getPhoneNumbers();
            List<String> socialMediaLinks = websiteData.getSocialMediaLinks();
            String phoneNumberString = String.join(", ", phoneNumbers);
            String socialMediaLinkString = String.join(", ", socialMediaLinks);

            String[] csvLine = new String[]{domain, phoneNumberString, socialMediaLinkString};


            if (lineIndex < csvLines.size()) {
                csvLines.set(lineIndex, csvLine);
            } else {

                csvLines.add(csvLine);
            }

            lineIndex++;
        }


        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            for (String[] csvLine : csvLines) {
                writer.writeNext(csvLine);
            }
        }
    } catch (IOException e) {

    }
}

}
