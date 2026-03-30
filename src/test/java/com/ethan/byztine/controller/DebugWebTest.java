package com.ethan.byztine.controller;

import com.ethan.byztine.application.ExecuteEventService;
import com.ethan.byztine.application.UserService;
import com.ethan.byztine.config.SecurityConfig;
import com.ethan.byztine.domain.Character;
import com.ethan.byztine.domain.combat.CombatEngine;
import com.ethan.byztine.domain.event.TrainingEvent;
import com.ethan.byztine.domain.user.User;
import com.ethan.byztine.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest({ DebugController.class, DebugApiController.class })
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class DebugWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ExecuteEventService executeEventService;

    @MockitoBean
    private CombatEngine combatEngine;

    @Test
    void debugPageShouldRender() throws Exception {
        mockMvc.perform(get("/debug"))
                .andExpect(status().isOk())
                .andExpect(view().name("debug"));
    }

    @Test
    void createUserShouldDelegateToUserService() throws Exception {
        mockMvc.perform(post("/api/debug/create-user")
                .param("username", "cassius")
                .param("email", "cassius@test.com")
                .param("password", "secret"))
                .andExpect(status().isOk())
                .andExpect(content().string("User created: cassius (cassius@test.com)"));

        verify(userService).register("cassius", "cassius@test.com", "secret");
    }

    @Test
    void createCharacterShouldAssignCharacterAndPersistUser() throws Exception {
        User user = new User("cassius", "cassius@test.com", "hash");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/debug/create-character")
                .param("email", user.getEmail())
                .param("name", "Cassius"))
                .andExpect(status().isOk())
                .andExpect(content().string("Character created: Cassius"));

        assertNotNull(user.getCharacter());
        assertEquals("Cassius", user.getCharacter().getName());
        verify(userRepository).save(user);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/debug/add-strength",
            "/api/debug/add-intelligence",
            "/api/debug/add-agility",
            "/api/debug/add-luck"
    })
    void addStatEndpointsShouldRejectNegativeAmounts(String endpoint) throws Exception {
        String safeEndpoint = Objects.requireNonNull(endpoint);

        mockMvc.perform(post(safeEndpoint)
                .param("email", "cassius@test.com")
                .param("amount", "-1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Amount must be positive"));

        verifyNoInteractions(userRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1 })
    void gainXpEndpointShouldRejectNonPositiveAmounts(int amount) throws Exception {
        mockMvc.perform(post("/api/debug/gain-xp")
                .param("email", "cassius@test.com")
                .param("amount", Integer.toString(amount)))
                .andExpect(status().isOk())
                .andExpect(content().string("Amount must be positive"));

        verifyNoInteractions(userRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1 })
    void addGoldEndpointShouldRejectNonPositiveAmounts(int amount) throws Exception {
        mockMvc.perform(post("/api/debug/add-gold")
                .param("email", "cassius@test.com")
                .param("amount", Integer.toString(amount)))
                .andExpect(status().isOk())
                .andExpect(content().string("Amount must be positive"));

        verifyNoInteractions(userRepository);
    }

    @Test
    void trainEndpointShouldExposeValidationMessage() throws Exception {
        User user = createUserWithCharacter();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(executeEventService.executeEvent(eq(user.getId()), any(TrainingEvent.class)))
                .thenThrow(new IllegalArgumentException("Invalid stat: wisdom"));

        mockMvc.perform(post("/api/debug/train")
                .param("email", user.getEmail())
                .param("stat", "wisdom"))
                .andExpect(status().isOk())
                .andExpect(content().string("Invalid stat: wisdom"));
    }

    @Test
    void characterEndpointShouldExposeLuck() throws Exception {
        User user = createUserWithCharacter();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/debug/character")
                .param("email", user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(
                        result.getResponse().getContentAsString().contains("LUCK: 5")));
    }

    private User createUserWithCharacter() {
        User user = new User("cassius", "cassius@test.com", "hash");
        user.assignCharacter(new Character("Cassius"));
        return user;
    }
}
