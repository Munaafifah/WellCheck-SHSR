// package com.SmartHealthRemoteSystem.SHSR.WebConfiguration;

// // import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
// // import com.SmartHealthRemoteSystem.SHSR.User.Doctor.DoctorRepository;
// import java.util.ArrayList;
// import java.util.Collection;
// import java.util.List;

// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;

// import com.SmartHealthRemoteSystem.SHSR.User.User;

// public class MyUserDetails implements UserDetails {
//     private User user;
//     private List<GrantedAuthority> authorities;

//     // @Autowired
//     // private DoctorRepository doctorRepository;

//     public MyUserDetails(User user){
//         this.user = user;
//         List<GrantedAuthority> authorities = new ArrayList<>();
//         authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
//         this.authorities = authorities;
//     }

//     @Override
//     public Collection<? extends GrantedAuthority> getAuthorities() {
//         return authorities;
//     }

//     @Override
//     public String getPassword() {
//         return this.user.getPassword();
//     }

//     @Override
//     public String getUsername() {
//         return this.user.getUserId();
//     }

//     @Override
//     public boolean isAccountNonExpired() {
//         return true;
//     }

//     @Override
//     public boolean isAccountNonLocked() {
//         return true;
//     }

//     @Override
//     public boolean isCredentialsNonExpired() {
//         return true;
//     }

//     @Override
//     public boolean isEnabled() {
//         return true;
//     }
// }

package com.SmartHealthRemoteSystem.SHSR.WebConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.SmartHealthRemoteSystem.SHSR.User.User;

public class MyUserDetails implements UserDetails {
    private final User user;
    private final List<GrantedAuthority> authorities;

    public MyUserDetails(User user) {
        this.user = user;
        this.authorities = new ArrayList<>();
        this.authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    public User getUser() {
        return this.user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

