package ch.corner.envres.B_web.rest;

import ch.corner.envres.Application;
import ch.corner.envres.domain.Appl;
import ch.corner.envres.repository.ApplRepository;
import ch.corner.envres.repository.search.ApplSearchRepository;
import ch.corner.envres.web.rest.ApplResource;

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
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
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
 * Test class for the ApplResource REST controller.
 *
 * @see ApplResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class ApplResourceIntTest {

    private static final String DEFAULT_APPL_NAME = "AAA";
    private static final String UPDATED_APPL_NAME = "BBB";

    @Inject
    private ApplRepository applRepository;

    @Inject
    private ApplSearchRepository applSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restApplMockMvc;

    private Appl appl;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ApplResource applResource = new ApplResource();
        ReflectionTestUtils.setField(applResource, "applSearchRepository", applSearchRepository);
        ReflectionTestUtils.setField(applResource, "applRepository", applRepository);
        this.restApplMockMvc = MockMvcBuilders.standaloneSetup(applResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        appl = new Appl();
        appl.setApplName(DEFAULT_APPL_NAME);
    }

    @Test
    @Commit
    public void createAppl() throws Exception {
        int databaseSizeBeforeCreate = applRepository.findAll().size();

        // Create the Appl

        restApplMockMvc.perform(post("/api/appls")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appl)))
                .andExpect(status().isCreated());

        // Validate the Appl in the database
        List<Appl> appls = applRepository.findAll();
        assertThat(appls).hasSize(databaseSizeBeforeCreate + 1);
        Appl testAppl = appls.get(appls.size() - 1);
        assertThat(testAppl.getApplName()).isEqualTo(DEFAULT_APPL_NAME);
    }

    @Test
    @Transactional
    public void checkApplNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = applRepository.findAll().size();
        // set the field null
        appl.setApplName(null);

        // Create the Appl, which fails.

        restApplMockMvc.perform(post("/api/appls")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appl)))
                .andExpect(status().isBadRequest());

        List<Appl> appls = applRepository.findAll();
        assertThat(appls).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAppls() throws Exception {
        // Initialize the database
        applRepository.saveAndFlush(appl);

        // Get all the appls
        restApplMockMvc.perform(get("/api/appls?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(appl.getId().intValue())))
                .andExpect(jsonPath("$.[*].applName").value(hasItem(DEFAULT_APPL_NAME.toString())));
    }

    @Test
    @Transactional
    public void getAppl() throws Exception {
        // Initialize the database
        applRepository.saveAndFlush(appl);

        // Get the appl
        restApplMockMvc.perform(get("/api/appls/{id}", appl.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(appl.getId().intValue()))
            .andExpect(jsonPath("$.applName").value(DEFAULT_APPL_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAppl() throws Exception {
        // Get the appl
        restApplMockMvc.perform(get("/api/appls/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAppl() throws Exception {
        // Initialize the database
        applRepository.saveAndFlush(appl);

		int databaseSizeBeforeUpdate = applRepository.findAll().size();

        // Update the appl
        appl.setApplName(UPDATED_APPL_NAME);

        restApplMockMvc.perform(put("/api/appls")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appl)))
                .andExpect(status().isOk());

        // Validate the Appl in the database
        List<Appl> appls = applRepository.findAll();
        assertThat(appls).hasSize(databaseSizeBeforeUpdate);
        Appl testAppl = appls.get(appls.size() - 1);
        assertThat(testAppl.getApplName()).isEqualTo(UPDATED_APPL_NAME);
    }

    @Test
    @Transactional
    public void deleteAppl() throws Exception {
        // Initialize the database
        applRepository.saveAndFlush(appl);

		int databaseSizeBeforeDelete = applRepository.findAll().size();

        // Get the appl
        restApplMockMvc.perform(delete("/api/appls/{id}", appl.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Appl> appls = applRepository.findAll();
        assertThat(appls).hasSize(databaseSizeBeforeDelete - 1);
    }
}
