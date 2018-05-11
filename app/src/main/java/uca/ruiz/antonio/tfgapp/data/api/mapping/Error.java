package uca.ruiz.antonio.tfgapp.data.api.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Error implements Serializable
{

    @SerializedName("codes")
    @Expose
    private ArrayList<String> codes = null;
    @SerializedName("arguments")
    @Expose
    private ArrayList<Argument> arguments = null;
    @SerializedName("defaultMessage")
    @Expose
    private String defaultMessage;
    @SerializedName("objectName")
    @Expose
    private String objectName;
    @SerializedName("field")
    @Expose
    private String field;
    @SerializedName("rejectedValue")
    @Expose
    private Object rejectedValue;
    @SerializedName("bindingFailure")
    @Expose
    private Boolean bindingFailure;
    @SerializedName("code")
    @Expose
    private String code;
    private final static long serialVersionUID = -8034626691048505324L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Error() {
    }

    /**
     *
     * @param rejectedValue
     * @param field
     * @param defaultMessage
     * @param arguments
     * @param objectName
     * @param codes
     * @param code
     * @param bindingFailure
     */
    public Error(ArrayList<String> codes, ArrayList<Argument> arguments, String defaultMessage, String objectName, String field, Object rejectedValue, Boolean bindingFailure, String code) {
        super();
        this.codes = codes;
        this.arguments = arguments;
        this.defaultMessage = defaultMessage;
        this.objectName = objectName;
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.bindingFailure = bindingFailure;
        this.code = code;
    }

    public ArrayList<String> getCodes() {
        return codes;
    }

    public void setCodes(ArrayList<String> codes) {
        this.codes = codes;
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(ArrayList<Argument> arguments) {
        this.arguments = arguments;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }

    public Boolean getBindingFailure() {
        return bindingFailure;
    }

    public void setBindingFailure(Boolean bindingFailure) {
        this.bindingFailure = bindingFailure;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
