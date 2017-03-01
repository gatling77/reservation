package ch.corner.envres.B_web.rest;

import ch.corner.envres.Application;
import ch.corner.envres.domain.DTAP;
import ch.corner.envres.repository.DTAPRepository;
import ch.corner.envres.repository.search.DTAPSearchRepository;
import ch.corner.envres.web.rest.DTAPResource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the DTAPResource REST controller.
 *
 * @see DTAPResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DTAPResourceIntTest {

    private static final String DEFAULT_LEVEL_NAME = "AAAAA";
    private static final String UPDATED_LEVEL_NAME = "BBBBB";

    private static final Integer DEFAULT_LEVEL_ID = 1;
    private static final Integer UPDATED_LEVEL_ID = 2;

    @Inject
    private DTAPRepository dTAPRepository;

    @Inject
    private DTAPSearchRepository dTAPSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDTAPMockMvc;

    private DTAP dTAP;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DTAPResource dTAPResource = new DTAPResource();
        ReflectionTestUtils.setField(dTAPResource, "dTAPSearchRepository", dTAPSearchRepository);
        ReflectionTestUtils.setField(dTAPResource, "dTAPRepository", dTAPRepository);
        this.restDTAPMockMvc = MockMvcBuilders.standaloneSetup(dTAPResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        dTAP = new DTAP();
        dTAP.setLevelName(DEFAULT_LEVEL_NAME);
        dTAP.setLevelId(DEFAULT_LEVEL_ID);
    }

    @Test
    @Transactional
    public void createDTAP() throws Exception {
        int databaseSizeBeforeCreate = dTAPRepository.findAll().size();

        // Create the DTAP

        restDTAPMockMvc.perform(post("/api/dTAPs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dTAP)))
                .andExpect(status().isCreated());

        // Validate the DTAP in the database
        List<DTAP> dTAPs = dTAPRepository.findAll();
        assertThat(dTAPs).hasSize(databaseSizeBeforeCreate + 1);
        DTAP testDTAP = dTAPs.get(dTAPs.size() - 1);
        assertThat(testDTAP.getLevelName()).isEqualTo(DEFAULT_LEVEL_NAME);
        assertThat(testDTAP.getLevelId()).isEqualTo(DEFAULT_LEVEL_ID);
    }

    @Test
    @Transactional
    public void checkLevelIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = dTAPRepository.findAll().size();
        // set the field null
        dTAP.setLevelId(null);

        // Create the DTAP, which fails.

        restDTAPMockMvc.perform(post("/api/dTAPs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dTAP)))
                .andExpect(status().isBadRequest());

        List<DTAP> dTAPs = dTAPRepository.findAll();
        assertThat(dTAPs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDTAPs() throws Exception {
        // Initialize the database
        dTAPRepository.saveAndFlush(dTAP);

        // Get all the dTAPs
        restDTAPMockMvc.perform(get("/api/dTAPs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(dTAP.getId().intValue())))
                .andExpect(jsonPath("$.[*].levelName").value(hasItem(DEFAULT_LEVEL_NAME.toString())))
                .andExpect(jsonPath("$.[*].levelId").value(hasItem(DEFAULT_LEVEL_ID)));
    }

    @Test
    @Transactional
    public void getDTAP() throws Exception {
        // Initialize the database
        dTAPRepository.saveAndFlush(dTAP);

        // Get the dTAP
        restDTAPMockMvc.perform(get("/api/dTAPs/{id}", dTAP.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(dTAP.getId().intValue()))
            .andExpect(jsonPath("$.levelName").value(DEFAULT_LEVEL_NAME.toString()))
            .andExpect(jsonPath("$.levelId").value(DEFAULT_LEVEL_ID));
    }

    @Test
    @Transactional
    public void getNonExistingDTAP() throws Exception {
        // Get the dTAP
        restDTAPMockMvc.perform(get("/api/dTAPs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDTAP() throws Exception {
        // Initialize the database
        dTAPRepository.saveAndFlush(dTAP);

		int databaseSizeBeforeUpdate = dTAPRepository.findAll().size();

        // Update the dTAP
        dTAP.setLevelName(UPDATED_LEVEL_NAME);
        dTAP.setLevelId(UPDATED_LEVEL_ID);

        restDTAPMockMvc.perform(put("/api/dTAPs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dTAP)))
                .andExpect(status().isOk());

        // Validate the DTAP in the database
        List<DTAP> dTAPs = dTAPRepository.findAll();
        assertThat(dTAPs).hasSize(databaseSizeBeforeUpdate);
        DTAP testDTAP = dTAPs.get(dTAPs.size() - 1);
        assertThat(testDTAP.getLevelName()).isEqualTo(UPDATED_LEVEL_NAME);
        assertThat(testDTAP.getLevelId()).isEqualTo(UPDATED_LEVEL_ID);
    }

    @Test
    @Transactional
    public void deleteDTAP() throws Exception {
        // Initialize the database
        dTAPRepository.saveAndFlush(dTAP);

		int databaseSizeBeforeDelete = dTAPRepository.findAll().size();

        // Get the dTAP
        restDTAPMockMvc.perform(delete("/api/dTAPs/{id}", dTAP.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<DTAP> dTAPs = dTAPRepository.findAll();
        assertThat(dTAPs).hasSize(databaseSizeBeforeDelete - 1);
    }
}
