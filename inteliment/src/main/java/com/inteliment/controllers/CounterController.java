package com.inteliment.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CounterController {

    private static final String EMPTY = "";
    private static final String WHITESPACE = "\\s+";
    private static final String PUNCTUATION = "[.,:;]";
    private static final String PIPE = "|";
    private static final String NEWLINE = "\n";

    private static final String COUNTS_KEY = "counts";
    private static final String SEARCH_TEXT_KEY = "searchText";
    private static final String TEXT_CSV_VALUE = "text/csv";

    private static Map<String, Integer> word2count = null;
    private static List<String> mostFrequentWords = null;

    private static final Logger logger = LoggerFactory.getLogger(CounterController.class);

    static {
        try {
            File file = ResourceUtils.getFile("classpath:sample.txt");
            word2count = new HashMap<>();
            String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
            logger.debug("File content = " + content.toString());

            // Remove all non-alphanumeric characters and whitespace(except space)
            String modifiedContent = content.toLowerCase().replaceAll(PUNCTUATION, EMPTY);

            // Build a map of word -> wordFrequency
            String[] words = modifiedContent.split(WHITESPACE);
            logger.info("Words = " + words.length);
            for (String word : words) {
                if (!word2count.containsKey(word)) {
                    word2count.put(word, 0);
                }
                word2count.put(word, word2count.get(word) + 1);
            }
            logger.info("word2count.size = " + word2count.size());

            // Build an inverse map of wordFrequency -> words
            MultiValueMap<Integer, String> count2words = new LinkedMultiValueMap<>();
            for (String word : word2count.keySet()) {
                Integer count = word2count.get(word);
                count2words.add(count, word);
            }
            logger.info("count2words.size = " + count2words.size());

            // Build list of word frequencies in descending order
            List<Integer> uniqueCounts = new ArrayList<Integer>(count2words.keySet());
            Collections.sort(uniqueCounts);
            Collections.reverse(uniqueCounts);
            logger.info("uniqueCounts.size = " + uniqueCounts.size());
            logger.info("uniqueCounts = " + uniqueCounts);

            // Build list of most frequently occurring words
            mostFrequentWords = new LinkedList<String>();
            for (Integer count : uniqueCounts) {
                mostFrequentWords.addAll(count2words.get(count));
            }
            logger.info("mostFrequentWords.size = " + mostFrequentWords.size());
            logger.info("mostFrequentWords = " + mostFrequentWords);

        } catch (FileNotFoundException e) {
            logger.error("Exception occurred: " + e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Exception occurred: " + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/counter-api/search", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Set<Entry<String, Integer>>>> getFrequencyCounts(
            @RequestBody Map<String, List<String>> request) {

        logger.info("Search API: request body = " + request);

        if (!request.containsKey(SEARCH_TEXT_KEY)) {
            logger.warn("Request is missing field = " + SEARCH_TEXT_KEY);
            return new ResponseEntity<Map<String, Set<Entry<String, Integer>>>>(
                    HttpStatus.BAD_REQUEST);
        }

        Map<String, Integer> wordCounts = new HashMap<String, Integer>();
        for (String word : request.get(SEARCH_TEXT_KEY)) {
            String word2 = word.toLowerCase();
            wordCounts.put(word, word2count.containsKey(word2) ? word2count.get(word2) : 0);
        }

        Map<String, Set<Entry<String, Integer>>> response = new HashMap<>();
        response.put(COUNTS_KEY, wordCounts.entrySet());
        return new ResponseEntity<Map<String, Set<Entry<String, Integer>>>>(response,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/counter-api/top/{limit}", method = RequestMethod.GET,
            produces = TEXT_CSV_VALUE)
    public ResponseEntity<String> getMostFrequentWords(@PathVariable("limit") String slimit) {

        logger.info("Top API: limit = " + slimit);
        int limit = 0;
        try {
            limit = Integer.parseInt(slimit);
        } catch (NumberFormatException e) {
            logger.warn("Limit is not an integer, limit = " + slimit);
            return new ResponseEntity<String>("Limit MUST be an integer", HttpStatus.BAD_REQUEST);
        }

        if (limit <= 0) {
            logger.warn("Limit is not a positive integer, limit = " + slimit);
            return new ResponseEntity<String>("Limit MUST be a positive integer",
                    HttpStatus.BAD_REQUEST);
        }

        StringBuffer buf = new StringBuffer();
        Iterator<String> iter = mostFrequentWords.iterator();
        while (limit > 0 && iter.hasNext()) {
            String word = iter.next();
            buf.append(word + PIPE + word2count.get(word) + NEWLINE);
            limit--;
        }
        return new ResponseEntity<String>(buf.toString(), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        logger.warn("Exception, e = " + ex.getMessage());
        return new ResponseEntity<String>("Correct your request and try again!",
                HttpStatus.BAD_REQUEST);
    }
}
