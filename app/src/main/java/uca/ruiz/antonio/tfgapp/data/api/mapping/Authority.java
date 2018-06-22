package uca.ruiz.antonio.tfgapp.data.api.mapping;

/**
 * Created by toni on 08/06/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Authority implements Serializable
{

    @SerializedName("authority")
    @Expose
    private AuthorityName name;
    private final static long serialVersionUID = -7287981482110975325L;

    public Authority() {
    }

    public Authority(AuthorityName name) {
        this.name = name;
    }

    public AuthorityName getName() {
        return name;
    }

    public void setName(AuthorityName name) {
        this.name = name;
    }

}
