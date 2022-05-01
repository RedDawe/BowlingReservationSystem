package cz.reddawe.bowlingreservationsystem.user;

import cz.reddawe.bowlingreservationsystem.authorization.Role;
import cz.reddawe.bowlingreservationsystem.authorization.RoleRepository;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.PasswordValidationException;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ResourceAlreadyExistsException;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.UsernameValidationException;
import cz.reddawe.bowlingreservationsystem.user.iorecords.RoleName;
import cz.reddawe.bowlingreservationsystem.user.iorecords.UserInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService underTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository, passwordEncoder, roleRepository);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void itShouldLoadUserByUsername() {
        // given
        String username = "username";
        User user = new User();
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        // when
        UserDetails result = underTest.loadUserByUsername(username);

        // then
        assertThat(result).isEqualTo(user);
    }

    @Test
    void itShouldThrowWhenUsernameDoesNotExist() {
        // given
        String username = "username";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Username username not found");
    }

    @Test
    void itShouldGetCurrentUser() {
        // given
        User user = new User();
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(user);

        // when
        Optional<User> result = underTest.getCurrentUser();

        // then
        assertThat(result).isPresent().hasValue(user);
    }

    @Test
    void itShouldReturnEmptyWhenAuthenticationIsNull() {
        // given
        given(securityContext.getAuthentication()).willReturn(null);

        // when
        Optional<User> result = underTest.getCurrentUser();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldReturnEmptyWhenPrincipalIsAnonymousUser() {
        // given
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("anonymousUser");

        // when
        Optional<User> result = underTest.getCurrentUser();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldThrowWhenPrincipalIsNull() {
        // given
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(null);

        // when
        // then
        assertThatThrownBy(() -> underTest.getCurrentUser())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Authentication returned null from getPrincipal");
    }

    @Test
    void itShouldThrowWhenPrincipalIsInvalid() {
        // given
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(new Object());

        // when
        // then
        assertThatThrownBy(() -> underTest.getCurrentUser())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("""
                Authentication::getPrincipal() returned object other than "anonymousUser" or
                cz.reddawe.bowlingreservationsystem.user.User
                """);
    }

    @Test
    void itShouldRegisterUser() {
        // given
        UserInput userInput = new UserInput("username", "password1");
        Role role = new Role("USER", null);

        given(roleRepository.getByName("USER")).willReturn(role);

        // when
        underTest.registerUser(userInput);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue()).isEqualTo(
                new User("username", "password1", role)
        );
    }

    @Test
    void itShouldThrowAtInvalidUsername() {
        // given
        UserInput userInput = new UserInput("us", null);

        // when
        // then
        assertThatThrownBy(() -> underTest.registerUser(userInput))
                .isInstanceOf(UsernameValidationException.class)
                .matches(e -> ((UsernameValidationException) e).getReason().equals("us"));
    }

    @Test
    void itShouldThrowAtInvalidPassword() {
        // given
        UserInput userInput = new UserInput("username", "password");

        // when
        // then
        assertThatThrownBy(() -> underTest.registerUser(userInput))
                .isInstanceOf(PasswordValidationException.class)
                .matches(e -> ((PasswordValidationException) e).getReason().equals(""));
    }

    @Test
    void itShouldThrowIfUsernameExists() {
        // given
        UserInput userInput = new UserInput("username", "password1");

        given(userRepository.findByUsername("username")).willReturn(Optional.of(new User()));

        // when
        // then
        assertThatThrownBy(() -> underTest.registerUser(userInput))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .matches(e -> ((ResourceAlreadyExistsException) e).getReason().equals("username"));
    }

    @Test
    void itShouldGetMyRoleName() {
        // given
        User user = new User("username", "password1", new Role("USER", null));

        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(user);

        // when
        RoleName result = underTest.getMyRoleName();

        // then
        assertThat(result).isEqualTo(new RoleName("USER"));
    }

    @Test
    void itShouldReturnAnonymousUserForNullAuthentication() {
        // given
        given(securityContext.getAuthentication()).willReturn(null);

        // when
        RoleName result = underTest.getMyRoleName();

        // then
        assertThat(result).isEqualTo(new RoleName("anonymousUser"));
    }

    @Test
    void itShouldReturnAnonymousUserForAnonymousUser() {
        // given
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("anonymousUser");

        // when
        RoleName result = underTest.getMyRoleName();

        // then
        assertThat(result).isEqualTo(new RoleName("anonymousUser"));
    }
}