package uca.ruiz.antonio.tfgapp.data.api.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Argument implements Serializable
{

    @SerializedName("codes")
    @Expose
    private ArrayList<String> codes = null;
    @SerializedName("arguments")
    @Expose
    private Object arguments;
    @SerializedName("defaultMessage")
    @Expose
    private String defaultMessage;
    @SerializedName("code")
    @Expose
    private String code;
    private final static long serialVersionUID = -8525630129948916236L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Argument() {
    }

    /**
     *
     * @param defaultMessage
     * @param arguments
     * @param codes
     * @param code
     */
    public Argument(ArrayList<String> codes, Object arguments, String defaultMessage, String code) {
        super();
        this.codes = codes;
        this.arguments = arguments;
        this.defaultMessage = defaultMessage;
        this.code = code;
    }

    public ArrayList<String> getCodes() {
        return codes;
    }

    public void setCodes(ArrayList<String> codes) {
        this.codes = codes;
    }

    public Object getArguments() {
        return arguments;
    }

    public void setArguments(Object arguments) {
        this.arguments = arguments;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
