package de.mpg.mpdl.auth;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.auth.configuration.TestConfiguration;
import de.mpg.mpdl.auth.configuration.WebConfiguration;
import de.mpg.mpdl.auth.model.UserAccount;
import de.mpg.mpdl.auth.repository.UserRepository;
import de.mpg.mpdl.auth.web.controller.UserAccountController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAccountUnitTest {
	
	private MockMvc mockMvc;
	private TestData testData;
	
	@Mock
	private ProjectionFactory projectionFactory; 
	@Mock
	private UserRepository userRepo;
	
	@InjectMocks
	private UserAccountController controller;
	
	@Before
	public void init() {
		
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		testData = new TestData();
	}

	@Test
	public void test_get_all_success() throws Exception {
	    List<UserAccount> users = testData.getTestUsersList();
	    when(userRepo.findAll()).thenReturn(users);
	    mockMvc.perform(get("/users"))
	            .andExpect(status().isOk())
	            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
	            .andExpect(jsonPath("$", hasSize(2)))
	            .andExpect(jsonPath("$[0].userid", is("testDepositor")))
	            .andExpect(jsonPath("$[1].userid", is("testModerator")));
	    verify(userRepo, times(1)).findAll();
	    verifyNoMoreInteractions(userRepo);
	}
	
	@Test
    public void test_get_by_userid_success() throws Exception {
        UserAccount user = testData.getTestUserDepositor();

        when(userRepo.findByUserid(user.getUserid())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/{userid}", user.getUserid()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.firstName", is("Deposi")))
                .andExpect(jsonPath("$.lastName", is("Tor")));

        verify(userRepo, atLeastOnce()).findByUserid(user.getUserid());
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    public void test_get_by_userid_fail_404_not_found() throws Exception {
        when(userRepo.findByUserid("not_existing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{userid}", "not_existing"))
                .andExpect(status().isNotFound());

        verify(userRepo, times(1)).findByUserid("not_existing");
        verifyNoMoreInteractions(userRepo);
    }
	
	/*
     * Java object 2 JSON
     */
    public static String asJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
