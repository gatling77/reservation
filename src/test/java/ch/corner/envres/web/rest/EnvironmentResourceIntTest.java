package ch.corner.envres.web.rest;

import ch.corner.envres.Application;
import ch.corner.envres.domain.Environment;
import ch.corner.envres.repository.EnvironmentRepository;
import ch.corner.envres.repository.search.EnvironmentSearchRepository;

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
 * Test class for the EnvironmentResource REST controller.
 *
 * @see EnvironmentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class EnvironmentResourceIntTest {

    private static final String DEFAULT_ENVIRONMENT_NAME = "AAA";
    private static final String UPDATED_ENVIRONMENT_NAME = "BBB";

    @Inject
    private EnvironmentRepository environmentRepository;

    @Inject
    private EnvironmentSearchRepository environmentSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restEnvironmentMockMvc;

    private Environment environment;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EnvironmentResource environmentResource = new EnvironmentResource();
        ReflectionTestUtils.setField(environmentResource, "environmentSearchRepository", environmentSearchRepository);
        ReflectionTestUtils.setField(environmentResource, "environmentRepository", environmentRepository);
        this.restEnvironmentMockMvc = MockMvcBuilders.standaloneSetup(environmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        environment = new Environment();
        environment.setEnvironmentName(DEFAULT_ENVIRONMENT_NAME);
    }

    @Test
    @Transactional
    public void createEnvironment() throws Exception {
        int databaseSizeBeforeCreate = environmentRepository.findAll().size();

        // Create the Environment

        restEnvironmentMockMvc.perform(post("/api/environments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(environment)))
                .andExpect(status().isCreated());

        // Validate the Environment in the database
        List<Environment> environments = environmentRepository.findAll();
        assertThat(environments).hasSize(databaseSizeBeforeCreate + 1);
        Environment testEnvironment = environments.get(environments.size() - 1);
        assertThat(testEnvironment.getEnvironmentName()).isEqualTo(DEFAULT_ENVIRONMENT_NAME);
    }

    @Test
    @Transactional
    public void checkEnvironmentNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = environmentRepository.findAll().size();
        // set the field null
        environment.setEnvironmentName(null);

        // Create the Environment, which fails.

        restEnvironmentMockMvc.perform(post("/api/environments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(environment)))
                .andExpect(status().isBadRequest());

        List<Environment> environments = environmentRepository.findAll();
        assertThat(environments).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEnvironments() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environments
        restEnvironmentMockMvc.perform(get("/api/environments?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(environment.getId().intValue())))
                .andExpect(jsonPath("$.[*].environmentName").value(hasItem(DEFAULT_ENVIRONMENT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getEnvironment() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get the environment
        restEnvironmentMockMvc.perform(get("/api/environments/{id}", environment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(environment.getId().intValue()))
            .andExpect(jsonPath("$.environmentName").value(DEFAULT_ENVIRONMENT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEnvironment() throws Exception {
        // Get the environment
        restEnvironmentMockMvc.perform(get("/api/environments/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEnvironment() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

		int databaseSizeBeforeUpdate = environmentRepository.findAll().size();

        // Update the environment
        environment.setEnvironmentName(UPDATED_ENVIRONMENT_NAME);

        restEnvironmentMockMvc.perform(put("/api/environments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(environment)))
                .andExpect(status().isOk());

        // Validate the Environment in the database
        List<Environment> environments = environmentRepository.findAll();
        assertThat(environments).hasSize(databaseSizeBeforeUpdate);
        Environment testEnvironment = environments.get(environments.size() - 1);
        assertThat(testEnvironment.getEnvironmentName()).isEqualTo(UPDATED_ENVIRONMENT_NAME);
    }

    @Test
    @Transactional
    public void deleteEnvironment() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

		int databaseSizeBeforeDelete = environmentRepository.findAll().size();

        // Get the environment
        restEnvironmentMockMvc.perform(delete("/api/environments/{id}", environment.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Environment> environments = environmentRepository.findAll();
        assertThat(environments).hasSize(databaseSizeBeforeDelete - 1);
    }
}
