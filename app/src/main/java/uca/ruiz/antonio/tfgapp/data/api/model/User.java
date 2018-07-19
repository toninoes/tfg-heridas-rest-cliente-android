package uca.ruiz.antonio.tfgapp.data.api.model;

/**
 * Created by toni on 08/06/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uca.ruiz.antonio.tfgapp.data.api.mapping.Authority;

public class User implements Serializable {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("authorities")
    @Expose
    private List<Authority> authorities = null;
    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("lastPasswordResetDate")
    @Expose
    private Date lastPasswordResetDate;
    private final static long serialVersionUID = -7074548215409497555L;
    @SerializedName("permisos")
    @Expose
    private ArrayList<Boolean> permisos = new ArrayList <>(3);

    public User() {
    }

    public User(String username, String firstname, String lastname, String email, List<Authority> authorities) {
        this.username = username;
        this.password = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.authorities = authorities;
        this.enabled = false;
        this.lastPasswordResetDate = new Date();
    }

    public User(String username, String firstname, String lastname, String email, List<Authority> authorities,
                ArrayList<Boolean> permisos) {
        this.username = username;
        this.password = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.authorities = authorities;
        this.enabled = false;
        this.lastPasswordResetDate = new Date();
        this.permisos = permisos;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLastnameComaAndFirstname() {
        return this.lastname + ", " + this.firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Boolean> getPermisos() {
        return permisos;
    }

    public void setPermisos(ArrayList<Boolean> permisos) {
        this.permisos = permisos;
    }
}
