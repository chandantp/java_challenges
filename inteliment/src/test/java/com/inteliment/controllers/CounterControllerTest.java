package com.inteliment.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.inteliment.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class CounterControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MediaType jsonContentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private MediaType textCsvContentType = new MediaType("text", "csv", Charset.forName("utf8"));

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testTopMinus1() throws Exception {
        mockMvc.perform(get("/counter-api/top/-1")).andExpect(status().isBadRequest());
    }

    @Test
    public void testTopZero() throws Exception {
        mockMvc.perform(get("/counter-api/top/0")).andExpect(status().isBadRequest());
    }

    @Test
    public void testTop1() throws Exception {
        mockMvc.perform(get("/counter-api/top/1")).andExpect(status().isOk())
                .andExpect(content().contentType(textCsvContentType))
                .andExpect(content().string("vel|17\n"));
    }

    @Test
    public void testSearchMissingSearchTextField() throws Exception {
        String input = "{\"boobooboo\":[\"Duis\", \"Sed\", \"Donec\"]}";
        mockMvc.perform(
                post("/counter-api/search").contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSearchMalformedJson() throws Exception {
        String input = "{\"searchText\":[Duis\", \"Sed\", \"Donec\"]}";
        mockMvc.perform(
                post("/counter-api/search").contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSearchIncorrectJson() throws Exception {
        String input = "{\"searchText\": \"Donec\"}";
        mockMvc.perform(
                post("/counter-api/search").contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSearchEmpty() throws Exception {
        String input = "{\"searchText\": []}";
        mockMvc.perform(
                post("/counter-api/search").contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isOk()).andExpect(content().contentType(jsonContentType))
                .andExpect(jsonPath("$.counts", hasSize(0)));
    }

    @Test
    public void testSearch() throws Exception {
        String input = "{\"searchText\":[\"Duis\", \"Sed\", \"Donec\"]}";
        mockMvc.perform(
                post("/counter-api/search").contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isOk()).andExpect(content().contentType(jsonContentType))
                .andExpect(jsonPath("$.counts[0][\"Sed\"]", is(4)))
                .andExpect(jsonPath("$.counts[1][\"Donec\"]", is(8)))
                .andExpect(jsonPath("$.counts[2][\"Duis\"]", is(11)));
    }
}
